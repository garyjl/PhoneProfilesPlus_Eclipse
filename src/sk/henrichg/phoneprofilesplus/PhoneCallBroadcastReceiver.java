package sk.henrichg.phoneprofilesplus;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class PhoneCallBroadcastReceiver extends PhoneCallReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "phoneCall";
	
	public static final int CALL_EVENT_UNDEFINED = 0; 
	public static final int CALL_EVENT_INCOMING_CALL_RINGING = 1; 
	public static final int CALL_EVENT_OUTGOING_CALL_STARTED = 2;
	public static final int CALL_EVENT_INCOMING_CALL_ANSWERED = 3; 
	public static final int CALL_EVENT_OUTGOING_CALL_ANSWERED = 4;
	public static final int CALL_EVENT_INCOMING_CALL_ENDED = 5; 
	public static final int CALL_EVENT_OUTGOING_CALL_ENDED = 6;
	
	protected boolean onStartReceive()
	{
		if (!GlobalData.getApplicationStarted(savedContext))
			return false;
		
		GlobalData.loadPreferences(savedContext);
		
		return true;
	}

	protected void onEndReceive()
	{
	}
	
	private void doCallEvent(int eventType, String phoneNumber, DataWrapper dataWrapper)
	{
		if (GlobalData.getGlobalEventsRuning(savedContext))
		{
			boolean callEventsExists = false;
			List<Event> eventList = dataWrapper.getEventList();
			for (Event event : eventList)
			{
				if (event._eventPreferencesCall._enabled && (event.getStatus() != Event.ESTATUS_STOP))
				{
					callEventsExists = true;
				}
			}
			
			if (callEventsExists)
			{
				// start service
				Intent eventsServiceIntent = new Intent(savedContext, EventsService.class);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_EVENT_ID, 0L);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_EVENT_CALL_EVENT_TYPE, eventType);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_EVENT_CALL_PHONE_NUMBER, phoneNumber);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
				startWakefulService(savedContext, eventsServiceIntent);
			}
			
		}
	}
	
	private void callStarted(boolean incoming, String phoneNumber)
	{
		DataWrapper dataWrapper = new DataWrapper(savedContext, false, false, 0);
		if (incoming)
			doCallEvent(CALL_EVENT_INCOMING_CALL_RINGING, phoneNumber, dataWrapper);
		else
			doCallEvent(CALL_EVENT_OUTGOING_CALL_STARTED, phoneNumber, dataWrapper);
		dataWrapper.invalidateDataWrapper();
	}
	
	private void callAnswered(boolean incoming, String phoneNumber)
	{
		DataWrapper dataWrapper = new DataWrapper(savedContext, false, false, 0);

		if (incoming)
			doCallEvent(CALL_EVENT_INCOMING_CALL_ANSWERED, phoneNumber, dataWrapper);
		else
			doCallEvent(CALL_EVENT_OUTGOING_CALL_ANSWERED, phoneNumber, dataWrapper);
		
		int speakerPhone = dataWrapper.getDatabaseHandler().getActiveProfileSpeakerphone();
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
		
		dataWrapper.invalidateDataWrapper();
	}
	
	private void callEnded(boolean incoming, String phoneNumber)
	{
    	//Deactivate loudspeaker
        AudioManager audioManager = (AudioManager)savedContext.getSystemService(Context.AUDIO_SERVICE);
    	if (audioManager.isSpeakerphoneOn())
    	{
    	    audioManager.setSpeakerphoneOn(false);
    		audioManager.setMode(AudioManager.MODE_NORMAL); 
        }
    	
		DataWrapper dataWrapper = new DataWrapper(savedContext, false, false, 0);
		if (incoming)
			doCallEvent(CALL_EVENT_INCOMING_CALL_ENDED, phoneNumber, dataWrapper);
		else
			doCallEvent(CALL_EVENT_OUTGOING_CALL_ENDED, phoneNumber, dataWrapper);
		dataWrapper.invalidateDataWrapper();
	}
	
    protected void onIncomingCallStarted(String number, Date start) 
    {
    	callStarted(true, number);
    }

    protected void onIncomingCallAnswered(String number, Date start)
    {
    	callAnswered(true, number);
    }

    protected void onOutgoingCallStarted(String number, Date start)
    {
    	callStarted(false, number);
    }

    protected void onIncomingCallEnded(String number, Date start, Date end)
    {
    	callEnded(true, number);
    }

    protected void onOutgoingCallEnded(String number, Date start, Date end)
    {
    	callEnded(false, number);
    }

    protected void onMissedCall(String number, Date start)
    {
    	callEnded(true, number);
    }
    
}
