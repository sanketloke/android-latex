package com.androtex.files.interfaces;

/**
 * Interface to manage the tex file compilation
 * 
 * @author Kevin Le Perf
 * 
 */
public interface ICompileFile {
	/**
	 * onCompileFileSuccess Method called when the pdf was successfully created
	 * 
	 * @param project
	 *            the project involved
	 * @param file
	 *            the tex file
	 * @param pdf
	 *            the pdf data as a byte array
	 * @param server_version
	 *            a string representing the file server or null
	 */
	public void onCompileFileSuccess(final String project, final String file,
			final byte[] pdf, final String server_version);

	/**
	 * onCompileFileFailure Method called when the pdf generation failed
	 * 
	 * @param project
	 *            the project involved
	 * @param file
	 *            the tex file
	 * @param log
	 *            the log data we obtained with the error
	 * @param server_version
	 *            a string representing the file server or null
	 */
	public void onCompileFileFailure(final String project, final String file,
			final String log,final String server_version);
}
