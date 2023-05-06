package com.rplees.iproxy.intercept.func.auth;

import java.util.Base64;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0
 * @created Apr 3, 2023 10:27:12 PM
 */
public interface AuthProvider<R extends AuthToken> {
	String authType();

	String authRealm();

	R authenticate(String authorization);

	default R authenticate(HttpRequest request) {
		return authenticate(request.headers().get(HttpHeaderNames.PROXY_AUTHORIZATION));
	}

	default boolean matches(HttpRequest request) {
		return true;
	}

	public abstract class DefaultAuthProvider implements AuthProvider<AuthToken> {

		public static final String AUTH_TYPE_BASIC = "Basic";
		public static final String AUTH_REALM_BASIC = "Access to the staging site";

		public String authType() {
			return AUTH_TYPE_BASIC;
		}

		public String authRealm() {
			return AUTH_REALM_BASIC;
		}

		protected abstract BasicAuthToken authenticate(String usr, String pwd);

		public BasicAuthToken authenticate(String authorization) {
			String usr = "";
			String pwd = "";
			if (authorization != null && authorization.length() > 0) {
				String token = authorization.substring(AUTH_TYPE_BASIC.length() + 1);
				String decode = new String(Base64.getDecoder().decode(token));
				String[] arr = decode.split(":");
				if (arr.length >= 1) {
					usr = arr[0];
				}
				if (arr.length >= 2) {
					pwd = arr[1];
				}
			}
			return authenticate(usr, pwd);
		}

	}
}
