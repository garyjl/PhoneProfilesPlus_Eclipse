package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class ScreenOnOffBroadcastReceiver extends WakefulBroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "screenOnOff";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		GlobalData.logE("#### ScreenOnOffBroadcastReceiver.onReceive","xxx");

		GlobalData.loadPreferences(context);
		
		if (GlobalData.getGlobalEventsRuning(context))
		{
			GlobalData.logE("@@@ ScreenOnOffBroadcastReceiver.onReceive","xxx");

			DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
			
			if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
			{
				// send broadcast for one wifi scan
				boolean wifiEventsExists = false;
				
				wifiEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_WIFIINFRONT) > 0;
				GlobalData.logE("ScreenOnOffBroadcastReceiver.onReceive","wifiEventsExists="+wifiEventsExists);
	
				if (wifiEventsExists)
				{
					if (WifiScanAlarmBroadcastReceiver.isAlarmSet(context))
					{
						// send broadcast only when wifi scan alarm is set
				 		Intent broadcastIntent = new Intent(context, WifiScanAlarmBroadcastReceiver.class);
						context.sendBroadcast(broadcastIntent);
					}
				}
			}
			
			/*
			boolean screenEventsExists = false;
			
			screenEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_SCREEN) > 0;
			GlobalData.logE("ScreenOnOffBroadcastReceiver.onReceive","screenEventsExists="+screenEventsExists);*/
			dataWrapper.invalidateDataWrapper();
			/*
			if (screenEventsExists)
			{
				// start service
				Intent eventsServiceIntent = new Intent(context, EventsService.class);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
				startWakefulService(context, eventsServiceIntent);
			}
			*/
		}

	}
	
}
