package com.androtex.projects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.androtex.R;
import com.androtex.http.ActionURLS;
import com.androtex.http.LoadManagement;
import com.androtex.http.LoadURL;
import com.androtex.http.LoaderElement;
import com.androtex.http.interfaces.IURLLoaded;
import com.androtex.projects.interfaces.IDeleteProject;

/**
 * Class used to retrieve an user's project list
 * 
 * @author Kevin Le Perf
 * @see LoaderElement
 * @see IURLLoaded
 * 
 */
public class DeleteProject extends LoadManagement implements IURLLoaded {
	private int _con;
	private String _login;
	private String _pwd;
	private IDeleteProject _parent;
	private Context _context;
	private String _name;
	private boolean _error = false;

	public DeleteProject(Context context, IDeleteProject parent, String login,
			String pwd, String name) {
		super(context);
		_context = context;
		_login = login;
		_pwd = pwd;
		_parent = parent;
		_con = 0;

		_name = name;
	}

	public void compute() {
		begin();

		LoadURL _url = new LoadURL(this, 1, getUrl()
				+ ActionURLS.getDeleteProjectURL(_login, _pwd, _name), null);
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

			_parent.onDeleteProjectFailure("log", null);
			return;
		}
		JSONObject _objet;
		try {
			_objet = new JSONObject(text);
			if (_objet.has("deleted")
					&& "ok".equals(_objet.optString("deleted"))
					&& _objet.has("projets")) {
				Log.d("infos", text);
				JSONArray _array = _objet.getJSONArray("projets");
				String[] res = new String[_array.length()];
				for (int i = 0; i < res.length; i++) {
					res[i] = _array.optString(i);
				}
				_parent.onDeleteProjectSuccess(res, _objet.optString("version"));
			} else {
				_parent.onDeleteProjectFailure(
						_context.getString(R.string.projectdeleteerror),
						_objet.optString("version"));
			}
		} catch (JSONException e) {
			_parent.onDeleteProjectFailure("segfault " + text, null);
			e.printStackTrace();
		}

	}

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
