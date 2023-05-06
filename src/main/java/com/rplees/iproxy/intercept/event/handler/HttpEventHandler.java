package com.rplees.iproxy.intercept.event.handler;

import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.proto.ParamContext;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public interface HttpEventHandler extends EventHandler {
	
	/**
	 * 拦截代理服务器到目标服务器的请求头
	 */
	void beforeRequest(Channel localChannel, HttpRequest msg, ParamContext paramCtx, EventHandlerContext ctx) throws Exception;

	/**
	 * 拦截代理服务器到目标服务器的请求体
	 */
	void beforeRequest(Channel localChannel, HttpContent msg, ParamContext paramCtx, EventHandlerContext ctx)  throws Exception;

	/**
	 * 拦截代理服务器到客户端的响应头
	 */
	void afterResponse(Channel localChannel, Channel remoteChannel, HttpResponse msg, ParamContext paramCtx, EventHandlerContext ctx)  throws Exception;

	/**
	 * 拦截代理服务器到客户端的响应体
	 */
	void afterResponse(Channel localChannel, Channel remoteChannel, HttpContent msg, ParamContext paramCtx, EventHandlerContext ctx)
			throws Exception;
}
