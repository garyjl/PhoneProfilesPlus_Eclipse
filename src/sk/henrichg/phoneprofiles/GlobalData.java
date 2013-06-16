package sk.henrichg.phoneprofiles;

import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class GlobalData extends Application {
	
	private static Context context;
	
	private static DatabaseHandler databaseHandler = null;
	private static List<Profile> profileList = null;
	private static boolean applicationStarted = false;

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
	
	public static List<Profile> getProfileList()
	{
		if (profileList == null)
			profileList = databaseHandler.getAllProfiles();

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
			profileList = databaseHandler.getAllProfiles();

		Profile profile;
		for (int i = 0; i < profileList.size(); i++)
		{
			profile = profileList.get(i); 
			if (profile.getChecked())
				return profile;
		}
		
		return null;
	}
	
	public static int getItemPosition(Profile profile)
	{
		for (int i = 0; i < profileList.size(); i++)
		{
			if (profileList.get(i).getID() == profile.getID())
				return i;
		}
		return -1;
	}
	
	public static void activateProfile(Profile profile)
	{
		for (Profile p : profileList)
		{
			p.setChecked(false);
		}
		
		// teraz musime najst profile v profileList 
		int position = getItemPosition(profile);
		if (position != -1)
		{
			// najdenemu objektu nastavime _checked
			Profile _profile = profileList.get(position);
			if (_profile != null)
				_profile.setChecked(true);
		}
	}
	
	public static Profile getProfileById(long id)
	{
		if (profileList == null)
			profileList = databaseHandler.getAllProfiles();

		Profile profile;
		for (int i = 0; i < profileList.size(); i++)
		{
			profile = profileList.get(i); 
			if (profile.getID() == id)
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
	
}
