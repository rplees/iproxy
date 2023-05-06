package com.rplees.iproxy.intercept.func.auth;

import com.rplees.iproxy.local.RuntimeOption;

import io.netty.channel.Channel;

/**
 * 
 *
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Apr 25, 2023 6:12:10 PM
 */
public class AuthContext {

    public static AuthToken getToken(Channel channel) {
    	return channel.attr(RuntimeOption.AUTH_TOKEN_ATTR_KEY).get();
    }

    public static void setToken(Channel channel, AuthToken token) {
    	channel.attr(RuntimeOption.AUTH_TOKEN_ATTR_KEY).set(token);
    }
}
