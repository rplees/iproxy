package com.rplees.iproxy.intercept.adapter;

import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.intercept.event.handler.WebSocketEventHandler;
import com.rplees.iproxy.proto.ParamContext;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketEventHandlerAdapter extends EventHandlerAdapter implements WebSocketEventHandler {

	@Override
	public void beforeRequest(Channel localChannel, WebSocketFrame msg, ParamContext paramCtx, EventHandlerContext ctx) throws Exception {
		ctx.fireBeforeRequest(localChannel, msg, paramCtx);
	}

	@Override
	public void afterResponse(Channel localChannel, Channel remoteChannel, WebSocketFrame msg, ParamContext paramCtx, EventHandlerContext ctx)
			throws Exception {
		ctx.fireAfterResponse(localChannel, remoteChannel, msg, paramCtx);
	}
}
