package com.rplees.iproxy.remote.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.proto.ParamContext;
import com.rplees.iproxy.proto.Proto;
import com.rplees.iproxy.utils.ChannelUtils;

import io.netty.channel.Channel;

/**
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 8, 2023 2:52:50 PM
 */
public final class ChannelBridgeCollector {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	Map<Channel, Map<String, ChannelBridge>> all = new ConcurrentHashMap<>();
	static ChannelBridgeCollector instance = new ChannelBridgeCollector();
	
	private ChannelBridgeCollector() {
	}
	
	public static ChannelBridgeCollector instance() {
		return instance;
	}
	
	public void stat() {
		if(all.size() == 0) {
			return;
		}
		
		for(Map.Entry<Channel, Map<String, ChannelBridge>> entry : all.entrySet()) {
			log.debug("CLIENT [{}], REMOTE NUM: [{}]", entry.getKey(), entry.getValue().size());
		}
	}
	
	public void clientClose(Channel localChannel) {
		log.debug("@@@@@CLIENT主动退出:" + localChannel);
		clientClose0(localChannel);
	}
	
	public void clientClose0(Channel localChannel) {
		ChannelUtils.channelClose(localChannel);
		
		Channel key = localChannel;
		if(all.containsKey(key)) {
			for(Map.Entry<String, ChannelBridge> entry : all.get(key).entrySet()) {
				ChannelUtils.channelClose(entry.getValue().remoteChannel);
			}
		}
		
		all.remove(key);
	}
	
	public void clientClose(ChannelBridge bridge) {
		log.debug("ChannelBridge>>CLIENT主动退出:" + bridge.localChannel +", REMOTE:" + bridge.remoteChannel);
		if(! all.containsKey(bridge.localChannel)) {
//			log.debug("clientClose>>>不包含");
		}
		
		clientClose0(bridge.localChannel);
	}
	
	public void removeClose(ChannelBridge bridge) {
		log.debug("ChannelBridge>>REMOTE主动退出:" + bridge.localChannel +", REMOTE:" + bridge.remoteChannel);
		ChannelUtils.channelClose(bridge.remoteChannel);
		
		Channel key = bridge.localChannel;
		boolean allRemove = false;
		if(! all.containsKey(key)) {
//			log.debug("removeClose>>不包含");
		} else {
			Map<String, ChannelBridge> map = all.get(key);
			ChannelBridge remove = map.remove(key(bridge));
			if(remove != null) {
				allRemove = map.size() == 0;
			}
		}
		
		if(allRemove) {
			ChannelUtils.channelClose(bridge.localChannel);
//			log.debug("删除Client: {}", key);
			all.remove(key);
		}
	}
	
	public String key(ChannelBridge bridge) {
		Proto remote = bridge.paramCtx.router().remote();
		return remote.toString();
	}
	
	public ChannelBridge findBridge(Channel localChannel, ParamContext paramCtx, RuntimeOption option) {
		Map<String, ChannelBridge> map = null;
		Channel key = localChannel;
		if(all.containsKey(key)) {
			map = all.get(key);
		} else {
			map = new ConcurrentHashMap<>();
			all.put(key, map);
		}
		
		String childKey = paramCtx.router().remote().toString();
		if(! map.containsKey(childKey)) {
//			log.debug("{} 注册: {}", all.size(), childKey);
			map.put(childKey, new ChannelBridge(paramCtx, option));
		}
		
		return map.get(childKey);
	}
}