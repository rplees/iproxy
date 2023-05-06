package com.rplees.iproxy.local.handler;

import com.rplees.iproxy.local.RuntimeOption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 6, 2023 10:55:09 AM
 */
public class TunnelHandler extends AbstractHandler {
//	private final Logger log = LoggerFactory.getLogger(this.getClass());
    
	public TunnelHandler(RuntimeOption option) {
		super(option);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		log.debug("触发 fireBeforeRequest-ByteBuf 消息事件, {}", msg.getClass().getSimpleName());
    	pipeline.fireBeforeRequest(ctx.channel(), (ByteBuf) msg, paramCtx(ctx));
	}
}