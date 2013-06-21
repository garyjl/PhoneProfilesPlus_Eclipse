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
import android.util.Log;
 
public class ProfilePreferencesFragment extends PreferenceListFragment 
										implements SharedPreferences.OnSharedPreferenceChangeListener
{
	
	private Profile profile;
	private int profile_position;
	private PreferenceManager prefMng;
	private SharedPreferences preferences;
	private Context context;
	private ActionMode actionMode;
	private Callback actionModeCallback;
	
	private static ImageViewPreference changedImageViewPreference;
	private static Activity preferencesActivity = null;
		
	static final String PREFS_NAME = "profile_preferences";
	
	static final String PREF_PROFILE_NAME = "profileName";
	static final String PREF_PROFILE_ICON = "profileIcon";
	static final String PREF_PROFILE_VOLUME_RINGER_MODE = "volumeRingerMode";
	static final String PREF_PROFILE_VOLUME_RINGTONE = "volumeRingtone";
	static final String PREF_PROFILE_VOLUME_NOTIFICATION = "volumeNotification";
	static final String PREF_PROFILE_VOLUME_MEDIA = "volumeMedia";
	static final String PREF_PROFILE_VOLUME_ALARM = "volumeAlarm";
	static final String PREF_PROFILE_VOLUME_SYSTEM = "volumeSystem";
	static final String PREF_PROFILE_VOLUME_VOICE = "volumeVoice";
	static final String PREF_PROFILE_SOUND_RINGTONE_CHANGE = "soundRingtoneChange";
	static final String PREF_PROFILE_SOUND_RINGTONE = "soundRingtone";
	static final String PREF_PROFILE_SOUND_NOTIFICATION_CHANGE = "soundNotificationChange";
	static final String PREF_PROFILE_SOUND_NOTIFICATION = "soundNotification";
	static final String PREF_PROFILE_SOUND_ALARM_CHANGE = "soundAlarmChange";
	static final String PREF_PROFILE_SOUND_ALARM = "soundAlarm";
	static final String PREF_PROFILE_DEVICE_AIRPLANE_MODE = "deviceAirplaneMode";
	static final String PREF_PROFILE_DEVICE_WIFI = "deviceWiFi";
	static final String PREF_PROFILE_DEVICE_BLUETOOTH = "deviceBluetooth";
	static final String PREF_PROFILE_DEVICE_SCREEN_TIMEOUT = "deviceScreenTimeout";
	static final String PREF_PROFILE_DEVICE_BRIGHTNESS = "deviceBrightness";
	static final String PREF_PROFILE_DEVICE_WALLPAPER_CHANGE = "deviceWallpaperChange";
	static final String PREF_PROFILE_DEVICE_WALLPAPER = "deviceWallpaper";
	static final String PREF_PROFILE_DEVICE_MOBILE_DATA = "deviceMobileData";
	static final String PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS = "deviceMobileDataPrefs";
	static final String PREF_PROFILE_DEVICE_GPS = "deviceGPS";
	static final String PREF_PROFILE_DEVICE_RUN_APPLICATION_CHANGE = "deviceRunApplicationChange";
	static final String PREF_PROFILE_DEVICE_RUN_APPLICATION_PACKAGE_NAME = "deviceRunApplicationPackageName";

	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		preferencesActivity = getSherlockActivity();
		
        // getting attached fragment data
		if (getArguments().containsKey(GlobalData.EXTRA_PROFILE_POSITION))
			profile_position = getArguments().getInt(GlobalData.EXTRA_PROFILE_POSITION);
    	Log.d("ProfilePreferencesFragment.onCreate", "profile_position=" + profile_position);
		
        context = getSherlockActivity().getBaseContext();
        
        loadPreferences();
        
		prefMng = getPreferenceManager();
		prefMng.setSharedPreferencesName(PREFS_NAME);
		prefMng.setSharedPreferencesMode(Activity.MODE_PRIVATE);
		
		addPreferencesFromResource(R.xml.profile_preferences);

        preferences = prefMng.getSharedPreferences();
        
        preferences.registerOnSharedPreferenceChangeListener(this);  
        
        createActionMode();
        

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

    	//Log.d("ProfilePreferencesFragment.onPause", "xxxx");
		
        if (profile_position > -1) 
        {
        	profile.setName(preferences.getString(PREF_PROFILE_NAME, ""));
        	profile.setIcon(preferences.getString(PREF_PROFILE_ICON, ""));
        	profile.setVolumeRingerMode(Integer.parseInt(preferences.getString(PREF_PROFILE_VOLUME_RINGER_MODE, "")));
        	profile.setVolumeRingtone(preferences.getString(PREF_PROFILE_VOLUME_RINGTONE, ""));
        	profile.setVolumeNotification(preferences.getString(PREF_PROFILE_VOLUME_NOTIFICATION, ""));
        	profile.setVolumeMedia(preferences.getString(PREF_PROFILE_VOLUME_MEDIA, ""));
        	profile.setVolumeAlarm(preferences.getString(PREF_PROFILE_VOLUME_ALARM, ""));
        	profile.setVolumeSystem(preferences.getString(PREF_PROFILE_VOLUME_SYSTEM, ""));
        	profile.setVolumeVoice(preferences.getString(PREF_PROFILE_VOLUME_VOICE, ""));
        	profile.setSoundRingtoneChange(preferences.getBoolean(PREF_PROFILE_SOUND_RINGTONE_CHANGE, false));
        	profile.setSoundRingtone(preferences.getString(PREF_PROFILE_SOUND_RINGTONE, ""));
        	profile.setSoundNotificationChange(preferences.getBoolean(PREF_PROFILE_SOUND_NOTIFICATION_CHANGE, false));
        	profile.setSoundNotification(preferences.getString(PREF_PROFILE_SOUND_NOTIFICATION, ""));
        	profile.setSoundAlarmChange(preferences.getBoolean(PREF_PROFILE_SOUND_ALARM_CHANGE, false));
        	profile.setSoundAlarm(preferences.getString(PREF_PROFILE_SOUND_ALARM, ""));
        	profile.setDeviceAirplaneMode(Integer.parseInt(preferences.getString(PREF_PROFILE_DEVICE_AIRPLANE_MODE, "")));
        	profile.setDeviceWiFi(Integer.parseInt(preferences.getString(PREF_PROFILE_DEVICE_WIFI, "")));
        	profile.setDeviceBluetooth(Integer.parseInt(preferences.getString(PREF_PROFILE_DEVICE_BLUETOOTH, "")));
        	profile.setDeviceScreenTimeout(Integer.parseInt(preferences.getString(PREF_PROFILE_DEVICE_SCREEN_TIMEOUT, "")));
        	profile.setDeviceBrightness(preferences.getString(PREF_PROFILE_DEVICE_BRIGHTNESS, ""));
        	profile.setDeviceWallpaperChange(preferences.getBoolean(PREF_PROFILE_DEVICE_WALLPAPER_CHANGE, false));
        	if (profile.getDeviceWallpaperChange())
        		profile.setDeviceWallpaper(preferences.getString(PREF_PROFILE_DEVICE_WALLPAPER, ""));
        	else
        		profile.setDeviceWallpaper("-|0");
        	profile.setDeviceMobileData(Integer.parseInt(preferences.getString(PREF_PROFILE_DEVICE_MOBILE_DATA, "")));
        	profile.setDeviceMobileDataPrefs(preferences.getBoolean(PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS, false));
        	profile.setDeviceGPS(Integer.parseInt(preferences.getString(PREF_PROFILE_DEVICE_GPS, "")));
        	profile.setDeviceRunApplicationChange(preferences.getBoolean(PREF_PROFILE_DEVICE_RUN_APPLICATION_CHANGE, false));
        	if (profile.getDeviceRunApplicationChange())
        		profile.setDeviceRunApplicationPackageName(preferences.getString(PREF_PROFILE_DEVICE_RUN_APPLICATION_PACKAGE_NAME, "-"));
        	else
        		profile.setDeviceRunApplicationPackageName("-");

        	//Log.d("ProfilePreferencesFragment.onPause", "profile activated="+profile.getChecked());
        	
        	// TODO toto asik je zle. treba vymysliet, ako sa dostat ku tomu adapteru inac
        	EditorProfileListFragment.getProfileListAdapter().updateItem(profile);

        	//Log.d("ProfilePreferencesFragment.onPause", "profile activated="+profile.getChecked());
        	
        	GlobalData.getDatabaseHandler().updateProfile(profile);
        	
        	//Log.d("ProfilePreferencesFragment.onPause", "updateProfile");


        }
		
	}
	
	@Override
	public void onDestroy()
	{
        preferences.unregisterOnSharedPreferenceChangeListener(this);        
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
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
	
	private void loadPreferences()
	{
		
    	// TODO toto asik je zle. treba vymysliet, ako sa dostat ku tomu adapteru inac
    	profile = (Profile) EditorProfileListFragment.getProfileListAdapter().getItem(profile_position);
		
    	SharedPreferences preferences = getSherlockActivity().getSharedPreferences(ProfilePreferencesFragment.PREFS_NAME, Activity.MODE_PRIVATE);

    	Editor editor = preferences.edit();
        editor.putString(PREF_PROFILE_NAME, profile.getName());
        editor.putString(PREF_PROFILE_ICON, profile.getIcon());
        editor.putString(PREF_PROFILE_VOLUME_RINGER_MODE, Integer.toString(profile.getVolumeRingerMode()));
        editor.putString(PREF_PROFILE_VOLUME_RINGTONE, profile.getVolumeRingtone());
        editor.putString(PREF_PROFILE_VOLUME_NOTIFICATION, profile.getVolumeNotification());
        editor.putString(PREF_PROFILE_VOLUME_MEDIA, profile.getVolumeMedia());
        editor.putString(PREF_PROFILE_VOLUME_ALARM, profile.getVolumeAlarm());
        editor.putString(PREF_PROFILE_VOLUME_SYSTEM, profile.getVolumeSystem());
        editor.putString(PREF_PROFILE_VOLUME_VOICE, profile.getVolumeVoice());
        editor.putBoolean(PREF_PROFILE_SOUND_RINGTONE_CHANGE, profile.getSoundRingtoneChange());
        editor.putString(PREF_PROFILE_SOUND_RINGTONE, profile.getSoundRingtone());
        editor.putBoolean(PREF_PROFILE_SOUND_NOTIFICATION_CHANGE, profile.getSoundNotificationChange());
        editor.putString(PREF_PROFILE_SOUND_NOTIFICATION, profile.getSoundNotification());
        editor.putBoolean(PREF_PROFILE_SOUND_ALARM_CHANGE, profile.getSoundAlarmChange());
        editor.putString(PREF_PROFILE_SOUND_ALARM, profile.getSoundAlarm());
        editor.putString(PREF_PROFILE_DEVICE_AIRPLANE_MODE, Integer.toString(profile.getDeviceAirplaneMode()));
        editor.putString(PREF_PROFILE_DEVICE_WIFI, Integer.toString(profile.getDeviceWiFi()));
        editor.putString(PREF_PROFILE_DEVICE_BLUETOOTH, Integer.toString(profile.getDeviceBluetooth()));
        editor.putString(PREF_PROFILE_DEVICE_SCREEN_TIMEOUT, Integer.toString(profile.getDeviceScreenTimeout()));
        editor.putString(PREF_PROFILE_DEVICE_BRIGHTNESS, profile.getDeviceBrightness());
        editor.putBoolean(PREF_PROFILE_DEVICE_WALLPAPER_CHANGE, profile.getDeviceWallpaperChange());
        editor.putString(PREF_PROFILE_DEVICE_WALLPAPER, profile.getDeviceWallpaper());
        editor.putString(PREF_PROFILE_DEVICE_MOBILE_DATA, Integer.toString(profile.getDeviceMobileData()));
        editor.putBoolean(PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS, profile.getDeviceMobileDataPrefs());
        editor.putString(PREF_PROFILE_DEVICE_GPS, Integer.toString(profile.getDeviceGPS()));
        editor.putBoolean(PREF_PROFILE_DEVICE_RUN_APPLICATION_CHANGE, profile.getDeviceRunApplicationChange());
        editor.putString(PREF_PROFILE_DEVICE_RUN_APPLICATION_PACKAGE_NAME, profile.getDeviceRunApplicationPackageName());
		editor.commit();
		
	}
	
	private void setSummary(String key, Object value)
	{
		//Log.d("ProfilePreferencesFragment.setSummary",key);
		
		if (key.equals(PREF_PROFILE_NAME))
		{	
	        prefMng.findPreference(key).setSummary(value.toString());
		}
		if (key.equals(PREF_PROFILE_VOLUME_RINGER_MODE))
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
		if (key.equals(PREF_PROFILE_SOUND_RINGTONE) ||
			key.equals(PREF_PROFILE_SOUND_NOTIFICATION) ||
			key.equals(PREF_PROFILE_SOUND_ALARM))
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
		if (key.equals(PREF_PROFILE_DEVICE_AIRPLANE_MODE) || 
			key.equals(PREF_PROFILE_DEVICE_WIFI) ||
			key.equals(PREF_PROFILE_DEVICE_BLUETOOTH) ||
			key.equals(PREF_PROFILE_DEVICE_MOBILE_DATA) ||
			key.equals(PREF_PROFILE_DEVICE_GPS))
		{
			boolean canChange = CheckHardwareFeatures.check(key, context);
			if (!canChange)
			{
				prefMng.findPreference(key).setEnabled(false);
				prefMng.findPreference(key).setSummary(getResources().getString(R.string.profile_preferences_device_not_allowed));
				if (key.equals(PREF_PROFILE_DEVICE_MOBILE_DATA))
				{
					prefMng.findPreference(PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS).setEnabled(false);
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
		if (key.equals(PREF_PROFILE_DEVICE_SCREEN_TIMEOUT))
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
        if (profile_position > -1) 
        {	

	    	// updating activity with selected profile preferences
	    	
        	//Log.d("PhonePreferencesActivity.updateSharedPreference", profile.getName());
	        setSummary(PREF_PROFILE_NAME, profile.getName());
	        setSummary(PREF_PROFILE_VOLUME_RINGER_MODE, profile.getVolumeRingerMode());
	        setSummary(PREF_PROFILE_SOUND_RINGTONE, profile.getSoundRingtone());
	        setSummary(PREF_PROFILE_SOUND_NOTIFICATION, profile.getSoundNotification());
	        setSummary(PREF_PROFILE_SOUND_ALARM, profile.getSoundAlarm());
	        setSummary(PREF_PROFILE_DEVICE_AIRPLANE_MODE, profile.getDeviceAirplaneMode());
	        setSummary(PREF_PROFILE_DEVICE_WIFI, profile.getDeviceWiFi());
	        setSummary(PREF_PROFILE_DEVICE_BLUETOOTH, profile.getDeviceBluetooth());
	        setSummary(PREF_PROFILE_DEVICE_SCREEN_TIMEOUT, profile.getDeviceScreenTimeout());
	        setSummary(PREF_PROFILE_DEVICE_MOBILE_DATA, profile.getDeviceMobileData());
	        setSummary(PREF_PROFILE_DEVICE_GPS, profile.getDeviceGPS());
			
        }
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
    	if (!(key.equals(PREF_PROFILE_SOUND_RINGTONE_CHANGE) ||
	    		key.equals(PREF_PROFILE_SOUND_NOTIFICATION_CHANGE) ||
	    		key.equals(PREF_PROFILE_SOUND_ALARM_CHANGE) ||
	    		key.equals(PREF_PROFILE_DEVICE_WALLPAPER_CHANGE) ||
	    		key.equals(PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS) || 
	    		key.equals(PREF_PROFILE_DEVICE_RUN_APPLICATION_CHANGE) 
	    		))
	    		setSummary(key, sharedPreferences.getString(key, ""));
    	
    	// show action mode
    	//if(actionMode!=null) 
    	//	return false;
        if (actionMode == null)
            actionMode = getSherlockActivity().startActionMode(actionModeCallback);
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
            }
 
            /** This is called when the action mode is created. This is called by startActionMode() */
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.setTitle(R.string.phone_preferences_actionmode_title);
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

	static public Activity getPreferencesActivity()
	{
		return preferencesActivity;
	}
	
	static public void setChangedImageViewPreference(ImageViewPreference changedImageViewPref)
	{
		changedImageViewPreference = changedImageViewPref;
	}

}