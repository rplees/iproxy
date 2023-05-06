package com.rplees.iproxy.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

public class ChannelUtils {
	
	public static void channelClose(Object... obj) {
		for (Object o : obj) {
			if(o == null) continue;
			
			if(o instanceof Channel) {
				close((Channel) o);
			} else if(o instanceof ChannelFuture) {
				close(((ChannelFuture) o).channel());
			} else if(o instanceof ChannelHandlerContext) {
				close(((ChannelHandlerContext) o).channel());
			}
		}
	}
	
	public static void close(Channel channel ) {
		if(channel.isOpen()) {
			channel.close();
		}
	}
}
