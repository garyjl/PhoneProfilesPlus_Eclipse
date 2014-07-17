package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.WakefulBroadcastReceiver;

public class WifiConnectionBroadcastReceiver extends WakefulBroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "wifiConnection";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		GlobalData.logE("#### WifiConnectionBroadcastReceiver.onReceive","xxx");
		
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
					/*
					SupplicantState supState;
			        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			        supState = wifiInfo.getSupplicantState();	
			        
			        if (supState.equals(SupplicantState.COMPLETED)) {
			            //logger.d("wifi enabled and connected");
			        } else {
			            if (supState.equals(SupplicantState.SCANNING)) {
			                //logger.d("wifi scanning");
			            } else if (supState.equals(SupplicantState.DISCONNECTED)) {
			                //logger.d("wifi disonnected");
			            } else {
			                //logger.d("wifi connecting");
			            }
			        }
			        */

	        		GlobalData.logE("@@@ WifiConnectionBroadcastReceiver.onReceive","state="+info.getState());
	        		
	    			DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
	    			boolean wifiEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_WIFICONNECTED) > 0;
	    			dataWrapper.invalidateDataWrapper();
	    	
	    			if (wifiEventsExists)
	    			{
	    				// start service
	    				Intent eventsServiceIntent = new Intent(context, EventsService.class);
	    				eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
	    				startWakefulService(context, eventsServiceIntent);
	    			}
	        	}
            }			
		}
	}
}
