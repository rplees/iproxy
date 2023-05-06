package com.rplees.iproxy.crt;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.WeakHashMap;

import com.rplees.iproxy.crt.service.bc.BouncyCastleCertGenerator;
import com.rplees.iproxy.local.RuntimeOption.SslOption;

public class CertPool {
	private static Map<Integer, Map<String, X509Certificate>> cache = new WeakHashMap<>();

	public static X509Certificate getCert(Integer port, String host, SslOption option) throws Exception {
		X509Certificate cert = null;
		if (host != null) {
			Map<String, X509Certificate> map = cache.get(port);
			if (map == null) {
				map = new WeakHashMap<>();
				cache.put(port, map);
			}
			
			String key = host.trim().toLowerCase();
			if (map.containsKey(key)) {
				cert = map.get(key);
			} else {
				KeyPair dynamicKeyPair = option.getDynamicKeyPair();
				cert = BouncyCastleCertGenerator.generateServerCert(option.getIssuer(), option.getNotBefore(), option.getNotAfter(), new KeyPair(dynamicKeyPair.getPublic(), option.getPriKey()), key);
				map.put(key, cert);
			}
		}
		
		return cert;
	}
}
