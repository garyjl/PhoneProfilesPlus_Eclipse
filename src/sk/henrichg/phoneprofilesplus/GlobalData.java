package sk.henrichg.phoneprofilesplus;

import com.stericson.RootTools.RootTools;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class GlobalData extends Application {

	public static Context context;
	
	static String PACKAGE_NAME;

	// musi byt tu, pouziva t ActivateProfileHelper
	static final String EXTRA_PROFILE_ID = "profile_id";
	static final String EXTRA_EVENT_ID = "event_id";
	static final String EXTRA_START_APP_SOURCE = "start_app_source";
	static final String EXTRA_RESET_EDITOR = "reset_editor";

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
	
	// musi byt tu, pouziva to ActivateProfileHelper
	static final int NOTIFICATION_ID = 700420;

	static final String PREF_PROFILE_NAME = "profileName";
	static final String PREF_PROFILE_ICON = "profileIcon";
	static final String PREF_PROFILE_VOLUME_RINGER_MODE = "volumeRingerMode";
	static final String PREF_PROFILE_VOLUME_RINGTONE = "volumeRingtone";
	static final String PREF_PROFILE_VOLUME_NOTIFICATION = "volumeNotification";
	static final String PREF_PROFILE_VOLUME_MEDIA = "volumeMedia";
	static final String PREF_PROFILE_VOLUME_ALARM = "volumeAlarm";
	static final String PREF_PROFILE_VOLUME_SYSTEM = "volumeSystem";
	static final String PREF_PROFILE_VOLUME_VOICE = "volumeVoice";
	static final String PREF_PROFILE_SOUND_RINGTONE_CHANGE = "soundRingtoneChange";
	static final String PREF_PROFILE_SOUND_RINGTONE = "soundRingtone";
	static final String PREF_PROFILE_SOUND_NOTIFICATION_CHANGE = "soundNotificationChange";
	static final String PREF_PROFILE_SOUND_NOTIFICATION = "soundNotification";
	static final String PREF_PROFILE_SOUND_ALARM_CHANGE = "soundAlarmChange";
	static final String PREF_PROFILE_SOUND_ALARM = "soundAlarm";
	static final String PREF_PROFILE_DEVICE_AIRPLANE_MODE = "deviceAirplaneMode";
	static final String PREF_PROFILE_DEVICE_WIFI = "deviceWiFi";
	static final String PREF_PROFILE_DEVICE_BLUETOOTH = "deviceBluetooth";
	static final String PREF_PROFILE_DEVICE_SCREEN_TIMEOUT = "deviceScreenTimeout";
	static final String PREF_PROFILE_DEVICE_BRIGHTNESS = "deviceBrightness";
	static final String PREF_PROFILE_DEVICE_WALLPAPER_CHANGE = "deviceWallpaperChange";
	static final String PREF_PROFILE_DEVICE_WALLPAPER = "deviceWallpaper";
	static final String PREF_PROFILE_DEVICE_MOBILE_DATA = "deviceMobileData";
	static final String PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS = "deviceMobileDataPrefs";
	static final String PREF_PROFILE_DEVICE_GPS = "deviceGPS";
	static final String PREF_PROFILE_DEVICE_RUN_APPLICATION_CHANGE = "deviceRunApplicationChange";
	static final String PREF_PROFILE_DEVICE_RUN_APPLICATION_PACKAGE_NAME = "deviceRunApplicationPackageName";
	static final String PREF_PROFILE_DEVICE_AUTOSYNC = "deviceAutosync";
	static final String PREF_PROFILE_SHOW_IN_ACTIVATOR = "showInActivator";
	
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
		
		context = getApplicationContext();
		
		PACKAGE_NAME = context.getPackageName();
		
		// initialization
		loadPreferences(getApplicationContext());

		//Log.d("GlobalData.onCreate", "memory usage (after create activateProfileHelper)=" + Debug.getNativeHeapAllocatedSize());
		
		//Log.d("GlobalData.onCreate","xxx");
		
	}
	
	public void onTerminate ()
	{
		DatabaseHandler.getInstance(context).closeConnecion();
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
	
	public static void startService(Context context)
	{
		if (!isServiceRunning(context))
		{
			//Log.d("GlobaData.startService","xxx");
			
			//Intent broadcast = new Intent(context, PhoneProfilesServiceSheduler.class);
			//broadcast.setAction(BROADCAST_ACTION);
			//context.sendBroadcast(broadcast);
			Intent service = new Intent(context, PhoneProfilesService.class);
			context.startService(service);
		/*	
			AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
				
				@Override
				protected void onPreExecute()
				{
					super.onPreExecute();
				}
				
				@Override
				protected Void doInBackground(Void... params) {
					while (!PhoneProfilesService.started)
					{
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					return null;
				}
				
				@Override
				protected void onPostExecute(Void result)
				{
					super.onPostExecute(result);
					
				}
				
			};
			task.execute();

			try {
				task.get(30000, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		*/
		}	
	}
	
	public static void stopService(Context context)
	{
	/*    Intent intent = new Intent(context, PhoneProfilesServiceStarter.class);
	    PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
	
	    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	    alarmManager.cancel(sender);
	*/     
		context.stopService(new Intent(context, PhoneProfilesService.class));
	}
	
	
	
	public static boolean isServiceRunning(Context context) {
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (PhoneProfilesService.class.getName().equals(service.service.getClassName())) {
	        	//Log.d("GlobalData.isServiceRunning","true");
	            return true;
	        }
	    }
    	//Log.d("GlobalData.isServiceRunning","false");
	    return false;
	}
}
