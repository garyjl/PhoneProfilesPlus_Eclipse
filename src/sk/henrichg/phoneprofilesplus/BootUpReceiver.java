package sk.henrichg.phoneprofilesplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		GlobalData.logE("## BootUpReceiver.onReceive", "xxxx");
		
		GlobalData.loadPreferences(context);
		
		GlobalData.logE("BootUpReceiver.onReceive", "applicationStartOnBoot="+GlobalData.applicationStartOnBoot);
		GlobalData.logE("BootUpReceiver.onReceive", "globalEventsRunning="+GlobalData.getGlobalEventsRuning(context));
		
		GlobalData.setApplicationStarted(context, false);
		
		if (GlobalData.applicationStartOnBoot)
		{	
			GlobalData.setApplicationStarted(context, true);
			
			GlobalData.grantRoot(true);

			// start PPHelper
			PhoneProfilesHelper.startPPHelper(context);
			
			DataWrapper dataWrapper = new DataWrapper(context, true, false, 0);
			dataWrapper.getActivateProfileHelper().initialize(dataWrapper, null, context);
			
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
					{
						profileId = profile._id;
						dataWrapper.getDatabaseHandler().deactivateProfile();
					}
					dataWrapper.activateProfile(profileId, GlobalData.STARTUP_SOURCE_BOOT, null, "");
				}
				else
					dataWrapper.activateProfile(0, GlobalData.STARTUP_SOURCE_BOOT, null, "");
			}

			dataWrapper.invalidateDataWrapper();
		}

	}

}
