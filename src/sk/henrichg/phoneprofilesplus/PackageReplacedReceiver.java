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
				GlobalData.grantRoot(true);

				// start PPHelper
				PhoneProfilesHelper.startPPHelper(context);
				// start ReceiverService
				context.startService(new Intent(context.getApplicationContext(), ReceiversService.class));
				
				DataWrapper dataWrapper = new DataWrapper(context, true, false, 0);
				dataWrapper.getActivateProfileHelper().initialize(dataWrapper, null, context);
				
				// zrusenie notifikacie
				dataWrapper.getActivateProfileHelper().removeNotification();

				// startneme eventy
				if (GlobalData.getGlobalEventsRuning(context))
					dataWrapper.firstStartEvents(true, false);
				else
				{
					BatteryEventsAlarmBroadcastReceiver.removeAlarm(context);
					if (GlobalData.applicationActivate)
					{
						Profile profile = dataWrapper.getDatabaseHandler().getActivatedProfile();
						long profileId = 0;
						if (profile != null)
							profileId = profile._id;
						dataWrapper.activateProfile(profileId, GlobalData.STARTUP_SOURCE_BOOT, null, "");
					}
					else
						dataWrapper.activateProfile(0, GlobalData.STARTUP_SOURCE_BOOT, null, "");
				}
				
				dataWrapper.invalidateDataWrapper();
			}
		}		
	}

}
