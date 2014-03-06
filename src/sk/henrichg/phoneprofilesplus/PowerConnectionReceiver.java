package sk.henrichg.phoneprofilesplus;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class PowerConnectionReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		GlobalData.logE("#### PowerConnectionReceiver.onReceive","xxx");
		doOnReceive(context, 0);
	}
	
	static public void doOnReceive(Context context, long event_id)
	{
		GlobalData.logE("PowerConnectionReceiver.doOnReceive","xxx");
		
		boolean batteryEventsExists = false;
		
		if (GlobalData.getGlobalEventsRuning(context))
		{
			if (event_id == 0)
			{
				DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
				List<Event> eventList = dataWrapper.getEventList();
				for (Event event : eventList)
				{
					if ((event._type == Event.ETYPE_BATTERY) && (event.getStatus() != Event.ESTATUS_STOP))
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
				eventsServiceIntent.putExtra(GlobalData.EXTRA_EVENT_TYPE, Event.ETYPE_BATTERY);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_EVENT_ID, event_id);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_POWER_CHANGE_RECEIVED, true);
				startWakefulService(context, eventsServiceIntent);
			}
			
		}
	}
}
