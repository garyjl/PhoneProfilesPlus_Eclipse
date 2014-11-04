package sk.henrichg.phoneprofilesplus;

import java.util.ArrayList;
import java.util.Calendar;
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
//    private static WakeLock wakeLock = null;

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
					removeAlarm(context, false);
				
				dataWrapper.invalidateDataWrapper();
			//}
		}
		
	}
	
	public static void initialize(Context context)
	{
		if (bluetooth == null)
			bluetooth = (BluetoothAdapter) BluetoothAdapter.getDefaultAdapter();
		
    	unlock();
    	setStartScan(context, false);
    	setBluetoothEnabledForScan(context, false);

    	SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt(GlobalData.PREF_EVENT_BLUETOOTH_LAST_STATE, -1);
		editor.commit();
	}
	
	public static void setAlarm(Context context, boolean oneshot)
	{
		GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.setAlarm","oneshot="+oneshot);

		if (GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_BLUETOOTH, context) 
				== GlobalData.HARDWARE_CHECK_ALLOWED)
		{
			GlobalData.logE("BluetoothScanAlarmBroadcastReceiver.setAlarm","BluetoothHardware=true");
			
	        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	 			
	 		Intent intent = new Intent(context, BluetoothScanAlarmBroadcastReceiver.class);
	 			
	 		if (oneshot)
	 		{
				removeAlarm(context, true);

				Calendar calendar = Calendar.getInstance();
		        //calendar.setTimeInMillis(System.currentTimeMillis());
		        calendar.add(Calendar.SECOND, 3);

		        long alarmTime = calendar.getTimeInMillis(); 
		        		
			    //SimpleDateFormat sdf = new SimpleDateFormat("EE d.MM.yyyy HH:mm:ss:S");
				//GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.setAlarm","oneshot="+oneshot+"; alarmTime="+sdf.format(alarmTime));
		        
				PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		        alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
	 		}
	 		else
	 		{
				removeAlarm(context, false);

				Calendar calendar = Calendar.getInstance();
		        calendar.add(Calendar.SECOND, 5);
		        long alarmTime = calendar.getTimeInMillis(); 

			    //SimpleDateFormat sdf = new SimpleDateFormat("EE d.MM.yyyy HH:mm:ss:S");
				//GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.setAlarm","oneshot="+oneshot+"; alarmTime="+sdf.format(alarmTime));
		        
				PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
				alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
												alarmTime,
												GlobalData.applicationEventBluetoothScanInterval * 60 * 1000, 
												alarmIntent);
	 		}
			
			GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.setAlarm","alarm is set");

		}
		else
			GlobalData.logE("BluetoothScanAlarmBroadcastReceiver.setAlarm","BluetoothHardware=false");
	}
	
	public static void removeAlarm(Context context, boolean oneshot)
	{
  		GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.removeAlarm","oneshot="+oneshot);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		Intent intent = new Intent(context, BluetoothScanAlarmBroadcastReceiver.class);
		PendingIntent pendingIntent;
		if (oneshot)
			pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 1, intent, PendingIntent.FLAG_NO_CREATE);
		else
			pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null)
        {
       		GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.removeAlarm","oneshot="+oneshot+"; alarm found");
        		
        	alarmManager.cancel(pendingIntent);
        	pendingIntent.cancel();
        }
        else
       		GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.removeAlarm","oneshot="+oneshot+"; alarm not found");
    }
	
	public static boolean isAlarmSet(Context context, boolean oneshot)
	{
		Intent intent = new Intent(context, BluetoothScanAlarmBroadcastReceiver.class);
		PendingIntent pendingIntent;
		if (oneshot)
			pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 1, intent, PendingIntent.FLAG_NO_CREATE);
		else
			pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);

        if (pendingIntent != null)
        	GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.isAlarmSet","oneshot="+oneshot+"; alarm found");
        else
        	GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.isAlarmSet","oneshot="+oneshot+"; alarm not found");

        return (pendingIntent != null);
	}

    public static void lock(Context context)
    {
		 // initialise the locks
		/*if (wakeLock == null)
	        wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
	                        .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BluetoothScanWakeLock");*/			

    	try {
    		//if (!wakeLock.isHeld())
            //	wakeLock.acquire();
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
		lock(context); // lock wakeLock, then scan.
        			// unlock() is then called at the end of the onReceive function of BluetoothScanBroadcastReceiver
		boolean startScan = bluetooth.startDiscovery();
      	GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.onReceive","scanStarted="+startScan);
		if (!startScan)
		{
			unlock();
			if (getBluetoothEnabledForScan(context))
			{
				GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.onReceive","disable bluetooth");
				bluetooth.disable();
			}
		}
		setStartScan(context, startScan);
	}
	
	static public void stopScan(Context context)
	{
		unlock();
		BluetoothScanAlarmBroadcastReceiver.setBluetoothEnabledForScan(context, false);
		BluetoothScanAlarmBroadcastReceiver.setStartScan(context, false);
		GlobalData.setForceOneBluetoothScan(context, false);
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

	static public void setBluetoothEnabledForScan(Context context, boolean setEnabled)
	{
		SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(GlobalData.PREF_EVENT_BLUETOOTH_ENABLED_FOR_SCAN, setEnabled);
		editor.commit();
	}
	
    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private static boolean enableBluetooth(DataWrapper dataWrapper, BluetoothAdapter bluetooth)
    {
    	if ((!GlobalData.getEventsBlocked(dataWrapper.context)) || GlobalData.getForceOneBluetoothScan(dataWrapper.context))
    	{
			boolean isAirplaneMode;
	    	if (android.os.Build.VERSION.SDK_INT >= 17)
	    		isAirplaneMode = Settings.Global.getInt(dataWrapper.context.getContentResolver(), Global.AIRPLANE_MODE_ON, 0) != 0;
	    	else
	    		isAirplaneMode = Settings.System.getInt(dataWrapper.context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
			boolean isBluetoothEnabled = bluetooth.isEnabled();
			if ((!isAirplaneMode) || isBluetoothEnabled)
			{
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
	        		//setBluetoothEnabledForScan(dataWrapper.context, false);
		    		return true;
		    	}
	    	}
    	}

   		//setBluetoothEnabledForScan(dataWrapper.context, false);
    	return false;
    }
	
}
