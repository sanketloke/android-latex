package com.androtex.files;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.androtex.files.interfaces.ICompileFile;
import com.androtex.http.ActionURLS;
import com.androtex.http.LoadManagement;
import com.androtex.http.LoadURL;
import com.androtex.http.interfaces.IURLLoaded;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import com.androtex.R;

/**
 * classe used to manage login
 * 
 * a first call is made to the login page from inmobi a second one to the
 * attemptlogin if an error occured > no cookie with *123 or *124 is created >
 * failure and finally to the dashboard > success
 * 
 * note : is this final call to the dashboard usefull?
 */
public class CompileFile extends LoadManagement implements IURLLoaded {

	private String _login;
	private String _pwd;
	private String _project;
	private String _file;
	/**
	 * _data encoded in Base64
	 */
	private String _data;
	private ICompileFile _parent;
	private Context _context;
	private int _con;

	public CompileFile(Context context, ICompileFile parent, String login,
			String pwd, String project, String file, String data) {
		super(context);
		_project = project;
		_login = login;
		_pwd = pwd;
		_file = file;
		_parent = parent;
		_context = context;
		_con = 0;
		_data = data;
	}

	public void compute() {
		begin();

		LoadURL _url = new LoadURL(this, 1, getUrl()
				+ ActionURLS.getCompileFileURL(_login, _pwd, _project, _file),
				null);

		//String _headers[] = { "Host", getServer(),
				// "User-Agent","Mozilla/5.0 (X11; Linux i686; rv:5.0) Gecko/20100101 Firefox/5.0",
				// "Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
				// "Accept-Language","fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3",
				// "Accept-Charset","ISO-8859-1,utf-8;q=0.7,*;q=0.7",
				// "Accept-Encoding","gzip, deflate",
				// "Connection","keep-alive",
				//"Referer", getProtocol()+"://"+getServer() };
		String _headers[] = { "Host", getServer()+":"+getPort(), 
				"Referer", getProtocol()+"://" + getServer()};
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("data", _data));

		_url.loadPostUrl(_headers, nvps, false);
	}

	public void loadEnd(int serial, String text, ProgressDialog progress) {
		end();

		if (_error) {
			try {
				if (progress != null)
					progress.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}

			_parent.onCompileFileFailure(_project, _file, "log", null);
			return;
		}
		JSONObject _objet;
		try {
			Log.d("infos", text);
			_objet = new JSONObject(text);
			if (_objet.has("pdf")) {
				byte[] _b = Base64.decode(_objet.optString("pdf"), 0);
				_parent.onCompileFileSuccess(_project, _file, _b, 
						_objet.optString("version"));
			} else {
				try {
					byte[] _b = Base64.decode(_objet.optString("dump"), 0);
					String str;
					str = new String(_b, "UTF8");
					_parent.onCompileFileFailure(_project, _file, str,
							_objet.optString("version"));
				} catch (Exception e) {
					e.printStackTrace();
					_parent.onCompileFileFailure(_project, _file, _context.getString(R.string.compileb64),
							null);
				}
			}
		} catch (JSONException e) {
			_parent.onCompileFileFailure(_project, _file, _context.getString(R.string.compilejson).toString() + text,
					null);
			e.printStackTrace();
		}

	}

	boolean _error = false;

	public void loadFailure(int serial, String text) {
		_error = true;
	}

	public void loadSuccess(int serial, String text) {
	}

	public int getConnectionNumber() {
		return _con;
	}

	public void loadMove(int serial, String text) {
	}

}
