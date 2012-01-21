package com.androtex.actionbar;

import com.androtex.R;

import android.os.Build;
import android.support.v4.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ActionBar.Tab;

public class TuneActionBar {
	private final boolean OK=Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB;
	private Tab _server_version;
	private String _server_text;
	private ActionBar _action_bar;
	private FragmentActivity _activity;

	public TuneActionBar(FragmentActivity activity){
		_activity = activity;

		_server_text = _activity.getString(R.string.server);
		_action_bar = null;
		try{
			_action_bar =  _activity.getSupportActionBar();
			if(OK)
				_action_bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			_action_bar.setDisplayShowTitleEnabled(false);

		}catch(Exception e){

		}
		createServerVersion();
		attachServerVersion();
	}

	public void attachServerVersion(){
		if(OK && _action_bar != null)
			_action_bar.addTab(_server_version,0);
	}

	public void createServerVersion(){
		if(OK && _action_bar != null){
			_server_version = _action_bar.newTab();
			_server_version.setIcon(R.drawable.server_version);
			_server_version.setText(_server_text);
			_server_version.setTabListener(new TabListenerVoid(null));
		}
	}

	public final String getVersion(){
		return _server_text;
	}

	public void refreshServerVersion(final String version){
		if(OK && _action_bar != null){
			if(version != null){
				_activity.runOnUiThread(new Thread(){
					public void run(){
						_server_text = version;
						_server_version.setText(_server_text);
						_action_bar.removeTab(_server_version);
						attachServerVersion();				
					}
				});
			}
		}
	}
}
