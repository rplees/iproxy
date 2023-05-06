package com.rplees.iproxy.intercept.adapter;

import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.intercept.event.handler.TunnelEventHandler;
import com.rplees.iproxy.proto.ParamContext;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class TunnelEventHandlerAdapter extends EventHandlerAdapter implements TunnelEventHandler {
	@Override
	public void beforeRequest(Channel localChannel, ByteBuf msg, ParamContext paramCtx, EventHandlerContext ctx) throws Exception {
		ctx.fireBeforeRequest(localChannel, msg, paramCtx);
	}

	@Override
	public void afterResponse(Channel localChannel, Channel remoteChannel, ByteBuf msg, ParamContext paramCtx,
			EventHandlerContext ctx) throws Exception {
		ctx.fireAfterResponse(localChannel, remoteChannel, msg, paramCtx);
	}
}
