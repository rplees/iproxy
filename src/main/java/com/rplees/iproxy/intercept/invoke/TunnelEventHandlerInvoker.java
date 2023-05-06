package com.rplees.iproxy.intercept.invoke;

import com.rplees.iproxy.proto.ParamContext;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * 执行器 
 *
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 8, 2023 1:15:29 PM
 */
public interface TunnelEventHandlerInvoker {
	TunnelEventHandlerInvoker fireBeforeRequest(Channel localChannel, ByteBuf msg, ParamContext paramCtx);
	TunnelEventHandlerInvoker fireAfterResponse(Channel localChannel, Channel remoteChannel, ByteBuf msg, ParamContext paramCtx);
}
