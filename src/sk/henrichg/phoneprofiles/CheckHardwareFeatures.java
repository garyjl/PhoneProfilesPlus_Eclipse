package sk.henrichg.phoneprofiles;

import android.content.Context;
import android.content.pm.PackageManager;
import com.stericson.RootTools.RootTools;

public class CheckHardwareFeatures {

	static boolean check(String preferenceKey, Context context)
	{
		boolean featurePresented = false;
		
		if (preferenceKey.equals(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_AIRPLANE_MODE))
		{	
			if (android.os.Build.VERSION.SDK_INT >= 17)
			{
				if (AirPlaneMode_SDK17.isSystemApp(context) && AirPlaneMode_SDK17.isAdminUser(context))
				{
					// aplikacia je nainstalovana ako systemova
					featurePresented = true;
				}
				else
				if (RootTools.isAccessGiven())
				{
					// zariadenie je rootnute
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
			featurePresented = true;
		
		return featurePresented;
	}
}
