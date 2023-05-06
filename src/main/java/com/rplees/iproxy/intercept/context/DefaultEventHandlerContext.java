package com.rplees.iproxy.intercept.context;

import com.rplees.iproxy.intercept.event.handler.EventHandler;

final class DefaultEventHandlerContext extends AbstractEventHandlerContext {
	EventHandler handler = null;

	DefaultEventHandlerContext(DefaultEventPipeline pipeline, String name, EventHandler handler) {
		super(pipeline, name);
		if (handler == null) {
			throw new NullPointerException("handler");
		}
		this.handler = handler;
	}

	@Override
	public EventHandler handler() {
		return handler;
	}
}
