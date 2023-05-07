package com.rplees.iproxy.test;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.intercept.EventInitializer;
import com.rplees.iproxy.intercept.event.handler.common.TraceLoggingHttpIntercept;
import com.rplees.iproxy.intercept.pipeline.EventPipeline;
import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.local.ServerStarter;
import com.rplees.iproxy.utils.Logs;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * 
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 2, 2023 12:44:10 PM
 */
public class LoggingServerTest {
	private static final Logger log = LoggerFactory.getLogger(LoggingServerTest.class);
	
	public static void main(String[] args) {
		int port = 1113;
        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        
        long millis = System.currentTimeMillis();
        RuntimeOption option = RuntimeOption.builder()
        		.initializer(new EventInitializer() {
					@Override
					public void init(EventPipeline pipeline) {
						Function<FullHttpResponse, Boolean> filter = (msg) -> {
//							String contentType = msg.headers().get("Content-Type");
//							if (StringUtils.contains(contentType, "image/")
//									|| StringUtils.contains(contentType, "png")
//									|| StringUtils.contains(contentType, "jpg")
//									|| StringUtils.contains(contentType, "jpeg")
//									) {
//								return true;
//							}
//							return false;
							
							return true;
						};
						
						pipeline.addLast(new TraceLoggingHttpIntercept(Logs.DEFAULT_LOG_PATH, filter));
					}
        			
        		})
        		.build();
        new ServerStarter(option).startAsync(port);
        log.info("启动代理服务器, 端口: {}, 耗时: {} ms", port, (System.currentTimeMillis() - millis));
	}
}
