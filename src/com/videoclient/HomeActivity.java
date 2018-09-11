package com.videoclient;

import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.util.AndroidConstants;
import com.util.HttpView;

public class HomeActivity extends CommonActivity implements OnTouchListener {
	Button stopbutton=null,startbutton=null;
	ImageView imageView=null;
	Timer timerRetakeScreenshot = null;
	RetakeScreenShotTimerTask task=null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	android.os.StrictMode.ThreadPolicy tp = android.os.StrictMode.ThreadPolicy.LAX;
		android.os.StrictMode.setThreadPolicy(tp);
        setTitle("Home - Remote Video Surveliance");
    	stopbutton = (Button) findViewById(R.id.stopButton);
    	startbutton = (Button) findViewById(R.id.startButton);
    	imageView = (ImageView) findViewById(R.id.imageView1);
		
		imageView.setOnTouchListener(this);
		imageView.setScaleType(ScaleType.FIT_XY);
    }
    public void fnClick(View v){
    	if(v.getId()==R.id.startButton){
    		imageView.setScaleType(ScaleType.FIT_XY);
    		stopbutton.setVisibility(Button.VISIBLE);
    		startbutton.setVisibility(Button.INVISIBLE);
    		startTimer();
    	}else if(v.getId()==R.id.stopButton){
    		imageView.setScaleType(ScaleType.FIT_XY);
    		stopTimer();
    		stopbutton.setVisibility(Button.INVISIBLE);
    		startbutton.setVisibility(Button.VISIBLE);
    	}else if(v.getId()==R.id.emergencyButton){
    		getEmergencyContact();
    	}
    }
    
    class RetakeScreenShotTimerTask extends TimerTask{
		@Override
		public void run() {
			takeScreenShot();
		}
	}
    public void takeScreenShot(){
		runOnUiThread(takeScreenShot);     // 
	}
    Runnable takeScreenShot = new Runnable() {
		public void run() {
					String url = AndroidConstants.MAIN_URL() + "method=receiveImage";
					Bitmap b = HttpView.drawable_from_url(url);
					if(b!=null){
						imageView.setImageBitmap(b); // iv is scaled and setting image
					}
				};
	};
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		stopTimer();
	}
	public void stopTimer(){
		if (timerRetakeScreenshot != null) {
			toast("Cancelling Timer");
			timerRetakeScreenshot.cancel();
			timerRetakeScreenshot = null;
			task.cancel();
			
		}
	}
	public void startTimer(){
		if (timerRetakeScreenshot == null) {
			toast("Start Timers ");
			timerRetakeScreenshot = new Timer();
			task=new RetakeScreenShotTimerTask();
			timerRetakeScreenshot.scheduleAtFixedRate(task, 10, AndroidConstants.IMAGE_CAPTURE_DELAY);
		}
	}
	@Override
	protected void onRestart() {

		super.onRestart();
		
		
	}
	
}