package com.androtex.files.interfaces;

/**
 * Interface to manage the file deletion in a project
 * 
 * @author Kevin Le Perf
 * 
 */
public interface IDeleteFile {
	/**
	 * onCreateFileSuccess Method called when the file was created
	 * 
	 * @param files
	 *            the new project's file list
	 * @param name
	 *            the name file we just deleted
	 * @param server_version
	 *            a string representing the file server or null
	 */
	public void onDeleteFileSuccess(String[] files,final String name, final String server_version);

	/**
	 * onCreateFileFailure
	 * 
	 * @param text
	 *            the text describing the error
	 * @param server_version
	 *            a string representing the file server or null
	 */
	public void onDeleteFileFailure(String text,final String server_version);
}
