package sk.henrichg.phoneprofiles;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
//import android.content.Context;
//import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.util.Log;

public class PhoneProfilesPreferencesActivity extends SherlockPreferenceActivity {

	private PreferenceManager prefMng;
	private SharedPreferences preferences;
	//private Context context;
	//private Intent intent;
	private OnSharedPreferenceChangeListener prefListener;
	
	private static Activity preferenceActivity = null;
	
	private String activeLanguage;
	
		
	static final String PREFS_NAME = "phone_profile_preferences";
	
    static final String PREF_APPLICATION_START_ON_BOOT = "applicationStartOnBoot";
    static final String PREF_APPLICATION_ACTIVATE = "applicationActivate";
    static final String PREF_APPLICATION_CLOSE = "applicationClose";
    static final String PREF_APPLICATION_ALERT = "applicationAlert";
    static final String PREF_APPLICATION_LANGUAGE = "applicationLanguage";
    static final String PREF_NOTIFICATION_TOAST = "notificationsToast";
    static final String PREF_NOTIFICATION_STATUS_BAR  = "notificationStatusBar";
    static final String PREF_NOTIFICATION_STATUS_BAR_STYLE  = "notificationStatusBarStyle";
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		PhoneProfilesActivity.setLanguage(getBaseContext(), false);
		
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		

		preferenceActivity = this;

        //intent = getIntent();
        //context = this;
        
		prefMng = getPreferenceManager();
		prefMng.setSharedPreferencesName(PREFS_NAME);
		prefMng.setSharedPreferencesMode(MODE_PRIVATE);
        
		addPreferencesFromResource(R.layout.phone_profiles_preferences);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        activeLanguage = preferences.getString(PREF_APPLICATION_LANGUAGE, "system");
        
        prefListener = new OnSharedPreferenceChangeListener() {
        	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

    	    	// updating activity with selected profile preferences
    	    	Log.d("PhoneProfilesPreferencesActivity.onSharedPreferenceChanged", key);
    	    	
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
		if (activeLanguage != preferences.getString(PREF_APPLICATION_LANGUAGE, "system"))
    		PhoneProfilesActivity.setLanguage(getBaseContext(), true);
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

}
