package com.androtex.http.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

class CodlabSSLSocketFactory implements LayeredSocketFactory {

	private static final CodlabSSLSocketFactory DEFAULT_FACTORY = new CodlabSSLSocketFactory();

	public static CodlabSSLSocketFactory getSocketFactory() {
		return DEFAULT_FACTORY;
	}

	private SSLContext sslcontext;
	private javax.net.ssl.SSLSocketFactory socketfactory;

	private CodlabSSLSocketFactory() {
		super();
		TrustManager[] tm = new TrustManager[] { new X509TrustManager() {

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
				// do nothing
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
				// do nothing
			}

			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

		} };
		try {
			this.sslcontext = SSLContext.getInstance(SSLSocketFactory.TLS);
			this.sslcontext.init(null, tm, new SecureRandom());
			this.socketfactory = this.sslcontext.getSocketFactory();
		} catch (NoSuchAlgorithmException e) {
			Log.e("---", "Failed to instantiate TrustAllSSLSocketFactory!", e);
		} catch (KeyManagementException e) {
			Log.e("--", "Failed to instantiate TrustAllSSLSocketFactory!", e);
		}
	}

	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {
		SSLSocket sslSocket = (SSLSocket) this.socketfactory.createSocket(
				socket, host, port, autoClose);
		return sslSocket;
	}

	public Socket connectSocket(Socket sock, String host, int port,
			InetAddress localAddress, int localPort, HttpParams params)
			throws IOException, UnknownHostException, ConnectTimeoutException {
		if (host == null) {
			throw new IllegalArgumentException("Target host may not be null.");
		}
		if (params == null) {
			throw new IllegalArgumentException("Parameters may not be null.");
		}

		SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());

		if ((localAddress != null) || (localPort > 0)) {

			// we need to bind explicitly
			if (localPort < 0) {
				localPort = 0; // indicates "any"
			}

			InetSocketAddress isa = new InetSocketAddress(localAddress,
					localPort);
			sslsock.bind(isa);
		}

		int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
		int soTimeout = HttpConnectionParams.getSoTimeout(params);

		InetSocketAddress remoteAddress;
		remoteAddress = new InetSocketAddress(host, port);

		sslsock.connect(remoteAddress, connTimeout);

		sslsock.setSoTimeout(soTimeout);

		return sslsock;
	}

	public Socket createSocket() throws IOException {
		// the cast makes sure that the factory is working as expected
		return (SSLSocket) this.socketfactory.createSocket();
	}

	public boolean isSecure(Socket sock) throws IllegalArgumentException {
		return true;
	}
}
