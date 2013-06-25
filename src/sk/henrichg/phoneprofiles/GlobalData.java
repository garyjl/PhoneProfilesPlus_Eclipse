package sk.henrichg.phoneprofiles;

import java.util.List;
import java.util.Locale;

import com.stericson.RootTools.RootTools;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Debug;
import android.util.Log;

public class GlobalData extends Application {
	
	private static Context context;
	
	private static DatabaseHandler databaseHandler = null;
	private static List<Profile> profileList = null;
	private static boolean applicationStarted = false;
	private static ActivateProfileHelper activateProfileHelper = null;

	static final String EXTRA_PROFILE_POSITION = "profile_position";
	static final String EXTRA_PROFILE_ID = "profile_id";
	static final String EXTRA_START_APP_SOURCE = "start_app_source";

	static final int STARTUP_SOURCE_NOTIFICATION = 1;
	static final int STARTUP_SOURCE_WIDGET = 2;
	static final int STARTUP_SOURCE_SHORTCUT = 3;
	static final int STARTUP_SOURCE_BOOT = 4;
	static final int STARTUP_SOURCE_ACTIVATOR = 5;

	static final int NOTIFICATION_ID = 700420;

	static final String PROFILE_ICON_DEFAULT = "ic_profile_default";
	
	static String PACKAGE_NAME;
	
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
	
	
	public void onCreate()
	{
		super.onCreate();
		
		context = getApplicationContext();
		PACKAGE_NAME = context.getPackageName();
		
		loadPreferences();

		databaseHandler = new DatabaseHandler(this);

		activateProfileHelper = new ActivateProfileHelper(); 

		Log.d("GlobalData.onCreate", "memory usage (after create activateProfileHelper)=" + Debug.getNativeHeapAllocatedSize());
		
		Log.d("GlobalData.onCreate","xxx");
		
	}
	
	public static boolean getApplicationStarted()
	{
		return applicationStarted;
	}
	
	public static void setApplicationStarted(boolean started)
	{
		applicationStarted = started;
	}

	public static DatabaseHandler getDatabaseHandler()
	{
		return databaseHandler;
	}

	public static ActivateProfileHelper getActivateProfileHelper()
	{
		return activateProfileHelper;
	}
	
	public static List<Profile> getProfileList()
	{
		if (profileList == null)
		{
			profileList = databaseHandler.getAllProfiles();
		
			for (Profile profile : profileList)
			{
				profile.generateIconBitmap(context);
				profile.generatePreferencesIndicator(context);
			}
		}

		return profileList;
	}

	public static void clearProfileList()
	{
		profileList.clear();
		profileList = null;
	}
	
	public static Profile getActivatedProfile()
	{
		if (profileList == null)
			getProfileList();

		Profile profile;
		for (int i = 0; i < profileList.size(); i++)
		{
			profile = profileList.get(i); 
			if (profile._checked)
				return profile;
		}
		
		return null;
	}
	
	public static Profile getFirstProfile()
	{
		if (profileList == null)
			getProfileList();
		
		Profile profile;
		if (profileList.size() > 0)
			profile = profileList.get(0);
		else
			profile = null;
		
		return profile;
	}
	
	public static int getItemPosition(Profile profile)
	{
		for (int i = 0; i < profileList.size(); i++)
		{
			if (profileList.get(i)._id == profile._id)
				return i;
		}
		return -1;
	}
	
	public static void activateProfile(Profile profile)
	{
		for (Profile p : profileList)
		{
			p._checked = false;
		}
		
		// teraz musime najst profile v profileList 
		int position = getItemPosition(profile);
		if (position != -1)
		{
			// najdenemu objektu nastavime _checked
			Profile _profile = profileList.get(position);
			if (_profile != null)
				_profile._checked = true;
		}
	}
	
	public static Profile getProfileById(long id)
	{
		if (profileList == null)
			getProfileList();

		Profile profile;
		for (int i = 0; i < profileList.size(); i++)
		{
			profile = profileList.get(i); 
			if (profile._id == id)
				return profile;
		}
		
		return null;
	}

	
	static public void loadPreferences()
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
		
	}

	// ----- Hardware check -------------------------------------
	
	static private boolean rootChecked = false;
	static private boolean rooted = false;

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
			if (RootTools.isAccessGiven())
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
	
	//------------------------------------------------------
	
	public static void setLanguage(Context context)//, boolean restart)
	{
		// jazyk na aky zmenit
		String lang = GlobalData.applicationLanguage;
		
		//Log.d("EditorProfilesActivity.setLanguauge", lang);

		Locale appLocale;
		
		if (!lang.equals("system"))
		{
			appLocale = new Locale(lang);
		}
		else
		{
			appLocale = Resources.getSystem().getConfiguration().locale;
		}
		
		Locale.setDefault(appLocale);
		Configuration appConfig = new Configuration();
		appConfig.locale = appLocale;
		context.getResources().updateConfiguration(appConfig, context.getResources().getDisplayMetrics());
		
		//languageChanged = restart;
	}
	
	public static void setTheme(Activity activity, boolean forPopup)
	{
		if (GlobalData.applicationTheme.equals("light"))
		{
			//Log.d("EditorProfilesActivity.setTheme","light");
			if (forPopup)
				activity.setTheme(R.style.PopupTheme);
			else
				activity.setTheme(R.style.Theme_Phoneprofilestheme);
		}
		else
		if (GlobalData.applicationTheme.equals("dark"))
		{
			//Log.d("EditorProfilesActivity.setTheme","dark");
			if (forPopup)
				activity.setTheme(R.style.PopupTheme_dark);
			else
				activity.setTheme(R.style.Theme_Phoneprofilestheme_dark);
		}
	}

	
}
