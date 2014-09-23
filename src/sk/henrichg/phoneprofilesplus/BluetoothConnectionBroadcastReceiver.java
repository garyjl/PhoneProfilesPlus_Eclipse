package sk.henrichg.phoneprofilesplus;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class BluetoothConnectionBroadcastReceiver extends WakefulBroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "bluetoothConnection";
	
	public static List<BluetoothDevice> connectedDevices = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		GlobalData.logE("#### BluetoothConnectionBroadcastReceiver.onReceive","xxx");
		
		if (!GlobalData.getApplicationStarted(context))
			// application is not started
			return;

		GlobalData.loadPreferences(context);
		
		if (GlobalData.getGlobalEventsRuning(context))
		{
			if (connectedDevices == null)
				connectedDevices = new ArrayList<BluetoothDevice>();
			
			String action = intent.getAction();
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			
			if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) 
			{
				GlobalData.logE("@@@ BluetoothConnectionBroadcastReceiver.onReceive","Received: Bluetooth Connected");
				boolean found = false;
				for (BluetoothDevice _device : connectedDevices)
				{
					if (_device.getAddress().equals(device.getAddress()))
					{
						found = true;
						break;
					}
				}
				if (!found)
					connectedDevices.add(device);
		    }
		    if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED) ||
		    	action.equals(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED))
		    {
		    	GlobalData.logE("BluetoothConnectionBroadcastReceiver.onReceive","Received: Bluetooth Disconnected");
		    	int index = 0;
		    	boolean found = false;
				for (BluetoothDevice _device : connectedDevices)
				{
					if (_device.getAddress().equals(device.getAddress()))
					{
						found = true;
						break;
					}
					++index;
				}
				if (found)
					connectedDevices.remove(index);
		    }		
		}
	}

	public static boolean isBluetoothConnected()
	{
		return (connectedDevices != null) && (connectedDevices.size() > 0);
	}
	
	public static boolean isBluetoothNameScanned(DataWrapper dataWrapper)
	{
		if (isBluetoothConnected())
		{
			for (BluetoothDevice _device : connectedDevices)
			{
				//TODO dorob isBluetoothNameScanned ked budu polozky
				if (dataWrapper.getDatabaseHandler().isBluetoothNameScanned(_device.getName()))
					return true;
			}
			return false;
		}
		else
			return false;
	}

}
