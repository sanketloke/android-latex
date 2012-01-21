package com.androtex.http.interfaces;

import android.app.ProgressDialog;

/**
 * Interface use to define url loading, and state
 * 
 * @author Kevin Le Perf
 * 
 */
public interface IURLLoaded {
	/**
	 * Function called when we receive a 200 code
	 * 
	 * @param serial
	 *            the serial number of the current url loader
	 * @param text
	 *            the content text
	 */
	public void loadSuccess(int serial, String text);

	/**
	 * Function called when we receive a code != 200
	 * 
	 * @param serial
	 *            the serial number of the current url loader
	 * @param text
	 *            the content text
	 */
	public void loadFailure(int serial, String text);

	/**
	 * Function called when the page moved
	 * 
	 * @param serial
	 *            the serial number of the current url loader
	 * @param text
	 *            the content text
	 * @param progress
	 *            an android progress dialog object
	 */
	public void loadMove(int serial, String text);

	/**
	 * Function called when success & failure are managed
	 * 
	 * @param serial
	 *            the serial number of the current url loader
	 * @param text
	 *            the content text
	 * @param progress
	 *            an android progress dialog object
	 */
	public void loadEnd(int serial, String text, ProgressDialog progress);
}
