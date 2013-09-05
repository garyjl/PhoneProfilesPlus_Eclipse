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
			profilesDataWrapper = new ProfilesDataWrapper(context, true, false, false, false);
	
		setUpdateWhenScreenOn(true);
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
		
		// intent
		Intent intent = new Intent(context, ActivateProfileActivity.class);
		intent.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_WIDGET);
		
	    // Publish the extension data update.
        publishUpdate(new ExtensionData()
                .visible(true)
                .icon(iconResource)
                .status("")
                .expandedTitle(profileName)
                .expandedBody("abcd\n123")   // indicator??
                .contentDescription("PhoneProfiles - "+profileName)
                .clickIntent(intent));		
	}

	public void updateExtension()
	{
		onUpdateData(UPDATE_REASON_CONTENT_CHANGED);
	}
	
}
