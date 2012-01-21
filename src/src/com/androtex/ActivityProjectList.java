package com.androtex;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.androtex.actionbar.TuneActionBar;
import com.androtex.files.DeleteFile;
import com.androtex.format.Valid;
import com.androtex.http.LoadManagement;
import com.androtex.pager.ProjectPagerAdapter;
import com.androtex.projects.CreateProject;
import com.androtex.projects.DeleteProject;
import com.androtex.projects.GetProjectList;
import com.androtex.projects.ListProjectAdapter;
import com.androtex.projects.ProjectArray;
import com.androtex.projects.RenameProject;
import com.androtex.projects.interfaces.ICreateProject;
import com.androtex.projects.interfaces.IDeleteProject;
import com.androtex.projects.interfaces.IProjectList;
import com.androtex.projects.interfaces.IRenameProject;
import com.androtex.user.MessagingService;
import com.viewpagerindicator.TitlePageIndicator;

public class ActivityProjectList extends FragmentActivity implements IProjectList,
ICreateProject, IRenameProject, IDeleteProject {
	private TuneActionBar _tune_action_bar;
	private ListView _liste;
	private Button _project_add;
	private EditText _project_name;
	private String _login;
	private String _password;

	/*private GetProjectList _project_list;
	private CreateProject _project_create;
	private DeleteProject _project_delete;
	private RenameProject _project_rename;
	private ProgressDialog _progress_create;*/
	private ProjectArray _project_array;

	private static LoadManagement _project_action;
	public static LoadManagement getProjectAction(){
		return _project_action;
	}
	public static void setProjectAction(LoadManagement action){
		_project_action = action;
	}

	public void computeAndCreateProgress(){
		_project_action.compute();
		createProjectProgress();
	}

	private static ProgressDialog _progress_create;

	private void createProjectProgress() {
		runOnUiThread(new Thread() {
			public void run() {
				if (_project_action != null && !_project_action.isComplete() &&
						_progress_create == null) {
					_progress_create = new ProgressDialog(
							ActivityProjectList.this);
					_progress_create.setMessage(ActivityProjectList.this
							.getText(R.string.waiting));
					_progress_create.setIndeterminate(true);
					_progress_create.setCancelable(false);
					// _progress.setMax(100000);
					_progress_create
					.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					_progress_create.show();
				}
			}
		});
	}

	private void stopProjectProgress() {
		runOnUiThread(new Thread() {
			public void run() {
				if (_progress_create != null && _progress_create.isShowing()) {
					// _project_create != null && _project_create.isComplete()){
					_progress_create.dismiss();
					_progress_create = null;
				}
			}
		});
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.projectlist);
		_tune_action_bar = new TuneActionBar(this);

		//_action_bar = getActionBar();
		//_action_bar.setDisplayShowTitleEnabled(false);

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.getString("login") != null
				&& extras.getString("password") != null) {
			if (extras.containsKey("version"))
				_tune_action_bar.refreshServerVersion(extras
						.getString("version"));

			_login = extras.getString("login");
			_password = extras.getString("password");

			ViewPager pager = (ViewPager)findViewById( R.id.viewpager );
			if(pager != null){
				ProjectPagerAdapter adapter = new ProjectPagerAdapter( this );
				TitlePageIndicator indicator =
					(TitlePageIndicator)findViewById( R.id.indicator );
				pager.setAdapter(adapter);
				indicator.setViewPager(pager);
			}else{
				setActivityProjectComponents((EditText) findViewById(R.projectlist.add),
						(Button) findViewById(R.projectlist.validadd));
				setActivityProjectComponents((ListView) findViewById(R.projectlist.list));
			}


			// createProjectProgress();
		}

		createProjectProgress();

	}

	public void setActivityProjectComponents(ListView list){
		_liste = list;

		if(_project_action == null){
			_project_action = new GetProjectList(this, this, _login, _password);
			_project_action.compute();
		}
	}

	public void setActivityProjectComponents(EditText name, Button add){
		_project_name = name;
		_project_add = add;


		_project_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				 * Intent intent = new Intent(ActivityMain.this,
				 * LatexEditorTestActivity.class); startActivity(intent);
				 */
				if (_project_action == null) {
					if (Valid.validString(_project_name.getText()
							.toString())) {
						_project_action = new CreateProject(
								ActivityProjectList.this,
								ActivityProjectList.this, _login,
								_password, _project_name.getText()
								.toString());
						_project_action.compute();
						createProjectProgress();
					} else {
						MessagingService.getInstance().makeMessage(
								ActivityProjectList.this,
								getString(R.string.patternwrong),
								MessagingService.MESSAGE_LONG);
					}
				}
			}
		});


	}
	@Override
	protected void onStart() {
		super.onStart();
		//_action_bar = this.getActionBar();
		//_action_bar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.idmenu.add_folder:
			runOnUiThread(new Thread() {
				public void run() {
					ViewPager pager = (ViewPager)findViewById( R.id.viewpager );
					if(pager != null){
						pager.setCurrentItem(0);
					}
					_project_name.requestFocus();
				}
			});
			return true;
		case R.idmenu.prefs:
			Intent i = new Intent(this, Preferences.class);
			startActivity(i);
			return true;
		case android.R.id.home:
			// app icon in Action Bar clicked; go home
			Intent intent = new Intent(this, ActivityMain.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("login", _login);
			intent.putExtra("password", _password);

			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		createProjectProgress();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopProjectProgress();
	}

	@Override
	public void onPause() {
		super.onPause();
		stopProjectProgress();
	}

	public final String getLogin() {
		return _login;
	}

	public final String getPassword() {
		return _password;
	}

	public void deleteProject(final String file) {
		AlertDialog.Builder alert = new AlertDialog.Builder(
				ActivityProjectList.this);

		alert.setTitle(ActivityProjectList.this.getText(R.string.projectdelete));

		alert.setMessage(ActivityProjectList.this
				.getText(R.string.projectdelete_message));

		alert.setPositiveButton(ActivityProjectList.this.getText(R.string.ok),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
				ActivityProjectList.this.runOnUiThread(new Thread(){
					public void run(){
						_project_action = new DeleteProject(ActivityProjectList.this,
								ActivityProjectList.this, _login, _password, file);
						_project_action.compute();
						createProjectProgress();
					}
				});
			}
		});

		alert.setNegativeButton(ActivityProjectList.this.getText(R.string.cancel),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});

		alert.show();
	}

	public void renameProject(final String project) {
		AlertDialog.Builder alert = new AlertDialog.Builder(
				ActivityProjectList.this);

		alert.setTitle(ActivityProjectList.this.getText(R.string.filename));

		alert.setMessage(ActivityProjectList.this
				.getText(R.string.filename_description));

		// Set an EditText view to get user input
		final EditText input = new EditText(ActivityProjectList.this);
		input.setText(project);
		alert.setView(input);

		alert.setPositiveButton(ActivityProjectList.this.getText(R.string.ok),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				if (value.length() < 1 || !Valid.validString(value)) {
					MessagingService.getInstance().makeMessage(
							ActivityProjectList.this,
							getString(R.string.patternwrong),
							MessagingService.MESSAGE_LONG);
					return;
				}

				_project_action = new RenameProject(
						ActivityProjectList.this,
						ActivityProjectList.this, getLogin(),
						getPassword(), project, value);
				_project_action.compute();
				createProjectProgress();
				dialog.dismiss();
			}
		});

		alert.setNegativeButton(
				ActivityProjectList.this.getText(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});

		alert.show();
	}

	@Override
	public void onProjectListSuccess(final String[] projects,
			final String version) {
		_tune_action_bar.refreshServerVersion(version);

		if (_project_action != null) {
			stopProjectProgress();
			_project_action = null;
		}

		_project_array = new ProjectArray(projects);
		this.runOnUiThread(new Thread() {
			public void run() {
				ListProjectAdapter _adapter = new ListProjectAdapter(
						ActivityProjectList.this, _project_array);
				_liste.setAdapter(_adapter);
			}
		});
	}

	@Override
	public void onProjectListFailure(final String text, final String version) {
		_tune_action_bar.refreshServerVersion(version);

		if (_project_action != null) {
			stopProjectProgress();
			_project_action = null;
		}

		String echec = ActivityProjectList.this.getText(
				R.string.projectlistfailure).toString()
				+ "\n" + text;
		MessagingService.getInstance().makeMessage(ActivityProjectList.this,
				echec, MessagingService.MESSAGE_LONG);
	}

	@Override
	public void onCreateProjectSuccess(final String[] projects,
			final String version) {
		_tune_action_bar.refreshServerVersion(version);

		if (_project_action != null) {
			stopProjectProgress();
			_project_action = null;
		}

		_project_array = new ProjectArray(projects);

		this.runOnUiThread(new Thread() {
			public void run() {
				ListProjectAdapter _adapter = new ListProjectAdapter(
						ActivityProjectList.this, _project_array);
				_liste.setAdapter(_adapter);
			}
		});
	}

	@Override
	public void onCreateProjectFailure(final String text, final String version) {
		_tune_action_bar.refreshServerVersion(version);

		if (_project_action != null) {
			stopProjectProgress();
			_project_action = null;
		}
		String echec = ActivityProjectList.this.getText(
				R.string.projectcreatefailure).toString()
				+ "\n" + text;
		MessagingService.getInstance().makeMessage(ActivityProjectList.this,
				echec, MessagingService.MESSAGE_LONG);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menuproject, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onDeleteProjectSuccess(final String[] projects,
			final String version) {
		_tune_action_bar.refreshServerVersion(version);

		if (_project_action != null) {
			stopProjectProgress();
			_project_action = null;
		}

		_project_array = new ProjectArray(projects);
		this.runOnUiThread(new Thread() {
			public void run() {
				ListProjectAdapter _adapter = new ListProjectAdapter(
						ActivityProjectList.this, _project_array);
				_liste.setAdapter(_adapter);
			}
		});
	}

	@Override
	public void onDeleteProjectFailure(final String text, final String version) {
		_tune_action_bar.refreshServerVersion(version);

		if (_project_action != null) {
			stopProjectProgress();
			_project_action = null;
		}

		String echec = ActivityProjectList.this.getText(
				R.string.projectdeleteerror).toString();
		MessagingService.getInstance().makeMessage(ActivityProjectList.this,
				echec, MessagingService.MESSAGE_LONG);
	}

	@Override
	public void onRenameProjectSuccess(final String[] projects,
			final String version) {
		_tune_action_bar.refreshServerVersion(version);

		if (_project_action != null) {
			stopProjectProgress();
			_project_action = null;
		}

		_project_array = new ProjectArray(projects);
		this.runOnUiThread(new Thread() {
			public void run() {
				ListProjectAdapter _adapter = new ListProjectAdapter(
						ActivityProjectList.this, _project_array);
				_liste.setAdapter(_adapter);
			}
		});
	}

	@Override
	public void onRenameProjectFailure(String text, String version) {
		_tune_action_bar.refreshServerVersion(version);

		if (_project_action != null) {
			stopProjectProgress();
			_project_action = null;
		}

		String echec = ActivityProjectList.this.getText(
				R.string.projectrenameerror).toString();
		MessagingService.getInstance().makeMessage(ActivityProjectList.this,
				echec, MessagingService.MESSAGE_LONG);
	}
}