package sk.henrichg.phoneprofiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PhoneProfilesServiceStarter extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Intent service = new Intent(context, PhoneProfilesService.class);
		context.startService(service);
	}

}
