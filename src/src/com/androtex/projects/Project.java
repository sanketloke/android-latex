package com.androtex.projects;

/**
 * Class representing a simple project
 * 
 * @author Kevin Le Perf
 * 
 */
public class Project {
	/**
	 * The project name
	 */
	private String _projet;

	/**
	 * Project initialize the current project with a name null
	 */
	private Project() {
		_projet = null;
	}

	/**
	 * Project initialize a project with a name
	 * 
	 * @param projet
	 *            the project name
	 */
	public Project(String projet) {
		this();
		_projet = projet;
	}

	/**
	 * getName
	 * 
	 * @return the project name
	 */
	public String getName() {
		return _projet;
	}

	/**
	 * toString method called to dump an object
	 * 
	 * @return a representation <b>name</b>
	 */
	public String toString() {
		return getName();
	}
}
