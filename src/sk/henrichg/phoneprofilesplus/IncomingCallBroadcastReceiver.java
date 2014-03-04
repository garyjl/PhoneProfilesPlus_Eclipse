package sk.henrichg.phoneprofilesplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;

public class IncomingCallBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (!GlobalData.getApplicationStarted(context))
			return;
		
		DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
		Profile profile = dataWrapper.getActivatedProfile();
		
		if ((profile != null) && (profile._volumeSpeakerPhone != 0))
		{
			try
	        {
	            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
	            
	            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
	            {
	            }
	              
	            if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
	            {
	                try {
	                      Thread.sleep(500); // Delay 0,5 seconds to handle better turning on loudspeaker
	                } catch (InterruptedException e) {
	                }
	                 
	                //Activate loudspeaker
	                AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	                audioManager.setMode(AudioManager.MODE_IN_CALL);
	                audioManager.setSpeakerphoneOn(profile._volumeSpeakerPhone == 1);
	            }
	              
	            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
	            {
	            	//Deactivate loudspeaker
	                AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	            	if (audioManager.isSpeakerphoneOn())
	            	{
	            	    audioManager.setSpeakerphoneOn(false);
	            		audioManager.setMode(AudioManager.MODE_NORMAL); 
	                }
	            }
	        }
	        catch(Exception e)
	        {
	              e.printStackTrace();
	        }
		}
	}

}
