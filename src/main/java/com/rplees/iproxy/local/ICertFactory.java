package com.rplees.iproxy.local;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import com.rplees.iproxy.crt.CertUtil;

/**
 *
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0
 * @created Mar 3, 2023 10:56:33 PM
 */
public interface ICertFactory {
	public static final DefaultCertFactory DEFAULT_CERT_FACTORY = new DefaultCertFactory();
	
	X509Certificate cert() throws Exception;
	PrivateKey priKey() throws Exception;
	
	public static class DefaultCertFactory implements ICertFactory {

		@Override
		public X509Certificate cert() throws Exception {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("ca.crt");
			return CertUtil.loadCert(is);
		}

		@Override
		public PrivateKey priKey() throws Exception {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("ca_private.der");
			return CertUtil.loadPriKey(toByte(is));
		}
		
		public byte[] toByte(InputStream is) throws IOException {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] bts = new byte[1024];
			int len;
			while ((len = is.read(bts)) != -1) {
				os.write(bts, 0, len);
			}
			is.close();
			os.close();

			return os.toByteArray();
		}
	}
}
