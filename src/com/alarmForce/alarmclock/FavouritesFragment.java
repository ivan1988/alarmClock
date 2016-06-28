package com.alarmForce.alarmclock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

import com.ontimealarm.alarmclock.R;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class FavouritesFragment extends Fragment {
	
	private AlarmSharedPreferences mSharedPrefs;
	
	private Set<String> mFavouriteAlarms;
	private ArrayList<String> mViewFavouriteAlarms;
	private boolean mAlarmSetter = false;
	private boolean mFavouriteAlarmSetter = false;
	
	private ArrayList<String> mActiveAlarmList;
	private ArrayList<Long> mActiveAlarmMins;
	
	private Handler mHandler = new Handler();
	
	private String mClockFormat;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);	
		
		mSharedPrefs = new AlarmSharedPreferences(getActivity().getApplicationContext());
		mClockFormat = mSharedPrefs.getClockFormat();
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.favourites_fragment,
				container, false);
		favouriteAlarmsHandler(rootView);
		return rootView;
	}
	
	private void favouriteAlarmsHandler(View rootView){
		
		
		
		final LinearLayout Alarms = (LinearLayout) rootView.findViewById(R.id.favouriteAlarmXML);
			
		mViewFavouriteAlarms = new ArrayList<String>();
		mFavouriteAlarms = mSharedPrefs.getFavouriteAlarms();
		if(mFavouriteAlarms != null){
			getActiveTimeMins();
			mViewFavouriteAlarms.addAll(mFavouriteAlarms);
			for(String str:mViewFavouriteAlarms){
				final long mAlarm = Long.valueOf(str);
				final String mHour = String.valueOf(mAlarm / 60);
				final String mMinute = String.valueOf(mAlarm % 60);

				Calendar alarm = Calendar.getInstance();
				alarm.set(Calendar.HOUR_OF_DAY, Integer.valueOf(mHour));
				alarm.set(Calendar.MINUTE, Integer.valueOf(mMinute));
				alarm.set(Calendar.SECOND, 0);
				final long mAlarmTimeCur = alarm.getTimeInMillis();
				
				// check clock format to display the alarms accordingly
				String format;
				if(mClockFormat.equals("true")){
					format = "HH:mm a";
				}else{
					format = "hh:mm a";
				}
				final String mTime = getTime(mAlarmTimeCur, format);

				// border for the layout
				GradientDrawable border = new GradientDrawable();
				border.setColor(Color.rgb(24, 24, 48));
				border.setStroke(4, Color.rgb(58, 58, 58));
				
				LinearLayout ll = new LinearLayout(getActivity());
				ll.setOrientation(LinearLayout.HORIZONTAL);
				ll.setPadding(0, 20, 0, 20);
				ll.setBackground(border);
				
				addRemove(ll, mAlarm);
				
				setTextView(ll, mTime);
				
				editAlarm(ll, mAlarm);
				
				if(mActiveAlarmMins.contains(mAlarm)){
					mAlarmSetter = true;
				}else{
					mAlarmSetter = false;
				}
				
				editAlarmStatus( ll, mAlarm, mAlarmTimeCur, mAlarmSetter);
				Alarms.addView(ll);
			}
		}
	}
	
	// remove the alarm from favorites or re-add if the button is clicked again
	private void addRemove(LinearLayout ll, final long time){
		final ImageButton removeAlarm = new ImageButton(getActivity());
		removeAlarm.setPadding(20, 15, 40, 0);
		removeAlarm.setImageResource(R.drawable.ic_action_important_on);
		removeAlarm.setBackgroundColor(Color.TRANSPARENT);
		removeAlarm.setOnClickListener(new OnClickListener(){
			boolean onOff = mFavouriteAlarmSetter;
			public void onClick(View v){
				if(onOff == true){
					mSharedPrefs.saveStringSet("FAVOURITE_ALARMS", String.valueOf(time));
					removeAlarm.setImageResource(R.drawable.ic_action_important_on);
					onOff = false;
				}else{
					mHandler.post(new AlarmListHandler(getActivity(),"FAVOURITE_ALARMS", String.valueOf(time)));
					removeAlarm.setImageResource(R.drawable.ic_action_important);
					onOff = true;
				}	
			}
		});
		ll.addView(removeAlarm);
	}
	
	
	private void setTextView(LinearLayout ll, final String time){
		TextView favouriteAlarm = new TextView(getActivity());
		favouriteAlarm.setText(time);	
		favouriteAlarm.setPadding(45, 0, 2, 0);
		favouriteAlarm.setTextColor(Color.rgb(255, 158, 13));
		favouriteAlarm.setTextSize(30);
		ll.addView(favouriteAlarm);
	}
	
	// edit the alarm time
	private void editAlarm(LinearLayout ll, final long time){		
		ImageButton editAlarm = new ImageButton(getActivity());
		editAlarm.setPadding(80, 30, 80, 0);
		editAlarm.setImageResource(R.drawable.ic_action_edit);
		editAlarm.setBackgroundColor(Color.TRANSPARENT);
		editAlarm.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent i = new Intent (getActivity().getApplicationContext(), FavouritesActivity.class);
				i.putExtra("time", time);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
		});
		ll.addView(editAlarm);
	}
	
	// turn the alarm on and off
	private void editAlarmStatus( final LinearLayout ll, final long time, final long curTime, final boolean alarmIsSet){
		final ImageButton setAlarm = new ImageButton(getActivity());
		setAlarm.setImageResource(R.drawable.ic_action_alarms);
		if(mAlarmSetter == true){
			setAlarm.setImageResource(R.drawable.ic_action_alarm_on);
		}
		setAlarm.setPadding(100, 10, 0, 0);
		setAlarm.setBackgroundColor(Color.TRANSPARENT);
		setAlarm.setOnClickListener(new OnClickListener(){
			boolean onOff = mAlarmSetter;
			public void onClick(View v){
				if(onOff == false){
					setAlarm(time);
					setAlarm.setImageResource(R.drawable.ic_action_alarm_on);					
					mHandler.post(new AlarmListHandler(getActivity(),"INACTIVE_ALARMS", String.valueOf(time)));
					onOff = true;
				}else{
					cancelAlarm(curTime);
					setAlarm.setImageResource(R.drawable.ic_action_alarms);
					onOff = false;
				}
			}
		});
		ll.addView(setAlarm);			
	}
    
	// set alarm
    private void setAlarm(long time){
    	String mHour = String.valueOf(time / 60);
		String mMinute = String.valueOf(time % 60);
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

    
    // get active alarm list
    private ArrayList<Long> getActiveTimeMins(){
    	
    	Set<String> mActiveAlarms = mSharedPrefs.getActiveAlarms();
    	mActiveAlarmList = new ArrayList<String>();
    	mActiveAlarmMins = new ArrayList<Long>();
		if(mActiveAlarms != null){
			mActiveAlarmList.addAll(mActiveAlarms);
			for(String str: mActiveAlarmList){
				long mActiveHour = Long.valueOf(getTime(Long.valueOf(str), "HH"));
				long mActiveMin = Long.valueOf(getTime(Long.valueOf(str), "mm"));
				long time = (mActiveHour * 60)+mActiveMin;
				mActiveAlarmMins.add(time);
			}
		}
		return mActiveAlarmMins;
    }
    
    // get simple time
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
