package sk.henrichg.phoneprofilesplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PackageReplacedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		int intentUid = intent.getExtras().getInt("android.intent.extra.UID");
		int myUid = android.os.Process.myUid();
		if (intentUid == myUid)
		{
			GlobalData.loadPreferences(context);
			
			if (GlobalData.getApplicationStarted(context))
			{
				DataWrapper dataWrapper = new DataWrapper(context, true, false, 0);
				dataWrapper.getActivateProfileHelper().initialize(dataWrapper, null, context);

				GlobalData.setApplicationStarted(context, false);
				
				// stop all events
				dataWrapper.stopAllEvents(false);
				
				// zrusenie notifikacie
				dataWrapper.getActivateProfileHelper().removeNotification();

				GlobalData.setApplicationStarted(context, true);
				
				GlobalData.grantRoot(true);

				// startneme eventy
				if (GlobalData.getGlobalEventsRuning(context))
				{
					dataWrapper.firstStartEvents(true, false);
				}

				dataWrapper.activateProfile(0, GlobalData.STARTUP_SOURCE_BOOT, null, "");
				dataWrapper.invalidateDataWrapper();

				// start PPHelper
				PhoneProfilesHelper.startPPHelper(context);
			}
		}		
	}

}
