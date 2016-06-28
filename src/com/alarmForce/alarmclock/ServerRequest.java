package com.alarmForce.alarmclock;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;

public class ServerRequest extends AsyncTask<String, Void, String>{
	
	String mAlarmTime;
	int mSnoozeOption;
	String mSnoozeDuration;	
	int mFavourites;
	int mVolumeAscending;
	int mVibrate;
	int mAscendingVibration;
	int mClockFormat;
	String mDistance;
		
	public ServerRequest(String mAlarmTime, int mSnoozeOption, String mSnoozeDuration, int mFavourites, 
			int mVolumeAscending, int mVibrate, int mAscendingVibration, int mClockFormat, String mDistance){
		this.mAlarmTime = mAlarmTime;
		this.mSnoozeOption = mSnoozeOption;
		this.mSnoozeDuration = mSnoozeDuration;
		this.mFavourites = mFavourites;
		this.mVolumeAscending = mVolumeAscending;
		this.mVibrate = mVibrate;		
		this.mAscendingVibration = mAscendingVibration;
		this.mClockFormat = mClockFormat;
		this.mDistance = mDistance;
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		try {
            URL url = new URL("http://192.168.1.79:8888/api.php?apikey=this_is_secret&add=1&alarmTime="+this.mAlarmTime
            		+"&snoozeOption="+this.mSnoozeOption+"&snoozeDuration="+this.mSnoozeDuration+"&favourites="+this.mFavourites+"&volumeAscending="+this.mVolumeAscending
            		+"&vibrate="+this.mVibrate+"&ascendingVibration="+this.mAscendingVibration+"&clockFormat="+this.mClockFormat+"&setDistance="+this.mDistance);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            @SuppressWarnings("unused")
			InputStream is = conn.getInputStream();  
        } catch (Exception e) {
            System.out.println("error exc: " + e);
            e.printStackTrace();
        }
		return null;
	}
}
