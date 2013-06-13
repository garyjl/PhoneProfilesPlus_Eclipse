package sk.henrichg.phoneprofiles;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.app.Activity;
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
	private String activeTheme;
	
		
/*	static final String PREFS_NAME = "phone_profile_preferences";
	
    private static final String PREF_APPLICATION_START_ON_BOOT = "applicationStartOnBoot";
    private static final String PREF_APPLICATION_ACTIVATE = "applicationActivate";
    private static final String PREF_APPLICATION_ALERT = "applicationAlert";
    private static final String PREF_APPLICATION_CLOSE = "applicationClose";
    private static final String PREF_APPLICATION_LONG_PRESS_ACTIVATION = "applicationLongClickActivation";
    private static final String PREF_APPLICATION_LANGUAGE = "applicationLanguage";
    private static final String PREF_APPLICATION_THEME = "applicationTheme";
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
    public static String applicationTheme;
    public static boolean applicationActivatorPrefIndicator;
    public static boolean applicationEditorPrefIndicator;
    public static boolean notificationsToast;
    public static boolean notificationStatusBar;
    public static String notificationStatusBarStyle;
*/
    
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// must by called before super.onCreate() for PreferenceActivity
		PhoneProfilesActivity.setTheme(this, false);
		PhoneProfilesActivity.setLanguage(getBaseContext());
		
		super.onCreate(savedInstanceState);

		preferenceActivity = this;

		invalidateLoader = false;
		
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		

        //intent = getIntent();
        //context = this;
        
		prefMng = getPreferenceManager();
		prefMng.setSharedPreferencesName(GlobalData.APPLICATION_PREFS_NAME);
		prefMng.setSharedPreferencesMode(MODE_PRIVATE);
        
		addPreferencesFromResource(R.layout.phone_profiles_preferences);

        preferences = getApplicationContext().getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, MODE_PRIVATE);
        activeLanguage = preferences.getString(GlobalData.PREF_APPLICATION_LANGUAGE, "system");
        activeTheme = preferences.getString(GlobalData.PREF_APPLICATION_THEME, "light");
        showEditorPrefIndicator = preferences.getBoolean(GlobalData.PREF_APPLICATION_EDITOR_PREF_INDICATOR, true);
        
        prefListener = new OnSharedPreferenceChangeListener() {
        	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

    	    	// updating activity with selected profile preferences
    	    	//Log.d("PhoneProfilesPreferencesActivity.onSharedPreferenceChanged", key);
    	    	
    	    	if (key.equals(GlobalData.PREF_APPLICATION_LANGUAGE) ||
    	    		key.equals(GlobalData.PREF_APPLICATION_THEME) ||	
    	    		key.equals(GlobalData.PREF_NOTIFICATION_STATUS_BAR_STYLE))
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
        setSummary(GlobalData.PREF_APPLICATION_LANGUAGE, preferences.getString(GlobalData.PREF_APPLICATION_LANGUAGE, ""));
        setSummary(GlobalData.PREF_APPLICATION_THEME, preferences.getString(GlobalData.PREF_APPLICATION_THEME, ""));
        setSummary(GlobalData.PREF_NOTIFICATION_STATUS_BAR_STYLE, preferences.getString(GlobalData.PREF_NOTIFICATION_STATUS_BAR_STYLE, ""));
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
		GlobalData.loadPreferences();
		
		if (activeLanguage != preferences.getString(GlobalData.PREF_APPLICATION_LANGUAGE, "system"))
		{
    		//Log.d("PhoneProfilesPreferencesActivity.onPause","language changed");
    		PhoneProfilesActivity.setLanguage(getBaseContext());
			invalidateLoader = true;
		}
		else
		if (activeTheme != preferences.getString(GlobalData.PREF_APPLICATION_THEME, "light"))
		{
    		Log.d("PhoneProfilesPreferencesActivity.onPause","theme changed");
    		//PhoneProfilesActivity.setTheme(this, false);
			invalidateLoader = true;
		}
    	else
    	{
    		//Log.d("PhoneProfilesPreferencesActivity.onPause","no language changed");
    		if (showEditorPrefIndicator != GlobalData.applicationEditorPrefIndicator)
    		{
        		//Log.d("PhoneProfilesPreferencesActivity.onPause","invalidate");
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
		if (key.equals(GlobalData.PREF_APPLICATION_LANGUAGE))
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
		if (key.equals(GlobalData.PREF_APPLICATION_THEME))
		{
			String sPrefTheme = value.toString();
			String[] prefThemes = getResources().getStringArray(R.array.themeArray);
			String[] prefThemeValues = getResources().getStringArray(R.array.themeValues);
			int iThemeValue = 0;
			for (String sThemeValue : prefThemeValues)
			{
				if (sThemeValue.equals(sPrefTheme))
					prefMng.findPreference(key).setSummary(prefThemes[iThemeValue]);
				++iThemeValue;
			}
		}
		if (key.equals(GlobalData.PREF_NOTIFICATION_STATUS_BAR_STYLE))
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
	
	static public boolean getInvalidateLoader()
	{
		boolean r = invalidateLoader;
		invalidateLoader = false;
		return r;
	}

}
