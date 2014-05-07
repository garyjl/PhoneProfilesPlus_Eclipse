package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class EventsAlarmBroadcastReceiver extends WakefulBroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "eventsAlarm";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		GlobalData.logE("#### EventsAlarmBroadcastReceiver.onReceive","xxx");
		
		GlobalData.loadPreferences(context);
		
		if (GlobalData.getGlobalEventsRuning(context))
		{
			
			//long eventId = intent.getLongExtra(GlobalData.EXTRA_EVENT_ID, 0);
			//boolean startEvent = intent.getBooleanExtra(GlobalData.EXTRA_START_SYSTEM_EVENT, true);
			
			//GlobalData.logE("EventsAlarmBroadcastReceiver.onReceive","eventId="+eventId);
			//GlobalData.logE("EventsAlarmBroadcastReceiver.onReceive","startEvent="+startEvent);
			
			boolean timeEventsExists = false;
			
			DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
			//Event event = dataWrapper.getEventById(eventId);

			/*
			if (event != null)
			{
				event._eventPreferencesTime.removeSystemEvent(context);
				
				
				Intent eventsServiceIntent = new Intent(context, EventsService.class);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_EVENT_ID, eventId);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
				startWakefulService(context, eventsServiceIntent);
				
			}
			*/
			
			timeEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_TIME) > 0;
			GlobalData.logE("EventsAlarmBroadcastReceiver.onReceive","timeEventsExists="+timeEventsExists);
			dataWrapper.invalidateDataWrapper();

			if (timeEventsExists)
			{
				// start service
				Intent eventsServiceIntent = new Intent(context, EventsService.class);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_EVENT_ID, 0L);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
				startWakefulService(context, eventsServiceIntent);
			}
			
		}
		
	}

}
