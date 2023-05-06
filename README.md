<div align="center">
  <h1>IProxy</h1>
  <p>
  
[![maven](https://img.shields.io/maven-central/v/com.rplees/iproxy.svg)](https://search.maven.org/search?q=com.rplees)
[![license](https://img.shields.io/github/license/rplees/iproxy.svg)](https://opensource.org/licenses/MIT)

  </p>
  <p>

[English](/README.md) | [中文](/README_zh-CN.md)

  </p>
</div>

---

## Introduction

IProxy is a JAVA written HTTP proxy server library that supports HTTP, HTTPS, Websocket protocols, and supports MITM (Man-in-the-middle), which can capture and tamper with HTTP, HTTPS packet.

## Usage

```xml
<dependency>
    <groupId>com.rplees</groupId>
    <artifactId>iproxy</artifactId>
    <version>1.1.3</version>
</dependency>
```

## Demo

- Normal HTTP proxy

```java
new ServerStarter().start(1113);
```

- MITM HTTP proxy

The following is a demonstration of a MITM attack that modifies the response header and response body when visiting the Baidu homepage

Code：

```java
int port = 1113;
RuntimeOption option = RuntimeOption.builder()
		//不开启的话HTTPS不会被拦截，而是直接转发原始报文
		.ssl(true)
		.initializer(new DefaultEventInitializer() {
			@Override
			public void init(EventPipeline pipeline) {
				pipeline.addFirst(new FullHttpIntercept() {
					public boolean match(ParamContext paramCtx) {
						//在匹配到百度首页时插入js
						if(StringUtils.contains(paramCtx.router().local().host(), "www.baidu.com")) {
							return true;
						}
						return false;
					}
					
					@Override
					public void responseHandle(FullHttpResponse msg, ParamContext paramCtx, EventHandlerContext ctx) {
						System.out.println(msg.content().toString(CharsetUtil.UTF_8));
						msg.content().writeBytes("<script>alert('hello')</script>".getBytes());
					}
				});
			}
		})
		.build();
new ServerStarter(option)
	.startAsync(port);
```

> Note: When https support is enabled, you need to install the CA certificate (`src/resources/ca.crt`) to a trusted root certificate authority.

More demo code can be found in the test package.

## HTTPS support

The CA certificate (src/resources/ca.crt) from the project needs to be imported to a trusted root certificate authority.
You can use the CertDownIntercept interceptor to enable the web certificate download feature, visit http://serverIP:serverPort to access.

> Note 1: If the certificate installation on Android phones pops up the password stored in your credentials, just enter the lock screen password.
>
> Note 2: Android 7 and above, the system no longer trusts user-installed certificates, you need to root and use the
> cat ca.crt > $(openssl x509 -inform PEM -subject_hash_old -in ca.crt | head -1).0
> command generates the d1488b25.0 file, and then moves the file to the
> /system/etc/security/cacerts/
> And give 644 access.
>
> Note 3: In Android 7 and above, even if you add the certificate to the system certificate, this certificate does not work in chrome. The reason is that chrome will only trust certificates with validity less than 27 months from 2018 (https://www.entrustdatacard.com/blog/2018/february/chrome-requires-ct-after-april-2018). So you need to generate the certificate file yourself.

### Custom CA

Since the root certificate and private key attached to the project are public, they are only suitable for local development and debugging, please generate your own root certificate and private key when using in the official environment, otherwise there will be risks.

- running the main method of the`CertUtil` class

- use openssl

```sh
openssl genrsa -out ca.key 2048
openssl pkcs8 -topk8 -nocrypt -inform PEM -outform DER -in ca.key -out ca_private.der
openssl req -sha256 -new -x509 -days 365 -key ca.key -out ca.crt \
    -subj "/C=CN/ST=GD/L=HZ/O=lee/OU=study/CN=testRoot"
```

Copy `ca.crt` and `ca_private.der` to the project src/resources/ after generation, or implement the ICACertFactory interface to custom load the root certificate and private key.

## Authentication

Currently only basic authentication are supported.

- Basic

```java
// curl -i -x 127.0.0.1:1113 -U admin:123456 http://www.baidu.com
int port = 1113;
RuntimeOption option = RuntimeOption.builder()
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
```

- Custom

Customize authentication by implementing the `AuthProvider` interface.

### Authentication context

After the authorization, the token returned from the verification pass can be obtained in the pipeline.

```java
AuthToken token = AuthContext.getToken(clientChannel);
```

## Pre-proxy support

Pre-proxy can be set,support http,socks4,socks5 protocol.

```java
int port = 1113;
RuntimeOption option = RuntimeOption.builder()
		.ssl(true)
		.proxyConfig(new ProxyConfig(ProxyType.SOCKS5, "127.0.0.1", 1085))//使用socks5二级代理
		.build();
new ServerStarter(option)
	.startAsync(port);
```

## Thanks
- [Proxyee](https://github.com/monkeyWie/proxyee)
