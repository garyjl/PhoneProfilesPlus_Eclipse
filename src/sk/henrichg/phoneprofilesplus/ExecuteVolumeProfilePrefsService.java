package sk.henrichg.phoneprofilesplus;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;

public class ExecuteVolumeProfilePrefsService extends IntentService //WakefulIntentService 
{
	
	public ExecuteVolumeProfilePrefsService() {
		super("ExecuteRadioProfilePrefsService");
	}

	//@Override
	//protected void doWakefulWork(Intent intent) {
	protected void onHandleIntent(Intent intent) {
		
		Context context = getBaseContext();
		
		GlobalData.loadPreferences(context);
		
		DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
		ActivateProfileHelper aph = dataWrapper.getActivateProfileHelper();
		aph.initialize(dataWrapper, null, context);
		
		long profile_id = intent.getLongExtra(GlobalData.EXTRA_PROFILE_ID, 0);
		String eventNotificationSound = intent.getStringExtra(GlobalData.EXTRA_EVENT_NOTIFICATION_SOUND);
		if (eventNotificationSound == null) eventNotificationSound = "";

		Profile profile = dataWrapper.getProfileById(profile_id);
		profile = GlobalData.getMappedProfile(profile, context);
		//profile = dataWrapper.filterProfileWithBatteryEvents(profile);
		
		if (profile != null)
		{
			AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			
			// nahodenie ringer modu - aby sa mohli nastavit hlasitosti
			aph.setRingerMode(profile, audioManager);
			
			aph.setVolumes(profile, audioManager);

			// nahodenie ringer modu - hlasitosti zmenia silent/vibrate
			aph.setRingerMode(profile, audioManager);
			
		/*	if (intent.getBooleanExtra(GlobalData.EXTRA_SECOND_SET_VOLUMES, false))
			{
				// run service for execute volumes - second set
				Intent volumeServiceIntent = new Intent(context, ExecuteVolumeProfilePrefsService.class);
				volumeServiceIntent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile._id);
				volumeServiceIntent.putExtra(GlobalData.EXTRA_SECOND_SET_VOLUMES, false);
				volumeServiceIntent.putExtra(GlobalData.EXTRA_EVENT_NOTIFICATION_SOUND, eventNotificationSound);
				//WakefulIntentService.sendWakefulWork(context, radioServiceIntent);
				context.startService(volumeServiceIntent);
			}
			else */
			{
				// play notification sound
				if (!eventNotificationSound.isEmpty())
				{
					audioManager.setMode(AudioManager.MODE_NORMAL);
					final String _eventNotificationSound = eventNotificationSound;
					final DataWrapper _dataWrapper = dataWrapper;
					
				    Handler handler = new Handler(getMainLooper());
					handler.post(new Runnable() {
						public void run() {
							MediaPlayer mp=new MediaPlayer();
							Uri ringtoneUri=Uri.parse(_eventNotificationSound);
							try {
								mp.setDataSource(_dataWrapper.context, ringtoneUri);
							    mp.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
							    mp.prepare();
							    mp.start();
					        	Thread.sleep(2000);
							}
							catch(Exception e)
							{
							    e.printStackTrace();
							}				
						}
					});
					
					/*
					Uri notification = Uri.parse(_eventNotificationSound);
							//RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					Ringtone r = RingtoneManager.getRingtone(_dataWrapper.context, notification);
					r.play();
					*/					
				}
			}
		}
		dataWrapper.invalidateDataWrapper();
		aph = null;
		dataWrapper = null;
	}
	
	
}
