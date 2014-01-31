package sk.henrichg.phoneprofilesplus;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class LauncherActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int startupSource;
		
		Intent intent = getIntent();
		startupSource = intent.getIntExtra(GlobalData.EXTRA_START_APP_SOURCE, 0);
		
		Intent intentLaunch;
		
		switch (startupSource) {
			case GlobalData.STARTUP_SOURCE_NOTIFICATION:
				if (GlobalData.applicationNotificationLauncher.equals("activator"))
					intentLaunch = new Intent(getBaseContext(), ActivateProfileActivity.class);
				else
					intentLaunch = new Intent(getBaseContext(), EditorProfilesActivity.class);
				break;
			case GlobalData.STARTUP_SOURCE_WIDGET:
				if (GlobalData.applicationWidgetLauncher.equals("activator"))
					intentLaunch = new Intent(getBaseContext(), ActivateProfileActivity.class);
				else
					intentLaunch = new Intent(getBaseContext(), EditorProfilesActivity.class);
				break;
			default:
				if (GlobalData.applicationHomeLauncher.equals("activator"))
					intentLaunch = new Intent(getBaseContext(), ActivateProfileActivity.class);
				else
					intentLaunch = new Intent(getBaseContext(), EditorProfilesActivity.class);
				break;
		}
		
		intentLaunch.putExtra(GlobalData.EXTRA_START_APP_SOURCE, startupSource);
		startActivity(intentLaunch);
		
		finish();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}	
	
}
