package sk.henrichg.phoneprofilesplus;

import java.util.List;

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
		doOnReceive(context, 0);
	}

	static public void doOnReceive(Context context, long event_id)
	{
		GlobalData.logE("BatteryEventsAlarmBroadcastReceiver.doOnReceive","xxx");
		
		boolean batteryEventsExists = false;

		if (GlobalData.getGlobalEventsRuning(context))
		{
			if (event_id == 0)
			{
				DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
				List<Event> eventList = dataWrapper.getEventList();
				for (Event event : eventList)
				{
					if (event._eventPreferencesBattery._enabled && (event.getStatus() != Event.ESTATUS_STOP))
					{
						batteryEventsExists = true;
					}
				}
				dataWrapper.invalidateDataWrapper();
			}
			else
				batteryEventsExists = true;

			if (batteryEventsExists)
			{
				// start service
				Intent eventsServiceIntent = new Intent(context, EventsService.class);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_EVENT_ID, event_id);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_POWER_CHANGE_RECEIVED, false);
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
