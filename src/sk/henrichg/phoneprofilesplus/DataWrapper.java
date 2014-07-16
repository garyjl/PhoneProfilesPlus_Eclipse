package sk.henrichg.phoneprofilesplus;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.text.format.DateFormat;
import android.widget.Toast;

public class DataWrapper {

	public Context context = null;
	private boolean forGUI = false;
	private boolean monochrome = false;
	private int monochromeValue = 0xFF;
	private Handler toastHandler;

	private DatabaseHandler databaseHandler = null;
	private ActivateProfileHelper activateProfileHelper = null;
	private List<Profile> profileList = null;
	private List<Event> eventList = null;
	
	DataWrapper(Context c, 
						boolean fgui, 
						boolean mono, 
						int monoVal)
	{
		context = c;
		
		setParameters(fgui, mono, monoVal); 
		
		databaseHandler = getDatabaseHandler();
		//activateProfileHelper = getActivateProfileHelper();
	}
	
	public void setParameters( 
			boolean fgui, 
			boolean mono, 
			int monoVal)
	{
		forGUI = fgui;
		monochrome = mono;
		monochromeValue = monoVal; 
	}
	
	public void setToastHandler(Handler handler)
	{
		toastHandler = handler;
	}
	
	public DatabaseHandler getDatabaseHandler()
	{
		if (databaseHandler == null)
			// parameter must by application context
			databaseHandler = DatabaseHandler.getInstance(context);
			
		return databaseHandler;
	}

	public ActivateProfileHelper getActivateProfileHelper()
	{
		if (activateProfileHelper == null)
			activateProfileHelper = new ActivateProfileHelper(); 

		return activateProfileHelper;
	}
	
	public List<Profile> getProfileList()
	{
		if (profileList == null)
		{
			profileList = getDatabaseHandler().getAllProfiles();
		
			if (forGUI)
			{
				for (Profile profile : profileList)
				{
					profile.generateIconBitmap(context, monochrome, monochromeValue);
					//if (generateIndicators)
						profile.generatePreferencesIndicator(context, monochrome, monochromeValue);
				}
			}
		}

		return profileList;
	}
	
	public void setProfileList(List<Profile> profileList, boolean recycleBitmaps)
	{
		if (recycleBitmaps)
			invalidateProfileList();
		else
			if (this.profileList != null)
				this.profileList.clear();
		this.profileList = profileList;
	}
	
	public Profile getNoinitializedProfile(String name, String icon, int order)
	{
		return new Profile(
				  name, 
				  icon + "|1", 
				  false, 
				  order,
				  0,
	         	  "-1|1|0",
	         	  "-1|1|0",
	         	  "-1|1|0",
	         	  "-1|1|0",
	         	  "-1|1|0",
	         	  "-1|1|0",
	         	  0,
	         	  Settings.System.DEFAULT_RINGTONE_URI.toString(),
	         	  0,
	         	  Settings.System.DEFAULT_NOTIFICATION_URI.toString(),
	         	  0,
	         	  Settings.System.DEFAULT_ALARM_ALERT_URI.toString(),
	         	  0,
	         	  0,
	         	  0,
	         	  0,
	         	  "-1|1|1|0",
	         	  0,
				  "-|0",
				  0,
				  0,
				  0,
				  0,
				  "-",
				  0,
				  false,
				  0,
				  0,
				  0,
				  0
			);
	}
	
	private String getVolumeLevelString(int percentage, int maxValue)
	{
		Double dValue = maxValue / 100.0 * percentage;
		return String.valueOf(dValue.intValue());
	}
	
	public List<Profile>  getDefaultProfileList()
	{
		invalidateProfileList();
		getDatabaseHandler().deleteAllProfiles();

		AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		int	maximumValueRing = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
		int	maximumValueNotification = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
		int	maximumValueMusic = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int	maximumValueAlarm = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
		//int	maximumValueSystem = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		//int	maximumValueVoicecall = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
		
		
		Profile profile;
		
		profile = getNoinitializedProfile(context.getString(R.string.default_profile_name_home), "ic_profile_home_2", 1);
		profile._showInActivator = true;
		profile._volumeRingerMode = 1;
		profile._volumeRingtone = getVolumeLevelString(71, maximumValueRing)+"|0|0";
		profile._volumeNotification = getVolumeLevelString(86, maximumValueNotification)+"|0|0";
		profile._volumeAlarm = getVolumeLevelString(100, maximumValueAlarm)+"|0|0";
		profile._volumeMedia = getVolumeLevelString(80, maximumValueMusic)+"|0|0";
		profile._deviceWiFi = 1;
		//profile._deviceBrightness = "60|0|0|0";
		getDatabaseHandler().addProfile(profile);
		profile = getNoinitializedProfile(context.getString(R.string.default_profile_name_outdoor), "ic_profile_outdoors_1", 2);
		profile._showInActivator = true;
		profile._volumeRingerMode = 2;
		profile._volumeRingtone = getVolumeLevelString(100, maximumValueRing)+"|0|0";
		profile._volumeNotification = getVolumeLevelString(100, maximumValueNotification)+"|0|0";
		profile._volumeAlarm = getVolumeLevelString(100, maximumValueAlarm)+"|0|0";
		profile._volumeMedia = getVolumeLevelString(93, maximumValueMusic)+"|0|0";
		profile._deviceWiFi = 2;
		//profile._deviceBrightness = "255|0|0|0";
		getDatabaseHandler().addProfile(profile);
		profile = getNoinitializedProfile(context.getString(R.string.default_profile_name_work), "ic_profile_work_5", 3);
		profile._showInActivator = true;
		profile._volumeRingerMode = 1;
		profile._volumeRingtone = getVolumeLevelString(57, maximumValueRing)+"|0|0"; 
		profile._volumeNotification = getVolumeLevelString(71, maximumValueNotification)+"|0|0";
		profile._volumeAlarm = getVolumeLevelString(57, maximumValueAlarm)+"|0|0";
		profile._volumeMedia = getVolumeLevelString(80, maximumValueMusic)+"|0|0";
		profile._deviceWiFi = 2;
		//profile._deviceBrightness = "60|0|0|0";
		getDatabaseHandler().addProfile(profile);
		profile = getNoinitializedProfile(context.getString(R.string.default_profile_name_meeting), "ic_profile_meeting_2", 4);
		profile._showInActivator = true;
		profile._volumeRingerMode = 4;
		profile._volumeRingtone = getVolumeLevelString(0, maximumValueRing)+"|0|0";
		profile._volumeNotification = getVolumeLevelString(0, maximumValueNotification)+"|0|0";
		profile._volumeAlarm = getVolumeLevelString(0, maximumValueAlarm)+"|0|0";
		profile._volumeMedia = getVolumeLevelString(0, maximumValueMusic)+"|0|0";
		profile._deviceWiFi = 0;
		//profile._deviceBrightness = "-1|1|1|0";
		getDatabaseHandler().addProfile(profile);
		profile = getNoinitializedProfile(context.getString(R.string.default_profile_name_sleep), "ic_profile_sleep", 5);
		profile._showInActivator = true;
		profile._volumeRingerMode = 4;
		profile._volumeRingtone = getVolumeLevelString(0, maximumValueRing)+"|0|0";
		profile._volumeNotification = getVolumeLevelString(0, maximumValueNotification)+"|0|0";
		profile._volumeAlarm = getVolumeLevelString(100, maximumValueAlarm)+"|0|0";
		profile._volumeMedia = getVolumeLevelString(0, maximumValueMusic)+"|0|0";
		profile._deviceWiFi = 0;
		//profile._deviceBrightness = "10|0|0|0";
		getDatabaseHandler().addProfile(profile);
		profile = getNoinitializedProfile(context.getString(R.string.default_profile_name_battery_low), "ic_profile_battery_1", 6);
		profile._showInActivator = false;
		profile._deviceAutosync = 2;
		profile._deviceMobileData = 2;
		profile._deviceWiFi = 2;
		profile._deviceBluetooth = 2;
		profile._deviceGPS = 2;
		getDatabaseHandler().addProfile(profile);
		
		return getProfileList();
	}
	
	public void invalidateProfileList()
	{
		if (profileList != null)
		{
			for (Profile profile : profileList)
			{
				profile.releaseIconBitmap();
				profile.releasePreferencesIndicator();
			}
			profileList.clear();
		}
		profileList = null;
	}
	
	private Profile getActivatedProfileFromDB()
	{
		Profile profile = getDatabaseHandler().getActivatedProfile();
		if (forGUI && (profile != null))
		{
			//Log.d("ProfilesDataWrapper.getActivatedProfile","forGUI=true");
			profile.generateIconBitmap(context, monochrome, monochromeValue);
			profile.generatePreferencesIndicator(context, monochrome, monochromeValue);
		}
		return profile;
	}
	
	public Profile getActivatedProfile()
	{
		if (profileList == null)
		{
			//Log.d("ProfilesDataWrapper.getActivatedProfile","profileList=null");
			return getActivatedProfileFromDB();
		}
		else
		{
			//Log.d("ProfilesDataWrapper.getActivatedProfile","profileList!=null");
			Profile profile;
			for (int i = 0; i < profileList.size(); i++)
			{
				profile = profileList.get(i); 
				if (profile._checked)
					return profile;
			}
			// when filter is set and profile not found, get profile from db
			return getActivatedProfileFromDB();
		}
	}
/*	
	public Profile getFirstProfile()
	{
		if (profileList == null)
		{
			Profile profile = getDatabaseHandler().getFirstProfile();
			if (forGUI && (profile != null))
			{
				profile.generateIconBitmap(context, monochrome, monochromeValue);
				profile.generatePreferencesIndicator(context, monochrome, monochromeValue);
			}
			return profile;
		}
		else
		{
			Profile profile;
			if (profileList.size() > 0)
				profile = profileList.get(0);
			else
				profile = null;
			
			return profile;
		}
	}
*/	
/*	
	public int getProfileItemPosition(Profile profile)
	{
		if (profile == null)
			return -1;
		
		if (profileList == null)
			return getDatabaseHandler().getProfilePosition(profile);
		else
		{
			for (int i = 0; i < profileList.size(); i++)
			{
				if (profileList.get(i)._id == profile._id)
					return i;
			}
			return -1;
		}
	}
*/	
	public void setProfileActive(Profile profile)
	{
		if ((profileList == null) || (profile == null))
			return;
		
		for (Profile p : profileList)
		{
			p._checked = false;
		}
		
		profile._checked = true;
		
	/*	// teraz musime najst profile v profileList 
		int position = getProfileItemPosition(profile);
		if (position != -1)
		{
			// najdenemu objektu nastavime _checked
			Profile _profile = profileList.get(position);
			if (_profile != null)
				_profile._checked = true;
		} */
	}
	
	public void activateProfileFromEvent(long profile_id, boolean interactive, String eventNotificationSound)
	{
		//Log.d("PhoneProfilesService.activateProfile",profile_id+"");
		/*
		Intent intent = new Intent(context, BackgroundActivateProfileActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	intent.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_SERVICE);
		intent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile_id);
		intent.putExtra(GlobalData.EXTRA_EVENT_NOTIFICATION_SOUND, eventNotificationSound);
	    context.startActivity(intent);
	    */
		getActivateProfileHelper().initialize(this, null, context);
		_activateProfile(getProfileById(profile_id), GlobalData.STARTUP_SOURCE_SERVICE, interactive, null, eventNotificationSound);
	}
	
	
	public void deactivateProfile()
	{
		if (profileList == null)
			return;
		
		for (Profile p : profileList)
		{
			p._checked = false;
		}
	}
	
	private Profile getProfileByIdFromDB(long id)
	{
		Profile profile = getDatabaseHandler().getProfile(id);
		if (forGUI && (profile != null))
		{
			profile.generateIconBitmap(context, monochrome, monochromeValue);
			profile.generatePreferencesIndicator(context, monochrome, monochromeValue);
		}
		return profile;
	}
	
	public Profile getProfileById(long id)
	{
		if (profileList == null)
		{
			return getProfileByIdFromDB(id);
		}
		else
		{
			Profile profile;
			for (int i = 0; i < profileList.size(); i++)
			{
				profile = profileList.get(i); 
				if (profile._id == id)
					return profile;
			}
			
			// when filter is set and profile not found, get profile from db
			return getProfileByIdFromDB(id);
		}
	}
	
	public void updateProfile(Profile profile)
	{
		if (profile != null)
		{
			Profile origProfile = getProfileById(profile._id);
			if (origProfile != null)
				origProfile.copyProfile(profile);
		}
	}
	
	public void reloadProfilesData()
	{
		invalidateProfileList();
		getProfileList();
	}
	
	public void deleteProfile(Profile profile)
	{
		if (profile == null)
			return;
		
		profileList.remove(profile);
		if (eventList == null)
			eventList = getEventList();
		// unlink profile from events
		for (Event event : eventList)
		{
			if (event._fkProfileStart == profile._id) 
				event._fkProfileStart = 0;
			if (event._fkProfileEnd == profile._id) 
				event._fkProfileEnd = Event.PROFILE_END_NO_ACTIVATE;
		}
	}
	
	public void deleteAllProfiles()
	{
		profileList.clear();
		if (eventList == null)
			eventList = getEventList();
		// unlink profiles from events
		for (Event event : eventList)
		{
			event._fkProfileStart = 0;
			event._fkProfileEnd = Event.PROFILE_END_NO_ACTIVATE;
		}
	}
	
//---------------------------------------------------

	public List<Event> getEventList()
	{
		if (eventList == null)
		{
			eventList = getDatabaseHandler().getAllEvents();
		}

		return eventList;
	}
	
	public void setEventList(List<Event> eventList)
	{
		if (this.eventList != null)
			this.eventList.clear();
		this.eventList = eventList;
	}

	public void invalidateEventList()
	{
		if (eventList != null)
			eventList.clear();
		eventList = null;
	}
	
/*	
	public Event getFirstEvent(int filterType)
	{
		if (eventList == null)
		{
			Event event = getDatabaseHandler().getFirstEvent();
			return event;
		}
		else
		{
			Event event;
			if (eventList.size() > 0)
				event = eventList.get(0);
			else
				event = null;
			
			return event;
		}
	}
*/	
/*
	public int getEventItemPosition(Event event)
	{
		if (event == null)
			return - 1;

		if (eventList == null)
			return getDatabaseHandler().getEventPosition(event);
		else
		{
			for (int i = 0; i < eventList.size(); i++)
			{
				if (eventList.get(i)._id == event._id)
					return i;
			}
			return -1;
		}
	}
*/	
	public void sortEventsByPriority()
	{
		class PriorityComparator implements Comparator<Event> {
			public int compare(Event lhs, Event rhs) {

			    int res =  lhs._priority - rhs._priority;
		        return res;
		    }
		}
		
		getEventList();
		if (eventList != null)
		{
		    Collections.sort(eventList, new PriorityComparator());
		}
	}
	
	public Event getEventById(long id)
	{
		if (eventList == null)
		{
			Event event = getDatabaseHandler().getEvent(id);
			return event;
		}
		else
		{
			Event event;
			for (int i = 0; i < eventList.size(); i++)
			{
				event = eventList.get(i); 
				if (event._id == id)
					return event;
			}

			// when filter is set and profile not found, get profile from db
			return getDatabaseHandler().getEvent(id);
		}
	}
	
	public void updateEvent(Event event)
	{
		if (event != null)
		{
			Event origEvent = getEventById(event._id);
			origEvent.copyEvent(event);
		}
	}
	
	public void reloadEventsData()
	{
		invalidateEventList();
		getEventList();
	}
	
	// pause all events associated with profile
	public void pauseEventsForProfile(Profile profile, boolean noSetSystemEvent)
	{
		List<EventTimeline> eventTimelineList = getEventTimelineList();
		
		for (Event event : getEventList())
		{
			if ((event.getStatusFromDB(this) == Event.ESTATUS_RUNNING) &&
				(event._fkProfileStart == profile._id))
				event.pauseEvent(this, eventTimelineList, false, true, noSetSystemEvent);
		}
	}

	// stops all events associated with profile
	public void stopEventsForProfile(Profile profile, boolean saveEventStatus)
	{
		List<EventTimeline> eventTimelineList = getEventTimelineList();
		
		for (Event event : getEventList())
		{
			if ((event.getStatusFromDB(this) == Event.ESTATUS_RUNNING) &&
				(event._fkProfileStart == profile._id))
				event.stopEvent(this, eventTimelineList, false, true, saveEventStatus);
		}
		restartEvents(false, false);
	}
	
	// pauses all events without activating profiles from Timeline
	public void pauseAllEvents(boolean noSetSystemEvent, boolean blockEvents)
	{
		List<EventTimeline> eventTimelineList = getEventTimelineList();
		
		for (Event event : getEventList())
		{
			if (event.getStatusFromDB(this) == Event.ESTATUS_RUNNING)
			{
				event.pauseEvent(this, eventTimelineList, false, true, noSetSystemEvent);
				if (event._forceRun)
					setEventBlocked(event, blockEvents);
			}
		}

		GlobalData.setEventsBlocked(context, blockEvents);
	}
	
	// stops all events without activating profiles from Timeline
	public void stopAllEvents(boolean saveEventStatus)
	{
		List<EventTimeline> eventTimelineList = getEventTimelineList();
		
		for (Event event : getEventList())
		{
			if (event.getStatusFromDB(this) != Event.ESTATUS_STOP)
				event.stopEvent(this, eventTimelineList, false, true, saveEventStatus);
		}
	}

	public void unlinkEventsFromProfile(Profile profile)
	{
		for (Event event : getEventList())
		{
			if (event._fkProfileStart == profile._id)
				event._fkProfileStart = 0;
			if (event._fkProfileEnd == profile._id)
				event._fkProfileEnd = Event.PROFILE_END_NO_ACTIVATE;
		}
	}
	
	public void unlinkAllEvents()
	{
		for (Event event : getEventList())
		{
			event._fkProfileStart = 0;
			event._fkProfileEnd = Event.PROFILE_END_NO_ACTIVATE;
		}
	}
	
	// this is called in boot or start application
	// or when restart alarm triggered (?)
	public void firstStartEvents(boolean invalidateList, boolean unblockEventsRun)
	{
		if (invalidateList)
			invalidateEventList();  // force load form db

		if (unblockEventsRun)
		{
			GlobalData.setEventsBlocked(context, false);
			getDatabaseHandler().unblockAllEvents();
		}
		
		GlobalData.setForceRunEventRunning(context, false);
		
		//getDatabaseHandler().updateAllEventsStatus(Event.ESTATUS_RUNNING, Event.ESTATUS_PAUSE);
		
		if (!GlobalData.getEventsBlocked(context))
			// events is not blocked, deactivate profile
			// profile will by activated in call of RestartEventsBroadcastReceiver
			getDatabaseHandler().deactivateProfile();

		SearchCalendarEventsBroadcastReceiver.setAlarm(context);
		WifiScanAlarmBroadcastReceiver.removeAlarm(context);
		
		//restartEvents(true, unblockEventsRun);
		Intent intent = new Intent();
		intent.setAction(RestartEventsBroadcastReceiver.INTENT_RESTART_EVENTS);
		context.sendBroadcast(intent);

	}
	
	public Event getNoinitializedEvent(String name)
	{
		return new Event(name, 
				0,
				Event.PROFILE_END_NO_ACTIVATE,
				Event.ESTATUS_STOP,
				"",
				false,
				false,
				true,
				Event.EPRIORITY_MEDIUM
         );
	}
	
	private long getProfileIdByName(String name)
	{
		if (profileList == null)
		{
			return 0;
		}
		else
		{
			Profile profile;
			for (int i = 0; i < profileList.size(); i++)
			{
				profile = profileList.get(i); 
				if (profile._name.equals(name))
					return profile._id;
			}
			return 0;
		}
	}
	
	public void generateDefaultEventList()
	{
		invalidateEventList();
		getDatabaseHandler().deleteAllEvents();

		Calendar calendar = Calendar.getInstance();
		calendar.clear(Calendar.DATE);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
    	int gmtOffset = TimeZone.getDefault().getRawOffset();
		
		Event event;
		
		event = getNoinitializedEvent(context.getString(R.string.default_event_name_during_the_week));
		event._fkProfileStart = getProfileIdByName(context.getString(R.string.default_profile_name_home));
		event._undoneProfile = false;
		event._eventPreferencesTime._enabled = true;
		event._eventPreferencesTime._monday = true;
		event._eventPreferencesTime._tuesday = true;
		event._eventPreferencesTime._wendesday = true;
		event._eventPreferencesTime._thursday = true;
		event._eventPreferencesTime._friday = true;
		calendar.set(Calendar.HOUR, 8);
		calendar.set(Calendar.MINUTE, 0);
		event._eventPreferencesTime._startTime = calendar.getTimeInMillis() + gmtOffset;
		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 0);
		event._eventPreferencesTime._endTime = calendar.getTimeInMillis() + gmtOffset;
		event._eventPreferencesTime._useEndTime = true;
		getDatabaseHandler().addEvent(event);
		event = getNoinitializedEvent(context.getString(R.string.default_event_name_weekend));
		event._fkProfileStart = getProfileIdByName(context.getString(R.string.default_profile_name_home));
		event._undoneProfile = false;
		event._eventPreferencesTime._enabled = true;
		event._eventPreferencesTime._saturday = true;
		event._eventPreferencesTime._sunday = true;
		calendar.set(Calendar.HOUR, 8);
		calendar.set(Calendar.MINUTE, 0);
		event._eventPreferencesTime._startTime = calendar.getTimeInMillis() + gmtOffset;
		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 0);
		event._eventPreferencesTime._endTime = calendar.getTimeInMillis() + gmtOffset;
		event._eventPreferencesTime._useEndTime = true;
		getDatabaseHandler().addEvent(event);
		event = getNoinitializedEvent(context.getString(R.string.default_event_name_during_the_work));
		event._fkProfileStart = getProfileIdByName(context.getString(R.string.default_profile_name_work));
		event._undoneProfile = true;
		event._priority = Event.EPRIORITY_HIGHER;
		event._eventPreferencesTime._enabled = true;
		event._eventPreferencesTime._monday = true;
		event._eventPreferencesTime._tuesday = true;
		event._eventPreferencesTime._wendesday = true;
		event._eventPreferencesTime._thursday = true;
		event._eventPreferencesTime._friday = true;
		calendar.set(Calendar.HOUR, 9);
		calendar.set(Calendar.MINUTE, 30);
		event._eventPreferencesTime._startTime = calendar.getTimeInMillis() + gmtOffset;
		calendar.set(Calendar.HOUR, 17);
		calendar.set(Calendar.MINUTE, 30);
		event._eventPreferencesTime._endTime = calendar.getTimeInMillis() + gmtOffset;
		event._eventPreferencesTime._useEndTime = true;
		getDatabaseHandler().addEvent(event);
		event = getNoinitializedEvent(context.getString(R.string.default_event_name_overnight));
		event._fkProfileStart = getProfileIdByName(context.getString(R.string.default_profile_name_sleep));
		event._undoneProfile = false;
		event._eventPreferencesTime._enabled = true;
		event._eventPreferencesTime._monday = true;
		event._eventPreferencesTime._tuesday = true;
		event._eventPreferencesTime._wendesday = true;
		event._eventPreferencesTime._thursday = true;
		event._eventPreferencesTime._friday = true;
		event._eventPreferencesTime._saturday = true;
		event._eventPreferencesTime._sunday = true;
		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 0);
		event._eventPreferencesTime._startTime = calendar.getTimeInMillis() + gmtOffset;
		calendar.set(Calendar.HOUR, 8);
		calendar.set(Calendar.MINUTE, 0);
		event._eventPreferencesTime._endTime = calendar.getTimeInMillis() + gmtOffset;
		event._eventPreferencesTime._useEndTime = true;
		getDatabaseHandler().addEvent(event);
		event = getNoinitializedEvent(context.getString(R.string.default_event_name_low_battery));
		event._fkProfileStart = getProfileIdByName(context.getString(R.string.default_profile_name_battery_low));
		event._undoneProfile = false;
		event._priority = Event.EPRIORITY_HIGHEST;
		event._forceRun = true;
		event._eventPreferencesBattery._enabled = true;
		event._eventPreferencesBattery._levelLow = 0;
		event._eventPreferencesBattery._levelHight = 10;
		event._eventPreferencesBattery._charging = false;
		getDatabaseHandler().addEvent(event);
		
	}
	
	
//---------------------------------------------------
	
	public List<EventTimeline> getEventTimelineList()
	{
		return getDatabaseHandler().getAllEventTimelines();
	}
	
	public void invalidateDataWrapper()
	{
		invalidateProfileList();
		invalidateEventList();
		databaseHandler = null;
		if (activateProfileHelper != null)
			activateProfileHelper.deinitialize();
		activateProfileHelper = null;
	}

//----- Activate profile ---------------------------------------------------------------------------------------------

	private void _activateProfile(Profile _profile, int startupSource, boolean _interactive, 
									Activity _activity, String eventNotificationSound)
	{
		Profile profile = GlobalData.getMappedProfile(_profile, context);
		//profile = filterProfileWithBatteryEvents(profile);
		
		boolean interactive = _interactive;
		final Activity activity = _activity;

		if ((startupSource != GlobalData.STARTUP_SOURCE_SERVICE) && 
			(startupSource != GlobalData.STARTUP_SOURCE_BOOT) &&
			(startupSource != GlobalData.STARTUP_SOURCE_LAUNCHER_START))
		{
			pauseAllEvents(false, true);
		}
		
		databaseHandler.activateProfile(profile);
		setProfileActive(profile);
		
		activateProfileHelper.execute(profile, interactive, eventNotificationSound);
		
		activateProfileHelper.showNotification(profile);
		activateProfileHelper.updateWidget();
		
		if (GlobalData.notificationsToast)
		{	
			// toast notification
			//Context _context = activity;
			//if (_context == null)
			//	_context = context.getApplicationContext();
			// create a handler to post messages to the main thread
			if (toastHandler != null)
			{
				final Profile __profile = profile;
				toastHandler.post(new Runnable() {
					public void run() {
						showToastAfterActivation(__profile);
					}
				});
			}
			else
				showToastAfterActivation(profile);
		}
			
		// for startActivityForResult
		if (activity != null)
		{
			Intent returnIntent = new Intent();
			returnIntent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile._id);
			returnIntent.getIntExtra(GlobalData.EXTRA_START_APP_SOURCE, startupSource);
			activity.setResult(Activity.RESULT_OK,returnIntent);
		}
		
		finishActivity(startupSource, true, activity);
	}
	
	private void showToastAfterActivation(Profile profile)
	{
		Toast msg = Toast.makeText(context, 
				context.getResources().getString(R.string.toast_profile_activated_0) + ": " + profile._name + " " +
				context.getResources().getString(R.string.toast_profile_activated_1), 
				Toast.LENGTH_SHORT);
		msg.show();
	}
	
	private void activateProfileWithAlert(Profile profile, int startupSource, boolean interactive, 
											Activity activity, String eventNotificationSound)
	{
		boolean isforceRunEvent = false;
		
		/*
		if (interactive)
		{
			// search for forceRun events
			getEventList();
			for (Event event : eventList)
			{
				if (event != null)
				{
					if ((event.getStatus() == Event.ESTATUS_RUNNING) && (event._forceRun))
					{
						isforceRunEvent = true;
						break;
					}
				}
			}
		}
		*/
		
		if ((interactive) && (GlobalData.applicationActivateWithAlert || 
							 (startupSource == GlobalData.STARTUP_SOURCE_EDITOR) || 
							 (isforceRunEvent)))	
		{	
			// set theme and language for dialog alert ;-)
			// not working on Android 2.3.x
			GUIData.setTheme(activity, true);
			GUIData.setLanguage(activity.getBaseContext());
			
			final Profile _profile = profile;
			final boolean _interactive = interactive;
			final int _startupSource = startupSource;
			final Activity _activity = activity;
			final String _eventNotificationSound = eventNotificationSound;

			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
			dialogBuilder.setTitle(activity.getResources().getString(R.string.profile_string_0) + ": " + profile._name);
			if (isforceRunEvent)
				dialogBuilder.setMessage(activity.getResources().getString(R.string.manual_profile_activation_forceRun_message));
			else
				dialogBuilder.setMessage(activity.getResources().getString(R.string.activate_profile_alert_message));
			//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
			dialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					_activateProfile(_profile, _startupSource, _interactive, _activity, _eventNotificationSound);
				}
			});
			dialogBuilder.setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// for startActivityForResult
					Intent returnIntent = new Intent();
					_activity.setResult(Activity.RESULT_CANCELED,returnIntent);
					
					finishActivity(_startupSource, false, _activity);
				}
			});
			dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				public void onCancel(DialogInterface dialog) {
					// for startActivityForResult
					Intent returnIntent = new Intent();
					_activity.setResult(Activity.RESULT_CANCELED,returnIntent);

					finishActivity(_startupSource, false, _activity);
				}
			});
			dialogBuilder.show();
		}
		else
		{
			_activateProfile(profile, startupSource, interactive, activity, eventNotificationSound);
		}
	}

	private void finishActivity(int startupSource, boolean afterActivation, Activity _activity)
	{
		final Activity activity = _activity;
		
		boolean finish = true;
		
		// kvoli nastaveniu brightness
		// pre BackgroundActivateProfileActivity nie je volana SetBrightnessWindowAttributesActivity
		// kde sa aj sleepuje
		boolean sleep = ((activity != null) && (activity instanceof BackgroundActivateProfileActivity));  

		if (startupSource == GlobalData.STARTUP_SOURCE_ACTIVATOR)
		{
			finish = false;
			if (GlobalData.applicationClose)
			{	
				// ma sa zatvarat aktivita po aktivacii
				if (GlobalData.getApplicationStarted(context))
					// aplikacia je uz spustena, mozeme aktivitu zavriet
					// tymto je vyriesene, ze pri spusteni aplikacie z launchera
					// sa hned nezavrie
					finish = afterActivation;
			}
		}
		else
		if (startupSource == GlobalData.STARTUP_SOURCE_EDITOR)
		{
			finish = false;
		}
		
		if (finish)
		{
			if (sleep)
			{
				Thread t = new Thread(new Runnable() {
		            public void run() {
		                try {
		                    Thread.sleep(100);
		                } catch (InterruptedException e) {
		                    System.out.println(e);
		                }
		                if (activity != null)
		                	activity.finish();
		            }
		        });
				t.start();
			}
			else
			{
                if (activity != null)
                	activity.finish();
			}
		}
	}
	
	public void activateProfile(long profile_id, int startupSource, Activity activity, String eventNotificationSound)
	{
		Profile profile;
		
		// pre profil, ktory je prave aktivny, treba aktualizovat aktivitu
		profile = getActivatedProfile();
		
		boolean actProfile = false;
		boolean interactive = false;
		if ((startupSource == GlobalData.STARTUP_SOURCE_SHORTCUT) ||
			(startupSource == GlobalData.STARTUP_SOURCE_WIDGET) ||
			(startupSource == GlobalData.STARTUP_SOURCE_ACTIVATOR) ||
			(startupSource == GlobalData.STARTUP_SOURCE_EDITOR) ||
			(startupSource == GlobalData.STARTUP_SOURCE_SERVICE) ||
			(startupSource == GlobalData.STARTUP_SOURCE_LAUNCHER))
		{
			// aktivacia spustena z shortcutu, widgetu, aktivatora, editora, zo service, profil aktivujeme
			actProfile = true;
			interactive = ((startupSource != GlobalData.STARTUP_SOURCE_SERVICE));
		}
		else
		if (startupSource == GlobalData.STARTUP_SOURCE_BOOT)	
		{
			// aktivacia bola spustena po boote telefonu
			
			if (GlobalData.applicationActivate)
			{
				// je nastavene, ze pri starte sa ma aktivita aktivovat
				actProfile = true;
			}
			else
			{
				// nema sa aktivovat profil pri starte, ale musim pozriet, ci daky event bezi
				// a ak ano, aktivovat profil posledneho eventu v timeline
				boolean eventRunning = false;
				List<EventTimeline> eventTimelineList = getEventTimelineList();
				if (eventTimelineList.size() > 0)
				{
					eventRunning = true;
					
					EventTimeline eventTimeline = eventTimelineList.get(eventTimelineList.size()-1);
					
					Event _event = getEventById(eventTimeline._fkEvent);
					profile = getProfileById(_event._fkProfileStart);
					actProfile = true;
				}

				
				if ((profile != null) && (!eventRunning))
				{
					getDatabaseHandler().deactivateProfile();
					//profile._checked = false;
					profile = null;
				}
			}
		}
		else
		if (startupSource == GlobalData.STARTUP_SOURCE_LAUNCHER_START)	
		{
			// aktivacia bola spustena z lauchera 
			
			if (GlobalData.applicationActivate)
			{
				// je nastavene, ze pri starte sa ma aktivita aktivovat
				actProfile = true;
			}
			else
			{
				if (profile != null)
				{
					getDatabaseHandler().deactivateProfile();
					//profile._checked = false;
					profile = null;
				}
			}
		}
			
		//Log.d("DataWrapper.activateProfile", "actProfile="+String.valueOf(actProfile));

		if ((startupSource == GlobalData.STARTUP_SOURCE_SHORTCUT) ||
			(startupSource == GlobalData.STARTUP_SOURCE_WIDGET) ||
			(startupSource == GlobalData.STARTUP_SOURCE_ACTIVATOR) ||
			(startupSource == GlobalData.STARTUP_SOURCE_EDITOR) ||
			(startupSource == GlobalData.STARTUP_SOURCE_SERVICE) ||
			(startupSource == GlobalData.STARTUP_SOURCE_LAUNCHER_START) ||
			(startupSource == GlobalData.STARTUP_SOURCE_LAUNCHER))	
		{
			if (profile_id == 0)
				profile = null;
			else
				profile = getProfileById(profile_id);

			//Log.d("DataWrapper.activateProfile","_iconBitmap="+String.valueOf(profile._iconBitmap));
			//Log.d("DataWrapper.activateProfile","_preferencesIndicator="+String.valueOf(profile._preferencesIndicator));
		}

		
		if (actProfile && (profile != null))
		{
			// aktivacia profilu
			activateProfileWithAlert(profile, startupSource, interactive, activity, eventNotificationSound);
		}
		else
		{
			activateProfileHelper.showNotification(profile);
			activateProfileHelper.updateWidget();

			// for startActivityForResult
			if (activity != null)
			{
				Intent returnIntent = new Intent();
				returnIntent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile_id);
				returnIntent.getIntExtra(GlobalData.EXTRA_START_APP_SOURCE, startupSource);
				activity.setResult(Activity.RESULT_OK,returnIntent);
			}
			
			finishActivity(startupSource, true, activity);
		}
		
	}

	@SuppressLint("NewApi")
	public boolean doEventService(Event event, boolean statePause, 
									boolean restartEvent, boolean interactive)
	{
		int newEventStatus = Event.ESTATUS_NONE;

		boolean eventStart = true;
		
		boolean timePassed = true;
		boolean batteryPassed = true;
		boolean callPassed = true;
		boolean peripheralPassed = true;
		boolean calendarPassed = true;
		boolean wifiPassed = true;
		
		boolean isCharging = false;
		float batteryPct = 100.0f;
		
		boolean phoneNumberFinded = false;
		
		if (event._eventPreferencesTime._enabled)
		{
			// compute start datetime
   			long startAlarmTime;
   			long endAlarmTime;
			
			startAlarmTime = event._eventPreferencesTime.computeAlarm(true);
			
   		    String alarmTimeS = DateFormat.getDateFormat(context).format(startAlarmTime) +
	   		    	  		    " " + DateFormat.getTimeFormat(context).format(startAlarmTime);
			GlobalData.logE("DataWrapper.doEventService","startAlarmTime="+alarmTimeS);
			
			endAlarmTime = event._eventPreferencesTime.computeAlarm(false);

   		    alarmTimeS = DateFormat.getDateFormat(context).format(endAlarmTime) +
	    	  		     " " + DateFormat.getTimeFormat(context).format(endAlarmTime);
   		    GlobalData.logE("DataWrapper.doEventService","endAlarmTime="+alarmTimeS);
			
			Calendar now = Calendar.getInstance();
			long nowAlarmTime = now.getTimeInMillis();
   		    alarmTimeS = DateFormat.getDateFormat(context).format(nowAlarmTime) +
   	  		     " " + DateFormat.getTimeFormat(context).format(nowAlarmTime);
		    GlobalData.logE("DataWrapper.doEventService","nowAlarmTime="+alarmTimeS);

			timePassed = ((nowAlarmTime >= startAlarmTime) && (nowAlarmTime <= endAlarmTime));
			
			eventStart = eventStart && timePassed;
		}
		
		if (event._eventPreferencesBattery._enabled)
		{
			// get battery status
			IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent batteryStatus = context.registerReceiver(null, ifilter);
			
			if (batteryStatus != null)
			{
				int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
				GlobalData.logE("DataWrapper.doEventService","status="+status);
				isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
				             status == BatteryManager.BATTERY_STATUS_FULL;
				GlobalData.logE("DataWrapper.doEventService","isCharging="+isCharging);
				
				int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				batteryPct = level / (float)scale;	
			}
			else
			{
				isCharging = false;
				batteryPct = -1.0f;
			}
			
			GlobalData.logE("DataWrapper.doEventService","batteryPct="+batteryPct);

			batteryPassed = (isCharging == event._eventPreferencesBattery._charging);
			
			if (batteryPassed)
			{
				if ((batteryPct >= (event._eventPreferencesBattery._levelLow / (float)100)) && 
				    (batteryPct <= (event._eventPreferencesBattery._levelHight / (float)100))) 
				{
					eventStart = eventStart && true;
				}
				else
				{
					batteryPassed = false;
					eventStart = eventStart && false;
				}
			}
		}

		if (event._eventPreferencesCall._enabled)
		{
			SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
			int callEventType = preferences.getInt(GlobalData.PREF_EVENT_CALL_EVENT_TYPE, PhoneCallBroadcastReceiver.CALL_EVENT_UNDEFINED);
			String phoneNumber = preferences.getString(GlobalData.PREF_EVENT_CALL_PHONE_NUMBER, "");
			
			if (callEventType != PhoneCallBroadcastReceiver.CALL_EVENT_UNDEFINED)
			{
				if (event._eventPreferencesCall._contactListType != EventPreferencesCall.CONTACT_LIST_TYPE_NOT_USE)
				{
					// find phone number
					String[] splits = event._eventPreferencesCall._contacts.split("\\|");
					for (int i = 0; i < splits.length; i++)
					{
						String [] splits2 = splits[i].split("#");
	
						// get phone number from contacts
						String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.HAS_PHONE_NUMBER };
						String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "='1' and " + ContactsContract.Contacts._ID + "=?";
						String[] selectionArgs = new String[] { splits2[0] };
						Cursor mCursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, selection, selectionArgs, null);
						while (mCursor.moveToNext()) 
						{
							if (Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) 
							{
								String[] projection2 = new String[] { ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.NUMBER };
								String selection2 = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?" + " and " + ContactsContract.CommonDataKinds.Phone._ID + "=?";
								String[] selection2Args = new String[] { splits2[0],splits2[1] };
								Cursor phones = context.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection2, selection2, selection2Args, null);
								while (phones.moveToNext()) 
								{
									String _phoneNumber = phones.getString(phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));
									//Log.e("DataWrapper.doEventService","_phoneNumber="+_phoneNumber);
									//Log.e("DataWrapper.doEventService","phoneNumber="+phoneNumber);
									if (PhoneNumberUtils.compare(_phoneNumber, phoneNumber))
									{
										phoneNumberFinded = true;
										break;
									}
								}
								phones.close();
							}
							if (phoneNumberFinded)
								break;
						}
						mCursor.close();
						if (phoneNumberFinded)
							break;
					}
					if (event._eventPreferencesCall._contactListType == EventPreferencesCall.CONTACT_LIST_TYPE_BLACK_LIST)
						phoneNumberFinded = !phoneNumberFinded;
				}
				else
					phoneNumberFinded = true;

				//Log.e("DataWrapper.doEventService","phoneNumberFinded="+phoneNumberFinded);
				//Log.e("DataWrapper.doEventService","EventsService.callEventType="+EventsService.callEventType);
				
				if (phoneNumberFinded)
				{
					if (event._eventPreferencesCall._callEvent == EventPreferencesCall.CALL_EVENT_RINGING)
					{
						if (callEventType == PhoneCallBroadcastReceiver.CALL_EVENT_INCOMING_CALL_RINGING)
							eventStart = eventStart && true;
						else
							callPassed = false;
					}
					else
					if (event._eventPreferencesCall._callEvent == EventPreferencesCall.CALL_EVENT_INCOMING_CALL_ANSWERED)
					{
						if (callEventType == PhoneCallBroadcastReceiver.CALL_EVENT_INCOMING_CALL_ANSWERED)
							eventStart = eventStart && true;
						else	
							callPassed = false;
					}
					else
					if (event._eventPreferencesCall._callEvent == EventPreferencesCall.CALL_EVENT_OUTGOING_CALL_STARTED)
					{
						if (callEventType == PhoneCallBroadcastReceiver.CALL_EVENT_OUTGOING_CALL_STARTED)
							eventStart = eventStart && true;
						else
							callPassed = false;
					}
					
					if ((callEventType == PhoneCallBroadcastReceiver.CALL_EVENT_INCOMING_CALL_ENDED) ||
						(callEventType == PhoneCallBroadcastReceiver.CALL_EVENT_OUTGOING_CALL_ENDED))
					{
						callPassed = true;
						eventStart = eventStart && false;
						Editor editor = preferences.edit();
						editor.putInt(GlobalData.PREF_EVENT_CALL_EVENT_TYPE, PhoneCallBroadcastReceiver.CALL_EVENT_UNDEFINED);
						editor.putString(GlobalData.PREF_EVENT_CALL_PHONE_NUMBER, "");
						editor.commit();
					}
				}
				else
					callPassed = false;
				
			}
			else
				callPassed = false;
		}

		if (event._eventPreferencesPeripherals._enabled)
		{
			if ((event._eventPreferencesPeripherals._peripheralType == EventPreferencesPeripherals.PERIPHERAL_TYPE_DESK_DOCK) ||
				(event._eventPreferencesPeripherals._peripheralType == EventPreferencesPeripherals.PERIPHERAL_TYPE_CAR_DOCK))
			{
				// get dock status
				IntentFilter ifilter = new IntentFilter(Intent.ACTION_DOCK_EVENT);
				Intent dockStatus = context.registerReceiver(null, ifilter);

				boolean isDocked = false;
				boolean isCar = false;
				boolean isDesk = false;				
				
				if (dockStatus != null)
				{
					int dockState = dockStatus.getIntExtra(Intent.EXTRA_DOCK_STATE, -1);
					isDocked = dockState != Intent.EXTRA_DOCK_STATE_UNDOCKED;
					isCar = dockState == Intent.EXTRA_DOCK_STATE_CAR;
					isDesk = dockState == Intent.EXTRA_DOCK_STATE_DESK || 
					         dockState == Intent.EXTRA_DOCK_STATE_LE_DESK ||
					         dockState == Intent.EXTRA_DOCK_STATE_HE_DESK;
				}
				
				if (isDocked)
				{
					if ((event._eventPreferencesPeripherals._peripheralType == EventPreferencesPeripherals.PERIPHERAL_TYPE_DESK_DOCK) 
							&& isDesk)
						peripheralPassed = true;
					else
					if ((event._eventPreferencesPeripherals._peripheralType == EventPreferencesPeripherals.PERIPHERAL_TYPE_CAR_DOCK) 
							&& isCar)
						peripheralPassed = true;
					else
						peripheralPassed = false;
				}
				else
					peripheralPassed = false;
				eventStart = eventStart && peripheralPassed;
			}
			else
			if ((event._eventPreferencesPeripherals._peripheralType == EventPreferencesPeripherals.PERIPHERAL_TYPE_WIRED_HEADSET) ||
				(event._eventPreferencesPeripherals._peripheralType == EventPreferencesPeripherals.PERIPHERAL_TYPE_BLUETOOTH_HEADSET) ||
				(event._eventPreferencesPeripherals._peripheralType == EventPreferencesPeripherals.PERIPHERAL_TYPE_HEADPHONES))
			{
				SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
				boolean headsetConnected = preferences.getBoolean(GlobalData.PREF_EVENT_HEADSET_CONNECTED, false);
				boolean headsetMicrophone = preferences.getBoolean(GlobalData.PREF_EVENT_HEADSET_MICROPHONE, false);
				boolean bluetoothHeadset = preferences.getBoolean(GlobalData.PREF_EVENT_HEADSET_BLUETOOTH, false);
				
				if (headsetConnected)
				{
					if ((event._eventPreferencesPeripherals._peripheralType == EventPreferencesPeripherals.PERIPHERAL_TYPE_WIRED_HEADSET) 
							&& headsetMicrophone && (!bluetoothHeadset))
						peripheralPassed = true;
					else
					if ((event._eventPreferencesPeripherals._peripheralType == EventPreferencesPeripherals.PERIPHERAL_TYPE_BLUETOOTH_HEADSET) 
							&& headsetMicrophone && bluetoothHeadset)
						peripheralPassed = true;
					else
					if ((event._eventPreferencesPeripherals._peripheralType == EventPreferencesPeripherals.PERIPHERAL_TYPE_HEADPHONES)
							&& (!headsetMicrophone) && (!bluetoothHeadset))
						peripheralPassed = true;
					else
						peripheralPassed = false;
				}
				else
					peripheralPassed = false;
				eventStart = eventStart && peripheralPassed;
			}
		}

		if (event._eventPreferencesCalendar._enabled)
		{
			// compute start datetime
   			long startAlarmTime;
   			long endAlarmTime;
			
   			if (event._eventPreferencesCalendar._eventFound)
   			{
				startAlarmTime = event._eventPreferencesCalendar.computeAlarm(true);
				
	   		    String alarmTimeS = DateFormat.getDateFormat(context).format(startAlarmTime) +
		   		    	  		    " " + DateFormat.getTimeFormat(context).format(startAlarmTime);
				GlobalData.logE("DataWrapper.doEventService","startAlarmTime="+alarmTimeS);
				
				endAlarmTime = event._eventPreferencesCalendar.computeAlarm(false);
	
	   		    alarmTimeS = DateFormat.getDateFormat(context).format(endAlarmTime) +
		    	  		     " " + DateFormat.getTimeFormat(context).format(endAlarmTime);
	   		    GlobalData.logE("DataWrapper.doEventService","endAlarmTime="+alarmTimeS);
				
				Calendar now = Calendar.getInstance();
				long nowAlarmTime = now.getTimeInMillis();
	   		    alarmTimeS = DateFormat.getDateFormat(context).format(nowAlarmTime) +
	   	  		     " " + DateFormat.getTimeFormat(context).format(nowAlarmTime);
			    GlobalData.logE("DataWrapper.doEventService","nowAlarmTime="+alarmTimeS);
	
				calendarPassed = ((nowAlarmTime >= startAlarmTime) && (nowAlarmTime <= endAlarmTime));
   			}
   			else
   				calendarPassed = false;
			
			eventStart = eventStart && calendarPassed;
		}
		
		if (event._eventPreferencesWifi._enabled)
		{
			wifiPassed = false;

			if (event._eventPreferencesWifi._connectionType == EventPreferencesWifi.CTYPE_CONNECTED)
			{
				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
				{
					GlobalData.logE("DataWrapper.doEventService","wifiStateEnabled=true");
					
					//ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
					//NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					//if (mWifi.isConnected())
					//{
					//	GlobalData.logE("DataWrapper.doEventService","wifiConnected=true");
	
						WifiInfo wifiInfo = wifiManager.getConnectionInfo();
						
						GlobalData.logE("DataWrapper.doEventService","wifiSSID="+wifiInfo.getSSID());
						GlobalData.logE("DataWrapper.doEventService","eventSSID="+event._eventPreferencesWifi._SSID);
						
						String ssid1 = event._eventPreferencesWifi._SSID;
						String ssid2 = "\"" + ssid1 + "\"";
						if (wifiInfo.getSSID().equals(ssid1) || wifiInfo.getSSID().equals(ssid2))
							wifiPassed = true;
					//}
				}
			}
			else
			if (event._eventPreferencesWifi._connectionType == EventPreferencesWifi.CTYPE_INFRONT)
			{
				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				boolean isWifiEnabled = wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
		    	if (android.os.Build.VERSION.SDK_INT >= 18)
		    		isWifiEnabled = isWifiEnabled || (wifiManager.isScanAlwaysAvailable());
				if (isWifiEnabled)
				{
					GlobalData.logE("DataWrapper.doEventService","wifiStateEnabled=true");

					if (WifiScanAlarmBroadcastReceiver.scanResults != null)
					{
						for (ScanResult result : WifiScanAlarmBroadcastReceiver.scanResults)
				        {
							String ssid1 = event._eventPreferencesWifi._SSID;
							String ssid2 = "\"" + ssid1 + "\"";
							if (result.SSID.equals(ssid1) || result.SSID.equals(ssid2))
							{
								wifiPassed = true;
								break;
							}
				        }
					}
				}
			}

			eventStart = eventStart && wifiPassed;
		}
		
		GlobalData.logE("DataWrapper.doEventService","timePassed="+timePassed);
		GlobalData.logE("DataWrapper.doEventService","batteryPassed="+batteryPassed);
		GlobalData.logE("DataWrapper.doEventService","callPassed="+callPassed);
		GlobalData.logE("DataWrapper.doEventService","peripheralPassed="+peripheralPassed);
		GlobalData.logE("DataWrapper.doEventService","calendarPassed="+calendarPassed);
		GlobalData.logE("DataWrapper.doEventService","wifiPassed="+wifiPassed);

		GlobalData.logE("DataWrapper.doEventService","eventStart="+eventStart);
		GlobalData.logE("DataWrapper.doEventService","restartEvent="+restartEvent);
		
		List<EventTimeline> eventTimelineList = getEventTimelineList();
		
		if (timePassed && 
			batteryPassed && 
			callPassed && 
			peripheralPassed && 
			calendarPassed &&
			wifiPassed)
		{
			// podmienky sedia, vykoname, co treba

			if (eventStart)
				newEventStatus = Event.ESTATUS_RUNNING;
			else
				newEventStatus = Event.ESTATUS_PAUSE;
			
		}
		else
			newEventStatus = Event.ESTATUS_PAUSE;

		GlobalData.logE("DataWrapper.doEventService","event.getStatus()="+event.getStatus());
		GlobalData.logE("DataWrapper.doEventService","newEventStatus="+newEventStatus);
		
		if ((event.getStatus() != newEventStatus) || restartEvent)
		{
			GlobalData.logE("DataWrapper.doEventService"," do new event status");
			
			if ((newEventStatus == Event.ESTATUS_RUNNING) && (!statePause))
			{
				GlobalData.logE("DataWrapper.doEventService","start event");
				event.startEvent(this, eventTimelineList, false, interactive);
			}
			else
			if ((newEventStatus == Event.ESTATUS_PAUSE) && statePause)
			{
				GlobalData.logE("DataWrapper.doEventService","pause event");
				event.pauseEvent(this, eventTimelineList, true, false, false);
			}
		}
		
		return (timePassed && 
				batteryPassed && 
				callPassed && 
				peripheralPassed && 
				calendarPassed &&
				wifiPassed);
	}
	
	public Profile filterProfileWithBatteryEvents(Profile profile)
	{
		if (profile != null)
		{
			Profile filteredProfile = new Profile(
					           profile._id,
							   profile._name, 
							   profile._icon, 
							   profile._checked, 
							   profile._porder,
							   profile._volumeRingerMode,
							   profile._volumeRingtone,
							   profile._volumeNotification,
							   profile._volumeMedia,
							   profile._volumeAlarm,
							   profile._volumeSystem,
							   profile._volumeVoice,
							   profile._soundRingtoneChange,
							   profile._soundRingtone,
							   profile._soundNotificationChange,
							   profile._soundNotification,
							   profile._soundAlarmChange,
							   profile._soundAlarm,
							   profile._deviceAirplaneMode,
							   profile._deviceWiFi,
							   profile._deviceBluetooth,
							   profile._deviceScreenTimeout,
							   profile._deviceBrightness,
							   profile._deviceWallpaperChange,
							   profile._deviceWallpaper,
							   profile._deviceMobileData,
							   profile._deviceMobileDataPrefs,
							   profile._deviceGPS,
							   profile._deviceRunApplicationChange,
							   profile._deviceRunApplicationPackageName,
							   profile._deviceAutosync,
							   profile._showInActivator,
							   profile._deviceAutoRotate,
							   profile._deviceLocationServicePrefs,
							   profile._volumeSpeakerPhone,
							   profile._deviceNFC);
		
			List<EventTimeline> eventTimelineList = getEventTimelineList();
			
			// search from last events in timeline
			for (int i = eventTimelineList.size()-1; i >= 0; i--)
			{
				EventTimeline eventTimeline = eventTimelineList.get(i);
				
				Event event = getEventById(eventTimeline._fkEvent);

				if ((event != null) && event._eventPreferencesBattery._enabled)
				{
					EventPreferencesBattery eventPreferences = event._eventPreferencesBattery;
					if (!eventPreferences._charging)
					{
						Profile eventProfile = getProfileById(event._fkProfileStart);
						
						if (filteredProfile._id == eventProfile._id)
							break;
						
						// preferences which event profile change, must by set as "no change" for filtered profile 
						
						if (eventProfile._volumeRingerMode != 0)
							filteredProfile._volumeRingerMode = 0;
						if (eventProfile.getVolumeRingtoneChange())
							filteredProfile._volumeRingtone = "0|1|0";
						if (eventProfile.getVolumeNotificationChange())
							filteredProfile._volumeNotification = "0|1|0";
						if (eventProfile.getVolumeAlarmChange())
							filteredProfile._volumeAlarm = "0|1|0";
						if (eventProfile.getVolumeMediaChange())
							filteredProfile._volumeMedia = "0|1|0";
						if (eventProfile.getVolumeSystemChange())
							filteredProfile._volumeSystem = "0|1|0";
						if (eventProfile.getVolumeVoiceChange())
							filteredProfile._volumeVoice = "0|1|0";
						if (eventProfile._soundRingtoneChange != 0)
							filteredProfile._soundRingtoneChange = 0;
						if (eventProfile._soundNotificationChange != 0)
							filteredProfile._soundNotificationChange = 0;
						if (eventProfile._soundAlarmChange != 0)
							filteredProfile._soundAlarmChange = 0;
						if (eventProfile._deviceAirplaneMode != 0)
							filteredProfile._deviceAirplaneMode = 0;
						if (eventProfile._deviceAutosync != 0)
							filteredProfile._deviceAutosync = 0;
						if (eventProfile._deviceMobileData != 0)
							filteredProfile._deviceMobileData = 0;
						if (eventProfile._deviceMobileDataPrefs != 0)
							filteredProfile._deviceMobileDataPrefs = 0;
						if (eventProfile._deviceWiFi != 0)
							filteredProfile._deviceWiFi = 0;
						if (eventProfile._deviceBluetooth != 0)
							filteredProfile._deviceBluetooth = 0;
						if (eventProfile._deviceGPS != 0)
							filteredProfile._deviceGPS = 0;
						if (eventProfile._deviceLocationServicePrefs != 0)
							filteredProfile._deviceLocationServicePrefs = 0;
						if (eventProfile._deviceScreenTimeout != 0)
							filteredProfile._deviceScreenTimeout = 0;
						if (eventProfile.getDeviceBrightnessChange() || eventProfile.getDeviceBrightnessAutomatic())
							filteredProfile._deviceBrightness = "0|1|0|0";
						if (eventProfile._deviceAutoRotate != 0)
							filteredProfile._deviceAutoRotate = 0;
						if (eventProfile._deviceRunApplicationChange != 0)
							filteredProfile._deviceRunApplicationChange = 0;
						if (eventProfile._deviceWallpaperChange != 0)
							filteredProfile._deviceWallpaperChange = 0;
						if (eventProfile._volumeSpeakerPhone != 0)
							filteredProfile._volumeSpeakerPhone = 0;
						if (eventProfile._deviceNFC != 0)
							filteredProfile._deviceNFC = 0;
						
						// last event finded
						break;
					}
				}
			}
			
			filteredProfile._iconBitmap = profile._iconBitmap;
			filteredProfile._preferencesIndicator = profile._preferencesIndicator;
			
			return filteredProfile;
		}
		else 
			return profile;
		
	}

	public void restartEvents(boolean ignoreForceRun, boolean unblockEventsRun)
	{
		GlobalData.logE("DataWrapper.restartEvents","xxx");

		if (!GlobalData.getGlobalEventsRuning(context))
			// events are globally stopped
			return;

		GlobalData.logE("DataWrapper.restartEvents","events are not globbaly stopped");
		
		if (GlobalData.getEventsBlocked(context) && (!ignoreForceRun))
			return;

		GlobalData.logE("DataWrapper.restartEvents","events are not blocked");
		
		if (unblockEventsRun)
		{
			GlobalData.setEventsBlocked(context, false);
			getDatabaseHandler().unblockAllEvents();
		}
		
		//getDatabaseHandler().updateAllEventsStatus(Event.ESTATUS_RUNNING, Event.ESTATUS_PAUSE);
		
		Intent intent = new Intent();
		intent.setAction(RestartEventsBroadcastReceiver.INTENT_RESTART_EVENTS);
		context.sendBroadcast(intent);
	}
	
	public void restartEventsWithAlert(Activity activity)
	{
		if (!GlobalData.getGlobalEventsRuning(context))
			// events are globally stopped
			return;
	
		/*
		if (!GlobalData.getEventsBlocked(context))
			return;
		*/

		if (GlobalData.applicationActivateWithAlert || (activity instanceof EditorProfilesActivity))
		{
			final Activity _activity = activity;
			
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
			dialogBuilder.setTitle(activity.getResources().getString(R.string.restart_events_alert_title));
			dialogBuilder.setMessage(activity.getResources().getString(R.string.restart_events_alert_message));
			//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
			dialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// ignoruj manualnu aktivaciu profilu
					// a odblokuj forceRun eventy
					restartEvents(true, true);
					if (GlobalData.applicationClose && (!(_activity instanceof EditorProfilesActivity)))
						_activity.finish();
				}
			});
			dialogBuilder.setNegativeButton(R.string.alert_button_no, null);
			dialogBuilder.show();
		}
		else
		{
			// ignoruj manualnu aktivaciu profilu
			// a odblokuj forceRun eventy
			restartEvents(true, true);
			if (GlobalData.applicationClose)
				activity.finish();
		}
	}
	
	public void setEventBlocked(Event event, boolean blocked)
	{
		event._blocked = blocked;
		getDatabaseHandler().updateEventBlocked(event);
	}
	
	public String getProfileNameWithManualIndicator(Profile profile)
	{
		if (profile == null)
			return "";
		
		if (!GlobalData.getEventsBlocked(context))
			return profile._name;
		else
		{
			if (GlobalData.getForceRunEventRunning(context))
			{
	   			/*if (android.os.Build.VERSION.SDK_INT >= 16)
	   				return "\u23E9 " + profile._name;
	   			else*/
	   				return "[>>] " + profile._name;
			}
			else
			{
	   			/*if (android.os.Build.VERSION.SDK_INT >= 16)
	   				return "\uD83D\uDC46 " + profile._name;
	   			else */
	   				return "[M] " + profile._name;
			}
		}
	}
	
}
