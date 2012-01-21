package com.androtex.files;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.androtex.R;
import com.androtex.files.interfaces.ICreateFile;
import com.androtex.http.ActionURLS;
import com.androtex.http.LoadManagement;
import com.androtex.http.LoadURL;
import com.androtex.http.interfaces.IURLLoaded;

/**
 * classe used to manage login
 * 
 * a first call is made to the login page from inmobi a second one to the
 * attemptlogin if an error occured > no cookie with *123 or *124 is created >
 * failure and finally to the dashboard > success
 * 
 * note : is this final call to the dashboard usefull?
 */
public class CreateFile extends LoadManagement implements IURLLoaded {
	private String _login;
	private String _pwd;
	private String _project;
	private String _file;
	private ICreateFile _parent;
	private Context _context;
	private int _con;

	public CreateFile(Context context, ICreateFile parent, String login,
			String pwd, String project, String file) {
		super(context);
		_project = project;
		_login = login;
		_pwd = pwd;
		_file = file;
		_parent = parent;
		_context = context;
		_con = 0;
	}

	public void compute() {
		begin();

		LoadURL _url = new LoadURL(this, 1, getUrl()
				+ ActionURLS.getCreateFileURL(_login, _pwd, _project, _file),
				null);
		//String _headers[] = { "Host", getServer(),
				// "User-Agent","Mozilla/5.0 (X11; Linux i686; rv:5.0) Gecko/20100101 Firefox/5.0",
				// "Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
				// "Accept-Language","fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3",
				// "Accept-Charset","ISO-8859-1,utf-8;q=0.7,*;q=0.7",
				// "Accept-Encoding","gzip, deflate",
				// "Connection","keep-alive",
				//"Referer", getProtocol()+"://" + getServer() };
		String _headers[] = { "Host", getServer()+":"+getPort(), 
				"Referer", getProtocol()+"://" + getServer()};
		_url.loadGetUrl(_headers, false);
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

			_parent.onCreateFileFailure("log", null);
			return;
		}
		JSONObject _objet;
		try {
			Log.d("infos", text);
			_objet = new JSONObject(text);
			if (_objet.has("files")) {
				JSONArray _array = _objet.getJSONArray("files");
				String[] res = new String[_array.length()];
				for (int i = 0; i < res.length; i++) {
					res[i] = _array.optString(i);
				}
				_parent.onCreateFileSuccess(res, 
						_objet.optString("version"));
			} else {
				_parent.onCreateFileFailure(_context.getString(R.string.createfilefailureexiste),
						_objet.optString("version"));
			}
		} catch (JSONException e) {
			_parent.onCreateFileFailure(text, null);
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
