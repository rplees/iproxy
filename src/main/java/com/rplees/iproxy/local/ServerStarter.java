package com.rplees.iproxy.local;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.crt.CertUtil;
import com.rplees.iproxy.intercept.EventInitializer.DefaultEventInitializer;
import com.rplees.iproxy.local.RuntimeOption.SslOption;
import com.rplees.iproxy.local.handler.GuessIfHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * 
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0
 * @created Mar 2, 2023 12:50:59 PM
 */
public class ServerStarter {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	RuntimeOption option;
	
	public RuntimeOption option() {
		return option;
	}
	
	public ServerStarter() {
		this(new RuntimeOption());
	}
	
	public ServerStarter(RuntimeOption option) {
		Objects.requireNonNull(option);
		this.option = option;
	}
	
	private void init() {
		if (option.getExceptionProvider() == null) {
			option.setExceptionProvider(RuntimeOption.DEFAULT_EXCEPTION_PROVIDER);
		}
		
		if(option.getGlobalEventHandler() == null) {
			option.setGlobalEventHandler(RuntimeOption.DEFAULT_GLOBAL_EVENT_HANDLER);
		}
		
		if (option.getInitializer() == null) {
			option.setInitializer(new DefaultEventInitializer());
		}
		
		if(option.getCertFactory() == null) {
			option.setCertFactory(RuntimeOption.DEFAULT_CERT_FACTORY);
		}
		
        try {
            if (option.isSsl()) {
            	SslContextBuilder contextBuilder = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE);
            	if (option.getCiphers() != null) {
            		contextBuilder.ciphers(option.getCiphers());
            	}
            	option.setClientSslCtx(contextBuilder.build());
            	
                X509Certificate cert = option.getCertFactory().cert();
                PrivateKey priKey = option.getCertFactory().priKey();
                
                SslOption sslOption = new SslOption();
                sslOption.setIssuer(CertUtil.getIssuer(cert));
                sslOption.setNotBefore(cert.getNotBefore());
                sslOption.setNotAfter(cert.getNotAfter());
                sslOption.setPriKey(priKey);
                sslOption.setDynamicKeyPair(CertUtil.genKeyPair());
                option.setSslOption(sslOption);
            }
        } catch (Exception e) {
        	option.setSsl(false);
            log.error("SSL init fail.", e);
        }
	}

	public void start(int port) {
		start(null, port);
	}

	public void start(String ip, int port) {
		try {
			ChannelFuture channelFuture = doBind(ip, port).sync();
			if (channelFuture.cause() != null) {
				throw channelFuture.cause();
			}
			
			channelFuture.channel().closeFuture().sync();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			close();
		}
	}

	public CompletionStage<Void> startAsync(int port) {
		return startAsync(null, port);
	}

	public CompletionStage<Void> startAsync(String ip, int port) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		doBind(ip, port).addListener(f -> {
			if (f.isSuccess()) {
				future.complete(null);
				shutdownHook();
			} else {
				future.completeExceptionally(f.cause());
				close();
			}
		});

		return future;
	}

	private ChannelFuture doBind(String ip, int port) {
		init();
		
		option.bossGroup = new NioEventLoopGroup(option.getBossGroupThreads());
		option.workerGroup = new NioEventLoopGroup(option.getWorkerGroupThreads());
		option.remoteGroup = new NioEventLoopGroup(option.getRemoteGroupThreads());
		
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(option.bossGroup, option.workerGroup).channel(NioServerSocketChannel.class)
//	            .option(ChannelOption.SO_BACKLOG, 100)
//				.option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(32 * 1024, 64 * 1024))
				.handler(new LoggingHandler(">>>Boss Group<<<", LogLevel.TRACE))
				.childHandler(new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast(new LoggingHandler(">>>Worker Group<<<", LogLevel.TRACE));
						ch.pipeline().addLast(RuntimeOption.SSL_GUESS_HANDLER, new GuessIfHandler(option));
					}
				});

		return ip == null ? bootstrap.bind(port) : bootstrap.bind(ip, port);
	}

	/**
	 * 释放资源
	 */
	public void close() {
		close0(option.remoteGroup);
		close0(option.bossGroup);
		close0(option.workerGroup);
	}
	
	private void close0(EventLoopGroup loop) {
		if (!(loop.isShutdown() || loop.isShuttingDown())) {
			loop.shutdownGracefully();
		}
	}

	/**
	 * 注册JVM关闭的钩子以释放资源
	 */
	public void shutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(this::close, "Server Shutdown Thread"));
	}
}
