package com.rplees.iproxy.intercept.event.handler;

import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.proto.ParamContext;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public interface WebSocketEventHandler extends EventHandler {
	
	void beforeRequest(Channel localChannel, WebSocketFrame msg, ParamContext paramCtx, EventHandlerContext ctx) throws Exception;

	void afterResponse(Channel localChannel, Channel remoteChannel, WebSocketFrame msg, ParamContext paramCtx, EventHandlerContext ctx)
			throws Exception;
}
