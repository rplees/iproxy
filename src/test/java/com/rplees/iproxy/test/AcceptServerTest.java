package com.rplees.iproxy.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.intercept.EventInitializer;
import com.rplees.iproxy.intercept.func.accept.GlobalEventHandler.DefaultGlobalEventHandler;
import com.rplees.iproxy.intercept.pipeline.EventPipeline;
import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.local.ServerStarter;
import com.rplees.iproxy.proto.Proto;
import com.rplees.iproxy.proto.Proto.HttpProto;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class AcceptServerTest {
	private static final Logger log = LoggerFactory.getLogger(AcceptServerTest.class);

    private static final Map<String, Integer> CLIENT_LIMIT_MAP = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
    	
    	int port = 1113;
        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        
        long millis = System.currentTimeMillis();
        
        RuntimeOption option = RuntimeOption.builder()
        		.ssl(true)
        		.globalEventHandler(new DefaultGlobalEventHandler() {
        			@Override
					public boolean accept(Channel localChannel, Proto proto) {
		                Integer count = CLIENT_LIMIT_MAP.getOrDefault(proto.host(), 1);
		                if (count > 0) {
		                	if(proto instanceof HttpProto) {
		                		FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN);
		                		fullHttpResponse.content().writeBytes("<html><div>Visit too often</div></html>".getBytes());
		                		localChannel.writeAndFlush(fullHttpResponse);
		                	}
		                    return false;
		                }
		                CLIENT_LIMIT_MAP.put(proto.host(), count + 1);
						return true;
					}
        			
        			@Override
        			public boolean interested(Channel localChannel, Proto proto) {
        				return true;
        			}
        		})
        		.initializer(new EventInitializer() {
					@Override
					public void init(EventPipeline pipeline) {
					}
        		})
        		.build();
        new ServerStarter(option)
        	.startAsync(port);
        log.info("启动代理服务器, 端口: {}, 耗时: {} ms", port, (System.currentTimeMillis() - millis));
    }
}
