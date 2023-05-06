package com.rplees.iproxy.intercept.invoke;

import io.netty.channel.Channel;

/**
 * 执行器 
 *
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 8, 2023 1:15:29 PM
 */
public interface EventHandlerInvoker {
	EventHandlerInvoker fireBeforeConnect(Channel localChannel);
}
