package sk.henrichg.phoneprofilesplus;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class BluetoothConnectionBroadcastReceiver extends WakefulBroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "bluetoothConnection";
	
	public static List<BluetoothDeviceData> connectedDevices = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		GlobalData.logE("#### BluetoothConnectionBroadcastReceiver.onReceive","xxx");

		if (connectedDevices == null)
			connectedDevices = new ArrayList<BluetoothDeviceData>();
		
		String action = intent.getAction();
		BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

		if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED) ||
			action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED) ||
		    action.equals(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED))
		{
			boolean connected = action.equals(BluetoothDevice.ACTION_ACL_CONNECTED);
		
			if (connected) 
			{
				GlobalData.logE("@@@ BluetoothConnectionBroadcastReceiver.onReceive","Received: Bluetooth Connected");
				boolean found = false;
				for (BluetoothDeviceData _device : connectedDevices)
				{
					if (_device.address.equals(device.getAddress()))
					{
						found = true;
						break;
					}
				}
				if (!found)
					connectedDevices.add(new BluetoothDeviceData(device.getName(), device.getAddress()));
		    }
			else
		    {
		    	GlobalData.logE("@@@ BluetoothConnectionBroadcastReceiver.onReceive","Received: Bluetooth Disconnected");
		    	int index = 0;
		    	boolean found = false;
				for (BluetoothDeviceData _device : connectedDevices)
				{
					if (_device.address.equals(device.getAddress()))
					{
						found = true;
						break;
					}
					++index;
				}
				if (found)
					connectedDevices.remove(index);
		    }
		
			if (!GlobalData.getApplicationStarted(context))
				// application is not started
				return;
	
			GlobalData.loadPreferences(context);
			
			if (GlobalData.getGlobalEventsRuning(context))
			{
	
				if (connected)
				{
	        		if (!GlobalData.getEventsBlocked(context))
	        		{
		        		if (BluetoothScanAlarmBroadcastReceiver.scanResults == null)
		        		{
		        			// no bluetooth scan data, rescan
							BluetoothScanAlarmBroadcastReceiver.sendBroadcast(context);
		        		}
	        		}
				}
				
				DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
				boolean bluetoothEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_BLUETOOTHCONNECTED) > 0;
				dataWrapper.invalidateDataWrapper();
		
				if (bluetoothEventsExists)
				{
	        		GlobalData.logE("@@@ BluetoothConnectionBroadcastReceiver.onReceive","bluetoothEventsExists="+bluetoothEventsExists);
	
					// start service
					Intent eventsServiceIntent = new Intent(context, EventsService.class);
					eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
					startWakefulService(context, eventsServiceIntent);
				}
				
	        	if (!connected)
	        	{
	        		if ((!BluetoothScanAlarmBroadcastReceiver.getBluetoothEnabledForScan(context)) &&
			        	(!GlobalData.getEventsBlocked(context)))
			        {
						// rescan bluetooth
						BluetoothScanAlarmBroadcastReceiver.sendBroadcast(context);
			        }
	        		else
	        		{
	        			BluetoothScanAlarmBroadcastReceiver.setBluetoothEnabledForScan(context, false);
	        		}
	        	}
			}
		}
	}

	public static boolean isBluetoothConnected(String adapterName)
	{
		if (adapterName.isEmpty())
			return (connectedDevices != null) && (connectedDevices.size() > 0);
		else
		{
			for (BluetoothDeviceData _device : connectedDevices)
			{
				if (_device.name.equals(adapterName))
					return true;
			}
			return false;
		}
	}
	
	public static boolean isAdapterNameScanned(DataWrapper dataWrapper)
	{
		if (isBluetoothConnected(""))
		{
			for (BluetoothDeviceData _device : connectedDevices)
			{
				//TODO dorob isBluetoothAdapterNameNameScanned ked budu polozky
				if (dataWrapper.getDatabaseHandler().isBluetoothAdapterNameScanned(_device.name))
					return true;
			}
			return false;
		}
		else
			return false;
	}

}
