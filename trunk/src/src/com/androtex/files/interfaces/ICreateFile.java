package com.androtex.files.interfaces;

/**
 * Interface to manage the file creation in a project
 * 
 * @author Kevin Le Perf
 * 
 */
public interface ICreateFile {
	/**
	 * onCreateFileSuccess Method called when the file was created
	 * 
	 * @param files
	 *            the new project's file list
	 * @param server_version
	 *            a string representing the file server or null
	 */
	public void onCreateFileSuccess(String[] files,final String server_version);

	/**
	 * onCreateFileFailure
	 * 
	 * @param text
	 *            the text describing the error
	 * @param server_version
	 *            a string representing the file server or null
	 */
	public void onCreateFileFailure(String text,final String server_version);
}
