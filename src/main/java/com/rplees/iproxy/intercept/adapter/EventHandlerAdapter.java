package com.rplees.iproxy.intercept.adapter;

import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.intercept.event.handler.EventHandler;

import io.netty.channel.Channel;

public abstract class EventHandlerAdapter implements EventHandler {
	@Override
	public void beforeConnect(Channel localChannel, EventHandlerContext ctx) throws Exception {
		ctx.fireBeforeConnect(localChannel);
	}
}
