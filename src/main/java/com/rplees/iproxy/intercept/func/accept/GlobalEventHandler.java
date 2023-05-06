package com.rplees.iproxy.intercept.func.accept;

import com.rplees.iproxy.proto.Proto;

import io.netty.channel.Channel;

public interface GlobalEventHandler {
    /**
     * 通道是否白名单
     * @param localChannel
     * @param proto
     * @return true-白名单
     */
    boolean accept(Channel localChannel, Proto proto);
    
    /**
     * 是否感兴趣
     * 
     * @param localChannel
     * @param proto
     * @return false-不感兴趣
     */
    boolean interested(Channel localChannel, Proto proto);
    
    public class DefaultGlobalEventHandler implements GlobalEventHandler {

		@Override
		public boolean accept(Channel localChannel, Proto proto) {
			return true;
		}

		@Override
		public boolean interested(Channel localChannel, Proto proto) {
			return true;
		}
    }
}