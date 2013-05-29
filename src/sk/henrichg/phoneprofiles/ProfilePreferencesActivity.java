package sk.henrichg.phoneprofiles;
 
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
 
public class ProfilePreferencesActivity extends SherlockPreferenceActivity {
	
	private Profile profile;
	private int profile_position;
	private PreferenceManager prefMng;
	private SharedPreferences preferences;
	private Context context;
	private Intent intent;
	private OnSharedPreferenceChangeListener prefListener;
	
	private static ImageViewPreference changedImageViewPreference;
	private static Activity preferenceActivity = null;
	
		
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

	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);

		PhoneProfilesActivity.setLanguage(getBaseContext(), false);
		
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
		preferenceActivity = this;

        intent = getIntent();
        context = this;
        
		prefMng = getPreferenceManager();
		prefMng.setSharedPreferencesName(PREFS_NAME);
		prefMng.setSharedPreferencesMode(MODE_PRIVATE);
        
		addPreferencesFromResource(R.layout.profile_preferences);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        
        prefListener = new OnSharedPreferenceChangeListener() {
        	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

    	    	// updating activity with selected profile preferences
    	    	//Log.d("ProfilePreferencesActivity.onSharedPreferenceChanged", key);

    	    	if (!(key.equals(PREF_PROFILE_SOUND_RINGTONE_CHANGE) ||
    	    		key.equals(PREF_PROFILE_SOUND_NOTIFICATION_CHANGE) ||
    	    		key.equals(PREF_PROFILE_SOUND_ALARM_CHANGE) ||
    	    		key.equals(PREF_PROFILE_DEVICE_WALLPAPER_CHANGE) ||
    	    		key.equals(PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS)
    	    		))
    	    		setSummary(key, prefs.getString(key, ""));

        	}

        };
        
        preferences.registerOnSharedPreferenceChangeListener(prefListener);
        
        // getting attached intent data
        profile_position = intent.getIntExtra(PhoneProfilesActivity.EXTRA_PROFILE_POSITION, -1);

    	//Log.d("ProfilePreferencesActivity.onCreate", "xxxx");
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();

		updateSharedPreference();
		
    	//Log.d("ProfilePreferencesActivity.onStart", preferences.getString(PREF_PROFILE_NAME, ""));

    	//Log.d("ProfilePreferencesActivity.onStart", "profile activated="+profile.getChecked());

	}
	
	@Override
	protected void onPause()
	{
		super.onPause();

    	//Log.d("ProfilePreferencesActivity.onPause", "xxxx");
		
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

        	//Log.d("ProfilePreferencesActivity.onPause", "profile activated="+profile.getChecked());
        	
        	PhoneProfilesActivity.getProfileListAdapter().updateItem(profile);

        	//Log.d("ProfilePreferencesActivity.onPause", "profile activated="+profile.getChecked());
        	
        	PhoneProfilesActivity.getDatabaseHandler().updateProfile(profile);
        	
        	//Log.d("ProfilePreferencesActivity.onPause", "updateProfile");


        }
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == ImageViewPreference.RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null)
		{
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			
			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			
			cursor.close();
			
			//Log.d("ProfilePreferencesActivity.onActivityResult", picturePath);
			
			// nastavime image identifikatoru na ziskanu cestu ku obrazku
			changedImageViewPreference.setImageIdentifierAndType(picturePath, false);
			
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
	}
	
	@Override
	protected void onDestroy()
	{
    	//Log.d("ProfilePreferencesActivity.onDestroy", "xxxx");
    	preferences.unregisterOnSharedPreferenceChangeListener(prefListener);
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
		Intent intent = getIntent();
		startActivity(intent);
		finish();
	}

	private void updateSharedPreference()
	{
        if (profile_position > -1) 
        {	

           	//profile = PhoneProfilesActivity.getDatabaseHandler().getProfile(profile_id);
        	profile = (Profile) PhoneProfilesActivity.getProfileListAdapter().getItem(profile_position);

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
			
        }
	}
	
	private void setSummary(String key, Object value)
	{
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
			
			//Log.d("ProfilePreferencesActivity.setSummary", ringtoneUri);
			
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
			key.equals(PREF_PROFILE_DEVICE_MOBILE_DATA))
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
	
	static public Activity getActivity()
	{
		return preferenceActivity;
	}
	
	static public void setChangedImageViewPreference(ImageViewPreference changedImageViewPref)
	{
		changedImageViewPreference = changedImageViewPref;
	}
	
	
}