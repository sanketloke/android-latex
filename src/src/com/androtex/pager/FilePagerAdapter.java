package com.androtex.pager;

import com.androtex.ActivityFileList;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FilePagerAdapter
extends PagerAdapter
implements TitleProvider{

	private String [] _titles;

	private final ActivityFileList _activity_main;


	public FilePagerAdapter(ActivityFileList context)
	{
		_activity_main = context;
		_titles = new String[]{
				_activity_main.getString(R.string.add_file),
				_activity_main.getString(R.string.filelist),
				_activity_main.getString(R.string.visualisation)};
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
			v = inflater.inflate(R.layout.fileadd, null);
			_activity_main.setActivityFileComponents(
					(Button)v.findViewById(R.filelist.add), 
					(Button)v.findViewById(R.filelist.create));
		}else if(position == 1){
			LayoutInflater inflater = (LayoutInflater) _activity_main
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.filelist, null);
			_activity_main.setActivityFileComponents((ListView)v.findViewById(R.filelist.list));
		}else{
			LayoutInflater inflater = (LayoutInflater) _activity_main
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.fileview, null);
			_activity_main.setActivityFileComponents(
					(EditText)v.findViewById(R.filelist.text),
					(ImageView)v.findViewById(R.filelist.image),
					(Button)v.findViewById(R.filelist.save),
					(Button)v.findViewById(R.filelist.compile));

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
