package sk.henrichg.phoneprofilesplus;

import java.util.ArrayList;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class BluetoothScanBroadcastReceiver extends WakefulBroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "bluetoothScan";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		//GlobalData.logE("#### BluetoothScanBroadcastReceiver.onReceive","xxx");
		GlobalData.logE("@@@ BluetoothScanBroadcastReceiver.onReceive","----- start");

		if (BluetoothScanAlarmBroadcastReceiver.bluetooth == null)
			BluetoothScanAlarmBroadcastReceiver.bluetooth = (BluetoothAdapter) BluetoothAdapter.getDefaultAdapter();
		
		GlobalData.loadPreferences(context);
		
		if (GlobalData.getGlobalEventsRuning(context))
		{

			boolean scanStarted = (BluetoothScanAlarmBroadcastReceiver.getStartScan(context));// ||
            //(BluetoothScanAlarmBroadcastReceiver.scanResults == null);
			
			if (scanStarted)
			{
				GlobalData.logE("@@@ BluetoothScanBroadcastReceiver.onReceive","xxx");

				//BluetoothScanAlarmBroadcastReceiver.scanResults = WifiScanAlarmBroadcastReceiver.wifi.getScanResults();
				
				String action = intent.getAction();
				
				// When discovery finds a device
	            if (BluetoothDevice.ACTION_FOUND.equals(action))
	            {
	                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            	
	            	if (BluetoothScanAlarmBroadcastReceiver.tmpScanResults == null)
	            		BluetoothScanAlarmBroadcastReceiver.tmpScanResults = new ArrayList<BluetoothDevice>();
					boolean found = false;
					for (BluetoothDevice _device : BluetoothScanAlarmBroadcastReceiver.tmpScanResults)
					{
						if (_device.getAddress().equals(device.getAddress()))
						{
							found = true;
							break;
						}
					}
					if (!found)
						BluetoothScanAlarmBroadcastReceiver.tmpScanResults.add(device);
	            }
	            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
	            {
					BluetoothScanAlarmBroadcastReceiver.unlock();
					
					BluetoothScanAlarmBroadcastReceiver.scanResults.clear();
					BluetoothScanAlarmBroadcastReceiver.scanResults.addAll(BluetoothScanAlarmBroadcastReceiver.tmpScanResults);
					BluetoothScanAlarmBroadcastReceiver.tmpScanResults.clear();

					/*
					if (BluetoothScanAlarmBroadcastReceiver.scanResults != null)
					{
						for (BluetoothDevice device : BluetoothScanAlarmBroadcastReceiver.scanResults)
						{
							GlobalData.logE("BluetoothScanBroadcastReceiver.onReceive","device.name="+device.getName());
						}
					}
					*/

					if (BluetoothScanAlarmBroadcastReceiver.getBluetoothEnabledForScan(context))
					{
						GlobalData.logE("@@@ BluetoothScanBroadcastReceiver.onReceive","disable wifi");
						BluetoothScanAlarmBroadcastReceiver.bluetooth.disable();
					}

					if (!GlobalData.getApplicationStarted(context))
						// application is not started
						return;
					
					/*
					boolean wifiEventsExists = false;
					
					DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
					wifiEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_WIFIINFRONT) > 0;
					GlobalData.logE("WifiScanBroadcastReceiver.onReceive","wifiEventsExists="+wifiEventsExists);
					dataWrapper.invalidateDataWrapper();
		
					if (wifiEventsExists)
					{*/
						// start service
						Intent eventsServiceIntent = new Intent(context, EventsService.class);
						eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
						startWakefulService(context, eventsServiceIntent);
					//}
	            
	            }				
				
			}

		}
		
		BluetoothScanAlarmBroadcastReceiver.unlock();
		BluetoothScanAlarmBroadcastReceiver.setStartScan(context, false);

		GlobalData.logE("@@@ BluetoothScanBroadcastReceiver.onReceive","----- end");
		
	}
	
}
