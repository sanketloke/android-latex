package com.androtex.projects;

/**
 * ProjectArray
 * 
 * Class representing an array of project
 * 
 * @author Kevin Le Perf
 * 
 */
public class ProjectArray {
	/**
	 * the project list
	 */
	private Project[] _projects;

	/**
	 * ProjectArray Constructor the initialize the project list
	 * 
	 * @param projects
	 *            the project listing
	 */
	public ProjectArray(String[] projects) {
		if (projects != null && projects.length != 0) {
			_projects = new Project[projects.length];
			for (int i = 0; i < projects.length; _projects[i] = new Project(
					projects[i]), i++)
				;
		}
	}

	/**
	 * get return the project at the <b>index</b> pos
	 * 
	 * @param index
	 *            the index of the project we need
	 * @return the project at <b>i</b> index or null if ArrayOutOfBounds
	 */
	public Project get(int index) {
		return (_projects != null && index < _projects.length) ? _projects[index]
				: null;
	}

	/**
	 * length return the project list size
	 * 
	 * @return the number of project known
	 */
	public int length() {
		return _projects != null ? _projects.length : 0;
	}

}
