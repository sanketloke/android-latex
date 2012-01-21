package com.androtex.files;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androtex.files.interfaces.IGetFileList;
import com.androtex.http.ActionURLS;
import com.androtex.http.LoadManagement;
import com.androtex.http.LoadURL;
import com.androtex.http.interfaces.IURLLoaded;

import android.app.ProgressDialog;
import android.content.Context;
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
public class GetFileList extends LoadManagement implements IURLLoaded {
	private String _login;
	private String _pwd;
	private String _project;
	private IGetFileList _parent;
	private Context _context;
	private int _con;

	public GetFileList(Context context, IGetFileList parent, String login,
			String pwd, String project) {
		super(context);
		_login = login;
		_pwd = pwd;
		_parent = parent;
		_context = context;
		_con = 0;
		_project = project;
	}

	public void compute() {
		begin();

		LoadURL _url = new LoadURL(this, 1, getUrl()
				+ ActionURLS.getExploreProjectURL(_login, _pwd, _project), null);

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

			_parent.onFileListFailure("log", null);
			return;
		}
		JSONObject _objet;
		try {
			_objet = new JSONObject(text);
			if (_objet.has("files")) {
				JSONArray _array = _objet.getJSONArray("files");
				String[] res = new String[_array.length()];
				for (int i = 0; i < res.length; i++) {
					res[i] = _array.optString(i);
				}
				_parent.onFileListSuccess(res, _objet.optString("version"));
			} else {
				_parent.onFileListFailure(_context.getString(R.string.wronglogin),
						_objet.optString("version"));
			}
		} catch (JSONException e) {
			_parent.onFileListFailure("segfault " + text, null);
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
