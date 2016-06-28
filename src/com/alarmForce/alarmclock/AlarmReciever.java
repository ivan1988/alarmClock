package com.alarmForce.alarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.widget.Toast;


public class AlarmReciever extends BroadcastReceiver {

	private Handler mHandler = new Handler();
	@Override
	public void onReceive(Context context, Intent intent){
		
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
		
		wl.acquire();
		try{	
			Bundle bundle = intent.getExtras();
			String id = bundle.getString("id");
			Long alarmTime = bundle.getLong("alarmTime");	
			// save active alarm to inactive alarms
			mHandler.post(new AlarmListHandler(context,"ACTIVE_ALARMS", String.valueOf(alarmTime)));			
			Intent newIntent = new Intent(context, AlarmOnActivity.class);
			newIntent.putExtra("id", id);
			newIntent.putExtra("alarmTime", alarmTime);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(newIntent);
		}catch (Exception e){
			Toast.makeText(context, "Alarm Didnt Go Off", Toast.LENGTH_LONG).show();
		}
		wl.release();
	}
}
