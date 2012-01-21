package com.androtex.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Class used to init connection param depending of the application shared
 * preferences
 * 
 * @author Kevin Le Perf
 * @see SharedPreferences
 * 
 */
public class LoaderElement {
	/**
	 * the server ip or host.domainname
	 */
	private String _server;

	/**
	 * the port we need to use
	 */
	private String _port;

	/**
	 * The API Location
	 */
	private String _page;
	/**
	 * The protocol used
	 */
	private boolean _protocol;
	
	/**
	 * the sharedpreferences object
	 */
	protected SharedPreferences pref;

	/**
	 * LoaderElement Intialize the loader with some bascis informations
	 * 
	 * @param context
	 *            the context we need to retrieve the basics informations
	 */
	protected LoaderElement(Context context) {
		pref = PreferenceManager.getDefaultSharedPreferences(context);

		_server = pref.getString("server", "94.125.160.65");
		_port = pref.getString("port", "8080");
		_protocol = pref.getBoolean("protocol", false);
		_page = pref.getString("fileaccess", "/index.php");
		if(_page.charAt(0)!='/'){
			_page="/"+_page;
		}
		while(_page.indexOf("//") >= 0)
			_page = _page.replace("//","/");
		
		_is_computing = false;
		_is_finish = false;
	}
	
	protected final String getUrl(){
		return ((_protocol == true) ? "https" : "http" )+
				"://"+_server+":"+_port+_page;
	}

	protected final String getServer(){
		return _server;
	}
	
	protected final String getPort(){
		return _port;
	}
	
	protected final String getProtocol(){
		return ((_protocol == true) ? "https" : "http" );
	}
	
	protected final String getPage(){
		return _page;
	}
	
	private boolean _is_computing;
	private boolean _is_finish;

	protected void begin() {
		_is_computing = true;
		_is_finish = false;
	}

	protected void end() {
		_is_computing = true;
		_is_finish = true;
	}

	public boolean isComplete() {
		return _is_computing && _is_finish;
	}
}
