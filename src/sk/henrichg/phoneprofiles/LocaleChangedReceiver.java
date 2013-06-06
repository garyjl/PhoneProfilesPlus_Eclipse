package sk.henrichg.phoneprofiles;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LocaleChangedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		PhoneProfilesPreferencesActivity.loadPreferences(context);
		
		if (PhoneProfilesPreferencesActivity.applicationLanguage.equals("system"))
		{	
			NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(PhoneProfilesActivity.NOTIFICATION_ID);
		}
		
		//Log.d("LocaleChangedReceiver.onReceive", "xxxxx");

	}

}
