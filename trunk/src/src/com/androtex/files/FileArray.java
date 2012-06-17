package com.androtex.files;

import android.util.Log;

public class FileArray {
	private File[] _filenames;
	private String _project;

	public FileArray(String project, String[] filenames) {
		_project = project;
		_filenames = new File[filenames.length];
		for (int i = 0; i < _filenames.length; _filenames[i] = new File(
				filenames[i]), i++)
			;
		Log.d("INFORMATIONS","OKKKKKKKKKK");
	}

	public File get(int i) {
		return (i < _filenames.length) ? _filenames[i] : null;
	}

	public String getProject() {
		return _project;
	}

	public int length() {
		return _filenames != null ? _filenames.length : 0;
	}

}
