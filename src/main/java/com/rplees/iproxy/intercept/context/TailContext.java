package com.rplees.iproxy.intercept.context;

import com.rplees.iproxy.intercept.event.handler.EventHandler;
import com.rplees.iproxy.intercept.event.handler.HttpEventHandler;
import com.rplees.iproxy.intercept.event.handler.TunnelEventHandler;
import com.rplees.iproxy.intercept.event.handler.WebSocketEventHandler;
import com.rplees.iproxy.intercept.pipeline.EventPipeline;
import com.rplees.iproxy.proto.ParamContext;
import com.rplees.iproxy.remote.handler.ChannelBridge;
import com.rplees.iproxy.remote.handler.ChannelBridgeCollector;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

final class TailContext extends AbstractEventHandlerContext implements HttpEventHandler, TunnelEventHandler, WebSocketEventHandler {
//	private static final Logger log = LoggerFactory.getLogger("TailContext");
	  
	static final String HEAD_NAME = "tail";
	
	TailContext(EventPipeline pipeline) {
        super(pipeline, HEAD_NAME);
    }
	
	private ChannelBridge findBridge(Channel localChannel, ParamContext paramCtx) {
		return ChannelBridgeCollector.instance().findBridge(localChannel, paramCtx, pipeline.option());
	}
	
	@Override
	public EventHandler handler() {
		return this;
	}

	@Override
	public void beforeConnect(Channel localChannel, EventHandlerContext ctx) throws Exception {
		//NOOP
	}

	@Override
	public void beforeRequest(Channel localChannel, HttpRequest msg, ParamContext paramCtx, EventHandlerContext ctx)
			throws Exception {
		try {
			findBridge(localChannel, paramCtx).sendToRemote(localChannel, msg, ctx);
		} finally {
//			log.trace("beforeRequest httpRequest:{}", msg.getClass().getSimpleName());
		}
	}

	@Override
	public void beforeRequest(Channel localChannel, HttpContent msg, ParamContext paramCtx, EventHandlerContext ctx)
			throws Exception {
		try {
			findBridge(localChannel, paramCtx).sendToRemote(localChannel, msg, ctx);
		} finally {
//			log.trace("beforeRequest httpContent: {} refCnt: {}", msg.getClass().getSimpleName(), msg.refCnt());
		}
	}

	@Override
	public void beforeRequest(Channel localChannel, ByteBuf msg, ParamContext paramCtx, EventHandlerContext ctx) throws Exception {
		try {
			findBridge(localChannel, paramCtx).sendToRemote(localChannel, msg, ctx);
		} finally {
//			log.trace("beforeRequest ByteBuf: {} refCnt:{}", msg.getClass().getSimpleName(),  msg.refCnt());
		}
	}

	@Override
	public void afterResponse(Channel localChannel, Channel remoteChannel, ByteBuf msg, ParamContext paramCtx,
			EventHandlerContext ctx) throws Exception {
		try {
			localChannel.writeAndFlush(msg);
		} finally {
//			log.trace("afterResponse ByteBuf:{} refCnt:{}", msg.getClass().getSimpleName(), msg.refCnt());
		}
	}
	
	@Override
	public void afterResponse(Channel localChannel, Channel remoteChannel, HttpResponse msg, ParamContext paramCtx,
			EventHandlerContext ctx) throws Exception {
		try {
			localChannel.writeAndFlush(msg);
		} finally {
//			log.debug("afterResponse HttpResponse: {}", msg.getClass().getSimpleName());
		}
	}

	@Override
	public void afterResponse(Channel localChannel, Channel remoteChannel, HttpContent msg, ParamContext paramCtx,
			EventHandlerContext ctx) throws Exception {
		try {
			localChannel.writeAndFlush(msg);
		} finally {
//			log.debug("afterResponse HttpContent: {} refCnt:{}", msg.getClass().getSimpleName(), msg.refCnt());
		}
	}

	@Override
	public void beforeRequest(Channel localChannel, WebSocketFrame msg, ParamContext paramCtx, EventHandlerContext ctx) throws Exception {
		try {
			findBridge(localChannel, paramCtx).sendToRemote(localChannel, msg, ctx);
		} finally {
//			log.debug("beforeRequest WebSocketFrame: {} refCnt:{}", msg.getClass().getSimpleName(),  msg.refCnt());
		}
	}

	@Override
	public void afterResponse(Channel localChannel, Channel remoteChannel, WebSocketFrame msg, ParamContext paramCtx, EventHandlerContext ctx)
			throws Exception {
		try {
			localChannel.writeAndFlush(msg);
		} finally {
//			log.debug("afterResponse WebSocketFrame: {} refCnt:{}", msg.getClass().getSimpleName(), msg.refCnt());
		}
	}
}