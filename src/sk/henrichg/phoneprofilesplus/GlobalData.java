package sk.henrichg.phoneprofilesplus;

import com.stericson.RootTools.RootTools;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.provider.Settings;

public class GlobalData extends Application {

	static String PACKAGE_NAME;

	// musi byt tu, pouziva t ActivateProfileHelper
	static final String EXTRA_PROFILE_ID = "profile_id";
	static final String EXTRA_EVENT_ID = "event_id";
	static final String EXTRA_START_APP_SOURCE = "start_app_source";
	static final String EXTRA_RESET_EDITOR = "reset_editor";
	//static final String EXTRA_FIRST_START_ACTIVITY = "restart_activity";
	static final String EXTRA_NEW_PROFILE_MODE = "new_profile_mode";
	static final String EXTRA_NEW_EVENT_MODE = "new_event_mode";
	static final String EXTRA_PREFERENCES_ACTIVITY = "preferences_activity";

	// musi byt tu, pouziva to ActivateProfileHelper
	static final int STARTUP_SOURCE_NOTIFICATION = 1;
	static final int STARTUP_SOURCE_WIDGET = 2;
	static final int STARTUP_SOURCE_SHORTCUT = 3;
	static final int STARTUP_SOURCE_BOOT = 4;
	static final int STARTUP_SOURCE_ACTIVATOR = 5;
	static final int STARTUP_SOURCE_SERVICE = 6;
	static final int STARTUP_SOURCE_SERVICE_INTERACTIVE = 7;
	static final int STARTUP_SOURCE_EDITOR = 8;
	static final int STARTUP_SOURCE_ACTIVATOR_START = 9;

	// request code for startActivityForResult with intent BackgroundActivateProfileActivity
	static final int REQUEST_CODE_ACTIVATE_PROFILE = 6220;
	// request code for startActivityForResult with intent ProfilePreferencesFragmentActivity
	static final int REQUEST_CODE_PROFILE_PREFERENCES = 6221;
	// request code for startActivityForResult with intent EventPreferencesFragmentActivity
	static final int REQUEST_CODE_EVENT_PREFERENCES = 6222;
	// request code for startActivityForResult with intent PhoneProfilesActivity
	static final int REQUEST_CODE_APPLICATION_PREFERENCES = 6229;
	// request code for startActivityForResult with intent "phoneprofiles.intent.action.EXPORTDATA"
	static final int REQUEST_CODE_REMOTE_EXPORT = 6250;
	
	// musi byt tu, pouziva to ActivateProfileHelper
	static final int NOTIFICATION_ID = 700420;

	static final String PREF_PROFILE_NAME = "prf_pref_profileName";
	static final String PREF_PROFILE_ICON = "prf_pref_profileIcon";
	static final String PREF_PROFILE_VOLUME_RINGER_MODE = "prf_pref_volumeRingerMode";
	static final String PREF_PROFILE_VOLUME_RINGTONE = "prf_pref_volumeRingtone";
	static final String PREF_PROFILE_VOLUME_NOTIFICATION = "prf_pref_volumeNotification";
	static final String PREF_PROFILE_VOLUME_MEDIA = "prf_pref_volumeMedia";
	static final String PREF_PROFILE_VOLUME_ALARM = "prf_pref_volumeAlarm";
	static final String PREF_PROFILE_VOLUME_SYSTEM = "prf_pref_volumeSystem";
	static final String PREF_PROFILE_VOLUME_VOICE = "prf_pref_volumeVoice";
	static final String PREF_PROFILE_SOUND_RINGTONE_CHANGE = "prf_pref_soundRingtoneChange";
	static final String PREF_PROFILE_SOUND_RINGTONE = "prf_pref_soundRingtone";
	static final String PREF_PROFILE_SOUND_NOTIFICATION_CHANGE = "prf_pref_soundNotificationChange";
	static final String PREF_PROFILE_SOUND_NOTIFICATION = "prf_pref_soundNotification";
	static final String PREF_PROFILE_SOUND_ALARM_CHANGE = "prf_pref_soundAlarmChange";
	static final String PREF_PROFILE_SOUND_ALARM = "prf_pref_soundAlarm";
	static final String PREF_PROFILE_DEVICE_AIRPLANE_MODE = "prf_pref_deviceAirplaneMode";
	static final String PREF_PROFILE_DEVICE_WIFI = "prf_pref_deviceWiFi";
	static final String PREF_PROFILE_DEVICE_BLUETOOTH = "prf_pref_deviceBluetooth";
	static final String PREF_PROFILE_DEVICE_SCREEN_TIMEOUT = "prf_pref_deviceScreenTimeout";
	static final String PREF_PROFILE_DEVICE_BRIGHTNESS = "prf_pref_deviceBrightness";
	static final String PREF_PROFILE_DEVICE_WALLPAPER_CHANGE = "prf_pref_deviceWallpaperChange";
	static final String PREF_PROFILE_DEVICE_WALLPAPER = "prf_pref_deviceWallpaper";
	static final String PREF_PROFILE_DEVICE_MOBILE_DATA = "prf_pref_deviceMobileData";
	static final String PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS = "prf_pref_deviceMobileDataPrefs";
	static final String PREF_PROFILE_DEVICE_GPS = "prf_pref_deviceGPS";
	static final String PREF_PROFILE_DEVICE_RUN_APPLICATION_CHANGE = "prf_pref_deviceRunApplicationChange";
	static final String PREF_PROFILE_DEVICE_RUN_APPLICATION_PACKAGE_NAME = "prf_pref_deviceRunApplicationPackageName";
	static final String PREF_PROFILE_DEVICE_AUTOSYNC = "prf_pref_deviceAutosync";
	static final String PREF_PROFILE_SHOW_IN_ACTIVATOR = "prf_pref_showInActivator";
	static final String PREF_PROFILE_DEVICE_AUTOROTATE = "prf_pref_deviceAutoRotation";
	
	static final String PROFILE_ICON_DEFAULT = "ic_profile_default";
	
	static final String APPLICATION_PREFS_NAME = "phone_profile_preferences";
	
    public static final String PREF_APPLICATION_START_ON_BOOT = "applicationStartOnBoot";
    public static final String PREF_APPLICATION_ACTIVATE = "applicationActivate";
    public static final String PREF_APPLICATION_ALERT = "applicationAlert";
    public static final String PREF_APPLICATION_CLOSE = "applicationClose";
    public static final String PREF_APPLICATION_LONG_PRESS_ACTIVATION = "applicationLongClickActivation";
    public static final String PREF_APPLICATION_LANGUAGE = "applicationLanguage";
    public static final String PREF_APPLICATION_THEME = "applicationTheme";
    public static final String PREF_APPLICATION_ACTIVATOR_PREF_INDICATOR = "applicationActivatorPrefIndicator";
    public static final String PREF_APPLICATION_EDITOR_PREF_INDICATOR = "applicationEditorPrefIndicator";
    public static final String PREF_APPLICATION_ACTIVATOR_HEADER = "applicationActivatorHeader";
    public static final String PREF_APPLICATION_EDITOR_HEADER = "applicationEditorHeader";
    public static final String PREF_NOTIFICATION_TOAST = "notificationsToast";
    public static final String PREF_NOTIFICATION_STATUS_BAR  = "notificationStatusBar";
    public static final String PREF_NOTIFICATION_STATUS_BAR_STYLE  = "notificationStatusBarStyle";
    public static final String PREF_APPLICATION_WIDGET_LIST_PREF_INDICATOR = "applicationWidgetListPrefIndicator";
    public static final String PREF_APPLICATION_WIDGET_LIST_HEADER = "applicationWidgetListHeader";
    public static final String PREF_APPLICATION_WIDGET_LIST_BACKGROUND = "applicationWidgetListBackground";
    public static final String PREF_APPLICATION_WIDGET_LIST_LIGHTNESS_B = "applicationWidgetListLightnessB";
    public static final String PREF_APPLICATION_WIDGET_LIST_LIGHTNESS_T = "applicationWidgetListLightnessT";
    public static final String PREF_APPLICATION_WIDGET_ICON_COLOR = "applicationWidgetIconColor";
    public static final String PREF_APPLICATION_WIDGET_ICON_LIGHTNESS = "applicationWidgetIconLightness";
    public static final String PREF_APPLICATION_WIDGET_LIST_ICON_COLOR = "applicationWidgetListIconColor";
    public static final String PREF_APPLICATION_WIDGET_LIST_ICON_LIGHTNESS = "applicationWidgetListIconLightness";
	public static final String PREF_APPLICATION_EDITOR_AUTO_CLOSE_DRAWER = "applicationEditorAutoCloseDrawer";

	private static final String PREF_GLOBAL_EVENTS_RUN_STOP = "globalEventsRunStop";
	
    public static boolean applicationStartOnBoot;
    public static boolean applicationActivate;
    public static boolean applicationActivateWithAlert;
    public static boolean applicationClose;
    public static boolean applicationLongClickActivation;
    public static String applicationLanguage;
    public static String applicationTheme;
    public static boolean applicationActivatorPrefIndicator;
    public static boolean applicationEditorPrefIndicator;
    public static boolean applicationActivatorHeader;
    public static boolean applicationEditorHeader;
    public static boolean notificationsToast;
    public static boolean notificationStatusBar;
    public static String notificationStatusBarStyle;
    public static boolean applicationWidgetListPrefIndicator;
    public static boolean applicationWidgetListHeader;
    public static String applicationWidgetListBackground;
    public static String applicationWidgetListLightnessB;
    public static String applicationWidgetListLightnessT;
    public static String applicationWidgetIconColor;
    public static String applicationWidgetIconLightness;
    public static String applicationWidgetListIconColor;
    public static String applicationWidgetListIconLightness;
    public static boolean applicationEditorAutoCloseDrawer;
    
    
	public void onCreate()
	{
	//	Debug.startMethodTracing("phoneprofiles");
		
		super.onCreate();
		
		PACKAGE_NAME = this.getPackageName();
		
		// initialization
		loadPreferences(this);

		//Log.d("GlobalData.onCreate", "memory usage (after create activateProfileHelper)=" + Debug.getNativeHeapAllocatedSize());
		
		//Log.d("GlobalData.onCreate","xxx");
		
	}
	
	public void onTerminate ()
	{
		DatabaseHandler.getInstance(this).closeConnecion();
	}
	
	//--------------------------------------------------------------
	
	static public void loadPreferences(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);

	    applicationStartOnBoot = preferences.getBoolean(PREF_APPLICATION_START_ON_BOOT, false);
	    applicationActivate = preferences.getBoolean(PREF_APPLICATION_ACTIVATE, true);
	    applicationActivateWithAlert = preferences.getBoolean(PREF_APPLICATION_ALERT, true);
	    applicationClose = preferences.getBoolean(PREF_APPLICATION_CLOSE, true);
	    applicationLongClickActivation = preferences.getBoolean(PREF_APPLICATION_LONG_PRESS_ACTIVATION, false);
	    applicationLanguage = preferences.getString(PREF_APPLICATION_LANGUAGE, "system");
	    applicationTheme = preferences.getString(PREF_APPLICATION_THEME, "light");
	    applicationActivatorPrefIndicator = preferences.getBoolean(PREF_APPLICATION_ACTIVATOR_PREF_INDICATOR, true);
	    applicationEditorPrefIndicator = preferences.getBoolean(PREF_APPLICATION_EDITOR_PREF_INDICATOR, true);
	    applicationActivatorHeader = preferences.getBoolean(PREF_APPLICATION_ACTIVATOR_HEADER, true);
	    applicationEditorHeader = preferences.getBoolean(PREF_APPLICATION_EDITOR_HEADER, true);
	    notificationsToast = preferences.getBoolean(PREF_NOTIFICATION_TOAST, true);
	    notificationStatusBar = preferences.getBoolean(PREF_NOTIFICATION_STATUS_BAR, true);
	    notificationStatusBarStyle = preferences.getString(PREF_NOTIFICATION_STATUS_BAR_STYLE, "0");
	    applicationWidgetListPrefIndicator = preferences.getBoolean(PREF_APPLICATION_WIDGET_LIST_PREF_INDICATOR, true);
	    applicationWidgetListHeader = preferences.getBoolean(PREF_APPLICATION_WIDGET_LIST_HEADER, true);
	    applicationWidgetListBackground = preferences.getString(PREF_APPLICATION_WIDGET_LIST_BACKGROUND, "25");
	    applicationWidgetListLightnessB = preferences.getString(PREF_APPLICATION_WIDGET_LIST_LIGHTNESS_B, "0");
	    applicationWidgetListLightnessT = preferences.getString(PREF_APPLICATION_WIDGET_LIST_LIGHTNESS_T, "100");
	    applicationWidgetIconColor = preferences.getString(PREF_APPLICATION_WIDGET_ICON_COLOR, "0");
	    applicationWidgetIconLightness = preferences.getString(PREF_APPLICATION_WIDGET_ICON_LIGHTNESS, "100");;
	    applicationWidgetListIconColor = preferences.getString(PREF_APPLICATION_WIDGET_LIST_ICON_COLOR, "0");
	    applicationWidgetListIconLightness = preferences.getString(PREF_APPLICATION_WIDGET_LIST_ICON_LIGHTNESS, "100");;
	    applicationEditorAutoCloseDrawer = preferences.getBoolean(PREF_APPLICATION_EDITOR_AUTO_CLOSE_DRAWER, true);
		
	}
	
	private static String getVolumeLevelString(int percentage, int maxValue)
	{
		Double dValue = maxValue / 100.0 * percentage;
		return String.valueOf(dValue.intValue());
	}
	
	static public Profile getDefaultProfile(Context context)
	{
		AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		int	maximumValueRing = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
		int	maximumValueNotification = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
		int	maximumValueMusic = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int	maximumValueAlarm = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
		int	maximumValueSystem = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		int	maximumValueVoicecall = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
		
		SharedPreferences preferences = context.getSharedPreferences(APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		
		Profile profile = new Profile();
		profile._id = -999;
		profile._name = context.getResources().getString(R.string.default_profile_name);
		profile._icon = PROFILE_ICON_DEFAULT;
		profile._checked = false;
		profile._porder = 0;
    	profile._volumeRingerMode = Integer.parseInt(preferences.getString(GlobalData.PREF_PROFILE_VOLUME_RINGER_MODE, "1")); // ring
    	profile._volumeRingtone = preferences.getString(GlobalData.PREF_PROFILE_VOLUME_RINGTONE, getVolumeLevelString(71, maximumValueRing)+"|0");
    	profile._volumeNotification = preferences.getString(GlobalData.PREF_PROFILE_VOLUME_NOTIFICATION, getVolumeLevelString(86, maximumValueNotification)+"|0");
    	profile._volumeMedia = preferences.getString(GlobalData.PREF_PROFILE_VOLUME_MEDIA, getVolumeLevelString(80, maximumValueMusic)+"|0");
    	profile._volumeAlarm = preferences.getString(GlobalData.PREF_PROFILE_VOLUME_ALARM, getVolumeLevelString(100, maximumValueAlarm)+"|0");
    	profile._volumeSystem = preferences.getString(GlobalData.PREF_PROFILE_VOLUME_SYSTEM, getVolumeLevelString(70, maximumValueSystem)+"|0");
    	profile._volumeVoice = preferences.getString(GlobalData.PREF_PROFILE_VOLUME_VOICE, getVolumeLevelString(70, maximumValueVoicecall)+"|0");
    	profile._soundRingtoneChange = preferences.getBoolean(GlobalData.PREF_PROFILE_SOUND_RINGTONE_CHANGE, true);
    	profile._soundRingtone = preferences.getString(GlobalData.PREF_PROFILE_SOUND_RINGTONE, Settings.System.DEFAULT_RINGTONE_URI.toString());
    	profile._soundNotificationChange = preferences.getBoolean(GlobalData.PREF_PROFILE_SOUND_NOTIFICATION_CHANGE, true);
    	profile._soundNotification = preferences.getString(GlobalData.PREF_PROFILE_SOUND_NOTIFICATION, Settings.System.DEFAULT_NOTIFICATION_URI.toString());
    	profile._soundAlarmChange = preferences.getBoolean(GlobalData.PREF_PROFILE_SOUND_ALARM_CHANGE, true);
    	profile._soundAlarm = preferences.getString(GlobalData.PREF_PROFILE_SOUND_ALARM, Settings.System.DEFAULT_ALARM_ALERT_URI.toString());
    	profile._deviceAirplaneMode = Integer.parseInt(preferences.getString(GlobalData.PREF_PROFILE_DEVICE_AIRPLANE_MODE, "2")); // OFF
    	profile._deviceWiFi = Integer.parseInt(preferences.getString(GlobalData.PREF_PROFILE_DEVICE_WIFI, "2")); // OFF
    	profile._deviceBluetooth = Integer.parseInt(preferences.getString(GlobalData.PREF_PROFILE_DEVICE_BLUETOOTH, "2")); //OFF
    	profile._deviceScreenTimeout = Integer.parseInt(preferences.getString(GlobalData.PREF_PROFILE_DEVICE_SCREEN_TIMEOUT, "2")); // 30 seconds
    	profile._deviceBrightness = preferences.getString(GlobalData.PREF_PROFILE_DEVICE_BRIGHTNESS, "100|0|0");  // automatic on
    	profile._deviceWallpaperChange = preferences.getBoolean(GlobalData.PREF_PROFILE_DEVICE_WALLPAPER_CHANGE, false);
   		profile._deviceWallpaper = preferences.getString(GlobalData.PREF_PROFILE_DEVICE_WALLPAPER, "-|0");
    	profile._deviceMobileData = Integer.parseInt(preferences.getString(GlobalData.PREF_PROFILE_DEVICE_MOBILE_DATA, "1")); //ON
    	profile._deviceMobileDataPrefs = preferences.getBoolean(GlobalData.PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS, false);
    	profile._deviceGPS = Integer.parseInt(preferences.getString(GlobalData.PREF_PROFILE_DEVICE_GPS, "2")); //OFF
    	profile._deviceRunApplicationChange = preferences.getBoolean(GlobalData.PREF_PROFILE_DEVICE_RUN_APPLICATION_CHANGE, false);
   		profile._deviceRunApplicationPackageName = preferences.getString(GlobalData.PREF_PROFILE_DEVICE_RUN_APPLICATION_PACKAGE_NAME, "-");
    	profile._deviceAutosync = Integer.parseInt(preferences.getString(GlobalData.PREF_PROFILE_DEVICE_AUTOSYNC, "1")); // ON
    	profile._deviceAutoRotate = Integer.parseInt(preferences.getString(GlobalData.PREF_PROFILE_DEVICE_AUTOROTATE, "1")); // ON
    	
    	return profile;
	}
	
	static public boolean getGlobalEventsRuning(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(PREF_GLOBAL_EVENTS_RUN_STOP, true);
	}
	
	static public void setGlobalEventsRuning(Context context, boolean globalEventsRuning)
	{
		SharedPreferences preferences = context.getSharedPreferences(APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(PREF_GLOBAL_EVENTS_RUN_STOP, globalEventsRuning);
		editor.commit();
	}

	// ----- Hardware check -------------------------------------
	
	static private boolean rootChecked = false;
	static private boolean rooted = false;
	static public boolean rootGranted = false;

	static boolean hardwareCheck(String preferenceKey, Context context)
	{
		boolean featurePresented = false;

		if (preferenceKey.equals(PREF_PROFILE_DEVICE_AIRPLANE_MODE))
		{	
			if (android.os.Build.VERSION.SDK_INT >= 17)
			{
				if (isRooted())
				{
					// zariadenie je rootnute
					featurePresented = true;
				}
				else
				//if (isSystemApp(context) && isAdminUser(context))
				if (isSystemApp(context))
				{
					// aplikacia je nainstalovana ako systemova
					featurePresented = true;
				}
			}
			else
				featurePresented = true;
		}
		else
		if (preferenceKey.equals(PREF_PROFILE_DEVICE_WIFI))
		{	
			if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI))
				// device ma Wifi
				featurePresented = true;
		}
		else
		if (preferenceKey.equals(PREF_PROFILE_DEVICE_BLUETOOTH))
		{	
			if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
				// device ma bluetooth
				featurePresented = true;
		}
		else
		if (preferenceKey.equals(PREF_PROFILE_DEVICE_MOBILE_DATA))
		{	
			if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY))
				// device ma mobilne data
				featurePresented = true;
		}
		else
		if (preferenceKey.equals(PREF_PROFILE_DEVICE_GPS))
		{	
			if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS))
			{
				// device ma gps

			/*	if (canExploitGPS(context))
				{
					featurePresented = true;
			    }
				else
				if ((android.os.Build.VERSION.SDK_INT >= 17) && isRooted())
				{
					featurePresented = true;
				}
				else 
				//if (isSystemApp(context) && isAdminUser(context))
				if (isSystemApp(context))
				{
					// aplikacia je nainstalovana ako systemova
					featurePresented = true;
			    } */
				featurePresented = true;
			}
		}
		else
			featurePresented = true;
		
		return featurePresented;
	}
	
	static boolean canExploitGPS(Context context)
	{
		// test expoiting power manager widget
	    PackageManager pacman = context.getPackageManager();
	    PackageInfo pacInfo = null;
	    try {
	        pacInfo = pacman.getPackageInfo("com.android.settings", PackageManager.GET_RECEIVERS);

		    if(pacInfo != null){
		        for(ActivityInfo actInfo : pacInfo.receivers){
		            //test if recevier is exported. if so, we can toggle GPS.
		            if(actInfo.name.equals("com.android.settings.widget.SettingsAppWidgetProvider") && actInfo.exported){
						return true;
		            }
		        }
		    }				
	    } catch (NameNotFoundException e) {
	        return false; //package not found
	    }   
	    return false;
	}
	
	static boolean isSystemApp(Context context)
	{
		ApplicationInfo ai = context.getApplicationInfo();
		
		if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
		{
			//Log.d(TAG, "isSystemApp==true");
			return true;
		}
		return false;
	}
	
	static boolean isUpdatedSystemApp(Context context)
	{
		ApplicationInfo ai = context.getApplicationInfo();
		
		if ((ai.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
		{
			//Log.d(TAG, "isUpdatedSystemApp==true");
			return true;
		}
		return false;
		
	}

/*	
	static boolean isAdminUser(Context context)
	{
		UserHandle uh = Process.myUserHandle();
		UserManager um = (UserManager)context.getSystemService(Context.USER_SERVICE);
		if (um != null)
		{
			long userSerialNumber = um.getSerialNumberForUser(uh);
			//Log.d(TAG, "userSerialNumber="+userSerialNumber);
			return userSerialNumber == 0;
		}
		else
			return false;
	}
*/
	
	static boolean isRooted()
	{
		if (!rootChecked)
		{
			if (RootTools.isRootAvailable())
			{
				// zariadenie je rootnute
				rootChecked = true;
				rooted = true;
			}
			else
			{
				rootChecked = true;
				rooted = false;
			}
		}
		return rooted;
	}
	
	static boolean grantRoot()
	{
		if (!rootGranted)
		{
			if (RootTools.isAccessGiven())
			{
				// root grantnuty
				rootChecked = true;
				rooted = true;
				rootGranted = true;
				return true;
			}
			else
			{
				// grant odmietnuty
				rootChecked = false;
				rooted = false;
				rootGranted = false;
				return false;
			}
		}
		else
			return true;
	}
	
	//------------------------------------------------------------
	
}
