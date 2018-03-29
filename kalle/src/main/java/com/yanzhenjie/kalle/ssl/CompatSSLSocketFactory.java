/*
 * Copyright © 2018 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.kalle.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * <p>Add CipherSuites to the lower version.</p>
 * Created by YanZhenjie on 2017/6/13.
 */
public class CompatSSLSocketFactory extends SSLSocketFactory {

    private static final String PROTOCOL_ARRAY[] = {"SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"};

    private static final X509TrustManager DEFAULT_TRUST_MANAGERS = new X509TrustManager() {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // Trust.
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // Trust.
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

    private static void setSupportProtocolAndCipherSuites(Socket socket) {
        if (socket instanceof SSLSocket)
            // https://developer.android.com/about/versions/android-5.0-changes.html#ssl
            ((SSLSocket) socket).setEnabledProtocols(PROTOCOL_ARRAY);
    }

    private SSLSocketFactory delegate;

    public CompatSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{DEFAULT_TRUST_MANAGERS}, new SecureRandom());
            delegate = sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new AssertionError(); // The system has no TLS. Just give up.
        }
    }

    public CompatSSLSocketFactory(SSLSocketFactory factory) {
        this.delegate = factory;
    }


    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        Socket ssl = delegate.createSocket(s, host, port, autoClose);
        setSupportProtocolAndCipherSuites(ssl);
        return ssl;
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        Socket ssl = delegate.createSocket(host, port);
        setSupportProtocolAndCipherSuites(ssl);
        return ssl;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        Socket ssl = delegate.createSocket(host, port, localHost, localPort);
        setSupportProtocolAndCipherSuites(ssl);
        return ssl;
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        Socket ssl = delegate.createSocket(host, port);
        setSupportProtocolAndCipherSuites(ssl);
        return ssl;
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        Socket ssl = delegate.createSocket(address, port, localAddress, localPort);
        setSupportProtocolAndCipherSuites(ssl);
        return ssl;
    }

    @Override
    public Socket createSocket() throws IOException {
        Socket ssl = delegate.createSocket();
        setSupportProtocolAndCipherSuites(ssl);
        return ssl;
    }
}
