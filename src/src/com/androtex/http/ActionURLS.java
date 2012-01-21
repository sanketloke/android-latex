package com.androtex.http;

public class ActionURLS {

	/* The server root page URL */
	private static final String SERVER_ROOT = "?";

	/* Type Level */
	private static final String TYPE_ATTR = "type";
	private static final String PROJECT_TYPE_ATTR = "project";
	private static final String FILE_TYPE_ATTR = "file";
	private static final String USER_TYPE_ATTR = "user";

	/* Action Level */
	private static final String ACTION_ATTR = "action";
	private static final String AUTHENTICATE_ACTION_ATTR = "auth";
	private static final String CREATE_ACTION_ATTR = "create";
	private static final String UPDATE_ACTION_ATTR = "update";
	private static final String DELETE_ACTION_ATTR = "delete";
	private static final String EXPLORE_ACTION_ATTR = "explore";
	private static final String COMPILE_ACTION_ATTR = "compile";

	/* Data level */
	private static final String LOGIN_DATA_ATTR = "login";
	private static final String PASSWORD_DATA_ATTR = "password";
	private static final String PROJECT_NAME_DATA_ATTR = "project";
	private static final String FILE_NAME_DATA_ATTR = "file";
	private static final String NEW_NAME_DATA_ATTR = "newname";

	// Request hierarchy :
	// 1. Type {project, file, user}
	// 2. Action {auth, create, update, delete,explore, compile}
	// 3. Data {login, password, project, file}

	// ----------------------------
	// USER TYPE URLS
	// ----------------------------

	public static String getCreateUserURL(String login, String password) {
		return new StringBuilder()
				.append(SERVER_ROOT + TYPE_ATTR + "=" + USER_TYPE_ATTR)
				.append("&" + ACTION_ATTR + "=" + CREATE_ACTION_ATTR)
				.append("&" + LOGIN_DATA_ATTR + "=" + login)
				.append("&" + PASSWORD_DATA_ATTR + "=" + password).toString();
	}

	public static String getDeleteUserURL(String login, String password) {
		return new StringBuilder()
				.append(SERVER_ROOT + TYPE_ATTR + "=" + USER_TYPE_ATTR)
				.append("&" + ACTION_ATTR + "=" + DELETE_ACTION_ATTR)
				.append("&" + LOGIN_DATA_ATTR + "=" + login)
				.append("&" + PASSWORD_DATA_ATTR + "=" + password).toString();
	}

	public static String getAuthenticationURL(String login, String password) {
		return new StringBuilder()
				.append(SERVER_ROOT + TYPE_ATTR + "=" + USER_TYPE_ATTR)
				.append("&" + ACTION_ATTR + "=" + AUTHENTICATE_ACTION_ATTR)
				.append("&" + LOGIN_DATA_ATTR + "=" + login)
				.append("&" + PASSWORD_DATA_ATTR + "=" + password).toString();
	}

	public static String getExploreUserURL(String login, String password) {
		return new StringBuilder()
				.append(SERVER_ROOT + TYPE_ATTR + "=" + USER_TYPE_ATTR)
				.append("&" + ACTION_ATTR + "=" + EXPLORE_ACTION_ATTR)
				.append("&" + LOGIN_DATA_ATTR + "=" + login)
				.append("&" + PASSWORD_DATA_ATTR + "=" + password).toString();
	}

	// ----------------------------
	// PROJECT TYPE URLS
	// ----------------------------

	public static String getCreateProjectURL(String login, String password,
			String projectName) {
		return new StringBuilder()
				.append(SERVER_ROOT + TYPE_ATTR + "=" + PROJECT_TYPE_ATTR)
				.append("&" + ACTION_ATTR + "=" + CREATE_ACTION_ATTR)
				.append("&" + LOGIN_DATA_ATTR + "=" + login)
				.append("&" + PASSWORD_DATA_ATTR + "=" + password)
				.append("&" + PROJECT_NAME_DATA_ATTR + "=" + projectName)
				.toString();
	}

	public static String getDeleteProjectURL(String login, String password,
			String projectName) {
		return new StringBuilder()
				.append(SERVER_ROOT + TYPE_ATTR + "=" + PROJECT_TYPE_ATTR)
				.append("&" + ACTION_ATTR + "=" + DELETE_ACTION_ATTR)
				.append("&" + LOGIN_DATA_ATTR + "=" + login)
				.append("&" + PASSWORD_DATA_ATTR + "=" + password)
				.append("&" + PROJECT_NAME_DATA_ATTR + "=" + projectName)
				.toString();
	}

	public static String getUpdateProjectURL(String login, String password,
			String projectName) {
		return new StringBuilder()
				.append(SERVER_ROOT + TYPE_ATTR + "=" + PROJECT_TYPE_ATTR)
				.append("&" + ACTION_ATTR + "=" + UPDATE_ACTION_ATTR)
				.append("&" + LOGIN_DATA_ATTR + "=" + login)
				.append("&" + PASSWORD_DATA_ATTR + "=" + password)
				.append("&" + PROJECT_NAME_DATA_ATTR + "=" + projectName)
				.toString();
	}

	public static String getUpdateProjectNameURL(String login, String password,
			String projectName, String newProjectName) {
		return new StringBuilder()
				.append(SERVER_ROOT + TYPE_ATTR + "=" + PROJECT_TYPE_ATTR)
				.append("&" + ACTION_ATTR + "=" + UPDATE_ACTION_ATTR)
				.append("&" + LOGIN_DATA_ATTR + "=" + login)
				.append("&" + PASSWORD_DATA_ATTR + "=" + password)
				.append("&" + PROJECT_NAME_DATA_ATTR + "=" + projectName)
				.append("&" + NEW_NAME_DATA_ATTR + "=" + newProjectName)
				.toString();
	}

	public static String getExploreProjectURL(String login, String password,
			String projectName) {
		return new StringBuilder()
				.append(SERVER_ROOT + TYPE_ATTR + "=" + PROJECT_TYPE_ATTR)
				.append("&" + ACTION_ATTR + "=" + EXPLORE_ACTION_ATTR)
				.append("&" + LOGIN_DATA_ATTR + "=" + login)
				.append("&" + PASSWORD_DATA_ATTR + "=" + password)
				.append("&" + PROJECT_NAME_DATA_ATTR + "=" + projectName)
				.toString();
	}

	// ----------------------------
	// FILE TYPE URLS
	// ----------------------------

	public static String getCreateFileURL(String login, String password,
			String projectName, String fileName) {
		return new StringBuilder()
				.append(SERVER_ROOT + TYPE_ATTR + "=" + FILE_TYPE_ATTR)
				.append("&" + ACTION_ATTR + "=" + CREATE_ACTION_ATTR)
				.append("&" + LOGIN_DATA_ATTR + "=" + login)
				.append("&" + PASSWORD_DATA_ATTR + "=" + password)
				.append("&" + PROJECT_NAME_DATA_ATTR + "=" + projectName)
				.append("&" + FILE_NAME_DATA_ATTR + "=" + fileName).toString();
	}

	public static String getDeleteFileURL(String login, String password,
			String projectName, String fileName) {
		return new StringBuilder()
				.append(SERVER_ROOT + TYPE_ATTR + "=" + FILE_TYPE_ATTR)
				.append("&" + ACTION_ATTR + "=" + DELETE_ACTION_ATTR)
				.append("&" + LOGIN_DATA_ATTR + "=" + login)
				.append("&" + PASSWORD_DATA_ATTR + "=" + password)
				.append("&" + PROJECT_NAME_DATA_ATTR + "=" + projectName)
				.append("&" + FILE_NAME_DATA_ATTR + "=" + fileName).toString();
	}

	public static String getUpdateFileURL(String login, String password,
			String projectName, String fileName) {
		return new StringBuilder()
				.append(SERVER_ROOT + TYPE_ATTR + "=" + FILE_TYPE_ATTR)
				.append("&" + ACTION_ATTR + "=" + UPDATE_ACTION_ATTR)
				.append("&" + LOGIN_DATA_ATTR + "=" + login)
				.append("&" + PASSWORD_DATA_ATTR + "=" + password)
				.append("&" + PROJECT_NAME_DATA_ATTR + "=" + projectName)
				.append("&" + FILE_NAME_DATA_ATTR + "=" + fileName).toString();
	}

	public static String getUpdateFileNameURL(String login, String password,
			String projectName, String fileName, String newFileName) {
		return new StringBuilder()
				.append(SERVER_ROOT + TYPE_ATTR + "=" + FILE_TYPE_ATTR)
				.append("&" + ACTION_ATTR + "=" + UPDATE_ACTION_ATTR)
				.append("&" + LOGIN_DATA_ATTR + "=" + login)
				.append("&" + PASSWORD_DATA_ATTR + "=" + password)
				.append("&" + PROJECT_NAME_DATA_ATTR + "=" + projectName)
				.append("&" + FILE_NAME_DATA_ATTR + "=" + fileName)
				.append("&" + NEW_NAME_DATA_ATTR + "=" + newFileName)
				.toString();
	}

	public static String getExploreFileURL(String login, String password,
			String projectName, String fileName) {
		return new StringBuilder()
				.append(SERVER_ROOT + TYPE_ATTR + "=" + FILE_TYPE_ATTR)
				.append("&" + ACTION_ATTR + "=" + EXPLORE_ACTION_ATTR)
				.append("&" + LOGIN_DATA_ATTR + "=" + login)
				.append("&" + PASSWORD_DATA_ATTR + "=" + password)
				.append("&" + PROJECT_NAME_DATA_ATTR + "=" + projectName)
				.append("&" + FILE_NAME_DATA_ATTR + "=" + fileName).toString();
	}

	public static String getCompileFileURL(String login, String password,
			String projectName, String fileName) {
		return new StringBuilder()
				.append(SERVER_ROOT + TYPE_ATTR + "=" + FILE_TYPE_ATTR)
				.append("&" + ACTION_ATTR + "=" + COMPILE_ACTION_ATTR)
				.append("&" + LOGIN_DATA_ATTR + "=" + login)
				.append("&" + PASSWORD_DATA_ATTR + "=" + password)
				.append("&" + PROJECT_NAME_DATA_ATTR + "=" + projectName)
				.append("&" + FILE_NAME_DATA_ATTR + "=" + fileName).toString();
	}

}
