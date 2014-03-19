package sk.henrichg.phoneprofilesplus;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class PowerConnectionReceiver extends WakefulBroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "powerConnection";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		GlobalData.logE("#### PowerConnectionReceiver.onReceive","xxx");
		
		GlobalData.loadPreferences(context);
		
		boolean batteryEventsExists = false;
		
		if (GlobalData.getGlobalEventsRuning(context))
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
	
			if (batteryEventsExists)
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
