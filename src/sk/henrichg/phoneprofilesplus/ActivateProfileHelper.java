package sk.henrichg.phoneprofilesplus;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.RemoteViews;

public class ActivateProfileHelper {

	private DataWrapper dataWrapper;
	
	private Activity activity;
	private Context context;
	private NotificationManager notificationManager;
	
	private static final String PPHELPERACTION = "sk.henrichg.phoneprofileshelper.ACTION";
	private static final String SETRADIOACTION = "sk.henrichg.phoneprofilesplus.SetRadiosForProfile.ACTION";
	private static final String PPHELPER_PROCEDURE = "procedure";
	private static final String PPHELPER_PROCEDURE_RADIO_CHANGE = "radioChange";
	private static final String PPHELPER_GPS_CHANGE = "GPSChange";
	private static final String PPHELPER_AIRPLANE_MODE_CHANGE = "airplaneModeChange";
	private static final String PPHELPER_NFC_CHANGE = "NFCChange";
	private static final String PPHELPER_WIFI_CHANGE = "WiFiChange";
	private static final String PPHELPER_BLUETOOTH_CHANGE = "bluetoothChange";
	private static final String PPHELPER_MOBILE_DATA_CHANGE = "mobileDataChange";
	
	public ActivateProfileHelper()
	{
		
	}

	public void initialize(DataWrapper dataWrapper, Activity a, Context c)
	{
		this.dataWrapper = dataWrapper;
		
		initializeNoNotificationManager(a, c);
		notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	public void initializeNoNotificationManager(Activity a, Context c)
	{
		activity = a;
		context = c;
	}

	public void deinitialize()
	{
		dataWrapper = null;
		activity = null;
		context = null;
		notificationManager = null;
	}
	
	@SuppressWarnings("deprecation")
	private void doExecuteForRadios(Profile profile)
	{
		// nahodenie mobilnych dat
		if (GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_MOBILE_DATA, context) == GlobalData.HARDWARE_CHECK_ALLOWED)
		{
			boolean _isMobileData = isMobileData(context);
			boolean _setMobileData = false;
			switch (profile._deviceMobileData) {
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
			{
				setMobileData(context, _isMobileData);

				try {
		        	Thread.sleep(200);
			    } catch (InterruptedException e) {
			        System.out.println(e);
			    }
			}
		}

		// nahodenie WiFi
		if (GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_WIFI, context) == GlobalData.HARDWARE_CHECK_ALLOWED)
		{
			WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			boolean setWifiState = false;
			switch (profile._deviceWiFi) {
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
			if (profile._deviceWiFi != 0)
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
		if (GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_BLUETOOTH, context) == GlobalData.HARDWARE_CHECK_ALLOWED)
		{
			BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			switch (profile._deviceBluetooth) {
				case 1 :
					bluetoothAdapter.enable();
					break;
				case 2 : 
					bluetoothAdapter.disable();
					break;
				case 3 :
					if ((bluetoothAdapter != null) && (!bluetoothAdapter.isEnabled()))
					{
						bluetoothAdapter.enable();
					}
					else
					if ((bluetoothAdapter != null) && (bluetoothAdapter.isEnabled()))
					{
						bluetoothAdapter.disable();
					}
					break;
			}
		}

		// nahodenie GPS
		if (GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_GPS, context) == GlobalData.HARDWARE_CHECK_ALLOWED)
		{
			String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

			//Log.d("ActivateProfileHelper.execute", provider);
		    
			switch (profile._deviceGPS) {
				case 1 :
					setGPS(context, true);
					break;
				case 2 : 
					setGPS(context, false);
					break;
				case 3 :
				    if (!provider.contains("gps"))
					{
						setGPS(context, true);
					}
					else
				    if (provider.contains("gps"))
					{
						setGPS(context, false);
					}
					break;
			}
		}
		
		// nahodenie NFC - len v PPHelper
		
	}
	
	public void executeForRadios(Profile profile)
	{
		boolean _isAirplaneMode = false;
		boolean _setAirplaneMode = false;
		if (GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_AIRPLANE_MODE, context) == GlobalData.HARDWARE_CHECK_ALLOWED)
		{
			_isAirplaneMode = isAirplaneMode(context);
			switch (profile._deviceAirplaneMode) {
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
		}
		
		if (_setAirplaneMode && _isAirplaneMode)
			// switch ON airplane mode, set it before executeForRadios
			setAirplaneMode(context, _isAirplaneMode);
		
		doExecuteForRadios(profile);

		if (_setAirplaneMode && !(_isAirplaneMode))
			// switch OFF airplane mode, set if after executeForRadios
			setAirplaneMode(context, _isAirplaneMode);
		
	}
	
	private void waitForVolumeChange()
	{
		/*Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
        });
		t.start();*/			
        /*try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            System.out.println(e);
        }*/
	}
	
	public void setVolumes(Profile profile, AudioManager audioManager)
	{
		if (profile.getVolumeSystemChange())
		{
			audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, profile.getVolumeSystemValue(), 0);
			waitForVolumeChange();
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_SYSTEM, profile.getVolumeSystemValue());
		}
		if (profile.getVolumeRingtoneChange())
		{
			audioManager.setStreamVolume(AudioManager.STREAM_RING, profile.getVolumeRingtoneValue(), 0);
			waitForVolumeChange();
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_RING, profile.getVolumeRingtoneValue());
		}
		if (profile.getVolumeNotificationChange())
		{
			audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, profile.getVolumeNotificationValue(), 0);
			waitForVolumeChange();
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_NOTIFICATION, profile.getVolumeNotificationValue());
		}
		if (profile.getVolumeMediaChange())
		{
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, profile.getVolumeMediaValue(), 0);
			waitForVolumeChange();
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_MUSIC, profile.getVolumeMediaValue());
		}
		if (profile.getVolumeAlarmChange())
		{
			audioManager.setStreamVolume(AudioManager.STREAM_ALARM, profile.getVolumeAlarmValue(), 0);
			waitForVolumeChange();
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_ALARM, profile.getVolumeAlarmValue());
		}
		if (profile.getVolumeVoiceChange())
			audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, profile.getVolumeVoiceValue(), 0);
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_VOICE, profile.getVolumeVoiceValue());
	}
	
	@SuppressWarnings("deprecation")
	public void setRingerMode(Profile profile, AudioManager audioManager)
	{
		switch (profile._volumeRingerMode) {
		case 1:  // Ring
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			try
			{
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			try
			{
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			Settings.System.putInt(context.getContentResolver(), "vibrate_when_ringing", 0);
			break;
		case 2:  // Ring & Vibrate
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			try
			{
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			try
			{
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			Settings.System.putInt(context.getContentResolver(), "vibrate_when_ringing", 1);
			break;
		case 3:  // Vibrate
			audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			try
			{
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			try
			{
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			Settings.System.putInt(context.getContentResolver(), "vibrate_when_ringing", 1);
			break;
		case 4:  // Silent
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			try
			{
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			try
			{
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			Settings.System.putInt(context.getContentResolver(), "vibrate_when_ringing", 0);
			break;
		}
	}
	
	public void execute(Profile _profile, boolean _interactive, String eventNotificationSound)
	{
		// rozdelit zvonenie a notifikacie - zial je to oznacene ako @Hide :-(
		//Settings.System.putInt(context.getContentResolver(), Settings.System.NOTIFICATIONS_USE_RING_VOLUME, 0);

		Profile profile = GlobalData.getMappedProfile(_profile, context);
		//profile = dataWrapper.filterProfileWithBatteryEvents(profile);
		
		boolean interactive = _interactive;
		
		//boolean radiosExecuted = false;
		
		// nahodenie volume
		/*// run service for execute volumes
		Intent volumeServiceIntent = new Intent(context, ExecuteVolumeProfilePrefsService.class);
		volumeServiceIntent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile._id);
		volumeServiceIntent.putExtra(GlobalData.EXTRA_SECOND_SET_VOLUMES, true);
		volumeServiceIntent.putExtra(GlobalData.EXTRA_EVENT_NOTIFICATION_SOUND, eventNotificationSound);
		//WakefulIntentService.sendWakefulWork(context, radioServiceIntent);
		context.startService(volumeServiceIntent);*/
		AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		// nahodenie ringer modu - aby sa mohli nastavit hlasitosti
		setRingerMode(profile, audioManager);
		setVolumes(profile, audioManager);
		// nahodenie ringer modu - hlasitosti zmenia silent/vibrate
		setRingerMode(profile, audioManager);

		// nahodenie ringtone
		if (profile._soundRingtoneChange == 1)
		{
			if (profile._soundRingtone.isEmpty())
				Settings.System.putString(context.getContentResolver(), Settings.System.RINGTONE, null);
			else
				Settings.System.putString(context.getContentResolver(), Settings.System.RINGTONE, profile._soundRingtone);
		}
		if (profile._soundNotificationChange == 1)
		{
			if (profile._soundNotification.isEmpty())
				Settings.System.putString(context.getContentResolver(), Settings.System.NOTIFICATION_SOUND, null);
			else
				Settings.System.putString(context.getContentResolver(), Settings.System.NOTIFICATION_SOUND, profile._soundNotification);
		}
		if (profile._soundAlarmChange == 1)
		{
			if (profile._soundAlarm.isEmpty())
				Settings.System.putString(context.getContentResolver(), Settings.System.ALARM_ALERT, null);
			else
				Settings.System.putString(context.getContentResolver(), Settings.System.ALARM_ALERT, profile._soundAlarm);
		}

		// event notification
		if (!eventNotificationSound.isEmpty())
		{
			audioManager.setMode(AudioManager.MODE_NORMAL);
			final String _eventNotificationSound = eventNotificationSound;
			final DataWrapper _dataWrapper = dataWrapper;
		    Handler handler = new Handler(context.getMainLooper());
			handler.post(new Runnable() {
				public void run() {
					MediaPlayer mp=new MediaPlayer();
					Uri ringtoneUri=Uri.parse(_eventNotificationSound);
					try {
						mp.setDataSource(_dataWrapper.context, ringtoneUri);
					    mp.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
					    mp.prepare();
					    mp.start();
			        	Thread.sleep(200);
					}
					catch(Exception e)
					{
					    e.printStackTrace();
					}				
				}
			});
		}
		
		// nahodenie radio preferences
		if (PhoneProfilesHelper.isPPHelperInstalled(context, 0))
		{
			// broadcast PPHelper
			Intent ppHelperIntent = new Intent();
			ppHelperIntent.setAction(PPHELPERACTION);
			ppHelperIntent.putExtra(PPHELPER_PROCEDURE, PPHELPER_PROCEDURE_RADIO_CHANGE);
			ppHelperIntent.putExtra(PPHELPER_GPS_CHANGE, profile._deviceGPS);
			ppHelperIntent.putExtra(PPHELPER_AIRPLANE_MODE_CHANGE, profile._deviceAirplaneMode);
			ppHelperIntent.putExtra(PPHELPER_NFC_CHANGE, profile._deviceNFC);
			ppHelperIntent.putExtra(PPHELPER_WIFI_CHANGE, profile._deviceWiFi);
			ppHelperIntent.putExtra(PPHELPER_BLUETOOTH_CHANGE, profile._deviceBluetooth);
			ppHelperIntent.putExtra(PPHELPER_MOBILE_DATA_CHANGE, profile._deviceMobileData);
		    context.sendBroadcast(ppHelperIntent);
		}
		else
		{
			// run service for execute radios
			Intent radioBroadcastReceiver = new Intent();
			radioBroadcastReceiver.setAction(SETRADIOACTION);
			radioBroadcastReceiver.putExtra(GlobalData.EXTRA_PROFILE_ID, profile._id);
		    context.sendBroadcast(radioBroadcastReceiver);
		}
		
		// nahodenie auto-sync
		boolean _isAutosync = ContentResolver.getMasterSyncAutomatically();
		boolean _setAutosync = false;
		switch (profile._deviceAutosync) {
			case 1:
				if (!_isAutosync)
				{
					_isAutosync = true;
					_setAutosync = true;
				}
				break;
			case 2:
				if (_isAutosync)
				{
					_isAutosync = false;
					_setAutosync = true;
				}
				break;
			case 3:
				_isAutosync = !_isAutosync;
				_setAutosync = true;
				break;
		}
		if (_setAutosync)
			ContentResolver.setMasterSyncAutomatically(_isAutosync);
		
		// screen timeout
		switch (profile._deviceScreenTimeout) {
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
		    	if (android.os.Build.VERSION.SDK_INT < 19)
		    		Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, -1);
		    	else
		    		Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 18000000);
				break;
		}
		
		// nahodenie podsvietenia
		if (profile.getDeviceBrightnessChange())
		{
			GlobalData.logE("ActivateProfileHelper.execute", "set brightness: profile="+profile._name);
			GlobalData.logE("ActivateProfileHelper.execute", "set brightness: _deviceBrightness="+profile._deviceBrightness);
			
			if (profile.getDeviceBrightnessAutomatic())
				Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
			else
			{
				Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
				Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, profile.getDeviceBrightnessValue());
			}
			
			if ((activity != null) && (activity instanceof BackgroundActivateProfileActivity))
			{
				Window window = activity.getWindow();
				LayoutParams layoutParams = window.getAttributes();
				
				if (profile.getDeviceBrightnessAutomatic())
					layoutParams.screenBrightness = LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
				else
					layoutParams.screenBrightness = profile.getDeviceBrightnessValue() / 255.0f;
				window.setAttributes(layoutParams);
			}
			else
			{
				Intent i = new Intent(context, SetBrightnessWindowAttributesActivity.class);
			    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			    context.startActivity(i);   				
			}
		}
		
		// nahodenie rotate
		switch (profile._deviceAutoRotate) {
			case 1:
				// set autorotate on
				Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
				Settings.System.putInt(context.getContentResolver(), Settings.System.USER_ROTATION, Surface.ROTATION_0);
				break;
			case 2:
				// set autorotate off
				// degree 0
				Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
				Settings.System.putInt(context.getContentResolver(), Settings.System.USER_ROTATION, Surface.ROTATION_0);
				break;
			case 3:
				// set autorotate off
				// degree 90
				Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
				Settings.System.putInt(context.getContentResolver(), Settings.System.USER_ROTATION, Surface.ROTATION_90);
				break;
			case 4:
				// set autorotate off
				// degree 180
				Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
				Settings.System.putInt(context.getContentResolver(), Settings.System.USER_ROTATION, Surface.ROTATION_180);
				break;
			case 5:
				// set autorotate off
				// degree 270
				Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
				Settings.System.putInt(context.getContentResolver(), Settings.System.USER_ROTATION, Surface.ROTATION_270);
				break;
		}
		
		// nahodenie pozadia
		if (profile._deviceWallpaperChange == 1)
		{
			//Log.d("ActivateProfileHelper.execute","set wallpaper");
			DisplayMetrics displayMetrics = new DisplayMetrics();
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			display.getMetrics(displayMetrics);
			int height = displayMetrics.heightPixels;
			int width = displayMetrics.widthPixels << 1; // best wallpaper width is twice screen width
			Bitmap decodedSampleBitmap = BitmapManipulator.resampleBitmap(profile.getDeviceWallpaperIdentifier(), width, height);
			if (decodedSampleBitmap != null)
			{
				// set wallpaper
				WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
				try {
					wallpaperManager.setBitmap(decodedSampleBitmap);
				} catch (IOException e) {
					Log.e("ActivateProfileHelper.execute", "Cannot set wallpaper. Image="+profile.getDeviceWallpaperIdentifier());
				}
			}
		}
		
		if (interactive)
		{
			// preferences, ktore vyzaduju interakciu uzivatela
			
			if (GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_MOBILE_DATA, context) == GlobalData.HARDWARE_CHECK_ALLOWED)
			{
				if (profile._deviceMobileDataPrefs == 1)
				{
		    		final Intent intent = new Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
		    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
			}
			
			//if (GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_GPS, context))
			//{  No check only GPS
				if (profile._deviceLocationServicePrefs == 1)
				{
				    final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    context.startActivity(intent);
				}
			//}			

			if (profile._deviceRunApplicationChange == 1)
			{
				Intent intent;
				PackageManager packageManager = context.getPackageManager();
				intent = packageManager.getLaunchIntentForPackage(profile._deviceRunApplicationPackageName);
				if (intent != null)
				{
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
		    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
			}
		}
		
	}
	
	//@SuppressWarnings("deprecation")
	public void showNotification(Profile profile)
	{
		if (GlobalData.notificationStatusBar)
		{	
			// close showed notification
			//notificationManager.cancel(GlobalData.NOTIFICATION_ID);

			// vytvorenie intentu na aktivitu, ktora sa otvori na kliknutie na notifikaciu
			Intent intent = new Intent(context, LauncherActivity.class);
			// nastavime, ze aktivita sa spusti z notifikacnej listy
			intent.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_NOTIFICATION);
			PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			
			// vytvorenie intentu na restart events
			Intent intentRE = new Intent(context, RestartEventsFromNotificationActivity.class);
			PendingIntent pIntentRE = PendingIntent.getActivity(context, 0, intentRE, PendingIntent.FLAG_CANCEL_CURRENT);
			
			// vytvorenie samotnej notifikacie
			NotificationCompat.Builder notificationBuilder;
	        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_drawer);

			boolean isIconResourceID;
			String iconIdentifier;
			String profileName;
			Bitmap iconBitmap;
			Bitmap preferencesIndicator;
			
			if (profile != null)
			{
				isIconResourceID = profile.getIsIconResourceID();
				iconIdentifier = profile.getIconIdentifier();
				profileName = dataWrapper.getProfileNameWithManualIndicator(profile, true);
				iconBitmap = profile._iconBitmap;
				preferencesIndicator = profile._preferencesIndicator;
			}
			else
			{
				isIconResourceID = true;
				iconIdentifier = GlobalData.PROFILE_ICON_DEFAULT;
				profileName = context.getResources().getString(R.string.profiles_header_profile_name_no_activated);
				iconBitmap = null;
				preferencesIndicator = null;
			}
	        
			if (isIconResourceID)
	        {
	        	notificationBuilder = new NotificationCompat.Builder(context)
	        		.setContentIntent(pIntent);
				

	        	int iconSmallResource;
	    		if (GlobalData.notificationStatusBarStyle.equals("0"))
	    		{
					//notificationBuilder.setSmallIcon(0);
	    			iconSmallResource = context.getResources().getIdentifier(iconIdentifier, "drawable", context.getPackageName());
					notificationBuilder.setSmallIcon(iconSmallResource);
			        //contentView.setImageViewResource(R.id.notification_activated_profile_icon, 0);
			        contentView.setImageViewResource(R.id.notification_activated_profile_icon, iconSmallResource);
	    		}
	    		else
	    		{
					//notificationBuilder.setSmallIcon(0);
	    			//contentView.setImageViewBitmap(R.id.notification_activated_profile_icon, null);
	    			iconSmallResource = context.getResources().getIdentifier(iconIdentifier+"_notify", "drawable", context.getPackageName());
					notificationBuilder.setSmallIcon(iconSmallResource);
	    			int iconLargeResource = context.getResources().getIdentifier(iconIdentifier, "drawable", context.getPackageName());
	    			Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), iconLargeResource);
	    			contentView.setImageViewBitmap(R.id.notification_activated_profile_icon, largeIcon);
	    		}
	        }
	        else
	        {
	        	int iconSmallResource;
	    		if (GlobalData.notificationStatusBarStyle.equals("0"))
	    			iconSmallResource = R.drawable.ic_profile_default;
	    		else
	    			iconSmallResource = R.drawable.ic_profile_default_notify;
	        			
	        	notificationBuilder = new NotificationCompat.Builder(context)
        			.setContentIntent(pIntent);
        	
	        	//notificationBuilder.setSmallIcon(0);
	        	notificationBuilder.setSmallIcon(iconSmallResource);

    			//contentView.setImageViewBitmap(R.id.notification_activated_profile_icon, null);
    			contentView.setImageViewBitmap(R.id.notification_activated_profile_icon, iconBitmap);

	        }

			
	        Notification notification = notificationBuilder.build();
			
	        contentView.setTextViewText(R.id.notification_activated_profile_name, profileName);

	        //contentView.setImageViewBitmap(R.id.notification_activated_profile_pref_indicator, 
	        //		ProfilePreferencesIndicator.paint(profile, context));
	        if ((preferencesIndicator != null) && (GlobalData.notificationPrefIndicator))
	        	contentView.setImageViewBitmap(R.id.notification_activated_profile_pref_indicator, preferencesIndicator);
	        else
	        	contentView.setImageViewResource(R.id.notification_activated_profile_pref_indicator, R.drawable.ic_empty);
	        
	        contentView.setOnClickPendingIntent(R.id.notification_activated_profile_restart_events, pIntentRE);
	        
	        notification.contentView = contentView;
	        
	        if (GlobalData.notificationStatusBarPermanent)
	        {
	        	//notification.flags |= Notification.FLAG_NO_CLEAR; 
	        	notification.flags |= Notification.FLAG_ONGOING_EVENT;
	        }
			notificationManager.notify(GlobalData.NOTIFICATION_ID, notification);
		}
		else
		{
			notificationManager.cancel(GlobalData.NOTIFICATION_ID);
		}
	}
	
	public void removeNotification()
	{
		notificationManager.cancel(GlobalData.NOTIFICATION_ID);
	}
	
	public void updateWidget()
	{
		// icon widget
		Intent intent = new Intent(context, IconWidgetProvider.class);
		intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
		int ids[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, IconWidgetProvider.class));
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		context.sendBroadcast(intent);

		// one row widget
		Intent intent4 = new Intent(context, OneRowWidgetProvider.class);
		intent4.setAction("android.appwidget.action.APPWIDGET_UPDATE");
		int ids4[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, OneRowWidgetProvider.class));
		intent4.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids4);
		context.sendBroadcast(intent4);
		
		// list widget
		Intent intent2 = new Intent(context, ProfileListWidgetProvider.class);
		intent2.setAction(ProfileListWidgetProvider.INTENT_REFRESH_LISTWIDGET);
		int ids2[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, ProfileListWidgetProvider.class));
		intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids2);
		context.sendBroadcast(intent2);

		// dashclock extension
		Intent intent3 = new Intent();
	    intent3.setAction(DashClockBroadcastReceiver.INTENT_REFRESH_DASHCLOCK);
		context.sendBroadcast(intent3);

		// activities
		Intent intent5 = new Intent();
	    intent5.setAction(RefreshGUIBroadcastReceiver.INTENT_REFRESH_GUI);
		context.sendBroadcast(intent5);
		
	}
	
	

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private boolean isAirplaneMode(Context context)
	{
    	if (android.os.Build.VERSION.SDK_INT >= 17)
    		return Settings.Global.getInt(context.getContentResolver(), Global.AIRPLANE_MODE_ON, 0) != 0;
    	else
    		return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
	}
	
	private void setAirplaneMode(Context context, boolean mode)
	{
    	if (android.os.Build.VERSION.SDK_INT >= 17)
    		setAirplaneMode_SDK17(context, mode);
    	else
    		setAirplaneMode_SDK8(context, mode);
	}
	
	private boolean isMobileData(Context context)
	{
		final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		try {
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
		
		/*
		final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null)
		{
			int netvorkType = networkInfo.getType(); // 0 = mobile, 1 = wifi
			//String netvorkTypeName = networkInfo.getTypeName(); // "mobile" or "WIFI"
			boolean connected = networkInfo.isConnected();  // true = active connection
			
			//if (netvorkType == 0)
			//{
				// connected into mobile data
				return connected;
			//}
			//else
			//{
				// conected into Wifi
			//	return false;
			//}
		}
		else
			return false;
		*/
		
	}
	
	private void setMobileData(Context context, boolean enable)
	{
		final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

		boolean OK = false;
		try {
			final Class<?> connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
			final Field iConnectivityManagerField = connectivityManagerClass.getDeclaredField("mService");
			iConnectivityManagerField.setAccessible(true);
			final Object iConnectivityManager = iConnectivityManagerField.get(connectivityManager);
			final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
			final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);
			
			setMobileDataEnabledMethod.invoke(iConnectivityManager, enable);
			
			OK = true;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		if (!OK)
		{
			try {
		        Method setMobileDataEnabledMethod = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
		
		        setMobileDataEnabledMethod.setAccessible(true);
		        setMobileDataEnabledMethod.invoke(connectivityManager, enable);
	
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void setGPS(Context context, boolean enable)
	{
		boolean isEnabled = Settings.Secure.isLocationProviderEnabled(context.getContentResolver(), LocationManager.GPS_PROVIDER);

		//Log.d("ActivateProfileHelper.setGPS", isEnabled + "");
	    
	    //if(!provider.contains(LocationManager.GPS_PROVIDER) && enable)
		if ((!isEnabled)  && enable)
	    {
    		//Log.d("ActivateProfileHelper.setGPS", "enable=true");
	    	if (GlobalData.canExploitGPS(context))
	    	{
				GlobalData.logE("ActivateProfileHelper.setGPS", "exploit");

		        final Intent poke = new Intent();
		        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
		        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
		        poke.setData(Uri.parse("3")); 
		        context.sendBroadcast(poke);
	    	}
	    	else
	    	if ((android.os.Build.VERSION.SDK_INT >= 17) && GlobalData.grantRoot(false))
			{
				// zariadenie je rootnute
				GlobalData.logE("ActivateProfileHelper.setGPS", "rooted");

				String command1;
				//String command2;

				String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
				
	    		String newSet;
	    		if (provider == "")
	    			newSet = LocationManager.GPS_PROVIDER;
	    		else
	    			newSet = String.format("%s,%s", provider, LocationManager.GPS_PROVIDER);
				
				command1 = "settings put secure location_providers_allowed \"" + newSet + "\"";
	    		//command1 = "sqlite3 /data/data/com.android.providers.settings/databases/settings.db \'update secure set value=\"" + newSet + "\" where name=\"location_providers_allowed\";\'";
	    		//Log.e("ActivateProfileHelper.setGPS","command1="+command1);
				//command2 = "am broadcast -a android.location.GPS_ENABLED_CHANGE --ez state true";
				CommandCapture command = new CommandCapture(0, command1); //, command2);
				try {
	    		/*	Command command = new Command(0, command1)
	    			{
							@Override
							public void commandCompleted(int arg0, int arg1) {
								
							}

							@Override
							public void commandOutput(int arg0, String arg1) {
								Log.e("ActivateProfileHelper.setGPS", arg1);
							}

							@Override
							public void commandTerminated(int arg0, String arg1) {
								
							}
	    			};*/	    			
					RootTools.getShell(true).add(command);
					commandWait(command);
					RootTools.closeAllShells();
				} catch (Exception e) {
					Log.e("ActivateProfileHelper.setGPS", "Error on run su: "+e.toString());
				} 
			}	    	
			else
			{
				/*GlobalData.logE("ActivateProfileHelper.setGPS", "old method");

				try {
					Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
					intent.putExtra("enabled", enable);
					context.sendBroadcast(intent); 
				} catch (SecurityException e) {
					e.printStackTrace();
				}*/

				// for normal apps it is only possible to open the system settings dialog
			/*	Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent); */ 
			}
	    }
	    else
        //if(provider.contains(LocationManager.GPS_PROVIDER) && (!enable))
		if (isEnabled && (!enable))
        {
    		//Log.d("ActivateProfileHelper.setGPS", "enable=false");
    		if (GlobalData.canExploitGPS(context))
	    	{
				GlobalData.logE("ActivateProfileHelper.setGPS", "exploit");
				
	            final Intent poke = new Intent();
	            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
	            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	            poke.setData(Uri.parse("3")); 
	            context.sendBroadcast(poke);
	    	}
	    	else
	    	if ((android.os.Build.VERSION.SDK_INT >= 17) && GlobalData.grantRoot(false))
			{
				// zariadenie je rootnute
				GlobalData.logE("ActivateProfileHelper.setGPS", "rooted");
				
				String command1;
				//String command2;

			    String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
				
	    		String[] list = provider.split(",");
	    		
	    		String newSet = "";
	    		int j = 0;
	    		for (int i = 0; i < list.length; i++)
	    		{
	    			
	    			if  (!list[i].equals(LocationManager.GPS_PROVIDER))
	    			{
	    				if (j > 0)
	    					newSet += ",";
	    				newSet += list[i];
	    				j++;
	    			}
	    		}
				
				command1 = "settings put secure location_providers_allowed \"" + newSet + "\"";
	    		//command1 = "sqlite3 /data/data/com.android.providers.settings/databases/settings.db \'update secure set value=\"" + newSet + "\" where name=\"location_providers_allowed\";\'";
	    		//Log.e("ActivateProfileHelper.setGPS","command1="+command1);
				//command2 = "am broadcast -a android.location.GPS_ENABLED_CHANGE --ez state false";
				CommandCapture command = new CommandCapture(0, command1);//, command2);
	    		try {
	    		/*	Command command = new Command(0, command1)
	    			{
							@Override
							public void commandCompleted(int arg0, int arg1) {
								
							}

							@Override
							public void commandOutput(int arg0, String arg1) {
								Log.e("ActivateProfileHelper.setGPS", arg1);
							}

							@Override
							public void commandTerminated(int arg0, String arg1) {
								
							}
	    			};	*/    			
					RootTools.getShell(true).add(command);
					commandWait(command);
					RootTools.closeAllShells();
				} catch (Exception e) {
					Log.e("ActivateProfileHelper.setGPS", "Error on run su: "+e.toString());
				}
			}	    	
			else
			{
	    		//GlobalData.logE("ActivateProfileHelper.setGPS", "old method");
				
				/*try {
					Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
					intent.putExtra("enabled", enable);
					context.sendBroadcast(intent); 
				} catch (SecurityException e) {
					e.printStackTrace();
				}*/

				// for normal apps it is only possible to open the system settings dialog
			/*	Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent); */ 
			}
        }	    	
	}
	
	private void setAirplaneMode_SDK17(Context context, boolean mode)
	{
		if (GlobalData.grantRoot(false))
		{
			// zariadenie je rootnute
			String command1;
			String command2;
			if (mode)
			{
				command1 = "settings put global airplane_mode_on 1";
				command2 = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true";
			}
			else
			{
				command1 = "settings put global airplane_mode_on 0";
				command2 = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false";
			}
			CommandCapture command = new CommandCapture(0, command1, command2);
			try {
				RootTools.getShell(true).add(command);
				commandWait(command);
				RootTools.closeAllShells();
			} catch (Exception e) {
				Log.e("AirPlaneMode_SDK17.setAirplaneMode", "Error on run su");
			}
		}
		else
		{
			// for normal apps it is only possible to open the system settings dialog
		/*	Intent intent = new Intent(android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent); */
		}
		
	}
	
	@SuppressWarnings("deprecation")
	private void setAirplaneMode_SDK8(Context context, boolean mode)
	{
		Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, mode ? 1 : 0);
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", mode);
		context.sendBroadcast(intent);
	}
	

	private void commandWait(Command cmd) throws Exception {
        int waitTill = 50;
        int waitTillMultiplier = 2;
        int waitTillLimit = 3200; //7 tries, 6350 msec

        while (!cmd.isFinished() && waitTill<=waitTillLimit) {
            synchronized (cmd) {
                try {
                    if (!cmd.isFinished()) {
                        cmd.wait(waitTill);
                        waitTill *= waitTillMultiplier;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!cmd.isFinished()){
            Log.e("ActivateProfileHelper", "Could not finish root command in " + (waitTill/waitTillMultiplier));
        }
    }	

}
