package sk.henrichg.phoneprofilesplus;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class PowerConnectionReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		GlobalData.logE("#### PowerConnectionReceiver.onReceive","xxx");
		doOnReceive(context);
	}
	
	static public void doOnReceive(Context context)
	{
		GlobalData.logE("PowerConnectionReceiver.doOnReceive","xxx");
		
		DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
		List<Event> eventList = dataWrapper.getEventList();

		if (GlobalData.getGlobalEventsRuning(dataWrapper.context))
		{
		
			for (Event event : eventList)
			{
				GlobalData.logE("PowerConnectionReceiver.onReceive","event._type="+event._type);
				GlobalData.logE("PowerConnectionReceiver.onReceive","event.getStatus()="+event.getStatus());
				
				if ((event._type == Event.ETYPE_BATTERY) && (event.getStatus() != Event.ESTATUS_STOP))
				{
					// start service
					Intent eventsServiceIntent = new Intent(context, EventsService.class);
					eventsServiceIntent.putExtra(GlobalData.EXTRA_EVENT_TYPE, event._type);
					eventsServiceIntent.putExtra(GlobalData.EXTRA_EVENT_ID, event._id);
					eventsServiceIntent.putExtra(GlobalData.EXTRA_POWER_CHANGE_RECEIVED, true);
					startWakefulService(context, eventsServiceIntent);
				}
			}
			
		}
		
		dataWrapper.invalidateDataWrapper();
		
	}
}
