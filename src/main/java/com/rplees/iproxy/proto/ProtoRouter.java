package com.rplees.iproxy.proto;

import java.net.MalformedURLException;
import java.net.URL;

import com.rplees.iproxy.proto.Proto.Encrypt;
import com.rplees.iproxy.proto.Proto.HttpProto;
import com.rplees.iproxy.utils.StringUtils;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;

/**
 * 路由 
 *
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 13, 2023 12:30:32 PM
 */
@SuppressWarnings("unchecked")
public class ProtoRouter {
	Proto local;
	Proto remote;
	
	public HttpProto httpLocal() {
		return local();
	}
	
	public <T extends Proto> T local() {
		return (T) local;
	}

	public <T extends Proto> T remote() {
		return (T) remote;
	}

	public ProtoRouter(Proto local, Proto remote) {
		this.local = local;
		this.remote = remote;
	}
	
	/**
	 * 转发 
	 * @param newUri
	 * @param msg
	 */
	public void forward(String newUri, HttpRequest msg) {
		URL url;
		try {
			url = new URL(newUri);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		
		String host = url.getHost();
		int port = url.getPort() != -1 ? url.getPort() : url.getDefaultPort();
		boolean isHttps = "HTTPS".equalsIgnoreCase(url.getProtocol());
		forward(host, port, isHttps, msg);
	}
	
	/**
	 * 转发
	 * @param host
	 * @param port
	 * @param isHttps
	 * @param msg
	 */
	public void forward(String host, int port, boolean isHttps, HttpRequest msg) {
		HttpProto remote = (HttpProto) this.remote;
		msg.setUri(StringUtils.replace(remote.uri(), remote.host, host));
		msg.headers().set(HttpHeaderNames.HOST, host);
		
		remote.host = host;
		remote.port = port;
		
		if(isHttps) {
			remote.encrypt = Encrypt.SSL;
			remote.decrypted = true;
		} else {
			remote.encrypt = Encrypt.NONE;
			remote.decrypted = false;
		}
	}
	
	public static ProtoRouter from(Proto local) {
		return new ProtoRouter(local, local.copy());
	}
}
