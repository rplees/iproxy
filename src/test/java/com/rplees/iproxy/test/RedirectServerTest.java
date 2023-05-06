package com.rplees.iproxy.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.intercept.EventInitializer.DefaultEventInitializer;
import com.rplees.iproxy.intercept.adapter.HttpEventHandlerAdapter;
import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.intercept.pipeline.EventPipeline;
import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.local.ServerStarter;
import com.rplees.iproxy.proto.ParamContext;
import com.rplees.iproxy.utils.StringUtils;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * 
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 2, 2023 12:44:10 PM
 */
public class RedirectServerTest {
	private static final Logger log = LoggerFactory.getLogger(AcceptServerTest.class);
	
	public static void main(String[] args) {
		int port = 1113;
        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        
        long millis = System.currentTimeMillis();
        
        RuntimeOption option = RuntimeOption.builder()
        		.ssl(true)
        		.initializer(new DefaultEventInitializer() {
        			@Override
        			public void init(EventPipeline pipeline) {
        				pipeline.addLast(new HttpEventHandlerAdapter() {
        					@Override
        					public void beforeRequest(Channel localChannel, HttpRequest msg, ParamContext paramCtx,
        							EventHandlerContext ctx) throws Exception {
        						if(StringUtils.contains(paramCtx.router().local().host(), "www.baidu.com")) {
        							HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND, Unpooled.EMPTY_BUFFER);
        							response.headers().set(HttpHeaderNames.LOCATION, "http://www.taobao.com");
                                    localChannel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                                    return;
        						}
        						
        						super.beforeRequest(localChannel, msg, paramCtx, ctx);
        					}
        				});
        			}
        		})
        		.build();
        new ServerStarter(option)
        	.startAsync(port);
        log.info("启动代理服务器, 端口: {}, 耗时: {} ms", port, (System.currentTimeMillis() - millis));
	}
}
