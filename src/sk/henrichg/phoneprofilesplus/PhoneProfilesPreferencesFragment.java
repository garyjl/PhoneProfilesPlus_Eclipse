package sk.henrichg.phoneprofilesplus;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.TwoStatePreference;

public class PhoneProfilesPreferencesFragment extends PreferenceListFragment 
                                              implements SharedPreferences.OnSharedPreferenceChangeListener
{

	private PreferenceManager prefMng;
	private SharedPreferences preferences;
	private static Activity preferencesActivity = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		preferencesActivity = getActivity();
        //context = getActivity().getBaseContext();

		prefMng = getPreferenceManager();
		prefMng.setSharedPreferencesName(GlobalData.APPLICATION_PREFS_NAME);
		prefMng.setSharedPreferencesMode(Activity.MODE_PRIVATE);
		
		preferences = prefMng.getSharedPreferences();
			
		addPreferencesFromResource(R.xml.phone_profiles_preferences);

        preferences.registerOnSharedPreferenceChangeListener(this);  
        
    }

	private void setSummary(String key)
	{
		
		Preference preference = prefMng.findPreference(key);
		
		if (preference == null)
			return;

		// Do not bind toggles.
		if (preference instanceof CheckBoxPreference
				|| (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
					&& preference instanceof TwoStatePreference)) {
			return;
		}

		String stringValue = preferences.getString(key, "");

		//Log.e("PhoneProfilesPreferencesFragment.setSummary",key+"="+stringValue);
		
		if (preference instanceof ListPreference) {
			// For list preferences, look up the correct display value in
			// the preference's 'entries' list.
			ListPreference listPreference = (ListPreference) preference;
			int index = listPreference.findIndexOfValue(stringValue);

			// Set the summary to reflect the new value.
			// **** Heno changes ** support for "%" in list items
			CharSequence summary = (index >= 0) ? listPreference.getEntries()[index] : null;
			//Log.e("PhoneProfilesPreferencesFragment.setSummary",key+"="+summary);
			if (summary != null)
			{
				String sSummary = summary.toString();
				sSummary = sSummary.replace("%", "%%");
				preference.setSummary(sSummary);
			}
			else
				preference.setSummary(summary);

		} 
		/*else if (preference instanceof RingtonePreference) {
			// For ringtone preferences, look up the correct display value
			// using RingtoneManager.
			if (TextUtils.isEmpty(stringValue)) {
				// Empty values correspond to 'silent' (no ringtone).
				preference.setSummary(R.string.ringtone_silent);
			} else {
				Ringtone ringtone = RingtoneManager.getRingtone(
						preference.getContext(), Uri.parse(stringValue));

				if (ringtone == null) {
					// Clear the summary if there was a lookup error.
					preference.setSummary(null);
				} else {
					// Set the summary to reflect the new ringtone display
					// name.
					String name = ringtone
							.getTitle(preference.getContext());
					preference.setSummary(name);
				}
			}

		}*/
		 else {
			// For all other preferences, set the summary to the value's
			// simple string representation.
		    //Log.e("PhoneProfilesPreferencesFragment.setSummary",key+"="+stringValue);
			preference.setSummary(preference.toString());
		}
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		setSummary(key);
	}
	
	private void updateSharedPreference()
	{
	    setSummary(GlobalData.PREF_APPLICATION_START_ON_BOOT);
	    setSummary(GlobalData.PREF_APPLICATION_ACTIVATE);
	    setSummary(GlobalData.PREF_APPLICATION_ALERT);
	    setSummary(GlobalData.PREF_APPLICATION_CLOSE);
	    setSummary(GlobalData.PREF_APPLICATION_LONG_PRESS_ACTIVATION);
	    setSummary(GlobalData.PREF_APPLICATION_HOME_LAUNCHER);
	    setSummary(GlobalData.PREF_APPLICATION_NOTIFICATION_LAUNCHER);
	    setSummary(GlobalData.PREF_APPLICATION_WIDGET_LAUNCHER);
	    setSummary(GlobalData.PREF_APPLICATION_LANGUAGE);
	    setSummary(GlobalData.PREF_APPLICATION_THEME);
	    setSummary(GlobalData.PREF_APPLICATION_ACTIVATOR_PREF_INDICATOR);
	    setSummary(GlobalData.PREF_APPLICATION_EDITOR_PREF_INDICATOR);
	    setSummary(GlobalData.PREF_APPLICATION_ACTIVATOR_HEADER);
	    setSummary(GlobalData.PREF_APPLICATION_EDITOR_HEADER);
	    setSummary(GlobalData.PREF_NOTIFICATION_TOAST);
	    setSummary(GlobalData.PREF_NOTIFICATION_STATUS_BAR);
	    setSummary(GlobalData.PREF_NOTIFICATION_STATUS_BAR_STYLE);
	    setSummary(GlobalData.PREF_APPLICATION_WIDGET_LIST_PREF_INDICATOR);
	    setSummary(GlobalData.PREF_APPLICATION_WIDGET_LIST_HEADER);
	    setSummary(GlobalData.PREF_APPLICATION_WIDGET_LIST_BACKGROUND);
	    setSummary(GlobalData.PREF_APPLICATION_WIDGET_LIST_LIGHTNESS_B);
	    setSummary(GlobalData.PREF_APPLICATION_WIDGET_LIST_LIGHTNESS_T);
	    setSummary(GlobalData.PREF_APPLICATION_WIDGET_ICON_COLOR);
	    setSummary(GlobalData.PREF_APPLICATION_WIDGET_ICON_LIGHTNESS);
	    setSummary(GlobalData.PREF_APPLICATION_WIDGET_LIST_ICON_COLOR);
	    setSummary(GlobalData.PREF_APPLICATION_WIDGET_LIST_ICON_LIGHTNESS);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();

		updateSharedPreference();
	}
	
	@Override
	public void onDestroy()
	{
        preferences.unregisterOnSharedPreferenceChangeListener(this); 
		super.onDestroy();
	}
	
	public void doOnActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		doOnActivityResult(requestCode, resultCode, data);
	}
	
	static public Activity getPreferencesActivity()
	{
		return preferencesActivity;
	}
	
}
