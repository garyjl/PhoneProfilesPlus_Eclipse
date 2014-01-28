package sk.henrichg.phoneprofilesplus;

import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

public class BackgroundActivateProfileActivity extends Activity {

	private DataWrapper dataWrapper;
	private DatabaseHandler databaseHandler;
	private ActivateProfileHelper activateProfileHelper;
	
	private int startupSource = 0;
	private long profile_id;
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Log.d("BackgroundActivateProfileActivity.onCreate","xxx");
		
		GlobalData.loadPreferences(getBaseContext());
		
		dataWrapper = new DataWrapper(getBaseContext(), true, false, 0);
		
		intent = getIntent();
		startupSource = intent.getIntExtra(GlobalData.EXTRA_START_APP_SOURCE, 0);
		profile_id = intent.getLongExtra(GlobalData.EXTRA_PROFILE_ID, 0);
		//Log.d("BackgroundActivateProfileActivity.onStart", "profile_id="+profile_id);

		
		activateProfileHelper = dataWrapper.getActivateProfileHelper();
		activateProfileHelper.initialize(this, getBaseContext());

		// initialize global profile list
		databaseHandler = dataWrapper.getDatabaseHandler();
		
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		//Log.d("BackgroundActivateProfileActivity.onStart", "startupSource="+startupSource);
		
		Profile profile;
		
		// pre profil, ktory je prave aktivny, treba aktualizovat aktivitu
		profile = dataWrapper.getActivatedProfile();
		
		boolean actProfile = false;
		boolean interactive = false;
		if ((startupSource == GlobalData.STARTUP_SOURCE_SHORTCUT) ||
			(startupSource == GlobalData.STARTUP_SOURCE_WIDGET) ||
			(startupSource == GlobalData.STARTUP_SOURCE_ACTIVATOR) ||
			(startupSource == GlobalData.STARTUP_SOURCE_EDITOR) ||
			(startupSource == GlobalData.STARTUP_SOURCE_SERVICE))
		{
			// aktivita spustena z shortcutu alebo zo service, profil aktivujeme
			actProfile = true;
			interactive = ((startupSource != GlobalData.STARTUP_SOURCE_SERVICE));
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
			else
			{
				if (profile != null)
				{
					dataWrapper.getDatabaseHandler().deactivateProfile();
					//profile._checked = false;
					profile = null;
				}
			}
		}
		else
		if (startupSource == GlobalData.STARTUP_SOURCE_ACTIVATOR_START)	
		{
			// aktivita bola spustena po boote telefonu
			
			if (GlobalData.applicationActivate)
			{
				// je nastavene, ze pri starte sa ma aktivita aktivovat
				actProfile = true;
			}
			else
			{
				if (profile != null)
				{
					dataWrapper.getDatabaseHandler().deactivateProfile();
					//profile._checked = false;
					profile = null;
				}
			}
		}
			
		//Log.d("BackgroundActivateProfileActivity.onStart", "actProfile="+String.valueOf(actProfile));

		if ((startupSource == GlobalData.STARTUP_SOURCE_SHORTCUT) ||
			(startupSource == GlobalData.STARTUP_SOURCE_WIDGET) ||
			(startupSource == GlobalData.STARTUP_SOURCE_ACTIVATOR) ||
			(startupSource == GlobalData.STARTUP_SOURCE_EDITOR) ||
			(startupSource == GlobalData.STARTUP_SOURCE_SERVICE))	
		{
			if (profile_id == 0)
				profile = null;
			else
				profile = dataWrapper.getProfileById(profile_id);

			//Log.d("BackgroundActivateProfileActivity.onStart","_iconBitmap="+String.valueOf(profile._iconBitmap));
			//Log.d("BackgroundActivateProfileActivity.onStart","_preferencesIndicator="+String.valueOf(profile._preferencesIndicator));
		}

		
		if (actProfile && (profile != null))
		{
			// aktivacia profilu
			activateProfileWithAlert(profile, interactive);
		}
		else
		{
			activateProfileHelper.showNotification(profile);
			activateProfileHelper.updateWidget();
			
			// for startActivityForResult
			Intent returnIntent = new Intent();
			returnIntent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile_id);
			returnIntent.getIntExtra(GlobalData.EXTRA_START_APP_SOURCE, startupSource);
			setResult(RESULT_OK,returnIntent);
			
			finishActivity();
		}
		
		//Log.d("ActivateProfileActivity.onStart", "xxxx");
		
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		activateProfileHelper = null;
		databaseHandler = null;
		dataWrapper.invalidateDataWrapper();
		dataWrapper = null;
	}
	
	
	private void activateProfileWithAlert(Profile profile, boolean interactive)
	{
		// set theme and language for dialog alert ;-)
		// not working on Android 2.3.x
		GUIData.setTheme(this, true);
		GUIData.setLanguage(getBaseContext());

		if ((GlobalData.applicationActivateWithAlert && interactive) ||
			(startupSource == GlobalData.STARTUP_SOURCE_EDITOR))	
		{	
			final Profile _profile = profile;
			final boolean _interactive = interactive;
			final Activity activity = this;

			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle(getResources().getString(R.string.profile_string_0) + ": " + profile._name);
			dialogBuilder.setMessage(getResources().getString(R.string.activate_profile_alert_message) + "?");
			//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
			dialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					activateProfile(_profile, _interactive);
				}
			});
			dialogBuilder.setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {

					// for startActivityForResult
					Intent returnIntent = new Intent();
					setResult(RESULT_CANCELED,returnIntent);

					finishActivity();
				}
			});
			dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				public void onCancel(DialogInterface dialog) {
					// for startActivityForResult
					Intent returnIntent = new Intent();
					setResult(RESULT_CANCELED,returnIntent);

					finishActivity();
				}
			});
			dialogBuilder.show();
		}
		else
		{
			activateProfile(profile, interactive);
		}
	}
	
	private void activateProfile(Profile _profile, boolean _interactive)
	{
		final Profile profile = _profile;
		final boolean interactive = _interactive;
		final Activity activity = this;
		
		databaseHandler.activateProfile(profile);
		dataWrapper.activateProfile(profile);
		if (startupSource != GlobalData.STARTUP_SOURCE_SERVICE)
			// for manual activation pause all running events
			// and setup for next start
			dataWrapper.pauseAllEvents(false);
		
		activateProfileHelper.execute(profile, interactive);
		
		activateProfileHelper.showNotification(profile);
		activateProfileHelper.updateWidget();
		
		if (GlobalData.notificationsToast)
		{	
			// toast notification
			Toast msg = Toast.makeText(activity, 
					getResources().getString(R.string.toast_profile_activated_0) + ": " + profile._name + " " +
					getResources().getString(R.string.toast_profile_activated_1), 
					Toast.LENGTH_SHORT);
			msg.show();
		}
		
		// for startActivityForResult
		Intent returnIntent = new Intent();
		returnIntent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile_id);
		returnIntent.getIntExtra(GlobalData.EXTRA_START_APP_SOURCE, startupSource);
		setResult(RESULT_OK,returnIntent);
		
		finishActivity();
	}

	private void finishActivity()
	{
		Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                System.out.println("finally exit");
                finish();
            }
        });
		t.start();			
	}
	
}
