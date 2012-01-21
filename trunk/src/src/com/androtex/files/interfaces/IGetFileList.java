package com.androtex.files.interfaces;

/**
 * Interface to manage the project's file listing
 * 
 * @author Kevin Le Perf
 * 
 */
public interface IGetFileList {
	/**
	 * onFileListSuccess
	 * 
	 * @param files
	 *            the file list in an array
	 * @param server_version
	 *            a string representing the file server or null
	 */
	public void onFileListSuccess(String[] files,final String server_version);

	/**
	 * onFileListFailure
	 * 
	 * @param text
	 *            a text describing the error
	 * @param server_version
	 *            a string representing the file server or null
	 */
	public void onFileListFailure(String text,final String server_version);
}
