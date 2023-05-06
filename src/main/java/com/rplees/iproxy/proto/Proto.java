package com.rplees.iproxy.proto;

import java.util.Objects;
import java.util.StringJoiner;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created Apr 25, 2023 5:30:26 PM
 */
public class Proto {
	ProtoType protoType;
	boolean decrypted;
	Encrypt encrypt;
	boolean proxy;
	String host;
	int port;
	boolean complete;
	boolean interested;

	protected Proto(ProtoBuilder<?, ?> b) {
		this.protoType = b.protoType;
		this.decrypted = b.decrypted;
		this.encrypt = b.encrypt;
		this.proxy = b.proxy;
		this.host = b.host;
		this.port = b.port;
		this.complete = b.complete;
		this.interested = b.interested;
	}

	public static ProtoBuilder<?, ?> builder() {
		return new ProtoBuilderImpl();
	}

	private static final class ProtoBuilderImpl extends ProtoBuilder<Proto, ProtoBuilderImpl> {
		private ProtoBuilderImpl() {
		}

		protected ProtoBuilderImpl self() {
			return this;
		}

		public Proto build() {
			return new Proto(this);
		}
	}

	public static abstract class ProtoBuilder<C extends Proto, B extends ProtoBuilder<C, B>> {
		private Proto.ProtoType protoType;

		private boolean decrypted;

		private Proto.Encrypt encrypt;

		private boolean proxy;

		private String host;

		private int port;

		private boolean complete;

		private boolean interested;

		protected abstract B self();

		public abstract C build();

		public B protoType(Proto.ProtoType protoType) {
			this.protoType = protoType;
			return self();
		}

		public B decrypted(boolean decrypted) {
			this.decrypted = decrypted;
			return self();
		}

		public B encrypt(Proto.Encrypt encrypt) {
			this.encrypt = encrypt;
			return self();
		}

		public B proxy(boolean proxy) {
			this.proxy = proxy;
			return self();
		}

		public B host(String host) {
			this.host = host;
			return self();
		}

		public B port(int port) {
			this.port = port;
			return self();
		}

		public B complete(boolean complete) {
			this.complete = complete;
			return self();
		}

		public B interested(boolean interested) {
			this.interested = interested;
			return self();
		}

		public String toString() {
			return "Proto.ProtoBuilder(protoType=" + this.protoType + ", decrypted=" + this.decrypted + ", encrypt="
					+ this.encrypt + ", proxy=" + this.proxy + ", host=" + this.host + ", port=" + this.port
					+ ", complete=" + this.complete + ", interested=" + this.interested + ")";
		}
	}

	public Proto() {
	}

	void assetComplete() {
		assert complete;
	}

	public ProtoType guessProtoType() {
		if (!interested) {
			return ProtoType.TUNNEL;
		}

		if (protoType == ProtoType.HTTP
				&& (encrypt == null || encrypt == Encrypt.NONE || (encrypt == Encrypt.SSL && decrypted))) {
			return ProtoType.HTTP;
		}

		if (protoType == ProtoType.WEBSORKET) {
			return ProtoType.WEBSORKET;
		}

		return ProtoType.TUNNEL;
	}

	public void complete(boolean ssl, boolean decrypted, boolean interested) {
		this.complete = true;

		this.encrypt = ssl ? Encrypt.SSL : Encrypt.NONE;
		this.decrypted = decrypted;
		this.interested = interested;
	}

	public boolean ssl() {
		assetComplete();
		return encrypt == Encrypt.SSL && decrypted;
	}

	public boolean proxy() {
		assetComplete();
		return proxy;
	}

	public String host() {
		assetComplete();
		return host;
	}

	public int port() {
		assetComplete();
		return port;
	}

	protected void copyTo(Proto proto) {
		proto.protoType = protoType;
		proto.host = host;
		proto.port = port;
		proto.encrypt = encrypt;
		proto.proxy = proxy;
		proto.decrypted = decrypted;
		proto.interested = interested;
		proto.complete = complete;
	}

	public Proto copy() {
		Proto proto = new Proto();
		copyTo(proto);
		return proto;
	}

	public boolean equalsTo(Proto that) {
		return port == that.port && encrypt == that.encrypt && host.equals(that.host) && proxy == that.proxy
				&& protoType.equals(that.protoType);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Proto that = (Proto) o;
		return equalsTo(that);
	}

	@Override
	public int hashCode() {
		return Objects.hash(protoType, encrypt, host, port, proxy);
	}

	public static enum Encrypt {
		NONE, SSL,
	}

	public static enum ProtoType {
		HTTP, WEBSORKET, RTSP, TUNNEL,
	}

	protected StringJoiner stringTo() {
		return new StringJoiner("|").add(String.valueOf(protoType))
				.add(String.valueOf(encrypt))
				.add(String.valueOf(decrypted))
				.add(host)
				.add(String.valueOf(port))
				.setEmptyValue(">>Empty<<");
	}

	@Override
	public String toString() {
		return stringTo().toString();
	}

	public static class HttpProto extends Proto {
		String uri;
		HttpMethod method;
		HttpRequest request;
		String playload;

		public HttpProto() {
		}

		protected HttpProto(HttpProtoBuilder<?, ?> b) {
			super(b);
			this.uri = b.uri;
			this.method = b.method;
			this.request = b.request;
			this.playload = b.playload;
		}

		public static HttpProtoBuilder<?, ?> builder() {
			return new HttpProtoBuilderImpl();
		}

		private static final class HttpProtoBuilderImpl extends HttpProtoBuilder<HttpProto, HttpProtoBuilderImpl> {
			private HttpProtoBuilderImpl() {
			}

			protected HttpProtoBuilderImpl self() {
				return this;
			}

			public Proto.HttpProto build() {
				return new Proto.HttpProto(this);
			}
		}

		public static abstract class HttpProtoBuilder<C extends HttpProto, B extends HttpProtoBuilder<C, B>>
				extends Proto.ProtoBuilder<C, B> {
			private String uri;

			private HttpMethod method;

			private HttpRequest request;

			private String playload;

			protected abstract B self();

			public abstract C build();

			public B uri(String uri) {
				this.uri = uri;
				return self();
			}

			public B method(HttpMethod method) {
				this.method = method;
				return self();
			}

			public B request(HttpRequest request) {
				this.request = request;
				return self();
			}

			public B playload(String playload) {
				this.playload = playload;
				return self();
			}

			public String toString() {
				return "Proto.HttpProto.HttpProtoBuilder(super=" + super.toString() + ", uri=" + this.uri + ", method="
						+ this.method + ", request=" + this.request + ", playload=" + this.playload + ")";
			}
		}

		public HttpProto playload(String playload) {
			this.playload = playload;
			return this;
		}

		public String playload() {
			return playload;
		}

		public HttpMethod method() {
			return method;
		}

		public HttpRequest request() {
			return request;
		}

		public void setUri(String uri) {
			assetComplete();
			this.uri = uri;
		}

		public String uri() {
			assetComplete();
			return uri;
		}

		public String wsUri() {
			String protocol = "ws";
			if (encrypt == Encrypt.SSL) {
				// SSL in use so use Secure WebSockets
				protocol = "wss";
			}
			return protocol + "://" + host + ":" + port + uri;
		}

		@Override
		public HttpProto copy() {
			HttpProto proto = new HttpProto();
			copyTo(proto);

			proto.uri = uri;
			proto.method = method;
			proto.request = request;
			proto.playload = playload;
			return proto;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			HttpProto that = (HttpProto) o;
			return equalsTo(that) && uri.equals(that.uri) && playload.equals(that.playload) && method == that.method;
		}

		@Override
		public String toString() {
			return stringTo().add(uri).add(String.valueOf(method)).toString();
		}
	}
}
