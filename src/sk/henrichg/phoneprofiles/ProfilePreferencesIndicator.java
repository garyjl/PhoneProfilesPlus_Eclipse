package sk.henrichg.phoneprofiles;

import sk.henrichg.phoneprofiles.Profile;
import sk.henrichg.phoneprofiles.R;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;


public class ProfilePreferencesIndicator {
	
	ProfilePreferencesIndicator()
	{
		
	}
	
	private static ImageView createIndicator(int resource, Context context)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		
		ImageView indicator = new ImageView(context);
		indicator.setImageResource(resource);
		indicator.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		indicator.setAdjustViewBounds(true);
		indicator.setScaleType(ScaleType.CENTER_CROP);
		indicator.setMaxWidth((int) (20 * scale + 0.5f));
		indicator.setMaxHeight((int) (20 * scale + 0.5f));
		
		return indicator;
	}
	
	public static void paint(LinearLayout parent, Profile profile)
	{
		
		parent.removeAllViews();
		
		if (profile != null)
		{
		
			// volume on
			if ((profile.getVolumeRingerMode() == 1) || (profile.getVolumeRingerMode() == 2))
				parent.addView(createIndicator(R.drawable.ic_profile_pref_volume_on, parent.getContext()));				
			// vibration
			if ((profile.getVolumeRingerMode() == 2) || (profile.getVolumeRingerMode() == 3))
				parent.addView(createIndicator(R.drawable.ic_profile_pref_vibration, parent.getContext()));				
			// volume off
			if (profile.getVolumeRingerMode() == 4)
				parent.addView(createIndicator(R.drawable.ic_profile_pref_volume_off, parent.getContext()));				
			// sound
			if (profile.getSoundRingtoneChange() || profile.getSoundNotificationChange() || profile.getSoundAlarmChange())
				parent.addView(createIndicator(R.drawable.ic_profile_pref_sound, parent.getContext()));				
			// airplane mode
			if (CheckHardwareFeatures.check(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_AIRPLANE_MODE, parent.getContext()))
			{
				if ((profile.getDeviceAirplaneMode() == 1) || (profile.getDeviceAirplaneMode() == 3))
					parent.addView(createIndicator(R.drawable.ic_profile_pref_airplane_mode, parent.getContext()));				
				if (profile.getDeviceAirplaneMode() == 2)
					parent.addView(createIndicator(R.drawable.ic_profile_pref_airplane_mode_off, parent.getContext()));
			}
			// mobile data
			if (CheckHardwareFeatures.check(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_MOBILE_DATA, parent.getContext()))
			{
				if ((profile.getDeviceMobileData() == 1) || (profile.getDeviceMobileData() == 3))
					parent.addView(createIndicator(R.drawable.ic_profile_pref_mobiledata, parent.getContext()));				
				if (profile.getDeviceMobileData() == 2)
					parent.addView(createIndicator(R.drawable.ic_profile_pref_mobiledata_off, parent.getContext()));				
				// mobile data preferences
				if (profile.getDeviceMobileDataPrefs())
					parent.addView(createIndicator(R.drawable.ic_profile_pref_mobiledata_pref, parent.getContext()));
			}
			// wifi
			if (CheckHardwareFeatures.check(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_WIFI, parent.getContext()))
			{
				if ((profile.getDeviceWiFi() == 1) || (profile.getDeviceWiFi() == 3))
					parent.addView(createIndicator(R.drawable.ic_profile_pref_wifi, parent.getContext()));				
				if (profile.getDeviceWiFi() == 2)
					parent.addView(createIndicator(R.drawable.ic_profile_pref_wifi_off, parent.getContext()));
			}
			// bluetooth
			if (CheckHardwareFeatures.check(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_BLUETOOTH, parent.getContext()))
			{
				if ((profile.getDeviceBluetooth() == 1) || (profile.getDeviceBluetooth() == 3))
					parent.addView(createIndicator(R.drawable.ic_profile_pref_bluetooth, parent.getContext()));				
				if (profile.getDeviceBluetooth() == 2)
					parent.addView(createIndicator(R.drawable.ic_profile_pref_bluetooth_off, parent.getContext()));
			}
			// screen timeout
			if (profile.getDeviceScreenTimeout() != 0)
				parent.addView(createIndicator(R.drawable.ic_profile_pref_screen_timeout, parent.getContext()));				
			// brightness/autobrightness
			if (profile.getDeviceBrightnessChange())
				if (profile.getDeviceBrightnessAutomatic())
					parent.addView(createIndicator(R.drawable.ic_profile_pref_autobrightness, parent.getContext()));				
				else
					parent.addView(createIndicator(R.drawable.ic_profile_pref_brightness, parent.getContext()));				
			// wallpaper
			if (profile.getDeviceWallpaperChange())
				parent.addView(createIndicator(R.drawable.ic_profile_pref_wallpaper, parent.getContext()));				
			
		}
	}

}
