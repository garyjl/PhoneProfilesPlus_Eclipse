package sk.henrichg.phoneprofiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockActivity;

import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.WallpaperManager;
import android.support.v4.app.NotificationCompat;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneProfilesActivity extends SherlockActivity {

	private static DatabaseHandler databaseHandler;
	private NotificationManager notificationManager;
	private List<Profile> profileList;
	private MainProfileListAdapter profileListAdapter;
	private ListView listView;
	private TextView activeProfileName;
	private ImageView activeProfileIcon;
	private String actualLanguage;
	private int startupSource = 0;
	private boolean applicationStarted;
	private Intent intent;

	static final String INTENT_PROFILE_ID = "profile_id";
	static final String INTENT_START_APP_SOURCE = "start_app_source";
	
	static final String PROFILE_ICON_DEFAULT = "ic_profile_default";
	
	static final int STARTUP_SOURCE_NOTIFICATION = 1;
	static final int STARTUP_SOURCE_WIDGET = 2;
	static final int STARTUP_SOURCE_SHORTCUT = 3;
	static final int STARTUP_SOURCE_BOOT = 4;

	static final int NOTIFICATION_ID = 700420;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_profiles);
		
		// na onCreate dame, ze aplikacia este nie je nastartovana
		applicationStarted = false;
		
		intent = getIntent();
		startupSource = intent.getIntExtra(INTENT_START_APP_SOURCE, 0);

		databaseHandler = new DatabaseHandler(this);
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		activeProfileName = (TextView)findViewById(R.id.activated_profile_name);
		activeProfileIcon = (ImageView)findViewById(R.id.activated_profile_icon);
		listView = (ListView)findViewById(R.id.main_profiles_list);
		
		SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
		actualLanguage = preferences.getString(PhoneProfilesPreferencesActivity.PREF_APPLICATION_LANGUAGE, "system");
		
		/*
		Profile profile = databaseHandler.getActivatedProfile();
		updateHeader(profile);
		showNotification(profile);
		updateWidget();
		*/
	
		profileList = new ArrayList<Profile>();
		profileListAdapter = new MainProfileListAdapter(this, profileList);
		
		listView.setAdapter(profileListAdapter);
		
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Log.d("PhoneProfilesActivity.onItemClick", "xxxx");
				
				activateProfileWithAlert(position);

			}
			
		});
		
		
		Log.d("PhoneProfileActivity.onCreate", "xxxx");
		
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		
		if (view.getId() == R.id.main_profiles_list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			Profile profile;
			
			profile = profileList.get(info.position);

			menu.setHeaderTitle(getResources().getString(R.string.profile_context_header) + ": " + profile.getName());
			menu.add(Menu.NONE, 1001, 1, getResources().getString(R.string.profile_context_item_edit));
			menu.add(Menu.NONE, 1002, 2, getResources().getString(R.string.profile_context_item_duplicate));
			menu.add(Menu.NONE, 1003, 3, getResources().getString(R.string.profile_context_item_delete));
			
		}
	}
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		final int position = info.position;
		
		switch (item.getItemId()) {
		case 1001:
			Log.d("PhoneProfileActivity.onContextItemSelected", "Edit");

			startProfilePreferencesActivity(info.position);
			
			return true;
		case 1002:
			Log.d("PhoneProfileActivity.onContextItemSelected", "Duplicate");
			
			duplicateProfile(position);
			
			return true;
		case 1003:
			Log.d("PhoneProfileActivity.onContextItemSelected", "Delete");

			deleteProfile(position);
			
			return true;
		default:
			return false;
		}
	}
	
	
	@Override
	protected void onStart()
	{
		super.onStart();

		setLanguage();
		
		updateListView();


		Log.d("PhoneProfilesActivity.onStart", "startupSource="+startupSource);
		
		boolean actProfile = false;
		if (startupSource == STARTUP_SOURCE_SHORTCUT)
		{
			// aktivita spustena z shortcutu, profil aktivujeme
			actProfile = true;
		}
		else
		if (startupSource == 0 || startupSource == STARTUP_SOURCE_BOOT)
		{
			
			// aktivita nebola spustena z notifikacie, ani z widgetu
			// lebo v tychto pripadoch sa nesmie spravit aktivacia profilu
			// pri starte aktivity
			
			if (!applicationStarted)
			{
				// aplikacia este nie je nastartovana, takze mozeme
				// aktivovat profil, ak je nastavene, ze sa tak ma stat 
				SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
				if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_APPLICATION_ACTIVATE, true))
				{
					// je nastavene, ze pri starte sa ma aktivita aktivovat
					actProfile = true;
				}
			}
		}
		Log.d("PhoneProfilesActivity.onStart", "actProfile="+String.valueOf(actProfile));

		Profile profile;
		
		// pre profil, ktory je prave aktivny, treba aktualizovat aktivitu
		profile = databaseHandler.getActivatedProfile();
		updateHeader(profile);
		showNotification(profile);
		updateWidget();
		
		if (startupSource == STARTUP_SOURCE_SHORTCUT)
		{
			long profile_id = intent.getLongExtra(INTENT_PROFILE_ID, 0);
			if (profile_id == 0)
				profile = null;
			else
				profile = databaseHandler.getProfile(profile_id);
		}
		
		if (actProfile && (profile != null))
		{
			// aktivacia profilu
			activateProfile(profile);
		}
		
		// reset, aby sa to dalej chovalo ako normalne spustenie z lauchera
		boolean finishActivity = (startupSource == STARTUP_SOURCE_BOOT) || (startupSource == STARTUP_SOURCE_SHORTCUT);
		startupSource = 0;

		// na onStart dame, ze aplikacia uz je nastartovana
		applicationStarted = true;
		
		Log.d("PhoneProfileActivity.onStart", "xxxx");
		
		if (finishActivity)
			finish();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
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
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_phone_profiles, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_new_profile:
			Log.d("PhoneProfileActivity.onOptionsItemSelected", "menu_new_profile");

			startProfilePreferencesActivity(-1);
			
			return true;
		case R.id.menu_settings:
			Log.d("PhoneProfilesActivity.onOptionsItemSelected", "menu_settings");
			
			Intent intent = new Intent(getBaseContext(), PhoneProfilesPreferencesActivity.class);

			startActivity(intent);

			return true;
		case R.id.menu_exit:
			Log.d("PhoneProfilesActivity.onOptionsItemSelected", "menu_exit");
			
			// zrusenie notifikacie
			showNotification(null);
			
			finish();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startProfilePreferencesActivity(int position)
	{

		SharedPreferences preferences;
		Editor editor;

		Profile profile;
		
		if (position != -1)
			// editacia profilu
			profile = profileList.get(position);
		else
		{
			// pridanie noveho profilu
			profile = new Profile(getResources().getString(R.string.profile_name_default), 
								  PROFILE_ICON_DEFAULT + "|1", 
								  false, 
								  0,
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
								  "-|0"
					);
			databaseHandler.addProfile(profile);
		}

		
        preferences = getSharedPreferences(ProfilePreferencesActivity.PREFS_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_NAME, profile.getName());
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_ICON, profile.getIcon());
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_VOLUME_RINGER_MODE, Integer.toString(profile.getVolumeRingerMode()));
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_VOLUME_RINGTONE, profile.getVolumeRingtone());
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_VOLUME_NOTIFICATION, profile.getVolumeNotification());
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_VOLUME_MEDIA, profile.getVolumeMedia());
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_VOLUME_ALARM, profile.getVolumeAlarm());
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_VOLUME_SYSTEM, profile.getVolumeSystem());
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_VOLUME_VOICE, profile.getVolumeVoice());
        editor.putBoolean(ProfilePreferencesActivity.PREF_PROFILE_SOUND_RINGTONE_CHANGE, profile.getSoundRingtoneChange());
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_SOUND_RINGTONE, profile.getSoundRingtone());
        editor.putBoolean(ProfilePreferencesActivity.PREF_PROFILE_SOUND_NOTIFICATION_CHANGE, profile.getSoundNotificationChange());
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_SOUND_NOTIFICATION, profile.getSoundNotification());
        editor.putBoolean(ProfilePreferencesActivity.PREF_PROFILE_SOUND_ALARM_CHANGE, profile.getSoundAlarmChange());
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_SOUND_ALARM, profile.getSoundAlarm());
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_AIRPLANE_MODE, Integer.toString(profile.getDeviceAirplaneMode()));
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_WIFI, Integer.toString(profile.getDeviceWiFi()));
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_BLUETOOTH, Integer.toString(profile.getDeviceBluetooth()));
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_SCREEN_TIMEOUT, Integer.toString(profile.getDeviceScreenTimeout()));
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_BRIGHTNESS, profile.getDeviceBrightness());
        editor.putBoolean(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_WALLPAPER_CHANGE, profile.getDeviceWallpaperChange());
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_WALLPAPER, profile.getDeviceWallpaper());
		editor.commit();
		
		Log.d("PhoneProfilesActivity.startProfilePreferencesActivityFromList", profile.getID()+"");
		
		Intent intent = new Intent(getBaseContext(), ProfilePreferencesActivity.class);
		intent.putExtra(INTENT_PROFILE_ID, profile.getID());

		startActivity(intent);
		
	}

	private void updateListView()
	{
		if (profileList != null)
			profileList.clear();
		profileList = databaseHandler.getAllProfiles();
		profileListAdapter.setList(profileList);
	}

	private void duplicateProfile(int position)
	{
		Profile profile = profileList.get(position);

		profile.setName(profile.getName()+"_d");
		profile.setChecked(false);
		databaseHandler.addProfile(profile);
		
		updateListView();

		startProfilePreferencesActivity(profileList.size()-1);
	}

	private void deleteProfile(int position)
	{
		final Profile profile = profileList.get(position);

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(getResources().getString(R.string.profile_string_0) + ": " + profile.getName());
		dialogBuilder.setMessage(getResources().getString(R.string.delete_profile_alert_message) + "?");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				databaseHandler.deleteProfile(profile);
				updateListView();
				// v pripade, ze sa odmaze aktivovany profil
				Profile profile = databaseHandler.getActivatedProfile();
				updateHeader(profile);
				showNotification(profile);
				updateWidget();
			}
		});
		dialogBuilder.setNegativeButton(android.R.string.no, null);
		dialogBuilder.show();
	}
	
	private void updateHeader(Profile profile)
	{
		if (profile == null)
		{
			activeProfileName.setText(getResources().getString(R.string.profiles_header_profile_name_no_activated));
	    	activeProfileIcon.setImageResource(R.drawable.ic_profile_default);
		}
		else
		{
			activeProfileName.setText(profile.getName());
	        if (profile.getIsIconResourceID())
	        {
				int res = getResources().getIdentifier(profile.getIconIdentifier(), "drawable", getPackageName());
				activeProfileIcon.setImageResource(res); // resource na ikonu
	        }
	        else
	        {
	        	activeProfileIcon.setImageBitmap(BitmapFactory.decodeFile(profile.getIconIdentifier()));
	        }
		}
	}
	
	@SuppressLint("InlinedApi")
	private void showNotification(Profile profile)
	{

		SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
		
		if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_NOTIFICATION_STATUS_BAR, true))
		{	
			if (profile == null)
			{
				notificationManager.cancel(NOTIFICATION_ID);
			}
			else
			{
				// vytvorenie intentu na aktivitu, ktora sa otvori na kliknutie na notifikaciu
				Intent intent = new Intent(this, PhoneProfilesActivity.class);
				// nastavime, ze aktivita sa spusti z notifikacnej listy
				intent.putExtra(INTENT_START_APP_SOURCE, STARTUP_SOURCE_NOTIFICATION);
				PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				// vytvorenie samotnej notifikacie
				NotificationCompat.Builder notificationBuilder;
		        if (profile.getIsIconResourceID())
		        {
		        	int iconResource = getResources().getIdentifier(profile.getIconIdentifier(), "drawable", getPackageName());
		        
					notificationBuilder = new NotificationCompat.Builder(this)
						.setContentText(getResources().getString(R.string.active_profile_notification_label))
						.setContentTitle(profile.getName())
						.setContentIntent(pIntent)
						.setSmallIcon(iconResource);
		        }
		        else
		        {
		        	if (android.os.Build.VERSION.SDK_INT >= 11)
		        	{
		        		Bitmap bitmap = BitmapFactory.decodeFile(profile.getIconIdentifier());
		        		Resources resources = getResources();
		        		int height = (int) resources.getDimension(android.R.dimen.notification_large_icon_height);
		        		int width = (int) resources.getDimension(android.R.dimen.notification_large_icon_width);
		        		bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
						notificationBuilder = new NotificationCompat.Builder(this)
							.setContentText(getResources().getString(R.string.active_profile_notification_label))
							.setContentTitle(profile.getName())
							.setContentIntent(pIntent)
							.setLargeIcon(bitmap)
							.setSmallIcon(R.drawable.ic_launcher);
		        	}
		        	else
		        	{
						notificationBuilder = new NotificationCompat.Builder(this)
						.setContentText(getResources().getString(R.string.active_profile_notification_label))
						.setContentTitle(profile.getName())
						.setContentIntent(pIntent)
						.setSmallIcon(R.drawable.ic_launcher);
		        	}
		        }
				Notification notification = notificationBuilder.getNotification();
				notification.flags |= Notification.FLAG_NO_CLEAR; 
				notificationManager.notify(NOTIFICATION_ID, notification);
			}
		}
		else
		{
			notificationManager.cancel(NOTIFICATION_ID);
		}
	}
	
	private void updateWidget()
	{
		Intent intent = new Intent(this, ActivateProfileWidget.class);
		intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
		int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), ActivateProfileWidget.class));
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		sendBroadcast(intent);
	}
	
	private void activateProfileWithAlert(int position)
	{
		SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);

		if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_APPLICATION_ALERT, true))
		{	
			final int _position = position;
			final Profile profile = profileList.get(_position);

			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle(getResources().getString(R.string.profile_string_0) + ": " + profile.getName());
			dialogBuilder.setMessage(getResources().getString(R.string.activate_profile_alert_message) + "?");
			//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
			dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					activateProfile(_position);
				}
			});
			dialogBuilder.setNegativeButton(android.R.string.no, null);
			dialogBuilder.show();
		}
		else
			activateProfile(position);
	}
	
	@SuppressWarnings("deprecation")
	private void activateProfile(Profile profile)
	{
		SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
		
		databaseHandler.activateProfile(profile);
		updateListView();
		updateHeader(profile);
		updateWidget();

		if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_NOTIFICATION_TOAST, true))
		{	
			// toast notification
			Toast msg = Toast.makeText(this, 
					getResources().getString(R.string.toast_profile_activated_0) + ": " + profile.getName() + " " +
					getResources().getString(R.string.toast_profile_activated_1), 
					Toast.LENGTH_LONG);
			msg.show();
		}

		showNotification(profile);

		// rozdelit zvonenie a notifikacie - zial je to oznacene ako @Hide :-(
		//Settings.System.putInt(getContentResolver(), Settings.System.NOTIFICATIONS_USE_RING_VOLUME, 0);

		AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		
		// nahodenie ringer modu
		switch (profile.getVolumeRingerMode()) {
			case 1:  // Ring
				audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
				break;
			case 2:  // Ring & Vibrate
				audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
				break;
			case 3:  // Vibrate
				audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
				break;
			case 4:  // Silent
				audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
				audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
				break;
		}
		
		// nahodenie volume
		if (profile.getVolumeRingtoneChange())
			audioManager.setStreamVolume(AudioManager.STREAM_RING, profile.getVolumeRingtoneValue(), 0);
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_RING, profile.getVolumeRingtoneValue());
		if (profile.getVolumeNotificationChange())
			audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, profile.getVolumeNotificationValue(), 0);
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_NOTIFICATION, profile.getVolumeNotificationValue());
		if (profile.getVolumeMediaChange())
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, profile.getVolumeMediaValue(), 0);
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_MUSIC, profile.getVolumeMediaValue());
		if (profile.getVolumeAlarmChange())
			audioManager.setStreamVolume(AudioManager.STREAM_ALARM, profile.getVolumeAlarmValue(), 0);
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_ALARM, profile.getVolumeAlarmValue());
		if (profile.getVolumeSystemChange())
			audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, profile.getVolumeSystemValue(), 0);
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_SYSTEM, profile.getVolumeSystemValue());
		if (profile.getVolumeVoiceChange())
			audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, profile.getVolumeVoiceValue(), 0);
			//Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_VOICE, profile.getVolumeVoiceValue());

		// nahodenie ringtone
		if (profile.getSoundRingtoneChange())
			Settings.System.putString(getContentResolver(), Settings.System.RINGTONE, profile.getSoundRingtone());
		if (profile.getSoundNotificationChange())
			Settings.System.putString(getContentResolver(), Settings.System.NOTIFICATION_SOUND, profile.getSoundNotification());
		if (profile.getSoundAlarmChange())
			Settings.System.putString(getContentResolver(), Settings.System.ALARM_ALERT, profile.getSoundAlarm());

		// nahodenie WiFi
		WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		boolean setWifiState = false;
		switch (profile.getDeviceWiFi()) {
			case 1 :
				setWifiState = true;
				break;
			case 2 : 
				setWifiState = false;
				break;
			case 3 :
				int wifiState = wifiManager.getWifiState(); 
				if ((wifiState == WifiManager.WIFI_STATE_DISABLED) || (wifiState == WifiManager.WIFI_STATE_DISABLING))
				{
					setWifiState = true;
				}
				else
				if ((wifiState == WifiManager.WIFI_STATE_ENABLED) || (wifiState == WifiManager.WIFI_STATE_ENABLING))
				{
					setWifiState = false;
				}
				break;
		}
		if (profile.getDeviceWiFi() != 0)
		{
			try {
				wifiManager.setWifiEnabled(setWifiState);
			} catch (Exception e) {
				// barla pre security exception INTERACT_ACROSS_USERS - chyba ROM 
				wifiManager.setWifiEnabled(setWifiState);
			}
		}	

		// nahodenie bluetooth
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		switch (profile.getDeviceBluetooth()) {
			case 1 :
				bluetoothAdapter.enable();
				break;
			case 2 : 
				bluetoothAdapter.disable();
				break;
			case 3 :
				if ((bluetoothAdapter != null) && (bluetoothAdapter.isEnabled()))
				{
					bluetoothAdapter.enable();
				}
				else
				if ((bluetoothAdapter != null) && (!bluetoothAdapter.isEnabled()))
				{
					bluetoothAdapter.disable();
				}
				break;
		}
		
		// nahodenie airplane modu
		boolean _isAirplaneMode = isAirplaneMode(getApplicationContext());
		boolean _setAirplaneMode = false;
		switch (profile.getDeviceAirplaneMode()) {
			case 1:
				if (!_isAirplaneMode)
				{
					_isAirplaneMode = true;
					_setAirplaneMode = true;
				}
				break;
			case 2:
				if (_isAirplaneMode)
				{
					_isAirplaneMode = false;
					_setAirplaneMode = true;
				}
				break;
			case 3:
				_isAirplaneMode = !_isAirplaneMode;
				_setAirplaneMode = true;
				break;
		}
		if (_setAirplaneMode)
			setAirplaneMode(getApplicationContext(), _isAirplaneMode);
		
		
/*		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		String enabledRadios = "";
		String disabledRadios = "";
			
		// nahodenie WiFi
		switch (profile.getDeviceWiFi()) {
			case 1 : 
				if (!enabledRadios.isEmpty())
					enabledRadios = enabledRadios + ",";
				enabledRadios = enabledRadios + "wifi";
				break;
			case 2 : 
				if (!disabledRadios.isEmpty())
					disabledRadios = disabledRadios + ",";
				disabledRadios = disabledRadios + "wifi";
				break;
			case 3 :
				int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
				if ((extraWifiState == WifiManager.WIFI_STATE_DISABLED) || (extraWifiState == WifiManager.WIFI_STATE_DISABLING))
				{
					if (!enabledRadios.isEmpty())
						enabledRadios = enabledRadios + ",";
					enabledRadios = enabledRadios + "wifi";
				}
				else
				if ((extraWifiState == WifiManager.WIFI_STATE_ENABLED) || (extraWifiState == WifiManager.WIFI_STATE_ENABLING))
				{
					if (!disabledRadios.isEmpty())
						disabledRadios = disabledRadios + ",";
					disabledRadios = disabledRadios + "wifi";
				}
				break;
		}
		
		// nahodenie Bluetooth
		switch (profile.getDeviceBluetooth()) {
			case 1 : 
				if (!enabledRadios.isEmpty())
					enabledRadios = enabledRadios + ",";
				enabledRadios = enabledRadios + "bluetooth";
				break;
			case 2 : 
				if (!disabledRadios.isEmpty())
					disabledRadios = disabledRadios + ",";
				disabledRadios = disabledRadios + "bluetooth";
				break;
			case 3 :
				BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if ((bluetoothAdapter != null) && (bluetoothAdapter.isEnabled()))
				{
					if (!enabledRadios.isEmpty())
						enabledRadios = enabledRadios + ",";
					enabledRadios = enabledRadios + "bluetooth";
				}
				else
				if ((bluetoothAdapter != null) && (!bluetoothAdapter.isEnabled()))
				{
					if (!disabledRadios.isEmpty())
						disabledRadios = disabledRadios + ",";
					disabledRadios = disabledRadios + "bluetooth";
				}
				break;
		}

		// nahodenie airplane mode
		switch (profile.getDeviceAirplaneMode()) {
			case 1 :
				// ak zapinam airplane mod disablujem vsetko
				disabledRadios = "cell,bluetooth,wifi,nfc";
				break;
			case 2 : 
				// ak vypinam airplane mod, enablujem este cell a ncf
				if (!enabledRadios.isEmpty())
					enabledRadios = enabledRadios + ",";
				enabledRadios = enabledRadios + "cell,nfc";
				break;
			case 3 :
				if (Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 0)
				{
					disabledRadios = "cell,bluetooth,wifi,nfc";
				}
				else
				{
					if (!enabledRadios.isEmpty())
						enabledRadios = enabledRadios + ",";
					enabledRadios = enabledRadios + "cell,nfc";
				} 
				break;
		}
		
		Log.d("PhoneProfilesActivity.activateProfile", "enabledRadios=" + enabledRadios);
		Log.d("PhoneProfilesActivity.activateProfile", "disabledRadios=" + disabledRadios);
		
		// zapni radia
		if (!enabledRadios.isEmpty())
		{
			Settings.System.putInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
			Settings.System.putString(getContentResolver(), Settings.System.AIRPLANE_MODE_RADIOS, enabledRadios);
			intent.putExtra("state", false);
			sendBroadcast(intent);
		}

		// vypni radia
		if (!disabledRadios.isEmpty())
		{
			Settings.System.putInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 1);
			Settings.System.putString(getContentResolver(), Settings.System.AIRPLANE_MODE_RADIOS, disabledRadios);
			intent.putExtra("state", true);
			sendBroadcast(intent);
		}

		// nastav default systemu
		Settings.System.putString(getContentResolver(), Settings.System.AIRPLANE_MODE_RADIOS, "cell,wifi,bluetooth,nfc");
*/
		
		// screen timeout
		switch (profile.getDeviceScreenTimeout()) {
			case 1:
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 15000);
				break;
			case 2:
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 30000);
				break;
			case 3:
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 60000);
				break;
			case 4:
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 120000);
				break;
			case 5:
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 600000);
				break;
			case 6:
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, -1);
				break;
		}
		
		// nahodenie podsvietenia
		if (profile.getDeviceBrightnessChange())
		{
			Window window = getWindow();
			LayoutParams layoutParams = window.getAttributes();
			
			if (profile.getDeviceBrightnessAutomatic())
			{
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
				layoutParams.screenBrightness = LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
			}
			else
			{
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, profile.getDeviceBrightnessValue());
				layoutParams.screenBrightness = profile.getDeviceBrightnessValue() / 255.0f;
			}
			
			window.setAttributes(layoutParams);
		}
		
		// nahodenie pozadia
		if (profile.getDeviceWallpaperChange())
		{
			Log.d("PhoneProfilesActivity.activateProfile","set wallpaper");
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			int height = displayMetrics.heightPixels;
			int width = displayMetrics.widthPixels << 1; // best wallpaper width is twice screen width
			// first decode with inJustDecodeDpunds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(profile.getDeviceWallpaperIdentifier(), options);
			// calaculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, width, height);
			// decode bitmap with inSampleSize
			options.inJustDecodeBounds = false;
			Bitmap decodedSampleBitmap = BitmapFactory.decodeFile(profile.getDeviceWallpaperIdentifier(), options);
			// set wallpaper
			WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
			try {
				wallpaperManager.setBitmap(decodedSampleBitmap);
			} catch (IOException e) {
				Log.e("PhoneProfilesActivity.activateProfile", "Cannot set wallpaper. Image="+profile.getDeviceWallpaperIdentifier());
			}
		}	
		
		if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_APPLICATION_CLOSE, true))
		{	
			// ma sa zatvarat aktivita po aktivacii
			if (applicationStarted)
				// aplikacia je uz spustena, mozeme aktivitu zavriet
				// tymto je vyriesene, ze pri spusteni aplikacie z launchera
				// sa hned nezavrie
				finish();
		}
	}
	
	private void activateProfile(int position)
	{
		Profile profile = profileList.get(position);
		activateProfile(profile);
	}
	
	private boolean isAirplaneMode(Context context)
	{
    	if (android.os.Build.VERSION.SDK_INT >= 17)
    		return AirPlaneMode_SDK17.getAirplaneMode(context);
    	else
    		return AirPlaneMode_SDK8.getAirplaneMode(context);
	}
	
	private void setAirplaneMode(Context context, boolean mode)
	{
    	if (android.os.Build.VERSION.SDK_INT >= 17)
    		AirPlaneMode_SDK17.setAirplaneMode(context, mode);
    	else
    		AirPlaneMode_SDK8.setAirplaneMode(context, mode);
	}
	
	private void setLanguage()
	{
		SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
		
		// jazyk na aky zmenit
		String lang = preferences.getString(PhoneProfilesPreferencesActivity.PREF_APPLICATION_LANGUAGE, "system");
		
		// jazyk aplikacie
		String defaultLanguage = getBaseContext().getResources().getConfiguration().locale.getLanguage();
		
		// actualLanguage = posledne nastaveny jazyk

		if (actualLanguage.equals("system"))
			// predpokladam, ze jazyk aplikacie = poslende nastaveny jazyk
			defaultLanguage = "system";
		
		Log.d("PhoneProfilesActivity.setLanguauge", actualLanguage);
		
		if ((!actualLanguage.equals(lang)) || (!defaultLanguage.equals(actualLanguage)))
		{
			Log.d("PhoneProfilesActivity.setLanguauge", lang);

			Locale appLocale;
			
			if (!lang.equals("system"))
			{
				appLocale = new Locale(lang);
			}
			else
			{
				appLocale = Resources.getSystem().getConfiguration().locale;
			}
			
			Locale.setDefault(appLocale);
			Configuration appConfig = new Configuration();
			appConfig.locale = appLocale;
			getBaseContext().getResources().updateConfiguration(appConfig, getBaseContext().getResources().getDisplayMetrics());
			
			actualLanguage = lang;
			
			// zavretie aktivity
			finish();
		}	
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		// raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		
		if (height > reqHeight || width > reqWidth)
		{
			// calculate ratios of height and width to requested height an width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			
			// choose the smalest ratio as InSamleSize value, this will guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width
			inSampleSize = (heightRatio < widthRatio) ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	
	public static DatabaseHandler getDatabaseHandler()
	{
		return databaseHandler;
	}
	

}
