package com.rplees.iproxy.remote.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.intercept.pipeline.EventPipeline;
import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.proto.Proto;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ReferenceCountUtil;

/**
 *
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 3, 2023 10:54:57 PM
 */
public class RemoteHttpHandlerInitializer extends ChannelInitializer<Channel> {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	protected ChannelBridge bridge;
	protected EventHandlerContext context;

    public RemoteHttpHandlerInitializer(ChannelBridge bridge, EventHandlerContext context) {
        this.bridge = bridge;
        this.context = context;
    }
    
    @Override
    protected void initChannel(Channel ch) throws Exception {
    	bridge.addProxyHandlerIf(ch);
        
        Proto proto = bridge.paramCtx().router().remote();
        RuntimeOption option = context.pipeline().option();
        
        if (proto.ssl()) {
        	ChannelHandler proxyHandler = ch.pipeline().get("_proxyHandler_");
        	if(proxyHandler != null) {
        		ch.pipeline().addAfter("_proxyHandler_", "_client_ssl_", option.getClientSslCtx().newHandler(ch.alloc(), proto.host(), proto.port()));
        	} else {
        		ch.pipeline().addFirst("_client_ssl_", option.getClientSslCtx().newHandler(ch.alloc(), proto.host(), proto.port()));
        	}
        }
        
        ch.pipeline().addLast(new LoggingHandler(">>>REMOTE-HTTP<<<", LogLevel.TRACE));
        ch.pipeline().addLast(RuntimeOption.HTTP_CODEC, option.httpClientCodec());
        ch.pipeline().addLast(new RemoteHttpReplayHandler(bridge, context));
    }
    
    private final class RemoteHttpReplayHandler extends AbstractRemoteHandler {
    	public RemoteHttpReplayHandler(ChannelBridge bridge, EventHandlerContext context) {
    		super(bridge, context);
    	}
    	
    	@Override
    	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    		if(ctx.channel().pipeline().get(RuntimeOption.READ_TIMEOUT) != null) {
    			ctx.channel().pipeline().remove(RuntimeOption.READ_TIMEOUT);
    		}
    		
    		EventPipeline pipeline = context.pipeline();
    		if (msg instanceof HttpResponse) {
    			DecoderResult decoderResult = ((HttpResponse) msg).decoderResult();
    			Throwable cause = decoderResult.cause();
    			if (cause != null) {
    				ReferenceCountUtil.release(msg);
    				exceptionCaught(ctx, cause);
    				return;
    			}
    			pipeline.fireAfterResponse(bridge.localChannel, ctx.channel(), (HttpResponse) msg, bridge.paramCtx());
    		} else if (msg instanceof HttpContent) {
    			pipeline.fireAfterResponse(bridge.localChannel, ctx.channel(), (HttpContent) msg, bridge.paramCtx());
    		} else {
    			log.warn("REMOTE-HTTP>>>>channelRead, msg type is ByteBuf.");
    			throw new RuntimeException("REMOTE-HTTP>>>>channelRead, msg type is ByteBuf.");
    		}
    	}
    }
}
