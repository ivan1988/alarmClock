package com.alarmForce.alarmclock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

import com.ontimealarm.alarmclock.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class HomeFragment extends Fragment{
	
	private AlarmSharedPreferences mSharedPrefs;

	private boolean mAlarmIsActive;
	private boolean mAlarmIsFavourite;
	
	private ArrayList<String> mViewFavouriteAlarms;
	private ArrayList<Long> mGetInactiveAlarms;
	private ArrayList<Long> mGetActiveAlarms;
	private ArrayList<Long> mGetAllAlarms;
	private String mSimpleTime;
	private long mGetTimeInMillies;
	
	private Handler mHandler = new Handler();
	
	private String mClockFormat;
	
//	@Override
//	public void onCreate(Bundle savedInstanceState){
//		super.onCreate(savedInstanceState);	
//	}
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.home_fragment, container, false);
		
		mSharedPrefs = new AlarmSharedPreferences(getActivity().getApplicationContext());
		mClockFormat = mSharedPrefs.getClockFormat();
		
		if(!isDeviceCompatable()){
			Toast.makeText(getActivity(), "Distance function not available", Toast.LENGTH_LONG).show();
		}
		
		alarmsViewHandler(rootView);
		return rootView;
	}
	
    public void onResume(){
    	super.onResume();
    }
	
    private boolean isDeviceCompatable(){
    	
    	int apiVersion = android.os.Build.VERSION.SDK_INT;

    	PackageManager packageManager = getActivity().getPackageManager();
    	return apiVersion >= android.os.Build.VERSION_CODES.KITKAT && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
    }
				
	private void alarmsViewHandler(View view){
		final LinearLayout Alarms = (LinearLayout) view.findViewById(R.id.setAlarmXML);
		getAllAlarms();
		getActiveAlarms();
		getFavouriteAlarms();
    	for(long l:mGetAllAlarms){
    		long mTime = l;
    		GradientDrawable border = new GradientDrawable();
    		border.setColor(Color.rgb(24, 24, 48));
    		border.setStroke(4, Color.rgb(58, 58, 58));
    		
    		LinearLayout ll = new LinearLayout(getActivity());
    		ll.setOrientation(LinearLayout.HORIZONTAL);
    		ll.setPadding(0, 20, 0, 20);
    		ll.setBackground(border);
    		
    		String mFavouriteTime = getSimpleTime(mTime);
    		if(mViewFavouriteAlarms.contains(mFavouriteTime)){
    			mAlarmIsFavourite = true;
    		}else{
    			mAlarmIsFavourite = false;
    		}
    		
    		addFavourite(ll, mFavouriteTime);

    		if(mGetActiveAlarms.contains(mTime)){
    			mAlarmIsActive = true;
    		}else{
    			mAlarmIsActive = false;
    		}

    		setTextView(ll, mTime);

    		editAlarm(ll, mTime);
    		
    		editAlarmStatus(ll, mTime, mAlarmIsActive);
    		
    		Alarms.addView(ll);
    	}
	}
	// button to add and remove from/to favourites
	private void addFavourite(LinearLayout ll, final String time){
		final ImageButton addFavourite = new ImageButton(getActivity());
		addFavourite.setPadding(20, 15, 40, 0);

		addFavourite.setImageResource(R.drawable.ic_action_important);
		if(mAlarmIsFavourite == true){
			addFavourite.setImageResource(R.drawable.ic_action_important_on);
		}
		addFavourite.setBackgroundColor(Color.TRANSPARENT);
		addFavourite.setOnClickListener(new OnClickListener(){
			boolean AlarmIsFavourite = mAlarmIsFavourite;
			public void onClick(View v){		
				if(AlarmIsFavourite == true){
					AlarmIsFavourite = false;
					mHandler.post(new AlarmListHandler(getActivity(),"FAVOURITE_ALARMS", time));
					addFavourite.setImageResource(R.drawable.ic_action_important);
				}else if(AlarmIsFavourite == false){
					AlarmIsFavourite = true;
					mSharedPrefs.saveStringSet("FAVOURITE_ALARMS", time);
					addFavourite.setImageResource(R.drawable.ic_action_important_on);
				}	
			}
		});
		ll.addView(addFavourite);
	}
	// textview of the alarm time
	private void setTextView(LinearLayout ll, long time){
		String format;
		if(mClockFormat.equals("true")){
			format = "HH:mm a";
		}else{
			format = "hh:mm a";
		}
		String mTime = getTime(time, format);
		
		TextView alarmTime = new TextView(getActivity());
		alarmTime.setText(mTime);	
		alarmTime.setPadding(45, 0, 2, 0);
		alarmTime.setTextColor(Color.rgb(255, 158, 13));
		alarmTime.setTextSize(30);
		ll.addView(alarmTime);
	}
	// edit alarm (send value to the set alarm bar)
	private void editAlarm(LinearLayout ll, long time){
		final long mTime = Long.valueOf(getSimpleTime(time));
		ImageButton editAlarm = new ImageButton(getActivity());
		editAlarm.setPadding(80, 30, 80, 0);
		editAlarm.setImageResource(R.drawable.ic_action_edit);
		editAlarm.setBackgroundColor(Color.TRANSPARENT);
		editAlarm.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent i = new Intent (getActivity().getApplicationContext(), HomeActivity.class);
				i.putExtra("time", mTime);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
		});				
		ll.addView(editAlarm);
	}
	// turn the alarm on and off
	private void editAlarmStatus( final LinearLayout ll, final long time, final boolean alarmIsSet){
		final ImageButton editAlarmStatus = new ImageButton(getActivity());

		editAlarmStatus.setImageResource(R.drawable.ic_action_alarms);
		if(alarmIsSet == true){
			editAlarmStatus.setImageResource(R.drawable.ic_action_alarm_on);
		}
		editAlarmStatus.setPadding(100, 10, 0, 0);
		editAlarmStatus.setBackgroundColor(Color.TRANSPARENT);
		editAlarmStatus.setOnClickListener(new OnClickListener(){
			boolean onOff = alarmIsSet;
			public void onClick(View v){						
				if(onOff == true){
					onOff = false;
					cancelAlarm(time);
					editAlarmStatus.setImageResource(R.drawable.ic_action_alarms);
				}else{
					onOff = true;
					setAlarm(time);
					mHandler.post(new AlarmListHandler(getActivity(),"INACTIVE_ALARMS", getSimpleTime(time)));
					editAlarmStatus.setImageResource(R.drawable.ic_action_alarm_on);		
				}
			}
		});
		ll.addView(editAlarmStatus);
	}

	private void setAlarm(long time){ 
		int simpleTime = Integer.valueOf(getSimpleTime(time));
		String mHour = String.valueOf(simpleTime / 60);
		String mMinute = String.valueOf(simpleTime % 60);
    	Calendar now = Calendar.getInstance();
		Calendar alarm = Calendar.getInstance();
		alarm.set(Calendar.HOUR_OF_DAY, Integer.valueOf(mHour));
		alarm.set(Calendar.MINUTE, Integer.valueOf(mMinute));
		alarm.set(Calendar.SECOND, 0);
		if (alarm.before(now)) alarm.add(Calendar.DAY_OF_MONTH, 1);
		long mAlarmTimeCur = alarm.getTimeInMillis();
		Intent create = new Intent(getActivity().getApplicationContext(), AlarmService.class);
		create.setAction(AlarmService.CREATE);
		create.putExtra("newAlarmTime", mAlarmTimeCur);
		getActivity().startService(create);
    }
    
    private void cancelAlarm(long time){
    	Intent cancel = new Intent(getActivity().getApplicationContext(), AlarmService.class);
		cancel.setAction(AlarmService.CANCEL);
		cancel.putExtra("cancelTime", time);
		getActivity().startService(cancel);
    }
    // get simple time to use as id and with inactive and favourite alarms
	private String getSimpleTime(Long time){
		int mHour = Integer.valueOf(getTime(time, "HH"));
		int mMinute = Integer.valueOf(getTime(time, "mm"));
		mSimpleTime = String.valueOf((mHour*60)+mMinute);
		return mSimpleTime;
	}
	// get time in milliseconds
	private long getTimeInMillis(long time){
		int mHour = (int) (time / 60);
		int mMinute = (int)(time % 60);
		
		// set to current millis for easier handling
		Calendar alarm = Calendar.getInstance();
		alarm.set(Calendar.HOUR_OF_DAY, mHour);
		alarm.set(Calendar.MINUTE, mMinute);
		alarm.set(Calendar.SECOND, 0);
		mGetTimeInMillies = alarm.getTimeInMillis();
		return mGetTimeInMillies;
	}
	// get all active alarms
	private ArrayList<Long> getActiveAlarms(){
		long mAlarmTime;
		Set<String> mActiveAlarmsSet;
		mActiveAlarmsSet = mSharedPrefs.getActiveAlarms();
		ArrayList<String> mActiveAlarms = new ArrayList<String>();
		mGetActiveAlarms = new ArrayList<Long>();	
		if(mActiveAlarmsSet != null){
			mActiveAlarms.addAll(mActiveAlarmsSet);
			for(String str:mActiveAlarms){
				mAlarmTime = Long.valueOf(str);
				mGetActiveAlarms.add(mAlarmTime);
			}
		}
		return mGetActiveAlarms;
	}
	
	// Get inactive alarms from shared prefs which are stored as mins(ie 1:24 = 84)
	// Stored as mins as that way they can be reset easily because the value is not from a previous date
	private ArrayList<Long> getInactiveAlarms(){
		ArrayList<String> mInactiveAlarms = new ArrayList<String>();
		mGetInactiveAlarms = new ArrayList<Long>();
		mInactiveAlarms = mSharedPrefs.getInactiveAlarms();
		if(mInactiveAlarms != null){
			for(int i= mInactiveAlarms.size() - 1; i>= 0; i--){
				final String mInactiveTime = mInactiveAlarms.get(i);
				long mTime = Long.valueOf(mInactiveTime);
				getTimeInMillis(mTime);
				mGetInactiveAlarms.add(mGetTimeInMillies);
			}
		}
		return mGetInactiveAlarms;		
    }
	
	private ArrayList<String> getFavouriteAlarms(){
		Set<String> mFavouriteAlarms = mSharedPrefs.getFavouriteAlarms();
		mViewFavouriteAlarms = new ArrayList<String>();
		if(mFavouriteAlarms != null){
			mViewFavouriteAlarms.addAll(mFavouriteAlarms);
		}
		return mViewFavouriteAlarms;
	}
	
	
    private ArrayList<Long> getAllAlarms(){
    	getActiveAlarms();
    	getInactiveAlarms();
   	
    	mGetAllAlarms = new ArrayList<Long>();
    	mGetActiveAlarms.addAll(mGetInactiveAlarms);
    	mGetAllAlarms = mGetActiveAlarms;
    	return mGetAllAlarms;
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

    public void onPause(){
    	super.onPause();
    }
}					