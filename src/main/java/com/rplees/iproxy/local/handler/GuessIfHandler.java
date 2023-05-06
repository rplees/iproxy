package com.rplees.iproxy.local.handler;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.LongAdder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.crt.CertPool;
import com.rplees.iproxy.intercept.func.auth.AuthContext;
import com.rplees.iproxy.intercept.func.auth.AuthProvider;
import com.rplees.iproxy.intercept.func.auth.AuthToken;
import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.proto.Proto;
import com.rplees.iproxy.proto.ProtoUtil;
import com.rplees.iproxy.proto.Proto.HttpProto;
import com.rplees.iproxy.proto.Proto.ProtoType;
import com.rplees.iproxy.utils.ChannelUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.ReferenceCountUtil;

/**
 * 负责代理TLS/SSL握手识别
 * 	成功识别则解码器SSLHandler注入
 * 
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 9, 2023 10:57:08 AM
 */
public class GuessIfHandler extends ChannelInboundHandlerAdapter {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	static final int SSL_CONTENT_TYPE_HANDSHAKE = 22;
	RuntimeOption option;
	LongAdder adder = new LongAdder();
	
	boolean ssl = false;
	boolean decrypted = false;
	boolean interested = false;
	
	/**
	 * 准备握手
	 */
	boolean prepareHandshake = false;
	
	Proto proto;
	
	public GuessIfHandler(RuntimeOption option) {
		this.option = option;
    }
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		adder.increment();
		log.debug("第[{}]步, 消息类型: {}, CHANNEL: {}", adder.longValue(), msg.getClass(), ctx.channel());
		if(msg instanceof FullHttpRequest) { // #2
			HttpRequest request = (HttpRequest) msg;
			if(decrypted) { //握手后的
				//进一步确认
				ProtoUtil.fill(request, (HttpProto) proto);
				completeAndGoOn(ctx, msg);
				log.debug("握手后进一步确认: {}", proto);
			} else {
				proto = ProtoUtil.base(request);
				log.debug("协议Base Proto: {}", proto);
				boolean accept = option.getGlobalEventHandler().accept(ctx.channel(), proto);
				if(! accept) {
					log.debug("Not Acceptable");
					HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_ACCEPTABLE);
					ctx.channel().writeAndFlush(response);
					ChannelUtils.channelClose(ctx.channel());
					ReferenceCountUtil.release(msg);
					return;
				}
				
				if (option.getAuthProvider() != null) {
					AuthProvider<?> authProvider = option.getAuthProvider();
		            if (authProvider.matches(request)) {
		            	AuthToken authToken = authProvider.authenticate(request);
		            	if (authToken == null) {
		            		log.debug("Unauthorized");
		            		
		            		HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, RuntimeOption.UNAUTHORIZED);
		            		response.headers().set(HttpHeaderNames.PROXY_AUTHENTICATE, authProvider.authType() + " realm=\"" + authProvider.authRealm() + "\"");
		            		ctx.writeAndFlush(response);
		            		
		            		ChannelUtils.channelClose(ctx.channel());
							ReferenceCountUtil.release(msg);
		            		return;
		            	}
		            	
		            	AuthContext.setToken(ctx.channel(), authToken);
		            }
		        }
				
				interested = option.getGlobalEventHandler().interested(ctx.channel(), proto);
				if (HttpMethod.CONNECT.name().equalsIgnoreCase(request.method().name())) {
					log.debug("SSL CONNECT代理应答成功 ");
					HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, RuntimeOption.CONNECTION_ESTABLISHED);
					ctx.writeAndFlush(response);
					ReferenceCountUtil.release(msg);
					
					//移除Http解码器, 下一步握手准备
					ctx.pipeline().remove(RuntimeOption.HTTP_CODEC);
					ctx.pipeline().remove(RuntimeOption.AGGREGATOR_INTERNAL);
					prepareHandshake = true;
					return;
				} else {
					ProtoUtil.fill(request, (HttpProto) proto);
					completeAndGoOn(ctx, msg);
					
					log.debug("发现HTTP协议:{}", proto);
				}
			}
			
		} else if(msg instanceof ByteBuf) {
			ByteBuf byteBuf = (ByteBuf) msg;
			if(ProtoUtil.isHttp(byteBuf)) { // #1
				log.debug("发现HTTP协议");
				ctx.channel().pipeline().addFirst(RuntimeOption.HTTP_CODEC, option.httpServerCodec());
				ctx.channel().pipeline().addAfter(RuntimeOption.HTTP_CODEC, RuntimeOption.AGGREGATOR_INTERNAL, new HttpObjectAggregator(option.getMaxContentLength()));
				ctx.pipeline().fireChannelRead(msg);
				return;
			}
			
			if(prepareHandshake && byteBuf.getByte(0) == SSL_CONTENT_TYPE_HANDSHAKE) { //#3 SSL 握手阶段
				ssl = true;
				log.debug("SSL握手阶段");
				if(interested) {
					if (option.isSsl()) {
						decrypted = true;
						log.debug("设置SSL-decrypted已解码");
						int port = ((InetSocketAddress) ctx.channel().localAddress()).getPort();
						SslContext sslCtx = SslContextBuilder.forServer(option.getSslOption().getDynamicKeyPair().getPrivate(), CertPool.getCert(port, proto.host(), option.getSslOption())).build();
						ctx.pipeline().addFirst(RuntimeOption.SSL_HANDLER, sslCtx.newHandler(ctx.alloc()));
						
						ctx.channel().pipeline().addAfter(RuntimeOption.SSL_HANDLER, RuntimeOption.HTTP_CODEC, option.httpServerCodec());
						ctx.channel().pipeline().addAfter(RuntimeOption.HTTP_CODEC, RuntimeOption.AGGREGATOR_INTERNAL, new HttpObjectAggregator(option.getMaxContentLength()));
						
						ctx.pipeline().fireChannelRead(msg);
					} else {
						log.debug("SSL没开启");
						completeAndGoOn(ctx, msg);
					}
				} else {
					log.debug("标记本次不感兴趣~~~");
					completeAndGoOn(ctx, msg);
				}
			} else {
				throw new RuntimeException("识别失败~~~");
			}
		} else {
			throw new RuntimeException("未知消息信息:" + msg);
		}
	}
	
	protected void completeAndGoOn(ChannelHandlerContext ctx, Object msg) {
		assert proto != null;
		proto.complete(ssl, decrypted, interested);
		ctx.channel().attr(RuntimeOption.PROTO_ATTR_KEY).set(proto);
		
		//释放
		ctx.pipeline().remove(this);
		
		if(ctx.pipeline().get(RuntimeOption.HTTP_CODEC) != null) {
			ctx.pipeline().remove(RuntimeOption.HTTP_CODEC);
		}
		if(ctx.pipeline().get(RuntimeOption.AGGREGATOR_INTERNAL) != null) {
			ctx.pipeline().remove(RuntimeOption.AGGREGATOR_INTERNAL);
		}
		
		ProtoType protoType = proto.guessProtoType();
		if (protoType == ProtoType.HTTP) {
			ctx.pipeline().addLast(RuntimeOption.HTTP_CODEC, option.httpServerCodec());
			ctx.pipeline().addAfter(RuntimeOption.HTTP_CODEC, RuntimeOption.HTTP_HANDLER, new HttpHandler(option));
		} else if(protoType == ProtoType.WEBSORKET) {
			ctx.pipeline().addLast(RuntimeOption.HTTP_CODEC, option.httpServerCodec());
			ctx.pipeline().addAfter(RuntimeOption.HTTP_CODEC, RuntimeOption.AGGREGATOR_INTERNAL, new  HttpObjectAggregator(option.getMaxContentLength()));
			ctx.pipeline().addAfter(RuntimeOption.AGGREGATOR_INTERNAL, RuntimeOption.WEBSOCKET_PROTOCOL_HANDLER, new  WebSocketServerProtocolHandler(((HttpProto) proto).uri()));
			ctx.pipeline().addAfter(RuntimeOption.WEBSOCKET_PROTOCOL_HANDLER, RuntimeOption.WEBSOCKET_HANDLER, new WebSocketHandler(option));
		} else {
			ctx.pipeline().addLast(RuntimeOption.TUNNEL_HANDLER, new TunnelHandler(option));
		}
		
		//reset
		ssl = false;
		decrypted = false;
		interested = false;
		log.info("SSLGuessHandler Completed, Bye Bye, Guessed pipline's: {}", ctx.pipeline());
		
		Object reMsg = msg;
		if(protoType == ProtoType.TUNNEL && !(msg instanceof ByteBuf)) {
			EmbeddedChannel embeddedChannel = new EmbeddedChannel();
			embeddedChannel.pipeline().addFirst(new HttpRequestEncoder());
			embeddedChannel.writeOutbound(msg);
			
			ByteBuf byteBuf = embeddedChannel.readOutbound();
			reMsg = byteBuf;
		}
		
		ctx.pipeline().fireChannelRead(reMsg);
	}
}