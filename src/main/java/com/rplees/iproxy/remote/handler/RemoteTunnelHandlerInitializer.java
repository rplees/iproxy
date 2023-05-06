package com.rplees.iproxy.remote.handler;

import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.intercept.pipeline.EventPipeline;
import com.rplees.iproxy.local.RuntimeOption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * http代理隧道，转发原始报文
 */
public class RemoteTunnelHandlerInitializer extends ChannelInitializer<Channel> {

	protected ChannelBridge bridge;
	protected EventHandlerContext context;

    public RemoteTunnelHandlerInitializer(ChannelBridge bridge, EventHandlerContext context) {
        this.bridge = bridge;
        this.context = context;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
    	ch.pipeline().addLast(new LoggingHandler("代理REMOTE-TUNNEL线程", LogLevel.TRACE));
    	bridge.addProxyHandlerIf(ch);
    	
        ch.pipeline().addLast(new AbstractRemoteHandler(bridge, context) {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            	if(ctx.channel().pipeline().get(RuntimeOption.READ_TIMEOUT) != null) {
            		ctx.channel().pipeline().remove(RuntimeOption.READ_TIMEOUT);
        		}
            	
            	EventPipeline pipeline = context.pipeline();
            	pipeline.fireAfterResponse(bridge.localChannel, ctx.channel(), (ByteBuf) msg, bridge.paramCtx());
            }
        });
    }
}
