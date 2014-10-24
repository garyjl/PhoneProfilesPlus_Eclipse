package sk.henrichg.phoneprofilesplus;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.util.Log;

public class BluetoothScanAlarmBroadcastReceiver extends BroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "bluetoothScanAlarm";

	public static BluetoothAdapter bluetooth = null;

	public static List<BluetoothDeviceData> tmpScanResults = null;
	public static List<BluetoothDeviceData> scanResults = null;
	
	public void onReceive(Context context, Intent intent) {
		
		GlobalData.logE("#### BluetoothScanAlarmBroadcastReceiver.onReceive","xxx");

		if (bluetooth == null)
			bluetooth = (BluetoothAdapter) BluetoothAdapter.getDefaultAdapter();
		
		if (scanResults == null)
			scanResults = new ArrayList<BluetoothDeviceData>();
		
		// disabled for firstStartEvents
		//if (!GlobalData.getApplicationStarted(context))
			// application is not started
		//	return;

		GlobalData.loadPreferences(context);

		if (GlobalData.getGlobalEventsRuning(context))
		{
			GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.onReceive","xxx");

			//if (!getStartScan(context))
			//{	
				boolean bluetoothEventsExists = false;
				
				DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
				bluetoothEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_BLUETOOTHINFRONT) > 0;
				GlobalData.logE("BluetoothScanAlarmBroadcastReceiver.onReceive","bluetoothEventsExists="+bluetoothEventsExists);
	
				if (bluetoothEventsExists || GlobalData.getForceOneBluetoothScan(context))
				{
					boolean isBluetoothEnabled = bluetooth.isEnabled();
					isBluetoothEnabled = isBluetoothEnabled || GlobalData.applicationEventBluetoothEnableBluetooth;

					GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.onReceive","isBluetoothEnabled="+isBluetoothEnabled);
					
					if (isBluetoothEnabled)
				    {

						boolean connected = BluetoothConnectionBroadcastReceiver.isBluetoothConnected("");
						if (connected && (!GlobalData.getForceOneBluetoothScan(context)))
						{
							GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.onReceive","bluetooth is connected");
	
							// bluetooth is connected

		        			setBluetoothEnabledForScan(context, false);
							
			    			boolean isBluetoothNameScanned = BluetoothConnectionBroadcastReceiver.isAdapterNameScanned(dataWrapper);  
			    			
			    			if (isBluetoothNameScanned)
			    			{
			    				// connected SSID is scanned
			    				// no scan
			    				
			    				setStartScan(context, false);
	
			    				GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.onReceive","connected SSID is scanned, no start scan");
	
			    				dataWrapper.invalidateDataWrapper();
			    				
			    				return;
			    			}
						}
						else
							isBluetoothEnabled = enableBluetooth(dataWrapper, bluetooth);

						GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.onReceive","isBluetoothEnabled="+isBluetoothEnabled);
						
						if (isBluetoothEnabled && ((!GlobalData.getEventsBlocked(context)) || GlobalData.getForceOneBluetoothScan(context)))
						{
							if (!getBluetoothEnabledForScan(context))
								startScan(context);
							else
								setStartScan(context, true);
						}
				    }
				    else
				    {
	    				setStartScan(context, false);
	    				setBluetoothEnabledForScan(context, false);
				    }
	
			      	GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.onReceive","scanStarted="+getStartScan(context));
				}
				else
					removeAlarm(context);
				
				dataWrapper.invalidateDataWrapper();
			//}
		}
		
	}
	
	public static void initialize(Context context)
	{
		bluetooth = (BluetoothAdapter) BluetoothAdapter.getDefaultAdapter();
		
    	unlock();
    	setStartScan(context, false);
    	setBluetoothEnabledForScan(context, false);
	}
	
	public static void setAlarm(Context context)
	{
		GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.setAlarm","xxx");

		if (GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_BLUETOOTH, context) 
				== GlobalData.HARDWARE_CHECK_ALLOWED)
		{
			GlobalData.logE("BluetoothScanAlarmBroadcastReceiver.setAlarm","BluetoothHardware=true");
			
			removeAlarm(context);
	        
	        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	 			
	 		Intent intent = new Intent(context, BluetoothScanAlarmBroadcastReceiver.class);
	 			
	 		/*
			Calendar calendar = Calendar.getInstance();
	        calendar.setTimeInMillis(System.currentTimeMillis());
	        calendar.add(Calendar.SECOND, 10);
	        */
	         
			PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
			alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
											5 * 1000,
											GlobalData.applicationEventBluetoothScanInterval * 60 * 1000, 
											alarmIntent);
			
			GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.setAlarm","alarm is set");

		}
		else
			GlobalData.logE("BluetoothScanAlarmBroadcastReceiver.setAlarm","BluetoothHardware=false");
	}
	
	public static void removeAlarm(Context context)
	{
  		GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.removeAlarm","xxx");

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		Intent intent = new Intent(context, BluetoothScanAlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null)
        {
       		GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.removeAlarm","alarm found");
        		
        	alarmManager.cancel(pendingIntent);
        	pendingIntent.cancel();
        }
        else
       		GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.removeAlarm","alarm not found");
        
        initialize(context);
    }
	
	public static boolean isAlarmSet(Context context)
	{
		Intent intent = new Intent(context, BluetoothScanAlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);

        if (pendingIntent != null)
        	GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.isAlarmSet","alarm found");
        else
        	GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.isAlarmSet","alarm not found");

        return (pendingIntent != null);
	}

    public static void lock(Context context)
    {
		 // initialise the locks
		/*if (wakeLock == null)
	        wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
	                        .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WifiScanWakeLock");*/			

    	try {
            //wakeLock.acquire();
		//	GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.lock","xxx");
        } catch(Exception e) {
            Log.e("BluetoothScanAlarmBroadcastReceiver.lock", "Error getting Lock: "+e.getMessage());
        }
    }
 
    public static void unlock()
    {
    	
        /*if ((wakeLock != null) && (wakeLock.isHeld()))
            wakeLock.release();*/
		//GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.unlock","xxx");
    }
    
    public static void sendBroadcast(Context context)
    {
		Intent broadcastIntent = new Intent(context, BluetoothScanAlarmBroadcastReceiver.class);
		context.sendBroadcast(broadcastIntent);
    }
    
	static public boolean getStartScan(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(GlobalData.PREF_EVENT_BLUETOOTH_START_SCAN, false);
	}

	static public void setStartScan(Context context, boolean startScan)
	{
		SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(GlobalData.PREF_EVENT_BLUETOOTH_START_SCAN, startScan);
		editor.commit();
	}
	
	static public void startScan(Context context)
	{
		initTmpScanResults();
		boolean startScan = bluetooth.startDiscovery();
		setStartScan(context, startScan);
      	GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.onReceive","scanStarted="+startScan);
	}
	
	static public void initTmpScanResults()
	{
		if (tmpScanResults != null)
			tmpScanResults.clear();
		else
			tmpScanResults = new ArrayList<BluetoothDeviceData>();
	}

	static public boolean getBluetoothEnabledForScan(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(GlobalData.PREF_EVENT_BLUETOOTH_ENABLED_FOR_SCAN, false);
	}

	static public void setBluetoothEnabledForScan(Context context, boolean eventsBlocked)
	{
		SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(GlobalData.PREF_EVENT_BLUETOOTH_ENABLED_FOR_SCAN, eventsBlocked);
		editor.commit();
	}
	
    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private static boolean enableBluetooth(DataWrapper dataWrapper, BluetoothAdapter bluetooth)
    {
		boolean isAirplaneMode;
    	if (android.os.Build.VERSION.SDK_INT >= 17)
    		isAirplaneMode = Settings.Global.getInt(dataWrapper.context.getContentResolver(), Global.AIRPLANE_MODE_ON, 0) != 0;
    	else
    		isAirplaneMode = Settings.System.getInt(dataWrapper.context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
		if (!isAirplaneMode)
		{
			boolean isBluetoothEnabled = bluetooth.isEnabled();
			GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.enableBluetooth","isBluetoothEnabled="+isBluetoothEnabled);

			if (!isBluetoothEnabled)
	    	{
	        	if (GlobalData.applicationEventBluetoothEnableBluetooth)
	        	{
					boolean bluetoothEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_BLUETOOTHINFRONT) > 0;
	
					if (bluetoothEventsExists)
					{
			        	GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.enableBluetooth","enable");
			        	bluetooth.enable();
						setBluetoothEnabledForScan(dataWrapper.context, true);
						return true;
					}
	        	}
	    	}
	    	else
	    	{
	        	setBluetoothEnabledForScan(dataWrapper.context, false);
	    		return true;
	    	}
    	}

    	setBluetoothEnabledForScan(dataWrapper.context, false);
    	return false;
    }
	
}
