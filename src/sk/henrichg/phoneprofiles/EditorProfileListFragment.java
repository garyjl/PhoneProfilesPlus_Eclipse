package sk.henrichg.phoneprofiles;

import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class EditorProfileListFragment extends SherlockFragment {

	private ActivateProfileHelper activateProfileHelper;
	private List<Profile> profileList;
	private EditorProfileListAdapter profileListAdapter;
	private DragSortListView listView;
	private TextView activeProfileName;
	private ImageView activeProfileIcon;
	private int startupSource = 0;
	private Intent intent;
	private DatabaseHandler databaseHandler;
	
	public static final String FILTER_TYPE_ARGUMENT = "filter_type";

	private int filterType = DatabaseHandler.FILTER_TYPE_PROFILES_ALL;  
	
	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private OnStartProfilePreferences onStartProfilePreferencesCallback = sDummyOnStartProfilePreferencesCallback;
	private OnFinishProfilePreferencesActionMode onFinishProfilePreferencesActionModeCallback = sDummyOnFinishProfilePreferencesActionModeCallback;
	private OnProfileCountChanged onProfileCountChangedCallback = sDummyOnProfileCountChangedCallback; 
	private OnProfileOrderChanged onProfileOrderChangedCallback = sDummyOnProfileOrderChangedCallback; 
	
	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface OnStartProfilePreferences {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onStartProfilePreferences(int position, int filterType, boolean afterDelete);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static OnStartProfilePreferences sDummyOnStartProfilePreferencesCallback = new OnStartProfilePreferences() {
		public void onStartProfilePreferences(int position, int filterType, boolean afterDelete) {
		}
	};
	
	public interface OnFinishProfilePreferencesActionMode {
		public void onFinishProfilePreferencesActionMode();
	}

	private static OnFinishProfilePreferencesActionMode sDummyOnFinishProfilePreferencesActionModeCallback = new OnFinishProfilePreferencesActionMode() {
		public void onFinishProfilePreferencesActionMode() {
		}
	};

	public interface OnProfileCountChanged {
		public void onProfileCountChanged();
	}

	private static OnProfileCountChanged sDummyOnProfileCountChangedCallback = new OnProfileCountChanged() {
		public void onProfileCountChanged() {
		}
	};

	public interface OnProfileOrderChanged {
		public void onProfileOrderChanged();
	}

	private static OnProfileOrderChanged sDummyOnProfileOrderChangedCallback = new OnProfileOrderChanged() {
		public void onProfileOrderChanged() {
		}
	};
	
	public EditorProfileListFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		//Log.e("EditorProfileListFragment.onAttach","xxx");

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof OnStartProfilePreferences)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		onStartProfilePreferencesCallback = (OnStartProfilePreferences) activity;

		if (!(activity instanceof OnProfileCountChanged)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		onProfileCountChangedCallback = (OnProfileCountChanged) activity;
		
		if (!(activity instanceof OnProfileOrderChanged)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		onProfileOrderChangedCallback = (OnProfileOrderChanged) activity;
		
		
		if (activity instanceof OnFinishProfilePreferencesActionMode)
			onFinishProfilePreferencesActionModeCallback = (OnFinishProfilePreferencesActionMode) activity;

	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		onStartProfilePreferencesCallback = sDummyOnStartProfilePreferencesCallback;
		onFinishProfilePreferencesActionModeCallback = sDummyOnFinishProfilePreferencesActionModeCallback;
		onProfileCountChangedCallback = sDummyOnProfileCountChangedCallback;
		onProfileOrderChangedCallback = sDummyOnProfileOrderChangedCallback;
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
        filterType = getArguments() != null ? 
        		getArguments().getInt(FILTER_TYPE_ARGUMENT, DatabaseHandler.FILTER_TYPE_PROFILES_ALL) : 
        		DatabaseHandler.FILTER_TYPE_PROFILES_ALL;

		//Log.e("EditorProfileListFragment.onCreate","xxx");
		
		databaseHandler = EditorProfilesActivity.profilesDataWrapper.getDatabaseHandler(); 
		
		intent = getSherlockActivity().getIntent();
		startupSource = intent.getIntExtra(GlobalData.EXTRA_START_APP_SOURCE, 0);
		
		activateProfileHelper = EditorProfilesActivity.profilesDataWrapper.getActivateProfileHelper();
		activateProfileHelper.initialize(getSherlockActivity(), getActivity().getBaseContext());
		
		final EditorProfileListFragment fragment = this;
		
		new AsyncTask<Void, Integer, Void>() {
			
			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				//updateHeader(null);
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				profileList = EditorProfilesActivity.profilesDataWrapper.getProfileList(filterType);
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result)
			{
				super.onPostExecute(result);

				if (listView != null)
				{
					profileListAdapter = new EditorProfileListAdapter(fragment, EditorProfilesActivity.profilesDataWrapper);
					listView.setAdapter(profileListAdapter);
				}

				doOnStart();
				
			}
			
		}.execute();
		
		setHasOptionsMenu(true);

		//Log.d("EditorProfileListFragment.onCreate", "xxxx");
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView;
		
		if (GlobalData.applicationEditorPrefIndicator && GlobalData.applicationEditorHeader)
			rootView = inflater.inflate(R.layout.editor_profile_list, container, false); 
		else
		if (GlobalData.applicationEditorHeader)
			rootView = inflater.inflate(R.layout.editor_profile_list_no_indicator, container, false); 
		else
			rootView = inflater.inflate(R.layout.editor_profile_list_no_header, container, false); 

		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		// az tu mame layout, tak mozeme ziskat view-y
		activeProfileName = (TextView)getSherlockActivity().findViewById(R.id.activated_profile_name);
		activeProfileIcon = (ImageView)getSherlockActivity().findViewById(R.id.activated_profile_icon);
		listView = (DragSortListView)getSherlockActivity().findViewById(R.id.editor_profiles_list);
		listView.setEmptyView(getSherlockActivity().findViewById(R.id.editor_profiles_list_empty));

		listView.setAdapter(profileListAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				//Log.d("EditorProfileListFragment.onItemClick", "xxxx");

				startProfilePreferencesActivity(position);
				
			}
			
		}); 
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				//Log.d("EditorProfileListFragment.onItemLongClick", "xxxx");
				
				if (!EditorProfileListAdapter.editIconClicked) // workaround
				{
					activateProfileWithAlert(position);
				}
				
				EditorProfileListAdapter.editIconClicked = false;
				
				return false;
			}
			
		});
		
        listView.setDropListener(new DragSortListView.DropListener() {
            public void drop(int from, int to) {
            	profileListAdapter.changeItemOrder(from, to);
            	databaseHandler.setPOrder(profileList);
        		onProfileOrderChangedCallback.onProfileOrderChanged();
        		//Log.d("EditorProfileListFragment.drop", "xxxx");
            }
        });
        
		if (profileList != null)
		{
			if (profileListAdapter == null)
			{
				profileListAdapter = new EditorProfileListAdapter(this, EditorProfilesActivity.profilesDataWrapper);
				listView.setAdapter(profileListAdapter);
			}
		}

		//Log.d("EditorProfileListFragment.onActivityCreated", "xxx");
        
	}
	
	private void doOnStart()
	{
	
		//Log.e("EditorProfileListFragment.doOnStart","xxx");
		
		// ak sa ma refreshnut aktivita, nebudeme robit nic, co je v onStart
		if (PhoneProfilesPreferencesActivity.getInvalidateEditor(false))
			return;
		
		if (profileListAdapter != null)
		{
			Profile profile;
			
			// pre profil, ktory je prave aktivny, treba aktualizovat aktivitu
			profile = EditorProfilesActivity.profilesDataWrapper.getActivatedProfile();
			updateHeader(profile);
			
			if (startupSource == 0)
			{
				// aktivita nebola spustena z notifikacie, ani z widgetu
				// pre profil, ktory je prave aktivny, treba aktualizovat notifikaciu a widgety 
				activateProfileHelper.showNotification(profile);
				activateProfileHelper.updateWidget();
			}
			
			profileListAdapter.notifyDataSetChanged();
		}

		// reset, aby sa to dalej chovalo ako normalne spustenie z lauchera
		startupSource = 0;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();

		doOnStart();
		
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startProfilePreferencesActivity(int position)
	{

		Profile profile;
		
		if (position != -1)
			// editacia profilu
			profile = profileList.get(position);
		else
		{
			// pridanie noveho profilu
			profile = new Profile(getResources().getString(R.string.profile_name_default), 
								  GUIData.PROFILE_ICON_DEFAULT + "|1", 
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
								  "-",
								  true
					);
			profileListAdapter.addItem(profile); // pridame profil do listview a nastavime jeho order
			databaseHandler.addProfile(profile);
			onProfileCountChangedCallback.onProfileCountChanged();

        	// generate bitmaps
			profile.generateIconBitmap(getSherlockActivity().getBaseContext(), false, 0);
			profile.generatePreferencesIndicator(getSherlockActivity().getBaseContext(), false, 0);
			
		}

		//Log.d("EditorProfileListFragment.startProfilePreferencesActivity", profile.getID()+"");
		
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) one must start profile preferences
		onStartProfilePreferencesCallback.onStartProfilePreferences(profileListAdapter.getItemId(profile), filterType, false);
	}

	public void duplicateProfile(int position)
	{
		Profile origProfile = profileList.get(position);

		Profile newProfile = new Profile(
				   origProfile._name+"_d", 
				   origProfile._icon, 
				   false, 
				   origProfile._porder,
				   origProfile._volumeRingerMode,
				   origProfile._volumeRingtone,
				   origProfile._volumeNotification,
				   origProfile._volumeMedia,
				   origProfile._volumeAlarm,
				   origProfile._volumeSystem,
				   origProfile._volumeVoice,
				   origProfile._soundRingtoneChange,
				   origProfile._soundRingtone,
				   origProfile._soundNotificationChange,
				   origProfile._soundNotification,
				   origProfile._soundAlarmChange,
				   origProfile._soundAlarm,
				   origProfile._deviceAirplaneMode,
				   origProfile._deviceWiFi,
				   origProfile._deviceBluetooth,
				   origProfile._deviceScreenTimeout,
				   origProfile._deviceBrightness,
				   origProfile._deviceWallpaperChange,
				   origProfile._deviceWallpaper,
				   origProfile._deviceMobileData,
				   origProfile._deviceMobileDataPrefs,
				   origProfile._deviceGPS,
				   origProfile._deviceRunApplicationChange,
				   origProfile._deviceRunApplicationPackageName,
				   origProfile._showInActivator);

		profileListAdapter.addItem(newProfile);
		databaseHandler.addProfile(newProfile);
		onProfileCountChangedCallback.onProfileCountChanged();
		
    	// generate bitmaps
		newProfile.generateIconBitmap(getSherlockActivity().getBaseContext(), false, 0);
		newProfile.generatePreferencesIndicator(getSherlockActivity().getBaseContext(), false, 0);
		
		
		//updateListView();

		startProfilePreferencesActivity(profileList.size()-1);
		
		
	}

	public void deleteProfile(int position)
	{
		final Profile profile = profileList.get(position);

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getSherlockActivity());
		dialogBuilder.setTitle(getResources().getString(R.string.profile_string_0) + ": " + profile._name);
		dialogBuilder.setMessage(getResources().getString(R.string.delete_profile_alert_message) + "?");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		
		final Activity activity = getActivity();
		
		dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				class DeleteAsyncTask extends AsyncTask<Void, Integer, Integer> 
				{
					private ProgressDialog dialog;
					
					DeleteAsyncTask()
					{
				         this.dialog = new ProgressDialog(activity);
					}
					
					@Override
					protected void onPreExecute()
					{
						super.onPreExecute();
						
					     this.dialog.setMessage(getResources().getString(R.string.delete_profile_progress_title) + "...");
					     this.dialog.show();						
					}
					
					@Override
					protected Integer doInBackground(Void... params) {
						
						profileListAdapter.deleteItemNoNotify(profile);
						databaseHandler.deleteProfile(profile);
						
						return 1;
					}
					
					@Override
					protected void onPostExecute(Integer result)
					{
						super.onPostExecute(result);
						
					    if (dialog.isShowing())
				            dialog.dismiss();
						
						if (result == 1)
						{
							profileListAdapter.notifyDataSetChanged();
							onProfileCountChangedCallback.onProfileCountChanged();
							//updateListView();
							// v pripade, ze sa odmaze aktivovany profil, nastavime, ze nic nie je aktivovane
							//Profile profile = databaseHandler.getActivatedProfile();
							Profile profile = profileListAdapter.getActivatedProfile();
							updateHeader(profile);
							activateProfileHelper.showNotification(profile);
							activateProfileHelper.updateWidget();
							
							profile = EditorProfilesActivity.profilesDataWrapper.getFirstProfile(filterType);
							onStartProfilePreferencesCallback.onStartProfilePreferences(profileListAdapter.getItemId(profile), filterType, true);
						}
					}
					
				}
				
				new DeleteAsyncTask().execute();
			}
		});
		dialogBuilder.setNegativeButton(android.R.string.no, null);
		dialogBuilder.show();
	}

	private void deleteAllProfiles()
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getSherlockActivity());
		dialogBuilder.setTitle(getResources().getString(R.string.alert_title_delete_all_profiles));
		dialogBuilder.setMessage(getResources().getString(R.string.alert_message_delete_all_profiles) + "?");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		
		final Activity activity = getActivity();
		
		dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				class DeleteAsyncTask extends AsyncTask<Void, Integer, Integer> 
				{
					private ProgressDialog dialog;
					
					DeleteAsyncTask()
					{
				         this.dialog = new ProgressDialog(activity);
					}
					
					@Override
					protected void onPreExecute()
					{
						super.onPreExecute();
						
					     this.dialog.setMessage(getResources().getString(R.string.delete_profiles_progress_title) + "...");
					     this.dialog.show();						
					}
					
					@Override
					protected Integer doInBackground(Void... params) {
						
						databaseHandler.deleteAllProfiles();
						profileListAdapter.clearNoNotify();
						
						return 1;
					}
					
					@Override
					protected void onPostExecute(Integer result)
					{
						super.onPostExecute(result);
						
					    if (dialog.isShowing())
				            dialog.dismiss();
						
						if (result == 1)
						{
							profileListAdapter.notifyDataSetChanged();
							onProfileCountChangedCallback.onProfileCountChanged();
							//updateListView();
							// v pripade, ze sa odmaze aktivovany profil, nastavime, ze nic nie je aktivovane
							//Profile profile = databaseHandler.getActivatedProfile();
							//Profile profile = profileListAdapter.getActivatedProfile();
							updateHeader(null);
							activateProfileHelper.showNotification(null);
							activateProfileHelper.updateWidget();
							
							Profile profile = EditorProfilesActivity.profilesDataWrapper.getFirstProfile(filterType);
							onStartProfilePreferencesCallback.onStartProfilePreferences(profileListAdapter.getItemId(profile), filterType, true);
						}
					}
					
				}
				
				new DeleteAsyncTask().execute();
				
			}
		});
		dialogBuilder.setNegativeButton(android.R.string.no, null);
		dialogBuilder.show();
	}
	
	public void updateHeader(Profile profile)
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
			activeProfileName.setText(profile._name);
	        if (profile.getIsIconResourceID())
	        {
				int res = getResources().getIdentifier(profile.getIconIdentifier(), "drawable", getSherlockActivity().getPackageName());
				activeProfileIcon.setImageResource(res); // resource na ikonu
	        }
	        else
	        {
        		//Resources resources = getResources();
        		//int height = (int) resources.getDimension(android.R.dimen.app_icon_size);
        		//int width = (int) resources.getDimension(android.R.dimen.app_icon_size);
        		//Bitmap bitmap = BitmapResampler.resample(profile.getIconIdentifier(), width, height);
	        	//activeProfileIcon.setImageBitmap(bitmap);
	        	activeProfileIcon.setImageBitmap(profile._iconBitmap);
	        }
		}
		
		if (GlobalData.applicationEditorPrefIndicator)
		{
			//Log.e("EditorProfileListFragment.updateHeader","indicator");

			ImageView profilePrefIndicatorImageView = (ImageView)getSherlockActivity().findViewById(R.id.activated_profile_pref_indicator);
			//profilePrefIndicatorImageView.setImageBitmap(ProfilePreferencesIndicator.paint(profile, getSherlockActivity().getBaseContext()));
			if (profile == null)
				profilePrefIndicatorImageView.setImageResource(R.drawable.ic_empty);
			else
				profilePrefIndicatorImageView.setImageBitmap(profile._preferencesIndicator);
		}
	}
	
	public void activateProfileWithAlert(int position)
	{
		final int _position = position;
		final Profile profile = profileList.get(_position);

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getSherlockActivity());
		dialogBuilder.setTitle(getResources().getString(R.string.profile_string_0) + ": " + profile._name);
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
	/*	profileListAdapter.activateProfile(profile);
		databaseHandler.activateProfile(profile);
		
		activateProfileHelper.execute(profile, interactive);

		updateHeader(profile);
		activateProfileHelper.updateWidget();

		if (GlobalData.notificationsToast)
		{	
			// toast notification
			Toast msg = Toast.makeText(getSherlockActivity().getBaseContext(), 
					getResources().getString(R.string.toast_profile_activated_0) + ": " + profile._name + " " +
					getResources().getString(R.string.toast_profile_activated_1), 
					Toast.LENGTH_LONG);
			msg.show();
		}

		activateProfileHelper.showNotification(profile); */
		
		profileListAdapter.activateProfile(profile);
		updateHeader(profile);

		EditorProfilesActivity.serviceCommunication.sendMessageIntoServiceLong(PhoneProfilesService.MSG_ACTIVATE_PROFILE, 
				                            profile._id);
		

	}
	
	private void activateProfile(int position, boolean interactive)
	{
		Profile profile = profileList.get(position);
		activateProfile(profile, interactive);
	}
	
	public void finishProfilePreferencesActionMode()
	{
		onFinishProfilePreferencesActionModeCallback.onFinishProfilePreferencesActionMode();
	}
	
	public void updateListView()
	{
		profileListAdapter.notifyDataSetChanged();
	}
	
	public int getFilterType()
	{
		return filterType;
	}


}
