package sk.henrichg.phoneprofiles;

import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class BackgroundActivateProfileActivity extends Activity {

	private ProfilesDataWrapper profilesDataWrapper;
	private DatabaseHandler databaseHandler;
	private ActivateProfileHelper activateProfileHelper;
	
	private int startupSource = 0;
	private long profile_id;
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Log.d("BackgroundActivateProfileActivity.onCreate","xxx");
		
		GlobalData.loadPreferences(getApplicationContext());
		
		profilesDataWrapper = new ProfilesDataWrapper(getApplicationContext(), true, true, false, false);
		
		intent = getIntent();
		startupSource = intent.getIntExtra(GlobalData.EXTRA_START_APP_SOURCE, 0);
		profile_id = intent.getLongExtra(GlobalData.EXTRA_PROFILE_ID, 0);
		//Log.d("BackgroundActivateProfileActivity.onStart", "profile_id="+profile_id);

		
		activateProfileHelper = profilesDataWrapper.getActivateProfileHelper();
		activateProfileHelper.initialize(this, getApplicationContext());

		// initialize global profile list
		databaseHandler = profilesDataWrapper.getDatabaseHandler();
		
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		//Log.d("BackgroundActivateProfileActivity.onStart", "startupSource="+startupSource);
		
		boolean actProfile = false;
		boolean interactive = false;
		if ((startupSource == GlobalData.STARTUP_SOURCE_SHORTCUT) ||
			(startupSource == GlobalData.STARTUP_SOURCE_SERVICE) ||
			(startupSource == GlobalData.STARTUP_SOURCE_SERVICE_INTERACTIVE))
		{
			// aktivita spustena z shortcutu alebo zo service, profil aktivujeme
			actProfile = true;
			interactive = (startupSource != GlobalData.STARTUP_SOURCE_SERVICE);
		}
		else
		if (startupSource == GlobalData.STARTUP_SOURCE_BOOT)	
		{
			// aktivita bola spustena po boote telefonu
			
			if (GlobalData.applicationActivate)
			{
				// je nastavene, ze pri starte sa ma aktivita aktivovat
				actProfile = true;
			}
		}
		//Log.d("BackgroundActivateProfileActivity.onStart", "actProfile="+String.valueOf(actProfile));

		Profile profile;
		
		// pre profil, ktory je prave aktivny, treba aktualizovat aktivitu
		profile = profilesDataWrapper.getActivatedProfile();

		//Log.d("BackgroundActivateProfileActivity.onStart","_iconBitmap="+String.valueOf(profile._iconBitmap));
		//Log.d("BackgroundActivateProfileActivity.onStart","_preferencesIndicator="+String.valueOf(profile._preferencesIndicator));
		
		if ((startupSource == GlobalData.STARTUP_SOURCE_SHORTCUT) ||
			(startupSource == GlobalData.STARTUP_SOURCE_SERVICE) ||
			(startupSource == GlobalData.STARTUP_SOURCE_SERVICE_INTERACTIVE))	
		{
			if (profile_id == 0)
				profile = null;
			else
				profile = profilesDataWrapper.getProfileById(profile_id);

			//Log.d("BackgroundActivateProfileActivity.onStart","_iconBitmap="+String.valueOf(profile._iconBitmap));
			//Log.d("BackgroundActivateProfileActivity.onStart","_preferencesIndicator="+String.valueOf(profile._preferencesIndicator));
		}

		
		if (actProfile && (profile != null))
		{
			// aktivacia profilu
			activateProfile(profile, interactive);
		}
		else
		{
			activateProfileHelper.showNotification(profile);
			activateProfileHelper.updateWidget();
		}
		
		//Log.d("ActivateProfileActivity.onStart", "xxxx");
		
		finish();
	}
	
	private void activateProfile(Profile profile, boolean interactive)
	{
		databaseHandler.activateProfile(profile);
		profilesDataWrapper.activateProfile(profile);

		activateProfileHelper.execute(profile, interactive);
		activateProfileHelper.showNotification(profile);
		activateProfileHelper.updateWidget();
		
		//profilesDataWrapper.sendMessageIntoServiceLong(PhoneProfilesService.MSG_PROFILE_ACTIVATED, profile_id);

		if (GlobalData.notificationsToast)
		{	
			// toast notification
			Toast msg = Toast.makeText(this, 
					getResources().getString(R.string.toast_profile_activated_0) + ": " + profile._name + " " +
					getResources().getString(R.string.toast_profile_activated_1), 
					Toast.LENGTH_LONG);
			msg.show();
		}

		
	}
	
}
