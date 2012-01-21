package com.androtex.http.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

public class Connexion {
	private static DefaultHttpClient _client;
	private static boolean _erreur = true;
	final static int MAX_TOTAL_CONNECTIONS = 50;

	private static boolean initLog() {
		_client = HttpUtils.getNewHttpClient();// new DefaultHttpClient(manager,
												// parameters);

		_client.addResponseInterceptor(new HttpResponseInterceptor() {

			public void process(final HttpResponse response,
					final HttpContext context) throws HttpException,
					IOException {
				HttpEntity entity = response.getEntity();
				Header ceheader = entity.getContentEncoding();
				if (ceheader != null) {
					HeaderElement[] codecs = ceheader.getElements();
					for (int i = 0; i < codecs.length; i++) {
						if (codecs[i].getName().equalsIgnoreCase("gzip")) {
							response.setEntity(new GzipDecompressingEntity(
									response.getEntity()));
							return;
						}
					}
				}
			}

		});

		_client.setRedirectHandler(new RedirectHandler() {
			public URI getLocationURI(HttpResponse response, HttpContext context)
					throws ProtocolException {
				return null;
			}

			public boolean isRedirectRequested(HttpResponse response,
					HttpContext context) {
				return false;
			}
		});
		/*
		 * _client.getParams().setParameter( ClientPNames.COOKIE_POLICY,
		 * CookiePolicy.NETSCAPE );
		 * 
		 * _client.getParams().setParameter( ClientPNames.COOKIE_POLICY,
		 * CookiePolicy.BROWSER_COMPATIBILITY);
		 */
		return _erreur;
	}

	static boolean compteCorrect() {
		return _erreur;
	}

	static public DefaultHttpClient getConnexion() {
		if (_client == null) {
			initLog();
		}
		return _client;
	}

	static class GzipDecompressingEntity extends HttpEntityWrapper {

		public GzipDecompressingEntity(final HttpEntity entity) {
			super(entity);
		}

		@Override
		public InputStream getContent() throws IOException,
				IllegalStateException {
			InputStream wrappedin = wrappedEntity.getContent();
			return new GZIPInputStream(wrappedin);
		}

		@Override
		public long getContentLength() {
			// length of ungzipped content is not known
			return -1;
		}

	}
}
