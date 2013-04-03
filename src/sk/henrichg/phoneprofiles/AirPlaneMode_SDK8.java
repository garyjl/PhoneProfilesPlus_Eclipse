package sk.henrichg.phoneprofiles;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class AirPlaneMode_SDK8 {

	@SuppressWarnings("deprecation")
	static boolean getAirplaneMode(Context context)
	{
		return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
	}
	
	@SuppressWarnings("deprecation")
	static void setAirplaneMode(Context context, boolean mode)
	{
		if (mode != getAirplaneMode(context))
		{
			Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, mode ? 1 : 0);
			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			intent.putExtra("state", mode);
			context.sendBroadcast(intent);
		}
	}
	
}
