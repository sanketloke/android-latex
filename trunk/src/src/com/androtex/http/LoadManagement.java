package com.androtex.http;

import android.content.Context;

/**
 * Class Visible by client package
 * 
 * option wich come with it : directly call begin() and end() to be able to use
 * isFinished()
 * 
 * @author Kevin Le Perf
 * 
 */
public abstract class LoadManagement extends LoaderElement {
	public LoadManagement(Context context) {
		super(context);
	}

	public abstract void compute();

}
