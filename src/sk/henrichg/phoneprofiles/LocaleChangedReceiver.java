package sk.henrichg.phoneprofiles;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class LocaleChangedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		SharedPreferences preferences = context.getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, Context.MODE_PRIVATE);

		if (preferences.getString(PhoneProfilesPreferencesActivity.PREF_APPLICATION_LANGUAGE, "system").equals("system"))
		{	
			NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(PhoneProfilesActivity.NOTIFICATION_ID);
		}
		
		//Log.d("LocaleChangedReceiver.onReceive", "xxxxx");

	}

}
