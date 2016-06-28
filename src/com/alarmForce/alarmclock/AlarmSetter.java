package com.alarmForce.alarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmSetter extends BroadcastReceiver {
	
	// recreate active alarms on reboot 
	@Override
	public void onReceive(Context context, Intent intent){
		Intent service = new Intent(context, AlarmService.class);
		service.setAction(AlarmService.RECREATE);
		context.startService(service);
	}
}
