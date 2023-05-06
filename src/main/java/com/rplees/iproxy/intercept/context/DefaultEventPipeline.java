package com.rplees.iproxy.intercept.context;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.rplees.iproxy.intercept.EventInitializer;
import com.rplees.iproxy.intercept.event.handler.EventHandler;
import com.rplees.iproxy.intercept.invoke.WebSocketEventHandlerInvoker;
import com.rplees.iproxy.intercept.pipeline.EventPipeline;
import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.proto.ParamContext;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.internal.StringUtil;

public class DefaultEventPipeline extends DefaultAttributeMap implements EventPipeline {
    final AbstractEventHandlerContext head;
    final AbstractEventHandlerContext tail;
    
    private RuntimeOption option;

    public DefaultEventPipeline() {
    	tail = new TailContext(this);
        head = new HeadContext(this);

        head.next = tail;
        tail.prev = head;
    }
    
    public DefaultEventPipeline(RuntimeOption option, EventInitializer initializer) {
    	this();
    	this.option = option;
    	if(initializer != null) {
    		initializer.init(this);
    	}
    }

    public void addLast(EventHandler handler) {
		AbstractEventHandlerContext newCtx = new DefaultEventHandlerContext(this, StringUtil.simpleClassName(handler), handler);
		AbstractEventHandlerContext prev = tail.prev;
		newCtx.prev = prev;
		newCtx.next = tail;
		prev.next = newCtx;
		tail.prev = newCtx;
    }

    public void addFirst(EventHandler handler) {
    	AbstractEventHandlerContext newCtx = new DefaultEventHandlerContext(this, StringUtil.simpleClassName(handler), handler);
    	
    	AbstractEventHandlerContext nextCtx = head.next;
        newCtx.prev = head;
        newCtx.next = nextCtx;
        head.next = newCtx;
        nextCtx.prev = newCtx;
    }

    @Override
    public final Map<String, EventHandler> toMap() {
        Map<String, EventHandler> map = new LinkedHashMap<String, EventHandler>();
        AbstractEventHandlerContext ctx = head.next;
        for (;;) {
            if (ctx == tail) {
                return map;
            }
            map.put(ctx.name(), ctx.handler());
            ctx = ctx.next;
        }
    }

    @Override
    public final Iterator<Map.Entry<String, EventHandler>> iterator() {
        return toMap().entrySet().iterator();
    }

    /**
     * Returns the {@link String} representation of this pipeline.
     */
    @Override
    public final String toString() {
        StringBuilder buf = new StringBuilder()
            .append(StringUtil.simpleClassName(this))
            .append('{');
        AbstractEventHandlerContext ctx = head.next;
        for (;;) {
            if (ctx == tail) {
                break;
            }

            buf.append('(')
               .append(ctx.name())
               .append(" = ")
               .append(ctx.handler().getClass().getName())
               .append(')');

            ctx = ctx.next;
            if (ctx == tail) {
                break;
            }

            buf.append(", ");
        }
        buf.append('}');
        return buf.toString();
    }

	@Override
	public EventPipeline fireBeforeConnect(Channel localChannel) {
		AbstractEventHandlerContext.invokeBeforeConnect(head, localChannel);
		return this;
	}

	@Override
	public EventPipeline fireBeforeRequest(Channel localChannel, HttpRequest msg, ParamContext paramCtx) {
		AbstractEventHandlerContext.invokeBeforeRequest(head, localChannel, msg, paramCtx);
		return this;
	}

	@Override
	public EventPipeline fireBeforeRequest(Channel localChannel, HttpContent msg, ParamContext paramCtx) {
		AbstractEventHandlerContext.invokeBeforeRequest(head, localChannel, msg, paramCtx);
		return this;
	}

	@Override
	public EventPipeline fireAfterResponse(Channel localChannel, Channel remoteChannel,
			HttpResponse msg, ParamContext paramCtx) {
		AbstractEventHandlerContext.invokeAfterResponse(head, localChannel, remoteChannel, msg, paramCtx);
		return this;
	}

	@Override
	public EventPipeline fireAfterResponse(Channel localChannel, Channel remoteChannel,
			HttpContent msg, ParamContext paramCtx) {
		AbstractEventHandlerContext.invokeAfterResponse(head, localChannel, remoteChannel, msg, paramCtx);
		return this;
	}
	
	@Override
	public EventPipeline fireBeforeRequest(Channel localChannel, ByteBuf msg, ParamContext paramCtx) {
		AbstractEventHandlerContext.invokeBeforeRequest(head, localChannel, msg, paramCtx);
		return this;
	}
	
	@Override
	public EventPipeline fireAfterResponse(Channel localChannel, Channel remoteChannel,
			ByteBuf msg, ParamContext paramCtx) {
		AbstractEventHandlerContext.invokeAfterResponse(head, localChannel, remoteChannel, msg, paramCtx);
		return this;
	}

	@Override
	public RuntimeOption option() {
		return option;
	}

	@Override
	public WebSocketEventHandlerInvoker fireBeforeRequest(Channel localChannel, WebSocketFrame msg, ParamContext paramCtx) {
		AbstractEventHandlerContext.invokeBeforeRequest(head, localChannel, msg, paramCtx);
		return this;
	}

	@Override
	public WebSocketEventHandlerInvoker fireAfterResponse(Channel localChannel, Channel remoteChannel,
			WebSocketFrame msg, ParamContext paramCtx) {
		AbstractEventHandlerContext.invokeAfterResponse(head, localChannel, remoteChannel, msg, paramCtx);
		return this;
	}
}
