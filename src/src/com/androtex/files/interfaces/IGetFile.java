package com.androtex.files.interfaces;

/**
 * Interface to manage the file we wanted
 * 
 * @author Kevin Le Perf
 * 
 */
public interface IGetFile {
	/**
	 * onTexFileSuccess Method called when the file obtained is a tex source
	 * file
	 * 
	 * @param file
	 *            the filename we got
	 * @param data
	 *            the data in a String
	 */
	public void onTexFileSuccess(final String file, final String data);

	/**
	 * onImageFileSuccess Method called when the file obtained is an image
	 * 
	 * @param file
	 *            the filename we got
	 * @param data
	 *            the data in a byte array
	 */
	public void onImageFileSuccess(final String file, final byte[] data);

	/**
	 * onFileFailure Method called when the retrieving failed
	 * 
	 * @param text
	 *            the text describing the error
	 */
	public void onFileFailure(final String text);
}
