package com.rplees.iproxy.local.handler;

import com.rplees.iproxy.local.RuntimeOption;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * 
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 6, 2023 10:55:09 AM
 */
public class WebSocketHandler extends AbstractHandler {
//	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public WebSocketHandler(RuntimeOption option) {
		super(option);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		WebSocketFrame frame = (WebSocketFrame) msg;
		
//        log.debug("触发 fireBeforeRequest-WebSocketFrame 消息事件, {}", msg);
        pipeline.fireBeforeRequest(ctx.channel(), frame, paramCtx(ctx));
	}
}