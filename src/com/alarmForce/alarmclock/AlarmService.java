package com.alarmForce.alarmclock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

public class AlarmService extends IntentService {
	
	public static final String CREATE = "CREATE";
	public static final String RECREATE = "RECREATE";
	public static final String CANCEL = "CANCEL";
	public static final String SNOOZE = "SNOOZE";
	
	AlarmSharedPreferences mSharedPrefs;
	
	private long mActiveAlarmTime;
	private Set<String> mActiveAlarms;
	private ArrayList<String> mActiveAlarmList;
	
	private long mCurTime;
	
	private Handler mHandler = new Handler();
	
	public Calendar cal = Calendar.getInstance();
	
	private IntentFilter matcher;
	
	private int mIsFavourite;
	
	private String mDisclaimerChoice;
	
	public AlarmService(){
		super(null);
		matcher = new IntentFilter();
		matcher.addAction(CANCEL);
		matcher.addAction(CREATE);
		matcher.addAction(RECREATE);
		matcher.addAction(SNOOZE);
	}
		
	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		long newTime = intent.getLongExtra("newAlarmTime", 0);
		long cancelTime = intent.getLongExtra("cancelTime", 0);
		if(matcher.matchAction(action)){
			execute(action, newTime, cancelTime);
		}
	}

	private void execute(String action, long newAlarmTime, long cancelTime){
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		mSharedPrefs = new AlarmSharedPreferences(getApplicationContext());
		mDisclaimerChoice = mSharedPrefs.getDisclaimerChoice();
		Intent i = new Intent(this, AlarmReciever.class);
		PendingIntent pi;
		int alarmManagerId;
		
		
		// called if the phone is rebooted to reset all active alarms
		if(RECREATE.equals(action)){		
			mActiveAlarms = mSharedPrefs.getActiveAlarms();
			mActiveAlarmList = new ArrayList<String>();
			if(mActiveAlarms != null){
				mActiveAlarmList.addAll(mActiveAlarms);
			   	for(String s : mActiveAlarmList){
			   		mActiveAlarmTime = Long.valueOf(s);
			   		mCurTime = System.currentTimeMillis();
			   		String mHour = getTime(mActiveAlarmTime, "HH");
					String mMinute = getTime(mActiveAlarmTime, "mm");
					alarmManagerId = Integer.valueOf(mHour.concat(mMinute));
					// on reboot check if active alarm has expired and delete it
				   	if(mActiveAlarmTime < mCurTime){				   		
				   		mHandler.post(new AlarmListHandler(getApplicationContext(),"ACTIVE_ALARMS", String.valueOf(alarmManagerId)));
				   		// if not set it
			   		}else{	    				
						i.putExtra("id", String.valueOf(alarmManagerId));
						i.putExtra("alarmTime", mActiveAlarmTime);
						pi = PendingIntent.getBroadcast(this, alarmManagerId, i, PendingIntent.FLAG_UPDATE_CURRENT);
						am.set(AlarmManager.RTC_WAKEUP, mActiveAlarmTime, pi);
			   		}
			   	}
			}
		}else if(CREATE.equals(action)){
			create(am, i, newAlarmTime);
		}else if(CANCEL.equals(action)){
			String mHour = getTime(cancelTime, "HH");
			String mMinute = getTime(cancelTime, "mm");
			alarmManagerId = Integer.valueOf(mHour.concat(mMinute));
			mHandler.post(new AlarmListHandler(getApplicationContext(),"ACTIVE_ALARMS", String.valueOf(cancelTime)));
			pi = PendingIntent.getBroadcast(this, alarmManagerId, i, PendingIntent.FLAG_UPDATE_CURRENT);
			am.cancel(pi);
		}else if(SNOOZE.equals(action)){
			String snoozeDuration = mSharedPrefs.getSnoozeDuration();
			long snoozeTime = System.currentTimeMillis() + (Long.valueOf(snoozeDuration)*60000);
			String mHour = getTime(snoozeTime, "HH");
			String mMinute = getTime(snoozeTime, "mm");
			alarmManagerId = Integer.valueOf(mHour.concat(mMinute));
			i.putExtra("id", "snoozeAlarm");
			i.putExtra("alarmTime", snoozeTime);
			pi = PendingIntent.getBroadcast(this, alarmManagerId, i, PendingIntent.FLAG_UPDATE_CURRENT);
			am.set(AlarmManager.RTC_WAKEUP, snoozeTime, pi);
		}
	}
	
	private void create(AlarmManager am, Intent i, long time){
		ArrayList<String> mActiveAlarmList;
		Set<String> mActiveAlarmSet = mSharedPrefs.getActiveAlarms();
		String mSimpleTimeToAdd = getTime(time, "HH:mm");
		boolean mAlarmExists = false;
		
		// check if the alarm exists and --
		mActiveAlarmList = new ArrayList<String>();
		if(mActiveAlarmSet != null){
			mActiveAlarmList.addAll(mActiveAlarmSet);
			
			for(String s: mActiveAlarmList){
				long Activetime = Long.valueOf(s);
				String simpleTime = getTime(Activetime, "HH:mm");
				if(simpleTime.equals(mSimpleTimeToAdd)){
					mAlarmExists = true;
				}
			}
		}
		// -- if not add it
		if(mAlarmExists == false){
			String mHour = getTime(time, "HH");
			String mMinute = getTime(time, "mm");
			int id = Integer.valueOf(mHour.concat(mMinute));				
			i.putExtra("id", String.valueOf(id));
			i.putExtra("alarmTime", time);
			if(mDisclaimerChoice.equals("true")){
				sendToServer(mSimpleTimeToAdd);
			}
			PendingIntent pi = PendingIntent.getBroadcast(this, id, i, PendingIntent.FLAG_UPDATE_CURRENT);
			mSharedPrefs.saveStringSet("ACTIVE_ALARMS", String.valueOf(time));
			am.set(AlarmManager.RTC_WAKEUP, time, pi);
		}
	}
	
	private void sendToServer(String time){		
		alarmIsFavourite(time);
		
		int mSnoozeOption;
		String mSnoozeDuration;	
		int mFavourites;
		int mVolumeAscending;
		int mVibrate;
		int mAscendingVibration;
		int mClockFormat;
		String mDistance;
		
		
		String snoozeOption = mSharedPrefs.getSnoozePref();
		if(snoozeOption.equals("true")){ mSnoozeOption = 1;}else{ mSnoozeOption = 0;}
		mSnoozeDuration = mSharedPrefs.getSnoozeDuration();	
		mFavourites = mIsFavourite;
		String volumeAscending = mSharedPrefs.getAscendingPref();
		if(volumeAscending.equals("true")){ mVolumeAscending = 1;}else{ mVolumeAscending = 0;}
		String vibrate = mSharedPrefs.getVibratePref();
		if(vibrate.equals("true")){ mVibrate = 1;}else{ mVibrate = 0;}
		String ascendingVibration = mSharedPrefs.getVibrateFirst();
		if(ascendingVibration.equals("true")){ mAscendingVibration = 1;}else{ mAscendingVibration = 0;}		
		String clockFormat = mSharedPrefs.getClockFormat();
		if(clockFormat.equals("true")){ mClockFormat = 1;}else{ mClockFormat = 0;}
		mDistance = mSharedPrefs.getSetDistance();
		
		ServerRequest server = new ServerRequest (time,  mSnoozeOption, mSnoozeDuration, mFavourites, mVolumeAscending, mVibrate, mAscendingVibration, mClockFormat, mDistance);
		server.execute();
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
	
	// check if alarm is in favorites
	private int alarmIsFavourite(String time){	
		ArrayList<String> mFavouriteList = new ArrayList<String>();;
		Set<String> mFavouriteSet = mSharedPrefs.getFavouriteAlarms();
		if(mFavouriteSet != null){
			mFavouriteList.addAll(mFavouriteSet);
			for(String s: mFavouriteList){
				if(s.equals(time)){
					mIsFavourite = 1;
				}else{
					mIsFavourite = 0;
				}
			}
		}
		
		return mIsFavourite;
	}
}
