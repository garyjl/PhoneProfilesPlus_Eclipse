package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class EventsAlarmBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		long eventId = intent.getLongExtra(GlobalData.EXTRA_EVENT_ID, 0);
		boolean startEvent = intent.getBooleanExtra(GlobalData.EXTRA_START_SYSTEM_EVENT, true);
		
		Log.e("EventsAlarmBroadcastReceiver.onReceive","eventId="+eventId);
		Log.e("EventsAlarmBroadcastReceiver.onReceive","startEvent="+startEvent);
		
		int eventsServiceProcedure = EventsService.ESP_PAUSE_EVENT;
		if (startEvent)
			eventsServiceProcedure = EventsService.ESP_START_EVENT;
		
		DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
		Event event = dataWrapper.getEventById(eventId);
		
		if (event != null)
		{
			Intent eventsServiceIntent = new Intent(context, EventsService.class);
			eventsServiceIntent.putExtra(GlobalData.EXTRA_EVENT_TYPE, event._type);
			eventsServiceIntent.putExtra(GlobalData.EXTRA_EVENT_ID, eventId);
			eventsServiceIntent.putExtra(GlobalData.EXTRA_EVENTS_SERVICE_PROCEDURE, eventsServiceProcedure);
			startWakefulService(context, eventsServiceIntent);
		}
		
		dataWrapper.invalidateDataWrapper();
			
	}

}