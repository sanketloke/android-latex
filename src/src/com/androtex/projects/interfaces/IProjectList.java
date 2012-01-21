package com.androtex.projects.interfaces;

/**
 * Interface managing the project list from an user
 * 
 * @author Kevin Le Perf
 * 
 */
public interface IProjectList {
	/**
	 * onProjectListSuccess Method called when the listing as successfully done
	 * 
	 * @param projects
	 *            the filename strings array got by the server call
	 * @param version
	 *            the server's version
	 */
	public void onProjectListSuccess(String[] projects, final String version);

	/**
	 * onProjectListFailure Method called when the listing returned and error
	 * 
	 * @param text
	 *            a text representing the error
	 * @param version
	 *            the server's version
	 */
	public void onProjectListFailure(String text, final String version);
}
