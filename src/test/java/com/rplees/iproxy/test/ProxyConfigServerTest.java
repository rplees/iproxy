package com.rplees.iproxy.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.local.ServerStarter;
import com.rplees.iproxy.local.proxy.ProxyConfig;
import com.rplees.iproxy.local.proxy.ProxyConfig.ProxyType;

public class ProxyConfigServerTest {
	private static final Logger log = LoggerFactory.getLogger(ProxyConfigServerTest.class);

    public static void main(String[] args) throws Exception {
    	int port = 1113;
        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        long millis = System.currentTimeMillis();
        RuntimeOption option = RuntimeOption.builder()
        		.ssl(true)
        		.proxyConfig(new ProxyConfig(ProxyType.SOCKS5, "127.0.0.1", 1085))//使用socks5二级代理
        		.build();
        new ServerStarter(option)
        	.startAsync(port);
        log.info("启动代理服务器, 端口: {}, 耗时: {} ms", port, (System.currentTimeMillis() - millis));
    }
}
