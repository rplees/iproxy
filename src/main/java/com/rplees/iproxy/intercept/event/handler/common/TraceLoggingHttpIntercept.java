package com.rplees.iproxy.intercept.event.handler.common;

import java.nio.charset.Charset;
import java.util.function.Function;

import org.slf4j.Logger;

import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.proto.ParamContext;
import com.rplees.iproxy.proto.Proto.HttpProto;
import com.rplees.iproxy.utils.Logs;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

/**
 * 
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Apr 21, 2023 8:21:06 PM
 */
public class TraceLoggingHttpIntercept extends FullHttpIntercept {
	public static final String RN = "\r\n";
	public String logPath;
	Function<FullHttpResponse, Boolean> filter;
	
	@Override
	public boolean match(ParamContext paramCtx) {
		return true;
	}
	
	public TraceLoggingHttpIntercept() {
		this(Logs.DEFAULT_LOG_PATH, null);
	}
	
	public TraceLoggingHttpIntercept(String logPath, Function<FullHttpResponse, Boolean> filter) {
		this.logPath = logPath;
		this.filter = filter;
	}
	
	public Logger logger(ParamContext paramCtx) {
		return Logs.logger(logPath, paramCtx.router().httpLocal());
	}
	
	@Override
	public void responseHandle(FullHttpResponse msg, ParamContext paramCtx, EventHandlerContext ctx) {
		if(filter != null && filter.apply(msg)) {
			return;
		}
		
		HttpProto local = paramCtx.router().httpLocal();
		StringBuffer buffer = new StringBuffer();
		buffer.append("=======================START===================").append(RN)
			.append("HOST:" + local.host()).append(RN)
			.append("URI:" + local.uri()).append(RN)
			.append("PROTO:" + local).append(RN)
			.append("HTTPSTATUS:" + msg.status()).append(RN)
			;
		
		if(local.method() == HttpMethod.POST) {
			buffer.append("PLAYLOAD:" + local.playload()).append(RN);
		}
		
		HttpRequest request = local.request();
		request.headers().iteratorAsString().forEachRemaining(e -> {
			buffer.append("HEADER:" + e.getKey() + ":" + e.getValue()).append(RN);
		});
		
		String content = msg.content().toString(Charset.defaultCharset());
		buffer.append("RESPONSE:" + content).append(RN);
		buffer.append("=======================END===================").append(RN);
		Logger logger = logger(paramCtx);
		logger.trace(buffer.toString());
	}
}
