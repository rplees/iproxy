package com.rplees.iproxy.remote.handler;

import java.util.LinkedList;
import java.util.List;

import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.local.proxy.ProxyHandleFactory;
import com.rplees.iproxy.proto.ParamContext;
import com.rplees.iproxy.proto.Proto;
import com.rplees.iproxy.proto.Proto.ProtoType;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.proxy.ProxyHandler;

/**
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 8, 2023 2:52:50 PM
 */
public final class ChannelBridge {
	ParamContext paramCtx;
	RuntimeOption option;
    List<Object> remoteMsgList;
    Channel localChannel;
    Channel remoteChannel;
    ConnectState connectState = ConnectState.NOT_CONNECTED;
    
    enum ConnectState {
    	NOT_CONNECTED,
    	CONNECTING,
    	CONNECTED,
    }
    
	public ChannelBridge(ParamContext paramCtx, RuntimeOption option) {
        this.paramCtx = paramCtx;
        this.option = option;
    }
	
	public ParamContext paramCtx() {
		return paramCtx;
	}

	public void clientFree() {
		ChannelBridgeCollector.instance().clientClose(this);
	}
	
	public void remoteFree(Channel remote) {
		ChannelBridgeCollector.instance().removeClose(this);
	}
	
	public void addProxyHandlerIf(Channel ch) {
		ProxyHandler proxyHandler = null;
		if(paramCtx.router().remote().proxy()) {
			proxyHandler = ProxyHandleFactory.build(option.getProxyConfig());
		}
		
		if (proxyHandler != null) {
            ch.pipeline().addLast(proxyHandler);
        }
	}
	
	ChannelHandler remoteHandler(Channel channel, EventHandlerContext ctx) {
		ProtoType protoType = paramCtx.router().remote().guessProtoType();
		
		if (protoType == ProtoType.HTTP) {
			return new RemoteHttpHandlerInitializer(this, ctx);
		} else if(protoType == ProtoType.WEBSORKET) {
			return new RemoteWebSocketHandlerInitializer(this, ctx);
		} else {
			return new RemoteTunnelHandlerInitializer(this, ctx);
		}
	}
	
	public void sendToRemote(Channel channel, Object msg, EventHandlerContext ctx) throws Exception {
		this.localChannel = channel;
		
		final Proto remote = paramCtx.router().remote();
		if(remoteChannel == null && connectState == ConnectState.NOT_CONNECTED) {
			if (remote.guessProtoType() == ProtoType.HTTP && !(msg instanceof HttpRequest)) {
				throw new RuntimeException(String.format("顺序 HttpRequest -> HttpContent, 错误, proto: %s, msg: %s", remote, msg));
            }
			connectState = ConnectState.CONNECTING;
			
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(option.getRemoteGroup())
					.channel(NioSocketChannel.class)
					.handler(remoteHandler(channel, ctx));
			
			remoteMsgList = new LinkedList<>();
			remoteMsgList.add(msg);
			ChannelFuture channelFuture = bootstrap.connect(remote.host(), remote.port());
			
			channelFuture.addListener((ChannelFutureListener) future -> {
				remoteChannel = future.channel();
				
				if(future.isSuccess()) {
					connectState = ConnectState.CONNECTED;
					
					synchronized (remoteMsgList) {
						remoteMsgList.forEach(obj -> {
							future.channel().writeAndFlush(obj)
								.addListener((ChannelFutureListener) f -> {
									if(! f.isSuccess()) {
										option.getExceptionProvider().remote(channel, f.channel(), f.cause());
									}
								});
						});
						
						if(remoteChannel.pipeline().get(RuntimeOption.READ_TIMEOUT) == null) {
//							remoteChannel.pipeline().addLast(RuntimeOption.READ_TIMEOUT, new ReadTimeoutHandler(option.getReadTimeout());
						}
						remoteMsgList.clear();
					}
				} else {
					clientFree();
					option.getExceptionProvider().remote(channel, remoteChannel, future.cause());
					remoteMsgList.clear();
					connectState = ConnectState.NOT_CONNECTED;
				}
			});
		} else {
			synchronized (remoteMsgList) {
				if(connectState == ConnectState.CONNECTED) {
					remoteChannel.writeAndFlush(msg);
					
					if(remoteChannel.pipeline().get(RuntimeOption.READ_TIMEOUT) == null) {
//						remoteChannel.pipeline().addLast(RuntimeOption.READ_TIMEOUT, new ReadTimeoutHandler(option.getReadTimeout()));
					}
				} else {
					remoteMsgList.add(msg);
				}
			}
		}
	}
}