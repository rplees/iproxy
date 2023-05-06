package com.rplees.iproxy.intercept.event.handler;

import com.rplees.iproxy.intercept.context.EventHandlerContext;
import com.rplees.iproxy.proto.ParamContext;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public interface TunnelEventHandler extends EventHandler {
	
	 /**
     * 代理端->远程端的请求ByteBuf消息事件
     * @param localChannel
     * @param byteBuf 消息
     * @param ctx
     * @throws Exception
     */
    void beforeRequest(Channel localChannel, ByteBuf msg, ParamContext paramCtx, EventHandlerContext ctx) throws Exception;
    
    /**
     * 代理端->客户端的返回ByteBuf消息事件
     * @param localChannel
     * @param byteBuf 消息
     * @param ctx
     * @throws Exception
     */
    void afterResponse(Channel localChannel, Channel remoteChannel, ByteBuf msg, ParamContext paramCtx, EventHandlerContext ctx)
			throws Exception;
}
