package com.videoclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.util.AndroidConstants;
import com.util.HttpView;
import com.util.StringHelper;

public class WelcomeActivity extends CommonActivity {
	protected int _splashTime = 2000;
	public static String TAG = "WelcomeActivity", value = "";
	AlertDialog alertDialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcomescreen);
		android.os.StrictMode.ThreadPolicy tp = android.os.StrictMode.ThreadPolicy.LAX;
		android.os.StrictMode.setThreadPolicy(tp);
		toast("Checking User IMEI ");

	}

	protected void onResume() {
		super.onResume();
		alertDialog = new AlertDialog.Builder(WelcomeActivity.this).create();
		alertDialog.setTitle("Error");
		alertDialog
				.setMessage("You are not authorized to access the application. Please contact administrator.");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.hide();
				finished();
			}
		});

		boolean success = HttpView.checkConnectivityServer(
				AndroidConstants.MAIN_SERVER_IP,
				StringHelper.n2i(AndroidConstants.MAIN_SERVER_PORT));
		if (success) {

			String userdetails = StringHelper.n2s(HttpView
					.connect2Server(HttpView.curl("checkIMEI",
							new String[] { "imei=" + getIMEI() })));
			if (userdetails.equalsIgnoreCase("true")) {
				
				Intent intent = new Intent(WelcomeActivity.this,
						HomeActivity.class);
				startActivity(intent);
			} else {
				runOnUiThread(new Runnable() {

					public void run() {
						alertDialog.show();

					}
				});
			}

		} else {
			Intent intent = new Intent(WelcomeActivity.this,
					ConfigTabActivity.class);
			intent.putExtra("MESSAGE",
					"We are unable to connect to your default server");
			startActivity(intent);
		}

	}

}
