package com.videoclient;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.util.AndroidConstants;
import com.util.HttpView;
import com.util.StringHelper;

public class CommonActivity extends Activity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.optionsmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent main = null;
		switch (item.getItemId()) {
		case R.id.itemChangeCamera: {
			String string = HttpView.connect2Server(AndroidConstants.MAIN_URL()
					+ "method=getCameraList");
			String[] array_spinner = new String[HttpView.count + 1];
			System.out.println(HttpView.count + 1);

			for (int i = 0; i < HttpView.count + 1; i++) {
				string = HttpView.result[i][0].trim();
				array_spinner[i] = string;
			}
			showPopupList(array_spinner,"Select Camera",1);
		}
			break;
		case R.id.itemSettings:
			main = new Intent(CommonActivity.this, ConfigTabActivity.class);
			startActivity(main);
			break;
		case R.id.itemRotate:  
			dialog();
			break;
		case R.id.itemChangeResolution:
		{
			String string = HttpView.connect2Server(AndroidConstants.MAIN_URL()
					+ "method=getQuality");
			String[] array_spinner = new String[HttpView.count + 1];
			System.out.println(HttpView.count + 1);

			for (int i = 0; i < HttpView.count + 1; i++) {
				string = HttpView.result[i][0].trim();
				array_spinner[i] = string;
			}
			showPopupList(array_spinner,"Change Quality",4);
		}
			break;
		case R.id.itemChangeMode:
		{
			String[] bases = getResources().getStringArray(R.array.modearray);
			showPopupList(bases, "Select Mode", 2);
		}

			break;
		case R.id.itemPlayAlarm:
		{
			String string = HttpView.connect2Server(AndroidConstants.MAIN_URL()
					+ "method=playAlarm");
			break;
		}
		case R.id.itemExit:

			finished();
			break;
		case R.id.itemEmergencyContacts:

			getEmergencyContact();
			break;
		}
		return true;
	}
	
	private void dialog() {
		// TODO Auto-generated method stub
		dialog = new Dialog(CommonActivity.this);
		dialog.setContentView(R.layout.rotatecamera);
		dialog.setTitle("Enter Rotation");

	
		Button b1 = (Button) dialog.findViewById(R.id.button1);
		b1.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				HttpView.connect2Server(HttpView.curl("rotate",
						new String[] { "r=-1" }));	// Left
			}
		});
		Button b2 = (Button) dialog.findViewById(R.id.button2);
		b2.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				HttpView.connect2Server(HttpView.curl("rotate",
						new String[] { "r=1" }));	// Right
			}
		});
		final Button b3 = (Button) dialog.findViewById(R.id.button3);
		b3.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if(b3.getText().toString().startsWith("Enable")){
					b3.setText("Disable Face Tracking");
					HttpView.connect2Server(HttpView.curl("faceTracking",
						new String[] { "r=1" }));	// ON
				}else if(b3.getText().toString().startsWith("Disable")){
					b3.setText("Enable Face Tracking");
					HttpView.connect2Server(HttpView.curl("faceTracking",
							new String[] { "r=0" }));	// Off
					}
			}
		});

		dialog.show();
	}
	public static int progressActual=0;
	public void getEmergencyContact(){
		HttpView.connect2Server(AndroidConstants.MAIN_URL()
				+ "method=getEmergencyContact");
		String[] array_spinner = new String[HttpView.count + 1];
		for (int i = 0; i < HttpView.count + 1; i++) {
			String string = HttpView.result[i][0].trim();
			String phone = HttpView.result[i][1].trim();
			array_spinner[i] = string+"-"+phone;
		}
		showPopupList(array_spinner, "Emergency Contact List", 3);
		
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView) v;
		view.setScaleType(ImageView.ScaleType.MATRIX);
		float scale;

		// Dump touch event to log
		// dumpEvent(event);

		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_DOWN: // first finger down only
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			Log.d(TAG, "mode=DRAG");
			mode = DRAG;
			break;
		case MotionEvent.ACTION_UP: // first finger lifted
		case MotionEvent.ACTION_POINTER_UP: // second finger lifted
			mode = NONE;
			Log.d(TAG, "mode=NONE");
			break;
		case MotionEvent.ACTION_POINTER_DOWN: // second finger down
			oldDist = spacing(event);
			Log.d(TAG, "oldDist=" + oldDist);
			if (oldDist > 5f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				Log.d(TAG, "mode=ZOOM");
			}
			break;

		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) { // movement of first finger
				matrix.set(savedMatrix);
				if (view.getLeft() >= -392) {
					matrix.postTranslate(event.getX() - start.x, event.getY()
							- start.y);
				}
			} else if (mode == ZOOM) { // pinch zooming
				float newDist = spacing(event);
				Log.d(TAG, "newDist=" + newDist);
				if (newDist > 5f) {
					matrix.set(savedMatrix);
					scale = newDist / oldDist; 
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}
		// Perform the transformation
		view.setImageMatrix(matrix);

		return true; // indicate event was handled
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}


	private void showPopupList(String[] array_spinner, final String title,
			final int mode) {
		final Dialog viewDialog = new Dialog(this);
		viewDialog.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		viewDialog.setTitle(title);

		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = li.inflate(R.layout.change, null);
		viewDialog.setContentView(dialogView);
		viewDialog.show();
		final ListView spinnercategory = (ListView) dialogView
				.findViewById(R.id.listView1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, array_spinner);
		adapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
		spinnercategory.setAdapter(adapter);
		spinnercategory
				.setOnItemClickListener(new ListView.OnItemClickListener() {
					
					public void onItemClick(AdapterView<?> ad, View view,
							int position, long duration) {
						System.out.println("Selected " + position);
						toast("Selected " + position);
						viewDialog.hide();
						if (mode == 1) {
							String string = HttpView
									.connect2Server(AndroidConstants.MAIN_URL()
											+ "method=launchCamera&camerno="
											+ position);
						} else if (mode == 2) {
							boolean success = false;
							if (position == 0) {
								success = true;
							} else {
								success = false;
							}
							String string = HttpView
									.connect2Server(AndroidConstants.MAIN_URL()
											+ "method=changemode&mode="
											+ success);
						}else if (mode == 3) {
							try{
								
								
							String details=StringHelper.n2s(ad.getItemAtPosition(position));
							System.out.println("Selected is "+details);	
							String phone=details.substring(details.indexOf("-")+1);
							makeCall(phone);
							}catch (Exception e) {

							}
						}else if (mode == 4) {
							try{
								
								
							String details=StringHelper.n2s(ad.getItemAtPosition(position));
							System.out.println("Selected is "+details);	
							String string = HttpView
									.connect2Server(AndroidConstants.MAIN_URL()
											+ "method=changequality&quality="+ position);
							}catch (Exception e) {

							}
						}
					}

				});
	}

    private void makeCall(String phone) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+phone));
            startActivity(callIntent);
        } catch (ActivityNotFoundException e) {
            Log.e("helloandroid dialing example", "Call failed", e);
        }
    }

	ProgressDialog progressDialog;
	AlertDialog alertDialog;

	class CheckConnectivityAsyncTask extends AsyncTask<String, String, String> {
		String message = "";
		String title = "";
		String action = "";

		@Override
		protected void onPreExecute() {
			System.out.println("In Aysnc");
			progressDialog = ProgressDialog.show(CommonActivity.this,
					"Please Wait", "Loading....", true);
			alertDialog = new AlertDialog.Builder(CommonActivity.this).create();
		}

		@Override
		protected String doInBackground(String... params) {
			String ip = params[0];
			int port = StringHelper.n2i(params[1]);
			boolean success = HttpView.checkConnectivityServer(ip, port);

			if (success) {

				title = "Success";
				if (params.length > 2 && params[2].equalsIgnoreCase("UpdateIp")) {
					action = "1";
					message = "Connection established with the Main Server.";
					AndroidConstants.MAIN_SERVER_IP = ip;
					AndroidConstants.MAIN_SERVER_PORT = port + "";
				} else {
					message = "Internet Connection Successful!";
				}
			} else {
				action = "";
				message = "Error Connecting to Server http://" + ip + ":"
						+ port;
				title = "Connectivity Error";
			}

			return success + "";
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			alertDialog.setTitle(title);
			alertDialog.setMessage(message);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					alertDialog.hide();
					if (action.length() > 0) {
						Intent main = new Intent(CommonActivity.this,
								WelcomeActivity.class);
						startActivity(main);
					}

				}
			});
			alertDialog.show();

		};

	}

	public void finished() {
		try {
			System.runFinalizersOnExit(true);
			finish();
			super.finish();
			super.onDestroy();
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			android.os.Process.killProcess(android.os.Process.myPid());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void toast(String message) {
		Toast t = Toast.makeText(CommonActivity.this, message, 1000);
		t.show();
	}

	public String getIMEI() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		System.out.println("Device IMEI is " + imei);
		return imei;

	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						String ip = inetAddress.getHostAddress().toString();
						if (ip.startsWith("10.0.2."))
							return "127.0.0.1";
						else
							return ip;
					}
				}
			}
		} catch (Exception ex) {
			Log.e("HttpView", ex.toString());
		}
		return null;
	}
	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;
	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	String TAG = "PopupMenu";
	int pos = 9;
	Dialog dialog;
}
