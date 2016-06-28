package com.alarmForce.alarmclock;

import android.media.AudioManager;
import android.media.Ringtone;
import android.os.Handler;

public class VolumeRunnable implements Runnable{
	private AudioManager mAudioManager;
	private Handler mHandler;
	private static final int DELAY_TIME = 10*1000;
	private int mAlarmVolume = 1;
	private int mCurrentVolume;
	private int mMaxVolume = 0;
	private Ringtone mRingtone;
	private boolean mIncrease;
	
	// handle alarm volume
	@SuppressWarnings("deprecation")
	VolumeRunnable(AudioManager audioManager, Handler handler, boolean alarmAscending, Ringtone ringtone){
		this.mAudioManager = audioManager;
		this.mHandler = handler;	
		this.mIncrease = alarmAscending;
		this.mRingtone = ringtone;
		
		mRingtone.setStreamType(AudioManager.STREAM_ALARM);
		
		mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
		
		if(mIncrease == true){
			mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mAlarmVolume, 0);
		}else{
			mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mMaxVolume, 0);
		}
	}
	
	@Override
	public void run() {
		mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
		if(mIncrease == true){
			if(mAlarmVolume > mMaxVolume){
				mAlarmVolume = 0;
			}
			if(mCurrentVolume >= mAlarmVolume && mMaxVolume >= mCurrentVolume){			
				mHandler.postDelayed(this, DELAY_TIME);				
				mAlarmVolume++;
				mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mAlarmVolume, 0);
			}else{
				mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mMaxVolume, 0);
			}
		}else{
			mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mMaxVolume, 0);
		}
	}
}
