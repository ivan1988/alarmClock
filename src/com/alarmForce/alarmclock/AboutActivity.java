package com.alarmForce.alarmclock;

import com.ontimealarm.alarmclock.R;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class AboutActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		
		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_action_bar, null);
		
		ImageButton mLogo = (ImageButton) mCustomView.findViewById(R.id.action_bar_logo);
		mLogo.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view){
				Intent i = new Intent (AboutActivity.this, HomeActivity.class);
				startActivity(i);
			}
		});

		ImageButton mSettingsButton = (ImageButton) mCustomView.findViewById(R.id.action_bar_settings);
		mSettingsButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view){
				Intent i = new Intent (AboutActivity.this, SettingsActivity.class);
				startActivity(i);
			}
		});
		
		mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
	}
	
	// handle the back button
	@Override
	public void onBackPressed() {
		Intent i = new Intent (AboutActivity.this, HomeActivity.class);
		startActivity(i);
	return;
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
}
