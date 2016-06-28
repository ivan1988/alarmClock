package com.alarmForce.alarmclock;

import com.ontimealarm.alarmclock.R;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
public class SettingsFragment extends PreferenceFragment{
	
	private AlarmSharedPreferences mSharedPrefs;
	
	SwitchPreference mClockFormat;
	SwitchPreference mAscendingPref;
	SwitchPreference mVibratePref;
	SwitchPreference mVibrateFirstPref;
	SwitchPreference mSnoozeBtnPref;
	RingtonePreference mRingtonePref;
	ListPreference mDistancePref;
	ListPreference mSnoozeDurationPref;
	ListPreference mResetPref;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.layout.settings_fragment);
		
		findView();
		
		mSharedPrefs = new AlarmSharedPreferences(getActivity().getApplicationContext());		
		ringtoneSummary();
		snoozeDurationSummary();
		distanceSummary();
		
		mClockFormat.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {				
				mSharedPrefs.save(AlarmSharedPreferences.CLOCK_FORMAT, String.valueOf(newValue));				
				Intent i = new Intent (getActivity().getApplicationContext(), SettingsActivity.class);
				startActivity(i);
				return true;
			}
		});
		
		mDistancePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				mSharedPrefs.save("SET_DISTANCE", String.valueOf(newValue));
				distanceSummary();
				return true;
			}
			
		});
		
		mAscendingPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				mSharedPrefs.save(AlarmSharedPreferences.VOLUME_ASCENDING, String.valueOf(newValue));
				return true;
			}			
		});
			
		mVibratePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				mSharedPrefs.save(AlarmSharedPreferences.ALARM_VIBRATE, String.valueOf(newValue));
				return true;
			}
		});
		
		mVibrateFirstPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				mSharedPrefs.save(AlarmSharedPreferences.VIBRATE_FIRST, String.valueOf(newValue));
				return true;
			}
		});
		
		mSnoozeBtnPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				mSharedPrefs.save(AlarmSharedPreferences.SNOOZE_SETTINGS, String.valueOf(newValue));
				return true;
			}
		});
		
		mSnoozeDurationPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				mSharedPrefs.save("SNOOZE_DURATION", String.valueOf(newValue));
				snoozeDurationSummary();
				return true;
			}			
		});

		mRingtonePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {			
				mSharedPrefs.save(AlarmSharedPreferences.ALARM_RINGTONE, String.valueOf(newValue));
				ringtoneSummary();
				return true;
			}			
		});
		
		mResetPref.setSummary("Click Here To Reset");
		mResetPref.setValueIndex(1);
		mResetPref.setDialogTitle("Delete all alarms and reset all settings back to default?");
		mResetPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {	
				if(newValue.equals("1")){
					mSharedPrefs.clearPrefs();
				}
				return true;
			}			
		});
	}
	
	private void findView(){
		mDistancePref = (ListPreference) findPreference("distanceListPref");
		mResetPref = (ListPreference) findPreference("resetPref");
		mSnoozeDurationPref = (ListPreference) findPreference("snoozeDurationPref");
		mSnoozeBtnPref = (SwitchPreference) findPreference("snoozeBtnPref");
		mVibrateFirstPref = (SwitchPreference) findPreference("vibrateFirstPref");
		mVibratePref = (SwitchPreference) findPreference("vibratePref");
		mAscendingPref = (SwitchPreference) findPreference("volumeAscendingPref");
		mClockFormat = (SwitchPreference) findPreference("clockFormatPref");
		mRingtonePref = (RingtonePreference) findPreference("ringtoneSelectPref");
		mRingtonePref.setDefaultValue("Select Ringtone");
	}
	
	private void ringtoneSummary(){
		String savedRingtone = mSharedPrefs.getAlarmRingtone();
		Uri ringtoneUri = Uri.parse(String.valueOf(savedRingtone));
		Ringtone ringtone = RingtoneManager.getRingtone(getActivity().getApplicationContext(), ringtoneUri);
		if(ringtone != null) mRingtonePref.setSummary(ringtone.getTitle(getActivity().getApplicationContext()));			
	}
	
	private void snoozeDurationSummary(){
		String snooze = mSharedPrefs.getSnoozeDuration();
		String minutes;
		if(snooze.equals("1")){
			minutes = " Minute";
		}else{
			minutes = " Minutes";
		}
		mSnoozeDurationPref.setSummary(snooze + minutes);
	}
	
	private void distanceSummary(){
		String distance = mSharedPrefs.getSetDistance();
		mDistancePref.setSummary(distance + " Steps");
	}
}	
