package com.rplees.iproxy.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.intercept.EventInitializer.DefaultEventInitializer;
import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.intercept.event.handler.common.FullHttpIntercept;
import com.rplees.iproxy.intercept.pipeline.EventPipeline;
import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.local.ServerStarter;
import com.rplees.iproxy.proto.ParamContext;
import com.rplees.iproxy.utils.StringUtils;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

/**
 * 
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 2, 2023 12:44:10 PM
 */
public class FullResponseServerTest {
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
        				pipeline.addFirst(new FullHttpIntercept() {
        					public boolean match(ParamContext paramCtx) {
        						if(StringUtils.contains(paramCtx.router().local().host(), "toolkit-server.rplees.com")) {
									return true;
								}
								return false;
        					}
        					
							@Override
							public void responseHandle(FullHttpResponse msg, ParamContext paramCtx, EventHandlerContext ctx) {
								System.out.println(msg.content().toString(CharsetUtil.UTF_8));
								msg.content().writeBytes("<script>alert('hello')</script>".getBytes());
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
