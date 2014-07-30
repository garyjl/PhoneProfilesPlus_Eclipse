package sk.henrichg.phoneprofilesplus;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.ListPreference;
import android.preference.PreferenceManager;

public class Event {
	
	public long _id;
	public String _name;
	public long _fkProfileStart;
	public long _fkProfileEnd;
	public boolean _undoneProfile;
	private int _status;  
	public String _notificationSound;
	public boolean _forceRun;
	public boolean _blocked;
	public int _priority;

	public EventPreferencesTime _eventPreferencesTime;
	public EventPreferencesBattery _eventPreferencesBattery;
	public EventPreferencesCall _eventPreferencesCall;
	public EventPreferencesPeripherals _eventPreferencesPeripherals;
	public EventPreferencesCalendar _eventPreferencesCalendar;
	public EventPreferencesWifi _eventPreferencesWifi;
	public EventPreferencesScreen _eventPreferencesScreen;

	public static final long PROFILE_END_NO_ACTIVATE = -999;
	
	public static final int ESTATUS_STOP = 0;
	public static final int ESTATUS_PAUSE = 1;
	public static final int ESTATUS_RUNNING = 2;
	public static final int ESTATUS_NONE = 99;

	public static final int EPRIORITY_LOWEST = -5; 
	public static final int EPRIORITY_VERY_LOW = -4;
	public static final int EPRIORITY_LOWER = -3;
	public static final int EPRIORITY_LOW = -1;
	public static final int EPRIORITY_LOWER_MEDIUM = -1;
	public static final int EPRIORITY_MEDIUM = 0;          
	public static final int EPRIORITY_UPPER_MEDIUM = 1;
	public static final int EPRIORITY_HIGH = 2;
	public static final int EPRIORITY_HIGHER = 3;
	public static final int EPRIORITY_VERY_HIGH = 4;
	public static final int EPRIORITY_HIGHEST = 5; 
	
    static final String PREF_EVENT_ENABLED = "eventEnabled";
    static final String PREF_EVENT_NAME = "eventName";
    static final String PREF_EVENT_PROFILE_START = "eventProfileStart";
    static final String PREF_EVENT_PROFILE_END = "eventProfileEnd";
    static final String PREF_EVENT_NOTIFICATION_SOUND = "eventNotificationSound";
    static final String PREF_EVENT_FORCE_RUN = "eventForceRun";
    static final String PREF_EVENT_UNDONE_PROFILE = "eventUndoneProfile";
    static final String PREF_EVENT_PRIORITY = "eventPriority";
	
	// Empty constructor
	public Event(){
		
	}
	
	// constructor
	public Event(long id, 
		         String name,
		         long fkProfileStart,
		         long fkProfileEnd,
		         int status,
		         String notificationSound,
		         boolean forceRun,
		         boolean blocked,
		         boolean undoneProfile,
		         int priority)
	{
		this._id = id;
		this._name = name;
        this._fkProfileStart = fkProfileStart;
        this._fkProfileEnd = fkProfileEnd;
        this._status = status;
        this._notificationSound = notificationSound;
        this._forceRun = forceRun;
        this._blocked = blocked;
        this._undoneProfile = undoneProfile;
        this._priority = priority;
        
        createEventPreferences();
	}
	
	// constructor
	public Event(String name,
	         	 long fkProfileStart,
	         	 long fkProfileEnd,
	         	 int status,
	         	 String notificationSound,
	         	 boolean forceRun,
	         	 boolean blocked,
	         	 boolean undoneProfile,
	         	 int priority)
	{
		this._name = name;
	    this._fkProfileStart = fkProfileStart;
	    this._fkProfileEnd = fkProfileEnd;
        this._status = status;
        this._notificationSound = notificationSound;
        this._forceRun = forceRun;
        this._blocked = blocked;
        this._undoneProfile = undoneProfile;
        this._priority = priority;
        
	    createEventPreferences();
	}
	
	public void copyEvent(Event event)
	{
		this._id = event._id;
		this._name = event._name;
        this._fkProfileStart = event._fkProfileStart;
        this._fkProfileEnd = event._fkProfileEnd;
        this._status = event._status;
        this._notificationSound = event._notificationSound;
        this._forceRun = event._forceRun;
        this._blocked = event._blocked;
        this._undoneProfile = event._undoneProfile;
        this._priority = event._priority;
        
        copyEventPreferences(event);
	}
	
	private void createEventPreferencesTime()
	{
       	this._eventPreferencesTime = new EventPreferencesTime(this, false, false, false, false, false, false, false, false, 0, 0, false);
	}
	
	private void createEventPreferencesBattery()
	{
       	this._eventPreferencesBattery = new EventPreferencesBattery(this, false, 0, 100, false);
	}
	
	private void createEventPreferencesCall()
	{
       	this._eventPreferencesCall = new EventPreferencesCall(this, false, 0, "", 0);
	}

	private void createEventPreferencesPeripherals()
	{
       	this._eventPreferencesPeripherals = new EventPreferencesPeripherals(this, false, 0);
	}

	private void createEventPreferencesCalendar()
	{
       	this._eventPreferencesCalendar = new EventPreferencesCalendar(this, false, "", 0, "");
	}

	private void createEventPreferencesWiFi()
	{
       	this._eventPreferencesWifi = new EventPreferencesWifi(this, false, "", 0);
	}

	private void createEventPreferencesScreen()
	{
       	this._eventPreferencesScreen = new EventPreferencesScreen(this, false, 0);
	}
	
	public void createEventPreferences()
	{
		//Log.e("Event.createEventPreferences","type="+_type);
		createEventPreferencesTime();
		createEventPreferencesBattery();
		createEventPreferencesCall();
		createEventPreferencesPeripherals();
		createEventPreferencesCalendar();
		createEventPreferencesWiFi();
		createEventPreferencesScreen();
	}
	
	public void copyEventPreferences(Event fromEvent)
	{
		if (this._eventPreferencesTime == null)
			createEventPreferencesTime();
		if (this._eventPreferencesBattery == null)
			createEventPreferencesBattery();
		if (this._eventPreferencesCall == null)
			createEventPreferencesCall();
		if (this._eventPreferencesPeripherals == null)
			createEventPreferencesPeripherals();
		if (this._eventPreferencesCalendar == null)
			createEventPreferencesCalendar();
		if (this._eventPreferencesWifi == null)
			createEventPreferencesWiFi();
		if (this._eventPreferencesScreen == null)
			createEventPreferencesScreen();
		this._eventPreferencesTime.copyPreferences(fromEvent);
		this._eventPreferencesBattery.copyPreferences(fromEvent);
		this._eventPreferencesCall.copyPreferences(fromEvent);
		this._eventPreferencesPeripherals.copyPreferences(fromEvent);
		this._eventPreferencesCalendar.copyPreferences(fromEvent);
		this._eventPreferencesWifi.copyPreferences(fromEvent);
		this._eventPreferencesScreen.copyPreferences(fromEvent);
	}
	
	public boolean isRunnable()
	{
		boolean runnable = (this._fkProfileStart != 0);
		if (!(this._eventPreferencesTime._enabled ||
			  this._eventPreferencesBattery._enabled ||
			  this._eventPreferencesCall._enabled ||
			  this._eventPreferencesPeripherals._enabled ||
			  this._eventPreferencesCalendar._enabled ||
			  this._eventPreferencesWifi._enabled ||
			  this._eventPreferencesScreen._enabled))
			runnable = false;
		if (this._eventPreferencesTime._enabled)
			runnable = runnable && this._eventPreferencesTime.isRunable();
		if (this._eventPreferencesBattery._enabled)
			runnable = runnable && this._eventPreferencesBattery.isRunable();
		if (this._eventPreferencesCall._enabled)
			runnable = runnable && this._eventPreferencesCall.isRunable();
		if (this._eventPreferencesPeripherals._enabled)
			runnable = runnable && this._eventPreferencesPeripherals.isRunable();
		if (this._eventPreferencesCalendar._enabled)
			runnable = runnable && this._eventPreferencesCalendar.isRunable();
		if (this._eventPreferencesWifi._enabled)
			runnable = runnable && this._eventPreferencesWifi.isRunable();
		if (this._eventPreferencesScreen._enabled)
			runnable = runnable && this._eventPreferencesScreen.isRunable();
		return runnable;
	}
	
	public void loadSharedPrefereces(SharedPreferences preferences)
	{
    	Editor editor = preferences.edit();
   		editor.putString(PREF_EVENT_NAME, this._name);
   		editor.putString(PREF_EVENT_PROFILE_START, Long.toString(this._fkProfileStart));
   		editor.putString(PREF_EVENT_PROFILE_END, Long.toString(this._fkProfileEnd));
   		editor.putBoolean(PREF_EVENT_ENABLED, this._status != ESTATUS_STOP);
   		editor.putString(PREF_EVENT_NOTIFICATION_SOUND, this._notificationSound);
   		editor.putBoolean(PREF_EVENT_FORCE_RUN, this._forceRun);
   		editor.putBoolean(PREF_EVENT_UNDONE_PROFILE, this._undoneProfile);
   		editor.putString(PREF_EVENT_PRIORITY, Integer.toString(this._priority));
        this._eventPreferencesTime.loadSharedPrefereces(preferences);
        this._eventPreferencesBattery.loadSharedPrefereces(preferences);
        this._eventPreferencesCall.loadSharedPrefereces(preferences);
        this._eventPreferencesPeripherals.loadSharedPrefereces(preferences);
        this._eventPreferencesCalendar.loadSharedPrefereces(preferences);
        this._eventPreferencesWifi.loadSharedPrefereces(preferences);
        this._eventPreferencesScreen.loadSharedPrefereces(preferences);
		editor.commit();
	}

	public void saveSharedPrefereces(SharedPreferences preferences)
	{
    	this._name = preferences.getString(PREF_EVENT_NAME, "");
		this._fkProfileStart = Long.parseLong(preferences.getString(PREF_EVENT_PROFILE_START, "0"));
		this._fkProfileEnd = Long.parseLong(preferences.getString(PREF_EVENT_PROFILE_END, Long.toString(PROFILE_END_NO_ACTIVATE)));
		this._status = (preferences.getBoolean(PREF_EVENT_ENABLED, false)) ? ESTATUS_PAUSE : ESTATUS_STOP;
		this._notificationSound = preferences.getString(PREF_EVENT_NOTIFICATION_SOUND, "");
		this._forceRun = preferences.getBoolean(PREF_EVENT_FORCE_RUN, false);
		this._undoneProfile = preferences.getBoolean(PREF_EVENT_UNDONE_PROFILE, true);
		this._priority = Integer.parseInt(preferences.getString(PREF_EVENT_PRIORITY, Integer.toString(EPRIORITY_MEDIUM)));
		//Log.e("Event.saveSharedPrefereces","notificationSound="+this._notificationSound);
		this._eventPreferencesTime.saveSharedPrefereces(preferences);
		this._eventPreferencesBattery.saveSharedPrefereces(preferences);
		this._eventPreferencesCall.saveSharedPrefereces(preferences);
		this._eventPreferencesPeripherals.saveSharedPrefereces(preferences);
		this._eventPreferencesCalendar.saveSharedPrefereces(preferences);
		this._eventPreferencesWifi.saveSharedPrefereces(preferences);
		this._eventPreferencesScreen.saveSharedPrefereces(preferences);
		
		if (!this.isRunnable())
			this._status = ESTATUS_STOP;
	}

	public void setSummary(PreferenceManager prefMng, String key, String value, Context context)
	{
		if (key.equals(PREF_EVENT_NAME))
		{	
	        prefMng.findPreference(key).setSummary(value);
		}
		if (key.equals(PREF_EVENT_PROFILE_START)||key.equals(PREF_EVENT_PROFILE_END))
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
		    	if (lProfileId == PROFILE_END_NO_ACTIVATE)
		    		prefMng.findPreference(key).setSummary(context.getResources().getString(R.string.event_preferences_profile_end_no_activate));
		    	else
		    		prefMng.findPreference(key).setSummary(context.getResources().getString(R.string.event_preferences_profile_not_set));
		    }
		}
		if (key.equals(PREF_EVENT_NOTIFICATION_SOUND))
		{
			String ringtoneUri = value.toString();
			if (ringtoneUri.isEmpty())
		        prefMng.findPreference(key).setSummary(R.string.preferences_notificationSound_None);
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
		if (key.equals(PREF_EVENT_PRIORITY))
		{
			ListPreference listPreference = (ListPreference)prefMng.findPreference(key);
			int index = listPreference.findIndexOfValue(value);
			CharSequence summary = (index >= 0) ? listPreference.getEntries()[index] : null;
			listPreference.setSummary(summary);
		}
	}

	public void setSummary(PreferenceManager prefMng, String key, SharedPreferences preferences, Context context)
	{
		if (key.equals(PREF_EVENT_NAME) ||
			key.equals(PREF_EVENT_PROFILE_START) ||
			key.equals(PREF_EVENT_PROFILE_END) ||
			key.equals(PREF_EVENT_NOTIFICATION_SOUND) ||
			key.equals(PREF_EVENT_PRIORITY))
			setSummary(prefMng, key, preferences.getString(key, ""), context);
		_eventPreferencesTime.setSummary(prefMng, key, preferences, context);
		_eventPreferencesBattery.setSummary(prefMng, key, preferences, context);
		_eventPreferencesCall.setSummary(prefMng, key, preferences, context);
		_eventPreferencesPeripherals.setSummary(prefMng, key, preferences, context);
		_eventPreferencesCalendar.setSummary(prefMng, key, preferences, context);
		_eventPreferencesWifi.setSummary(prefMng, key, preferences, context);
		_eventPreferencesScreen.setSummary(prefMng, key, preferences, context);
	}
	
	public void setAllSummary(PreferenceManager prefMng, Context context)
	{
		setSummary(prefMng, PREF_EVENT_NAME, _name, context);
		setSummary(prefMng, PREF_EVENT_PROFILE_START, Long.toString(this._fkProfileStart), context);
		setSummary(prefMng, PREF_EVENT_PROFILE_END, Long.toString(this._fkProfileEnd), context);
		setSummary(prefMng, PREF_EVENT_NOTIFICATION_SOUND, this._notificationSound, context);
		setSummary(prefMng, PREF_EVENT_PRIORITY, Integer.toString(this._priority), context);
		_eventPreferencesTime.setAllSummary(prefMng, context);
		_eventPreferencesBattery.setAllSummary(prefMng, context);
		_eventPreferencesCall.setAllSummary(prefMng, context);
		_eventPreferencesPeripherals.setAllSummary(prefMng, context);
		_eventPreferencesCalendar.setAllSummary(prefMng, context);
		_eventPreferencesWifi.setAllSummary(prefMng, context);
		_eventPreferencesScreen.setAllSummary(prefMng, context);
	}
	
	public String getPreferecesDescription(Context context)
	{
		String description;
		
		description = "";
		
		description = _eventPreferencesTime.getPreferencesDescription(description, context);
		description = description + "\n";
		description = _eventPreferencesCalendar.getPreferencesDescription(description, context);
		description = description + "\n";
		description = _eventPreferencesBattery.getPreferencesDescription(description, context);
		description = description + "\n";
		description = _eventPreferencesCall.getPreferencesDescription(description, context);
		description = description + "\n";
		description = _eventPreferencesWifi.getPreferencesDescription(description, context);
		description = description + "\n";
		description = _eventPreferencesPeripherals.getPreferencesDescription(description, context);
		description = description + "\n";
		description = _eventPreferencesScreen.getPreferencesDescription(description, context);
		
		return description;
	}
	
	private boolean canActivateReturnProfile()
	{
		boolean canActivate = false;
		
		if (this._eventPreferencesTime._enabled)
			canActivate = canActivate || this._eventPreferencesTime.activateReturnProfile();
		if (this._eventPreferencesBattery._enabled)
			canActivate = canActivate || this._eventPreferencesBattery.activateReturnProfile();
		if (this._eventPreferencesCall._enabled)
			canActivate = canActivate || this._eventPreferencesCall.activateReturnProfile();
		if (this._eventPreferencesPeripherals._enabled)
			canActivate = canActivate || this._eventPreferencesPeripherals.activateReturnProfile();
		if (this._eventPreferencesCalendar._enabled)
			canActivate = canActivate || this._eventPreferencesCalendar.activateReturnProfile();
		if (this._eventPreferencesWifi._enabled)
			canActivate = canActivate || this._eventPreferencesWifi.activateReturnProfile();
		if (this._eventPreferencesScreen._enabled)
			canActivate = canActivate || this._eventPreferencesScreen.activateReturnProfile();
		
		return canActivate;
	}
	
	private int getEventTimelinePosition(List<EventTimeline> eventTimelineList)
	{
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
			return eventPosition;
		else
			return -1;
	}
	
	private EventTimeline addEventTimeline(DataWrapper dataWrapper, 
											List<EventTimeline> eventTimelineList)
	{
		EventTimeline eventTimeline = new EventTimeline();
		eventTimeline._fkEvent = this._id;
		eventTimeline._eorder = 0;

		Profile profile = null;
		if (eventTimelineList.size() == 0)
		{
			profile = dataWrapper.getActivatedProfile();
			if (profile != null)
				eventTimeline._fkProfileEndActivated = profile._id;
			else
				eventTimeline._fkProfileEndActivated = 0;
		}
		else
		{
			eventTimeline._fkProfileEndActivated = 0;
			EventTimeline _eventTimeline = eventTimelineList.get(eventTimelineList.size()-1);
			if (_eventTimeline != null)
			{
				Event event = dataWrapper.getEventById(_eventTimeline._fkEvent);
				if (event != null)
					eventTimeline._fkProfileEndActivated = event._fkProfileStart;
			}
		}

		dataWrapper.getDatabaseHandler().addEventTimeline(eventTimeline);
		eventTimelineList.add(eventTimeline);
		
		return eventTimeline;
	}
	
	public void startEvent(DataWrapper dataWrapper,
							List<EventTimeline> eventTimelineList, 
							boolean ignoreGlobalPref,
							boolean interactive)
	{
		if ((!GlobalData.getGlobalEventsRuning(dataWrapper.context)) && (!ignoreGlobalPref))
			// events are globally stopped
			return;
		
		if (!this.isRunnable())
			// event is not runnable, no pause it
			return;

		if (GlobalData.getEventsBlocked(dataWrapper.context))
		{
			// blocked by manual profile activation
			GlobalData.logE("Event.startEvent","event_id="+this._id+" events blocked");

			
			if (!_forceRun)
				// event is not forceRun
				return;
			if (_blocked)
				// forceRun event is temporary blocked
				return;
		}
		
		// search for runing event with higher priority
		for (EventTimeline eventTimeline : eventTimelineList)
		{
			Event event = dataWrapper.getEventById(eventTimeline._fkEvent);
			if ((event != null) && (event._priority > this._priority))
				// is running event with higher priority
				return;
		}
		
		if (_forceRun)
			GlobalData.setForceRunEventRunning(dataWrapper.context, true);
		
		GlobalData.logE("Event.startEvent","event_id="+this._id+"-----------------------------------");
		
		EventTimeline eventTimeline;		
		
		//if (getStatus() != ESTATUS_RUNNING)
		//{
	/////// delete duplicate from timeline
			boolean exists = true;
			while (exists)
			{
				exists = false;
	
				int timeLineSize = eventTimelineList.size();
				
				// test whenever event exists in timeline
				eventTimeline = null;
				int eventPosition = getEventTimelinePosition(eventTimelineList);
				GlobalData.logE("Event.startEvent","eventPosition="+eventPosition);
				if (eventPosition != -1)
					eventTimeline = eventTimelineList.get(eventPosition);
				
				exists = eventPosition != -1;
				
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
							eventTimelineList.get(i)._fkProfileEndActivated = 
									eventTimelineList.get(i-1)._fkProfileEndActivated;
						}
						eventTimelineList.get(0)._fkProfileEndActivated = eventTimeline._fkProfileEndActivated;
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

			eventTimeline = addEventTimeline(dataWrapper, eventTimelineList);

			long activatedProfileId = 0;
			Profile activatedProfile = dataWrapper.getActivatedProfile();
			if (activatedProfile != null)
				activatedProfileId = activatedProfile._id;
			
			if (this._fkProfileStart != activatedProfileId)
			{
				// no activate profile, when is already activated
				GlobalData.logE("Event.startEvent","event_id="+this._id+" activate profile id="+this._fkProfileStart);
				
				if (interactive)
					dataWrapper.activateProfileFromEvent(this._fkProfileStart, interactive, _notificationSound);
				else
					dataWrapper.activateProfileFromEvent(this._fkProfileStart, interactive, "");
			}
			else
			{
				ActivateProfileHelper activateProfileHelper = dataWrapper.getActivateProfileHelper();
				activateProfileHelper.initialize(dataWrapper, null, dataWrapper.context);
				activateProfileHelper.showNotification(dataWrapper.getActivatedProfile());
				activateProfileHelper.updateWidget();
			}
			
		//}

		setSystemEvent(dataWrapper.context, ESTATUS_RUNNING);
		
		this._status = ESTATUS_RUNNING;
		
		dataWrapper.getDatabaseHandler().updateEventStatus(this);
		
		return;
	}
	
	private void doActivateEndProfile(DataWrapper dataWrapper,
            							int eventPosition,
            							int timeLineSize,
										List<EventTimeline> eventTimelineList,
										EventTimeline eventTimeline,
										boolean activateReturnProfile)
	{
		
		// check whether events behind are set _fkProfileEnd or _undoProfile
		// when true, no activate "end profile"
		if ((eventPosition < (timeLineSize-1)) && (timeLineSize > 1))
		{	
			/*for (int i = eventPosition; i < (timeLineSize-1); i++)
			{
				if (_fkProfileEnd != Event.PROFILE_END_NO_ACTIVATE)
					return;
				if (_undoneProfile)
					return;
			}*/
			return;
		}
		
		// activate profile only when profile not already activated
		if (activateReturnProfile && canActivateReturnProfile())
		{
			Profile profile = dataWrapper.getActivatedProfile();
			long activatedProfileId = 0;
			if (profile != null)
				activatedProfileId = profile._id;
			// first activate _fkProfileEnd
			if (_fkProfileEnd != Event.PROFILE_END_NO_ACTIVATE)
			{
				if (_fkProfileEnd != activatedProfileId)
				{
					GlobalData.logE("Event.pauseEvent","activate end porfile");
					dataWrapper.activateProfileFromEvent(_fkProfileEnd, false, "");
					activatedProfileId = _fkProfileEnd;
				}
			}
			// second activate when undoneProfile is set
			if (_undoneProfile)
			{
				if (eventTimeline._fkProfileEndActivated != activatedProfileId)
				{
					GlobalData.logE("Event.pauseEvent","undone profile");
					GlobalData.logE("Event.pauseEvent","_fkProfileEndActivated="+eventTimeline._fkProfileEndActivated);
					if (eventTimeline._fkProfileEndActivated != 0)
						dataWrapper.activateProfileFromEvent(eventTimeline._fkProfileEndActivated, false, "");
				}
			}
		}
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

		// unblock temporary paused event
		dataWrapper.setEventBlocked(this, false);
		
		GlobalData.logE("Event.pauseEvent","event_id="+this._id+"-----------------------------------");
		
		int timeLineSize = eventTimelineList.size();
		
		// test whenever event exists in timeline
		boolean exists = false;
		int eventPosition = getEventTimelinePosition(eventTimelineList);
		GlobalData.logE("Event.pauseEvent","eventPosition="+eventPosition);

		exists = eventPosition != -1;
		
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
					eventTimelineList.get(i)._fkProfileEndActivated = 
							eventTimelineList.get(i-1)._fkProfileEndActivated;
				}
				eventTimelineList.get(0)._fkProfileEndActivated = eventTimeline._fkProfileEndActivated;
				dataWrapper.getDatabaseHandler().updateProfileReturnET(eventTimelineList);

				doActivateEndProfile(dataWrapper, eventPosition, timeLineSize, 
									eventTimelineList, eventTimeline, activateReturnProfile);				
			}
			else
			if (eventPosition == (timeLineSize-1))
			{
				// event is in end of timeline 
				
				doActivateEndProfile(dataWrapper, eventPosition, timeLineSize, 
									eventTimelineList, eventTimeline, activateReturnProfile);				
			}
			else
			{
				// event is in middle of timeline 
				doActivateEndProfile(dataWrapper, eventPosition, timeLineSize, 
									eventTimelineList, eventTimeline, activateReturnProfile);				
			}
		}

		if (!noSetSystemEvent)
			setSystemEvent(dataWrapper.context, ESTATUS_PAUSE);

		this._status = ESTATUS_PAUSE;
		
		dataWrapper.getDatabaseHandler().updateEventStatus(this);

		if (_forceRun)
		{
			boolean forceRunRunning = false;
			for (EventTimeline eventTimeline : eventTimelineList)
			{
				Event event = dataWrapper.getEventById(eventTimeline._fkEvent);
				if ((event != null) && (event._forceRun))
				{
					forceRunRunning = true;
					break;
				}
			}
				
			if (!forceRunRunning)
				GlobalData.setForceRunEventRunning(dataWrapper.context, false);
		}
		
		
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
			_eventPreferencesTime.setSystemRunningEvent(context);
			_eventPreferencesBattery.setSystemRunningEvent(context);
			_eventPreferencesCall.setSystemRunningEvent(context);
			_eventPreferencesPeripherals.setSystemRunningEvent(context);
			_eventPreferencesCalendar.setSystemRunningEvent(context);
			_eventPreferencesWifi.setSystemRunningEvent(context);
			_eventPreferencesScreen.setSystemRunningEvent(context);
		}
		else
		if (forStatus == ESTATUS_RUNNING)
		{
			// event started
			// setup system event for pause status
			_eventPreferencesTime.setSystemPauseEvent(context);
			_eventPreferencesBattery.setSystemPauseEvent(context);
			_eventPreferencesCall.setSystemPauseEvent(context);
			_eventPreferencesPeripherals.setSystemPauseEvent(context);
			_eventPreferencesCalendar.setSystemPauseEvent(context);
			_eventPreferencesWifi.setSystemPauseEvent(context);
			_eventPreferencesScreen.setSystemPauseEvent(context);
		}
		else
		if (forStatus == ESTATUS_STOP)
		{
			// event stopped
			// remove all system events
			_eventPreferencesTime.removeSystemEvent(context);
			_eventPreferencesBattery.removeSystemEvent(context);
			_eventPreferencesCall.removeSystemEvent(context);
			_eventPreferencesPeripherals.removeSystemEvent(context);
			_eventPreferencesCalendar.removeSystemEvent(context);
			_eventPreferencesWifi.removeSystemEvent(context);
			_eventPreferencesScreen.removeSystemEvent(context);
		}
	}
	
}

