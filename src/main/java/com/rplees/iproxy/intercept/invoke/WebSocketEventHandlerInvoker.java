package com.rplees.iproxy.intercept.invoke;

import com.rplees.iproxy.proto.ParamContext;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * 执行器 
 *
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 8, 2023 1:15:29 PM
 */
public interface WebSocketEventHandlerInvoker {
	WebSocketEventHandlerInvoker fireBeforeRequest(Channel localChannel, WebSocketFrame msg, ParamContext paramCtx);
	WebSocketEventHandlerInvoker fireAfterResponse(Channel localChannel, Channel remoteChannel, WebSocketFrame msg, ParamContext paramCtx);
}
