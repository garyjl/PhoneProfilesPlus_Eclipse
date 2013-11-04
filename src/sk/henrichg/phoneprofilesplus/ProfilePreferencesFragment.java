package sk.henrichg.phoneprofilesplus;
 
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
 
public class ProfilePreferencesFragment extends PreferenceListFragment 
										implements SharedPreferences.OnSharedPreferenceChangeListener
{
	
	private Profile profile;
	public long profile_id;
	private boolean first_start_activity;
	private boolean new_profile;
	public boolean profileNonEdited = true;
	private PreferenceManager prefMng;
	private SharedPreferences preferences;
	private Context context;
	private ActionMode actionMode;
	private Callback actionModeCallback;
	
	private int actionModeButtonClicked = BUTTON_UNDEFINED;
	
	private static ImageViewPreference changedImageViewPreference;
	private static Activity preferencesActivity = null;
		
	static final String PREFS_NAME = "profile_preferences";
	
	static final int BUTTON_UNDEFINED = 0;
	static final int BUTTON_CANCEL = 1;
	static final int BUTTON_SAVE = 2;
	static final int BUTTON_CANCEL_NO_REFRESH = 3; 
	
	private OnRestartProfilePreferences onRestartProfilePreferencesCallback = sDummyOnRestartProfilePreferencesCallback;
	private OnRedrawProfileListFragment onRedrawProfileListFragmentCallback = sDummyOnRedrawProfileListFragmentCallback;

	// invokes when restart of profile preferences fragment needed (undo preference changes)
	public interface OnRestartProfilePreferences {
		/**
		 * Callback for restart fragment.
		 */
		public void onRestartProfilePreferences(Profile profile);
	}

	private static OnRestartProfilePreferences sDummyOnRestartProfilePreferencesCallback = new OnRestartProfilePreferences() {
		public void onRestartProfilePreferences(Profile profile) {
		}
	};
	
	// invokes when profile list fragment redraw needed (preference changes accepted)
	public interface OnRedrawProfileListFragment {
		/**
		 * Callback for redraw profile list fragment.
		 */
		public void onRedrawProfileListFragment(Profile profile, boolean newProfile);
	}

	private static OnRedrawProfileListFragment sDummyOnRedrawProfileListFragmentCallback = new OnRedrawProfileListFragment() {
		public void onRedrawProfileListFragment(Profile profile, boolean newProfile) {
		}
	};
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (!(activity instanceof OnRestartProfilePreferences)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		onRestartProfilePreferencesCallback = (OnRestartProfilePreferences) activity;
		
		if (!(activity instanceof OnRedrawProfileListFragment)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		onRedrawProfileListFragmentCallback = (OnRedrawProfileListFragment) activity;
		
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		onRestartProfilePreferencesCallback = sDummyOnRestartProfilePreferencesCallback;
		onRedrawProfileListFragmentCallback = sDummyOnRedrawProfileListFragmentCallback;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		preferencesActivity = getSherlockActivity();
		
		prefMng = getPreferenceManager();
		prefMng.setSharedPreferencesName(PREFS_NAME);
		prefMng.setSharedPreferencesMode(Activity.MODE_PRIVATE);
		
        // getting attached fragment data
		if (getArguments().containsKey(GlobalData.EXTRA_NEW_PROFILE))
			new_profile = getArguments().getBoolean(GlobalData.EXTRA_NEW_PROFILE);
		if (getArguments().containsKey(GlobalData.EXTRA_PROFILE_ID))
			profile_id = getArguments().getLong(GlobalData.EXTRA_PROFILE_ID);
    	//Log.e("ProfilePreferencesFragment.onCreate", "profile_id=" + profile_id);
		if (new_profile)
		{
			profile = EditorProfilesActivity.dataWrapper.getNoinitializedProfile(
					getResources().getString(R.string.profile_name_default), 
					GUIData.PROFILE_ICON_DEFAULT, 0); 
			profile._showInActivator = true;
		}
		else
			profile = (Profile)EditorProfilesActivity.dataWrapper.getProfileById(profile_id);
		if (getArguments().containsKey(GlobalData.EXTRA_FIRST_START_ACTIVITY))
		{
			first_start_activity = getArguments().getBoolean(GlobalData.EXTRA_FIRST_START_ACTIVITY);
	        getArguments().remove(GlobalData.EXTRA_FIRST_START_ACTIVITY);
		}
		else
			first_start_activity = false;
        if (first_start_activity)
        	loadPreferences();
    	
        context = getSherlockActivity().getBaseContext();
        
		addPreferencesFromResource(R.xml.profile_preferences);

        preferences = prefMng.getSharedPreferences();
        
        preferences.registerOnSharedPreferenceChangeListener(this);  
        
        createActionMode();
        
        if ((savedInstanceState != null) && savedInstanceState.getBoolean("action_mode_showed", false))
            showActionMode();
        else
        if (new_profile && first_start_activity)
        	showActionMode();

    	//Log.d("ProfilePreferencesFragment.onCreate", "xxxx");
    }
	
	@Override
	public void onStart()
	{
		super.onStart();

		updateSharedPreference();
		
    	//Log.d("ProfilePreferencesFragment.onStart", preferences.getString(PREF_PROFILE_NAME, ""));

    	//Log.d("ProfilePreferencesFragment.onStart", "profile activated="+profile.getChecked());

	}
	
	@Override
	public void onPause()
	{
		super.onPause();

	/*	if (actionMode != null)
		{
			restart = false; // nerestartovat fragment
			actionMode.finish();
		} */
		
    	//Log.d("ProfilePreferencesFragment.onPause", "xxxx");
		
	}

	@Override
	public void onDestroy()
	{
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        profile = null;
		super.onDestroy();
	}

	public void doOnActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == ImageViewPreference.RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null)
		{
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			
			Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			
			cursor.close();
			
			//Log.d("ProfilePreferencesFragment.onActivityResult", picturePath);
			
			// nastavime image identifikatoru na ziskanu cestu ku obrazku
			changedImageViewPreference.setImageIdentifierAndType(picturePath, false);
			
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		doOnActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        outState.putBoolean("action_mode_showed", (actionMode != null));
	}	
	
	private void loadPreferences()
	{
    	if (profile != null)
    	{
	    	SharedPreferences preferences = getSherlockActivity().getSharedPreferences(ProfilePreferencesFragment.PREFS_NAME, Activity.MODE_PRIVATE);
	
	    	Editor editor = preferences.edit();
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
	        editor.putString(GlobalData.PREF_PROFILE_DEVICE_AUTOSYNC, Integer.toString(profile._deviceAutosync));
	        editor.putBoolean(GlobalData.PREF_PROFILE_SHOW_IN_ACTIVATOR, profile._showInActivator);
			editor.commit();
    	}
		
	}
	
	private void savePreferences()
	{
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
    	profile._deviceAutosync = Integer.parseInt(preferences.getString(GlobalData.PREF_PROFILE_DEVICE_AUTOSYNC, ""));
    	profile._showInActivator = preferences.getBoolean(GlobalData.PREF_PROFILE_SHOW_IN_ACTIVATOR, true);

    	//Log.d("ProfilePreferencesFragment.onPause", "profile activated="+profile.getChecked());
    	
    	// update bitmaps
		profile.generateIconBitmap(context, false, 0);
		profile.generatePreferencesIndicator(context, false, 0);

    	//Log.d("ProfilePreferencesFragment.onPause", "profile activated="+profile.getChecked());
		
		if (new_profile)
		{
			// add profile into DB
			EditorProfilesActivity.dataWrapper.getDatabaseHandler().addProfile(profile);
			profile_id = profile._id;

        	//Log.d("ProfilePreferencesFragment.onPause", "addProfile");
			
		}
		else
        if (profile_id > 0) 
        {
			EditorProfilesActivity.dataWrapper.getDatabaseHandler().updateProfile(profile);
        	
        	//Log.d("ProfilePreferencesFragment.onPause", "updateProfile");

        }

        onRedrawProfileListFragmentCallback.onRedrawProfileListFragment(profile, new_profile);
	}
	
	private void setSummary(String key, Object value)
	{
		//Log.d("ProfilePreferencesFragment.setSummary",key);
		
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
			key.equals(GlobalData.PREF_PROFILE_DEVICE_AUTOSYNC) ||
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
		
	}
	
	private void updateSharedPreference()
	{
        if (profile_id > 0) 
        {	

	    	// updating activity with selected profile preferences
	    	
        	//Log.d("PhonePreferencesActivity.updateSharedPreference", profile.getName());
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
	        setSummary(GlobalData.PREF_PROFILE_DEVICE_AUTOSYNC, profile._deviceAutosync);
			
        }
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
    	if (!(key.equals(GlobalData.PREF_PROFILE_SOUND_RINGTONE_CHANGE) ||
	    		key.equals(GlobalData.PREF_PROFILE_SOUND_NOTIFICATION_CHANGE) ||
	    		key.equals(GlobalData.PREF_PROFILE_SOUND_ALARM_CHANGE) ||
	    		key.equals(GlobalData.PREF_PROFILE_DEVICE_WALLPAPER_CHANGE) ||
	    		key.equals(GlobalData.PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS) || 
	    		key.equals(GlobalData.PREF_PROFILE_DEVICE_RUN_APPLICATION_CHANGE) ||
	    		key.equals(GlobalData.PREF_PROFILE_SHOW_IN_ACTIVATOR) 
	    		))
	    		setSummary(key, sharedPreferences.getString(key, ""));
    	showActionMode();
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
               if (actionModeButtonClicked == BUTTON_CANCEL)
            	   onRestartProfilePreferencesCallback.onRestartProfilePreferences(profile);
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
	
	private void showActionMode()
	{
		profileNonEdited = false;
		
        if (actionMode == null)
        {
        	
        	actionModeButtonClicked = BUTTON_UNDEFINED;
        	
        	LayoutInflater inflater = LayoutInflater.from(getSherlockActivity());
        	View actionView = inflater.inflate(R.layout.profile_preferences_action_mode, null);

            actionMode = getSherlockActivity().startActionMode(actionModeCallback);
            actionMode.setCustomView(actionView); 
            
            actionMode.getCustomView().findViewById(R.id.profile_preferences_action_menu_cancel).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					//Log.d("actionMode.onClick", "cancel");
					
					finishActionMode(BUTTON_CANCEL);
					
				}
           	});

            actionMode.getCustomView().findViewById(R.id.profile_preferences_action_menu_save).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					//Log.d("actionMode.onClick", "save");
			
					savePreferences();
					
					finishActionMode(BUTTON_SAVE);
					
				}
           	});
        }
	}
	
	public boolean isActionModeActive()
	{
		return (actionMode != null);
	}
	
	public void finishActionMode(int button)
	{
		int _button = button;
		
		if (_button == BUTTON_SAVE)
			new_profile = false;
		
		if (!EditorProfilesActivity.mTwoPane)
		{
			actionModeButtonClicked = BUTTON_UNDEFINED;
			getSherlockActivity().finish(); // finish activity;
		}
		else
		if (actionMode != null)
		{	
			actionModeButtonClicked = _button;
			actionMode.finish();
		}
	}

	static public Activity getPreferencesActivity()
	{
		return preferencesActivity;
	}
	
	static public void setChangedImageViewPreference(ImageViewPreference changedImageViewPref)
	{
		changedImageViewPreference = changedImageViewPref;
	}

}
