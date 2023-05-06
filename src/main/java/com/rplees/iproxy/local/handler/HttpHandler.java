package com.rplees.iproxy.local.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.proto.ParamContext;
import com.rplees.iproxy.proto.ProtoUtil;
import com.rplees.iproxy.proto.Proto.HttpProto;
import com.rplees.iproxy.utils.StringUtils;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;

/**
 * 
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 6, 2023 10:55:09 AM
 */
public class HttpHandler extends AbstractHandler {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	ParamContext paramCtx;
	public HttpHandler(RuntimeOption option) {
		super(option);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof HttpRequest) {
			HttpRequest request = (HttpRequest) msg;
			HttpProto origProto = (HttpProto) ctx.channel().attr(RuntimeOption.PROTO_ATTR_KEY).get();
			HttpProto uriProto = origProto.copy();
			
			ProtoUtil.fill(request, uriProto);
			paramCtx = paramCtx(uriProto);
			HttpProto local = paramCtx.router().local();
			if(! StringUtils.equals(local.uri(), request.uri())) {
				log.error("{}!==== >>>>>>>>>>>>>>URI: {}, REQUEST URI: {}", ctx.channel(), local.uri(), request.uri());
			}
			
            DecoderResult result = request.decoderResult();
            if (result.cause() instanceof Exception) {
            	log.error("异常: {}", result.cause().getLocalizedMessage());
                HttpResponseStatus status = HttpResponseStatus.BAD_REQUEST;
                HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                ReferenceCountUtil.release(msg);
                return;
            }
            
//            log.debug("触发 fireBeforeRequest-HttpRequest 消息事件, {}", msg);
            pipeline.fireBeforeRequest(ctx.channel(), request, paramCtx);
		} else if(msg instanceof HttpContent) {
			if(paramCtx == null) {
				log.error("HttpContent paramCtx is null, HttpContent: {}", msg);
			}
			
//			log.debug("触发 fireBeforeRequest-HttpContent 消息事件, {}", msg);
			pipeline.fireBeforeRequest(ctx.channel(), (HttpContent) msg, paramCtx);
		}
	}
}