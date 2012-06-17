package com.androtex.pager;

import com.androtex.ActivityProjectList;
import com.androtex.R;
import com.androtex.format.Valid;
import com.androtex.login.CreateLogin;
import com.androtex.login.Login;
import com.androtex.user.MessagingService;
import com.androtex.viewpagerindicator.TitleProvider;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ProjectPagerAdapter
extends PagerAdapter
implements TitleProvider{

	private String [] _titles;

	private final ActivityProjectList _activity_main;


	public ProjectPagerAdapter(ActivityProjectList context)
	{
		_activity_main = context;
		_titles = new String[]{
				_activity_main.getString(R.string.projectcreate),
				_activity_main.getString(R.string.projectlist)};
	}

	@Override
	public void destroyItem(View pager, int position, Object view) {
		try{
			((ViewPager)pager).removeView((View)view);
		}catch(Exception e){
			
		}
	}

	@Override
	public void finishUpdate(View container) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCount() {
		return _titles.length;
	}

	@Override
	public Object instantiateItem(View pager, int position) {
		View v = null;

		if(position == 0){
			LayoutInflater inflater = (LayoutInflater) _activity_main
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.projectadd, null);
			_activity_main.setActivityProjectComponents((EditText)v.findViewById(R.projectlist.add),
					(Button)v.findViewById(R.projectlist.validadd));
		}else{
			LayoutInflater inflater = (LayoutInflater) _activity_main
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.projectlisting, null);
			_activity_main.setActivityProjectComponents((ListView)v.findViewById(R.projectlist.list));
		}
		//TextView v = new TextView(_activity_main);
		//v.setText(_titles[position]);
		((ViewPager)pager).addView( v, 0 );
		return v;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
		// TODO Auto-generated method stub

	}

	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startUpdate(View container) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTitle(int position) {
		return _titles[position];
	}

}
