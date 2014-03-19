package sk.henrichg.phoneprofilesplus;

import java.util.Date;

import android.content.Context;
import android.media.AudioManager;

public class PhoneCallBroadcastReceiver extends PhoneCallReceiver {

	protected boolean onStartReceive()
	{
		if (!GlobalData.getApplicationStarted(super.savedContext))
			return false;
		
		GlobalData.loadPreferences(savedContext);
		
		return true;
	}

	protected void onEndReceive()
	{
	}
	
	private void callAnswered(boolean incoming)
	{
		DataWrapper dataWrapper = new DataWrapper(savedContext, false, false, 0);
		int speakerPhone = dataWrapper.getDatabaseHandler().getActiveProfileSpeakerphone();
		dataWrapper.invalidateDataWrapper();

		if (speakerPhone != 0)
		{
	        try {
	            Thread.sleep(500); // Delay 0,5 seconds to handle better turning on loudspeaker
	        } catch (InterruptedException e) {
	        }
		
	        //Activate loudspeaker
	        AudioManager audioManager = (AudioManager)savedContext.getSystemService(Context.AUDIO_SERVICE);
	        audioManager.setMode(AudioManager.MODE_IN_CALL);
	        audioManager.setSpeakerphoneOn(speakerPhone == 1);
		}
	}
	
	private void callEnded(boolean incoming)
	{
    	//Deactivate loudspeaker
        AudioManager audioManager = (AudioManager)savedContext.getSystemService(Context.AUDIO_SERVICE);
    	if (audioManager.isSpeakerphoneOn())
    	{
    	    audioManager.setSpeakerphoneOn(false);
    		audioManager.setMode(AudioManager.MODE_NORMAL); 
        }
	}
	
    protected void onIncomingCallStarted(String number, Date start) {
    }

    protected void onIncomingCallAnswered(String number, Date start) {
    	callAnswered(false);
    }

    protected void onOutgoingCallAnswered(String number, Date start) {
    	callAnswered(false);
    }

    protected void onIncomingCallEnded(String number, Date start, Date end) {
    	callEnded(true);
    }

    protected void onOutgoingCallEnded(String number, Date start, Date end) {
    	callEnded(false);
    }

    protected void onMissedCall(String number, Date start) {
    }
    
}
