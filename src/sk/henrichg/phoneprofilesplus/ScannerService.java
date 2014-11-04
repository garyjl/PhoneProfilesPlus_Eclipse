package sk.henrichg.phoneprofilesplus;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.Context;
import android.net.wifi.WifiManager;

public class ScannerService extends IntentService
{

	Context context;
	DataWrapper dataWrapper;
	
	public ScannerService()
	{
		super("ScannerService");
	}

	@SuppressLint("NewApi")
	@Override
	protected void onHandleIntent(Intent intent)
	{
		context = getBaseContext();

		GlobalData.logE("@@@ ScannerService.onHandleIntent", "xxx");

		String scanType = intent.getStringExtra(GlobalData.EXTRA_SCANNER_TYPE);
		
		if (scanType.equals(GlobalData.SCANNER_TYPE_WIFI))
		{
			if (!WifiScanAlarmBroadcastReceiver.getStartScan(context))
			{
				GlobalData.logE("@@@ ScannerService.onHandleIntent", "getStartScan=false");
	
				dataWrapper = new DataWrapper(context, false, false, 0);
				
				if (WifiScanAlarmBroadcastReceiver.wifi == null)
					WifiScanAlarmBroadcastReceiver.wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	
				// start scan
				if (GlobalData.getEventsBlocked(context) && (!GlobalData.getForceOneWifiScan(context)))
				{
					WifiScanAlarmBroadcastReceiver.setStartScan(context, false);
					WifiScanAlarmBroadcastReceiver.setWifiEnabledForScan(context, false);
				}
				else
				{
					// enable wifi
					int wifiState;
					if ((android.os.Build.VERSION.SDK_INT >= 18) && WifiScanAlarmBroadcastReceiver.wifi.isScanAlwaysAvailable())
						wifiState = WifiManager.WIFI_STATE_ENABLED;
					else
						wifiState = enableWifi(dataWrapper, WifiScanAlarmBroadcastReceiver.wifi);
					
					if (wifiState == WifiManager.WIFI_STATE_ENABLED)
						WifiScanAlarmBroadcastReceiver.startScan(context);
					else
					if (wifiState == WifiManager.WIFI_STATE_ENABLING)
						WifiScanAlarmBroadcastReceiver.setStartScan(context, true);
					else
					{
						WifiScanAlarmBroadcastReceiver.setStartScan(context, false);
						WifiScanAlarmBroadcastReceiver.setWifiEnabledForScan(context, false);
		    			GlobalData.setForceOneWifiScan(context, false);
				    }
					
					if (WifiScanAlarmBroadcastReceiver.getStartScan(context))
					{
						GlobalData.logE("@@@ ScannerService.onHandleIntent", "waiting for scan end");
						
						// wait for scan end
				    	for (int i = 0; i < 5 * 60; i++) // 60 seconds for wifi scan
				    	{
					        try {
					        	Thread.sleep(200);
						    } catch (InterruptedException e) {
						        System.out.println(e);
						    }
				        	if (!WifiScanAlarmBroadcastReceiver.getStartScan(context))
				        		break;
				    	}
	
						GlobalData.logE("@@@ ScannerService.onHandleIntent", "scan ended");
				    	
				    	GlobalData.setForceOneWifiScan(context, false);
				    	WifiScanAlarmBroadcastReceiver.setWifiEnabledForScan(context, false);
				    	WifiScanAlarmBroadcastReceiver.setStartScan(context, false);
					}
				}
			}
			else
				GlobalData.logE("@@@ ScannerService.onHandleIntent", "getStartScan=true");
		}
		else
		if (scanType.equals(GlobalData.SCANNER_TYPE_BLUETOOTH))
		{
			if (!BluetoothScanAlarmBroadcastReceiver.getStartScan(context))
			{
				GlobalData.logE("@@@ ScannerService.onHandleIntent", "getStartScan=false");
	
				dataWrapper = new DataWrapper(context, false, false, 0);
				
				if (BluetoothScanAlarmBroadcastReceiver.bluetooth == null)
					BluetoothScanAlarmBroadcastReceiver.bluetooth = (BluetoothAdapter) BluetoothAdapter.getDefaultAdapter();
	
				// start scan
				if (GlobalData.getEventsBlocked(context) && (!GlobalData.getForceOneBluetoothScan(context)))
				{
					BluetoothScanAlarmBroadcastReceiver.setStartScan(context, false);
					BluetoothScanAlarmBroadcastReceiver.setBluetoothEnabledForScan(context, false);
				}
				else
				{
					// enable bluetooth
					int bluetoothState = enableBluetooth(dataWrapper, BluetoothScanAlarmBroadcastReceiver.bluetooth);

					if (bluetoothState == BluetoothAdapter.STATE_ON)
						BluetoothScanAlarmBroadcastReceiver.startScan(context);
					else
					if (bluetoothState == BluetoothAdapter.STATE_TURNING_ON)
						BluetoothScanAlarmBroadcastReceiver.setStartScan(context, true);
					else
					{
						BluetoothScanAlarmBroadcastReceiver.setStartScan(context, false);
						BluetoothScanAlarmBroadcastReceiver.setBluetoothEnabledForScan(context, false);
	        			GlobalData.setForceOneBluetoothScan(context, false);
				    }
					
					if (BluetoothScanAlarmBroadcastReceiver.getStartScan(context))
					{
						GlobalData.logE("@@@ ScannerService.onHandleIntent", "waiting for scan end");
						
						// wait for scan end
				    	for (int i = 0; i < 5 * 20; i++) // 20 seconds for bluetooth scan
				    	{
					        try {
					        	Thread.sleep(200);
						    } catch (InterruptedException e) {
						        System.out.println(e);
						    }
				        	if (!BluetoothScanAlarmBroadcastReceiver.getStartScan(context))
				        		break;
				    	}
	
						GlobalData.logE("@@@ ScannerService.onHandleIntent", "scan ended");
				    	
				    	GlobalData.setForceOneBluetoothScan(context, false);
				    	BluetoothScanAlarmBroadcastReceiver.setBluetoothEnabledForScan(context, false);
				    	BluetoothScanAlarmBroadcastReceiver.setStartScan(context, false);
					}
				}
			}
			else
				GlobalData.logE("@@@ ScannerService.onHandleIntent", "getStartScan=true");
		}
	
	}

    @SuppressLint("NewApi")
	private static int enableWifi(DataWrapper dataWrapper, WifiManager wifi)
    {
    	GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.enableWifi","xxx");

    	int wifiState = wifi.getWifiState();
    	
    	if ((!GlobalData.getEventsBlocked(dataWrapper.context)) || GlobalData.getForceOneWifiScan(dataWrapper.context))
    	{
    		if (wifiState != WifiManager.WIFI_STATE_ENABLING)
    		{
				boolean isWifiEnabled = (wifiState == WifiManager.WIFI_STATE_ENABLED);
				boolean isScanAlwaisAvailable = false;
		    	if (android.os.Build.VERSION.SDK_INT >= 18)
		    		isScanAlwaisAvailable = wifi.isScanAlwaysAvailable();
	        	GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.enableWifi","isScanAlwaisAvailable="+isScanAlwaisAvailable);
	    		isWifiEnabled = isWifiEnabled || isScanAlwaisAvailable;
		    	if (!isWifiEnabled)
		    	{
		        	if (GlobalData.applicationEventWifiEnableWifi)
		        	{
						boolean wifiEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_WIFIINFRONT) > 0;
		
						if (wifiEventsExists)
						{
				        	GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.enableWifi","set enabled");
							wifi.setWifiEnabled(true);
							WifiScanAlarmBroadcastReceiver.setWifiEnabledForScan(dataWrapper.context, true);
							return WifiManager.WIFI_STATE_ENABLING;
						}
		        	}
		    	}
		    	else
		    	{
		        	GlobalData.logE("@@@ WifiScanAlarmBroadcastReceiver.enableWifi","already enabled");
		    		//if (isScanAlwaisAvailable)
		    		//	setWifiEnabledForScan(dataWrapper.context, false);
		    		return wifiState;
		    	}
    		}
    	}

		//setWifiEnabledForScan(dataWrapper.context, false);
    	return wifiState;
    }
	
    @SuppressLint("NewApi")
	private static int enableBluetooth(DataWrapper dataWrapper, BluetoothAdapter bluetooth)
    {
    	int bluetoothState = bluetooth.getState();
    	
    	if ((!GlobalData.getEventsBlocked(dataWrapper.context)) || GlobalData.getForceOneBluetoothScan(dataWrapper.context))
    	{
    		boolean isBluetoothEnabled = bluetoothState == BluetoothAdapter.STATE_ON;
			if (!isBluetoothEnabled)
	    	{
	        	if (GlobalData.applicationEventBluetoothEnableBluetooth)
	        	{
					boolean bluetoothEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_BLUETOOTHINFRONT) > 0;
	
					if (bluetoothEventsExists)
					{
			        	GlobalData.logE("@@@ BluetoothScanAlarmBroadcastReceiver.enableBluetooth","enable");
			        	bluetooth.enable();
			        	BluetoothScanAlarmBroadcastReceiver.setBluetoothEnabledForScan(dataWrapper.context, true);
						return BluetoothAdapter.STATE_TURNING_ON;
					}
	        	}
	    	}
	    	else
	    	{
        		//setBluetoothEnabledForScan(dataWrapper.context, false);
	    		return bluetoothState;
	    	}
    	}

   		//setBluetoothEnabledForScan(dataWrapper.context, false);
    	return bluetoothState;
    }
	
    
}
