package com.imoxion.sensems.server.nio.ssl;

import com.imoxion.sensems.server.config.ImEmsConfig;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.RequiredArgsConstructor;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@RequiredArgsConstructor
public class ImSSLContext {
    public SslContext get(boolean bStartTls) throws GeneralSecurityException, IOException {
        String keyFile = ImEmsConfig.getInstance().getCertPath();
        String keyPass = ImEmsConfig.getInstance().getCertPass();
        String[] tlsProtocolArray = ImEmsConfig.getInstance().getTlsProtocolArray();
        if(tlsProtocolArray == null){
            tlsProtocolArray = new String[]{ "TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3" };
        }
        SslContext sslContext;

        // self signed (default domain)
        if( keyFile.equals("") || keyPass.equals("") ){
            SelfSignedCertificate ssc = new SelfSignedCertificate(ImEmsConfig.getInstance().getDefaultDomain());
            //SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslContext = SslContextBuilder
                    .forServer(ssc.certificate(), ssc.privateKey())
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .protocols(tlsProtocolArray)
                    //.protocols("TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3")
                    .startTls(bStartTls)
                    .build();
        } else {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try( InputStream in = new FileInputStream(keyFile) ) {
                keyStore.load(in, keyPass.toCharArray());
            }

            KeyManagerFactory keyManagerFactory =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyPass.toCharArray());

//            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            tmf.init(keyStore);
//
//            X509TrustManager defaultTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
//            SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);

            sslContext = SslContextBuilder
                    .forServer(keyManagerFactory)
                    //.trustManager(tm)
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .protocols(tlsProtocolArray)
                    //.protocols("TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3")
                    .startTls(bStartTls)
                    .build();

        }

        return sslContext;
    }

    private static class SavingTrustManager implements X509TrustManager {

        private final X509TrustManager tm;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        public boolean isClientTrusted(X509Certificate[] chain) {
            return true;
        }
        public boolean isServerTrusted(X509Certificate[] chain) {
            return true;
        }

        public X509Certificate[] getAcceptedIssuers() {
            // 수정함
            return new X509Certificate[0];
            //throw new UnsupportedOperationException();
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            //throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
//          this.chain = chain;
//          tm.checkServerTrusted(chain, authType);
        }
    }
}
