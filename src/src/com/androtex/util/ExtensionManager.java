package com.androtex.util;

public class ExtensionManager {
	public static boolean isExtensionValid(String ext){
		if(ext != null)
			return "png".equals(ext) || 
					"gif".equals(ext) || 
					"jpeg".equals(ext) || 
					"jpg".equals(ext) || 
					"eps".equals(ext) || 
					"aux".equals(ext) || 
					"bib".equals(ext) || 
					"tex".equals(ext);
		return false;
			
	}
	
	public static boolean isDisplayable(String ext){
		if(ext != null)
			return "png".equals(ext) || 
					"gif".equals(ext) || 
					"jpeg".equals(ext) || 
					"jpg".equals(ext) || 
					"aux".equals(ext) || 
					"bib".equals(ext) || 
					"tex".equals(ext);
		return false;
	}
	public static boolean isImage(String ext){
		if(ext != null)
			return "png".equals(ext) || 
					"gif".equals(ext) || 
					"jpeg".equals(ext) || 
					"jpg".equals(ext);
		return false;
	}
	public static boolean isTex(String ext){
		if(ext != null)
			return "tex".equals(ext);
		return false;
	}
}
