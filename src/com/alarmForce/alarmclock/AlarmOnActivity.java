package com.alarmForce.alarmclock;

import com.ontimealarm.alarmclock.R;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

public class AlarmOnActivity extends FragmentActivity{
	
	//private Handler mHandler = new Handler();
	private ImageButton mLogo;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_on_activity);	

		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_action_bar, null);
		
		mLogo = (ImageButton) mCustomView.findViewById(R.id.action_bar_logo);
		mLogo.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view){
				Toast.makeText(getApplicationContext(), "Please wait for alarm to stop", Toast.LENGTH_LONG).show();
			}
		});

		ImageButton mSettingsButton = (ImageButton) mCustomView.findViewById(R.id.action_bar_settings);
		mSettingsButton.setVisibility(View.GONE);
		
		ImageButton mAboutButton = (ImageButton) mCustomView.findViewById(R.id.action_bar_about);
		mAboutButton.setVisibility(View.GONE);
		
		mActionBar.setCustomView(mCustomView);	
		mActionBar.setDisplayShowCustomEnabled(true);
	}
	
	// disable the back button if the alarm is on
	@Override
	public void onBackPressed() {
		
	}
	
	// bring the view on top of lock screen
	@Override
	public void onAttachedToWindow() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
		        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
		        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}
		
	@Override
	public void onPause(){
		super.onPause();
	}
}
