package sk.henrichg.phoneprofiles;
 
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
 
public class EventPreferencesFragment extends PreferenceListFragment 
										implements SharedPreferences.OnSharedPreferenceChangeListener
{
	
	private Event event;
	private int event_position;
	private PreferenceManager prefMng;
	private SharedPreferences preferences;
	private Context context;
	private ActionMode actionMode;
	private Callback actionModeCallback;
	
	private boolean restart; 
	
	private static Activity preferencesActivity = null;
		
	static final String PREFS_NAME = "event_preferences";
	
	private OnRestartEventPreferences onRestartEventPreferencesCallback = sDummyOnRestartEventPreferencesCallback;
	private OnRedrawListFragment onRedrawListFragmentCallback = sDummyOnRedrawListFragmentCallback;

	public interface OnRestartEventPreferences {
		/**
		 * Callback for restart fragment.
		 */
		public void onRestartEventPreferences(int position);
	}

	private static OnRestartEventPreferences sDummyOnRestartEventPreferencesCallback = new OnRestartEventPreferences() {
		public void onRestartEventPreferences(int position) {
		}
	};
	
	public interface OnRedrawListFragment {
		/**
		 * Callback for redraw event list fragment.
		 */
		public void onRedrawListFragment();
	}

	private static OnRedrawListFragment sDummyOnRedrawListFragmentCallback = new OnRedrawListFragment() {
		public void onRedrawListFragment() {
		}
	};
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (!(activity instanceof OnRestartEventPreferences)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		onRestartEventPreferencesCallback = (OnRestartEventPreferences) activity;
		
		if (!(activity instanceof OnRedrawListFragment)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		onRedrawListFragmentCallback = (OnRedrawListFragment) activity;
		
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		onRestartEventPreferencesCallback = sDummyOnRestartEventPreferencesCallback;
		onRedrawListFragmentCallback = sDummyOnRedrawListFragmentCallback;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		preferencesActivity = getSherlockActivity();
		
        // getting attached fragment data
		if (getArguments().containsKey(GlobalData.EXTRA_EVENT_POSITION))
			event_position = getArguments().getInt(GlobalData.EXTRA_EVENT_POSITION);
    	//Log.d("EventPreferencesFragment.onCreate", "event_position=" + event_position);
		
        context = getSherlockActivity().getBaseContext();
        
        loadPreferences();
        
		prefMng = getPreferenceManager();
		prefMng.setSharedPreferencesName(PREFS_NAME);
		prefMng.setSharedPreferencesMode(Activity.MODE_PRIVATE);
		
		addPreferencesFromResource(R.xml.event_preferences);

        preferences = prefMng.getSharedPreferences();
        
        preferences.registerOnSharedPreferenceChangeListener(this);  
        
        createActionMode();
        

    	//Log.d("EventPreferencesFragment.onCreate", "xxxx");
    }
	
	@Override
	public void onStart()
	{
		super.onStart();

		updateSharedPreference();
		
    	//Log.d("EventPreferencesFragment.onStart", preferences.getString(PREF_EVENT_NAME, ""));

	}
	
	@Override
	public void onPause()
	{
		super.onPause();

		if (actionMode != null)
		{
			restart = false; // nerestartovat fragment
			actionMode.finish();
		}
		
    	//Log.d("EventPreferencesFragment.onPause", "xxxx");
		
	}
	
	@Override
	public void onDestroy()
	{
        preferences.unregisterOnSharedPreferenceChangeListener(this);        
		super.onDestroy();
	}

	public void doOnActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		doOnActivityResult(requestCode, resultCode, data);
	}
	
	private void loadPreferences()
	{
		
    	event = (Event)EditorProfilesActivity.profilesDataWrapper.getEventList().get(event_position);
    	
    	if (event != null)
    	{
	    	SharedPreferences preferences = getSherlockActivity().getSharedPreferences(EventPreferencesFragment.PREFS_NAME, Activity.MODE_PRIVATE);
	
	    	Editor editor = preferences.edit();
	    	/*
	        editor.putString(GlobalData.PREF_PROFILE_NAME, profile._name);
	        editor.putString(GlobalData.PREF_PROFILE_ICON, profile._icon);
	        editor.putString(GlobalData.PREF_PROFILE_VOLUME_RINGER_MODE, Integer.toString(profile._volumeRingerMode));
	        editor.putString(GlobalData.PREF_PROFILE_VOLUME_RINGTONE, profile._volumeRingtone);
	        editor.putString(GlobalData.PREF_PROFILE_VOLUME_NOTIFICATION, profile._volumeNotification);
	        editor.putString(GlobalData.PREF_PROFILE_VOLUME_MEDIA, profile._volumeMedia);
	        editor.putString(GlobalData.PREF_PROFILE_VOLUME_ALARM, profile._volumeAlarm);
	        editor.putString(GlobalData.PREF_PROFILE_VOLUME_SYSTEM, profile._volumeSystem);
	        editor.putString(GlobalData.PREF_PROFILE_VOLUME_VOICE, profile._volumeVoice);
	        editor.putBoolean(GlobalData.PREF_PROFILE_SOUND_RINGTONE_CHANGE, profile._soundRingtoneChange);
	        editor.putString(GlobalData.PREF_PROFILE_SOUND_RINGTONE, profile._soundRingtone);
	        editor.putBoolean(GlobalData.PREF_PROFILE_SOUND_NOTIFICATION_CHANGE, profile._soundNotificationChange);
	        editor.putString(GlobalData.PREF_PROFILE_SOUND_NOTIFICATION, profile._soundNotification);
	        editor.putBoolean(GlobalData.PREF_PROFILE_SOUND_ALARM_CHANGE, profile._soundAlarmChange);
	        editor.putString(GlobalData.PREF_PROFILE_SOUND_ALARM, profile._soundAlarm);
	        editor.putString(GlobalData.PREF_PROFILE_DEVICE_AIRPLANE_MODE, Integer.toString(profile._deviceAirplaneMode));
	        editor.putString(GlobalData.PREF_PROFILE_DEVICE_WIFI, Integer.toString(profile._deviceWiFi));
	        editor.putString(GlobalData.PREF_PROFILE_DEVICE_BLUETOOTH, Integer.toString(profile._deviceBluetooth));
	        editor.putString(GlobalData.PREF_PROFILE_DEVICE_SCREEN_TIMEOUT, Integer.toString(profile._deviceScreenTimeout));
	        editor.putString(GlobalData.PREF_PROFILE_DEVICE_BRIGHTNESS, profile._deviceBrightness);
	        editor.putBoolean(GlobalData.PREF_PROFILE_DEVICE_WALLPAPER_CHANGE, profile._deviceWallpaperChange);
	        editor.putString(GlobalData.PREF_PROFILE_DEVICE_WALLPAPER, profile._deviceWallpaper);
	        editor.putString(GlobalData.PREF_PROFILE_DEVICE_MOBILE_DATA, Integer.toString(profile._deviceMobileData));
	        editor.putBoolean(GlobalData.PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS, profile._deviceMobileDataPrefs);
	        editor.putString(GlobalData.PREF_PROFILE_DEVICE_GPS, Integer.toString(profile._deviceGPS));
	        editor.putBoolean(GlobalData.PREF_PROFILE_DEVICE_RUN_APPLICATION_CHANGE, profile._deviceRunApplicationChange);
	        editor.putString(GlobalData.PREF_PROFILE_DEVICE_RUN_APPLICATION_PACKAGE_NAME, profile._deviceRunApplicationPackageName);
	        editor.putBoolean(GlobalData.PREF_PROFILE_SHOW_IN_ACTIVATOR, profile._showInActivator);
	        */
			editor.commit();
    	}
		
	}
	
	private void savePreferences()
	{
        if (event_position > -1) 
        {
        	/*
        	profile._name = preferences.getString(GlobalData.PREF_PROFILE_NAME, "");
        	profile._icon = preferences.getString(GlobalData.PREF_PROFILE_ICON, "");
        	profile._volumeRingerMode = Integer.parseInt(preferences.getString(GlobalData.PREF_PROFILE_VOLUME_RINGER_MODE, ""));
        	profile._volumeRingtone = preferences.getString(GlobalData.PREF_PROFILE_VOLUME_RINGTONE, "");
        	profile._volumeNotification = preferences.getString(GlobalData.PREF_PROFILE_VOLUME_NOTIFICATION, "");
        	profile._volumeMedia = preferences.getString(GlobalData.PREF_PROFILE_VOLUME_MEDIA, "");
        	profile._volumeAlarm = preferences.getString(GlobalData.PREF_PROFILE_VOLUME_ALARM, "");
        	profile._volumeSystem = preferences.getString(GlobalData.PREF_PROFILE_VOLUME_SYSTEM, "");
        	profile._volumeVoice = preferences.getString(GlobalData.PREF_PROFILE_VOLUME_VOICE, "");
        	profile._soundRingtoneChange = preferences.getBoolean(GlobalData.PREF_PROFILE_SOUND_RINGTONE_CHANGE, false);
        	profile._soundRingtone = preferences.getString(GlobalData.PREF_PROFILE_SOUND_RINGTONE, "");
        	profile._soundNotificationChange = preferences.getBoolean(GlobalData.PREF_PROFILE_SOUND_NOTIFICATION_CHANGE, false);
        	profile._soundNotification = preferences.getString(GlobalData.PREF_PROFILE_SOUND_NOTIFICATION, "");
        	profile._soundAlarmChange = preferences.getBoolean(GlobalData.PREF_PROFILE_SOUND_ALARM_CHANGE, false);
        	profile._soundAlarm = preferences.getString(GlobalData.PREF_PROFILE_SOUND_ALARM, "");
        	profile._deviceAirplaneMode = Integer.parseInt(preferences.getString(GlobalData.PREF_PROFILE_DEVICE_AIRPLANE_MODE, ""));
        	profile._deviceWiFi = Integer.parseInt(preferences.getString(GlobalData.PREF_PROFILE_DEVICE_WIFI, ""));
        	profile._deviceBluetooth = Integer.parseInt(preferences.getString(GlobalData.PREF_PROFILE_DEVICE_BLUETOOTH, ""));
        	profile._deviceScreenTimeout = Integer.parseInt(preferences.getString(GlobalData.PREF_PROFILE_DEVICE_SCREEN_TIMEOUT, ""));
        	profile._deviceBrightness = preferences.getString(GlobalData.PREF_PROFILE_DEVICE_BRIGHTNESS, "");
        	profile._deviceWallpaperChange = preferences.getBoolean(GlobalData.PREF_PROFILE_DEVICE_WALLPAPER_CHANGE, false);
        	if (profile._deviceWallpaperChange)
        		profile._deviceWallpaper = preferences.getString(GlobalData.PREF_PROFILE_DEVICE_WALLPAPER, "");
        	else
        		profile._deviceWallpaper = "-|0";
        	profile._deviceMobileData = Integer.parseInt(preferences.getString(GlobalData.PREF_PROFILE_DEVICE_MOBILE_DATA, ""));
        	profile._deviceMobileDataPrefs = preferences.getBoolean(GlobalData.PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS, false);
        	profile._deviceGPS = Integer.parseInt(preferences.getString(GlobalData.PREF_PROFILE_DEVICE_GPS, ""));
        	profile._deviceRunApplicationChange = preferences.getBoolean(GlobalData.PREF_PROFILE_DEVICE_RUN_APPLICATION_CHANGE, false);
        	if (profile._deviceRunApplicationChange)
        		profile._deviceRunApplicationPackageName = preferences.getString(GlobalData.PREF_PROFILE_DEVICE_RUN_APPLICATION_PACKAGE_NAME, "-");
        	else
        		profile._deviceRunApplicationPackageName = "-";
        	profile._showInActivator = preferences.getBoolean(GlobalData.PREF_PROFILE_SHOW_IN_ACTIVATOR, true);
			*/
        	
			EditorProfilesActivity.profilesDataWrapper.getDatabaseHandler().updateEvent(event);
        	
        	//Log.d("EventPreferencesFragment.onPause", "updateEvent");

        }

        onRedrawListFragmentCallback.onRedrawListFragment();
	}
	
	private void setSummary(String key, Object value)
	{
		//Log.d("EventPreferencesFragment.setSummary",key);
		/*
		if (key.equals(GlobalData.PREF_PROFILE_NAME))
		{	
	        prefMng.findPreference(key).setSummary(value.toString());
		}
		if (key.equals(GlobalData.PREF_PROFILE_VOLUME_RINGER_MODE))
		{
			String sPrefVolumeMode = value.toString();
			int iPrefVolumeMode;
			try {
				iPrefVolumeMode = Integer.parseInt(sPrefVolumeMode);
			} catch (Exception e) {
				iPrefVolumeMode = 0;
			}
			String[] prefVolumeModes = getResources().getStringArray(R.array.ringerModeArray);
			prefMng.findPreference(key).setSummary(prefVolumeModes[iPrefVolumeMode]);
		}
		if (key.equals(GlobalData.PREF_PROFILE_SOUND_RINGTONE) ||
			key.equals(GlobalData.PREF_PROFILE_SOUND_NOTIFICATION) ||
			key.equals(GlobalData.PREF_PROFILE_SOUND_ALARM))
		{
			String ringtoneUri = value.toString();
			
			//Log.d("ProfilePreferencesFragment.setSummary", ringtoneUri);
			
			Uri uri = Uri.parse(ringtoneUri);
			Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
			String ringtoneName;
			if (ringtone == null)
				ringtoneName = "[no ringtones]";
			else
				ringtoneName = ringtone.getTitle(context);
			
	        prefMng.findPreference(key).setSummary(ringtoneName);
		}
		if (key.equals(GlobalData.PREF_PROFILE_DEVICE_AIRPLANE_MODE) || 
			key.equals(GlobalData.PREF_PROFILE_DEVICE_WIFI) ||
			key.equals(GlobalData.PREF_PROFILE_DEVICE_BLUETOOTH) ||
			key.equals(GlobalData.PREF_PROFILE_DEVICE_MOBILE_DATA) ||
			key.equals(GlobalData.PREF_PROFILE_DEVICE_GPS))
		{
			boolean canChange = GlobalData.hardwareCheck(key, context);
			if (!canChange)
			{
				prefMng.findPreference(key).setEnabled(false);
				prefMng.findPreference(key).setSummary(getResources().getString(R.string.profile_preferences_device_not_allowed));
				if (key.equals(GlobalData.PREF_PROFILE_DEVICE_MOBILE_DATA))
				{
					prefMng.findPreference(GlobalData.PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS).setEnabled(false);
				}
			}
			else
			{
				String sPrefDeviceMode = value.toString();
				int iPrefDeviceMode;
				try {
					iPrefDeviceMode = Integer.parseInt(sPrefDeviceMode);
				} catch (Exception e) {
					iPrefDeviceMode = 0;
				}
				String[] PrefDeviceModes = getResources().getStringArray(R.array.hardwareModeArray);
				prefMng.findPreference(key).setSummary(PrefDeviceModes[iPrefDeviceMode]);
			}
			
		}
		if (key.equals(GlobalData.PREF_PROFILE_DEVICE_SCREEN_TIMEOUT))
		{
			String sPrefScreenTimeout = value.toString();
			int iPrefScreenTimeout;
			try {
				iPrefScreenTimeout = Integer.parseInt(sPrefScreenTimeout);
			} catch (Exception e) {
				iPrefScreenTimeout = 0;
			}
			String[] PrefScreenTimeouts = getResources().getStringArray(R.array.screenTimeoutArray);
			prefMng.findPreference(key).setSummary(PrefScreenTimeouts[iPrefScreenTimeout]);
		}
		*/
		
	}
	
	private void updateSharedPreference()
	{
        if (event_position > -1) 
        {	

	    	// updating activity with selected event preferences
	    	
        	//Log.d("PhonePreferencesActivity.updateSharedPreference", event.getName());
        	/*
	        setSummary(GlobalData.PREF_PROFILE_NAME, profile._name);
	        setSummary(GlobalData.PREF_PROFILE_VOLUME_RINGER_MODE, profile._volumeRingerMode);
	        setSummary(GlobalData.PREF_PROFILE_SOUND_RINGTONE, profile._soundRingtone);
	        setSummary(GlobalData.PREF_PROFILE_SOUND_NOTIFICATION, profile._soundNotification);
	        setSummary(GlobalData.PREF_PROFILE_SOUND_ALARM, profile._soundAlarm);
	        setSummary(GlobalData.PREF_PROFILE_DEVICE_AIRPLANE_MODE, profile._deviceAirplaneMode);
	        setSummary(GlobalData.PREF_PROFILE_DEVICE_WIFI, profile._deviceWiFi);
	        setSummary(GlobalData.PREF_PROFILE_DEVICE_BLUETOOTH, profile._deviceBluetooth);
	        setSummary(GlobalData.PREF_PROFILE_DEVICE_SCREEN_TIMEOUT, profile._deviceScreenTimeout);
	        setSummary(GlobalData.PREF_PROFILE_DEVICE_MOBILE_DATA, profile._deviceMobileData);
	        setSummary(GlobalData.PREF_PROFILE_DEVICE_GPS, profile._deviceGPS);
	        */
			
        }
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		/*
    	if (!(key.equals(GlobalData.PREF_PROFILE_SOUND_RINGTONE_CHANGE) ||
	    		key.equals(GlobalData.PREF_PROFILE_SOUND_NOTIFICATION_CHANGE) ||
	    		key.equals(GlobalData.PREF_PROFILE_SOUND_ALARM_CHANGE) ||
	    		key.equals(GlobalData.PREF_PROFILE_DEVICE_WALLPAPER_CHANGE) ||
	    		key.equals(GlobalData.PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS) || 
	    		key.equals(GlobalData.PREF_PROFILE_DEVICE_RUN_APPLICATION_CHANGE) ||
	    		key.equals(GlobalData.PREF_PROFILE_SHOW_IN_ACTIVATOR) 
	    		))
	    		setSummary(key, sharedPreferences.getString(key, ""));
	    */
    	
        if (actionMode == null)
        {
        	
        	restart = true;
        	
        	LayoutInflater inflater = LayoutInflater.from(getSherlockActivity());
        	View actionView = inflater.inflate(R.layout.event_preferences_action_mode, null);

            actionMode = getSherlockActivity().startActionMode(actionModeCallback);
            actionMode.setCustomView(actionView); 
            
            actionMode.getCustomView().findViewById(R.id.event_preferences_action_menu_cancel).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					//Log.d("actionMode.onClick", "cancel");
					
					actionMode.finish();
					
				}
           	});

            actionMode.getCustomView().findViewById(R.id.event_preferences_action_menu_save).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					//Log.d("actionMode.onClick", "save");
			
					savePreferences();
					
					restart = false; // nerestartovat fragment
					actionMode.finish();
					
				}
           	});
        }
	}
	
	private void createActionMode()
	{
		actionModeCallback = new ActionMode.Callback() {
			 
            /** Invoked whenever the action mode is shown. This is invoked immediately after onCreateActionMode */
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
 
            /** Called when user exits action mode */
            public void onDestroyActionMode(ActionMode mode) {
               actionMode = null;
               if (restart)
            	   onRestartEventPreferencesCallback.onRestartEventPreferences(event_position);
            }
 
            /** This is called when the action mode is created. This is called by startActionMode() */
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                //mode.setTitle(R.string.phone_preferences_actionmode_title);
                //getSherlockActivity().getSupportMenuInflater().inflate(R.menu.context_menu, menu);
                return true;
            }
 
            /** This is called when an item in the context menu is selected */
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            /*    switch(item.getItemId()){
                    case R.id.action1:
                        Toast.makeText(getBaseContext(), "Selected Action1 ", Toast.LENGTH_LONG).show();
                        mode.finish();  // Automatically exists the action mode, when the user selects this action
                        break;
                    case R.id.action2:
                        Toast.makeText(getBaseContext(), "Selected Action2 ", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.action3:
                        Toast.makeText(getBaseContext(), "Selected Action3 ", Toast.LENGTH_LONG).show();
                        break;
                }  */
                return false;
            }

        };		
	}
	
	public void finishActionMode()
	{
		if (actionMode != null)
		{	
			restart = true;
			actionMode.finish();
		}
	}

	static public Activity getPreferencesActivity()
	{
		return preferencesActivity;
	}
	
}