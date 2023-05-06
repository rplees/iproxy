package com.rplees.iproxy.intercept.func.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;

public interface ExceptionProvider {
	public static final ExceptionProvider DEFAULT_EXCEPTION_PROVIDER = new DefaultExceptionProvider();
			
	void client(Throwable cause);

	void client(Channel localChannel, Throwable cause);

	void remote(Channel localChannel, Channel remoteChannel, Throwable cause);

	public static class DefaultExceptionProvider implements ExceptionProvider {
		private static final Logger log = LoggerFactory.getLogger(DefaultExceptionProvider.class);
		
		@Override
		public void client(Throwable cause) {
			log.error(cause.getLocalizedMessage());
			cause.printStackTrace();
		}

		@Override
		public void client(Channel localChannel, Throwable cause) {
			log.error(cause.getLocalizedMessage());
			cause.printStackTrace();
		}

		@Override
		public void remote(Channel localChannel, Channel remoteChannel, Throwable cause) {
			log.error(cause.getLocalizedMessage());
//			cause.printStackTrace();
		}
	}
}
