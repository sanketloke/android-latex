package com.androtex.files;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.androtex.files.interfaces.IUpdateFile;
import com.androtex.http.ActionURLS;
import com.androtex.http.LoadManagement;
import com.androtex.http.LoadURL;
import com.androtex.http.interfaces.IURLLoaded;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Class used to initiate a tex file modification on the server
 * 
 * @author Kevin Le Perf
 * 
 */
public class UpdateFile extends LoadManagement implements IURLLoaded {
	/**
	 * The user login
	 */
	private String _login;
	/**
	 * The user password encoded
	 */
	private String _pwd;

	/**
	 * The specified project
	 */
	private String _project;

	/**
	 * The file involved
	 */
	private String _file;

	/**
	 * _data encoded in Base64
	 */
	private String _data;

	/**
	 * The parent we will alert
	 */
	private IUpdateFile _parent;

	private int _con;

	/**
	 * used internaly if we had an http error
	 */
	private boolean _error = false;

	/**
	 * 
	 * @param context
	 *            the activity or programme context
	 * @param parent
	 *            the parent
	 * @param login
	 *            the login info
	 * @param pwd
	 *            the password infos
	 * @param project
	 *            the project name
	 * @param file
	 *            the file name
	 * @param data
	 *            the data we want to update
	 */
	public UpdateFile(Context context, IUpdateFile parent, String login,
			String pwd, String project, String file, String data) {
		super(context);
		_project = project;
		_login = login;
		_pwd = pwd;
		_file = file;
		_parent = parent;
		_con = 0;
		_data = data;
	}

	/**
	 * compute Call the server to update the file
	 */
	public void compute() {
		begin();

		LoadURL _url = new LoadURL(this, 1, getUrl()
				+ ActionURLS.getUpdateFileURL(_login, _pwd, _project, _file),
				null);

		String _headers[] = { "Host", getServer()+":"+getPort(), 
				"Referer", getProtocol()+"://" + getServer()};
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("data", _data));

		_url.loadPostUrl(_headers, nvps, false);
	}

	/**
	 * loadEnd
	 * 
	 * @param serial
	 *            the serial of the page we successfully loaded
	 * @param text
	 *            the text we obtained
	 * @param progress
	 *            a reference to a potential progressbar
	 */
	public void loadEnd(int serial, String text, ProgressDialog progress) {
		end();

		// if we have an error we call the parent with the "log" error
		if (_error) {
			try {
				if (progress != null)
					progress.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}

			_parent.onUpdateFileFailure(_project, _file, "log",null);
			return;
		}

		// else

		JSONObject _objet;
		try {
			_objet = new JSONObject(text);
			// has the text successfully been saved ?
			if (_objet.has("updated")) {
				_parent.onUpdateFileSuccess(_project, _file, "ok?",
						_objet.optString("version"));
			} else {
				_parent.onUpdateFileFailure(_project, _file, "error?",
						_objet.optString("version"));
			}
		} catch (JSONException e) {
			// error lol
			_parent.onUpdateFileFailure(_project, _file, text, null);
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

	/**
	 * @deprecated
	 */
	public void loadMove(int serial, String text) {
	}

}
