package com.androtex.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.androtex.http.interfaces.IURLLoaded;
import com.androtex.http.utils.Connexion;


import android.app.ProgressDialog;
import android.util.Log;

public class LoadURL {
	private String _resultat = "";
	private IURLLoaded _object;
	private int _serial;
	private String _url;
	private ProgressDialog _progress;
	private static ArrayList<String> _cookies = new ArrayList<String>();

	public LoadURL(IURLLoaded caller, int serial, String url,
			ProgressDialog progress) {
		_object = caller;
		_url = url;
		_serial = serial;
		_progress = progress;
	}

	public static void clearCookies() {
		if (_cookies != null)
			_cookies.clear();
	}

	public void loadGetUrl(final String[] _headers, final Boolean addCookie) {
		new Thread() {
			public void run() {
				HttpGet httppost = new HttpGet(_url);
				if (_headers != null) {
					for (int i = 0; i < _headers.length; i += 2)
						httppost.addHeader(_headers[i], _headers[i + 1]);
				}
				httppost.addHeader("Cookie", getCookies());

				if (addCookie)
					_resultat += "Cookies : " + getCookies() + "\n";

				// httppost.addHeader("Cookie",
				// "JSESSIONID=5A490F7D73B00F4F503BD99B648ADA37;__utma=176039418.2147291891.1310121444.1311533642.1311554643.47;__utmz=176039418.1310121444.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); rs123=4028cb972f24d54e012f3199730e01dc; rn124=ff5lah8d2sh9; lzxc124=\"QZJo/F0OIBID+NxLs7qwS/WpQiQ=\"; __utmc=176039418; _app_identifier4028cb972f24d54e012f3199730e01dc=true; __utmb=176039418.1.10.1311554643;hr123=\"pokeke@hotmail.fr\";lzxc123=\"dsqSpRFjSSs3UKBLgcCDZlJEVp0=\";rn123=ff5lah8d2sh9;merchant=false");
				try {
					HttpResponse response = Connexion.getConnexion().execute(
							httppost);
					Header[] _header = response.getAllHeaders();
					for (int i = 0; i < _header.length; i++) {
						if (_header[i].getName().indexOf("ookie") >= 0) {
							if (addCookie)
								_resultat += " " + _header[i].getValue() + "\n";
							_cookies.add(_header[i].getValue());
						}
					}
					int status = response.getStatusLine().getStatusCode();
					Log.d("status", " " + status);
					switch (status) {
					case HttpStatus.SC_OK:
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(response.getEntity()
										.getContent()));
						StringBuffer s = new StringBuffer("");
						String l = "";
						while ((l = reader.readLine()) != null) {
							s.append(l);
						}
						_resultat += s.toString();
						_object.loadSuccess(_serial, _resultat);
						break;
					case HttpStatus.SC_MOVED_TEMPORARILY:
						BufferedReader readermove = new BufferedReader(
								new InputStreamReader(response.getEntity()
										.getContent()));
						StringBuffer smove = new StringBuffer("");
						String lmove = "";
						while ((lmove = readermove.readLine()) != null) {
							smove.append(lmove);
						}
						_resultat += smove.toString();
						_object.loadMove(_serial, _resultat);
					case HttpStatus.SC_MOVED_PERMANENTLY:

					default:
						ByteArrayOutputStream ostream = new ByteArrayOutputStream();
						response.getEntity().writeTo(ostream);
						_resultat += ostream.toString();
						_object.loadFailure(_serial, _resultat);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				_object.loadEnd(_serial, _resultat, _progress);
			}
		}.start();
	}

	public String getCookies() {
		String cookie = "";
		int add = 0;
		for (int i = 0; i < _cookies.size(); i++) {
			if (_cookies.get(i).indexOf("Path") < 0
					|| (_cookies.get(i).indexOf("Path") >= 0 && _url
							.indexOf(_cookies.get(i).split("Path=")[1]
									.split(";")[0]) >= 0)) {
				cookie += (add > 0 ? "; " : "") + _cookies.get(i).split(";")[0];
				add++;
			}
		}
		return cookie;
	}

	public static String getCookiePub() {
		for (int i = 0; i < _cookies.size(); i++)
			if (_cookies.get(i).indexOf("Path=/pub") >= 0
					&& _cookies.get(i).indexOf("JSESSIONID") >= 0)
				return _cookies.get(i).split("=")[1].split(";")[0];
		return "";
	}

	public void loadPostUrl(final String[] _headers,
			final List<BasicNameValuePair> _list, final Boolean addCookie) {
		new Thread() {
			public void run() {
				HttpPost httppost = new HttpPost(_url);

				if (_headers != null) {
					for (int i = 0; i < _headers.length; i += 2)
						httppost.addHeader(_headers[i], _headers[i + 1]);
				}
				httppost.addHeader("Cookie", getCookies());

				if (addCookie)
					_resultat += "Cookies : " + getCookies() + "\n";

				try {
					UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(
							_list, HTTP.UTF_8);
					httppost.setEntity(p_entity);
					HttpResponse response = Connexion.getConnexion().execute(
							httppost);
					int status = response.getStatusLine().getStatusCode();
					Header[] _header = response.getAllHeaders();

					for (int i = 0; i < _header.length; i++) {
						if (_header[i].getName().indexOf("ookie") >= 0) {
							if (addCookie)
								_resultat += " " + _header[i].getValue() + "\n";
							_cookies.add(_header[i].getValue());
						}
					}

					if (status == HttpStatus.SC_OK) {
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(response.getEntity()
										.getContent()));
						StringBuffer s = new StringBuffer("");
						String l = "";
						while ((l = reader.readLine()) != null) {
							// Log.d(" ",l);
							s.append(l);
						}
						_resultat += s.toString();
						_object.loadSuccess(_serial, _resultat);
					} else {
						ByteArrayOutputStream ostream = new ByteArrayOutputStream();
						response.getEntity().writeTo(ostream);
						_resultat += ostream.toString();
						_object.loadFailure(_serial, _resultat);
					}
				} catch (Exception e) {
				}
				_object.loadEnd(_serial, _resultat, _progress);
			}
		}.start();
	}

}
