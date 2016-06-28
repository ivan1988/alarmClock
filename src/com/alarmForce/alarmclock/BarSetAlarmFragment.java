package com.alarmForce.alarmclock;

import java.util.Calendar;

import com.ontimealarm.alarmclock.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

public class BarSetAlarmFragment extends Fragment{
	
	public int selectedHours;
	public int selectedMinutes;
	
	private TimePicker timePicker;
	
	private ImageButton mSetAlarm;
	private ImageButton mFavouriteAlarms;
	
	private TextView mTimeRemaining;
	
	double timePickerFadeIn = 0.0;
	
	private Handler mHandler = new Handler();
	
	private AlarmSharedPreferences mSharedPrefs;
	
	public long mAlarmTimeCur;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSharedPrefs = new AlarmSharedPreferences(getActivity().getApplicationContext());
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup setAlarmBarXML, Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.bar_set_alarm_fragment, setAlarmBarXML, false);
    	
    	findView(rootView);
    	
    	// get the intent homeFragment to edit alarms 
    	Intent i = getActivity().getIntent();  	
    	long mEditAlarmTime = i.getLongExtra("time", 0);
    	if(mEditAlarmTime != 0){
    		editExistingAlarm(mEditAlarmTime);
    	}
    	clockFormat();
    	
    	timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				updateDisplay(hourOfDay, minute);
			}
		});
    	
    	// set alarm
    	mSetAlarm.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Calendar now = Calendar.getInstance();
				Calendar alarm = Calendar.getInstance();
				alarm.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
				alarm.set(Calendar.MINUTE, timePicker.getCurrentMinute());
				alarm.set(Calendar.SECOND, 0);				
				if (alarm.before(now)) alarm.add(Calendar.DAY_OF_MONTH, 1);
				mAlarmTimeCur = alarm.getTimeInMillis();
				mHandler.post(new AlarmListHandler(getActivity(),"DUPLICATES", String.valueOf(mAlarmTimeCur)));
				Intent create = new Intent(getActivity().getApplicationContext(), AlarmService.class);
				create.setAction(AlarmService.CREATE);
				create.putExtra("newAlarmTime", mAlarmTimeCur);
				getActivity().startService(create);	
				Intent i = new Intent (getActivity().getApplicationContext(), HomeActivity.class);
				startActivity(i);
			}
		});
    	
    	// button for favorite alarms 
    	mFavouriteAlarms.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent i = new Intent (getActivity().getApplicationContext(), FavouritesActivity.class);
				startActivity(i);
			}
		});
    	
    	return rootView;
	}
	
	// Function to update the TextView that shows time left before the alarm goes off
	// "below the timePicker"
	private void updateDisplay(int mHourOfDay, int mMinuteOfDay){
		Calendar c = Calendar.getInstance(); 
		int mCalcHours = c.get(Calendar.HOUR_OF_DAY);
		int mCalcMinutes = c.get(Calendar.MINUTE);
		
		int mTotalTime = (mCalcHours * 60) + mCalcMinutes;
		int mTotalSelected = (mHourOfDay * 60) + mMinuteOfDay;
		int mMinutes24h = 1440;

		int mTimeRemainMins;
		
		if(mTotalSelected > mTotalTime){
			mTimeRemainMins = mTotalSelected - mTotalTime;
		}else if(mTotalSelected < mTotalTime){
			mTimeRemainMins = mMinutes24h + (mTotalSelected - mTotalTime);
		}else{
			mTimeRemainMins = mTotalSelected - mTotalTime;
		}
		mTimeRemaining.setText(String.valueOf(mTimeRemainMins / 60) + " hours and " + 
		String.valueOf(Math.abs(mTimeRemainMins % 60)) + " minutes from now");
	}
	
	// edit existing alarm with content passed from homeFragment
	private void editExistingAlarm(long time){ 
		int hour = (int) (time / 60);
		int minute = (int)(time % 60);
		timePicker.setCurrentHour(hour);
    	timePicker.setCurrentMinute(minute);
    	updateDisplay(hour, minute);
    	fadeIn();
	}
	
	// fade in when the user clicks on edit alarm.
	// this will alert the user that the changes are done in the Set Alarm bar
	private void fadeIn(){
		if(timePickerFadeIn < 0.1){
			timePicker.setAlpha(0);
		}	
    	if(timePickerFadeIn < 1){
    		timePickerFadeIn += 0.1;
    		final float f = (float)timePickerFadeIn;
    		mHandler.postDelayed(new Runnable(){		
				public void run(){	
					fadeIn();
					timePicker.setAlpha((float) f);
					if(timePickerFadeIn >= 0.8){
						mSetAlarm.setAlpha((float) 1);
					} else if(timePickerFadeIn >= 0.7){
						mSetAlarm.setAlpha((float) 0.8);
					}
				}
			}, 50);
		}
	}
	
	// set timePickers format 
	private void clockFormat(){				
		String text = mSharedPrefs.getClockFormat();		
		timePicker.setIs24HourView(Boolean.valueOf(text));
	}
	
	//find views for this fragment. As these are hardly ever edited i felt its a good idea to have them out of the way
	private void findView(View rootView){
		mSetAlarm = (ImageButton) rootView.findViewById(R.id.alarmSetButton);
		mFavouriteAlarms = (ImageButton) rootView.findViewById(R.id.favourite_alarms);
		mTimeRemaining = (TextView) rootView.findViewById(R.id.alarm_time_remaining);
    	timePicker = (TimePicker) rootView.findViewById(R.id.timePicker);
	}
}


