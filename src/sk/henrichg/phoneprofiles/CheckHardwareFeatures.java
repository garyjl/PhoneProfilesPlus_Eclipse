package sk.henrichg.phoneprofiles;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.stericson.RootTools.RootTools;

public class CheckHardwareFeatures {
	
	static private boolean rootChecked = false;
	static private boolean rooted = false;

	static boolean check(String preferenceKey, Context context)
	{
		boolean featurePresented = false;

		if (!rootChecked)
		{
			if (RootTools.isAccessGiven())
			{
				// zariadenie je rootnute
				rootChecked = true;
				rooted = true;
			}
			else
			{
				rootChecked = true;
				rooted = false;
			}
		}
		
		if (preferenceKey.equals(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_AIRPLANE_MODE))
		{	
			if (android.os.Build.VERSION.SDK_INT >= 17)
			{
				if (rooted)
				{
					// zariadenie je rootnute
					featurePresented = true;
				}
				else
				if (AirPlaneMode_SDK17.isSystemApp(context) && AirPlaneMode_SDK17.isAdminUser(context))
				{
					// aplikacia je nainstalovana ako systemova
					featurePresented = true;
				}
			}
		}
		else
		if (preferenceKey.equals(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_WIFI))
		{	
			if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI))
				// device ma Wifi
				featurePresented = true;
		}
		else
		if (preferenceKey.equals(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_BLUETOOTH))
		{	
			if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
				// device ma bluetooth
				featurePresented = true;
		}
		else
		if (preferenceKey.equals(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_MOBILE_DATA))
		{	
			if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY))
				// device ma mobilne data
				featurePresented = true;
		}
		else
		if (preferenceKey.equals(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_GPS))
		{	
			if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS))
			{
				// device ma gps

				// test expoiting power manager widget
			    PackageManager pacman = context.getPackageManager();
			    PackageInfo pacInfo = null;
			    try {
			        pacInfo = pacman.getPackageInfo("com.android.settings", PackageManager.GET_RECEIVERS);

				    if(pacInfo != null){
				        for(ActivityInfo actInfo : pacInfo.receivers){
				            //test if recevier is exported. if so, we can toggle GPS.
				            if(actInfo.name.equals("com.android.settings.widget.SettingsAppWidgetProvider") && actInfo.exported){
								featurePresented = true;
				            }
				        }
				    }				
			    } catch (NameNotFoundException e) {
			        ; //package not found
			    }
			}
		}
		else
			featurePresented = true;
		
		return featurePresented;
	}
}
