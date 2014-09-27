package sk.henrichg.phoneprofilesplus;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.util.Log;

public class WifiScanAlarmBroadcastReceiver extends BroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "wifiScanAlarm";

	public static WifiManager wifi = null;
	private static WifiLock wifiLock = null;
    //private static WakeLock wakeLock = null;

	public static List<ScanResult> scanResults = null;
	
	@SuppressLint("NewApi")
	public void onReceive(Context context, Intent intent) {
		
		GlobalData.logE("#### WifiScanAlarmBroadcastReceiver.onReceive","xxx");

		if (wifi == null)
			wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
		// disabled fro firstStartEvents
		//if (!GlobalData.getApplicationStarted(context))
			// application is not started
		//	return;

		GlobalData.loadPreferences(context);

		if (GlobalData.getGlobalEventsRuning(context))
		{
			GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.onReceive","xxx");

			//if (!getStartScan(context))
			//{	
				boolean wifiEventsExists = false;
				
				DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
				wifiEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_WIFIINFRONT) > 0;
				GlobalData.logE("WifiScanAlarmBroadcastReceiver.onReceive","wifiEventsExists="+wifiEventsExists);
	
				if (wifiEventsExists || GlobalData.getForceOneWifiScan(context))
				{
					boolean isWifiEnabled = wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
					isWifiEnabled = isWifiEnabled || GlobalData.applicationEventWifiEnableWifi;
			    	if (android.os.Build.VERSION.SDK_INT >= 18)
			    		isWifiEnabled = isWifiEnabled || (wifi.isScanAlwaysAvailable());
					if (isWifiEnabled)
				    {
						ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
						NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
						if (networkInfo.isConnected() && (!GlobalData.getForceOneWifiScan(context)))
						{
							GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.onReceive","wifi is connected");
	
							// wifi is connected

		        			setWifiEnabledForScan(context, false);
							
							WifiInfo wifiInfo = wifi.getConnectionInfo();
			    			String SSID = dataWrapper.getSSID(wifi, wifiInfo);
			    			boolean isSSIDScanned = dataWrapper.getDatabaseHandler().isSSIDScanned(SSID); 
			    			
			    			if (isSSIDScanned)
			    			{
			    				// connected SSID is scanned
			    				// no scan
			    				
			    				setStartScan(context, false);
	
			    				GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.onReceive","connected SSID is scanned, no start scan");
	
			    				dataWrapper.invalidateDataWrapper();
			    				
			    				return;
			    			}
						}
						else
							isWifiEnabled = enableWifi(dataWrapper, wifi);
						
						
						if (isWifiEnabled && ((!GlobalData.getEventsBlocked(context)) || GlobalData.getForceOneWifiScan(context)))
						{
					        lock(context, wifi); // lock wakeLock and wifiLock, then scan.
					                       // unlock() is then called at the end of the onReceive function of WifiScanBroadcastReceiver
		    				setStartScan(context, wifi.startScan());
						}
				    }
				    else
				    {
	    				setStartScan(context, false);
	        			setWifiEnabledForScan(context, false);
				    }
	
			      	GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.onReceive","scanStarted="+getStartScan(context));
				}
				else
					removeAlarm(context);
				
				dataWrapper.invalidateDataWrapper();
			//}
		}
		
	}
	
	public static void initialize(Context context)
	{
		wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
    	unlock();
    	setStartScan(context, false);
    	setWifiEnabledForScan(context, false);
	}
	
	public static void setAlarm(Context context)
	{
		GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.setAlarm","xxx");

		if (GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_WIFI, context) 
				== GlobalData.HARDWARE_CHECK_ALLOWED)
		{
			GlobalData.logE("WifiScanAlarmBroadcastReceiver.setAlarm","WifiHardware=true");
			
			removeAlarm(context);
	        
	        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	 			
	 		Intent intent = new Intent(context, WifiScanAlarmBroadcastReceiver.class);
	 			
	 		/*
			Calendar calendar = Calendar.getInstance();
	        calendar.setTimeInMillis(System.currentTimeMillis());
	        calendar.add(Calendar.SECOND, 10);
	        */
	         
			PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
			alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
											5 * 1000,
											GlobalData.applicationEventWifiScanInterval * 60 * 1000, 
											alarmIntent);
			
			GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.setAlarm","alarm is set");

		}
		else
			GlobalData.logE("WifiScanAlarmBroadcastReceiver.setAlarm","WifiHardware=false");
	}
	
	public static void removeAlarm(Context context)
	{
  		GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.removeAlarm","xxx");

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		Intent intent = new Intent(context, WifiScanAlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null)
        {
       		GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.removeAlarm","alarm found");
        		
        	alarmManager.cancel(pendingIntent);
        	pendingIntent.cancel();
        }
        else
       		GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.removeAlarm","alarm not found");
        
        initialize(context);
    }
	
	public static boolean isAlarmSet(Context context)
	{
		Intent intent = new Intent(context, WifiScanAlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);

        if (pendingIntent != null)
        	GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.isAlarmSet","alarm found");
        else
        	GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.isAlarmSet","alarm not found");

        return (pendingIntent != null);
	}

    private static void lock(Context context, WifiManager wifi)
    {
		 // initialise the locks
		if (wifiLock == null)
	        wifiLock = wifi.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY , "WifiScanWifiLock");
		/*if (wakeLock == null)
	        wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
	                        .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WifiScanWakeLock");*/			

    	try {
            //wakeLock.acquire();
            wifiLock.acquire();
		//	GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.lock","xxx");
        } catch(Exception e) {
            Log.e("WifiScanAlarmBroadcastReceiver.lock", "Error getting Lock: "+e.getMessage());
        }
    }
 
    public static void unlock()
    {
    	
        /*if ((wakeLock != null) && (wakeLock.isHeld()))
            wakeLock.release();*/
        if ((wifiLock != null) && (wifiLock.isHeld()))
            wifiLock.release();
		//GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.unlock","xxx");
    }
    
    public static void sendBroadcast(Context context)
    {
		Intent broadcastIntent = new Intent(context, WifiScanAlarmBroadcastReceiver.class);
		context.sendBroadcast(broadcastIntent);
    }
    
	static public boolean getStartScan(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(GlobalData.PREF_EVENT_WIFI_START_SCAN, false);
	}

	static public void setStartScan(Context context, boolean startScan)
	{
		SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(GlobalData.PREF_EVENT_WIFI_START_SCAN, startScan);
		editor.commit();
	}

	static public boolean getWifiEnabledForScan(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(GlobalData.PREF_EVENT_WIFI_ENABLED_FOR_SCAN, false);
	}

	static public void setWifiEnabledForScan(Context context, boolean eventsBlocked)
	{
		SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(GlobalData.PREF_EVENT_WIFI_ENABLED_FOR_SCAN, eventsBlocked);
		editor.commit();
	}
	
    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private static boolean enableWifi(DataWrapper dataWrapper, WifiManager wifi)
    {
		boolean isAirplaneMode;
    	if (android.os.Build.VERSION.SDK_INT >= 17)
    		isAirplaneMode = Settings.Global.getInt(dataWrapper.context.getContentResolver(), Global.AIRPLANE_MODE_ON, 0) != 0;
    	else
    		isAirplaneMode = Settings.System.getInt(dataWrapper.context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
		if (!isAirplaneMode)
		{
			boolean isWifiEnabled = wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
	    	if (android.os.Build.VERSION.SDK_INT >= 18)
	    		isWifiEnabled = isWifiEnabled || (wifi.isScanAlwaysAvailable());
	    	if (!isWifiEnabled)
	    	{
	        	if (GlobalData.applicationEventWifiEnableWifi)
	        	{
					boolean wifiEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_WIFIINFRONT) > 0;
	
					if (wifiEventsExists)
					{
			        	GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.enableWifi","enable");
						wifi.setWifiEnabled(true);
						setWifiEnabledForScan(dataWrapper.context, true);
						return true;
					}
	        	}
	    	}
	    	else
	    		return true;
    	}

    	setWifiEnabledForScan(dataWrapper.context, false);
    	return false;
    }
	
}
