package com.androtex.files;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.androtex.R;
import com.androtex.files.interfaces.IDeleteFile;
import com.androtex.http.ActionURLS;
import com.androtex.http.LoadManagement;
import com.androtex.http.LoadURL;
import com.androtex.http.LoaderElement;
import com.androtex.http.interfaces.IURLLoaded;

/**
 * Class used to retrieve an user's project list
 * 
 * @author Kevin Le Perf
 * @see LoaderElement
 * @see IURLLoaded
 * 
 */
public class DeleteFile extends LoadManagement implements IURLLoaded {
	private int _con;
	private String _login;
	private String _pwd;
	private IDeleteFile _parent;
	private Context _context;
	private String _project;
	private String _name;

	public DeleteFile(Context context, IDeleteFile parent, String login,
			String pwd, String project, String name) {
		super(context);
		_context = context;
		_login = login;
		_pwd = pwd;
		_parent = parent;
		_con = 0;

		_project = project;
		_name = name;
	}

	public void compute() {
		begin();

		LoadURL _url = new LoadURL(this, 1, getUrl()
				+ ActionURLS.getDeleteFileURL(_login, _pwd, _project, _name),
				null);
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

			_parent.onDeleteFileFailure("log", null);
			return;
		}
		JSONObject _objet;
		try {
			_objet = new JSONObject(text);
			Log.d("infos", text);
			if (_objet.has("deleted") && "ok".equals(_objet.optString("deleted"))
				&& _objet.has("files")) {
				JSONArray _array = _objet.getJSONArray("files");
				String[] res = new String[_array.length()];
				for (int i = 0; i < res.length; i++) {
					res[i] = _array.optString(i);
				}
				_parent.onDeleteFileSuccess(res, _name,
						_objet.optString("version"));
			} else {
				_parent.onDeleteFileFailure(_context.getString(R.string.filedeleteerror),
						_objet.optString("version"));
			}
		} catch (JSONException e) {
			_parent.onDeleteFileFailure("segfault " + text,null);
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
