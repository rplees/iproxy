package com.rplees.iproxy.intercept.context;

import com.rplees.iproxy.intercept.event.handler.EventHandler;
import com.rplees.iproxy.intercept.invoke.EventHandlerInvoker;
import com.rplees.iproxy.intercept.invoke.HttpEventHandlerInvoker;
import com.rplees.iproxy.intercept.invoke.TunnelEventHandlerInvoker;
import com.rplees.iproxy.intercept.invoke.WebSocketEventHandlerInvoker;
import com.rplees.iproxy.intercept.pipeline.EventPipeline;

public interface EventHandlerContext extends EventHandlerInvoker, HttpEventHandlerInvoker, TunnelEventHandlerInvoker, WebSocketEventHandlerInvoker {
	EventPipeline pipeline();
	EventHandler handler();
	String name();
}
