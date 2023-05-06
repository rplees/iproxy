package com.rplees.iproxy.local.handler;

import com.rplees.iproxy.intercept.context.DefaultEventPipeline;
import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.proto.ParamContext;
import com.rplees.iproxy.proto.Proto;
import com.rplees.iproxy.proto.ProtoRouter;
import com.rplees.iproxy.remote.handler.ChannelBridgeCollector;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 6, 2023 10:55:09 AM
 */
public abstract class AbstractHandler extends ChannelDuplexHandler {
	
	DefaultEventPipeline pipeline;
	RuntimeOption option;
    
	public AbstractHandler(RuntimeOption option) {
		this.option = option;
		this.pipeline = new DefaultEventPipeline(option, option.getInitializer());
	}
	
	public ParamContext paramCtx(ChannelHandlerContext ctx) {
		Proto proto = ctx.channel().attr(RuntimeOption.PROTO_ATTR_KEY).get();
		return paramCtx(proto);
	}
	
	public ParamContext paramCtx(Proto proto) {
		ProtoRouter router = ProtoRouter.from(proto);
		ParamContext paramCtx = ParamContext.DefaultParamContext.from(router);
		return paramCtx;
	}
	
	@Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		ChannelBridgeCollector.instance().clientClose(ctx.channel());
		super.channelUnregistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	ChannelBridgeCollector.instance().clientClose(ctx.channel());
        option.getExceptionProvider().client(ctx.channel(), cause);
    }
}