package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.WakefulBroadcastReceiver;

public class WifiConnectionBroadcastReceiver extends WakefulBroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "wifiConnection";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		GlobalData.logE("#### WifiConnectionBroadcastReceiver.onReceive","xxx");
		
		if (!GlobalData.getApplicationStarted(context))
			// application is not started
			return;

		GlobalData.loadPreferences(context);
		
		if (GlobalData.getGlobalEventsRuning(context))
		{
		    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
	        if (info != null)
	        {
	    		GlobalData.logE("WifiConnectionBroadcastReceiver.onReceive","state="+info.getState());
	
	        	if ((info.getState() == NetworkInfo.State.CONNECTED) ||
	        		(info.getState() == NetworkInfo.State.DISCONNECTED))
	        	{
	        		GlobalData.logE("@@@ WifiConnectionBroadcastReceiver.onReceive","state="+info.getState());

		        	if (info.getState() == NetworkInfo.State.CONNECTED)
			        {
		        		if ((!WifiScanAlarmBroadcastReceiver.getWifiEnabledForScan(context)) &&
				        	(!GlobalData.getEventsBlocked(context)))
		        		{
			        		//if (WifiScanAlarmBroadcastReceiver.scanResults == null)
			        		//{
			        		//	// no wifi scan data, rescan
								// rescan wifi for update scanResults after connect
								WifiScanAlarmBroadcastReceiver.sendBroadcast(context);
			        		//}
		        		}
			        }
	        		
	    			DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
	    			boolean wifiEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_WIFICONNECTED) > 0;
	    			dataWrapper.invalidateDataWrapper();
	    	
	    			if (wifiEventsExists)
	    			{
		        		GlobalData.logE("@@@ WifiConnectionBroadcastReceiver.onReceive","wifiEventsExists="+wifiEventsExists);

	    				// start service
	    				Intent eventsServiceIntent = new Intent(context, EventsService.class);
	    				eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
	    				startWakefulService(context, eventsServiceIntent);
	    			}
	    			
		        	if (info.getState() == NetworkInfo.State.DISCONNECTED)
		        	{
		        		if ((!WifiScanAlarmBroadcastReceiver.getWifiEnabledForScan(context)) &&
				        	(!GlobalData.getEventsBlocked(context)))
				        {
							// rescan wifi for update scanResults after disconnect
							WifiScanAlarmBroadcastReceiver.sendBroadcast(context);
				        }
		        		else
		        		{
		        			WifiScanAlarmBroadcastReceiver.setWifiEnabledForScan(context, false);
		        		}
		        	}
	        	}
            }			
		}
	}
}
