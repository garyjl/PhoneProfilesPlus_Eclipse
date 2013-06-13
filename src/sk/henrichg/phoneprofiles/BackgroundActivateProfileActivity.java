package sk.henrichg.phoneprofiles;

import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class BackgroundActivateProfileActivity extends Activity {

	private DatabaseHandler databaseHandler;
	private ActivateProfileHelper activateProfileHelper;
	
	private int startupSource = 0;
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//PhoneProfilesActivity.setLanguage(getBaseContext());
		
		intent = getIntent();
		startupSource = intent.getIntExtra(GlobalData.EXTRA_START_APP_SOURCE, 0);
		
		activateProfileHelper = new ActivateProfileHelper(this, getBaseContext());

		databaseHandler = GlobalData.getDatabaseHandler();
		
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		//Log.d("ActivateProfileActivity.onStart", "startupSource="+startupSource);
		
		boolean actProfile = false;
		boolean interactive = false;
		if (startupSource == GlobalData.STARTUP_SOURCE_SHORTCUT)
		{
			// aktivita spustena z shortcutu, profil aktivujeme
			actProfile = true;
			interactive = true;
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
		profile = GlobalData.getActivatedProfile();
		activateProfileHelper.showNotification(profile);
		activateProfileHelper.updateWidget();
		
		if (startupSource == GlobalData.STARTUP_SOURCE_SHORTCUT)
		{
			long profile_id = intent.getLongExtra(GlobalData.EXTRA_PROFILE_ID, 0);
			if (profile_id == 0)
				profile = null;
			else
				profile = GlobalData.getProfileById(profile_id);
		}
		
		if (actProfile && (profile != null))
		{
			// aktivacia profilu
			activateProfile(profile, interactive);
		}
		
		//Log.d("ActivateProfileActivity.onStart", "xxxx");
		
		finish();
	}
	
	private void activateProfile(Profile profile, boolean interactive)
	{
		databaseHandler.activateProfile(profile);
		GlobalData.activateProfile(profile);

		activateProfileHelper.execute(profile, interactive);
		activateProfileHelper.showNotification(profile);
		activateProfileHelper.updateWidget();

		if (GlobalData.notificationsToast)
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
