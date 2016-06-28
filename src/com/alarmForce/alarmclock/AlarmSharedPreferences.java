package com.alarmForce.alarmclock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AlarmSharedPreferences {	
	public static final String PREFS_NAME = "APP_PREFS";

	public static final String ACTIVE_ALARMS = "ACTIVE_ALARMS";
	public static final String SNOOZE_ALARM_ID = "SNOOZE_ALARM_ID";
	public static final String ALARM_RESET = "ALARM_RESET";
	
	public static final String INACTIVE_ALARM = "INACTIVE_ALARM";
	public static final String INACTIVE_ALARM_LIST_SIZE = "INACTIVE_ALARM_LIST_SIZE";
	public static final String FAVOURITE_ALARMS = "FAVOURITE_ALARMS";
	
	public static final String CLOCK_FORMAT = "CLOCK_FORMAT";
	public static final String SET_DISTANCE = "SET_DISTANCE";
	public static final String VOLUME_ASCENDING = "VOLUME_ASCENDING";
	public static final String ALARM_VOLUME = "ALARM_VOLUME";
	public static final String ALARM_RINGTONE = "ALARM_RINGTONE";
	public static final String ALARM_VIBRATE = "ALARM_VIBRATE";
	public static final String VIBRATE_FIRST = "VIBRATE_FIRST";
	public static final String SNOOZE_SETTINGS = "SNOOZE_SETTINGS";
	public static final String SNOOZE_DURATION = "SNOOZE_DURATION";
	
	public static final String DISCLAIMER = "DISCLAIMER";
	
	//private Set<String> mDefaultActiveAlarm = new HashSet<String>(){{ add("0");}};

	private String mDefaultSnoozeAlarmId = "0";
	private String mDefaultInactiveAlarmSize = "0";
	private String mDefaultAlarmReset = "false";
	
	private String mDefaultClockFormat = "false";
	private String mDefaultDistance = "21";
	private String mDefaultAscending = "true";
	private String mDefaultAlarmVolume = "7";
	private String mDefaultRingtone = "";
	private String mDefaultVibrate = "true";
	private String mDefaultVibrateFirst = "true";
	private String mDefaultSnoozeSet = "true";
	private String mDefaultSnoozeDuration = "1";
	
	private String mDefaultDisclaimer = "none";
	
	public Context mCtx;
	
	public AlarmSharedPreferences(Context ctx){
		super();
		mCtx = ctx;
	}
		
	public Set<String> getFavouriteAlarms(){
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Set<String> stringSet = sharedPref.getStringSet(FAVOURITE_ALARMS, null);
		return stringSet;
	}
	
	public Set<String> getActiveAlarms(){
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Set<String> stringSet = sharedPref.getStringSet(ACTIVE_ALARMS, null);
			// I had the default value set but instead check if the value is null within the fragments
			// due to default value showing on home screen and on reboot setting the alarm to 1am
				//== null ? mDefaultActiveAlarm : sharedPref.getStringSet(ACTIVE_ALARMS, null); 
		return stringSet;
	}
	
	
	public ArrayList<String> getInactiveAlarms() {
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String size = sharedPref.getString(INACTIVE_ALARM_LIST_SIZE, null) == null ? mDefaultInactiveAlarmSize : sharedPref.getString(INACTIVE_ALARM_LIST_SIZE, null);
		int s = Integer.valueOf(size);
		ArrayList<String> list = new ArrayList<String>();
		for(int i=0;i<s;i++){
			list.add(sharedPref.getString(INACTIVE_ALARM + i, null));
		}
		return list;
	}

	public String getSnoozeAlarmId() {
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String string = sharedPref.getString(SNOOZE_ALARM_ID, null) == null ? mDefaultSnoozeAlarmId : sharedPref.getString(SNOOZE_ALARM_ID, null);
		return string;
	}
	
	public String getDisclaimerChoice() {
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String string = sharedPref.getString(DISCLAIMER, null) == null ? mDefaultDisclaimer : sharedPref.getString(DISCLAIMER, null);
		return string;
	}
	
	public String getAlarmReset() {
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String string = sharedPref.getString(ALARM_RESET, null) == null ? mDefaultAlarmReset : sharedPref.getString(ALARM_RESET, null);
		return string;
	}
	
	public String getClockFormat() {
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String string = sharedPref.getString(CLOCK_FORMAT, null) == null ? mDefaultClockFormat : sharedPref.getString(CLOCK_FORMAT, null);
		return string;
	}
	
	public String getSetDistance() {
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String string = sharedPref.getString(SET_DISTANCE, null) == null ? mDefaultDistance : sharedPref.getString(SET_DISTANCE, null);
		return string;
	}
	
	public String getAscendingPref() {
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String string = sharedPref.getString(VOLUME_ASCENDING, null) == null ? mDefaultAscending : sharedPref.getString(VOLUME_ASCENDING, null);
		return string;
	}
	
	public String getAlarmVolume() {
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String string = sharedPref.getString(ALARM_VOLUME, null) == null ? mDefaultAlarmVolume : sharedPref.getString(ALARM_VOLUME, null);
		return string;
	}
	
	public String getAlarmRingtone() {
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String string = sharedPref.getString(ALARM_RINGTONE, null) == null ? mDefaultRingtone : sharedPref.getString(ALARM_RINGTONE, null);
		return string;
	}
	
	public String getVibratePref() {
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String string = sharedPref.getString(ALARM_VIBRATE, null) == null ? mDefaultVibrate : sharedPref.getString(ALARM_VIBRATE, null);
		return string;
	}
	
	public String getVibrateFirst() {
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String string = sharedPref.getString(VIBRATE_FIRST, null) == null ? mDefaultVibrateFirst : sharedPref.getString(VIBRATE_FIRST, null);
		return string;
	}
	
	public String getSnoozePref() {
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String string = sharedPref.getString(SNOOZE_SETTINGS, null) == null ? mDefaultSnoozeSet : sharedPref.getString(SNOOZE_SETTINGS, null);
		return string;
	}
	
	public String getSnoozeDuration() {
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String string = sharedPref.getString(SNOOZE_DURATION, null) == null ? mDefaultSnoozeDuration : sharedPref.getString(SNOOZE_DURATION, null);
		return string;
	}
	
	public void save( String prefsKey, String string ) {
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPref.edit();
		editor.putString(prefsKey, string);
		editor.commit();
	}
	
	public void saveStringSet(String prefsKey, String string ){
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPref.edit();
		
		Set<String> sharedStr = new HashSet<String>(sharedPref.getStringSet(prefsKey , new HashSet<String>()));
		sharedStr.add(string);
				
		editor.putStringSet(prefsKey, sharedStr);
		editor.commit();
	}
	
	public void clearPrefs(){
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPref.edit();
		
		editor.clear();
		editor.commit();
	}
	
	public void removableValue(String prefsKey){
		SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPref.edit();
		
		editor.remove(prefsKey);
		editor.commit();
	}	
}