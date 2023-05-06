package com.rplees.iproxy.intercept.invoke;

import com.rplees.iproxy.proto.ParamContext;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * HTTP 执行器 
 *
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 8, 2023 1:15:53 PM
 */
public interface HttpEventHandlerInvoker {
	HttpEventHandlerInvoker fireBeforeRequest(Channel localChannel, HttpRequest msg, ParamContext paramCtx);
	HttpEventHandlerInvoker fireBeforeRequest(Channel localChannel, HttpContent msg, ParamContext paramCtx);
	HttpEventHandlerInvoker fireAfterResponse(Channel localChannel, Channel remoteChannel, HttpResponse msg, ParamContext paramCtx);
	HttpEventHandlerInvoker fireAfterResponse(Channel localChannel, Channel remoteChannel, HttpContent msg, ParamContext paramCtx);
}
