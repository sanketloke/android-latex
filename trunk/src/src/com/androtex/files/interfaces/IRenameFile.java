package com.androtex.files.interfaces;

/**
 * Interface to manage the file deletion in a project
 * 
 * @author Kevin Le Perf
 * 
 */
public interface IRenameFile {
	/**
	 * onCreateFileSuccess Method called when the file was created
	 * 
	 * @param files
	 *            the new project's file list
	 * @param server_version
	 *            a string representing the file server or null
	 */
	public void onRenameFileSuccess(String[] files, final String previous_name,
			final String new_name, final String server_version);

	/**
	 * onCreateFileFailure
	 * 
	 * @param text
	 *            the text describing the error
	 * @param server_version
	 *            a string representing the file server or null
	 */
	public void onRenameFileFailure(String text,final String server_version);
}
