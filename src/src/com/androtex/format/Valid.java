package com.androtex.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Valid {
	public static boolean validString(String string){
		Pattern pattern = Pattern.compile("([a-zA-Z0-9._])+");
		Matcher matcher = pattern.matcher(string);
		return matcher.matches() && string.indexOf("..") < 0;
	}
}
