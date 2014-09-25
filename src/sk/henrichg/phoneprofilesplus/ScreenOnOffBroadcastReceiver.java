package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class ScreenOnOffBroadcastReceiver extends WakefulBroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "screenOnOff";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		GlobalData.logE("#### ScreenOnOffBroadcastReceiver.onReceive","xxx");

		if (!GlobalData.getApplicationStarted(context))
			// application is not started
			return;

		GlobalData.loadPreferences(context);
		
		if (GlobalData.getGlobalEventsRuning(context))
		{
			if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
				GlobalData.logE("@@@ ScreenOnOffBroadcastReceiver.onReceive","screen on");
			else
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
				GlobalData.logE("@@@ ScreenOnOffBroadcastReceiver.onReceive","screen off");
			else
			if (intent.getAction().equals(Intent.ACTION_USER_PRESENT))
				GlobalData.logE("@@@ ScreenOnOffBroadcastReceiver.onReceive","screen unlock");

			DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
			
			if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
			{
				// send broadcast for one wifi scan
				boolean wifiEventsExists = false;
				wifiEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_WIFIINFRONT) > 0;
				GlobalData.logE("ScreenOnOffBroadcastReceiver.onReceive","wifiEventsExists="+wifiEventsExists);
				if (wifiEventsExists && (!GlobalData.getEventsBlocked(context)))
				{
					//if (WifiScanAlarmBroadcastReceiver.isAlarmSet(context))
					//{	
					//	// alarm is set = wifi scanning is ON
						// rescan wifi
						WifiScanAlarmBroadcastReceiver.sendBroadcast(context);
					//}
				}
				// send broadcast for one bluetooth scan
				boolean bluetoothEventsExists = false;
				bluetoothEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_BLUETOOTHINFRONT) > 0;
				GlobalData.logE("ScreenOnOffBroadcastReceiver.onReceive","bluetoothEventsExists="+bluetoothEventsExists);
				if (bluetoothEventsExists && (!GlobalData.getEventsBlocked(context)))
				{
					//if (BluetoothScanAlarmBroadcastReceiver.isAlarmSet(context))
					//{	
					//	// alarm is set = bluetooth scanning is ON
						// rescan bluetooth
						BluetoothScanAlarmBroadcastReceiver.sendBroadcast(context);
					//}
				}
			}
			
			
			boolean screenEventsExists = false;
			
			screenEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_SCREEN) > 0;
			GlobalData.logE("ScreenOnOffBroadcastReceiver.onReceive","screenEventsExists="+screenEventsExists);
			dataWrapper.invalidateDataWrapper();
			
			if (screenEventsExists)
			{
				// start service
				Intent eventsServiceIntent = new Intent(context, EventsService.class);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
				startWakefulService(context, eventsServiceIntent);
			}
			
		}

	}
	
}
