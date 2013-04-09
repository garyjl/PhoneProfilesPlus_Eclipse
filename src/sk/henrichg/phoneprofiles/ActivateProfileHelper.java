package sk.henrichg.phoneprofiles;

import java.io.IOException;

import android.app.Activity;
import android.app.WallpaperManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

public class ActivateProfileHelper {
	
	private Activity activity;
	private Context context;
	
	public ActivateProfileHelper()
	{
		
	}

	public ActivateProfileHelper(Activity a, Context c)
	{
		activity = a;
		context = c;
	}
	
	@SuppressWarnings("deprecation")
	public void execute(Profile profile)
	{
		// rozdelit zvonenie a notifikacie - zial je to oznacene ako @Hide :-(
		//Settings.System.putInt(getContentResolver(), Settings.System.NOTIFICATIONS_USE_RING_VOLUME, 0);

		AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		
		// nahodenie ringer modu
		switch (profile.getVolumeRingerMode()) {
			case 1:  // Ring
				audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
				break;
			case 2:  // Ring & Vibrate
				audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
				break;
			case 3:  // Vibrate
				audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
				break;
			case 4:  // Silent
				audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
				break;
		}
		
		// nahodenie volume
		if (profile.getVolumeRingtoneChange())
			audioManager.setStreamVolume(AudioManager.STREAM_RING, profile.getVolumeRingtoneValue(), 0);
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_RING, profile.getVolumeRingtoneValue());
		if (profile.getVolumeNotificationChange())
			audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, profile.getVolumeNotificationValue(), 0);
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_NOTIFICATION, profile.getVolumeNotificationValue());
		if (profile.getVolumeMediaChange())
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, profile.getVolumeMediaValue(), 0);
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_MUSIC, profile.getVolumeMediaValue());
		if (profile.getVolumeAlarmChange())
			audioManager.setStreamVolume(AudioManager.STREAM_ALARM, profile.getVolumeAlarmValue(), 0);
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_ALARM, profile.getVolumeAlarmValue());
		if (profile.getVolumeSystemChange())
			audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, profile.getVolumeSystemValue(), 0);
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_SYSTEM, profile.getVolumeSystemValue());
		if (profile.getVolumeVoiceChange())
			audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, profile.getVolumeVoiceValue(), 0);
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_VOICE, profile.getVolumeVoiceValue());

		// nahodenie ringtone
		if (profile.getSoundRingtoneChange())
			Settings.System.putString(context.getContentResolver(), Settings.System.RINGTONE, profile.getSoundRingtone());
		if (profile.getSoundNotificationChange())
			Settings.System.putString(context.getContentResolver(), Settings.System.NOTIFICATION_SOUND, profile.getSoundNotification());
		if (profile.getSoundAlarmChange())
			Settings.System.putString(context.getContentResolver(), Settings.System.ALARM_ALERT, profile.getSoundAlarm());

		// nahodenie WiFi
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		boolean setWifiState = false;
		switch (profile.getDeviceWiFi()) {
			case 1 :
				setWifiState = true;
				break;
			case 2 : 
				setWifiState = false;
				break;
			case 3 :
				int wifiState = wifiManager.getWifiState(); 
				if ((wifiState == WifiManager.WIFI_STATE_DISABLED) || (wifiState == WifiManager.WIFI_STATE_DISABLING))
				{
					setWifiState = true;
				}
				else
				if ((wifiState == WifiManager.WIFI_STATE_ENABLED) || (wifiState == WifiManager.WIFI_STATE_ENABLING))
				{
					setWifiState = false;
				}
				break;
		}
		if (profile.getDeviceWiFi() != 0)
		{
			try {
				wifiManager.setWifiEnabled(setWifiState);
			} catch (Exception e) {
				// barla pre security exception INTERACT_ACROSS_USERS - chyba ROM 
				wifiManager.setWifiEnabled(setWifiState);
			}
		}	

		// nahodenie bluetooth
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		switch (profile.getDeviceBluetooth()) {
			case 1 :
				bluetoothAdapter.enable();
				break;
			case 2 : 
				bluetoothAdapter.disable();
				break;
			case 3 :
				if ((bluetoothAdapter != null) && (bluetoothAdapter.isEnabled()))
				{
					bluetoothAdapter.enable();
				}
				else
				if ((bluetoothAdapter != null) && (!bluetoothAdapter.isEnabled()))
				{
					bluetoothAdapter.disable();
				}
				break;
		}
		
		// nahodenie airplane modu
		boolean _isAirplaneMode = isAirplaneMode(context.getApplicationContext());
		boolean _setAirplaneMode = false;
		switch (profile.getDeviceAirplaneMode()) {
			case 1:
				if (!_isAirplaneMode)
				{
					_isAirplaneMode = true;
					_setAirplaneMode = true;
				}
				break;
			case 2:
				if (_isAirplaneMode)
				{
					_isAirplaneMode = false;
					_setAirplaneMode = true;
				}
				break;
			case 3:
				_isAirplaneMode = !_isAirplaneMode;
				_setAirplaneMode = true;
				break;
		}
		if (_setAirplaneMode)
			setAirplaneMode(context.getApplicationContext(), _isAirplaneMode);
		
		
/*		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		String enabledRadios = "";
		String disabledRadios = "";
			
		// nahodenie WiFi
		switch (profile.getDeviceWiFi()) {
			case 1 : 
				if (!enabledRadios.isEmpty())
					enabledRadios = enabledRadios + ",";
				enabledRadios = enabledRadios + "wifi";
				break;
			case 2 : 
				if (!disabledRadios.isEmpty())
					disabledRadios = disabledRadios + ",";
				disabledRadios = disabledRadios + "wifi";
				break;
			case 3 :
				int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
				if ((extraWifiState == WifiManager.WIFI_STATE_DISABLED) || (extraWifiState == WifiManager.WIFI_STATE_DISABLING))
				{
					if (!enabledRadios.isEmpty())
						enabledRadios = enabledRadios + ",";
					enabledRadios = enabledRadios + "wifi";
				}
				else
				if ((extraWifiState == WifiManager.WIFI_STATE_ENABLED) || (extraWifiState == WifiManager.WIFI_STATE_ENABLING))
				{
					if (!disabledRadios.isEmpty())
						disabledRadios = disabledRadios + ",";
					disabledRadios = disabledRadios + "wifi";
				}
				break;
		}
		
		// nahodenie Bluetooth
		switch (profile.getDeviceBluetooth()) {
			case 1 : 
				if (!enabledRadios.isEmpty())
					enabledRadios = enabledRadios + ",";
				enabledRadios = enabledRadios + "bluetooth";
				break;
			case 2 : 
				if (!disabledRadios.isEmpty())
					disabledRadios = disabledRadios + ",";
				disabledRadios = disabledRadios + "bluetooth";
				break;
			case 3 :
				BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if ((bluetoothAdapter != null) && (bluetoothAdapter.isEnabled()))
				{
					if (!enabledRadios.isEmpty())
						enabledRadios = enabledRadios + ",";
					enabledRadios = enabledRadios + "bluetooth";
				}
				else
				if ((bluetoothAdapter != null) && (!bluetoothAdapter.isEnabled()))
				{
					if (!disabledRadios.isEmpty())
						disabledRadios = disabledRadios + ",";
					disabledRadios = disabledRadios + "bluetooth";
				}
				break;
		}

		// nahodenie airplane mode
		switch (profile.getDeviceAirplaneMode()) {
			case 1 :
				// ak zapinam airplane mod disablujem vsetko
				disabledRadios = "cell,bluetooth,wifi,nfc";
				break;
			case 2 : 
				// ak vypinam airplane mod, enablujem este cell a ncf
				if (!enabledRadios.isEmpty())
					enabledRadios = enabledRadios + ",";
				enabledRadios = enabledRadios + "cell,nfc";
				break;
			case 3 :
				if (Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 0)
				{
					disabledRadios = "cell,bluetooth,wifi,nfc";
				}
				else
				{
					if (!enabledRadios.isEmpty())
						enabledRadios = enabledRadios + ",";
					enabledRadios = enabledRadios + "cell,nfc";
				} 
				break;
		}
		
		Log.d("PhoneProfilesActivity.activateProfile", "enabledRadios=" + enabledRadios);
		Log.d("PhoneProfilesActivity.activateProfile", "disabledRadios=" + disabledRadios);
		
		// zapni radia
		if (!enabledRadios.isEmpty())
		{
			Settings.System.putInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
			Settings.System.putString(getContentResolver(), Settings.System.AIRPLANE_MODE_RADIOS, enabledRadios);
			intent.putExtra("state", false);
			sendBroadcast(intent);
		}

		// vypni radia
		if (!disabledRadios.isEmpty())
		{
			Settings.System.putInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 1);
			Settings.System.putString(getContentResolver(), Settings.System.AIRPLANE_MODE_RADIOS, disabledRadios);
			intent.putExtra("state", true);
			sendBroadcast(intent);
		}

		// nastav default systemu
		Settings.System.putString(getContentResolver(), Settings.System.AIRPLANE_MODE_RADIOS, "cell,wifi,bluetooth,nfc");
*/
		
		// screen timeout
		switch (profile.getDeviceScreenTimeout()) {
			case 1:
				Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 15000);
				break;
			case 2:
				Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 30000);
				break;
			case 3:
				Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 60000);
				break;
			case 4:
				Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 120000);
				break;
			case 5:
				Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 600000);
				break;
			case 6:
				Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, -1);
				break;
		}
		
		// nahodenie podsvietenia
		if (profile.getDeviceBrightnessChange())
		{
			Window window = activity.getWindow();
			LayoutParams layoutParams = window.getAttributes();
			
			if (profile.getDeviceBrightnessAutomatic())
			{
				Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
				layoutParams.screenBrightness = LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
			}
			else
			{
				Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
				Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, profile.getDeviceBrightnessValue());
				layoutParams.screenBrightness = profile.getDeviceBrightnessValue() / 255.0f;
			}
			
			window.setAttributes(layoutParams);
		}
		
		// nahodenie pozadia
		if (profile.getDeviceWallpaperChange())
		{
			Log.d("PhoneProfilesActivity.activateProfile","set wallpaper");
			DisplayMetrics displayMetrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			int height = displayMetrics.heightPixels;
			int width = displayMetrics.widthPixels << 1; // best wallpaper width is twice screen width
			// first decode with inJustDecodeDpunds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(profile.getDeviceWallpaperIdentifier(), options);
			// calaculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, width, height);
			// decode bitmap with inSampleSize
			options.inJustDecodeBounds = false;
			Bitmap decodedSampleBitmap = BitmapFactory.decodeFile(profile.getDeviceWallpaperIdentifier(), options);
			// set wallpaper
			WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
			try {
				wallpaperManager.setBitmap(decodedSampleBitmap);
			} catch (IOException e) {
				Log.e("PhoneProfilesActivity.activateProfile", "Cannot set wallpaper. Image="+profile.getDeviceWallpaperIdentifier());
			}
		}	
		
	}

	private boolean isAirplaneMode(Context context)
	{
    	if (android.os.Build.VERSION.SDK_INT >= 17)
    		return AirPlaneMode_SDK17.getAirplaneMode(context);
    	else
    		return AirPlaneMode_SDK8.getAirplaneMode(context);
	}
	
	private void setAirplaneMode(Context context, boolean mode)
	{
    	if (android.os.Build.VERSION.SDK_INT >= 17)
    		AirPlaneMode_SDK17.setAirplaneMode(context, mode);
    	else
    		AirPlaneMode_SDK8.setAirplaneMode(context, mode);
	}
	
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		// raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		
		if (height > reqHeight || width > reqWidth)
		{
			// calculate ratios of height and width to requested height an width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			
			// choose the smalest ratio as InSamleSize value, this will guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width
			inSampleSize = (heightRatio < widthRatio) ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	
}
