package com.alarmForce.alarmclock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;

// this class updates the alarm lists and transfers inactive alarms from 
//active alarms list to inactive alarms list.
// also deletes users existing alarms that are displayed on the home page
public class AlarmListHandler implements Runnable {
	
	AlarmSharedPreferences mSharedPrefs;
	public Context mCtx;
	
	private String mPrefsKey;
	private String mTimeToRemove;
	private Set<String> mAlarmSet;
	private ArrayList<String> mAlarmList;
	
	AlarmListHandler(Context ctx, String prefsKey, String alarm){
		this.mCtx = ctx;
		this.mPrefsKey = prefsKey;
		this.mTimeToRemove = alarm;
		mSharedPrefs = new AlarmSharedPreferences(ctx);
	}

	@Override
	public void run() {
		if(mPrefsKey.equals("ACTIVE_ALARMS")){
			mAlarmSet = mSharedPrefs.getActiveAlarms();
			handleActiveAlarm(mAlarmSet);			
		}else if(mPrefsKey.equals("INACTIVE_ALARMS")){
			handleInactiveAlarm(mTimeToRemove);
		}else if(mPrefsKey.equals("FAVOURITE_ALARMS")){
			mAlarmSet = mSharedPrefs.getFavouriteAlarms();
			handleFavouriteAlarms(mAlarmSet);
		}else if(mPrefsKey.equals("DUPLICATES")){
			checkForDuplicates();
		}
	}
	
	private void handleFavouriteAlarms(Set<String> mAlarmSet){
		mAlarmList = new ArrayList<String>();
		
		if(mAlarmList != null){
			// add the favorite alarms set to the list
			mAlarmList.addAll(mAlarmSet);
			
			
			if(mAlarmList.contains(mTimeToRemove)){
				// remove the time from the list
				mAlarmList.remove(mTimeToRemove);
				// clear favorite alarms in AlarmSharedPrefs.java
				mSharedPrefs.removableValue(mPrefsKey);
			}
			
			for(String s: mAlarmList){
				// add all values minus the removed one
				mSharedPrefs.saveStringSet(mPrefsKey, s);
			}
		}
	}
	
	private void handleActiveAlarm(Set<String> time){
		boolean removable = false;
		String removableValue = null;
		if(time != null){
			
			mAlarmList = new ArrayList<String>();
			// add active alarm set to list
		    mAlarmList.addAll(time);
		    
		    // convert time to remove to simple format
		    String mToRemove = getTime(Long.valueOf(mTimeToRemove), "HH:mm");
		    for(String s: mAlarmList){
		    	// convert active time to simple format
		    	// reason to use simple time instead of comparing millies is because millies values varied slightly
		    	String mActiveTime = getTime(Long.valueOf(s), "HH:mm");
		    	if(mActiveTime.equals(mToRemove)){
		    		// add the cancelled alarm to inactive alarms
		    		addToInactiveAlarms();
		    		// set the removable value
		    		removableValue=s;
		    		removable = true;
		    		// clear the active alarms in AlarmSharedPrefs.java
		    		mSharedPrefs.removableValue(mPrefsKey);
		    	}		    	
			}
		    
		    if(removable == true){
		    	// remove the removable value
		    	mAlarmList.remove(removableValue);
		    	for(String s: mAlarmList){
					// add all values minus the removed one
					mSharedPrefs.saveStringSet(mPrefsKey, s);
				}
		    }
		}		
	}
	
	// delete from inactive alarms
	private void handleInactiveAlarm(String time){
		// get all inactive alarms
		ArrayList<String> inactiveAlarmList = mSharedPrefs.getInactiveAlarms();

		if(inactiveAlarmList != null){	
			for(int i=0; i<inactiveAlarmList.size(); i++){
				// delete all inactive alarms in AlarmSharedPrefs.java
				mSharedPrefs.removableValue("INACTIVE_ALARM" + i);
			}	
				if(inactiveAlarmList.contains(time)){
					// remove the value
					inactiveAlarmList.remove(time);
			}
			
			// store inactive alarms
			for(int i=0; i<inactiveAlarmList.size(); i++){
				mSharedPrefs.save("INACTIVE_ALARM" + i, inactiveAlarmList.get(i));
			}
			// store the list size so it can be used in a for loop to retrieve all inactive alarms
			mSharedPrefs.save("INACTIVE_ALARM_LIST_SIZE", String.valueOf(inactiveAlarmList.size()));
		}		
	}
	
	// add the inactive alarm
	private void addToInactiveAlarms(){
		ArrayList<String> inactiveAlarmList = mSharedPrefs.getInactiveAlarms();
		long mTime = Long.valueOf(mTimeToRemove);
		int mHour = Integer.valueOf(getTime(mTime, "HH"));
		int mMinute = Integer.valueOf(getTime(mTime, "mm"));
		String mTimeToAdd = String.valueOf((mHour * 60) + mMinute);
		String mSimpleTime = getTime(mTime, "HH:mm");
		boolean mAlarmExists = false;
		
		// check if inactive alarm exists to stop saving duplicates
		if(inactiveAlarmList != null){
			for(String s: inactiveAlarmList){
				long time = Long.valueOf(s);
				String simpleTime = getTime(time, "HH:mm");
				if(simpleTime.equals(mSimpleTime)){
					mAlarmExists = true;
				}
			}
		}
		
		if(mAlarmExists == false){
			// limit the amount of inactive alarms saved to 10
			if(inactiveAlarmList.size() >= 10){
				inactiveAlarmList.remove(0);
			}
			// add inactive alarm
			inactiveAlarmList.add(mTimeToAdd);
			for(int i=0; i<inactiveAlarmList.size(); i++){
				mSharedPrefs.save("INACTIVE_ALARM" + i, inactiveAlarmList.get(i));
			}
			mSharedPrefs.save("INACTIVE_ALARM_LIST_SIZE", String.valueOf(inactiveAlarmList.size()));
		}
	}
	
	// check for duplicates when the alarm is set
	private void checkForDuplicates(){
	    int mHour = Integer.valueOf(getTime(Long.valueOf(mTimeToRemove), "HH"));
		int mMinute = Integer.valueOf(getTime(Long.valueOf(mTimeToRemove), "mm"));
		String mTimeToCheck = String.valueOf((mHour * 60) + mMinute);
	    ArrayList<String> inactiveAlarmList = mSharedPrefs.getInactiveAlarms();
	    
		if(inactiveAlarmList != null){		
			for(String s: inactiveAlarmList){
				if(s.equals(mTimeToCheck)){
					handleInactiveAlarm(mTimeToCheck);
				}
			}		    
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getTime(long milliSeconds, String clockFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(clockFormat); 
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        calendar.getTime();
        return formatter.format(calendar.getTime());
    }
}
	
	

