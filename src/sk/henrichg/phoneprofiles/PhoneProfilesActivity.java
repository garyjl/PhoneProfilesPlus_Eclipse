package sk.henrichg.phoneprofiles;

import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockActivity;

import android.os.Bundle;
import android.provider.Settings;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
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
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mobeta.android.dslv.DragSortListView;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneProfilesActivity extends SherlockActivity {

	private static DatabaseHandler databaseHandler;
	private NotificationManager notificationManager;
	private ActivateProfileHelper activateProfileHelper;
	private List<Profile> profileList;
	private static MainProfileListAdapter profileListAdapter;
	private DragSortListView listView;
	private TextView activeProfileName;
	private ImageView activeProfileIcon;
	private int startupSource = 0;
	private static boolean applicationStarted;
	private Intent intent;
	private static boolean languageChanged = false;

	static final String EXTRA_PROFILE_POSITION = "profile_position";
	static final String EXTRA_PROFILE_ID = "profile_id";
	static final String EXTRA_START_APP_SOURCE = "start_app_source";
	
	static final String PROFILE_ICON_DEFAULT = "ic_profile_default";
	
	static final int STARTUP_SOURCE_NOTIFICATION = 1;
	static final int STARTUP_SOURCE_WIDGET = 2;
	static final int STARTUP_SOURCE_SHORTCUT = 3;
	static final int STARTUP_SOURCE_BOOT = 4;

	static final int NOTIFICATION_ID = 700420;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setLanguage(getBaseContext(), false);
		
		setContentView(R.layout.activity_phone_profiles);
		
		// na onCreate dame, ze aplikacia este nie je nastartovana
		applicationStarted = false;
		
		intent = getIntent();
		startupSource = intent.getIntExtra(EXTRA_START_APP_SOURCE, 0);
		
		activateProfileHelper = new ActivateProfileHelper(this, getBaseContext());

		databaseHandler = new DatabaseHandler(this);
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		activeProfileName = (TextView)findViewById(R.id.activated_profile_name);
		activeProfileIcon = (ImageView)findViewById(R.id.activated_profile_icon);
		listView = (DragSortListView)findViewById(R.id.main_profiles_list);
		
		profileList = databaseHandler.getAllProfiles();

		profileListAdapter = new MainProfileListAdapter(this, profileList);
		
		listView.setAdapter(profileListAdapter);
		
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Log.d("PhoneProfilesActivity.onItemClick", "xxxx");
				
				activateProfileWithAlert(position);

			}
			
		});
		
        listView.setDropListener(new DragSortListView.DropListener() {
            public void drop(int from, int to) {
            	profileListAdapter.changeItemOrder(from, to);
            	databaseHandler.setPOrder(profileList);
        		Log.d("PhoneProfileActivity.drop", "xxxx");
            }
        });
        
        //listView.setRemoveListener(onRemove);
		
		
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
		
		if (applicationStarted && languageChanged)
		{
			// zrusenie notifikacie
			showNotification(null);
			finish();
		}

		Log.d("PhoneProfilesActivity.onStart", "startupSource="+startupSource);
		
		boolean actProfile = false;
		if (startupSource == 0)
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
		
		if (actProfile && (profile != null))
		{
			// aktivacia profilu
			activateProfile(profile);
		}
		
		// reset, aby sa to dalej chovalo ako normalne spustenie z lauchera
		startupSource = 0;

		// na onStart dame, ze aplikacia uz je nastartovana
		applicationStarted = true;
		
		Log.d("PhoneProfileActivity.onStart", "xxxx");
		
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
			profileListAdapter.addItem(profile); // pridame profil do listview a nastavime jeho order
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
		intent.putExtra(EXTRA_PROFILE_POSITION, profileListAdapter.getItemId(profile));

		startActivity(intent);
		
	}

	private void duplicateProfile(int position)
	{
		Profile profile = profileList.get(position);

		profile.setName(profile.getName()+"_d");
		profile.setChecked(false);
		profileListAdapter.addItem(profile);
		databaseHandler.addProfile(profile);
		
		//updateListView();

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
				profileListAdapter.deleteItem(profile);
				databaseHandler.deleteProfile(profile);
				//updateListView();
				// v pripade, ze sa odmaze aktivovany profil, nastavime, ze nic nie je aktivovane
				//Profile profile = databaseHandler.getActivatedProfile();
				Profile profile = profileListAdapter.getActivatedProfile();
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
				intent.putExtra(EXTRA_START_APP_SOURCE, STARTUP_SOURCE_NOTIFICATION);
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
				@SuppressWarnings("deprecation")
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
	
	private void activateProfile(Profile profile)
	{
		SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
		
		profileListAdapter.activateProfile(profile);
		databaseHandler.activateProfile(profile);
		
		activateProfileHelper.execute(profile);

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
	
	public static void setLanguage(Context context, boolean restart)
	{
		SharedPreferences preferences = context.getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
		
		// jazyk na aky zmenit
		String lang = preferences.getString(PhoneProfilesPreferencesActivity.PREF_APPLICATION_LANGUAGE, "system");
		
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
		context.getResources().updateConfiguration(appConfig, context.getResources().getDisplayMetrics());
		
		languageChanged = restart;
	}
	
	public static DatabaseHandler getDatabaseHandler()
	{
		return databaseHandler;
	}
	
	public static MainProfileListAdapter getProfileListAdapter()
	{
		return profileListAdapter;
	}

}
