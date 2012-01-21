package com.androtex.login.interfaces;

/**
 * Interface to manage Login status
 * 
 * @author kevin
 * 
 */
public interface ICreateLogin {
	/**
	 * Function called when we are successfully logged
	 * 
	 * @param text
	 *            the content text, "log" if it was an internal request
	 * @param login
	 *            the user login
	 * @param password
	 *            the user password
	 * @param server_version
	 *            a string representing the file server or null
	 */
	public void onCreateLoginSuccess(String text, String login, String password, final String server_version);

	/**
	 * Function called when we have received login error
	 * 
	 * @param text
	 *            the content text, "log" if it was an internal request
	 * @param server_version
	 *            a string representing the file server or null
	 */
	public void onCreateLoginFailure(String text, final String server_version);
}
