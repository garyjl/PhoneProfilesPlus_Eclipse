package sk.henrichg.phoneprofilesplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PhoneProfilesServiceStarter extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		//Log.d("PhoneProfilesServiceStarter.onResume","xxx");
		
		Intent service = new Intent(context, PhoneProfilesService.class);
		context.startService(service);
	}

}
