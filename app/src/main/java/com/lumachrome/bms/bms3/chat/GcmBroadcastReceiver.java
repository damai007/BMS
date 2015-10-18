package com.lumachrome.bms.bms3.chat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		// Explicitly specify that GcmIntentService will handle the intent.
		ComponentName comp = new ComponentName(context.getPackageName(),
				GcmIntentService.class.getName());
		// Start the service, keeping the device awake while it is launching.
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
		
		Bundle extras = intent.getExtras();
		Intent i = new Intent("CHAT_MESSAGE_RECEIVED");
		i.putExtra("message", extras.getString("message"));
		i.putExtra("chattingToName", extras.getString("sender"));
		i.putExtra("chattingToDeviceID", extras.getString("devId"));
		Log.i("VVV", "Extra GcmBroadcastReceiver : " + extras.toString());
		//Log.d("VVV", "GcmBroadcastReceiver : " + extras.getString("chattingToDeviceID"));
		
		context.sendBroadcast(i);
		
	}
}