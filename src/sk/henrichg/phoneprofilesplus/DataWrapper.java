package sk.henrichg.phoneprofilesplus;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
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
	
	public void activateProfileFromEvent(long profile_id, String eventNotificationSound)
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
		_activateProfile(getProfileById(profile_id), GlobalData.STARTUP_SOURCE_SERVICE, false, null, eventNotificationSound);
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
				event._fkProfileEnd = Event.PROFILE_END_ACTIVATED;
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
			event._fkProfileEnd = Event.PROFILE_END_ACTIVATED;
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
			}
			GlobalData.setEventsBlocked(context, blockEvents);
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
		BatteryEventsAlarmBroadcastReceiver.removeAlarm(context);
	}
	
	// this is called in boot or start application
	// or when restart alarm triggered (?)
	public void firstStartEvents(boolean invalidateList, boolean ignoreGlobalPref)
	{
		if (invalidateList)
			invalidateEventList();  // force load form db

		GlobalData.setEventsBlocked(context, false);
		getDatabaseHandler().unblockAllEvents();
		
		BatteryEventsAlarmBroadcastReceiver.removeAlarm(context);
		
		for (Event event : getEventList())
		{
			event._blocked = false;
			
			int status = event.getStatus();
			
			// remove all system events
			event.setSystemEvent(context, Event.ESTATUS_STOP);
			
			// reset system event
			if (status != Event.ESTATUS_STOP)
			{
				event.setSystemEvent(context, status);
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

	private void _activateProfile(Profile _profile, int startupSource, boolean _interactive, 
									Activity _activity, String eventNotificationSound)
	{
		Profile profile = GlobalData.getMappedProfile(_profile, context);
		profile = filterProfileWithBatteryEvents(profile);
		
		boolean interactive = _interactive;
		final Activity activity = _activity;

		if ((startupSource != GlobalData.STARTUP_SOURCE_SERVICE) && 
			(startupSource != GlobalData.STARTUP_SOURCE_BOOT) &&
			(startupSource != GlobalData.STARTUP_SOURCE_LAUNCHER_START))
		{
			pauseAllEvents(false, true);
			// search for forceRun events
			getEventList();
			for (Event event : eventList)
			{
				if (event != null)
				{
					if (event._forceRun)
					{
						// temporary block forceRun event
						setEventBlocked(event, true);
					}
				}
			}
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
		
		//if (interactive || (startupSource == GlobalData.STARTUP_SOURCE_EDITOR))
		if (interactive)
		{
			// search for forceRun events
			getEventList();
			for (Event event : eventList)
			{
				if (event != null)
				{
					if (event._forceRun)
					{
						isforceRunEvent = true;
						break;
					}
				}
			}
		}
		
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
				dialogBuilder.setMessage(activity.getResources().getString(R.string.manual_profile_activation_forceRun_message) + "?");
			else
				dialogBuilder.setMessage(activity.getResources().getString(R.string.activate_profile_alert_message) + "?");
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
			
		//Log.d("BackgroundActivateProfileActivity.onStart", "actProfile="+String.valueOf(actProfile));

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

			//Log.d("BackgroundActivateProfileActivity.onStart","_iconBitmap="+String.valueOf(profile._iconBitmap));
			//Log.d("BackgroundActivateProfileActivity.onStart","_preferencesIndicator="+String.valueOf(profile._preferencesIndicator));
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

	public boolean doEventService(Event event, boolean restartEvent)
	{
		int newEventStatus = Event.ESTATUS_NONE;

		boolean eventStart = true;
		
		boolean timePassed = true;
		boolean batteryPassed = true;
		boolean callPassed = true;
		
		boolean isCharging = false;
		float batteryPct = 100.0f;
		
		boolean phoneNumberFinded = false;
		
		if (event._eventPreferencesTime._enabled)
		{
			// compute start datetime
   			long startAlarmTime;
   			long endAlarmTime;
			int daysToAdd;
			
			daysToAdd = event._eventPreferencesTime.computeDaysForAdd(true, true);
			startAlarmTime = event._eventPreferencesTime.computeAlarm(true, daysToAdd);
			
   		    String alarmTimeS = DateFormat.getDateFormat(context).format(startAlarmTime) +
	   		    	  		    " " + DateFormat.getTimeFormat(context).format(startAlarmTime);
			GlobalData.logE("DataWrapper.doEventService","startAlarmTime="+alarmTimeS);
			
			daysToAdd = event._eventPreferencesTime.computeDaysForAdd(false, true);
			endAlarmTime = event._eventPreferencesTime.computeAlarm(false, daysToAdd);

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
			
			int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			GlobalData.logE("DataWrapper.doEventService","status="+status);
			isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
			             status == BatteryManager.BATTERY_STATUS_FULL;
			GlobalData.logE("DataWrapper.doEventService","isCharging="+isCharging);
			
			int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			
			batteryPct = level / (float)scale;	
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
			if (EventsService.callEventType != PhoneCallBroadcastReceiver.CALL_EVENT_UNDEFINED)
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
									String phoneNumber = phones.getString(phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));
									//Log.e("DataWrapper.doEventService","phoneNumber="+phoneNumber);
									//Log.e("DataWrapper.doEventService","EventsService.phoneNumber="+EventsService.phoneNumber);
									if (PhoneNumberUtils.compare(phoneNumber, EventsService.phoneNumber))
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
						if (EventsService.callEventType == PhoneCallBroadcastReceiver.CALL_EVENT_INCOMING_CALL_RINGING)
							eventStart = eventStart && true;
						else
							callPassed = false;
					}
					else
					if (event._eventPreferencesCall._callEvent == EventPreferencesCall.CALL_EVENT_INCOMING_CALL_ANSWERED)
					{
						if (EventsService.callEventType == PhoneCallBroadcastReceiver.CALL_EVENT_INCOMING_CALL_ANSWERED)
							eventStart = eventStart && true;
						else	
							callPassed = false;
					}
					else
					if (event._eventPreferencesCall._callEvent == EventPreferencesCall.CALL_EVENT_OUTGOING_CALL_STARTED)
					{
						if (EventsService.callEventType == PhoneCallBroadcastReceiver.CALL_EVENT_OUTGOING_CALL_STARTED)
							eventStart = eventStart && true;
						else
							callPassed = false;
					}
					
					if ((EventsService.callEventType == PhoneCallBroadcastReceiver.CALL_EVENT_INCOMING_CALL_ENDED) ||
						(EventsService.callEventType == PhoneCallBroadcastReceiver.CALL_EVENT_OUTGOING_CALL_ENDED))
					{
						callPassed = true;
						eventStart = eventStart && false;
						EventsService.callEventType = PhoneCallBroadcastReceiver.CALL_EVENT_UNDEFINED;
						EventsService.phoneNumber = "";
					}
				}
				else
					callPassed = false;
				
			}
			else
				callPassed = false;
		}
		
		GlobalData.logE("DataWrapper.doEventService","timePassed="+timePassed);
		GlobalData.logE("DataWrapper.doEventService","batteryPassed="+batteryPassed);
		GlobalData.logE("DataWrapper.doEventService","callPassed="+callPassed);

		GlobalData.logE("DataWrapper.doEventService","eventStart="+eventStart);
		
		List<EventTimeline> eventTimelineList = getEventTimelineList();
		
		if (timePassed && batteryPassed && callPassed)
		{
			// podmienky sedia, vykoname, co treba

			if (eventStart)
				newEventStatus = Event.ESTATUS_RUNNING;
			else
				newEventStatus = Event.ESTATUS_PAUSE;
			
		}
		else
			newEventStatus = Event.ESTATUS_PAUSE;

		if ((event.getStatus() != newEventStatus) || restartEvent)
		{
			if (newEventStatus == Event.ESTATUS_RUNNING)
			{
				GlobalData.logE("DataWrapper.doEventService","start event");
				event.startEvent(this, eventTimelineList, restartEvent, false);
			}
			else
			if (newEventStatus == Event.ESTATUS_PAUSE)
			{
				GlobalData.logE("DataWrapper.doEventService","pause event");
				event.pauseEvent(this, eventTimelineList, true, false, false);
			}
		
			// refresh GUI
			Intent refreshIntent = new Intent();
			refreshIntent.setAction(RefreshGUIBroadcastReceiver.INTENT_REFRESH_GUI);
			context.sendBroadcast(refreshIntent);
		}
		
		return (timePassed && batteryPassed && callPassed);
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

	public void restartEvents()
	{
		if (!GlobalData.getGlobalEventsRuning(context))
			// events are globally stopped
			return;
		
		GlobalData.setEventsBlocked(context, false);
		getDatabaseHandler().unblockAllEvents();
		
		getEventList();
		for (Event event : eventList)
		{
			event._blocked = false;
			if (event.getStatus() != Event.ESTATUS_STOP)
				doEventService(event, true);
		}
	}
	
	public void setEventBlocked(Event event, boolean blocked)
	{
		event._blocked = blocked;
		getDatabaseHandler().updateEventBlocked(event);
	}
}
