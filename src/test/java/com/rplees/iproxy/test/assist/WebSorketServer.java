package com.rplees.iproxy.test.assist;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LoggingHandler;

/**
 * 
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 2, 2023 12:44:10 PM
 */
public class WebSorketServer {
	protected static final Logger log = LoggerFactory.getLogger(WebSorketServer.class.getSimpleName());
	public static void main(String[] args) {
		int port = 1111;
        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        
        log.info("正在启动websocket服务器");
        NioEventLoopGroup boss=new NioEventLoopGroup();
        NioEventLoopGroup work=new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap=new ServerBootstrap();
            bootstrap.group(boss,work);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<Channel>() {
            	@Override
            	protected void initChannel(Channel ch) throws Exception {
            		ch.pipeline().addLast("logging",new LoggingHandler("DEBUG"));
                    ch.pipeline().addLast("http-codec",new HttpServerCodec());
                    ch.pipeline().addLast(new WebSocketServerProtocolHandler("/info"));
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<TextWebSocketFrame>() {
						@Override
						protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
							String request = ((TextWebSocketFrame) msg).text();
							log.info("channelRead0: {}", request);
							TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString() + ctx.channel().id() + "：" + request);
							ctx.channel().writeAndFlush(tws);
						}
					});
            	}
            });
            Channel channel = bootstrap.bind(port).sync().channel();
            log.info("webSocket服务器启动成功："+channel);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.info("运行出错："+e);
        }finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
            log.info("websocket服务器已关闭");
        }
	}
}
