package com.rplees.iproxy.proto;

import java.net.MalformedURLException;
import java.net.URL;

import com.rplees.iproxy.proto.Proto.HttpProto;
import com.rplees.iproxy.proto.Proto.ProtoType;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;

public class ProtoUtil {
	public static boolean isHttp(ByteBuf byteBuf) {
		if(byteBuf.readableBytes() < 8) {
			return false;
		}
		
		String magicWorld = byteBuf.toString(0, 8, CharsetUtil.UTF_8);
		return magicWorld.startsWith("GET ") 
				|| magicWorld.startsWith("POST ") 
				|| magicWorld.startsWith("HEAD ")
				|| magicWorld.startsWith("PUT ") 
				|| magicWorld.startsWith("DELETE ")
				|| magicWorld.startsWith("OPTIONS ") 
				|| magicWorld.startsWith("CONNECT ")
				|| magicWorld.startsWith("TRACE ");
	}
	
	public static boolean isHttpConnect(ByteBuf byteBuf) {
		if(byteBuf.readableBytes() < 8) {
			return false;
		}
		
		String magicWorld = byteBuf.toString(0, 8, CharsetUtil.UTF_8);
		return magicWorld.startsWith("CONNECT ");
	}
	
	public static URL httpUrl(String host) {
		try {
			return new URL("http://" + host);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static HttpProto base(HttpRequest request) {
		String host = request.headers().get(HttpHeaderNames.HOST);
		URL url = httpUrl(host);
		boolean proxy = request.headers().contains(HttpHeaderNames.PROXY_CONNECTION);
		
		return HttpProto.builder()
				.host(url.getHost())
				.port(url.getPort() != -1 ? url.getPort() : url.getDefaultPort())
				.proxy(proxy)
				.build();
	}
	
	public static HttpProto fill(HttpRequest request, HttpProto proto) {
		ProtoType protoType = ProtoType.HTTP;
		if(HttpHeaderValues.WEBSOCKET.toString().equals(request.headers().get(HttpHeaderNames.UPGRADE))) {
			protoType = ProtoType.WEBSORKET;
		} else {
			//RICH ProtoType
		}
		
		proto.method = request.method();
		proto.uri = request.uri();
		proto.request = request;
		proto.protoType = protoType;
		return proto;
	}
	
	public static HttpProto parseHttpRequestProto(HttpRequest request) {
		HttpProto proto = base(request);
		fill(request, proto);
		return proto;
	}
}
