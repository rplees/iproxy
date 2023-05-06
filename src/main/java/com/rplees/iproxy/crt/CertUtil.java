package com.rplees.iproxy.crt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.rplees.iproxy.crt.service.bc.BouncyCastleCertGenerator;

public class CertUtil {

    public static KeyPair genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048, new SecureRandom());
        return keyPairGen.genKeyPair();
    }
    
    public static X509Certificate loadCert(InputStream inputStream) throws CertificateException, IOException {
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			return (X509Certificate) cf.generateCertificate(inputStream);
		} finally {
			inputStream.close();
		}
    }
    
    public static PrivateKey loadPriKey(byte[] bts)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bts);
        return KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec);
    }
    
    /**
     * 从文件加载证书
     */
    public static X509Certificate loadCert(String path) throws Exception {
        return loadCert(new FileInputStream(path));
    }

    /**
     * 从文件加载证书
     */
    public static X509Certificate loadCert(URI uri) throws Exception {
        return loadCert(Paths.get(uri).toString());
    }


    /**
     * 读取ssl证书使用者信息
     */
    public static String getIssuer(X509Certificate cert) throws Exception {
    	return Stream.of(cert.getIssuerDN().toString().split(", "))
	    		.sorted((o1, o2) -> -1)
	    		.collect(Collectors.joining(", "));
    }

    public static void main(String[] args) throws Exception {
        KeyPair keyPair = genKeyPair();
        
        File caCertFile = new File("./ca.crt");
        if (caCertFile.exists()) {
            caCertFile.delete();
        }

        Files.write(Paths.get(caCertFile.toURI()),
        		BouncyCastleCertGenerator.generateCaCert(
                        "C=CN, ST=ZJ, L=HZ, O=Lee Sweet, OU=JustStudy, CN=IProxy",
                        new Date(),
                        new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365 * 100)),
                        keyPair)
                        .getEncoded());

        File caPriKeyFile = new File("./ca_private.der");
        if (caPriKeyFile.exists()) {
            caPriKeyFile.delete();
        }

        Files.write(caPriKeyFile.toPath(), new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded()).getEncoded());
    }
}
