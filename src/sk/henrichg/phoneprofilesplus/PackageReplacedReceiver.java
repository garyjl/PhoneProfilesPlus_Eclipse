package sk.henrichg.phoneprofilesplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PackageReplacedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		int intentUid = intent.getExtras().getInt("android.intent.extra.UID");
		int myUid = android.os.Process.myUid();
		if (intentUid == myUid)
		{
			//Log.e("PackageReplacedReceiver.onReceive","xxx");
			
			if (GlobalData.getApplicationStarted(context))
			{
				GlobalData.grantRoot();
					
				DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
				dataWrapper.firstStartEvents(true);
				dataWrapper.invalidateDataWrapper();
			}
		}		
	}

}
