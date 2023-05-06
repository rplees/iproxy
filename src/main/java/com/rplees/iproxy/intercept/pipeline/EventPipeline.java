package com.rplees.iproxy.intercept.pipeline;

import java.util.Map;
import java.util.Map.Entry;

import com.rplees.iproxy.intercept.event.handler.EventHandler;
import com.rplees.iproxy.intercept.invoke.EventHandlerInvoker;
import com.rplees.iproxy.intercept.invoke.HttpEventHandlerInvoker;
import com.rplees.iproxy.intercept.invoke.TunnelEventHandlerInvoker;
import com.rplees.iproxy.intercept.invoke.WebSocketEventHandlerInvoker;
import com.rplees.iproxy.local.RuntimeOption;

import io.netty.util.AttributeMap;

/**
 * 事件流处理 
 *
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 11, 2023 5:02:50 PM
 */
public interface EventPipeline extends HttpEventHandlerInvoker, TunnelEventHandlerInvoker, WebSocketEventHandlerInvoker, EventHandlerInvoker, Iterable<Entry<String, EventHandler>>, AttributeMap {
	
	void addLast(EventHandler intercept);

	void addFirst(EventHandler intercept);

	Map<String, EventHandler> toMap();
	
	RuntimeOption option();
}
