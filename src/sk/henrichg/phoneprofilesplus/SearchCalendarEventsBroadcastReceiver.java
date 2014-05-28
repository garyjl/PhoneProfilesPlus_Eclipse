package sk.henrichg.phoneprofilesplus;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class SearchCalendarEventsBroadcastReceiver extends WakefulBroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "searchCalendarEvents";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		GlobalData.logE("#### SearchCalendarEventsBroadcastReceiver.onReceive","xxx");
		
		GlobalData.loadPreferences(context);
		
		if (GlobalData.getGlobalEventsRuning(context))
		{
			boolean calendarEventsExists = false;
			
			DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
			calendarEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_CALENDAR) > 0;
			GlobalData.logE("SearchCalendarEventsBroadcastReceiver.onReceive","calendarEventsExists="+calendarEventsExists);
			dataWrapper.invalidateDataWrapper();

			if (calendarEventsExists)
			{
				// start service
				Intent eventsServiceIntent = new Intent(context, EventsService.class);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
				startWakefulService(context, eventsServiceIntent);
			}
			
		}
		
	}
	
	public static void setAlarm(Context context)
	{
		removeAlarm(context);
		
		GlobalData.logE("SearchCalendarEventsBroadcastReceiver.setAlarm","xxx");
 		
 		AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
 			
 		Intent intent = new Intent(context, SearchCalendarEventsBroadcastReceiver.class);
 			
 		/*
		Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);
        */
         
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
		alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
										//5 * 1000,
										AlarmManager.INTERVAL_DAY,
										AlarmManager.INTERVAL_DAY,
										alarmIntent);
	}
	
	public static void removeAlarm(Context context)
	{
  		GlobalData.logE("SearchCalendarEventsBroadcastReceiver.removeAlarm","xxx");

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		Intent intent = new Intent(context, SearchCalendarEventsBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null)
        {
       		GlobalData.logE("SearchCalendarEventsBroadcastReceiver.removeAlarm","alarm found");
        		
        	alarmManager.cancel(pendingIntent);
        	pendingIntent.cancel();
        }
    }

}
