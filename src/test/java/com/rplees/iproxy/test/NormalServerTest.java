package com.rplees.iproxy.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.local.ServerStarter;

/**
 * 
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Mar 2, 2023 12:44:10 PM
 */
public class NormalServerTest {
	private static final Logger log = LoggerFactory.getLogger(AcceptServerTest.class);
	
	public static void main(String[] args) {
		int port = 1113;
        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        
        long millis = System.currentTimeMillis();
        new ServerStarter().startAsync(port);
        log.info("启动代理服务器, 端口: {}, 耗时: {} ms", port, (System.currentTimeMillis() - millis));
	}
}
