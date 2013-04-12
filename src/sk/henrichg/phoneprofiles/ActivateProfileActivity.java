package sk.henrichg.phoneprofiles;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

public class ActivateProfileActivity extends Activity {

	private static DatabaseHandler databaseHandler;
	private ActivateProfileHelper activateProfileHelper;
	
	private int startupSource = 0;
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PhoneProfilesActivity.setLanguage(getBaseContext(), false);
		
		intent = getIntent();
		startupSource = intent.getIntExtra(PhoneProfilesActivity.EXTRA_START_APP_SOURCE, 0);
		
		activateProfileHelper = new ActivateProfileHelper(this, getBaseContext());

		databaseHandler = new DatabaseHandler(this);
		
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		Log.d("ActivateProfileActivity.onStart", "startupSource="+startupSource);
		
		boolean actProfile = false;
		if (startupSource == PhoneProfilesActivity.STARTUP_SOURCE_SHORTCUT)
		{
			// aktivita spustena z shortcutu, profil aktivujeme
			actProfile = true;
		}
		else
		if (startupSource == PhoneProfilesActivity.STARTUP_SOURCE_BOOT)
		{
			// aktivita bola spustena po boote telefonu
			
			SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
			if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_APPLICATION_ACTIVATE, true))
			{
				// je nastavene, ze pri starte sa ma aktivita aktivovat
				actProfile = true;
			}
		}
		Log.d("PhoneProfilesActivity.onStart", "actProfile="+String.valueOf(actProfile));

		Profile profile;
		
		// pre profil, ktory je prave aktivny, treba aktualizovat aktivitu
		profile = databaseHandler.getActivatedProfile();
		activateProfileHelper.showNotification(profile);
		activateProfileHelper.updateWidget();
		
		if (startupSource == PhoneProfilesActivity.STARTUP_SOURCE_SHORTCUT)
		{
			long profile_id = intent.getLongExtra(PhoneProfilesActivity.EXTRA_PROFILE_ID, 0);
			if (profile_id == 0)
				profile = null;
			else
				profile = databaseHandler.getProfile(profile_id);
		}
		
		if (actProfile && (profile != null))
		{
			// aktivacia profilu
			activateProfile(profile);
		}
		
		Log.d("ActivateProfileActivity.onStart", "xxxx");
		
		finish();
	}
	
	private void activateProfile(Profile profile)
	{
		SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
		
		databaseHandler.activateProfile(profile);

		activateProfileHelper.execute(profile);
		activateProfileHelper.showNotification(profile);
		activateProfileHelper.updateWidget();

		if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_NOTIFICATION_TOAST, true))
		{	
			// toast notification
			Toast msg = Toast.makeText(this, 
					getResources().getString(R.string.toast_profile_activated_0) + ": " + profile.getName() + " " +
					getResources().getString(R.string.toast_profile_activated_1), 
					Toast.LENGTH_LONG);
			msg.show();
		}

		
	}
	
}
