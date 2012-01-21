package com.androtex.files;

public class File {
	private String _filename;

	private File() {
		_filename = null;
	}

	public File(String filename) {
		this();
		_filename = filename;
	}

	public String getName() {
		return _filename;
	}
}
