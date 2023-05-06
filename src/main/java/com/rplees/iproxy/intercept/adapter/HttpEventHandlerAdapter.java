package com.rplees.iproxy.intercept.adapter;

import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.intercept.event.handler.HttpEventHandler;
import com.rplees.iproxy.proto.ParamContext;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public class HttpEventHandlerAdapter extends EventHandlerAdapter implements HttpEventHandler {

	@Override
	public void beforeRequest(Channel localChannel, HttpRequest msg, ParamContext paramCtx, EventHandlerContext ctx)
			throws Exception {
		ctx.fireBeforeRequest(localChannel, msg, paramCtx);
	}

	@Override
	public void beforeRequest(Channel localChannel, HttpContent msg, ParamContext paramCtx, EventHandlerContext ctx)
			throws Exception {
		ctx.fireBeforeRequest(localChannel, msg, paramCtx);
	}

	@Override
	public void afterResponse(Channel localChannel, Channel remoteChannel, HttpResponse msg, ParamContext paramCtx,
			EventHandlerContext ctx) throws Exception {
		ctx.fireAfterResponse(localChannel, remoteChannel, msg, paramCtx);
	}

	@Override
	public void afterResponse(Channel localChannel, Channel remoteChannel, HttpContent msg, ParamContext paramCtx,
			EventHandlerContext ctx) throws Exception {
		ctx.fireAfterResponse(localChannel, remoteChannel, msg, paramCtx);
	}
}
