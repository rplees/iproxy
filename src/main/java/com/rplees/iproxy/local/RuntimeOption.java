package com.rplees.iproxy.local;

import java.net.SocketAddress;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Date;

import com.rplees.iproxy.intercept.EventInitializer;
import com.rplees.iproxy.intercept.func.accept.GlobalEventHandler;
import com.rplees.iproxy.intercept.func.auth.AuthProvider;
import com.rplees.iproxy.intercept.func.auth.AuthToken;
import com.rplees.iproxy.intercept.func.exception.ExceptionProvider;
import com.rplees.iproxy.local.proxy.ProxyConfig;
import com.rplees.iproxy.proto.Proto;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.resolver.AddressResolverGroup;
import io.netty.util.AttributeKey;

/**
 * 
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created May 6, 2023 11:06:00 AM
 */
public class RuntimeOption {
	public static final int DEFAULT_MAX_INITIAL_LINE_LENGTH = 4096;
	public static final int DEFAULT_MAX_HEADER_SIZE = 8192;
	public static final int DEFAULT_MAX_CHUNK_SIZE = 8192;
	public static final int DEFAULT_MAX_CONTENT_LENGTH = 8192 * 1024;
	public static final AttributeKey<Proto> PROTO_ATTR_KEY = AttributeKey.valueOf(Proto.class, "__PROTO__");
	public static final AttributeKey<AuthToken> AUTH_TOKEN_ATTR_KEY = AttributeKey.valueOf(AuthToken.class, "__AUTH_TOKEN__");
	public static final ExceptionProvider DEFAULT_EXCEPTION_PROVIDER = (ExceptionProvider) new ExceptionProvider.DefaultExceptionProvider();
	public static final GlobalEventHandler.DefaultGlobalEventHandler DEFAULT_GLOBAL_EVENT_HANDLER = new GlobalEventHandler.DefaultGlobalEventHandler();
	public static final ICertFactory DEFAULT_CERT_FACTORY = ICertFactory.DEFAULT_CERT_FACTORY;
	public static final HttpResponseStatus CONNECTION_ESTABLISHED = new HttpResponseStatus(200, "Connection established");
	public static final HttpResponseStatus UNAUTHORIZED = new HttpResponseStatus(407, "Unauthorized");
	public static final String HTTP_CODEC = "httpCodec";
	public static final String SSL_HANDLER = "sslHandler";
	public static final String DECOMPRESS = "decompress";
	public static final String AGGREGATOR = "aggregator";
	public static final String AGGREGATOR_INTERNAL = "aggregatorInternal";
	public static final String READ_TIMEOUT = "readTimeout";
	public static final String SSL_GUESS_HANDLER = "sslGuessHandler";
	public static final String HTTP_HANDLER = "httpHandler";
	public static final String TUNNEL_HANDLER = "tunnelHandler";
	public static final String WEBSOCKET_HANDLER = "websocketHandler";
	public static final String WEBSOCKET_PROTOCOL_HANDLER = "webSocketProtocolHandler";

	EventLoopGroup bossGroup;
	EventLoopGroup workerGroup;
	EventInitializer initializer;
	EventLoopGroup remoteGroup;
	ProxyConfig proxyConfig;
	ICertFactory certFactory;
	SslContext clientSslCtx;
	ExceptionProvider exceptionProvider;
	GlobalEventHandler globalEventHandler;
	AuthProvider<?> authProvider;
	boolean ssl;
	SslOption sslOption;
	int bossGroupThreads;
	int workerGroupThreads;
	int remoteGroupThreads;
	int maxInitialLineLength = DEFAULT_MAX_INITIAL_LINE_LENGTH;
	int maxHeaderSize = DEFAULT_MAX_HEADER_SIZE;
	int maxChunkSize = DEFAULT_MAX_CHUNK_SIZE;
	int maxContentLength = DEFAULT_MAX_CONTENT_LENGTH;
	int readTimeout = 30;

	AddressResolverGroup<? extends SocketAddress> resolver;
	Iterable<String> ciphers;

	public static RuntimeOptionBuilder builder() {
		return new RuntimeOptionBuilder();
	}

	public void setBossGroup(EventLoopGroup bossGroup) {
		this.bossGroup = bossGroup;
	}

	public void setWorkerGroup(EventLoopGroup workerGroup) {
		this.workerGroup = workerGroup;
	}

	public void setInitializer(EventInitializer initializer) {
		this.initializer = initializer;
	}

	public void setRemoteGroup(EventLoopGroup remoteGroup) {
		this.remoteGroup = remoteGroup;
	}

	public void setProxyConfig(ProxyConfig proxyConfig) {
		this.proxyConfig = proxyConfig;
	}

	public void setCertFactory(ICertFactory certFactory) {
		this.certFactory = certFactory;
	}

	public void setClientSslCtx(SslContext clientSslCtx) {
		this.clientSslCtx = clientSslCtx;
	}

	public void setExceptionProvider(ExceptionProvider exceptionProvider) {
		this.exceptionProvider = exceptionProvider;
	}

	public void setGlobalEventHandler(GlobalEventHandler globalEventHandler) {
		this.globalEventHandler = globalEventHandler;
	}

	public void setAuthProvider(AuthProvider<?> authProvider) {
		this.authProvider = authProvider;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public void setSslOption(SslOption sslOption) {
		this.sslOption = sslOption;
	}

	public void setBossGroupThreads(int bossGroupThreads) {
		this.bossGroupThreads = bossGroupThreads;
	}

	public void setWorkerGroupThreads(int workerGroupThreads) {
		this.workerGroupThreads = workerGroupThreads;
	}

	public void setRemoteGroupThreads(int remoteGroupThreads) {
		this.remoteGroupThreads = remoteGroupThreads;
	}

	public void setMaxInitialLineLength(int maxInitialLineLength) {
		this.maxInitialLineLength = maxInitialLineLength;
	}

	public void setMaxHeaderSize(int maxHeaderSize) {
		this.maxHeaderSize = maxHeaderSize;
	}

	public void setMaxChunkSize(int maxChunkSize) {
		this.maxChunkSize = maxChunkSize;
	}

	public void setMaxContentLength(int maxContentLength) {
		this.maxContentLength = maxContentLength;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setResolver(AddressResolverGroup<? extends SocketAddress> resolver) {
		this.resolver = resolver;
	}

	public void setCiphers(Iterable<String> ciphers) {
		this.ciphers = ciphers;
	}

	public String toString() {
		return "RuntimeOption(bossGroup=" + getBossGroup() + ", workerGroup=" + getWorkerGroup() + ", initializer="
				+ getInitializer() + ", remoteGroup=" + getRemoteGroup() + ", proxyConfig=" + getProxyConfig()
				+ ", certFactory=" + getCertFactory() + ", clientSslCtx=" + getClientSslCtx() + ", exceptionProvider="
				+ getExceptionProvider() + ", globalEventHandler=" + getGlobalEventHandler() + ", authProvider="
				+ getAuthProvider() + ", ssl=" + isSsl() + ", sslOption=" + getSslOption() + ", bossGroupThreads="
				+ getBossGroupThreads() + ", workerGroupThreads=" + getWorkerGroupThreads() + ", remoteGroupThreads="
				+ getRemoteGroupThreads() + ", maxInitialLineLength=" + getMaxInitialLineLength() + ", maxHeaderSize="
				+ getMaxHeaderSize() + ", maxChunkSize=" + getMaxChunkSize() + ", maxContentLength="
				+ getMaxContentLength() + ", readTimeout=" + getReadTimeout() + ", resolver=" + getResolver()
				+ ", ciphers=" + getCiphers() + ")";
	}

	public EventLoopGroup getBossGroup() {
		return this.bossGroup;
	}

	public EventLoopGroup getWorkerGroup() {
		return this.workerGroup;
	}

	public EventInitializer getInitializer() {
		return this.initializer;
	}

	public EventLoopGroup getRemoteGroup() {
		return this.remoteGroup;
	}

	public ProxyConfig getProxyConfig() {
		return this.proxyConfig;
	}

	public ICertFactory getCertFactory() {
		return this.certFactory;
	}

	public SslContext getClientSslCtx() {
		return this.clientSslCtx;
	}

	public ExceptionProvider getExceptionProvider() {
		return this.exceptionProvider;
	}

	public GlobalEventHandler getGlobalEventHandler() {
		return this.globalEventHandler;
	}

	public AuthProvider<?> getAuthProvider() {
		return this.authProvider;
	}

	public boolean isSsl() {
		return this.ssl;
	}

	public SslOption getSslOption() {
		return this.sslOption;
	}

	public int getBossGroupThreads() {
		return this.bossGroupThreads;
	}

	public int getWorkerGroupThreads() {
		return this.workerGroupThreads;
	}

	public int getRemoteGroupThreads() {
		return this.remoteGroupThreads;
	}

	public int getMaxInitialLineLength() {
		return this.maxInitialLineLength;
	}

	public int getMaxHeaderSize() {
		return this.maxHeaderSize;
	}

	public int getMaxChunkSize() {
		return this.maxChunkSize;
	}

	public int getMaxContentLength() {
		return this.maxContentLength;
	}

	public int getReadTimeout() {
		return this.readTimeout;
	}

	public AddressResolverGroup<? extends SocketAddress> getResolver() {
		return this.resolver;
	}

	public Iterable<String> getCiphers() {
		return this.ciphers;
	}

	public HttpServerCodec httpServerCodec() {
		return new HttpServerCodec(getMaxInitialLineLength(), getMaxHeaderSize(), getMaxChunkSize());
	}

	public HttpClientCodec httpClientCodec() {
		return new HttpClientCodec(getMaxInitialLineLength(), getMaxHeaderSize(), getMaxChunkSize());
	}

	public static class RuntimeOptionBuilder {
		private EventLoopGroup bossGroup;

		private EventLoopGroup workerGroup;

		private EventInitializer initializer;

		private EventLoopGroup remoteGroup;

		private ProxyConfig proxyConfig;

		private ICertFactory certFactory;

		private SslContext clientSslCtx;

		private ExceptionProvider exceptionProvider;

		private GlobalEventHandler globalEventHandler;

		private AuthProvider<?> authProvider;

		private boolean ssl;

		private RuntimeOption.SslOption sslOption;

		private int bossGroupThreads;

		private int workerGroupThreads;

		private int remoteGroupThreads;

		private boolean maxInitialLineLength$set;

		private int maxInitialLineLength$value;

		private boolean maxHeaderSize$set;

		private int maxHeaderSize$value;

		private boolean maxChunkSize$set;

		private int maxChunkSize$value;

		private boolean maxContentLength$set;

		private int maxContentLength$value;

		private boolean readTimeout$set;

		private int readTimeout$value;

		private AddressResolverGroup<? extends SocketAddress> resolver;

		private Iterable<String> ciphers;

		public RuntimeOptionBuilder bossGroup(EventLoopGroup bossGroup) {
			this.bossGroup = bossGroup;
			return this;
		}

		public RuntimeOptionBuilder workerGroup(EventLoopGroup workerGroup) {
			this.workerGroup = workerGroup;
			return this;
		}

		public RuntimeOptionBuilder initializer(EventInitializer initializer) {
			this.initializer = initializer;
			return this;
		}

		public RuntimeOptionBuilder remoteGroup(EventLoopGroup remoteGroup) {
			this.remoteGroup = remoteGroup;
			return this;
		}

		public RuntimeOptionBuilder proxyConfig(ProxyConfig proxyConfig) {
			this.proxyConfig = proxyConfig;
			return this;
		}

		public RuntimeOptionBuilder certFactory(ICertFactory certFactory) {
			this.certFactory = certFactory;
			return this;
		}

		public RuntimeOptionBuilder clientSslCtx(SslContext clientSslCtx) {
			this.clientSslCtx = clientSslCtx;
			return this;
		}

		public RuntimeOptionBuilder exceptionProvider(ExceptionProvider exceptionProvider) {
			this.exceptionProvider = exceptionProvider;
			return this;
		}

		public RuntimeOptionBuilder globalEventHandler(GlobalEventHandler globalEventHandler) {
			this.globalEventHandler = globalEventHandler;
			return this;
		}

		public RuntimeOptionBuilder authProvider(AuthProvider<?> authProvider) {
			this.authProvider = authProvider;
			return this;
		}

		public RuntimeOptionBuilder ssl(boolean ssl) {
			this.ssl = ssl;
			return this;
		}

		public RuntimeOptionBuilder sslOption(RuntimeOption.SslOption sslOption) {
			this.sslOption = sslOption;
			return this;
		}

		public RuntimeOptionBuilder bossGroupThreads(int bossGroupThreads) {
			this.bossGroupThreads = bossGroupThreads;
			return this;
		}

		public RuntimeOptionBuilder workerGroupThreads(int workerGroupThreads) {
			this.workerGroupThreads = workerGroupThreads;
			return this;
		}

		public RuntimeOptionBuilder remoteGroupThreads(int remoteGroupThreads) {
			this.remoteGroupThreads = remoteGroupThreads;
			return this;
		}

		public RuntimeOptionBuilder maxInitialLineLength(int maxInitialLineLength) {
			this.maxInitialLineLength$value = maxInitialLineLength;
			this.maxInitialLineLength$set = true;
			return this;
		}

		public RuntimeOptionBuilder maxHeaderSize(int maxHeaderSize) {
			this.maxHeaderSize$value = maxHeaderSize;
			this.maxHeaderSize$set = true;
			return this;
		}

		public RuntimeOptionBuilder maxChunkSize(int maxChunkSize) {
			this.maxChunkSize$value = maxChunkSize;
			this.maxChunkSize$set = true;
			return this;
		}

		public RuntimeOptionBuilder maxContentLength(int maxContentLength) {
			this.maxContentLength$value = maxContentLength;
			this.maxContentLength$set = true;
			return this;
		}

		public RuntimeOptionBuilder readTimeout(int readTimeout) {
			this.readTimeout$value = readTimeout;
			this.readTimeout$set = true;
			return this;
		}

		public RuntimeOptionBuilder resolver(AddressResolverGroup<? extends SocketAddress> resolver) {
			this.resolver = resolver;
			return this;
		}

		public RuntimeOptionBuilder ciphers(Iterable<String> ciphers) {
			this.ciphers = ciphers;
			return this;
		}

		public RuntimeOption build() {
			RuntimeOption option = new RuntimeOption();
			option.bossGroup = this.bossGroup;
			option.workerGroup = this.workerGroup;
			option.initializer = this.initializer;
			option.remoteGroup = this.remoteGroup;
			option.proxyConfig = this.proxyConfig;
			option.certFactory = this.certFactory;
			option.clientSslCtx = this.clientSslCtx;
			option.exceptionProvider = this.exceptionProvider;
			option.globalEventHandler = this.globalEventHandler;
			option.authProvider = this.authProvider;
			option.ssl = this.ssl;
			option.sslOption = this.sslOption;
			option.bossGroupThreads = this.bossGroupThreads;
			option.workerGroupThreads = this.workerGroupThreads;
			option.remoteGroupThreads = this.remoteGroupThreads;
			option.resolver = this.resolver;
			option.ciphers = this.ciphers;
			
			if (this.maxInitialLineLength$set) {
				option.maxInitialLineLength = this.maxInitialLineLength$value;
			}
			
			if (this.maxHeaderSize$set) {
				option.maxHeaderSize = this.maxHeaderSize$value;
			}
			
			if (this.maxChunkSize$set) {
				option.maxChunkSize = this.maxChunkSize$value;
			}
			
			if (this.maxContentLength$set) {
				option.maxContentLength = this.maxContentLength$value;
			}
			
			if (this.readTimeout$set) {
				option.readTimeout = this.readTimeout$value;
			}
			
			return option;
		}
	}
	
	public static class SslOption {
		String issuer;

		Date notBefore;

		Date notAfter;

		PrivateKey priKey;

		KeyPair dynamicKeyPair;

		public void setIssuer(String issuer) {
			this.issuer = issuer;
		}

		public void setNotBefore(Date notBefore) {
			this.notBefore = notBefore;
		}

		public void setNotAfter(Date notAfter) {
			this.notAfter = notAfter;
		}

		public void setPriKey(PrivateKey priKey) {
			this.priKey = priKey;
		}

		public void setDynamicKeyPair(KeyPair dynamicKeyPair) {
			this.dynamicKeyPair = dynamicKeyPair;
		}

		public String toString() {
			return "RuntimeOption.SslOption(issuer=" + getIssuer() + ", notBefore=" + getNotBefore() + ", notAfter="
					+ getNotAfter() + ", priKey=" + getPriKey() + ", dynamicKeyPair=" + getDynamicKeyPair() + ")";
		}

		public String getIssuer() {
			return this.issuer;
		}

		public Date getNotBefore() {
			return this.notBefore;
		}

		public Date getNotAfter() {
			return this.notAfter;
		}

		public PrivateKey getPriKey() {
			return this.priKey;
		}

		public KeyPair getDynamicKeyPair() {
			return this.dynamicKeyPair;
		}
	}
}
