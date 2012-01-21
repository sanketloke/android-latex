package com.androtex.user;

import android.app.Activity;
import android.widget.Toast;

public class MessagingService {

	private static MessagingService _INSTANCE;

	public static final int MESSAGE_LONG = Toast.LENGTH_LONG;
	public static final int MESSAGE_SHORT = Toast.LENGTH_SHORT;

	private MessagingService() {

	}

	public static MessagingService getInstance() {
		if (_INSTANCE == null) {
			_INSTANCE = new MessagingService();
		}
		return _INSTANCE;
	}

	public void makeMessage(final Activity act, final String text,
			final int length) {
		act.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(act.getBaseContext(), text, length).show();
			}
		});
	}
}
