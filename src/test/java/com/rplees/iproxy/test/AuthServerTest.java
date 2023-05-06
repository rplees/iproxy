package com.rplees.iproxy.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.intercept.func.auth.AuthProvider;
import com.rplees.iproxy.intercept.func.auth.BasicAuthToken;
import com.rplees.iproxy.local.RuntimeOption;
import com.rplees.iproxy.local.ServerStarter;

public class AuthServerTest {
	private static final Logger log = LoggerFactory.getLogger(AuthServerTest.class);

    public static void main(String[] args) throws Exception {
    	int port = 1113;
        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        long millis = System.currentTimeMillis();
        RuntimeOption option = RuntimeOption.builder()
        		.ssl(true)
        		.authProvider(new AuthProvider.DefaultAuthProvider() {
					@Override
					protected BasicAuthToken authenticate(String usr, String pwd) {
						if ("admin".equals(usr) && "123456".equals(pwd)) {
		                    return new BasicAuthToken(usr, pwd);
		                }
						
		                return null;
					}
        		})
        		.build();
        new ServerStarter(option)
        	.startAsync(port);
        log.info("启动代理服务器, 端口: {}, 耗时: {} ms", port, (System.currentTimeMillis() - millis));
    }
}
