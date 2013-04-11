package sk.henrichg.phoneprofiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		SharedPreferences preferences = context.getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, Context.MODE_PRIVATE);

		if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_APPLICATION_START_ON_BOOT, false))
		{	
		//	Intent i = new Intent(context, PhoneProfilesActivity.class);
			Intent i = new Intent(context, ActivateProfileActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra(PhoneProfilesActivity.EXTRA_START_APP_SOURCE, PhoneProfilesActivity.STARTUP_SOURCE_BOOT);
			context.startActivity(i);
		}

	}

}
