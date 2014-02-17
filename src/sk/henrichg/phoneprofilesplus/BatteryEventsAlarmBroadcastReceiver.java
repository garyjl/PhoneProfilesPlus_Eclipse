package sk.henrichg.phoneprofilesplus;

import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class BatteryEventsAlarmBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		
		GlobalData.logE("BatteryEventsAlarmBroadcastReceiver.onReceive","xxx");
		
		DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
		List<Event> eventList = dataWrapper.getEventList();

		boolean batteryEventsExists = false;

		if (GlobalData.getGlobalEventsRuning(dataWrapper.context))
		{
		
			for (Event event : eventList)
			{
				GlobalData.logE("BatteryEventsAlarmBroadcastReceiver.onReceive","event._type="+event._type);
				GlobalData.logE("BatteryEventsAlarmBroadcastReceiver.onReceive","event.getStatus()="+event.getStatus());
				
				if ((event._type == Event.ETYPE_BATTERY) && (event.getStatus() != Event.ESTATUS_STOP))
				{
					batteryEventsExists = true;
					
					// start service
					Intent eventsServiceIntent = new Intent(context, EventsService.class);
					eventsServiceIntent.putExtra(GlobalData.EXTRA_EVENT_TYPE, event._type);
					eventsServiceIntent.putExtra(GlobalData.EXTRA_EVENT_ID, event._id);
					eventsServiceIntent.putExtra(GlobalData.EXTRA_POWER_CHANGE_RECEIVED, false);
					startWakefulService(context, eventsServiceIntent);
				}
			}
			
		}
		
		if (!batteryEventsExists)
			removeAlarm(context);
		
		dataWrapper.invalidateDataWrapper();
			
	}

	static public void removeAlarm(Context context)
	{
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
