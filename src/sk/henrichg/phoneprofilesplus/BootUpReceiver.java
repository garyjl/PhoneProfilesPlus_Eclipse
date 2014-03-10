package sk.henrichg.phoneprofilesplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		GlobalData.setApplicationStarted(context, false);
		
		if (GlobalData.applicationStartOnBoot)
		{	
			GlobalData.setApplicationStarted(context, true);
			
			GlobalData.grantRoot();

			// startneme eventy
			if (GlobalData.getGlobalEventsRuning(context))
			{
				DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
				dataWrapper.firstStartEvents(true, false);
				dataWrapper.invalidateDataWrapper();
			}
			
			Intent i = new Intent(context, BackgroundActivateProfileActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_BOOT);
			context.startActivity(i);
		}

	}

}
