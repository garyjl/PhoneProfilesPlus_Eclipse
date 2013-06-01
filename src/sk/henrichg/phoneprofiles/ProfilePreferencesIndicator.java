package sk.henrichg.phoneprofiles;

import sk.henrichg.phoneprofiles.Profile;
import sk.henrichg.phoneprofiles.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;


public class ProfilePreferencesIndicator {
	
	private static final int PREFERENCES_COUNT = 10;
	
	ProfilePreferencesIndicator()
	{
		
	}
	
	private static Bitmap createIndicatorBitmap(Context context)
	{
		// bitmapa, z ktorej zobrerieme velkost
    	Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_profile_pref_volume_on);

		int width  = bmp.getWidth() * PREFERENCES_COUNT; 
		int height  = bmp.getHeight();
		
		return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	}
	
	private static int preferenceIndex;
	
	private static void addIndicator(Bitmap indicatorBitmap, int preferenceBitmapResourceID, Context context)
	{
		Bitmap preferenceBitmap = BitmapFactory.decodeResource(context.getResources(), preferenceBitmapResourceID);
		
		Canvas canvas = new Canvas(indicatorBitmap);
		canvas.drawBitmap(preferenceBitmap, preferenceBitmap.getWidth() * preferenceIndex, 0, null);
		//canvas.save();
		
		++preferenceIndex;
	}
	
	public static Bitmap paint(Profile profile, Context context)
	{
		Bitmap indicatorBitmap = createIndicatorBitmap(context);
		
		if (profile != null)
		{
			preferenceIndex = 0;
			
			// volume on
			if ((profile.getVolumeRingerMode() == 1) || (profile.getVolumeRingerMode() == 2))
				addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_volume_on, context);				
			// vibration
			if ((profile.getVolumeRingerMode() == 2) || (profile.getVolumeRingerMode() == 3))
				addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_vibration, context);				
			// volume off
			if (profile.getVolumeRingerMode() == 4)
				addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_volume_off, context);				
			// sound
			if (profile.getSoundRingtoneChange() || profile.getSoundNotificationChange() || profile.getSoundAlarmChange())
				addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_sound, context);				
			// airplane mode
			if (CheckHardwareFeatures.check(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_AIRPLANE_MODE, context))
			{
				if ((profile.getDeviceAirplaneMode() == 1) || (profile.getDeviceAirplaneMode() == 3))
					addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_airplane_mode, context);				
				if (profile.getDeviceAirplaneMode() == 2)
					addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_airplane_mode_off, context);
			}
			// mobile data
			if (CheckHardwareFeatures.check(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_MOBILE_DATA, context))
			{
				if ((profile.getDeviceMobileData() == 1) || (profile.getDeviceMobileData() == 3))
					addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_mobiledata, context);				
				if (profile.getDeviceMobileData() == 2)
					addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_mobiledata_off, context);				
				// mobile data preferences
				if (profile.getDeviceMobileDataPrefs())
					addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_mobiledata_pref, context);
			}
			// wifi
			if (CheckHardwareFeatures.check(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_WIFI, context))
			{
				if ((profile.getDeviceWiFi() == 1) || (profile.getDeviceWiFi() == 3))
					addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_wifi, context);				
				if (profile.getDeviceWiFi() == 2)
					addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_wifi_off, context);
			}
			// bluetooth
			if (CheckHardwareFeatures.check(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_BLUETOOTH, context))
			{
				if ((profile.getDeviceBluetooth() == 1) || (profile.getDeviceBluetooth() == 3))
					addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_bluetooth, context);				
				if (profile.getDeviceBluetooth() == 2)
					addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_bluetooth_off, context);
			}
			// gps
			if (CheckHardwareFeatures.check(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_GPS, context))
			{
				if ((profile.getDeviceGPS() == 1) || (profile.getDeviceGPS() == 3))
					addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_gps_on, context);				
				if (profile.getDeviceGPS() == 2)
					addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_gps_off, context);
			}
			// screen timeout
			if (profile.getDeviceScreenTimeout() != 0)
				addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_screen_timeout, context);				
			// brightness/autobrightness
			if (profile.getDeviceBrightnessChange())
				if (profile.getDeviceBrightnessAutomatic())
					addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_autobrightness, context);				
				else
					addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_brightness, context);	
			// run application
			if (profile.getDeviceRunApplicationChange())
				addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_run_application, context);				
			// wallpaper
			if (profile.getDeviceWallpaperChange())
				addIndicator(indicatorBitmap, R.drawable.ic_profile_pref_wallpaper, context);				
			
		}
		
		return indicatorBitmap;
		//imageView.setImageBitmap(indicatorBitmap);
		//imageView.setImageBitmap(indicatorBitmap.copy(indicatorBitmap.getConfig(), false));
		//imageView.setImageDrawable(new BitmapDrawable(imageView.getContext().getResources(), indicatorBitmap));
		
	}

}
