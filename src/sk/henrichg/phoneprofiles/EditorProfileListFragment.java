package sk.henrichg.phoneprofiles;

import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockFragment;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EditorProfileListFragment extends SherlockFragment {

	private ActivateProfileHelper activateProfileHelper;
	private List<Profile> profileList;
	private static EditorProfileListAdapter profileListAdapter;
	private DragSortListView listView;
	private TextView activeProfileName;
	private ImageView activeProfileIcon;
	private int startupSource = 0;
	private Intent intent;
	
	public EditorProfileListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		intent = getActivity().getIntent();
		startupSource = intent.getIntExtra(GlobalData.EXTRA_START_APP_SOURCE, 0);
		
		activateProfileHelper = new ActivateProfileHelper(getActivity(), getActivity().getBaseContext());
		profileList = GlobalData.getProfileList();
		profileListAdapter = new EditorProfileListAdapter(this, profileList);
		
		setHasOptionsMenu(true);

		//Log.d("EditorProfileListFragment.onCreate", "xxxx");
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView;
		
		if (GlobalData.applicationEditorPrefIndicator && GlobalData.applicationEditorHeader)
			rootView = inflater.inflate(R.layout.fragment_profile_list, container, false); 
		else
		if (GlobalData.applicationEditorHeader)
			rootView = inflater.inflate(R.layout.fragment_profile_list_no_indicator, container, false); 
		else
			rootView = inflater.inflate(R.layout.fragment_profile_list_no_header, container, false); 

		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		// az tu mame layout, tak mozeme ziskat view-y
		activeProfileName = (TextView)getActivity().findViewById(R.id.activated_profile_name);
		activeProfileIcon = (ImageView)getActivity().findViewById(R.id.activated_profile_icon);
		listView = (DragSortListView)getActivity().findViewById(R.id.main_profiles_list);

		listView.setAdapter(profileListAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Log.d("EditorProfileListFragment.onItemClick", "xxxx");

				startProfilePreferencesActivity(position);
				
			}
			
		}); 
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				Log.d("EditorProfileListFragment.onItemLongClick", "xxxx");
				
				if (!MainProfileListAdapter.editIconClicked) // workaround
				{
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
        		//Log.d("EditorProfileListFragment.drop", "xxxx");
            }
        });
        
        //listView.setRemoveListener(onRemove);

		Log.d("EditorProfileListFragment.onActivityCreated", "xxx");
        
	}
	
	@Override
	public void onStart()
	{
		super.onStart();

		// ak sa ma refreshnut aktivita, nebudeme robit nic, co je v onStart
		if (PhoneProfilesPreferencesActivity.getInvalidateEditor(false))
			return;
		
		Log.d("EditorProfileListFragment.onStart", "xxx");
		
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
		
		//Log.d("EditorProfileListFragment.onStart", "xxxx");
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.fragment_editor_profile_list, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_new_profile:
			//Log.d("PhoneProfileActivity.onOptionsItemSelected", "menu_new_profile");

			startProfilePreferencesActivity(-1);
			
			return true;
		case R.id.menu_delete_all_profiles:
			//Log.d("EditorProfileListFragment.onOptionsItemSelected", "menu_delete_all_profiles");
			
			deleteAllProfiles();
			
			return true;
			
		case R.id.menu_export:
			//Log.d("EditorProfileListFragment.onOptionsItemSelected", "menu_export");

			exportProfiles();
			
			return true;
		case R.id.menu_import:
			//Log.d("EditorProfileListFragment.onOptionsItemSelected", "menu_import");

			importProfiles();
			
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

		
        preferences = getActivity().getSharedPreferences(ProfilePreferencesActivity.PREFS_NAME, Activity.MODE_PRIVATE);
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
		
		//Log.d("EditorProfileListFragment.startProfilePreferencesActivity", profile.getID()+"");
		
		Intent intent = new Intent(getActivity().getBaseContext(), ProfilePreferencesActivity.class);
		intent.putExtra(GlobalData.EXTRA_PROFILE_POSITION, profileListAdapter.getItemId(profile));

		//Log.d("EditorProfileListFragment.startProfilePreferencesActivity", profile.getChecked()+"");
		
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

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
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
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
		dialogBuilder.setTitle(getResources().getString(R.string.alert_title_delete_all_profiles));
		dialogBuilder.setMessage(getResources().getString(R.string.alert_message_delete_all_profiles) + "?");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				GlobalData.getDatabaseHandler().deleteAllProfiles();
				profileListAdapter.clear();
				//updateListView();
				// v pripade, ze sa odmaze aktivovany profil, nastavime, ze nic nie je aktivovane
				//Profile profile = databaseHandler.getActivatedProfile();
				//Profile profile = profileListAdapter.getActivatedProfile();
				updateHeader(null);
				activateProfileHelper.showNotification(null);
				activateProfileHelper.updateWidget();
			}
		});
		dialogBuilder.setNegativeButton(android.R.string.no, null);
		dialogBuilder.show();
	}
	
	private void updateHeader(Profile profile)
	{
		if (!GlobalData.applicationEditorHeader)
			return;
		
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
				int res = getResources().getIdentifier(profile.getIconIdentifier(), "drawable", getActivity().getPackageName());
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
		
		if (GlobalData.applicationEditorPrefIndicator)
		{
			ImageView profilePrefIndicatorImageView = (ImageView)getActivity().findViewById(R.id.activated_profile_pref_indicator);
			profilePrefIndicatorImageView.setImageBitmap(ProfilePreferencesIndicator.paint(profile, getActivity().getBaseContext()));
		}
	}
	
	public void activateProfileWithAlert(int position)
	{
		final int _position = position;
		final Profile profile = profileList.get(_position);

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
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
			Toast msg = Toast.makeText(getActivity().getBaseContext(), 
					getResources().getString(R.string.toast_profile_activated_0) + ": " + profile.getName() + " " +
					getResources().getString(R.string.toast_profile_activated_1), 
					Toast.LENGTH_LONG);
			msg.show();
		}

		activateProfileHelper.showNotification(profile);

	}
	
	private void activateProfile(int position, boolean interactive)
	{
		Profile profile = profileList.get(position);
		activateProfile(profile, interactive);
	}
	
	private void importExportErrorDialog(int importExport)
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
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
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
		dialogBuilder.setTitle(getResources().getString(R.string.import_profiles_alert_title));
		dialogBuilder.setMessage(getResources().getString(R.string.import_profiles_alert_message) + "?");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				if (GlobalData.getDatabaseHandler().importDB()  == 1)
				{
					GlobalData.clearProfileList();

					// toast notification
					Toast msg = Toast.makeText(getActivity().getBaseContext(), 
							getResources().getString(R.string.toast_import_ok), 
							Toast.LENGTH_LONG);
					msg.show();

					// TODO tu by sme mohli len fragment refreshnut
					// refresh activity
					Intent refresh = new Intent(getActivity().getBaseContext(), EditorProfilesActivity.class);
					startActivity(refresh);
					getActivity().finish();
				
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
			Toast msg = Toast.makeText(getActivity().getBaseContext(), 
					getResources().getString(R.string.toast_export_ok), 
					Toast.LENGTH_LONG);
			msg.show();
		
		}
		else
		{
			importExportErrorDialog(2);
		}
		
	}
	
	public static EditorProfileListAdapter getProfileListAdapter()
	{
		return profileListAdapter;
	}

}
