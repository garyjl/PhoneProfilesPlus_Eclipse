package sk.henrichg.phoneprofiles;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class PhoneProfilesDashClockExtension extends DashClockExtension {

	private ProfilesDataWrapper profilesDataWrapper;
	private Context context;
	private static PhoneProfilesDashClockExtension instance;
	
	public PhoneProfilesDashClockExtension()
	{
		instance = this;
	}
	
	public static PhoneProfilesDashClockExtension getInstance()
	{
		return instance;
	}
	
	@Override
    protected void onInitialize(boolean isReconnect) {
		super.onInitialize(isReconnect);

		context = getApplicationContext();
		
		if (profilesDataWrapper == null)
			profilesDataWrapper = new ProfilesDataWrapper(context, true, false, 0, false, false, false);
	
		setUpdateWhenScreenOn(true);
	}

	private int maxLength;
	private String addIntoIndicator(String indicator, String preference)
	{
		String ind = indicator;
		if (ind.length() > maxLength)
		{
			ind = ind + '\n';
			maxLength += 25;
		}
		else
			if (ind != "") ind = ind + "-";
		ind = ind + preference;
		return ind;
	}
	
	@Override
	protected void onUpdateData(int reason) {
		
		Profile profile = profilesDataWrapper.getActivatedProfile();
		
		boolean isIconResourceID;
		String iconIdentifier;
		String profileName;
		if (profile != null)
		{
			isIconResourceID = profile.getIsIconResourceID();
			iconIdentifier = profile.getIconIdentifier();
			profileName = profile._name;
		}
		else
		{
			isIconResourceID = true;
			iconIdentifier = GUIData.PROFILE_ICON_DEFAULT;
			profileName = context.getResources().getString(R.string.profile_name_default);
		}
		int iconResource;
		if (isIconResourceID)
			iconResource = context.getResources().getIdentifier(iconIdentifier, "drawable", context.getPackageName());
		else
			iconResource = context.getResources().getIdentifier(GUIData.PROFILE_ICON_DEFAULT, "drawable", context.getPackageName());
	
		// profile preferences indicator
		maxLength = 25;
		String indicator1 = "";
		// volume on
		if ((profile._volumeRingerMode == 1) || (profile._volumeRingerMode == 2))
			indicator1 = addIntoIndicator(indicator1, "rng");
		// vibration
		if ((profile._volumeRingerMode == 2) || (profile._volumeRingerMode == 3))
			indicator1 = addIntoIndicator(indicator1, "vib");
		// volume off
		if (profile._volumeRingerMode == 4)
			indicator1 = addIntoIndicator(indicator1, "sil");
		// sound
		if (profile._soundRingtoneChange || profile._soundNotificationChange || profile._soundAlarmChange)
			indicator1 = addIntoIndicator(indicator1, "snd");
		// airplane mode
		if ((profile._deviceAirplaneMode == 1) || (profile._deviceAirplaneMode == 3))
			indicator1 = addIntoIndicator(indicator1, "am1");
		if (profile._deviceAirplaneMode == 2)
			indicator1 = addIntoIndicator(indicator1, "am0");
		// mobile data
		if ((profile._deviceMobileData == 1) || (profile._deviceMobileData == 3))
			indicator1 = addIntoIndicator(indicator1, "md1");
		if (profile._deviceMobileData == 2)
			indicator1 = addIntoIndicator(indicator1, "md0");
		// mobile data preferences
		if (profile._deviceMobileDataPrefs)
			indicator1 = addIntoIndicator(indicator1, "mdP");
		// wifi
		if ((profile._deviceWiFi == 1) || (profile._deviceWiFi == 3))
			indicator1 = addIntoIndicator(indicator1, "wf1");
		if (profile._deviceWiFi == 2)
			indicator1 = addIntoIndicator(indicator1, "wf0");
		// bluetooth
		if ((profile._deviceBluetooth == 1) || (profile._deviceBluetooth == 3))
			indicator1 = addIntoIndicator(indicator1, "bt1");
		if (profile._deviceBluetooth == 2)
			indicator1 = addIntoIndicator(indicator1, "bt0");
		// gps
		if ((profile._deviceGPS == 1) || (profile._deviceGPS == 3))
			indicator1 = addIntoIndicator(indicator1, "gp1");
		if (profile._deviceGPS == 2)
			indicator1 = addIntoIndicator(indicator1, "gp0");
		// screen timeout
		if (profile._deviceScreenTimeout != 0)
			indicator1 = addIntoIndicator(indicator1, "stm");
		// brightness/autobrightness
		if (profile.getDeviceBrightnessChange())
		{
			if (profile.getDeviceBrightnessAutomatic())
				indicator1 = addIntoIndicator(indicator1, "brA");
			else
				indicator1 = addIntoIndicator(indicator1, "brt");
		}
		// run application
		if (profile._deviceRunApplicationChange)
			indicator1 = addIntoIndicator(indicator1, "rap");
		// wallpaper
		if (profile._deviceWallpaperChange)
			indicator1 = addIntoIndicator(indicator1, "wlp");
		/////////////////////////////////////////////////////////////
		
		// intent
		Intent intent = new Intent(context, ActivateProfileActivity.class);
		intent.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_WIDGET);
		
	    // Publish the extension data update.
        publishUpdate(new ExtensionData()
                .visible(true)
                .icon(iconResource)
                .status("")
                .expandedTitle(profileName)
                .expandedBody(indicator1)
                .contentDescription("PhoneProfiles - "+profileName)
                .clickIntent(intent));		
	}

	public void updateExtension()
	{
		onUpdateData(UPDATE_REASON_CONTENT_CHANGED);
	}
	
}
