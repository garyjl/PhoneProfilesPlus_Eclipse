package sk.henrichg.phoneprofilesplus;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.Settings;
import android.widget.Toast;

public class DataWrapper {

	public Context context = null;
	private boolean forGUI = false;
	private boolean monochrome = false;
	private int monochromeValue = 0xFF;
	

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
			if (origEvent._type != event._type)
			{
				for (int i = 0; i < eventList.size(); i++)
				{
					if (eventList.get(i)._id == event._id)
					{
						Event newEvent = new Event();
						newEvent.copyEvent(event);
						eventList.set(i, newEvent);
						break;
					}
				}				
			}
			else
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
				(event._fkProfile == profile._id))
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
				(event._fkProfile == profile._id))
				event.stopEvent(this, eventTimelineList, false, true, saveEventStatus);
		}
	}
	
	// pauses all events without activating profiles from Timeline
	public void pauseAllEvents(boolean noSetSystemEvent)
	{
		List<EventTimeline> eventTimelineList = getEventTimelineList();
		
		for (Event event : getEventList())
		{
			if (event.getStatusFromDB(this) == Event.ESTATUS_RUNNING)
				event.pauseEvent(this, eventTimelineList, false, true, noSetSystemEvent);
		}
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
	
	// this is called in boot or start application
	// or when restart alarm triggered (?)
	public void firstStartEvents(boolean invalidateList, boolean ignoreGlobalPref)
	{
		if (invalidateList)
			invalidateEventList();  // force load form db

		List<EventTimeline> eventTimelineList = getEventTimelineList();
		
		for (Event event : getEventList())
		{
			int status = event.getStatus();

			
			// remove all system events
			//event.setSystemEvent(context, Event.ESTATUS_STOP);
			event.stopEvent(this, eventTimelineList, false, ignoreGlobalPref, false);
			
			// reset system event
			//if (status != Event.ESTATUS_STOP)
			//	event.setSystemEvent(context, status);
			if (status != Event.ESTATUS_STOP)
			{
				if (!event.invokeBroadcastReceiver(context))
					event.pauseEvent(this, eventTimelineList, false, ignoreGlobalPref, false);
			}
		}

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

	private void _activateProfile(Profile _profile, int startupSource, boolean _interactive, Activity _activity)
	{
		Profile profile = GlobalData.getMappedProfile(_profile, context);
		profile = filterProfileWithBatteryEvents(profile);
		boolean interactive = _interactive;
		Activity activity = _activity;
		
		databaseHandler.activateProfile(profile);
		setProfileActive(profile);
		
		if ((startupSource != GlobalData.STARTUP_SOURCE_SERVICE) && (startupSource != GlobalData.STARTUP_SOURCE_BOOT))
		{
			// for manual activation pause all running events
			// and setup for next start
			pauseAllEvents(false);
			// block starting battery events
			GlobalData.setBatteryPausedByManualProfileActivation(context, true);
		}
		
		activateProfileHelper.execute(profile, interactive);
		
		activateProfileHelper.showNotification(profile);
		activateProfileHelper.updateWidget();
		
		if (GlobalData.notificationsToast)
		{	
			// toast notification
			Toast msg = Toast.makeText(activity, 
					activity.getResources().getString(R.string.toast_profile_activated_0) + ": " + profile._name + " " +
					activity.getResources().getString(R.string.toast_profile_activated_1), 
					Toast.LENGTH_SHORT);
			msg.show();
		}
		
		// for startActivityForResult
		Intent returnIntent = new Intent();
		returnIntent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile._id);
		returnIntent.getIntExtra(GlobalData.EXTRA_START_APP_SOURCE, startupSource);
		activity.setResult(Activity.RESULT_OK,returnIntent);
		
		finishActivity(startupSource, true, activity);
	}
	
	private void activateProfileWithAlert(Profile profile, int startupSource, boolean interactive, Activity activity)
	{
		// set theme and language for dialog alert ;-)
		// not working on Android 2.3.x
		GUIData.setTheme(activity, true);
		GUIData.setLanguage(activity.getBaseContext());

		if ((GlobalData.applicationActivateWithAlert && interactive) ||
			(startupSource == GlobalData.STARTUP_SOURCE_EDITOR))	
		{	
			final Profile _profile = profile;
			final boolean _interactive = interactive;
			final int _startupSource = startupSource;
			final Activity _activity = activity;

			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
			dialogBuilder.setTitle(activity.getResources().getString(R.string.profile_string_0) + ": " + profile._name);
			dialogBuilder.setMessage(activity.getResources().getString(R.string.activate_profile_alert_message) + "?");
			//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
			dialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					_activateProfile(_profile, _startupSource, _interactive, _activity);
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
			_activateProfile(profile, startupSource, interactive, activity);
		}
	}

	private void finishActivity(int startupSource, boolean activate, Activity _activity)
	{
		final Activity activity = _activity;
		
		boolean finish = true;
		boolean sleep = true;
		
		if (startupSource == GlobalData.STARTUP_SOURCE_ACTIVATOR)
		{
			finish = false;
			if (GlobalData.applicationClose)
			{	
				// ma sa zatvarat aktivita po aktivacii
				
				if (GlobalData.getApplicationStarted(activity.getBaseContext()))
					// aplikacia je uz spustena, mozeme aktivitu zavriet
					// tymto je vyriesene, ze pri spusteni aplikacie z launchera
					// sa hned nezavrie
					finish = activate;
			}
			else
				// nerobit sleep, lebo aktivita zostane otvorena 
				sleep = false;
		}
		
		if (finish)
		{
			if (sleep)
			{
				Thread t = new Thread(new Runnable() {
		            public void run() {
		                try {
		                    Thread.sleep(500);
		                } catch (InterruptedException e) {
		                    System.out.println(e);
		                }
		                activity.finish();
		            }
		        });
				t.start();
			}
			else
                activity.finish();
		}
	}
	
	public void activateProfile(long profile_id, int startupSource, Activity activity)
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
			(startupSource == GlobalData.STARTUP_SOURCE_SERVICE))
		{
			// aktivita spustena z shortcutu alebo zo service, profil aktivujeme
			actProfile = true;
			interactive = ((startupSource != GlobalData.STARTUP_SOURCE_SERVICE));
		}
		else
		if (startupSource == GlobalData.STARTUP_SOURCE_BOOT)	
		{
			// aktivita bola spustena po boote telefonu
			
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
					
					Event event = getEventById(eventTimeline._fkEvent);
					profile = getProfileById(event._fkProfile);
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
			// aktivita bola spustena po boote telefonu
			
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
			
		//Log.d("BackgroundActivateProfileActivity.onStart", "actProfile="+String.valueOf(actProfile));

		if ((startupSource == GlobalData.STARTUP_SOURCE_SHORTCUT) ||
			(startupSource == GlobalData.STARTUP_SOURCE_WIDGET) ||
			(startupSource == GlobalData.STARTUP_SOURCE_ACTIVATOR) ||
			(startupSource == GlobalData.STARTUP_SOURCE_EDITOR) ||
			(startupSource == GlobalData.STARTUP_SOURCE_SERVICE) ||
			(startupSource == GlobalData.STARTUP_SOURCE_LAUNCHER_START))	
		{
			if (profile_id == 0)
				profile = null;
			else
				profile = getProfileById(profile_id);

			//Log.d("BackgroundActivateProfileActivity.onStart","_iconBitmap="+String.valueOf(profile._iconBitmap));
			//Log.d("BackgroundActivateProfileActivity.onStart","_preferencesIndicator="+String.valueOf(profile._preferencesIndicator));
		}

		
		if (actProfile && (profile != null))
		{
			// aktivacia profilu
			activateProfileWithAlert(profile, startupSource, interactive, activity);
		}
		else
		{
			activateProfileHelper.showNotification(profile);
			activateProfileHelper.updateWidget();

			// for startActivityForResult
			Intent returnIntent = new Intent();
			returnIntent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile_id);
			returnIntent.getIntExtra(GlobalData.EXTRA_START_APP_SOURCE, startupSource);
			activity.setResult(Activity.RESULT_OK,returnIntent);
			
			finishActivity(startupSource, true, activity);
		}
		
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
							   profile._volumeSpeakerPhone);
		
			List<EventTimeline> eventTimelineList = getEventTimelineList();
			
			// search from last events in timeline
			for (int i = eventTimelineList.size()-1; i >= 0; i--)
			{
				EventTimeline eventTimeline = eventTimelineList.get(i);
				
				Event event = getEventById(eventTimeline._fkEvent);

				if ((event != null) && (event._type == Event.ETYPE_BATTERY))
				{
					EventPreferencesBattery eventPreferences = (EventPreferencesBattery)event._eventPreferences;
					if (eventPreferences._detectorType == EventPreferencesBattery.DETECTOR_TYPE_LOW_LEVEL)
					{
						Profile eventProfile = getProfileById(event._fkProfile);
						
						if (filteredProfile._id == eventProfile._id)
							break;
						
						// preferences which event profile change, must by set as "no change" for filtered profile 
						
						if (eventProfile._volumeRingerMode != 0)
							filteredProfile._volumeRingerMode = 0;
						if (eventProfile.getVolumeRingtoneChange())
							filteredProfile._volumeRingtone = "0|0|0";
						if (profile.getVolumeNotificationChange())
							filteredProfile._volumeNotification = "0|0|0";
						if (profile.getVolumeAlarmChange())
							filteredProfile._volumeAlarm = "0|0|0";
						if (profile.getVolumeMediaChange())
							filteredProfile._volumeMedia = "0|0|0";
						if (profile.getVolumeSystemChange())
							filteredProfile._volumeSystem = "0|0|0";
						if (profile.getVolumeVoiceChange())
							filteredProfile._volumeVoice = "0|0|";
						if (profile._soundRingtoneChange != 0)
							filteredProfile._soundRingtoneChange = 0;
						if (profile._soundNotificationChange != 0)
							filteredProfile._soundNotificationChange = 0;
						if (profile._soundAlarmChange != 0)
							filteredProfile._soundAlarmChange = 0;
						if (profile._deviceAirplaneMode != 0)
							filteredProfile._deviceAirplaneMode = 0;
						if (profile._deviceAutosync != 0)
							filteredProfile._deviceAutosync = 0;
						if (profile._deviceMobileData != 0)
							filteredProfile._deviceMobileData = 0;
						if (profile._deviceMobileDataPrefs != 0)
							filteredProfile._deviceMobileDataPrefs = 0;
						if (profile._deviceWiFi != 0)
							filteredProfile._deviceWiFi = 0;
						if (profile._deviceBluetooth != 0)
							filteredProfile._deviceBluetooth = 0;
						if (profile._deviceGPS != 0)
							filteredProfile._deviceGPS = 0;
						if (profile._deviceLocationServicePrefs != 0)
							filteredProfile._deviceLocationServicePrefs = 0;
						if (profile._deviceScreenTimeout != 0)
							filteredProfile._deviceScreenTimeout = 0;
						if (profile.getDeviceBrightnessChange() || profile.getDeviceBrightnessAutomatic())
							filteredProfile._deviceBrightness = "0|0|0|0";
						if (profile._deviceAutoRotate != 0)
							filteredProfile._deviceAutoRotate = 0;
						if (profile._deviceRunApplicationChange != 0)
							filteredProfile._deviceRunApplicationChange = 0;
						if (profile._deviceWallpaperChange != 0)
							filteredProfile._deviceWallpaperChange = 0;
						if (profile._volumeSpeakerPhone != 0)
							filteredProfile._volumeSpeakerPhone = 0;
						
						// last event finded
						break;
					}
				}
			}
			
			return filteredProfile;
		}
		else 
			return profile;
		
	}
	
}
