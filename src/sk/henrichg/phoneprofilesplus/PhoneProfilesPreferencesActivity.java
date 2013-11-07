package sk.henrichg.phoneprofilesplus;

import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Bundle;
import net.saik0.android.unifiedpreference.UnifiedPreferenceFragment;
import net.saik0.android.unifiedpreference.UnifiedSherlockPreferenceActivity;

public class PhoneProfilesPreferencesActivity extends
		UnifiedSherlockPreferenceActivity {

	
	//private PreferenceManager prefMng;
	private SharedPreferences preferences;
	//private Context context;
	//private Intent intent;
	private OnSharedPreferenceChangeListener prefListener;
	
	private boolean showEditorPrefIndicator;
	private boolean showEditorHeader;
	private String activeLanguage;
	private String activeTheme;

	private boolean invalidateEditor = false; 
	
	@Override public void onCreate(Bundle savedInstanceState) {

		// must by called before super.onCreate() for PreferenceActivity
		GUIData.setTheme(this, false);
		GUIData.setLanguage(getBaseContext());
		
		
		// Set header resource MUST BE CALLED BEFORE super.onCreate 
		setHeaderRes(R.xml.phone_profiles_preferences_headers);
		
		
		// Set desired preference file and mode (optional)
		//prefMng = getPgetPreferenceManager();
		//prefMng.setSharedPreferencesName(GlobalData.APPLICATION_PREFS_NAME);
		//prefMng.setSharedPreferencesMode(MODE_PRIVATE);
		setSharedPreferencesName(GlobalData.APPLICATION_PREFS_NAME);
		setSharedPreferencesMode(MODE_PRIVATE);

		super.onCreate(savedInstanceState);
		
		invalidateEditor = false;
		
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.title_activity_phone_profiles_preferences);
		
        preferences = getBaseContext().getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, MODE_PRIVATE);
        activeLanguage = preferences.getString(GlobalData.PREF_APPLICATION_LANGUAGE, "system");
        activeTheme = preferences.getString(GlobalData.PREF_APPLICATION_THEME, "light");
        showEditorPrefIndicator = preferences.getBoolean(GlobalData.PREF_APPLICATION_EDITOR_PREF_INDICATOR, true);
        showEditorHeader = preferences.getBoolean(GlobalData.PREF_APPLICATION_EDITOR_HEADER, true);
        
     /*   prefListener = new OnSharedPreferenceChangeListener() {
        	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

    	    	// updating activity with selected profile preferences
    	    	//Log.d("PhoneProfilesPreferencesActivity.onSharedPreferenceChanged", key);
    	    	
    	    	if (key.equals(GlobalData.PREF_APPLICATION_LANGUAGE) ||
    	    		key.equals(GlobalData.PREF_APPLICATION_THEME) ||	
    	    		key.equals(GlobalData.PREF_NOTIFICATION_STATUS_BAR_STYLE))
    	    		setSummary(key, prefs.getString(key, ""));
    	    		
    	    }

        };
        
        preferences.registerOnSharedPreferenceChangeListener(prefListener); */
		
		
	}

	public static class ActivatorPreferencesFragment extends UnifiedPreferenceFragment {}
	public static class EditorPreferencesFragment extends UnifiedPreferenceFragment {}
	public static class GUIPreferencesFragment extends UnifiedPreferenceFragment {}
	public static class ActivationPreferencesFragment extends UnifiedPreferenceFragment {}
	public static class NotificationsPreferencesFragment extends UnifiedPreferenceFragment {}
	public static class StartPreferencesFragment extends UnifiedPreferenceFragment {}
	public static class WidgetListPreferencesFragment extends UnifiedPreferenceFragment {}
	public static class WidgetIconShortcutPreferencesFragment extends UnifiedPreferenceFragment {}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
		GUIData.reloadActivity(this);
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
	
/*
	private void updateSharedPreference()
	{
        setSummary(GlobalData.PREF_APPLICATION_LANGUAGE, preferences.getString(GlobalData.PREF_APPLICATION_LANGUAGE, ""));
        setSummary(GlobalData.PREF_APPLICATION_THEME, preferences.getString(GlobalData.PREF_APPLICATION_THEME, ""));
        setSummary(GlobalData.PREF_NOTIFICATION_STATUS_BAR_STYLE, preferences.getString(GlobalData.PREF_NOTIFICATION_STATUS_BAR_STYLE, ""));
	}
*/
	
	@Override
	protected void onStart()
	{
		super.onStart();
		//updateSharedPreference();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
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
	
	@Override
	public void finish() {
		
		GlobalData.loadPreferences(getBaseContext());
		
		//Log.d("PhoneProfilesPreferencesActivity.onPause", "xxx");
		
		if (activeLanguage != GlobalData.applicationLanguage)
		{
    		//Log.d("PhoneProfilesPreferencesActivity.onPause","language changed");
			GUIData.setLanguage(getBaseContext());
			invalidateEditor = true;
		}
		else
		if (activeTheme != GlobalData.applicationTheme)
		{
    		//Log.d("PhoneProfilesPreferencesActivity.onPause","theme changed");
    		//EditorProfilesActivity.setTheme(this, false);
			invalidateEditor = true;
		}
		else
   		if (showEditorPrefIndicator != GlobalData.applicationEditorPrefIndicator)
   		{
       		//Log.d("PhoneProfilesPreferencesActivity.onPause","invalidate");
   			invalidateEditor = true;
   		}
		else
   		if (showEditorHeader != GlobalData.applicationEditorHeader)
   		{
       		//Log.d("PhoneProfilesPreferencesActivity.onPause","invalidate");
   			invalidateEditor = true;
   		}
		
		// for startActivityForResult
		Intent returnIntent = new Intent();
		returnIntent.putExtra(GlobalData.EXTRA_RESET_EDITOR, invalidateEditor);
		setResult(RESULT_OK,returnIntent);

	    super.finish();
	}
	
	/*
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
*/	

}