package sk.henrichg.phoneprofiles;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

public class ActivateProfileHelper {
	
	private Activity activity;
	private Context context;
	private NotificationManager notificationManager;
	
	
	public ActivateProfileHelper()
	{
		
	}

	public ActivateProfileHelper(Activity a, Context c)
	{
		activity = a;
		context = c;
		notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	@SuppressWarnings("deprecation")
	public void execute(Profile profile, boolean interactive)
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

		// nahodenie mobilnych dat
		if (CheckHardwareFeatures.check(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_MOBILE_DATA, context))
		{
			boolean _isMobileData = isMobileData(context.getApplicationContext());
			boolean _setMobileData = false;
			switch (profile.getDeviceMobileData()) {
				case 1:
					if (!_isMobileData)
					{
						_isMobileData = true;
						_setMobileData = true;
					}
					break;
				case 2:
					if (_isMobileData)
					{
						_isMobileData = false;
						_setMobileData = true;
					}
					break;
				case 3:
					_isMobileData = !_isMobileData;
					_setMobileData = true;
					break;
			}
			if (_setMobileData)
				setMobileData(context.getApplicationContext(), _isMobileData);
		}

		// nahodenie WiFi
		if (CheckHardwareFeatures.check(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_WIFI, context))
		{
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
		}
		
		// nahodenie bluetooth
		if (CheckHardwareFeatures.check(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_BLUETOOTH, context))
		{
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
		}
		
		// nahodenie airplane modu
		if (CheckHardwareFeatures.check(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_AIRPLANE_MODE, context))
		{
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
		}
		
		
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
			Bitmap decodedSampleBitmap = BitmapResampler.resample(profile.getDeviceWallpaperIdentifier(), width, height); 
			// set wallpaper
			WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
			try {
				wallpaperManager.setBitmap(decodedSampleBitmap);
			} catch (IOException e) {
				Log.e("PhoneProfilesActivity.activateProfile", "Cannot set wallpaper. Image="+profile.getDeviceWallpaperIdentifier());
			}
		}	
		
		if (interactive)
		{
			// preferences, ktore vyzaduju interakciu uzivatela
			
			if (profile.getDeviceMobileDataPrefs())
			{
		    	if (android.os.Build.VERSION.SDK_INT > 10)
		    	{
		    		final Intent intent = new Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
					activity.startActivityForResult(intent, 1);
		    	}
		    	else
		    	{
		    		final Intent intent = new Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
					final ComponentName componentName = new ComponentName("com.android.phone", "com.android.phone.Settings");
					intent.addCategory(Intent.ACTION_MAIN);
					intent.setComponent(componentName);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					activity.startActivity(intent);
		    	}
			}
		}
		
	}
	
	//@SuppressWarnings("deprecation")
	@SuppressLint("InlinedApi")
	public void showNotification(Profile profile)
	{

		SharedPreferences preferences = context.getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, Context.MODE_PRIVATE);
		
		if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_NOTIFICATION_STATUS_BAR, true))
		{	
			if (profile == null)
			{
				notificationManager.cancel(PhoneProfilesActivity.NOTIFICATION_ID);
			}
			else
			{
				// vytvorenie intentu na aktivitu, ktora sa otvori na kliknutie na notifikaciu
				Intent intent = new Intent(context, PhoneProfilesActivity.class);
				// nastavime, ze aktivita sa spusti z notifikacnej listy
				intent.putExtra(PhoneProfilesActivity.EXTRA_START_APP_SOURCE, PhoneProfilesActivity.STARTUP_SOURCE_NOTIFICATION);
				PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				// vytvorenie samotnej notifikacie
				NotificationCompat.Builder notificationBuilder;
		        if (profile.getIsIconResourceID())
		        {
		        	int iconSmallResource = context.getResources().getIdentifier(profile.getIconIdentifier()+"_notify", "drawable", context.getPackageName());
		        	int iconLargeResource = context.getResources().getIdentifier(profile.getIconIdentifier(), "drawable", context.getPackageName());
		        	Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), iconLargeResource);
		        
		        	notificationBuilder = new NotificationCompat.Builder(context)
						.setContentText(context.getResources().getString(R.string.active_profile_notification_label))
						.setContentTitle(profile.getName())
						.setContentIntent(pIntent)
						.setLargeIcon(largeIcon)
						.setSmallIcon(iconSmallResource);
		        }
		        else
		        {
		        	int iconSmallResource = R.drawable.ic_launcher_notify;
		        			
		        	if (android.os.Build.VERSION.SDK_INT >= 11)
		        	{
		        		Resources resources = context.getResources();
		        		int height = (int) resources.getDimension(android.R.dimen.notification_large_icon_height);
		        		int width = (int) resources.getDimension(android.R.dimen.notification_large_icon_width);
		        		Bitmap bitmap = BitmapResampler.resample(profile.getIconIdentifier(), width, height);

		        		notificationBuilder = new NotificationCompat.Builder(context)
							.setContentText(context.getResources().getString(R.string.active_profile_notification_label))
							.setContentTitle(profile.getName())
							.setContentIntent(pIntent)
							.setLargeIcon(bitmap)
							.setSmallIcon(iconSmallResource);
		        	}
		        	else
		        	{
						notificationBuilder = new NotificationCompat.Builder(context)
						.setContentText(context.getResources().getString(R.string.active_profile_notification_label))
						.setContentTitle(profile.getName())
						.setContentIntent(pIntent)
						.setSmallIcon(iconSmallResource);
		        	}
		        }
				Notification notification = notificationBuilder.build();
						//.getNotification();
				notification.flags |= Notification.FLAG_NO_CLEAR; 
				notificationManager.notify(PhoneProfilesActivity.NOTIFICATION_ID, notification);
			}
		}
		else
		{
			notificationManager.cancel(PhoneProfilesActivity.NOTIFICATION_ID);
		}
	}
	
	public void updateWidget()
	{
		Intent intent = new Intent(context, ActivateProfileWidget.class);
		intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
		int ids[] = AppWidgetManager.getInstance(activity.getApplication()).getAppWidgetIds(new ComponentName(activity.getApplication(), ActivateProfileWidget.class));
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		context.sendBroadcast(intent);
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
	
	private boolean isMobileData(Context context)
	{
		final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
	/*	try {
			final Class<?> connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
			final Method getMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("getMobileDataEnabled");
			getMobileDataEnabledMethod.setAccessible(true);
			return (Boolean)getMobileDataEnabledMethod.invoke(connectivityManager);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return false;
		}
		*/
		
		final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null)
		{
			int netvorkType = networkInfo.getType(); // 0 = mobile, 1 = wifi
			//String netvorkTypeName = networkInfo.getTypeName(); // "mobile" or "WIFI"
			boolean connected = networkInfo.isConnected();  // true = active connection
			
			if (netvorkType == 0)
			{
				// connected into mobile data
				return connected;
			}
			else
			{
				// conected into Wifi
				return false;
			}
		}
		else
			return false;
		
	}
	
	private void setMobileData(Context context, boolean enabled)
	{
    	if (android.os.Build.VERSION.SDK_INT <= 8)
    	{
    		
    		//     <uses-permission android:name="android.permission.MODIFY_PHONE_STATE"/>
    		// toto nebude asik fungovat, lebo to vyzaduje, aby apka bola systemova
    		// musime skusit najst riesenie pre root, ako je airplane mode
    		
    		Method dataConnSwitchmethod;
    		Class<?> telephonyManagerClass;
    		Object iTelephonyStub;
    		Class<?> iTelephonyClass;
    		
    		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    		
    		boolean isEnabled;
    		if (telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED)
    			isEnabled = true;
    		else
    			isEnabled = false;
    		
    		try {
				telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
				Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
				getITelephonyMethod.setAccessible(true);
				iTelephonyStub = getITelephonyMethod.invoke(telephonyManager);
				iTelephonyClass = Class.forName(iTelephonyStub.getClass().getName());
				
				if (isEnabled)
					dataConnSwitchmethod = iTelephonyClass.getDeclaredMethod("disableDataConnectivity");
				else
					dataConnSwitchmethod = iTelephonyClass.getDeclaredMethod("enableDataConnectivity");
				
				dataConnSwitchmethod.setAccessible(true);
				dataConnSwitchmethod.invoke(iTelephonyStub);
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				Log.e("ActivateProfileHelper.setMobileData", e.getMessage());
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				Log.e("ActivateProfileHelper.setMobileData", e.getMessage());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				Log.e("ActivateProfileHelper.setMobileData", e.getMessage());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				Log.e("ActivateProfileHelper.setMobileData", e.getMessage());
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				Log.e("ActivateProfileHelper.setMobileData", e.getMessage());
			}
    		
    		
    	}
    	else
    	{
    		final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    		try {
				final Class<?> connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
				final Field iConnectivityManagerField = connectivityManagerClass.getDeclaredField("mService");
				iConnectivityManagerField.setAccessible(true);
				final Object iConnectivityManager = iConnectivityManagerField.get(connectivityManager);
				final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
				final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
				setMobileDataEnabledMethod.setAccessible(true);
				
				setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				Log.e("ActivateProfileHelper.setMobileData", e.getMessage());
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
				Log.e("ActivateProfileHelper.setMobileData", e.getMessage());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				Log.e("ActivateProfileHelper.setMobileData", e.getMessage());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				Log.e("ActivateProfileHelper.setMobileData", e.getMessage());
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				Log.e("ActivateProfileHelper.setMobileData", e.getMessage());
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				Log.e("ActivateProfileHelper.setMobileData", e.getMessage());
			}
    	}
		
	}
	
}
