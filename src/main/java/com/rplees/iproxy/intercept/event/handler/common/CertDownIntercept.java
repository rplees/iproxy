package com.rplees.iproxy.intercept.event.handler.common;

import java.security.cert.X509Certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.intercept.adapter.HttpEventHandlerAdapter;
import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.local.ICertFactory;
import com.rplees.iproxy.proto.ParamContext;
import com.rplees.iproxy.proto.Proto;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

/***
 * 处理证书下载页面
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 5, 2023 10:48:25 PM
 */
public class CertDownIntercept extends HttpEventHandlerAdapter {
	private static final Logger log = LoggerFactory.getLogger(CertDownIntercept.class);
	String DEFAULT_DOWN_URL = "^.*/iproxy_ca.crt.*$"; 
	String DEFAULT_DOWN_URL_TIP = "^.*/index.rp*$"; 
	
    private X509Certificate cert = null;
    private String downUrl = null;
    private String downUrlTip = null;
    

    public CertDownIntercept(ICertFactory certFactory) {
        try {
			this.cert = certFactory.cert();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("CertDownIntercept init error.", e);
		}
    }
    
    public CertDownIntercept downUrl(String downUrl) {
    	this.downUrl = downUrl;
    	return this;
    }
    
    public CertDownIntercept downUrlTip(String downUrlTip) {
    	this.downUrlTip = downUrlTip;
    	return this;
    }
    
    public CertDownIntercept(X509Certificate cert) {
        this.cert = cert;
    }

    boolean isVisitSelf(ParamContext paramCtx) {
    	Proto proto = paramCtx.router().local();
    	return ! proto.proxy();
    }
    
    boolean flag;
    @Override
    public void beforeRequest(Channel localChannel, HttpRequest msg, ParamContext paramCtx, EventHandlerContext ctx) throws Exception {
    	if(downUrl == null) {
    		downUrl = DEFAULT_DOWN_URL;
    	}
    	if(downUrlTip == null) {
    		downUrlTip = DEFAULT_DOWN_URL_TIP;
    	}
    	
//        if (isVisitSelf(paramCtx)) { // 直接访问
            if (msg.uri().matches(downUrl)) {  //下载证书
            	flag = false;
            	byte[] bts = cert.getEncoded();
                FullHttpResponse httpResponse = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK, Unpooled.wrappedBuffer(bts));
                
                httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/x-x509-ca-cert");
                httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, bts.length);
                localChannel.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
            }
//            else if (msg.uri().matches("^.*/favicon.ico$")) {
//            	flag = false;
//				URL url = Thread.currentThread().getContextClassLoader().getResource("favicon.ico");
//				File file = new File(url.toURI());
//				if(! file.exists()) {
//					HttpResponse httpResponse = new DefaultHttpResponse(msg.protocolVersion(), HttpResponseStatus.NOT_FOUND);
//					localChannel.write(httpResponse).addListener(ChannelFutureListener.CLOSE);;
//				} else {
//					MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
//					RandomAccessFile raf = new RandomAccessFile(file, "r");
//					HttpResponse httpResponse = new DefaultHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
//					httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, mimeTypesMap.getContentType(file));
//					localChannel.write(httpResponse);
//					localChannel.write(new DefaultFileRegion(raf.getChannel(), 0, raf.length()));
//					localChannel.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
//					raf.close();
//				}
//            }
            else if(msg.uri().matches(downUrlTip)) {  //跳转下载页面
            	flag = false;
            	String html = "<html><body><div style=\"margin-top:100px;text-align:center;\"><a href=\"iproxy_ca.crt\">点击下载 iproxy_ca.crt 证书</a></div></body></html>";
            	FullHttpResponse httpResponse = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK, Unpooled.wrappedBuffer(html.getBytes("UTF-8")));
            	
                httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=utf-8");
                httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, html.getBytes().length);
                localChannel.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
            } else {
            	flag = true;
            	ctx.fireBeforeRequest(localChannel, msg, paramCtx);
            }
//        } else {
//        	flag = true;
//        	ctx.fireBeforeRequest(localChannel, msg, paramCtx);
//        }
    }

    @Override
    public void beforeRequest(Channel localChannel, HttpContent msg, ParamContext paramCtx, EventHandlerContext pipeline) throws Exception {
        if (flag) {
            pipeline.fireBeforeRequest(localChannel, msg, paramCtx);
        }
    }
}