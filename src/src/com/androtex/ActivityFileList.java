package com.androtex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URLDecoder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.androtex.actionbar.TuneActionBar;
import com.androtex.files.CompileFile;
import com.androtex.files.CreateDataFile;
import com.androtex.files.CreateFile;
import com.androtex.files.DeleteFile;
import com.androtex.files.FileArray;
import com.androtex.files.GetFile;
import com.androtex.files.GetFileList;
import com.androtex.files.ListFileAdapter;
import com.androtex.files.RenameFile;
import com.androtex.files.UpdateFile;
import com.androtex.files.interfaces.ICompileFile;
import com.androtex.files.interfaces.ICreateFile;
import com.androtex.files.interfaces.IDeleteFile;
import com.androtex.files.interfaces.IGetFile;
import com.androtex.files.interfaces.IGetFileList;
import com.androtex.files.interfaces.IRenameFile;
import com.androtex.files.interfaces.IUpdateFile;
import com.androtex.format.TextFormat;
import com.androtex.format.Valid;
import com.androtex.http.LoadManagement;
import com.androtex.pager.FilePagerAdapter;
import com.androtex.pager.ProjectPagerAdapter;
import com.androtex.user.MessagingService;
import com.androtex.util.ExtensionManager;
import com.androtex.viewpagerindicator.TitlePageIndicator;

public class ActivityFileList extends FragmentActivity implements IGetFileList,
ICreateFile, IGetFile, IUpdateFile, ICompileFile, IRenameFile,
IDeleteFile {
	int PICK_REQUEST_CODE = 0;
	private TuneActionBar _tune_action_bar;

	private ListView _list;
	private EditText _zone_texte;
	private Button _save;
	private Button _compile;
	private Button _add;
	private Button _create;
	private ImageView _zone_image;
	//private TextFormat _editeur;
	private FileArray _file_array;

	private static LoadManagement _user_action;
	public static LoadManagement getLoginAction(){
		return _user_action;
	}
	public static void setFileAction(LoadManagement _action){
		_user_action = _action;
	}
	public void computeAndCreateProgress(){
		_user_action.compute();
		createActionProgress();
	}
	private static ProgressDialog _progress;
	private String _login;
	private String _password;
	private String _project;

	private String _file_in_edition = null;
	private byte[] _image_data;

	private MessagingService _message;
	private String _zone_texte_save;

	private void createActionProgress() {
		runOnUiThread(new Thread() {
			public void run() {
				if (_user_action != null && !_user_action.isComplete()
						&& _progress == null) {
					_progress = new ProgressDialog(ActivityFileList.this);
					_progress.setMessage(ActivityFileList.this
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

	private void stopActionProgress() {
		runOnUiThread(new Thread() {
			public void run() {
				if (// _user_action != null && _user_action.isComplete() &&
						_progress != null && _progress.isShowing()) {
					_progress.dismiss();
					_progress = null;
				}
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_zone_texte_save = null;
		setContentView(R.layout.editortest);
		_tune_action_bar = new TuneActionBar(this);

		_file_in_edition = null;

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.getString("login") != null
				&& extras.getString("project") != null
				&& extras.getString("password") != null) {
			if (extras.containsKey("version"))
				_tune_action_bar.refreshServerVersion(extras
						.getString("version"));

			_login = extras.getString("login");
			_password = extras.getString("password");
			_project = extras.getString("project");

			ViewPager pager = (ViewPager)findViewById( R.id.viewpager );
			if(pager != null){
				FilePagerAdapter adapter = new FilePagerAdapter( this );
				TitlePageIndicator indicator =
						(TitlePageIndicator)findViewById( R.id.indicator );
				pager.setAdapter(adapter);
				indicator.setViewPager(pager);
			}else{
				//--------------------
				setActivityFileComponents(
						(EditText) findViewById(R.filelist.text),
						(ImageView) findViewById(R.filelist.image),
						(Button) findViewById(R.filelist.save),
						(Button) findViewById(R.filelist.compile));

				//-------------------------------
				//----------------------------
				setActivityFileComponents((ListView) findViewById(R.filelist.list));

				setActivityFileComponents(
						(Button) findViewById(R.filelist.add), 
						(Button) findViewById(R.filelist.create));
			}
		}

		createActionProgress();

	}


	public void setActivityFileComponents(Button add,
			Button create){
		_add = add;
		_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				addExistingFile();
			}

		});

		_create = create;
		_create.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createFile();
			}
		});
	}
	public void setActivityFileComponents(EditText text,
			ImageView image,
			Button save,
			Button compile){
		_zone_texte = text;
		if(_zone_texte_save != null)
			_zone_texte.setText(_zone_texte_save);
		_zone_texte_save = null;
		Log.d("zone texte save", "texte "+_zone_texte_save);
		_zone_image = image;
		_save = save;
		_compile = compile;
		//_editeur = new TextFormat(_zone_texte);

		_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (_file_in_edition != null
						&& _file_in_edition.length() > 4
						&& "tex".equals(_file_in_edition
								.substring(_file_in_edition.length() - 3))) {
					String update = _zone_texte.getText().toString();
					_user_action = new UpdateFile(ActivityFileList.this,
							ActivityFileList.this, _login, _password,
							_project, _file_in_edition, update);
					_user_action.compute();
					createActionProgress();
				}//else{
				//	MessagingService.getInstance().makeMessage(ActivityFileList.this, echec,Toast.LENGTH_LONG);
				//}
			}

		});

		_compile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (_file_in_edition != null
						&& _file_in_edition.length() > 4
						&& "tex".equals(_file_in_edition
								.substring(_file_in_edition.length() - 3))) {
					String update = _zone_texte.getText().toString();
					_user_action = new CompileFile(ActivityFileList.this,
							ActivityFileList.this, _login, _password,
							_project, _file_in_edition, update);
					_user_action.compute();
					createActionProgress();
				}
			}

		});

		//_zone_texte.addTextChangedListener(_editeur);
	}

	public void setActivityFileComponents(ListView list){
		_list = list;

		try{
			_user_action = new GetFileList(this, this, _login, _password,
					_project);
			_user_action.compute();
			createActionProgress();
		}catch(Exception e){

		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		try{
			//getActionBar().setDisplayHomeAsUpEnabled(true);
		}catch(Exception e){

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.idmenu.add_existing_file:
			addExistingFile();
			return true;
		case R.idmenu.add_file:
			createFile();
			return true;
		case R.idmenu.prefs:
			Intent i = new Intent(this, Preferences.class);
			startActivity(i);
			return true;
		case android.R.id.home:
			// app icon in Action Bar clicked; go home
			Intent intent = new Intent(this, ActivityProjectList.class);
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
		createActionProgress();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopActionProgress();
	}

	@Override
	public void onPause() {
		super.onPause();
		stopActionProgress();
	}

	@Override
	public void onRestoreInstanceState(Bundle restore) {
		super.onRestoreInstanceState(restore);
		if (restore.containsKey("version"))
			_tune_action_bar.refreshServerVersion(restore.getString("version"));

		if (restore.containsKey("login"))
			_login = restore.getString("login");
		if (restore.containsKey("password"))
			_password = restore.getString("password");
		if (restore.containsKey("project"))
			_project = restore.getString("project");

		if (restore.containsKey("__file__"))
			_file_in_edition = restore.getString("__file__");

		if (restore.containsKey("__text__")){
			if(_zone_texte != null)
				_zone_texte.setText(restore.getString("__text__"));
			else
				_zone_texte_save = restore.getString("__text__");
		}

		if (restore.containsKey("__data__"))
			_image_data = restore.getByteArray("__data__");

		if (_file_in_edition != null) {
			String ext = _file_in_edition
					.substring(_file_in_edition.length() - 3);

			if (ExtensionManager.isImage(ext)) {
				_zone_image.setImageBitmap(BitmapFactory.decodeByteArray(
						_image_data, 0, _image_data.length));
				changeVisibility(false, true);
			} else {
				changeVisibility(true, false);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle save) {
		super.onSaveInstanceState(save);
		save.putString("login", _login);
		save.putString("password", _password);
		save.putString("project", _project);

		if (_tune_action_bar != null && _tune_action_bar.getVersion() != null)
			save.putString("version", _tune_action_bar.getVersion());

		if (_file_in_edition != null)
			save.putString("__file__", _file_in_edition);

		if (_zone_texte != null)
			save.putString("__text__", _zone_texte.getText().toString());

		if (_image_data != null)
			save.putByteArray("__data__", _image_data);
	}

	public void addExistingFile() {
		Intent intentBrowseFiles = new Intent(Intent.ACTION_GET_CONTENT);
		intentBrowseFiles.setType("*/*");
		intentBrowseFiles.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intentBrowseFiles, PICK_REQUEST_CODE);
	}

	public void createFile() {
		AlertDialog.Builder alert = new AlertDialog.Builder(
				ActivityFileList.this);

		alert.setTitle(ActivityFileList.this.getText(R.string.filename));

		alert.setMessage(ActivityFileList.this
				.getText(R.string.filename_description));

		// Set an EditText view to get user input
		final EditText input = new EditText(ActivityFileList.this);
		alert.setView(input);

		alert.setPositiveButton(ActivityFileList.this.getText(R.string.ok),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				if (value.length() < 5
						|| !(".tex".equals(value.substring(value
								.length() - 4))))
					value = value + ".tex";

				if (Valid.validString(value)) {
					_user_action = new CreateFile(
							ActivityFileList.this,
							ActivityFileList.this, _login, _password,
							_project, value);
					_user_action.compute();

					dialog.dismiss();
					createActionProgress();
				} else {
					MessagingService.getInstance().makeMessage(
							ActivityFileList.this,
							getString(R.string.patternwrong),
							MessagingService.MESSAGE_LONG);
				}
			}
		});

		alert.setNegativeButton(ActivityFileList.this.getText(R.string.cancel),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});

		alert.show();

	}

	public final String getLogin() {
		return _login;
	}

	public final String getPassword() {
		return _password;
	}

	public final String getProject() {
		return _project;
	}

	public void getFile(String name) {

		_user_action = new GetFile(this, this, getLogin(), getPassword(),
				getProject(), name);
		_user_action.compute();
		createActionProgress();
	}

	public void renameFileFromProject(final String file) {
		AlertDialog.Builder alert = new AlertDialog.Builder(
				ActivityFileList.this);

		alert.setTitle(ActivityFileList.this.getText(R.string.filename));

		alert.setMessage(ActivityFileList.this
				.getText(R.string.filename_description));

		// Set an EditText view to get user input
		final EditText input = new EditText(ActivityFileList.this);
		input.setText(file);
		alert.setView(input);

		alert.setPositiveButton(ActivityFileList.this.getText(R.string.ok),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				if (value.length() < 5) {
					return;
				}

				if (Valid.validString(value)) {
					String ext1 = value.substring(value.length() - 3);
					String ext2 = file.substring(file.length() - 3);
					if (ext1.equals(ext2)) {
						_user_action = new RenameFile(
								ActivityFileList.this,
								ActivityFileList.this, getLogin(),
								getPassword(), getProject(), file,
								value);
						_user_action.compute();
						createActionProgress();
						dialog.dismiss();
					}
				} else {
					MessagingService.getInstance().makeMessage(
							ActivityFileList.this,
							getString(R.string.patternwrong),
							MessagingService.MESSAGE_LONG);
				}
			}
		});

		alert.setNegativeButton(ActivityFileList.this.getText(R.string.cancel),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});

		alert.show();

	}

	public void deleteFileFromProject(final String file) {
		AlertDialog.Builder alert = new AlertDialog.Builder(
				ActivityFileList.this);

		alert.setTitle(ActivityFileList.this.getText(R.string.filedelete));

		alert.setMessage(ActivityFileList.this
				.getText(R.string.filedelete_message));

		alert.setPositiveButton(ActivityFileList.this.getText(R.string.ok),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
				ActivityFileList.this.runOnUiThread(new Thread(){
					public void run(){
						_user_action = new DeleteFile(ActivityFileList.this, ActivityFileList.this, getLogin(), getPassword(),
								getProject(), file);
						_user_action.compute();
						createActionProgress();
					}
				});
			}
		});

		alert.setNegativeButton(ActivityFileList.this.getText(R.string.cancel),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});

		alert.show();

	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == PICK_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Uri uri = intent.getData();
				// String type = intent.getType();
				Log.d("uri"," "+uri.toString());
				String path = uri.toString();
				try {
					readFromFile(path);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onCreateFileSuccess(String[] files, final String version) {
		_tune_action_bar.refreshServerVersion(version);
		if (_user_action != null) {
			stopActionProgress();
			_user_action = null;
		}

		_file_array = new FileArray(_project, files);
		runOnUiThread(new Thread() {
			public void run() {
				MessagingService.getInstance().makeMessage(
						ActivityFileList.this,
						getString(R.string.createfilesuccess),
						MessagingService.MESSAGE_LONG);

				ListFileAdapter _adapter = new ListFileAdapter(
						ActivityFileList.this, _file_array);
				_list.setAdapter(_adapter);


				ViewPager pager = (ViewPager)findViewById( R.id.viewpager );
				if(pager != null){
					pager.setCurrentItem(1);
				}
			}
		});
	}

	@Override
	public void onCreateFileFailure(final String text, final String version) {
		_tune_action_bar.refreshServerVersion(version);

		if (_user_action != null) {
			stopActionProgress();
			_user_action = null;
		}

		String echec = ActivityFileList.this
				.getText(R.string.createfilefailure).toString() + "\n" + text;

		MessagingService.getInstance().makeMessage(ActivityFileList.this,
				echec, MessagingService.MESSAGE_LONG);
	}

	@Override
	public void onFileListSuccess(String[] files, final String version) {
		_tune_action_bar.refreshServerVersion(version);

		if (_user_action != null) {
			stopActionProgress();
			_user_action = null;
		}

		_file_array = new FileArray(_project, files);
		this.runOnUiThread(new Thread() {
			public void run() {
				ListFileAdapter _adapter = new ListFileAdapter(
						ActivityFileList.this, _file_array);
				_list.setAdapter(_adapter);


				ViewPager pager = (ViewPager)findViewById( R.id.viewpager );
				if(pager != null){
					pager.setCurrentItem(1);
				}
			}
		});
	}

	@Override
	public void onFileListFailure(final String text, final String version) {
		_tune_action_bar.refreshServerVersion(version);

		if (_user_action != null) {
			stopActionProgress();
			_user_action = null;
		}

		String echec = ActivityFileList.this.getText(R.string.filelistfailure)
				.toString() + "\n" + text;
		MessagingService.getInstance().makeMessage(ActivityFileList.this,
				echec, MessagingService.MESSAGE_LONG);
	}

	private byte[] extendTwice(byte[] prev) {
		byte[] _new = new byte[prev.length * 2];
		for (int i = 0; i < prev.length; _new[i] = prev[i], i++)
			;
		return _new;
	}

	public void readFromFile(String path) throws Exception {
		if(path.startsWith("content://"))
			path = path.replace("content://", "");
		if(path.startsWith("file://"))
			path = path.replace("file://", "");//.replace("/mnt", "");
		if(path.startsWith("org.openintents.filemanager/"))
			path = path.replaceFirst("org.openintents.filemanager/", "");
		if(path.startsWith("com.ghostsq.commander.FileProvider/"))
			path = path.replaceFirst("com.ghostsq.commander.FileProvider/", "");
		path = URLDecoder.decode(path,"UTF-8");

		String[] _path = path.split("/");
		String _filename = _path[_path.length - 1];
		if (_filename.length() < 3){
			//|| _filename.charAt(0) == '.'
			//|| !Valid.validString(_filename)) {
			//error > no extension will be correct !
			MessagingService.getInstance().makeMessage(
					ActivityFileList.this,
					getString(R.string.patternwrong),
					MessagingService.MESSAGE_LONG);
			return;
		}else{
			//we send the file to be processed
			//first the path
			//the first, the real one, extracted
			nextReadFromFile(path, _filename);
		}
	}

	private void nextReadFromFile(final String path, final String filenamegiven) throws Exception {
		try {
			String filename = filenamegiven;
			String[] _path = path.split("/");
			//we extract the extension
			final String ext = filename.substring(filename.length() - 3);

			if (ExtensionManager.isExtensionValid(ext)) {
				final String __path = path;

				//the filename without extension
				final String filename_edit = filename.substring(0, filename.length() - 4);

				//if the filename is valid
				if(Valid.validString(filename)){
					int read = 0;
					File f = new File(path);
					if (f.canRead()) {
						FileInputStream fIn = new FileInputStream(f);
						byte[] _buff = new byte[100];
						int _char = 0;
						do {
							_char = fIn.read();
							if (_char != -1) {
								if (read == _buff.length)
									_buff = extendTwice(_buff);
								_buff[read] = (byte) (_char & 0xff);
								read++;
							}
						} while (_char != -1);
						String b = Base64.encodeToString(_buff, 0, read, 0);

						_user_action = new CreateDataFile(ActivityFileList.this,
								ActivityFileList.this, _login, _password, _project,
								filename, b);
						_user_action.compute();
						createActionProgress();
					} else {
						String echec = ActivityFileList.this.getText(
								R.string.cantread).toString();
						echec += "\n";
						echec += __path;

						MessagingService.getInstance().makeMessage(ActivityFileList.this, echec,Toast.LENGTH_LONG);
					}
				}else{
					//the filename given is not valid !
					//we will ask the user to write the filename
					//in this part we will then invoke
					//an other function
					renameFileBeforeAdd(path, 
							filename, 
							filename_edit,
							ext);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void renameFileBeforeAdd(final String path, 
			final String filenamegiven, 
			final String shorten,
			final String ext) {
		AlertDialog.Builder alert = new AlertDialog.Builder(
				ActivityFileList.this);

		alert.setTitle(ActivityFileList.this.getText(R.string.filename));

		alert.setMessage(ActivityFileList.this
				.getText(R.string.newfilename_description));

		// Set an EditText view to get user input
		final EditText input = new EditText(ActivityFileList.this);
		input.setText(shorten);
		alert.setView(input);

		alert.setPositiveButton(ActivityFileList.this.getText(R.string.ok),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString()+"."+ext;
				dialog.dismiss();
				//if we have written something
				//and the newfilename.ext is valid
				if (value.length() >=1 && Valid.validString(value)) {
					//we recall the previous function
					//with the previous path (we will read)
					//and the filename we will register serverside
					try {
						nextReadFromFile(path, value);
					} catch (Exception e) {
						MessagingService.getInstance().makeMessage(
								ActivityFileList.this,
								"Exception",
								MessagingService.MESSAGE_LONG);
						e.printStackTrace();
					}
				} else {
					MessagingService.getInstance().makeMessage(
							ActivityFileList.this,
							getString(R.string.patternwrong),
							MessagingService.MESSAGE_LONG);
				}
			}
		});

		alert.setNegativeButton(ActivityFileList.this.getText(R.string.cancel),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});

		alert.show();


	}

	@Override
	public void onTexFileSuccess(final String file, final String data) {

		if (_user_action != null) {
			stopActionProgress();
			_user_action = null;
		}
		runOnUiThread(new Thread() {
			public void run() {
				_file_in_edition = file;
				_zone_texte.setText(data);
				changeVisibility(true, false);


				ViewPager pager = (ViewPager)findViewById( R.id.viewpager );
				if(pager != null){
					pager.setCurrentItem(2);
				}
			}
		});
	}

	@Override
	public void onImageFileSuccess(final String file, final byte[] data) {

		if (_user_action != null) {
			stopActionProgress();
			_user_action = null;
		}

		try{
			_image_data = data;
			_file_in_edition = file;

			runOnUiThread(new Thread() {
				public void run() {
					updateImage();
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void updateImage(){
		try{
			_zone_image.setImageBitmap(BitmapFactory.decodeByteArray(_image_data,
					0, _image_data.length));
			changeVisibility(false, true);


			ViewPager pager = (ViewPager)findViewById( R.id.viewpager );
			if(pager != null){
				pager.setCurrentItem(2);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onFileFailure(final String text) {

		if (_user_action != null) {
			stopActionProgress();
			_user_action = null;
		}

		String echec = ActivityFileList.this.getText(R.string.cantload)
				.toString() + "\n" + text;
		MessagingService.getInstance().makeMessage(ActivityFileList.this,
				echec, MessagingService.MESSAGE_LONG);
	}

	@Override
	public void onUpdateFileSuccess(final String project, final String file,
			final String text, final String version) {
		_tune_action_bar.refreshServerVersion(version);

		if (_user_action != null) {
			stopActionProgress();
			_user_action = null;
		}

		String res = ActivityFileList.this.getText(R.string.fileupdated)
				.toString();
		res = res.replace("{NAME}", file);
		res = res.replace("{PROJECT}", project);
		MessagingService.getInstance().makeMessage(ActivityFileList.this, res,
				MessagingService.MESSAGE_LONG);

	}

	@Override
	public void onUpdateFileFailure(final String project, final String file,
			final String text, final String version) {
		_tune_action_bar.refreshServerVersion(version);

		if (_user_action != null) {
			stopActionProgress();
			_user_action = null;
		}
		String echec = ActivityFileList.this.getText(R.string.cantmaj)
				.toString() + "\n" + text;
		MessagingService.getInstance().makeMessage(ActivityFileList.this,
				echec, MessagingService.MESSAGE_LONG);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menufile, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onCompileFileSuccess(final String project, final String file,
			final byte[] pdf, final String version) {
		_tune_action_bar.refreshServerVersion(version);

		if (_user_action != null) {
			stopActionProgress();
			_user_action = null;
		}
		String exppdf = "";
		if (file.length() > 4) {
			exppdf = file.substring(0, file.length() - 4) + ".pdf";
		}

		File f = new File("/sdcard/" + exppdf);
		try {
			if (!f.exists())
				f.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (f.canWrite()) {
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(pdf);
				fos.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		try {
			if (f.exists()) {
				Uri path = Uri.fromFile(f);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(path, "application/pdf");
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				startActivity(intent);
			}
		} catch (ActivityNotFoundException e) {
			MessagingService.getInstance()
			.makeMessage(ActivityFileList.this,
					getString(R.string.pdfnopdf),
					MessagingService.MESSAGE_LONG);
		} catch (Exception e) {
			MessagingService.getInstance()
			.makeMessage(ActivityFileList.this,
					getString(R.string.pdferror),
					MessagingService.MESSAGE_LONG);
		}

	}

	@Override
	public void onCompileFileFailure(final String project, final String file,
			final String log, final String version) {
		_tune_action_bar.refreshServerVersion(version);

		if (_user_action != null) {
			stopActionProgress();
			_user_action = null;
		}

		MessagingService.getInstance().makeMessage(ActivityFileList.this,
				getString(R.string.pdferror)+((log != null)?"\n\n"+log:""), MessagingService.MESSAGE_LONG);
	}

	@Override
	public void onRenameFileSuccess(final String[] files,
			final String previous_name, final String new_name,
			final String server_version) {
		_tune_action_bar.refreshServerVersion(server_version);
		if (_user_action != null) {
			stopActionProgress();
			_user_action = null;
		}

		// do we have changed the file we are currently editing ?
		if (_file_in_edition != null && previous_name.equals(_file_in_edition)) {
			// if so we change the filename in edition
			_file_in_edition = new_name;
		}

		_file_array = new FileArray(_project, files);

		runOnUiThread(new Thread() {
			public void run() {
				MessagingService.getInstance().makeMessage(
						ActivityFileList.this,
						getString(R.string.filerenamesuccess),
						MessagingService.MESSAGE_LONG);

				ListFileAdapter _adapter = new ListFileAdapter(
						ActivityFileList.this, _file_array);
				_list.setAdapter(_adapter);
			}
		});
	}

	@Override
	public void onRenameFileFailure(final String text,
			final String server_version) {
		_tune_action_bar.refreshServerVersion(server_version);

		if (_user_action != null) {
			stopActionProgress();
			_user_action = null;
		}

		String echec = ActivityFileList.this.getText(R.string.filerenameerror)
				.toString();
		MessagingService.getInstance().makeMessage(ActivityFileList.this,
				echec, MessagingService.MESSAGE_LONG);
	}

	@Override
	public void onDeleteFileSuccess(final String[] files,
			final String name,  final String server_version) {
		_tune_action_bar.refreshServerVersion(server_version);
		if (_user_action != null) {
			stopActionProgress();
			_user_action = null;
		}
		_file_array = new FileArray(_project, files);



		runOnUiThread(new Thread() {
			public void run() {
				// do we have changed the file we are currently editing ?
				if (_file_in_edition != null && name.equals(_file_in_edition)) {
					// if so we change the filename in edition
					_file_in_edition = null;
					changeVisibility(false, false);
				}

				MessagingService.getInstance().makeMessage(
						ActivityFileList.this,
						getString(R.string.filedeletesuccess),
						MessagingService.MESSAGE_LONG);

				ListFileAdapter _adapter = new ListFileAdapter(
						ActivityFileList.this, _file_array);
				_list.setAdapter(_adapter);
			}
		});
	}

	@Override
	public void onDeleteFileFailure(final String text,
			final String server_version) {
		_tune_action_bar.refreshServerVersion(server_version);

		if (_user_action != null) {
			stopActionProgress();
			_user_action = null;
		}

		String echec = ActivityFileList.this.getText(R.string.filedeleteerror)
				.toString();
		MessagingService.getInstance().makeMessage(ActivityFileList.this,
				echec, MessagingService.MESSAGE_LONG);
	}

	//00 > none display
	//10 text
	//01 img
	//11 error x°)
	private int _manage_null_ui = 0;

	private void changeVisibility(boolean show_text, boolean show_img){
		if(_zone_texte == null || _save == null ||
				_compile == null || _zone_image == null){

		}

		if(show_text){
			if(_zone_texte != null)
				_zone_texte.setVisibility(View.VISIBLE);
			if(_save != null)
				_save.setVisibility(View.VISIBLE);
			if(_compile != null)
				_compile.setVisibility(View.VISIBLE);
		}else{
			if(_zone_texte != null)
				_zone_texte.setVisibility(View.GONE);
			if(_save != null)
				_save.setVisibility(View.GONE);
			if(_compile != null)
				_compile.setVisibility(View.GONE);
		}

		if(show_img){
			if(_zone_image != null)
				_zone_image.setVisibility(View.VISIBLE);
		}else{
			if(_zone_image != null)
				_zone_image.setVisibility(View.GONE);
		}
	}
}