package com.androtex.actionbar;

import android.support.v4.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ActionBar.Tab;


public class TabListenerVoid implements ActionBar.TabListener {
	private Fragment _frag;
	
	public TabListenerVoid(Fragment fragment) {
		_frag = fragment;
	}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

}
