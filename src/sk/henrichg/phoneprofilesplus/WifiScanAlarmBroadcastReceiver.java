package sk.henrichg.phoneprofilesplus;

import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.content.WakefulBroadcastReceiver;

public class WifiScanAlarmBroadcastReceiver extends WakefulBroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "wifiScanAlarm";
	
	public static boolean scanStarted = false;
	
	public static List<ScanResult> scanResults = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		GlobalData.logE("#### WifiScanAlarmBroadcastReceiver.onReceive","xxx");
		
		GlobalData.loadPreferences(context);
		
		if (GlobalData.getGlobalEventsRuning(context))
		{
			boolean wifiEventsExists = false;
			
			DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
			wifiEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_WIFISCAN) > 0;
			GlobalData.logE("WifiScanAlarmBroadcastReceiver.onReceive","wifiEventsExists="+wifiEventsExists);
			dataWrapper.invalidateDataWrapper();

			if (wifiEventsExists)
			{
				WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		      	scanStarted = wifi.startScan();

		      	GlobalData.logE("WifiScanAlarmBroadcastReceiver.onReceive","scanStarted="+scanStarted);
			}
			else
				removeAlarm(context);
			
		}
		
	}
	
	public static void setAlarm(Context context)
	{
		removeAlarm(context);
		
		GlobalData.logE("WifiScanAlarmBroadcastReceiver.setAlarm","xxx");

		if (GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_WIFI, context) == GlobalData.HARDWARE_CHECK_ALLOWED)
		{
			GlobalData.logE("WifiScanAlarmBroadcastReceiver.setAlarm","WifiHardware=true");
			
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
		}
		else
			GlobalData.logE("WifiScanAlarmBroadcastReceiver.setAlarm","WifiHardware=false");
	}
	
	public static void removeAlarm(Context context)
	{
  		GlobalData.logE("WifiScanAlarmBroadcastReceiver.removeAlarm","xxx");

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		Intent intent = new Intent(context, WifiScanAlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null)
        {
       		GlobalData.logE("WifiScanAlarmBroadcastReceiver.removeAlarm","alarm found");
        		
        	alarmManager.cancel(pendingIntent);
        	pendingIntent.cancel();
        }
    }
	
	public static boolean isAlarmSet(Context context)
	{
		Intent intent = new Intent(context, WifiScanAlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);

        if (pendingIntent != null)
        	GlobalData.logE("WifiScanAlarmBroadcastReceiver.isAlarmSet","alarm found");

        return (pendingIntent != null);
	}

}
