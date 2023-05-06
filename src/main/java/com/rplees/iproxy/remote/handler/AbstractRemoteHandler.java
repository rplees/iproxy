package com.rplees.iproxy.remote.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.intercept.context.EventHandlerContext;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutException;

/**
 *
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 3, 2023 10:54:57 PM
 */
public class AbstractRemoteHandler extends ChannelDuplexHandler {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected ChannelBridge bridge;
	protected EventHandlerContext context;

    public AbstractRemoteHandler(ChannelBridge bridge, EventHandlerContext context) {
        this.bridge = bridge;
        this.context = context;
    }
    
    @Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    	bridge.remoteFree(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(cause instanceof ReadTimeoutException) {
			log.error("proto: {}, ReadTimeoutException", ctx.channel().remoteAddress());
		}
		
		bridge.remoteFree(ctx.channel());
		context.pipeline().option()
			.getExceptionProvider().remote(bridge.remoteChannel, ctx.channel(), cause);
	}
}
