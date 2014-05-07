package sk.henrichg.phoneprofilesplus;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class BatteryEventsAlarmBroadcastReceiver extends WakefulBroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "batteryEventAlarm";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		GlobalData.logE("##### BatteryEventsAlarmBroadcastReceiver.onReceive","xxx");

		GlobalData.loadPreferences(context);
		
		boolean batteryEventsExists = false;

		if (GlobalData.getGlobalEventsRuning(context))
		{
			DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
			batteryEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_BATTERY) > 0;
			dataWrapper.invalidateDataWrapper();

			if (batteryEventsExists)
			{
				// start service
				Intent eventsServiceIntent = new Intent(context, EventsService.class);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_EVENT_ID, 0L);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
				startWakefulService(context, eventsServiceIntent);
			}
				
		}

		if (!batteryEventsExists)
			removeAlarm(context);
	}
	
	static public void removeAlarm(Context context)
	{
   		GlobalData.logE("BatteryEventsAlarmBroadcastReceiver.removeAlarm","xxx");

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		Intent intent = new Intent(context, BatteryEventsAlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null)
        {
       		GlobalData.logE("BatteryEventsAlarmBroadcastReceiver.removeAlarm","alarm found");
        		
        	alarmManager.cancel(pendingIntent);
        	pendingIntent.cancel();
        }
	}
	
}
