package sk.henrichg.phoneprofiles;

import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockActivity;

import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mobeta.android.dslv.DragSortListView;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneProfilesActivity extends SherlockActivity {

	//private DatabaseHandler databaseHandler;
	private ActivateProfileHelper activateProfileHelper;
	private List<Profile> profileList;
	private static MainProfileListAdapter profileListAdapter;
	private DragSortListView listView;
	private TextView activeProfileName;
	private ImageView activeProfileIcon;
	private int startupSource = 0;
	//private static boolean applicationStarted;
	private Intent intent;
	//private static boolean languageChanged = false;
	private static ApplicationsCache applicationsCache;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//PhoneProfilesPreferencesActivity.loadPreferences(getBaseContext());
		
		setTheme(this, false);
		setLanguage(getBaseContext());
		
		setContentView(R.layout.activity_phone_profiles);

		//getSupportActionBar().setHomeButtonEnabled(true);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		intent = getIntent();
		startupSource = intent.getIntExtra(GlobalData.EXTRA_START_APP_SOURCE, 0);
		
		
	/*	getSupportActionBar().setDisplayShowTitleEnabled(false);
		
		// Create an array adapter to populate dropdownlist 
	    ArrayAdapter<CharSequence> navigationAdapter =
	            ArrayAdapter.createFromResource(getBaseContext(), R.array.phoneProfilesNavigator, R.layout.sherlock_spinner_item);

	    // Enabling dropdown list navigation for the action bar 
	    getSupportActionBar().setNavigationMode(com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_LIST);
	*/
		// na onCreate dame, ze aplikacia este nie je nastartovana
		//applicationStarted = false;
		
		activateProfileHelper = new ActivateProfileHelper(this, getBaseContext());

		//databaseHandler = new DatabaseHandler(this);
		
		applicationsCache = new ApplicationsCache();
		
		activeProfileName = (TextView)findViewById(R.id.activated_profile_name);
		activeProfileIcon = (ImageView)findViewById(R.id.activated_profile_icon);
		listView = (DragSortListView)findViewById(R.id.main_profiles_list);
		
		profileList = GlobalData.getProfileList();

		profileListAdapter = new MainProfileListAdapter(this, profileList);
		
		listView.setAdapter(profileListAdapter);
		
		//registerForContextMenu(listView);
		//listView.setLongClickable(false);

	/*    // Defining Navigation listener 
	    ActionBar.OnNavigationListener navigationListener = new ActionBar.OnNavigationListener() {

	        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
	            switch(itemPosition) {
	            case 0:
	            	//
	                break;
	            case 1:
	                //...
	                break;
	            }
	            return false;
	        }
	    };

	    // Setting dropdown items and item navigation listener for the actionbar 
	    getSupportActionBar().setListNavigationCallbacks(navigationAdapter, navigationListener);
	    navigationAdapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
	*/	
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Log.d("PhoneProfilesActivity.onItemClick", "xxxx");

				//SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
				//if (!preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_APPLICATION_LONG_PRESS_ACTIVATION, false))
				//	activateProfileWithAlert(position);
				
				startProfilePreferencesActivity(position);
				
			}
			
		}); 
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				Log.d("PhoneProfilesActivity.onItemLongClick", "xxxx");
				
				if (!MainProfileListAdapter.editIconClicked) // workaround
				{
					//SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
					//if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_APPLICATION_LONG_PRESS_ACTIVATION, false))
						activateProfileWithAlert(position);
				}
				
				MainProfileListAdapter.editIconClicked = false;
				
				return false;
			}
			
		});
		
        listView.setDropListener(new DragSortListView.DropListener() {
            public void drop(int from, int to) {
            	profileListAdapter.changeItemOrder(from, to);
            	GlobalData.getDatabaseHandler().setPOrder(profileList);
        		//Log.d("PhoneProfileActivity.drop", "xxxx");
            }
        });
        
        //listView.setRemoveListener(onRemove);
		
		
		//Log.d("PhoneProfileActivity.onCreate", "xxxx");
		
	}

/*	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		
		if (view.getId() == R.id.main_profiles_list) {
			//AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			//Profile profile;
			
			//profile = profileList.get(info.position);

			//menu.setHeaderTitle(getResources().getString(R.string.profile_context_header) + ": " + profile.getName());
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
			//Log.d("PhoneProfileActivity.onContextItemSelected", "Edit");

			startProfilePreferencesActivity(info.position);
			
			return true;
		case 1002:
			//Log.d("PhoneProfileActivity.onContextItemSelected", "Duplicate");
			
			duplicateProfile(position);
			
			return true;
		case 1003:
			//Log.d("PhoneProfileActivity.onContextItemSelected", "Delete");

			deleteProfile(position);
			
			return true;
		default:
			return false;
		}
	}
*/	
	
	@Override
	protected void onStart()
	{
		super.onStart();

		Log.d("PhoneProfilesActivity.onStart", "xxx");
		
		if (PhoneProfilesPreferencesActivity.getInvalidateLoader())
		{
			Log.d("PhoneProfilesActivity.onStart", "invalidate");

		//	ViewGroup vg = (ViewGroup) findViewById(R.id.layout_phone_profiles_activity);
		//	vg.invalidate();
			
			// refresh activity
			Intent refresh = new Intent(getBaseContext(), PhoneProfilesActivity.class);
			startActivity(refresh);
			finish();
			
			return;
		}
		
	/*	if (applicationStarted && languageChanged)
		{
			// zrusenie notifikacie
			activateProfileHelper.showNotification(null);
			finish();
			return;
		} */

		//Log.d("PhoneProfilesActivity.onStart", "startupSource="+startupSource);
		
		Profile profile;
		
		// pre profil, ktory je prave aktivny, treba aktualizovat aktivitu
		profile = GlobalData.getActivatedProfile();
		updateHeader(profile);
		
		if (startupSource == 0)
		{
			// aktivita nebola spustena z notifikacie, ani z widgetu
			// pre profil, ktory je prave aktivny, treba aktualizovat notifikaciu a widgety 
			activateProfileHelper.showNotification(profile);
			activateProfileHelper.updateWidget();
		}

		// reset, aby sa to dalej chovalo ako normalne spustenie z lauchera
		startupSource = 0;
		
	/*	// na onStart dame, ze aplikacia uz je nastartovana
		applicationStarted = true; */
		
		//Log.d("PhoneProfileActivity.onStart", "xxxx");
		
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
		applicationsCache.clearCache();
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
			//Log.d("PhoneProfileActivity.onOptionsItemSelected", "menu_new_profile");

			startProfilePreferencesActivity(-1);
			
			return true;
		case R.id.menu_delete_all_profiles:
			//Log.d("PhoneProfilesActivity.onOptionsItemSelected", "menu_delete_all_profiles");
			
			deleteAllProfiles();
			
			return true;
			
		case R.id.menu_settings:
			//Log.d("PhoneProfilesActivity.onOptionsItemSelected", "menu_settings");
			
			Intent intent = new Intent(getBaseContext(), PhoneProfilesPreferencesActivity.class);

			startActivity(intent);

			return true;
		case R.id.menu_export:
			//Log.d("PhoneProfilesActivity.onOptionsItemSelected", "menu_export");

			exportProfiles();
			
			return true;
		case R.id.menu_import:
			//Log.d("PhoneProfilesActivity.onOptionsItemSelected", "menu_import");

			importProfiles();
			
			return true;
		case R.id.menu_exit:
			//Log.d("PhoneProfilesActivity.onOptionsItemSelected", "menu_exit");
			
			// zrusenie notifikacie
			activateProfileHelper.showNotification(null);
			
			finish();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
		//setContentView(R.layout.activity_phone_profiles);
		Intent intent = getIntent();
		startActivity(intent);
		finish();
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
								  GlobalData.PROFILE_ICON_DEFAULT + "|1", 
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
								  "-|0",
								  0,
								  false,
								  0,
								  false,
								  "-"
					);
			profileListAdapter.addItem(profile); // pridame profil do listview a nastavime jeho order
			GlobalData.getDatabaseHandler().addProfile(profile);
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
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_MOBILE_DATA, Integer.toString(profile.getDeviceMobileData()));
        editor.putBoolean(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS, profile.getDeviceMobileDataPrefs());
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_GPS, Integer.toString(profile.getDeviceGPS()));
        editor.putBoolean(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_RUN_APPLICATION_CHANGE, profile.getDeviceRunApplicationChange());
        editor.putString(ProfilePreferencesActivity.PREF_PROFILE_DEVICE_RUN_APPLICATION_PACKAGE_NAME, profile.getDeviceRunApplicationPackageName());
		editor.commit();
		
		//Log.d("PhoneProfilesActivity.startProfilePreferencesActivity", profile.getID()+"");
		
		Intent intent = new Intent(getBaseContext(), ProfilePreferencesActivity.class);
		intent.putExtra(GlobalData.EXTRA_PROFILE_POSITION, profileListAdapter.getItemId(profile));

		//Log.d("PhoneProfilesActivity.startProfilePreferencesActivity", profile.getChecked()+"");
		
		startActivity(intent);
		
	}

	public void duplicateProfile(int position)
	{
		Profile origProfile = profileList.get(position);

		Profile newProfile = new Profile(
				   origProfile.getName()+"_d", 
				   origProfile.getIcon(), 
				   false, 
				   origProfile.getPOrder(),
				   origProfile.getVolumeRingerMode(),
				   origProfile.getVolumeRingtone(),
				   origProfile.getVolumeNotification(),
				   origProfile.getVolumeMedia(),
				   origProfile.getVolumeAlarm(),
				   origProfile.getVolumeSystem(),
				   origProfile.getVolumeVoice(),
				   origProfile.getSoundRingtoneChange(),
				   origProfile.getSoundRingtone(),
				   origProfile.getSoundNotificationChange(),
				   origProfile.getSoundNotification(),
				   origProfile.getSoundAlarmChange(),
				   origProfile.getSoundAlarm(),
				   origProfile.getDeviceAirplaneMode(),
				   origProfile.getDeviceWiFi(),
				   origProfile.getDeviceBluetooth(),
				   origProfile.getDeviceScreenTimeout(),
				   origProfile.getDeviceBrightness(),
				   origProfile.getDeviceWallpaperChange(),
				   origProfile.getDeviceWallpaper(),
				   origProfile.getDeviceMobileData(),
				   origProfile.getDeviceMobileDataPrefs(),
				   origProfile.getDeviceGPS(),
				   origProfile.getDeviceRunApplicationChange(),
				   origProfile.getDeviceRunApplicationPackageName());

		profileListAdapter.addItem(newProfile);
		GlobalData.getDatabaseHandler().addProfile(newProfile);
		
		//updateListView();

		startProfilePreferencesActivity(profileList.size()-1);
	}

	public void deleteProfile(int position)
	{
		final Profile profile = profileList.get(position);

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(getResources().getString(R.string.profile_string_0) + ": " + profile.getName());
		dialogBuilder.setMessage(getResources().getString(R.string.delete_profile_alert_message) + "?");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				profileListAdapter.deleteItem(profile);
				GlobalData.getDatabaseHandler().deleteProfile(profile);
				//updateListView();
				// v pripade, ze sa odmaze aktivovany profil, nastavime, ze nic nie je aktivovane
				//Profile profile = databaseHandler.getActivatedProfile();
				Profile profile = profileListAdapter.getActivatedProfile();
				updateHeader(profile);
				activateProfileHelper.showNotification(profile);
				activateProfileHelper.updateWidget();
			}
		});
		dialogBuilder.setNegativeButton(android.R.string.no, null);
		dialogBuilder.show();
	}

	private void deleteAllProfiles()
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(getResources().getString(R.string.alert_title_delete_all_profiles));
		dialogBuilder.setMessage(getResources().getString(R.string.alert_message_delete_all_profiles) + "?");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				profileListAdapter.clear();
				GlobalData.getDatabaseHandler().deleteAllProfiles();
				//updateListView();
				// v pripade, ze sa odmaze aktivovany profil, nastavime, ze nic nie je aktivovane
				//Profile profile = databaseHandler.getActivatedProfile();
				Profile profile = profileListAdapter.getActivatedProfile();
				updateHeader(profile);
				activateProfileHelper.showNotification(profile);
				activateProfileHelper.updateWidget();
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
        		Resources resources = getResources();
        		int height = (int) resources.getDimension(android.R.dimen.app_icon_size);
        		int width = (int) resources.getDimension(android.R.dimen.app_icon_size);
        		Bitmap bitmap = BitmapResampler.resample(profile.getIconIdentifier(), width, height);

	        	activeProfileIcon.setImageBitmap(bitmap);
	        }
		}
		
		ImageView profilePrefIndicatorImageView = (ImageView)findViewById(R.id.activated_profile_pref_indicator);
		if (GlobalData.applicationEditorPrefIndicator)
			profilePrefIndicatorImageView.setImageBitmap(ProfilePreferencesIndicator.paint(profile, getBaseContext()));
		else
			profilePrefIndicatorImageView.setImageBitmap(null);
		
	}
	
	public void activateProfileWithAlert(int position)
	{
	//	SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);

	//	if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_APPLICATION_ALERT, true))
	//	{	
			final int _position = position;
			final Profile profile = profileList.get(_position);

			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle(getResources().getString(R.string.profile_string_0) + ": " + profile.getName());
			dialogBuilder.setMessage(getResources().getString(R.string.activate_profile_alert_message) + "?");
			//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
			dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					activateProfile(_position, true);
				}
			});
			dialogBuilder.setNegativeButton(android.R.string.no, null);
			dialogBuilder.show();
	//	}
	//	else
	//		activateProfile(position, true);
	}
	
	private void activateProfile(Profile profile, boolean interactive)
	{
		profileListAdapter.activateProfile(profile);
		GlobalData.getDatabaseHandler().activateProfile(profile);
		
		activateProfileHelper.execute(profile, interactive);

		updateHeader(profile);
		activateProfileHelper.updateWidget();

		if (GlobalData.notificationsToast)
		{	
			// toast notification
			Toast msg = Toast.makeText(getBaseContext(), 
					getResources().getString(R.string.toast_profile_activated_0) + ": " + profile.getName() + " " +
					getResources().getString(R.string.toast_profile_activated_1), 
					Toast.LENGTH_LONG);
			msg.show();
		}

		activateProfileHelper.showNotification(profile);

	/*	if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_APPLICATION_CLOSE, true))
		{	
			// ma sa zatvarat aktivita po aktivacii
			if (applicationStarted)
				// aplikacia je uz spustena, mozeme aktivitu zavriet
				// tymto je vyriesene, ze pri spusteni aplikacie z launchera
				// sa hned nezavrie
				finish();
		}  */
	}
	
	private void activateProfile(int position, boolean interactive)
	{
		Profile profile = profileList.get(position);
		activateProfile(profile, interactive);
	}
	
	private void importExportErrorDialog(int importExport)
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(getResources().getString(R.string.import_profiles_alert_title));
		String resMessage;
		if (importExport == 1)
			resMessage = getResources().getString(R.string.import_profiles_alert_error);
		else
			resMessage = getResources().getString(R.string.export_profiles_alert_error);
		dialogBuilder.setMessage(resMessage + "!");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dialogBuilder.setPositiveButton(android.R.string.ok, null);
		dialogBuilder.show();
	}
	
	private void importProfiles()
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(getResources().getString(R.string.import_profiles_alert_title));
		dialogBuilder.setMessage(getResources().getString(R.string.import_profiles_alert_message) + "?");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				if (GlobalData.getDatabaseHandler().importDB()  == 1)
				{

					// toast notification
					Toast msg = Toast.makeText(getBaseContext(), 
							getResources().getString(R.string.toast_import_ok), 
							Toast.LENGTH_LONG);
					msg.show();
					
					activateProfileHelper.showNotification(null);
					finish();
				
				}
				else
				{
					importExportErrorDialog(1);
				}
			}
		});
		dialogBuilder.setNegativeButton(android.R.string.no, null);
		dialogBuilder.show();
	}

	private void exportProfiles()
	{
		if (GlobalData.getDatabaseHandler().exportDB() == 1)
		{

			// toast notification
			Toast msg = Toast.makeText(getBaseContext(), 
					getResources().getString(R.string.toast_export_ok), 
					Toast.LENGTH_LONG);
			msg.show();
		
		}
		else
		{
			importExportErrorDialog(2);
		}
		
	}
	
	
	public static void setLanguage(Context context)//, boolean restart)
	{
		// jazyk na aky zmenit
		String lang = GlobalData.applicationLanguage;
		
		//Log.d("PhoneProfilesActivity.setLanguauge", lang);

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
		
		//languageChanged = restart;
	}
	
	public static void setTheme(Activity activity, boolean forPopup)
	{
		if (GlobalData.applicationTheme.equals("light"))
		{
			Log.d("PhoneProfilesActivity.setTheme","light");
			if (forPopup)
				activity.setTheme(R.style.PopupTheme);
			else
				activity.setTheme(R.style.Theme_Phoneprofilestheme);
		}
		else
		if (GlobalData.applicationTheme.equals("dark"))
		{
			Log.d("PhoneProfilesActivity.setTheme","dark");
			if (forPopup)
				activity.setTheme(R.style.PopupTheme_dark);
			else
				activity.setTheme(R.style.Theme_Phoneprofilestheme_dark);
		}
	}
	
	public static MainProfileListAdapter getProfileListAdapter()
	{
		return profileListAdapter;
	}

	public static ApplicationsCache getApplicationsCache()
	{
		return applicationsCache;
	}
	
}
