package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class SetRadioPrefsForProfileBroadcastReceiver extends WakefulBroadcastReceiver {

	private static final String	ACTION = "sk.henrichg.phoneprofileshelper.ACTION";
	
	@Override
	public void onReceive(Context context, Intent intent) {

    	Log.e("SetRadioPrefsForProfileBroadcastReceiver.onReceive","xxx");
		
		String action = intent.getAction();
		
		if (action.equals (ACTION))
		{
/*			
			// start service
			Intent serviceIntent = new Intent(context, SetProfilePreferenceService.class);
			serviceIntent.putExtra(SetProfilePreferenceService.PROCEDURE, procedure);

			int GPSChange = intent.getIntExtra(SetProfilePreferenceService.GPS_CHANGE, 0);
			int airplaneModeChange = intent.getIntExtra(SetProfilePreferenceService.AIRPLANE_MODE_CHANGE, 0);
			int NFCChange = intent.getIntExtra(SetProfilePreferenceService.NFC_CHANGE, 0);
			int WifiChange = intent.getIntExtra(SetProfilePreferenceService.WIFI_CHANGE, 0);
			int bluetoothChange = intent.getIntExtra(SetProfilePreferenceService.BLUETOOTH_CHANGE, 0);
			int mobileDataChange = intent.getIntExtra(SetProfilePreferenceService.MOBILE_DATA_CHANGE, 0);

			Log.e("SetRadioPrefsForProfileBroadcastReceiver.onReceive","GPSChange="+GPSChange);
			Log.e("SetRadioPrefsForProfileBroadcastReceiver.onReceive","airplaneModeChange="+airplaneModeChange);
			Log.e("SetRadioPrefsForProfileBroadcastReceiver.onReceive","NFCChange="+NFCChange);
			Log.e("SetRadioPrefsForProfileBroadcastReceiver.onReceive","WifiChange="+WifiChange);
			Log.e("SetRadioPrefsForProfileBroadcastReceiver.onReceive","bluetoothChange="+bluetoothChange);
			Log.e("SetRadioPrefsForProfileBroadcastReceiver.onReceive","mobileDataChange="+mobileDataChange);
			serviceIntent.putExtra(SetProfilePreferenceService.GPS_CHANGE, GPSChange);
			serviceIntent.putExtra(SetProfilePreferenceService.AIRPLANE_MODE_CHANGE, airplaneModeChange);
			serviceIntent.putExtra(SetProfilePreferenceService.NFC_CHANGE, NFCChange);
			serviceIntent.putExtra(SetProfilePreferenceService.WIFI_CHANGE, WifiChange);
			serviceIntent.putExtra(SetProfilePreferenceService.BLUETOOTH_CHANGE, bluetoothChange);
			serviceIntent.putExtra(SetProfilePreferenceService.MOBILE_DATA_CHANGE, mobileDataChange);
			startWakefulService(context, serviceIntent);
*/			
		}		
		
	}

}
