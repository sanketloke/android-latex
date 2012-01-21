package com.androtex.projects.interfaces;

/**
 * Interface to manage the creation of a project
 * 
 * @author Kevin Le Perf
 * 
 */
public interface ICreateProject {
	/**
	 * onCreateProjectSuccess Method called when a project was succesfully
	 * created
	 * 
	 * @param projects
	 *            return the listing list with the new one
	 * @param version
	 *            a string representing the server version
	 */
	public void onCreateProjectSuccess(String[] projects, final String version);

	/**
	 * onCreateProjectFailure Method called when a project creation failed
	 * 
	 * @param text
	 *            a text representing the error
	 * @param version
	 *            a string representing the server version
	 */
	public void onCreateProjectFailure(String text, final String version);
}
