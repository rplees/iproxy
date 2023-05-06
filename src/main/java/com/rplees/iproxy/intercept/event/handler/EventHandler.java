package com.rplees.iproxy.intercept.event.handler;

import com.rplees.iproxy.intercept.context.EventHandlerContext;

import io.netty.channel.Channel;

/***
 * localChannel -客户端
 * remoteChannel - 远程端
 * self - 代理端
 * 
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 4, 2023 10:14:18 AM
 */
public interface EventHandler {
    /**
     * 代理端->远程端准备发起连接前事件
     * @param localChannel
     * @param ctx
     * @throws Exception
     */
    void beforeConnect(Channel localChannel, EventHandlerContext ctx) throws Exception;
}
