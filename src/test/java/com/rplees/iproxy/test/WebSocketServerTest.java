package com.rplees.iproxy.test;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.intercept.EventInitializer;
import com.rplees.iproxy.intercept.adapter.WebSocketEventHandlerAdapter;
import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.intercept.pipeline.EventPipeline;
import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.local.ServerStarter;
import com.rplees.iproxy.proto.ParamContext;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * 
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 2, 2023 12:44:10 PM
 */
public class WebSocketServerTest {
	private static final Logger log = LoggerFactory.getLogger(AcceptServerTest.class);
	
	public static void main(String[] args) {
		int port = 1113;
        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        
        long millis = System.currentTimeMillis();
        
        RuntimeOption option = RuntimeOption.builder()
        		.ssl(true)
        		.initializer(new EventInitializer() {
					@Override
					public void init(EventPipeline pipeline) {
						pipeline.addLast(new WebSocketEventHandlerAdapter() {
		        			@Override
		        			public void beforeRequest(Channel localChannel, WebSocketFrame msg, ParamContext paramCtx,
		        					EventHandlerContext ctx) throws Exception {
		        				if(msg instanceof TextWebSocketFrame) {
		        					TextWebSocketFrame frame = (TextWebSocketFrame) msg;
		        					frame.content().clear();
		        					frame.content().writeBytes(("beforeRequest" + new Date()).getBytes());
		        					frame.content().markReaderIndex();
		        				}
		        				super.beforeRequest(localChannel, msg, paramCtx, ctx);
		        			}
		        			
		        			@Override
		        			public void afterResponse(Channel localChannel, Channel remoteChannel, WebSocketFrame msg, ParamContext paramCtx,
		        					EventHandlerContext ctx) throws Exception {
		        				if(msg instanceof TextWebSocketFrame) {
		        					TextWebSocketFrame frame = (TextWebSocketFrame) msg;
		        					frame.content().clear();
		        					frame.content().writeBytes(("afterResponse" + new Date()).getBytes());
		        					frame.content().markReaderIndex();
		        				}
		        				
		        				super.afterResponse(localChannel, remoteChannel, msg, paramCtx, ctx);
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
