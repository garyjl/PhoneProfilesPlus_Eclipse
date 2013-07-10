package sk.henrichg.phoneprofiles;

import sk.henrichg.phoneprofiles.Profile;
import sk.henrichg.phoneprofiles.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;


public class ProfilePreferencesIndicator {
	
	private static Bitmap createIndicatorBitmap(Context context, int countDrawables)
	{
		// bitmapa, z ktorej zobrerieme velkost
    	Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_profile_pref_volume_on);

		int width  = bmp.getWidth() * countDrawables; 
		int height  = bmp.getHeight();
		
		return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	}
	
	private static void addIndicator(Bitmap indicatorBitmap, int preferenceBitmapResourceID, int index, Context context, Canvas canvas)
	{
		Bitmap preferenceBitmap = BitmapFactory.decodeResource(context.getResources(), preferenceBitmapResourceID);
		
		canvas.drawBitmap(preferenceBitmap, preferenceBitmap.getWidth() * index, 0, null);
		//canvas.save();
		
	}
	
	public static Bitmap paint(Profile profile, Context context)
	{
		
		int[] drawables = new int[20];
		int countDrawables = 0;

		if (profile != null)
		{
			// volume on
			if ((profile._volumeRingerMode == 1) || (profile._volumeRingerMode == 2))
				drawables[countDrawables++] = R.drawable.ic_profile_pref_volume_on;
			// vibration
			if ((profile._volumeRingerMode == 2) || (profile._volumeRingerMode == 3))
				drawables[countDrawables++] = R.drawable.ic_profile_pref_vibration;
			// volume off
			if (profile._volumeRingerMode == 4)
				drawables[countDrawables++] = R.drawable.ic_profile_pref_volume_off;
			// sound
			if (profile._soundRingtoneChange || profile._soundNotificationChange || profile._soundAlarmChange)
				drawables[countDrawables++] = R.drawable.ic_profile_pref_sound;
			// airplane mode
			if ((profile._deviceAirplaneMode == 1) || (profile._deviceAirplaneMode == 3))
				drawables[countDrawables++] = R.drawable.ic_profile_pref_airplane_mode;
			if (profile._deviceAirplaneMode == 2)
				drawables[countDrawables++] = R.drawable.ic_profile_pref_airplane_mode_off;
			// mobile data
			if ((profile._deviceMobileData == 1) || (profile._deviceMobileData == 3))
				drawables[countDrawables++] = R.drawable.ic_profile_pref_mobiledata;
			if (profile._deviceMobileData == 2)
				drawables[countDrawables++] = R.drawable.ic_profile_pref_mobiledata_off;
			// mobile data preferences
			if (profile._deviceMobileDataPrefs)
				drawables[countDrawables++] = R.drawable.ic_profile_pref_mobiledata_pref;
			// wifi
			if ((profile._deviceWiFi == 1) || (profile._deviceWiFi == 3))
				drawables[countDrawables++] = R.drawable.ic_profile_pref_wifi;
			if (profile._deviceWiFi == 2)
				drawables[countDrawables++] = R.drawable.ic_profile_pref_wifi_off;
			// bluetooth
			if ((profile._deviceBluetooth == 1) || (profile._deviceBluetooth == 3))
				drawables[countDrawables++] = R.drawable.ic_profile_pref_bluetooth;
			if (profile._deviceBluetooth == 2)
				drawables[countDrawables++] = R.drawable.ic_profile_pref_bluetooth_off;
			// gps
			if ((profile._deviceGPS == 1) || (profile._deviceGPS == 3))
				drawables[countDrawables++] = R.drawable.ic_profile_pref_gps_on;
			if (profile._deviceGPS == 2)
				drawables[countDrawables++] = R.drawable.ic_profile_pref_gps_off;
			// screen timeout
			if (profile._deviceScreenTimeout != 0)
				drawables[countDrawables++] = R.drawable.ic_profile_pref_screen_timeout;
			// brightness/autobrightness
			if (profile.getDeviceBrightnessChange())
				if (profile.getDeviceBrightnessAutomatic())
					drawables[countDrawables++] = R.drawable.ic_profile_pref_autobrightness;
				else
					drawables[countDrawables++] = R.drawable.ic_profile_pref_brightness;
			// run application
			if (profile._deviceRunApplicationChange)
				drawables[countDrawables++] = R.drawable.ic_profile_pref_run_application;
			// wallpaper
			if (profile._deviceWallpaperChange)
				drawables[countDrawables++] = R.drawable.ic_profile_pref_wallpaper;
			
		}
		else
			countDrawables = -1;
		
		Bitmap indicatorBitmap;
		if (countDrawables >= 0)
		{
			if (countDrawables > 0)
			{
				indicatorBitmap = createIndicatorBitmap(context, countDrawables);
				Canvas canvas = new Canvas(indicatorBitmap);
			
				for (int i = 0; i < countDrawables; i++)
					addIndicator(indicatorBitmap, drawables[i], i, context, canvas);
			}
			else
				indicatorBitmap = createIndicatorBitmap(context, 1);
		}
		else
			indicatorBitmap = null;
		
		return indicatorBitmap;
		
	}

}
