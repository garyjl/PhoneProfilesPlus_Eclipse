package sk.henrichg.phoneprofilesplus;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;


public class ReceiversService extends Service {

	private final BatteryEventBroadcastReceiver batteryEventReceiver = new BatteryEventBroadcastReceiver();
	private final HeadsetConnectionBroadcastReceiver headsetPlugReceiver = new HeadsetConnectionBroadcastReceiver();
	private final RestartEventsBroadcastReceiver restartEventsReceiver = new RestartEventsBroadcastReceiver();
	private final WifiConnectionBroadcastReceiver wifiConnectionReceiver = new WifiConnectionBroadcastReceiver();
	private final WifiScanBroadcastReceiver wifiScanReceiver = new WifiScanBroadcastReceiver();
	
	@Override
    public void onCreate()
	{
		IntentFilter intentFilter1 = new IntentFilter();
		intentFilter1.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryEventReceiver, intentFilter1);
		
		IntentFilter intentFilter2 = new IntentFilter();
		for (String action: HeadsetConnectionBroadcastReceiver.HEADPHONE_ACTIONS) {
			intentFilter2.addAction(action);
        }		
		registerReceiver(headsetPlugReceiver, intentFilter2);
		
		
		IntentFilter intentFilter3 = new IntentFilter();
		//intentFilter3.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION); //WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		intentFilter3.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		registerReceiver(wifiConnectionReceiver, intentFilter3);
		

		IntentFilter intentFilter4 = new IntentFilter();
		intentFilter4.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(wifiScanReceiver, intentFilter4);

		
		
		// receivers for system date and time change
		// events must by restarted
		IntentFilter intentFilter99 = new IntentFilter();
		intentFilter99.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		intentFilter99.addAction(Intent.ACTION_TIME_CHANGED);
	    registerReceiver(restartEventsReceiver, intentFilter99);
		
	}
	 
	@Override
    public void onDestroy()
	{
		unregisterReceiver(batteryEventReceiver);
		unregisterReceiver(headsetPlugReceiver);
		unregisterReceiver(wifiConnectionReceiver);
		unregisterReceiver(wifiScanReceiver);
		
		unregisterReceiver(restartEventsReceiver);
    }
	 
	@Override
    public int onStartCommand(Intent intent, int flags, int startId)
	{
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
	
	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
