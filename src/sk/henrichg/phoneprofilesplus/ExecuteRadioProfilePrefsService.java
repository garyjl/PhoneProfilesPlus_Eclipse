package sk.henrichg.phoneprofilesplus;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class ExecuteRadioProfilePrefsService extends IntentService 
{
	
	public ExecuteRadioProfilePrefsService() {
		super("ExecuteRadioProfilePrefsService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		GlobalData.logE("ExecuteRadioProfilePrefsService.onHandleIntent","-- START ----------");
		
		Context context = getBaseContext();
		
		GlobalData.loadPreferences(context);
		
		DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
		ActivateProfileHelper aph = dataWrapper.getActivateProfileHelper();
		aph.initialize(dataWrapper, null, context);
		
		long profile_id = intent.getLongExtra(GlobalData.EXTRA_PROFILE_ID, 0);
		Profile profile = dataWrapper.getProfileById(profile_id);
		profile = GlobalData.getMappedProfile(profile, context);
		//profile = dataWrapper.filterProfileWithBatteryEvents(profile);
		if (profile != null)
		{
			aph.executeForRadios(profile);
		}
		dataWrapper.invalidateDataWrapper();
		aph = null;
		dataWrapper = null;
		
		//SetRadioPrefsForProfileBroadcastReceiver.completeWakefulIntent(intent);

		GlobalData.logE("ExecuteRadioProfilePrefsService.onHandleIntent","-- END ----------");
		
	}
}
