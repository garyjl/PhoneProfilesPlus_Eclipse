package sk.henrichg.phoneprofilesplus;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class Event {
	
	public long _id;
	public String _name;
	public int _type;
	public long _fkProfile;
	private int _status;  
	public String _notificationSound;

	public EventPreferences _eventPreferences;
	public int _typeOld;
	public EventPreferences _eventPreferencesOld;
	
	public static final int ETYPE_TIME = 1;
	public static final int ETYPE_BATTERY = 2;
	
	public static final int ESTATUS_STOP = 0;
	public static final int ESTATUS_PAUSE = 1;
	public static final int ESTATUS_RUNNING = 2;
	
    static final String PREF_EVENT_ENABLED = "eventEnabled";
    static final String PREF_EVENT_NAME = "eventName";
    static final String PREF_EVENT_TYPE = "eventType";
    static final String PREF_EVENT_PROFILE = "eventProfile";
    static final String PREF_EVENT_NOTIFICATION_SOUND = "eventNotificationSound";
	
    static final String PREF_EVENT_ENABLED_TMP = "eventEnabledTmp";
    static final String PREF_EVENT_NAME_TMP = "eventNameTmp";
    static final String PREF_EVENT_PROFILE_TMP = "eventProfileTmp";
    static final String PREF_EVENT_NOTIFICATION_SOUND_TMP = "eventNotificationSoundTmp";
	

	// Empty constructorn
	public Event(){
		
	}
	
	// constructor
	public Event(long id, 
		         String name,
		         int type,
		         long fkProfile,
		         int status,
		         String notificationSound)
	{
		this._id = id;
		this._name = name;
        this._type = type;
        this._fkProfile = fkProfile;
        this._status = status;
        this._notificationSound = notificationSound;
        
        createEventPreferences();
	}
	
	// constructor
	public Event(String name,
	         	 int type,
	         	 long fkProfile,
	         	 int status,
	         	 String notificationSound)
	{
		this._name = name;
	    this._type = type;
	    this._fkProfile = fkProfile;
        this._status = status;
        this._notificationSound = notificationSound;
	    
	    createEventPreferences();
	}
	
	public void copyEvent(Event event)
	{
		this._id = event._id;
		this._name = event._name;
        this._type = event._type;
        this._fkProfile = event._fkProfile;
        this._status = event._status;
        this._notificationSound = event._notificationSound;
        
        copyEventPreferences(event);
	}
	
	public void createEventPreferences()
	{
		//Log.e("Event.createEventPreferences","type="+_type);
        switch (this._type)
        {
        case ETYPE_TIME:
        	this._eventPreferences = new EventPreferencesTime(this, false, false, false, false, false, false, false, 0, 0, false);
        	break;
        case ETYPE_BATTERY:
        	this._eventPreferences = new EventPreferencesBattery(this, 15, 0);
        	break;
        }
	}
	
	public void copyEventPreferences(Event fromEvent)
	{
		if (this._eventPreferences == null)
			createEventPreferences();
		this._eventPreferences.copyPreferences(fromEvent);
	}
	
	public void changeEventType(int type)
	{
		this._typeOld = this._type;
		this._eventPreferencesOld = this._eventPreferences;
		
		this._type = type;
		createEventPreferences();
	}
	
	public void undoEventType()
	{
		if (this._typeOld != 0)
		{
			this._type = this._typeOld;
			this._eventPreferences = this._eventPreferencesOld;
		}
		
		this._typeOld = 0;
		this._eventPreferencesOld = null;
	}
	
	public Event getOldEvent()
	{
		Event event = new Event();
		event.copyEvent(this);
		if (this._typeOld != 0)
		{
			event._eventPreferences = this._eventPreferencesOld;
			event._typeOld = 0;
			event._eventPreferencesOld = null;
		}
		
		return event;
	}
	
	public boolean isRunnable()
	{
		return  (this._fkProfile != 0) &&
				(this._eventPreferences != null) &&
				(this._eventPreferences.isRunable());
	}
	
	public void loadSharedPrefereces(SharedPreferences preferences)
	{
    	Editor editor = preferences.edit();
   		editor.putString(PREF_EVENT_NAME, this._name);
   		editor.putString(PREF_EVENT_TYPE, Integer.toString(this._type));
   		editor.putString(PREF_EVENT_PROFILE, Long.toString(this._fkProfile));
   		editor.putBoolean(PREF_EVENT_ENABLED, this._status != ESTATUS_STOP);
   		editor.putString(PREF_EVENT_NOTIFICATION_SOUND, this._notificationSound);
        this._eventPreferences.loadSharedPrefereces(preferences);
		editor.commit();
	}

	public void saveSharedPrefereces(SharedPreferences preferences)
	{
    	this._name = preferences.getString(PREF_EVENT_NAME, "");
		this._type = Integer.parseInt(preferences.getString(PREF_EVENT_TYPE, "0"));
		this._fkProfile = Long.parseLong(preferences.getString(PREF_EVENT_PROFILE, "0"));
		this._status = (preferences.getBoolean(PREF_EVENT_ENABLED, false)) ? ESTATUS_PAUSE : ESTATUS_STOP;
		this._notificationSound = preferences.getString(PREF_EVENT_NOTIFICATION_SOUND, ""); 
		Log.e("Event.saveSharedPrefereces","notificationSound="+this._notificationSound);
		this._eventPreferences.saveSharedPrefereces(preferences);
		
		if (!this.isRunnable())
			this._status = ESTATUS_STOP;
		
		this._typeOld = 0;
		this._eventPreferencesOld = null;
	}

	public void loadSharedPreferecesTmp(SharedPreferences preferences)
	{
		String eventName = preferences.getString(PREF_EVENT_NAME_TMP, "");
		String fkProfile = preferences.getString(PREF_EVENT_PROFILE_TMP, "0");
		boolean eventEnabled = preferences.getBoolean(PREF_EVENT_ENABLED_TMP, false);
		String notificationSound = preferences.getString(PREF_EVENT_NOTIFICATION_SOUND_TMP, "");
    	Editor editor = preferences.edit();
   		editor.putString(PREF_EVENT_NAME, eventName);
   		editor.putString(PREF_EVENT_PROFILE, fkProfile);
   		editor.putBoolean(PREF_EVENT_ENABLED, eventEnabled);
   		editor.putString(PREF_EVENT_NOTIFICATION_SOUND, notificationSound);
		editor.commit();
		this._name = eventName;
		this._fkProfile = Long.parseLong(fkProfile);
		this._status = (eventEnabled) ? ESTATUS_PAUSE : ESTATUS_STOP;
	}

	public void saveSharedPreferecesTmp(SharedPreferences preferences)
	{
		String eventName = preferences.getString(PREF_EVENT_NAME, "");
		String fkProfile = preferences.getString(PREF_EVENT_PROFILE, "0");
		boolean eventEnabled = preferences.getBoolean(PREF_EVENT_ENABLED, false);
		String notificationSound = preferences.getString(PREF_EVENT_NOTIFICATION_SOUND, "");
    	Editor editor = preferences.edit();
   		editor.putString(PREF_EVENT_NAME_TMP, eventName);
   		editor.putString(PREF_EVENT_PROFILE_TMP, fkProfile);
   		editor.putBoolean(PREF_EVENT_ENABLED_TMP, eventEnabled);
   		editor.putString(PREF_EVENT_NOTIFICATION_SOUND_TMP, notificationSound);
		editor.commit();
	}
	
	public void setSummary(PreferenceManager prefMng, String key, String value, Context context)
	{
		if (key.equals(PREF_EVENT_NAME))
		{	
	        prefMng.findPreference(key).setSummary(value);
		}
		if (key.equals(PREF_EVENT_TYPE))
		{	
			String sEventType = value;
			int iEventType;
			try {
				iEventType = Integer.parseInt(sEventType);
			} catch (Exception e) {
				iEventType = 1;
			}
			
	    	for (int pos = 0; pos < EventTypePreferenceAdapter.eventTypes.length; pos++)
	    	{
	    		if (iEventType == EventTypePreferenceAdapter.eventTypes[pos])
	    		{
	    	        prefMng.findPreference(key).setSummary(EventTypePreferenceAdapter.eventTypeNameIds[pos]);
	    		}
	    	}
		}
		if (key.equals(PREF_EVENT_PROFILE))
		{
			String sProfileId = value;
			long lProfileId;
			try {
				lProfileId = Long.parseLong(sProfileId);
			} catch (Exception e) {
				lProfileId = 0;
			}
			DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
		    Profile profile = dataWrapper.getProfileById(lProfileId);
		    if (profile != null)
		    {
    	        prefMng.findPreference(key).setSummary(profile._name);
		    }
		    else
		    {
    	        prefMng.findPreference(key).setSummary(context.getResources().getString(R.string.event_preferences_profile_not_set));
		    }
		}
		if (key.equals(PREF_EVENT_NOTIFICATION_SOUND))
		{
			String ringtoneUri = value.toString();
			if (ringtoneUri.isEmpty())
		        prefMng.findPreference(key).setSummary(R.string.event_preferences_notificationSound_None);
			else
			{
				Uri uri = Uri.parse(ringtoneUri);
				Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
				String ringtoneName;
				if (ringtone == null)
					ringtoneName = "";
				else
					ringtoneName = ringtone.getTitle(context);
				prefMng.findPreference(key).setSummary(ringtoneName);
			}
		}
	}

	public void setSummary(PreferenceManager prefMng, String key, SharedPreferences preferences, Context context)
	{
		if (key.equals(PREF_EVENT_NAME) ||
			key.equals(PREF_EVENT_TYPE) ||
			key.equals(PREF_EVENT_PROFILE) ||
			key.equals(PREF_EVENT_NOTIFICATION_SOUND))
			setSummary(prefMng, key, preferences.getString(key, ""), context);
		_eventPreferences.setSummary(prefMng, key, preferences, context);
	}
	
	public void setAllSummary(PreferenceManager prefMng, Context context)
	{
		setSummary(prefMng, PREF_EVENT_NAME, _name, context);
		setSummary(prefMng, PREF_EVENT_TYPE, Integer.toString(_type), context);
		setSummary(prefMng, PREF_EVENT_PROFILE, Long.toString(_fkProfile), context);
		setSummary(prefMng, PREF_EVENT_NOTIFICATION_SOUND, _notificationSound, context);
		_eventPreferences.setAllSummary(prefMng, context);
	}
	
	public String getPreferecesDescription(Context context)
	{
		String description;
		
		description = "";
		
		if (_eventPreferences != null)
			description = _eventPreferences.getPreferencesDescription(description, context);
		
		return description;
	}
	
	
	public void startEvent(DataWrapper dataWrapper,
							List<EventTimeline> eventTimelineList,
							boolean ignoreGlobalPref)
	{
		if ((!GlobalData.getGlobalEventsRuning(dataWrapper.context)) && (!ignoreGlobalPref))
			// events are globally stopped
			return;
		
		if (!this.isRunnable())
			// event is not runnable, no pause it
			return;
		
		GlobalData.logE("Event.startEvent","event_id="+this._id+"-----------------------------------");
		
		EventTimeline eventTimeline;		
		
	/////// delete duplicate from timeline
		
		// test whenever event exists in timeline
		boolean exists = true;
		while (exists)
		{
			exists = false;

			int timeLineSize = eventTimelineList.size();
			
			eventTimeline = null;
			int eventPosition = -1;
			for (EventTimeline _eventTimeline : eventTimelineList)
			{
				eventPosition++;
				if (_eventTimeline._fkEvent == this._id)
				{
					eventTimeline = _eventTimeline;
					exists = true;
					break;
				}
			}
			
			if (exists)
			{
				// remove event from timeline
				eventTimelineList.remove(eventTimeline);
				dataWrapper.getDatabaseHandler().deleteEventTimeline(eventTimeline);
				
				if ((eventPosition == 0) && (timeLineSize > 1))
				{
					// event is in start of timeline
					
					// move _fkProfileReturn up
					for (int i = eventTimelineList.size()-1; i > 0; i--)
					{
						eventTimelineList.get(i)._fkProfileReturn = 
								eventTimelineList.get(i-1)._fkProfileReturn;
					}
					eventTimelineList.get(0)._fkProfileReturn = eventTimeline._fkProfileReturn;
					dataWrapper.getDatabaseHandler().updateProfileReturnET(eventTimelineList);
				}
				else
				if (eventPosition == (timeLineSize-1))
				{
					// event is in end of timeline 
					// do nothing
				}
				else
				{
					// event is in middle of timeline 
					// do nothing
				}
			}
		}
	//////////////////////////////////

		eventTimeline = new EventTimeline();
		eventTimeline._fkEvent = this._id;
		eventTimeline._eorder = 0;
		Profile profile = dataWrapper.getActivatedProfile();
		if (profile != null)
			eventTimeline._fkProfileReturn = profile._id;
		else
			eventTimeline._fkProfileReturn = 0;
		
		dataWrapper.getDatabaseHandler().addEventTimeline(eventTimeline);
		eventTimelineList.add(eventTimeline);
		
		if (this._fkProfile != eventTimeline._fkProfileReturn)
			// no activate profile, when is already activated
			dataWrapper.activateProfileFromEvent(this._fkProfile, _notificationSound);

		setSystemEvent(dataWrapper.context, ESTATUS_RUNNING);
		
		this._status = ESTATUS_RUNNING;
		
		dataWrapper.getDatabaseHandler().updateEventStatus(this);
		
		return;
	}
	
	public void pauseEvent(DataWrapper dataWrapper, 
							List<EventTimeline> eventTimelineList,
							boolean activateReturnProfile, 
							boolean ignoreGlobalPref,
							boolean noSetSystemEvent)
	{
		if ((!GlobalData.getGlobalEventsRuning(dataWrapper.context)) && (!ignoreGlobalPref))
			// events are globally stopped
			return;

		if (!this.isRunnable())
			// event is not runnable, no pause it
			return;

		GlobalData.logE("Event.pauseEvent","event_id="+this._id+"-----------------------------------");
		
		int timeLineSize = eventTimelineList.size();
		
		// test whenever event exists in timeline
		boolean exists = false;
		int eventPosition = -1;
		for (EventTimeline eventTimeline : eventTimelineList)
		{
			eventPosition++;
			if (eventTimeline._fkEvent == this._id)
			{
				exists = true;
				break;
			}
		}
		
		if (exists)
		{
			EventTimeline eventTimeline = eventTimelineList.get(eventPosition);
			
			// remove event from timeline
			eventTimelineList.remove(eventTimeline);
			dataWrapper.getDatabaseHandler().deleteEventTimeline(eventTimeline);
	
			
			if ((eventPosition == 0) && (timeLineSize > 1))
			{
				// event is in start of timeline

				// move _fkProfileReturn up
				for (int i = eventTimelineList.size()-1; i > 0; i--)
				{
					eventTimelineList.get(i)._fkProfileReturn = 
							eventTimelineList.get(i-1)._fkProfileReturn;
				}
				eventTimelineList.get(0)._fkProfileReturn = eventTimeline._fkProfileReturn;
				dataWrapper.getDatabaseHandler().updateProfileReturnET(eventTimelineList);
			}
			else
			if (eventPosition == (timeLineSize-1))
			{
				// event is in end of timeline 
				
				// activate profile only when profile not already activated 
				if ((eventTimeline._fkProfileReturn != dataWrapper.getActivatedProfile()._id)
					&& (activateReturnProfile)
					&& (_eventPreferences.activateReturnProfile()))
				{
					GlobalData.logE("Event.pauseEvent","activate return profile");
					dataWrapper.activateProfileFromEvent(eventTimeline._fkProfileReturn, _notificationSound);
				}
			}
			else
			{
				// event is in middle of timeline 

				// do nothing
			}
		}

		if (!noSetSystemEvent)
			setSystemEvent(dataWrapper.context, ESTATUS_PAUSE);

		this._status = ESTATUS_PAUSE;
		
		dataWrapper.getDatabaseHandler().updateEventStatus(this);
		
		return;
	}
	
	public void stopEvent(DataWrapper dataWrapper,
							List<EventTimeline> eventTimelineList,
							boolean activateReturnProfile, 
							boolean ignoreGlobalPref,
							boolean saveEventStatus)
	{
		if ((!GlobalData.getGlobalEventsRuning(dataWrapper.context)) && (!ignoreGlobalPref))
			// events are globally stopped
			return;

		GlobalData.logE("Event.stopEvent","event_id="+this._id+"-----------------------------------");
		
		if (this._status == ESTATUS_RUNNING)
		{
			// event zrovna bezi, zapauzujeme ho
			pauseEvent(dataWrapper, eventTimelineList, activateReturnProfile, ignoreGlobalPref, true);
		}
	
		setSystemEvent(dataWrapper.context, ESTATUS_STOP);
		
		this._status = ESTATUS_STOP;
		
		if (saveEventStatus)
			dataWrapper.getDatabaseHandler().updateEventStatus(this);
		
		return;
	}
	
	public int getStatus()
	{
		return _status;
	}
	
	public int getStatusFromDB(DataWrapper dataWrapper)
	{
		return dataWrapper.getDatabaseHandler().getEventStatus(this);
	}
	
	public void setStatus(int status)
	{
		_status = status;
	}
	
	public void setSystemEvent(Context context, int forStatus)
	{
		if (forStatus == ESTATUS_PAUSE)
		{
			// event paused
			// setup system event for next running status
			_eventPreferences.setSystemRunningEvent(context);
		}
		else
		if (forStatus == ESTATUS_RUNNING)
		{
			// event started
			// setup system event for pause status
			_eventPreferences.setSystemPauseEvent(context);
		}
		else
		if (forStatus == ESTATUS_STOP)
		{
			// event stopped
			// remove all system events
			_eventPreferences.removeSystemEvent(context);
		}
	}
	
	public boolean invokeBroadcastReceiver(Context context)
	{
		boolean started = _eventPreferences.invokeBroadcastReceiver(context); 
		return started;
	}

}

