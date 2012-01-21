package com.androtex;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.androtex.actionbar.TuneActionBar;
import com.androtex.format.Valid;
import com.androtex.http.LoadManagement;
import com.androtex.login.CreateLogin;
import com.androtex.login.Login;
import com.androtex.login.interfaces.ICreateLogin;
import com.androtex.login.interfaces.ILogin;
import com.androtex.pager.ConnectPagerAdapter;
import com.androtex.user.MessagingService;
import com.viewpagerindicator.TitlePageIndicator;

public class ActivityMain extends FragmentActivity implements ILogin, ICreateLogin {
	private EditText _login_login;
	private EditText _login_password;
	private Button _login;
	private EditText _login_new_login;
	private EditText _login_new_password;
	private Button _login_new;
	private TuneActionBar _tune_action_bar;

	public static LoadManagement _login_action;
	public static LoadManagement getLoginAction(){
		return _login_action;
	}
	public static void setLoginAction(LoadManagement _action){
		_login_action = _action;
	}
	public void computeAndCreateProgress(){
		_login_action.compute();
		createProgress();
	}
	private static ProgressDialog _progress;

	private String _str_login;
	private String _str_password;

	private void createProgress() {
		runOnUiThread(new Thread() {
			public void run() {
				if (_progress == null && _login_action != null && !_login_action.isComplete()) {
					_progress = new ProgressDialog(ActivityMain.this);
					_progress.setMessage(ActivityMain.this
							.getText(R.string.waiting));
					_progress.setIndeterminate(true);
					_progress.setCancelable(false);
					// _progress.setMax(100000);
					_progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					_progress.show();
				}
			}
		});
	}

	private void stopProgress() {
		runOnUiThread(new Thread() {
			public void run() {
				if (_progress != null && _progress.isShowing()) {
					// _login_action != null && _login_action.isComplete()){
					_progress.dismiss();
					_progress = null;
				}
			}
		});
	}

	public void onStart() {
		super.onStart();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		_tune_action_bar = new TuneActionBar(this);
		// remove the activity title to make space for tabs

		ViewPager pager = (ViewPager)findViewById( R.id.viewpager );
		if(pager != null){
			ConnectPagerAdapter adapter = new ConnectPagerAdapter( this );
			TitlePageIndicator indicator =
				(TitlePageIndicator)findViewById( R.id.indicator );
			pager.setAdapter(adapter);
			indicator.setViewPager(pager);

		}else{
			Log.d("NNNONONO","NNNONONO");
			_login_login = (EditText) findViewById(R.login.username);
			_login_password = (EditText) findViewById(R.login.password);
			_login = (Button) findViewById(R.login.valid);
			_login_new_login = (EditText) findViewById(R.loginnew.username);
			_login_new_password = (EditText) findViewById(R.loginnew.password);
			_login.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (_login_action == null) {
						if (Valid.validString(_login_login.getText().toString())
								&& Valid.validString(_login_password.getText()
										.toString())) {
							_login_action = new Login(ActivityMain.this,
									ActivityMain.this, _login_login.getText()
									.toString(), _login_password.getText()
									.toString());
							_login_action.compute();
							createProgress();
						} else {
							MessagingService.getInstance().makeMessage(
									ActivityMain.this,
									getString(R.string.patternwrong),
									MessagingService.MESSAGE_LONG);
						}
					}
				}
			});
			_login_new = (Button) findViewById(R.loginnew.valid);

			_login_new.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (_login_action == null) {
						if (Valid.validString(_login_new_login.getText().toString())
								&& Valid.validString(_login_new_password.getText()
										.toString())) {
							_login_action = new CreateLogin(ActivityMain.this,
									ActivityMain.this, _login_new_login.getText()
									.toString(), _login_new_password
									.getText().toString());
							_login_action.compute();
							createProgress();
						} else {
							MessagingService.getInstance().makeMessage(
									ActivityMain.this,
									getString(R.string.patternwrong),
									MessagingService.MESSAGE_LONG);
						}
					}
				}
			});

		}
		createProgress();

	}

	@Override
	public void onResume() {
		super.onResume();
		createProgress();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopProgress();
	}

	@Override
	public void onPause() {
		super.onPause();
		stopProgress();
	}

	@Override
	public void onRestoreInstanceState(Bundle restore) {
		if (restore.containsKey("version"))
			_tune_action_bar.refreshServerVersion(restore.getString("version"));
		super.onRestoreInstanceState(restore);
	}

	@Override
	public void onSaveInstanceState(Bundle save) {
		if (_tune_action_bar != null && _tune_action_bar.getVersion() != null)
			save.putString("version", _tune_action_bar.getVersion());
		super.onSaveInstanceState(save);
	}

	@Override
	public void onLoginSuccess(String text, String login, String password,
			final String version) {
		_tune_action_bar.refreshServerVersion(version);

		_str_login = login;
		_str_password = password;

		stopProgress();
		if (_login_action != null)
			_login_action = null;

		Intent intent = new Intent(ActivityMain.this, ActivityProjectList.class);
		intent.putExtra("login", _str_login);
		intent.putExtra("password", _str_password);

		startActivity(intent);
	}

	@Override
	public void onCreateLoginSuccess(String text, String login,
			String password, final String version) {
		_tune_action_bar.refreshServerVersion(version);

		_str_login = login;
		_str_password = password;
		stopProgress();
		if (_login_action != null)
			_login_action = null;
		Intent intent = new Intent(ActivityMain.this, ActivityProjectList.class);
		intent.putExtra("login", _str_login);
		intent.putExtra("password", _str_password);

		startActivity(intent);
	}

	@Override
	public void onLoginFailure(final String text, final String version) {
		_tune_action_bar.refreshServerVersion(version);
		stopProgress();
		if (_login_action != null)
			_login_action = null;

		String echec = ActivityMain.this.getText(R.string.failed).toString()
		+ "\n" + text;
		MessagingService.getInstance().makeMessage(ActivityMain.this, echec,
				MessagingService.MESSAGE_LONG);
	}

	@Override
	public void onCreateLoginFailure(final String text, final String version) {
		_tune_action_bar.refreshServerVersion(version);

		stopProgress();
		if (_login_action != null)
			_login_action = null;

		String echec = ActivityMain.this.getText(R.string.failed).toString()
		+ "\n" + text;
		MessagingService.getInstance().makeMessage(ActivityMain.this, echec,
				MessagingService.MESSAGE_LONG);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menumain, menu);
		Log.d("creation","crate");


		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.idmenu.prefs:
			Intent i = new Intent(this, Preferences.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}