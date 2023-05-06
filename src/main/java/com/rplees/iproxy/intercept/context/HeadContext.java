package com.rplees.iproxy.intercept.context;

import com.rplees.iproxy.intercept.event.handler.EventHandler;
import com.rplees.iproxy.intercept.event.handler.HttpEventHandler;
import com.rplees.iproxy.intercept.event.handler.TunnelEventHandler;
import com.rplees.iproxy.intercept.event.handler.WebSocketEventHandler;
import com.rplees.iproxy.intercept.pipeline.EventPipeline;
import com.rplees.iproxy.proto.ParamContext;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

final class HeadContext extends AbstractEventHandlerContext implements HttpEventHandler, TunnelEventHandler, WebSocketEventHandler {
	static final String HEAD_NAME = "head";
	
	HeadContext(EventPipeline pipeline) {
        super(pipeline, HEAD_NAME);
    }
	
	@Override
	public EventHandler handler() {
		return this;
	}

	@Override
	public void beforeConnect(Channel localChannel, EventHandlerContext ctx) throws Exception {
		ctx.fireBeforeConnect(localChannel);
	}

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

	@Override
	public void beforeRequest(Channel localChannel, ByteBuf msg, ParamContext paramCtx, EventHandlerContext ctx) throws Exception {
		ctx.fireBeforeRequest(localChannel, msg, paramCtx);
	}

	@Override
	public void afterResponse(Channel localChannel, Channel remoteChannel, ByteBuf msg, ParamContext paramCtx,
			EventHandlerContext ctx) throws Exception {
		ctx.fireAfterResponse(localChannel, remoteChannel, msg, paramCtx);
	}

	@Override
	public void beforeRequest(Channel localChannel, WebSocketFrame msg, ParamContext paramCtx, EventHandlerContext ctx) throws Exception {
		ctx.fireBeforeRequest(localChannel, msg, paramCtx);
	}

	@Override
	public void afterResponse(Channel localChannel, Channel remoteChannel, WebSocketFrame msg, ParamContext paramCtx,  EventHandlerContext ctx)
			throws Exception {
		ctx.fireAfterResponse(localChannel, remoteChannel, msg, paramCtx);
	}

}
