package sk.henrichg.phoneprofiles;
 
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
 
public class ProfilePreferencesFragment extends PreferenceListFragment {
	
	private Profile profile;
	private int profile_position;
	private PreferenceManager prefMng;
	private SharedPreferences preferences;
	private Context context;
	private Intent intent;
	
	private static ImageViewPreference changedImageViewPreference;
		
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

    public ProfilePreferencesFragment(int xmlId){
    	super(xmlId);
    }
	
	public ProfilePreferencesFragment() {
		super();
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

        intent = getActivity().getIntent();
        context = getActivity().getBaseContext();
        
		prefMng = getPreferenceManager();
		prefMng.setSharedPreferencesName(PREFS_NAME);
		prefMng.setSharedPreferencesMode(Activity.MODE_PRIVATE);
        
        preferences = prefMng.getSharedPreferences();

        // getting attached intent data
        profile_position = intent.getIntExtra(GlobalData.EXTRA_PROFILE_POSITION, -1);

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
	
	private void updateSharedPreference()
	{
        if (profile_position > -1) 
        {	

        	// TODO toto asik je zle. treba vymysliet, ako sa dostat ku tomu adapteru inac
        	profile = (Profile) EditorProfileListFragment.getProfileListAdapter().getItem(profile_position);

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
	
	public void preferenceChanged(SharedPreferences prefs, String key)
	{
    	if (!(key.equals(PREF_PROFILE_SOUND_RINGTONE_CHANGE) ||
	    		key.equals(PREF_PROFILE_SOUND_NOTIFICATION_CHANGE) ||
	    		key.equals(PREF_PROFILE_SOUND_ALARM_CHANGE) ||
	    		key.equals(PREF_PROFILE_DEVICE_WALLPAPER_CHANGE) ||
	    		key.equals(PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS) || 
	    		key.equals(PREF_PROFILE_DEVICE_RUN_APPLICATION_CHANGE) 
	    		))
	    		setSummary(key, prefs.getString(key, ""));
	}
	
	static public void setChangedImageViewPreference(ImageViewPreference changedImageViewPref)
	{
		changedImageViewPreference = changedImageViewPref;
	}
	
	
}