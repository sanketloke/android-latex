package com.androtex.user;

import com.androtex.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
				
				AlertDialog.Builder builder = new AlertDialog.Builder(act);
				builder.setMessage(text)
				.setCancelable(true)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				})
				.setNeutralButton(R.string.reportmail, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						String emailList[] = {"pokeke100@gmail.com"};

						Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setType("plain/text");
						intent.putExtra(Intent.EXTRA_EMAIL, emailList);
						intent.putExtra(Intent.EXTRA_SUBJECT, "[Androtex] Report an uncommon bug");
						intent.putExtra(Intent.EXTRA_TEXT, text != null ? text : "Hi,");
						act.startActivity(Intent.createChooser(intent, "Send me a mail!"));
						dialog.dismiss();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}
}
