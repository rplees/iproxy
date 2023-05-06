package com.rplees.iproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.local.ServerStarter;

/**
 *
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created May 6, 2023 12:35:01 PM
 */
public class ProxyLunch {
	private static final Logger log = LoggerFactory.getLogger(ProxyLunch.class);
	
    public static void main(String[] args) {
    	int port = 1113;
        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        
        long millis = System.currentTimeMillis();
        RuntimeOption option = RuntimeOption.builder()
        		.ssl(true)
        		.build();
        new ServerStarter(option)
        	.startAsync(port);
        log.info("启动代理服务器, 端口: {}, 耗时: {} ms", port, (System.currentTimeMillis() - millis));
    }
}
