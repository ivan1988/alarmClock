package com.alarmForce.alarmclock;

import com.ontimealarm.alarmclock.R;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class HomeActivity extends FragmentActivity {
	
	private String mDisclaimer;	
	public static AlarmSharedPreferences mSharedPrefs;	
	private String mDisclaimerChoice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_action_bar, null);
		
		
		mSharedPrefs = new AlarmSharedPreferences(getApplicationContext());
		mDisclaimer = getResources().getString(R.string.disclaimer);
		mDisclaimerChoice = mSharedPrefs.getDisclaimerChoice();
		
		if(mDisclaimerChoice.equals("none")){
			new AlertDialog.Builder(this)
		    .setTitle("Disclaimer")
		    .setMessage(mDisclaimer)
		    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	mSharedPrefs.save("DISCLAIMER", "true");
		        }
		     })
		    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	mSharedPrefs.save("DISCLAIMER", "false");
		        }
		     })
		    .setIcon(android.R.drawable.ic_dialog_alert)
		     .show();
		}
		
		ImageButton mSettingsButton = (ImageButton) mCustomView.findViewById(R.id.action_bar_settings);
		mSettingsButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view){
				Intent i = new Intent (HomeActivity.this, SettingsActivity.class);
				startActivity(i);
			}
		});
		
		ImageButton mAboutButton = (ImageButton) mCustomView.findViewById(R.id.action_bar_about);
		mAboutButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view){
				Intent i = new Intent (HomeActivity.this, AboutActivity.class);
				startActivity(i);
			}
		});
		
		mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);		
	}
	@Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	// handle the back button to close the alarm
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
	}
}


