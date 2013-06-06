package sk.henrichg.phoneprofiles;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
//import android.content.Context;
//import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;

public class PhoneProfilesPreferencesActivity extends SherlockPreferenceActivity {

	private PreferenceManager prefMng;
	private SharedPreferences preferences;
	//private Context context;
	//private Intent intent;
	private OnSharedPreferenceChangeListener prefListener;
	
	private static Activity preferenceActivity = null;
	
	private boolean showEditorPrefIndicator;
	private static boolean invalidateLoader = false; 

	private String activeLanguage;
	
		
	static final String PREFS_NAME = "phone_profile_preferences";
	
    private static final String PREF_APPLICATION_START_ON_BOOT = "applicationStartOnBoot";
    private static final String PREF_APPLICATION_ACTIVATE = "applicationActivate";
    private static final String PREF_APPLICATION_ALERT = "applicationAlert";
    private static final String PREF_APPLICATION_CLOSE = "applicationClose";
    private static final String PREF_APPLICATION_LONG_PRESS_ACTIVATION = "applicationLongClickActivation";
    private static final String PREF_APPLICATION_LANGUAGE = "applicationLanguage";
    private static final String PREF_APPLICATION_ACTIVATOR_PREF_INDICATOR = "applicationActivatorPrefIndicator";
    private static final String PREF_APPLICATION_EDITOR_PREF_INDICATOR = "applicationEditorPrefIndicator";
    private static final String PREF_NOTIFICATION_TOAST = "notificationsToast";
    private static final String PREF_NOTIFICATION_STATUS_BAR  = "notificationStatusBar";
    private static final String PREF_NOTIFICATION_STATUS_BAR_STYLE  = "notificationStatusBarStyle";

    public static boolean applicationStartOnBoot;
    public static boolean applicationActivate;
    public static boolean applicationActivateWithAlert;
    public static boolean applicationClose;
    public static boolean applicationLongClickActivation;
    public static String applicationLanguage;
    public static boolean applicationActivatorPrefIndicator;
    public static boolean applicationEditorPrefIndicator;
    public static boolean notificationsToast;
    public static boolean notificationStatusBar;
    public static String notificationStatusBarStyle;
    
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		preferenceActivity = this;

		invalidateLoader = false;
		
		PhoneProfilesActivity.setLanguage(getBaseContext(), false);
		
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		

        //intent = getIntent();
        //context = this;
        
		prefMng = getPreferenceManager();
		prefMng.setSharedPreferencesName(PREFS_NAME);
		prefMng.setSharedPreferencesMode(MODE_PRIVATE);
        
		addPreferencesFromResource(R.layout.phone_profiles_preferences);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        activeLanguage = preferences.getString(PREF_APPLICATION_LANGUAGE, "system");
        showEditorPrefIndicator = preferences.getBoolean(PREF_APPLICATION_EDITOR_PREF_INDICATOR, true);
        
        prefListener = new OnSharedPreferenceChangeListener() {
        	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

    	    	// updating activity with selected profile preferences
    	    	//Log.d("PhoneProfilesPreferencesActivity.onSharedPreferenceChanged", key);
    	    	
    	    	if (key.equals(PREF_APPLICATION_LANGUAGE) ||
    	    		key.equals(PREF_NOTIFICATION_STATUS_BAR_STYLE))
    	    		setSummary(key, prefs.getString(key, ""));
    	    		
    	    }

        };
        
        preferences.registerOnSharedPreferenceChangeListener(prefListener);
	
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
		Intent intent = getIntent();
		startActivity(intent);
		finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void updateSharedPreference()
	{
        setSummary(PREF_APPLICATION_LANGUAGE, preferences.getString(PREF_APPLICATION_LANGUAGE, ""));
        setSummary(PREF_NOTIFICATION_STATUS_BAR_STYLE, preferences.getString(PREF_NOTIFICATION_STATUS_BAR_STYLE, ""));
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		updateSharedPreference();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		loadPreferences(getBaseContext());
		
		if (activeLanguage != preferences.getString(PREF_APPLICATION_LANGUAGE, "system"))
			// sposobi ukoncenie aplikacie (vid LocaleChangedReceiver
    		PhoneProfilesActivity.setLanguage(getBaseContext(), true);
    	else
    	{
    		Log.d("PhoneProfilesPreferencesActivity.onPause","no language changed");
    		if (showEditorPrefIndicator != applicationEditorPrefIndicator)
    		{
        		Log.d("PhoneProfilesPreferencesActivity.onPause","invalidate");
    			invalidateLoader = true;
    		}
    	}
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}
	
	@Override
	protected void onDestroy()
	{
    	preferences.unregisterOnSharedPreferenceChangeListener(prefListener);
		super.onDestroy();
	}
	
	private void setSummary(String key, Object value)
	{
		if (key.equals(PREF_APPLICATION_LANGUAGE))
		{
			String sPrefLanguauge = value.toString();
			String[] prefLanguages = getResources().getStringArray(R.array.languageArray);
			String[] prefLangValues = getResources().getStringArray(R.array.languageValues);
			int ilangValue = 0;
			for (String slangValue : prefLangValues)
			{
				if (slangValue.equals(sPrefLanguauge))
					prefMng.findPreference(key).setSummary(prefLanguages[ilangValue]);
				++ilangValue;
			}
		}
		if (key.equals(PREF_NOTIFICATION_STATUS_BAR_STYLE))
		{
			String sPrefNotifIconStyle = value.toString();
			String[] prefNotifIconStyles = getResources().getStringArray(R.array.notificationIconStyleArray);
			String[] prefNotifIconStyleValues = getResources().getStringArray(R.array.notificationIconStyleValues);
			int ilangValue = 0;
			for (String slangValue : prefNotifIconStyleValues)
			{
				if (slangValue.equals(sPrefNotifIconStyle))
					prefMng.findPreference(key).setSummary(prefNotifIconStyles[ilangValue]);
				++ilangValue;
			}
		}
	}
	
	static public Activity getActivity()
	{
		return preferenceActivity;
	}
	
	static public void loadPreferences(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, Context.MODE_PRIVATE);

	    applicationStartOnBoot = preferences.getBoolean(PREF_APPLICATION_START_ON_BOOT, false);
	    applicationActivate = preferences.getBoolean(PREF_APPLICATION_ACTIVATE, true);
	    applicationActivateWithAlert = preferences.getBoolean(PREF_APPLICATION_ALERT, true);
	    applicationClose = preferences.getBoolean(PREF_APPLICATION_CLOSE, true);
	    applicationLongClickActivation = preferences.getBoolean(PREF_APPLICATION_LONG_PRESS_ACTIVATION, false);
	    applicationLanguage = preferences.getString(PREF_APPLICATION_LANGUAGE, "system");
	    applicationActivatorPrefIndicator = preferences.getBoolean(PREF_APPLICATION_ACTIVATOR_PREF_INDICATOR, true);
	    applicationEditorPrefIndicator = preferences.getBoolean(PREF_APPLICATION_EDITOR_PREF_INDICATOR, true);
	    notificationsToast = preferences.getBoolean(PREF_NOTIFICATION_TOAST, true);
	    notificationStatusBar = preferences.getBoolean(PREF_NOTIFICATION_STATUS_BAR, true);
	    notificationStatusBarStyle = preferences.getString(PREF_NOTIFICATION_STATUS_BAR_STYLE, "0");
		
	}
	
	static public boolean getInvalidateLoader()
	{
		boolean r = invalidateLoader;
		invalidateLoader = false;
		return r;
	}

}
