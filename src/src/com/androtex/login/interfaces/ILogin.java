package com.androtex.login.interfaces;

/**
 * Interface to manage Login status
 * 
 * @author kevin
 * 
 */
public interface ILogin {
	/**
	 * Function called when we are successfully logged
	 * 
	 * @param text
	 *            the content text, "log" if it was an internal request
	 * @param login
	 *            a string representing the user login
	 * @param password
	 *            the user's password
	 * @param server_version
	 *            a string representing the file server or null
	 */
	public void onLoginSuccess(final String text, final String login, final String password, final String server_version);

	/**
	 * Function called when we have received login error
	 * 
	 * @param text
	 *            the content text, "log" if it was an internal request
	 * @param server_version
	 *            a string representing the file server or null
	 */
	public void onLoginFailure(final String text, final String server_version);
}
