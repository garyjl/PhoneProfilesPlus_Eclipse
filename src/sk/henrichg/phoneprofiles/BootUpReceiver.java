package sk.henrichg.phoneprofiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		PhoneProfilesPreferencesActivity.loadPreferences(context);
		
		if (PhoneProfilesPreferencesActivity.applicationStartOnBoot)
		{	
			Intent i = new Intent(context, BackgroundActivateProfileActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_BOOT);
			context.startActivity(i);
		}

	}

}
