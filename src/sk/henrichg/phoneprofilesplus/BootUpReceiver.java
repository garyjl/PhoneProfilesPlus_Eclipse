package sk.henrichg.phoneprofilesplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		GlobalData.logE("## BootUpReceiver.onReceive", "xxxx");
		GlobalData.logE("BootUpReceiver.onReceive", "applicationStartOnBoot="+GlobalData.applicationStartOnBoot);
		GlobalData.logE("BootUpReceiver.onReceive", "globalEventsRunning="+GlobalData.getGlobalEventsRuning(context));
		
		GlobalData.setApplicationStarted(context, false);
		
		if (GlobalData.applicationStartOnBoot)
		{	
			GlobalData.setApplicationStarted(context, true);
			
			GlobalData.grantRoot();

			DataWrapper dataWrapper = new DataWrapper(context, true, false, 0);
			dataWrapper.getActivateProfileHelper().initialize(dataWrapper, null, context);
			
			// startneme eventy
			if (GlobalData.getGlobalEventsRuning(context))
			{
				dataWrapper.firstStartEvents(true, false);
			}

			dataWrapper.activateProfile(0, GlobalData.STARTUP_SOURCE_BOOT, null, "");
			dataWrapper.invalidateDataWrapper();

			/*
			Intent i = new Intent(context, BackgroundActivateProfileActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_BOOT);
			context.startActivity(i);
			*/
		}

	}

}
