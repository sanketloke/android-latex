package com.androtex.files.interfaces;

/**
 * Interface created to manage file updating
 * 
 * @author Kevin Le Perf
 * 
 */
public interface IUpdateFile {
	/**
	 * onUpdateFileSuccess method called when the update has been done
	 * 
	 * @param project
	 *            the involved project
	 * @param file
	 *            the file name which has been saved
	 * @param text
	 *            a text describing the success
	 * @param server_version
	 *            a string representing the file server or null
	 */
	public void onUpdateFileSuccess(final String project, final String file,
			final String text, final String server_version);

	/**
	 * onUpdateFileFailure method called if the update has failed
	 * 
	 * @param project
	 *            the involved project
	 * @param file
	 *            the file name which has been saved
	 * @param text
	 *            a text describing the failure
	 * @param server_version
	 *            a string representing the file server or null
	 */
	public void onUpdateFileFailure(final String project, final String file,
			final String text,final String server_version);
}
