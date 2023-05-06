package com.rplees.iproxy.remote.handler;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.intercept.pipeline.EventPipeline;
import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.proto.Proto;
import com.rplees.iproxy.proto.Proto.HttpProto;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler.ClientHandshakeStateEvent;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 *
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 3, 2023 10:54:57 PM
 */
public class RemoteWebSocketHandlerInitializer extends ChannelInitializer<Channel> {
	
	protected ChannelBridge bridge;
	protected EventHandlerContext context;

    public RemoteWebSocketHandlerInitializer(ChannelBridge bridge, EventHandlerContext context) {
        this.bridge = bridge;
        this.context = context;
    }
    
    @Override
    protected void initChannel(Channel ch) throws Exception {
    	bridge.addProxyHandlerIf(ch);
    	
        Proto proto = bridge.paramCtx().router().remote();
        RuntimeOption option = context.pipeline().option();
        
        if (proto.ssl()) {
            ch.pipeline().addFirst(option.getClientSslCtx().newHandler(ch.alloc(), proto.host(), proto.port()));
        }
        ch.pipeline().addLast(new LoggingHandler(">>>REMOTE-WEBSOCKET<<<", LogLevel.TRACE));
        ch.pipeline().addLast(RuntimeOption.HTTP_CODEC, option.httpClientCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(RuntimeOption.DEFAULT_MAX_CONTENT_LENGTH));
        ch.pipeline().addLast(RuntimeOption.WEBSOCKET_PROTOCOL_HANDLER, new WebSocketClientProtocolHandler(WebSocketClientHandshakerFactory.newHandshaker(new URI(((HttpProto) proto).wsUri()), WebSocketVersion.V13, null, false, new DefaultHttpHeaders())));
        ch.pipeline().addLast(new RemoteWebSocketReplayHandler(bridge, context));
    }
    
    private final class RemoteWebSocketReplayHandler extends AbstractRemoteHandler {
    	public RemoteWebSocketReplayHandler(ChannelBridge bridge, EventHandlerContext context) {
			super(bridge, context);
		}

    	boolean handshakeComplete = false;
    	Map<Object, ChannelPromise> msgMap = new LinkedHashMap<>();
    	
    	@Override
    	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    		if(! handshakeComplete && msg instanceof WebSocketFrame) {
    			msgMap.put(msg, promise);
    		} else {
    			super.write(ctx, msg, promise);
    		}
    	}
    	
    	@Override
    	public void flush(ChannelHandlerContext ctx) throws Exception {
			super.flush(ctx);
    	}
    	
    	@Override
    	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    		super.userEventTriggered(ctx, evt);
    		if(evt instanceof WebSocketClientProtocolHandler.ClientHandshakeStateEvent) {
    			WebSocketClientProtocolHandler.ClientHandshakeStateEvent e = ((WebSocketClientProtocolHandler.ClientHandshakeStateEvent) evt);
    			if(e == ClientHandshakeStateEvent.HANDSHAKE_COMPLETE) {//握手完成
    				handshakeComplete = true;
    				
    				for (Map.Entry<Object, ChannelPromise> entry : msgMap.entrySet()) {
						ctx.write(entry.getKey(), entry.getValue());
					}
    				
    				if(msgMap.size() > 0) {
    					ctx.flush();
    				}
    				
    				msgMap.clear();
    			}
    		}
    	}
    	
    	@Override
    	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    		if(ctx.channel().pipeline().get(RuntimeOption.READ_TIMEOUT) != null) {
    			ctx.channel().pipeline().remove(RuntimeOption.READ_TIMEOUT);
    		}
    		
    		EventPipeline pipeline = context.pipeline();
    		pipeline.fireAfterResponse(bridge.localChannel, ctx.channel(), (WebSocketFrame) msg, bridge.paramCtx());
    	}
    }
}
