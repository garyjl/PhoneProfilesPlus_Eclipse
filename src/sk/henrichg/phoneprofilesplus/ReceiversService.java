package sk.henrichg.phoneprofilesplus;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;


public class ReceiversService extends Service {

	private final BatteryEventBroadcastReceiver batteryEventReceiver = new BatteryEventBroadcastReceiver();
	private final HeadsetConnectionBroadcastReceiver headsetPlugReceiver = new HeadsetConnectionBroadcastReceiver();
	
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
	}
	 
	@Override
    public void onDestroy()
	{
		unregisterReceiver(batteryEventReceiver);
		unregisterReceiver(headsetPlugReceiver);
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
