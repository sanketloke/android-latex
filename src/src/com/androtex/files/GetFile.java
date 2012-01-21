package com.androtex.files;

import java.io.UnsupportedEncodingException;

import com.androtex.files.interfaces.IGetFile;
import com.androtex.http.ActionURLS;
import com.androtex.http.LoadManagement;
import com.androtex.http.LoadURL;
import com.androtex.http.interfaces.IURLLoaded;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Base64;
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
public class GetFile extends LoadManagement implements IURLLoaded {
	private String _login;
	private String _pwd;
	private String _project;
	private String _file;
	private IGetFile _parent;
	private Context _context;
	private int _con;

	public GetFile(Context context, IGetFile parent, String login, String pwd,
			String project, String file) {
		super(context);
		_login = login;
		_pwd = pwd;
		_parent = parent;
		_file = file;
		_context = context;
		_con = 0;
		_project = project;
	}

	public void compute() {
		begin();

		LoadURL _url = new LoadURL(this, 1, getUrl()
				+ ActionURLS.getExploreFileURL(_login, _pwd, _project, _file),
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

			_parent.onFileFailure("log");
			return;
		}

		if (text.length() == 0)
			if ("tex".equals(_file.substring(_file.length() - 3)))
				_parent.onTexFileSuccess(_file, "");
			else
				_parent.onFileFailure(_context.getString(R.string.errorencoding));

		// _parent.onFileFailure("wrong password or login? missing file?...");

		byte[] res = Base64.decode(text, 0);
		if ("tex".equals(_file.substring(_file.length() - 3))) {
			try {
				_parent.onTexFileSuccess(_file, new String(res, "UTF8"));
			} catch (UnsupportedEncodingException e) {
				_parent.onFileFailure(_context.getString(R.string.errorencoding));
				e.printStackTrace();
			}
		} else {
			_parent.onImageFileSuccess(_file, res);
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
