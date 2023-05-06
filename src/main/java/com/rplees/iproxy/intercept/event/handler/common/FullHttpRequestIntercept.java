package com.rplees.iproxy.intercept.event.handler.common;

import java.nio.charset.Charset;

import com.rplees.iproxy.intercept.adapter.HttpEventHandlerAdapter;
import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.proto.ParamContext;
import com.rplees.iproxy.proto.Proto.HttpProto;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 7, 2023 9:01:05 AM
 */
public abstract class FullHttpRequestIntercept extends HttpEventHandlerAdapter {
	public static final String DECOMPRESS = "__decompress__request__"; 
	public static final String AGGREGATOR = "__aggregator__request__"; 

    @Override
    public final void beforeRequest(Channel localChannel, HttpRequest msg, ParamContext paramCtx, EventHandlerContext ctx) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            HttpProto local = paramCtx.router().local();
            local.playload(request.content().toString(Charset.defaultCharset()));
            
            if(match(paramCtx)) {
            	if(requestMatch(request, paramCtx,  ctx)) {
            		requestHandle(request, paramCtx, ctx);
            		if (request.headers().contains(HttpHeaderNames.CONTENT_LENGTH)) {
            			request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
            		}
            	}
            }
            
            if(localChannel.pipeline().get(AGGREGATOR) != null) {
            	localChannel.pipeline().remove(AGGREGATOR);
            }
            
            if(localChannel.pipeline().get(DECOMPRESS) != null) {
            	localChannel.pipeline().remove(DECOMPRESS);
            }
        } else if(match(paramCtx)) {
        	localChannel.pipeline().addAfter(RuntimeOption.HTTP_CODEC, DECOMPRESS, new HttpContentDecompressor());
        	localChannel.pipeline().addAfter(DECOMPRESS, AGGREGATOR, new HttpObjectAggregator(ctx.pipeline().option().getMaxContentLength()));
            localChannel.pipeline().fireChannelRead(msg);
            return;
        }
        
        ctx.fireBeforeRequest(localChannel, msg, paramCtx);
    }

    public abstract boolean match(ParamContext paramCtx);
    
    public boolean requestMatch(FullHttpRequest msg, ParamContext paramCtx, EventHandlerContext ctx) {
    	return true;
    }
    
    public abstract void requestHandle(FullHttpRequest msg, ParamContext paramCtx, EventHandlerContext ctx);
}
