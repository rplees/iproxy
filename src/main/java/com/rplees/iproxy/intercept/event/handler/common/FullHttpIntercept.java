package com.rplees.iproxy.intercept.event.handler.common;

import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.proto.ParamContext;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponse;

public abstract class FullHttpIntercept extends FullHttpRequestIntercept {
	public static final String DECOMPRESS = "__decompress__response__"; 
	public static final String AGGREGATOR = "__aggregator__response__"; 
	
    @Override
    public final void afterResponse(Channel localChannel, Channel remoteChannel, HttpResponse msg, ParamContext paramCtx, EventHandlerContext ctx) throws Exception {
        if (msg instanceof FullHttpResponse) {
        	if(match(paramCtx)) {
        		FullHttpResponse response = (FullHttpResponse) msg;
        		if(responseMatch(response, paramCtx, ctx)) {
        			responseHandle(response, paramCtx, ctx);
        			if (response.headers().contains(HttpHeaderNames.CONTENT_LENGTH)) {
        				response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
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
            remoteChannel.pipeline().addAfter(RuntimeOption.HTTP_CODEC, DECOMPRESS, new HttpContentDecompressor());
            remoteChannel.pipeline().addAfter(DECOMPRESS, AGGREGATOR, new HttpObjectAggregator(ctx.pipeline().option().getMaxContentLength()));
            remoteChannel.pipeline().fireChannelRead(msg);
            return;
        }
        
        ctx.fireAfterResponse(localChannel, remoteChannel, msg, paramCtx);
    }
    
    @Override
    public void requestHandle(FullHttpRequest msg, ParamContext paramCtx, EventHandlerContext ctx) {
    	
    }
    
    public boolean responseMatch(FullHttpResponse msg, ParamContext paramCtx, EventHandlerContext ctx) {
    	return true;
    }
    
    public abstract void responseHandle(FullHttpResponse msg, ParamContext paramCtx, EventHandlerContext ctx);
}
