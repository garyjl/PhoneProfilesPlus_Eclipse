package sk.henrichg.phoneprofilesplus;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.util.Log;

public class WifiScanAlarmBroadcastReceiver extends BroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "wifiScanAlarm";

	private static WifiLock wifiLock = null;
    private static WakeLock wakeLock = null;
 
	public static boolean scanStarted = false;
	public static List<ScanResult> scanResults = null;

	@SuppressLint("NewApi")
	public void onReceive(Context context, Intent intent) {
		
		GlobalData.logE("#### WifiScanAlarmBroadcastReceiver.onReceive","xxx");
		
		GlobalData.loadPreferences(context);
		
		if (GlobalData.getGlobalEventsRuning(context))
		{
			GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.onReceive","xxx");

			boolean wifiEventsExists = false;
			
			DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
			wifiEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_WIFIINFRONT) > 0;
			GlobalData.logE("WifiScanAlarmBroadcastReceiver.onReceive","wifiEventsExists="+wifiEventsExists);

			if (wifiEventsExists)
			{
				WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				boolean isWifiEnabled = wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
		    	if (android.os.Build.VERSION.SDK_INT >= 18)
		    		isWifiEnabled = isWifiEnabled || (wifi.isScanAlwaysAvailable());
				if (isWifiEnabled)
			    {
					ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					if (networkInfo.isConnected())
					{
						GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.onReceive","wifi is connected");

						// wifi is connected
						WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
						WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		    			String SSID = dataWrapper.getSSID(wifiManager, wifiInfo);
		    			boolean isSSIDScanned = dataWrapper.getDatabaseHandler().isSSIDScanned(SSID); 
		    			
		    			if (isSSIDScanned)
		    			{
		    				// connected SSID is scanned
		    				// no scan
		    				
		    				scanStarted = false;

		    				GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.onReceive","connected SSID is scanned, no start scan");

		    				dataWrapper.invalidateDataWrapper();
		    				
		    				return;
		    			}
					}
					
					
			        lock(context); // lock wakeLock and wifiLock, then scan.
			                       // unlock() is then called at the end of the onReceive function of WifiScanBroadcastReceiver
			        scanStarted = wifi.startScan();
			    }
			    else
			    	scanStarted = false;

		      	GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.onReceive","scanStarted="+scanStarted);
			}
			else
				removeAlarm(context);
			
			dataWrapper.invalidateDataWrapper();
			
		}
		
	}
	
	public static void setAlarm(Context context)
	{
		GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.setAlarm","xxx");

		if (GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_WIFI, context) 
				== GlobalData.HARDWARE_CHECK_ALLOWED)
		{
			GlobalData.logE("WifiScanAlarmBroadcastReceiver.setAlarm","WifiHardware=true");
			
	        // enable Wifi
	        enableWifi(context);
	        
	        /*
			ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (networkInfo.isConnected())
			{
				GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.setAlarm","wifi is connected");

				// wifi is connected
				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    			DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
    			String SSID = dataWrapper.getSSID(wifiManager, wifiInfo);
    			boolean isSSIDScanned = dataWrapper.getDatabaseHandler().isSSIDScanned(SSID); 
    			dataWrapper.invalidateDataWrapper();
    			
    			if (isSSIDScanned)
    			{
    				// connected SSID is scanned
    				// no set alarm

    				GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.setAlarm","connected SSID is scanned, no set alarm");
    				
    				return;
    			}
			}
			*/

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
        	
        	unlock();
        	
			WifiScanAlarmBroadcastReceiver.scanStarted = false;
        }
        else
       		GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.removeAlarm","alarm not found");
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

    public static void lock(Context context)
    {
		 // initialise the locks
		if (wifiLock == null)
	        wifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
	                        .createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY , "WifiScanWifiLock");
		if (wakeLock == null)
	        wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
	                        .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WifiScanWakeLock");			

    	try {
            wakeLock.acquire();
            wifiLock.acquire();
		//	GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.lock","xxx");
        } catch(Exception e) {
            Log.e("WifiScanAlarmBroadcastReceiver.lock", "Error getting Lock: "+e.getMessage());
        }
    }
 
    public static void unlock()
    {
    	
        if ((wakeLock != null) && (wakeLock.isHeld()))
            wakeLock.release();
        if ((wifiLock != null) && (wifiLock.isHeld()))
            wifiLock.release();
		//GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.unlock","xxx");
    }
    
    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void enableWifi(Context context)
    {
    	if (GlobalData.applicationEventWifiEnableWifi)
    	{
    		boolean isAirplaneMode;
        	if (android.os.Build.VERSION.SDK_INT >= 17)
        		isAirplaneMode = Settings.Global.getInt(context.getContentResolver(), Global.AIRPLANE_MODE_ON, 0) != 0;
        	else
        		isAirplaneMode = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
    		if (!isAirplaneMode)
    		{
				WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				boolean isWifiEnabled = wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
		    	if (android.os.Build.VERSION.SDK_INT >= 18)
		    		isWifiEnabled = isWifiEnabled || (wifi.isScanAlwaysAvailable());
		    	if (!isWifiEnabled)
		    	{
					DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
					boolean wifiEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_WIFI) > 0;
					dataWrapper.invalidateDataWrapper();

					if (wifiEventsExists)
						wifi.setWifiEnabled(true);
		    	}
    		}
    	}
    }
	
}
