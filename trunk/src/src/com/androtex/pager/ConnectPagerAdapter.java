package com.androtex.pager;

import com.androtex.ActivityMain;
import com.androtex.R;
import com.androtex.format.Valid;
import com.androtex.login.CreateLogin;
import com.androtex.login.Login;
import com.androtex.user.MessagingService;
import com.viewpagerindicator.TitleProvider;

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
import android.widget.TextView;

public class ConnectPagerAdapter
extends PagerAdapter
implements TitleProvider{

	private String [] _titles;

	private final ActivityMain _activity_main;


	public ConnectPagerAdapter(ActivityMain context)
	{
		_activity_main = context;
		_titles = new String[]{
				_activity_main.getString(R.string.connection),
				_activity_main.getString(R.string.createaccount)};
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
		Log.d("information"," "+position);
		if(position == 0){
			LayoutInflater inflater = (LayoutInflater) _activity_main
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.loginconnect, null);
			
			final EditText _login_login = (EditText) v.findViewById(R.login.username);
			final EditText _login_password = (EditText) v.findViewById(R.login.password);
			final Button _login = (Button) v.findViewById(R.login.valid);
			_login.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (_activity_main.getLoginAction() == null) {
						if (Valid.validString(_login_login.getText().toString())
								&& Valid.validString(_login_password.getText()
										.toString())) {
							_activity_main.setLoginAction(new Login(_activity_main,
									_activity_main, _login_login.getText()
									.toString(), _login_password.getText()
									.toString()));
							_activity_main.computeAndCreateProgress();
						} else {
							MessagingService.getInstance().makeMessage(
									_activity_main,
									_activity_main.getString(R.string.patternwrong),
									MessagingService.MESSAGE_LONG);
						}
					}
				}
			});
		}else{
			LayoutInflater inflater = (LayoutInflater) _activity_main
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.logincreate, null);
			

			final EditText _login_new_login = (EditText)v.findViewById(R.loginnew.username);
			final EditText _login_new_password = (EditText)v.findViewById(R.loginnew.password);
			final Button _login_new = (Button)v.findViewById(R.loginnew.valid);

			_login_new.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (_activity_main.getLoginAction() == null) {
						if (Valid.validString(_login_new_login.getText().toString())
								&& Valid.validString(_login_new_password.getText()
										.toString())) {
							_activity_main.setLoginAction(new CreateLogin(_activity_main,
									_activity_main, _login_new_login.getText()
									.toString(), _login_new_password
									.getText().toString()));
							_activity_main.computeAndCreateProgress();
						} else {
							MessagingService.getInstance().makeMessage(
									_activity_main,
									_activity_main.getString(R.string.patternwrong),
									MessagingService.MESSAGE_LONG);
						}
					}
				}
			});
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
