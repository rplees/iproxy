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
import io.netty.util.internal.ObjectUtil;

public abstract class AbstractEventHandlerContext implements EventHandlerContext {
	volatile AbstractEventHandlerContext next;
	volatile AbstractEventHandlerContext prev;
	final String name;
	EventPipeline pipeline;

	AbstractEventHandlerContext(EventPipeline pipeline, String name) {
		this.name = ObjectUtil.checkNotNull(name, "name");
		this.pipeline = pipeline;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public EventPipeline pipeline() {
		return pipeline;
	}

	private AbstractEventHandlerContext findWebSocketContextNext() {
		AbstractEventHandlerContext ctx = this;
		do {
			ctx = ctx.next;
		} while (!(ctx.handler() instanceof WebSocketEventHandler));
		return ctx;
	}

	private AbstractEventHandlerContext findTunnelContextNext() {
		AbstractEventHandlerContext ctx = this;
		do {
			ctx = ctx.next;
		} while (!(ctx.handler() instanceof TunnelEventHandler));
		return ctx;
	}

	private AbstractEventHandlerContext findHttpContextNext() {
		AbstractEventHandlerContext ctx = this;
		do {
			ctx = ctx.next;
		} while (!(ctx.handler() instanceof HttpEventHandler));
		return ctx;
	}

	private AbstractEventHandlerContext findContextNext0() {
		AbstractEventHandlerContext ctx = this;
		return ctx.next;
	}

	@Override
	public EventHandlerContext fireBeforeConnect(Channel localChannel) {
		invokeBeforeConnect(findContextNext0(), localChannel);
		return this;
	}

	static void invokeBeforeConnect(final AbstractEventHandlerContext next, Channel localChannel) {
		next.invokeBeforeConnect(localChannel);
	}

	private void invokeBeforeConnect(Channel localChannel) {
		try {
			((EventHandler) handler()).beforeConnect(localChannel, this);
		} catch (Exception e) {
			exceptionCaught(e);
		}
	}

	private void exceptionCaught(Throwable throwable) {
		throwable.printStackTrace();
	}

	@Override
	public EventHandlerContext fireBeforeRequest(Channel localChannel, ByteBuf msg, ParamContext paramCtx) {
		invokeBeforeRequest(findTunnelContextNext(), localChannel, msg, paramCtx);
		return this;
	}

	static void invokeBeforeRequest(final AbstractEventHandlerContext next, Channel localChannel, ByteBuf msg,
			ParamContext paramCtx) {
		next.invokeBeforeRequest(localChannel, msg, paramCtx);
	}

	private void invokeBeforeRequest(Channel localChannel, ByteBuf msg, ParamContext paramCtx) {
		try {
			((TunnelEventHandler) handler()).beforeRequest(localChannel, msg, paramCtx, this);
		} catch (Exception e) {
			exceptionCaught(e);
		}
	}

	@Override
	public EventHandlerContext fireBeforeRequest(Channel localChannel, HttpRequest msg, ParamContext paramCtx) {
		invokeBeforeRequest(findHttpContextNext(), localChannel, msg, paramCtx);
		return this;
	}

	static void invokeBeforeRequest(final AbstractEventHandlerContext next, Channel localChannel, HttpRequest msg,
			ParamContext paramCtx) {
		next.invokeBeforeRequest(localChannel, msg, paramCtx);
	}

	private void invokeBeforeRequest(Channel localChannel, HttpRequest msg, ParamContext paramCtx) {
		try {
			((HttpEventHandler) handler()).beforeRequest(localChannel, msg, paramCtx, this);
		} catch (Exception e) {
			exceptionCaught(e);
		}
	}

	@Override
	public EventHandlerContext fireBeforeRequest(Channel localChannel, HttpContent msg, ParamContext paramCtx) {
		invokeBeforeRequest(findHttpContextNext(), localChannel, msg, paramCtx);
		return this;
	}

	static void invokeBeforeRequest(final AbstractEventHandlerContext next, Channel localChannel, HttpContent msg,
			ParamContext paramCtx) {
		next.invokeBeforeRequest(localChannel, msg, paramCtx);
	}

	private void invokeBeforeRequest(Channel localChannel, HttpContent msg, ParamContext paramCtx) {
		try {
			((HttpEventHandler) handler()).beforeRequest(localChannel, msg, paramCtx, this);
		} catch (Exception e) {
			exceptionCaught(e);
		}
	}

	@Override
	public EventHandlerContext fireAfterResponse(Channel localChannel, Channel remoteChannel, ByteBuf msg,
			ParamContext paramCtx) {
		invokeAfterResponse(findTunnelContextNext(), localChannel, remoteChannel, msg, paramCtx);
		return this;
	}

	static void invokeAfterResponse(final AbstractEventHandlerContext next, Channel localChannel,
			Channel remoteChannel, ByteBuf msg, ParamContext paramCtx) {
		next.invokeAfterResponse(localChannel, remoteChannel, msg, paramCtx);
	}

	private void invokeAfterResponse(Channel localChannel, Channel remoteChannel, ByteBuf msg, ParamContext paramCtx) {
		try {
			((TunnelEventHandler) handler()).afterResponse(localChannel, remoteChannel, msg, paramCtx, this);
		} catch (Exception e) {
			exceptionCaught(e);
		}
	}

	@Override
	public EventHandlerContext fireAfterResponse(Channel localChannel, Channel remoteChannel, HttpResponse msg,
			ParamContext paramCtx) {
		invokeAfterResponse(findHttpContextNext(), localChannel, remoteChannel, msg, paramCtx);
		return this;
	}

	static void invokeAfterResponse(final AbstractEventHandlerContext next, Channel localChannel,
			Channel remoteChannel, HttpResponse msg, ParamContext paramCtx) {
		next.invokeAfterResponse(localChannel, remoteChannel, msg, paramCtx);
	}

	private void invokeAfterResponse(Channel localChannel, Channel remoteChannel, HttpResponse msg,
			ParamContext paramCtx) {
		try {
			((HttpEventHandler) handler()).afterResponse(localChannel, remoteChannel, msg, paramCtx, this);
		} catch (Exception e) {
			exceptionCaught(e);
		}
	}

	@Override
	public EventHandlerContext fireAfterResponse(Channel localChannel, Channel remoteChannel, HttpContent msg,
			ParamContext paramCtx) {
		invokeAfterResponse(findHttpContextNext(), localChannel, remoteChannel, msg, paramCtx);
		return this;
	}

	static void invokeAfterResponse(final AbstractEventHandlerContext next, Channel localChannel,
			Channel remoteChannel, HttpContent msg, ParamContext paramCtx) {
		next.invokeAfterResponse(localChannel, remoteChannel, msg, paramCtx);
	}

	private void invokeAfterResponse(Channel localChannel, Channel remoteChannel, HttpContent msg,
			ParamContext paramCtx) {
		try {
			((HttpEventHandler) handler()).afterResponse(localChannel, remoteChannel, msg, paramCtx, this);
		} catch (Exception e) {
			exceptionCaught(e);
		}
	}

	@Override
	public EventHandlerContext fireBeforeRequest(Channel localChannel, WebSocketFrame msg, ParamContext paramCtx) {
		invokeBeforeRequest(findWebSocketContextNext(), localChannel, msg, paramCtx);
		return this;
	}

	static void invokeBeforeRequest(final AbstractEventHandlerContext next, Channel localChannel, WebSocketFrame msg,
			ParamContext paramCtx) {
		next.invokeBeforeRequest(localChannel, msg, paramCtx);
	}

	private void invokeBeforeRequest(Channel localChannel, WebSocketFrame msg, ParamContext paramCtx) {
		try {
			((WebSocketEventHandler) handler()).beforeRequest(localChannel, msg, paramCtx, this);
		} catch (Exception e) {
			exceptionCaught(e);
		}
	}

	@Override
	public EventHandlerContext fireAfterResponse(Channel localChannel, Channel remoteChannel, WebSocketFrame msg,
			ParamContext paramCtx) {
		invokeAfterResponse(findWebSocketContextNext(), localChannel, remoteChannel, msg, paramCtx);
		return this;
	}

	static void invokeAfterResponse(final AbstractEventHandlerContext next, Channel localChannel,
			Channel remoteChannel, WebSocketFrame msg, ParamContext paramCtx) {
		next.invokeAfterResponse(localChannel, remoteChannel, msg, paramCtx);
	}

	private void invokeAfterResponse(Channel localChannel, Channel remoteChannel, WebSocketFrame msg,
			ParamContext paramCtx) {
		try {
			((WebSocketEventHandler) handler()).afterResponse(localChannel, remoteChannel, msg, paramCtx, this);
		} catch (Exception e) {
			exceptionCaught(e);
		}
	}
}
