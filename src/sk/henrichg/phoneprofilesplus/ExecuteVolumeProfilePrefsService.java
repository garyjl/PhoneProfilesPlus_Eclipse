package sk.henrichg.phoneprofilesplus;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class ExecuteVolumeProfilePrefsService extends IntentService //WakefulIntentService 
{
	
	public ExecuteVolumeProfilePrefsService() {
		super("ExecuteRadioProfilePrefsService");
	}

	//@Override
	//protected void doWakefulWork(Intent intent) {
	protected void onHandleIntent(Intent intent) {
		
		Context context = getBaseContext();
		DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
		ActivateProfileHelper aph = dataWrapper.getActivateProfileHelper();
		aph.initialize(null, context);
		
		long profile_id = intent.getLongExtra(GlobalData.EXTRA_PROFILE_ID, 0);
		Profile profile = dataWrapper.getProfileById(profile_id);
		profile = GlobalData.getMappedProfile(profile, context);
		if (profile != null)
		{
			aph.setVolumes(profile);
		}
		dataWrapper.invalidateDataWrapper();
		aph = null;
		dataWrapper = null;
	}
	
	
}
