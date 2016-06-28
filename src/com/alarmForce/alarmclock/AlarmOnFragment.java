package com.alarmForce.alarmclock;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.ontimealarm.alarmclock.R;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AlarmOnFragment extends Fragment implements SensorEventListener {
	
   	private SensorManager sensorManager;
    private boolean activityRunning;
    
    private Float mCurrentCount = Float.valueOf(0.0f);
    private Float mUpdatedCount = Float.valueOf(0.0f);
    
    public static final String CURRENT_COUNT = "currentCount";
    
    private TextView mAlarmTimeText;
    private Button mAlarmOffButton;
    private Button mSnoozeButton;
    private String mAlarmId;
    private long mAlarmTime;
    public Handler mHandler = new Handler();
    public VolumeRunnable mVolumeRunnable;
    private Vibrator mVibrate;
    public Ringtone mAlert;
    private boolean mTimer = true;
    private boolean mAlarmStatus = true;
    public AudioManager mAudioManager;
    private String mSnoozePref;
    public boolean mAlarmAscending;
    private int mAlarmSimpleTime;
    private String mReset; 
    
    boolean mRunAlarmOnce = false;
    
    public static AlarmSharedPreferences mSharedPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mSharedPrefs = new AlarmSharedPreferences(getActivity().getApplicationContext());

		mVibrate = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		
		mReset = mSharedPrefs.getAlarmReset();
		
    	setRetainInstance( true );
		if (savedInstanceState != null){			
			mCurrentCount = savedInstanceState.getFloat(CURRENT_COUNT);
		}
		
		if(sensorManager == null){
			sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		}
		
		// check if the step counter sensor is available and if not display the alarm off button
		if(!isDeviceCompatable()){
			mAlarmOffButton.setVisibility(View.VISIBLE);
		}	
    }

    	    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){	
	    	super.onSaveInstanceState(savedInstanceState);
	    	
	}
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {	  
    	View rootView = inflater.inflate(R.layout.alarm_on_fragment, container, false);
    	
    	// get the alarm time and id from the AlarmReciever.java
    	Intent i = getActivity().getIntent();
		Bundle bundle = i.getExtras();    	
    	mAlarmId = bundle.getString("id");
    	mAlarmTime = bundle.getLong("alarmTime");
    	getSimpleAlarmTime(mAlarmTime);

    	timeView(rootView);
    
    	// alarm off button in case the step counter sensor is not available
        mAlarmOffButton = (Button) rootView.findViewById(R.id.alarm_off_button);
		mAlarmOffButton.setVisibility(View.GONE);
		mAlarmOffButton.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				alarmOn(false);
				mAlarmStatus = false;
			}
		});
		
		mSnoozePref = mSharedPrefs.getSnoozePref();
		// check if the user has already snoozed and if so hide the button
		// or if the alarm has been reset in onStop()
		// or if the user has got the snooze option off
		mSnoozeButton = (Button) rootView.findViewById(R.id.snooze_button);
		if(mAlarmId.equals("snoozeAlarm")||mSnoozePref.equals("false")||mReset.equals("true")){
			mSnoozeButton.setVisibility(View.GONE);		
		}else{
			mSnoozeButton.setVisibility(View.VISIBLE);
		}
		mSnoozeButton.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				Intent create = new Intent(getActivity().getApplicationContext(), AlarmService.class);
				create.setAction(AlarmService.SNOOZE);			
				getActivity().startService(create);
				alarmOn(false);
				mAlarmStatus = false;
			}
		});
		return rootView;
    }
    
    private void timeView(View v){
    	mAlarmTimeText = (TextView) v.findViewById(R.id.alarm_time);
    	String format = mSharedPrefs.getClockFormat();
    	String time;
    	if(format.equals("true")){
    		time = getTime(mAlarmTime, "HH:mm a");
    	}else{
    		time = getTime(mAlarmTime, "hh:mm a");
    	}
    	mAlarmTimeText.setText("Alarm for " + time);
    }
    
    private void getSimpleAlarmTime(long time){
    	String mHour = getTime(time, "HH");
		String mMinute = getTime(time, "mm");
		mAlarmSimpleTime = Integer.valueOf(mHour.concat(mMinute));	
    }

    // check id device has got step counter sensor
    private boolean isDeviceCompatable(){    	
    	int apiVersion = android.os.Build.VERSION.SDK_INT;
    	PackageManager packageManager = getActivity().getPackageManager();
    	return apiVersion >= android.os.Build.VERSION_CODES.KITKAT && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
    }
    
	@Override
	public void onResume() {
        super.onResume();
        
        activityRunning = true;
        // run alarm once so when the alarm resets in the on stop it does not go off if the existing alarm is running
        if(mRunAlarmOnce == false){
        	alarmOn(true);
        	mRunAlarmOnce = true;
        }
        
        // get the step counter sensor or display the off button
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
        	// longest delay available is set (less sensitive)
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
			mAlarmOffButton.setVisibility(View.VISIBLE); 
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        activityRunning = false;
        sensorManager.unregisterListener(this);
    }
    
    @Override
    public void onStop(){		
    	super.onStop();
    	if(mAlarmStatus == true){
	    	resetAlarm();
    	}
   }
    
    // check if sensor has changed and pass the sensor value into the updateAlarmStatus()
    @Override
    public void onSensorChanged(SensorEvent event) {	    	
        if (activityRunning) {
        	if(mCurrentCount < 1.0){
        		mCurrentCount = (float) event.values[0];
        	}	        	
        	mUpdatedCount = (float) event.values[0] - mCurrentCount; 
        	updateAlarmStatus(mUpdatedCount);
        }       
    }
    
    // check if user has taken enough steps for the alarm to go off
    private void updateAlarmStatus(float count){
    	float mUserSteps = Float.valueOf(4);
    	if(count == mUserSteps){
			alarmOn(false);
			mAlarmStatus = false;
    	}
    }

	private void alarmOn(boolean status){	
    	if(status == true){
        	String uri = mSharedPrefs.getAlarmRingtone();
        	mAlert = RingtoneManager.getRingtone(getActivity().getApplicationContext(), 
        			Uri.parse(uri));
        	// check if ringtone is set and if not assign the default ringtone
        	if(mAlert == null){
        		mAlert = RingtoneManager.getRingtone(getActivity().getApplicationContext(), 
        				( RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE) ) );
        	}       
        	mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        	
        	String vibFirst;
        	if(mReset.equals("true")){
        		vibFirst = "false";
        		mAlarmAscending = false;
        	}else{
        		vibFirst = mSharedPrefs.getVibrateFirst();
        		mAlarmAscending = Boolean.valueOf(mSharedPrefs.getAscendingPref());
        	}

        	String vibrate = mSharedPrefs.getVibratePref();
        	if(vibrate.equals("true")){
        		Vibrate(true);
        	}else{
        		vibFirst = "false";
        	}
        	
        	// if users preference is for the phone to vibrate before the alarm goes then delay the alarm sound
        	if(vibFirst.equals("true")){ 
        		Vibrate(true);       				
        		try {
					Thread.sleep(40000);
					// double check if the alarm is still on
					if(mTimer == true){
    					mSharedPrefs.save("ALARM_RESET", "true");
    					mHandler.post(mVolumeRunnable = new VolumeRunnable(mAudioManager, mHandler, mAlarmAscending, mAlert));
    					mAlert.play();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}else{
        		mHandler.post(mVolumeRunnable = new VolumeRunnable(mAudioManager, mHandler, mAlarmAscending, mAlert));
        		mAlert.play();
        	}
    	}else{
    		Vibrate(false);
    		mAlert.stop(); 
    		mTimer = false;
    		mHandler.removeCallbacks(mVolumeRunnable);
    		cancelAlarm();
    	}
    	
    }
    
    private void Vibrate(boolean onOff){
    	if(onOff == true){		
			long pattern[]={0,150,70,150, 70, 150, 800};
	        mVibrate.vibrate(pattern, 0);
    	}else{
    		mVibrate.cancel();
    	}
    }
    
    // reset alarm for every time the onStop is called in case the user manually cancels the app before the alarm is turned off.
    private void resetAlarm(){	
		AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(getActivity().getApplicationContext(), AlarmReciever.class);
		long time = System.currentTimeMillis() + 3000;
		i.putExtra("id", String.valueOf(mAlarmId));
		i.putExtra("alarmTime", mAlarmTime);
		PendingIntent pi = PendingIntent.getBroadcast(getActivity().getApplicationContext(), mAlarmSimpleTime, i, PendingIntent.FLAG_UPDATE_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP, time, pi);
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

    private void cancelAlarm(){
    	AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(getActivity().getApplicationContext(), AlarmReciever.class);
    	PendingIntent pi = PendingIntent.getBroadcast(getActivity().getApplicationContext(), mAlarmSimpleTime, i, PendingIntent.FLAG_UPDATE_CURRENT);
		am.cancel(pi);
		mSharedPrefs.save("ALARM_RESET", "false");
		// kill this activity as it was causing problems due to all the resets of the alarm
		Intent intent = new Intent(getActivity().getApplicationContext(), AlarmOnActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
        System.exit(0);
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
