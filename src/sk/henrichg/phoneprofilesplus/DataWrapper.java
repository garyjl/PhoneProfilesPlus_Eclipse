package sk.henrichg.phoneprofilesplus;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.Settings;

public class DataWrapper {

	private Context context = null;
	private boolean forGUI = false;
	private boolean monochrome = false;
	private int monochromeValue = 0xFF;
	

	private DatabaseHandler databaseHandler = null;
	private ActivateProfileHelper activateProfileHelper = null;
	private List<Profile> profileList = null;
	private List<Event> eventList = null;
	// timeline of runnig events
	private List<EventTimeline> eventTimelineList = null;
	
	DataWrapper(Context c, 
						boolean fgui, 
						boolean mono, 
						int monoVal)
	{
		context = c;
		
		setParameters(fgui, mono, monoVal); 
		
		databaseHandler = getDatabaseHandler();
		activateProfileHelper = getActivateProfileHelper();
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
	
	public DatabaseHandler getDatabaseHandler()
	{
		if (databaseHandler == null)
			// parameter must by application context
			databaseHandler = DatabaseHandler.getInstance(context.getApplicationContext());
			
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
	
	public Profile getNoinitializedProfile(String name, String icon, int order)
	{
		return new Profile(
				  name, 
				  icon + "|1", 
				  false, 
				  order,
				  0,
	         	  "-1|1",
	         	  "-1|1",
	         	  "-1|1",
	         	  "-1|1",
	         	  "-1|1",
	         	  "-1|1",
	         	  false,
	         	  Settings.System.DEFAULT_RINGTONE_URI.toString(),
	         	  false,
	         	  Settings.System.DEFAULT_NOTIFICATION_URI.toString(),
	         	  false,
	         	  Settings.System.DEFAULT_ALARM_ALERT_URI.toString(),
	         	  0,
	         	  0,
	         	  0,
	         	  0,
	         	  "-1|1|1",
	         	  false,
				  "-|0",
				  0,
				  false,
				  0,
				  false,
				  "-",
				  0,
				  false
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
		profile._volumeRingtone = getVolumeLevelString(71, maximumValueRing)+"|0";
		profile._volumeNotification = getVolumeLevelString(86, maximumValueNotification)+"|0";
		profile._volumeAlarm = getVolumeLevelString(100, maximumValueAlarm)+"|0";
		profile._volumeMedia = getVolumeLevelString(80, maximumValueMusic)+"|0";
		profile._deviceWiFi = 1;
		//profile._deviceBrightness = "60|0|0";
		getDatabaseHandler().addProfile(profile);
		profile = getNoinitializedProfile(context.getString(R.string.default_profile_name_outdoor), "ic_profile_outdoors_1", 2);
		profile._showInActivator = true;
		profile._volumeRingerMode = 2;
		profile._volumeRingtone = getVolumeLevelString(100, maximumValueRing)+"|0";
		profile._volumeNotification = getVolumeLevelString(100, maximumValueNotification)+"|0";
		profile._volumeAlarm = getVolumeLevelString(100, maximumValueAlarm)+"|0";
		profile._volumeMedia = getVolumeLevelString(93, maximumValueMusic)+"|0";
		profile._deviceWiFi = 2;
		//profile._deviceBrightness = "255|0|0";
		getDatabaseHandler().addProfile(profile);
		profile = getNoinitializedProfile(context.getString(R.string.default_profile_name_work), "ic_profile_work_5", 3);
		profile._showInActivator = true;
		profile._volumeRingerMode = 1;
		profile._volumeRingtone = getVolumeLevelString(57, maximumValueRing)+"|0"; 
		profile._volumeNotification = getVolumeLevelString(71, maximumValueNotification)+"|0";
		profile._volumeAlarm = getVolumeLevelString(57, maximumValueAlarm)+"|0";
		profile._volumeMedia = getVolumeLevelString(80, maximumValueMusic)+"|0";
		profile._deviceWiFi = 2;
		//profile._deviceBrightness = "60|0|0";
		getDatabaseHandler().addProfile(profile);
		profile = getNoinitializedProfile(context.getString(R.string.default_profile_name_meeting), "ic_profile_meeting_2", 4);
		profile._showInActivator = true;
		profile._volumeRingerMode = 4;
		profile._volumeRingtone = getVolumeLevelString(0, maximumValueRing)+"|0";
		profile._volumeNotification = getVolumeLevelString(0, maximumValueNotification)+"|0";
		profile._volumeAlarm = getVolumeLevelString(0, maximumValueAlarm)+"|0";
		profile._volumeMedia = getVolumeLevelString(0, maximumValueMusic)+"|0";
		profile._deviceWiFi = 0;
		//profile._deviceBrightness = "-1|1|1";
		getDatabaseHandler().addProfile(profile);
		profile = getNoinitializedProfile(context.getString(R.string.default_profile_name_sleep), "ic_profile_sleep", 5);
		profile._showInActivator = true;
		profile._volumeRingerMode = 4;
		profile._volumeRingtone = getVolumeLevelString(0, maximumValueRing)+"|0";
		profile._volumeNotification = getVolumeLevelString(0, maximumValueNotification)+"|0";
		profile._volumeAlarm = getVolumeLevelString(100, maximumValueAlarm)+"|0";
		profile._volumeMedia = getVolumeLevelString(0, maximumValueMusic)+"|0";
		profile._deviceWiFi = 0;
		//profile._deviceBrightness = "10|0|0";
		getDatabaseHandler().addProfile(profile);
		
		return getProfileList();
	}
	
	public void invalidateProfileList()
	{
		if (profileList != null)
			profileList.clear();
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
	public void activateProfile(Profile profile)
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
	
	public void activateProfileFromEvent(long profile_id)
	{
		//Log.d("PhoneProfilesService.activateProfile",profile_id+"");
		Intent intent = new Intent(context, BackgroundActivateProfileActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	intent.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_SERVICE);
		intent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile_id);
	    context.startActivity(intent);		
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
			if (event._fkProfile == profile._id) 
				event._fkProfile = 0;
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
			event._fkProfile = 0;
		}
	}

	public void deleteProfileFromService(Profile profile)
	{
		if (profile == null)
			return;
		
		profileList.remove(profile);
		if (eventList == null)
			eventList = getEventList();
		// unlink profile from events
		for (Event event : eventList)
		{
			if (event._fkProfile == profile._id) 
				event._fkProfile = 0;
			event.stopEvent(this, false);
		}
	}
	
	public void deleteAllProfilesFromService()
	{
		profileList.clear();
		if (eventList == null)
			eventList = getEventList();
		// unlink profiles from events
		for (Event event : eventList)
		{
			event._fkProfile = 0;
			event.stopEvent(this, false);
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
	
	public void reloadEventsData()
	{
		invalidateEventList();
		getEventList();
	}
	
//---------------------------------------------------
	
	public List<EventTimeline> getEventTimelineList()
	{
		if (eventTimelineList == null)
		{
			eventTimelineList = getDatabaseHandler().getAllEventTimelines();
		}

		return eventTimelineList;
	}
	
	public void invalidateEventTimelineList()
	{
		if (eventTimelineList != null)
			eventTimelineList.clear();
		eventTimelineList = null;
	}
	
	public void reloadEventTimelineList()
	{
		invalidateEventTimelineList();
		getEventTimelineList();
	}
	
	
}
