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

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;

/**
 * 
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 2, 2023 12:44:10 PM
 */
public class ForwardServerTest {
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
        			int i = 0;
        			@Override
        			public void init(EventPipeline pipeline) {
        				pipeline.addLast(new HttpEventHandlerAdapter() {
        					String[] hosts = new String[] {"cn.bing.com", "juejin.com", "xxxxxx.com", "bbbb.com", "uuuu.com"};
        					
        					@Override
        					public void beforeRequest(Channel localChannel, HttpRequest msg, ParamContext paramCtx,
        							EventHandlerContext ctx) throws Exception {
        						i ++;
        						
        						if(StringUtils.contains(paramCtx.router().local().host(), "www.baidu.com")) {
        							String h = hosts[i % 4];
        							paramCtx.router().forward(h, 80, false, msg);
        						}
        						
        						if(StringUtils.contains(paramCtx.router().local().host(), "oooooooooooooooooo.com")) {
        							String h = "toolkit-server.rplees.com";
        							paramCtx.router().forward(h, 80, false, msg);
        						}
        						
        						if(StringUtils.contains(paramCtx.router().local().host(), "11111111111111iui.com")) {
        							String h = "121.43.183.245";
        							paramCtx.router().forward(h, 9099, false, msg);
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
