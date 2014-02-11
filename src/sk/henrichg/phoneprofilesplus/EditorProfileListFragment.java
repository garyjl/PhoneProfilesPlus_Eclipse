package sk.henrichg.phoneprofilesplus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.mobeta.android.dslv.DragSortListView;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EditorProfileListFragment extends Fragment {

	public DataWrapper dataWrapper;
	private ActivateProfileHelper activateProfileHelper;
	private List<Profile> profileList;
	private EditorProfileListAdapter profileListAdapter;
	private DragSortListView listView;
	private TextView activeProfileName;
	private ImageView activeProfileIcon;
	private DatabaseHandler databaseHandler;
	
	public static final int EDIT_MODE_UNDEFINED = 0;
	public static final int EDIT_MODE_INSERT = 1;
	public static final int EDIT_MODE_DUPLICATE = 2;
	public static final int EDIT_MODE_EDIT = 3;
	public static final int EDIT_MODE_DELETE = 4;
	
	public static final String FILTER_TYPE_ARGUMENT = "filter_type";
	
	public static final int FILTER_TYPE_ALL = 0;
	public static final int FILTER_TYPE_SHOW_IN_ACTIVATOR = 1;
	public static final int FILTER_TYPE_NO_SHOW_IN_ACTIVATOR = 2;

	private int filterType = FILTER_TYPE_ALL;  
	
	/**
	 * The fragment's current callback objects
	 */
	private OnStartProfilePreferences onStartProfilePreferencesCallback = sDummyOnStartProfilePreferencesCallback;
	private OnFinishProfilePreferencesActionMode onFinishProfilePreferencesActionModeCallback = sDummyOnFinishProfilePreferencesActionModeCallback;
	
	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified.
	 */
	// invoked when start profile preference fragment/activity needed 
	public interface OnStartProfilePreferences {
		public void onStartProfilePreferences(Profile profile, int editMode, int filterType);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static OnStartProfilePreferences sDummyOnStartProfilePreferencesCallback = new OnStartProfilePreferences() {
		public void onStartProfilePreferences(Profile profile, int editMode, int filterType) {
		}
	};
	
	// invoked when action mode finish needed (from profile list adapter) 
	public interface OnFinishProfilePreferencesActionMode {
		public void onFinishProfilePreferencesActionMode();
	}

	private static OnFinishProfilePreferencesActionMode sDummyOnFinishProfilePreferencesActionModeCallback = new OnFinishProfilePreferencesActionMode() {
		public void onFinishProfilePreferencesActionMode() {
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
		
		if (activity instanceof OnFinishProfilePreferencesActionMode)
			onFinishProfilePreferencesActionModeCallback = (OnFinishProfilePreferencesActionMode) activity;

	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		onStartProfilePreferencesCallback = sDummyOnStartProfilePreferencesCallback;
		onFinishProfilePreferencesActionModeCallback = sDummyOnFinishProfilePreferencesActionModeCallback;
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
        filterType = getArguments() != null ? 
        		getArguments().getInt(FILTER_TYPE_ARGUMENT, EditorProfileListFragment.FILTER_TYPE_ALL) : 
        			EditorProfileListFragment.FILTER_TYPE_ALL;

		//Log.e("EditorProfileListFragment.onCreate","xxx");
	
   		dataWrapper = new DataWrapper(getActivity().getBaseContext(), true, false, 0);
        		
    	databaseHandler = dataWrapper.getDatabaseHandler(); 
	
    	activateProfileHelper = dataWrapper.getActivateProfileHelper();
    	activateProfileHelper.initialize(getActivity(), getActivity().getBaseContext());
		
		setHasOptionsMenu(true);

		Log.e("EditorProfileListFragment.onCreate", "xxxx");
		
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

		Log.e("EditorProfileListFragment.onCreateView", "xxxx");
		
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		doOnViewCreated(view, savedInstanceState);

		Log.e("EditorProfileListFragment.onViewCreated", "xxxx");

		super.onViewCreated(view, savedInstanceState);
	}

	private static class LoadProfilesTask extends AsyncTask<Void, Integer, Void> 
	{
		EditorProfileListFragment fragment;
		boolean defaultProfilesGenerated = false;
		
		private WeakReference<List<Profile>> profileListReference;
		private List<Profile> lProfileList;
		
		
		LoadProfilesTask(EditorProfileListFragment fragment)
		{
			this.fragment = fragment;
		}
		
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			//Log.e("EditorProfileListFragment.doOnViewCreated.onPreExecute",fragment.dataWrapper+"");

			fragment.profileList = new ArrayList<Profile>();
			profileListReference = new WeakReference<List<Profile>>(fragment.profileList);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			//Log.e("EditorProfileListFragment.doOnViewCreated.doInBackground",fragment.dataWrapper+"");

			lProfileList = fragment.dataWrapper.getProfileList();
			if (lProfileList.size() == 0)
			{
				// no profiles in DB, generate default profiles
				lProfileList = fragment.dataWrapper.getDefaultProfileList();
				defaultProfilesGenerated = true;
			}
			// sort list
			if (fragment.filterType != EditorProfileListFragment.FILTER_TYPE_SHOW_IN_ACTIVATOR)
				fragment.sortAlphabetically(lProfileList);
			else
				fragment.sortByPOrder(lProfileList);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			
			//Log.e("EditorProfileListFragment.doOnViewCreated.onPostExecute",fragment.dataWrapper+"");

			final List<Profile> profileList = profileListReference.get();
			
		    if (profileListReference != null && lProfileList != null) {
		    	profileList.clear();
		    	for (Profile origProfile : lProfileList)
		    	{
					//Profile newProfile = new Profile();
		    		//newProfile.copyProfile(origProfile);
					//profileList.add(newProfile);
		    		profileList.add(origProfile);
		    	}
		    	lProfileList.clear();
		    	fragment.dataWrapper.setProfileList(profileList, false);
		    }				
			
			fragment.profileListAdapter = new EditorProfileListAdapter(fragment, fragment.dataWrapper, fragment.filterType);
			fragment.listView.setAdapter(fragment.profileListAdapter);

			if (defaultProfilesGenerated)
			{
				fragment.activateProfileHelper.updateWidget();
				Toast msg = Toast.makeText(fragment.getActivity(), 
						fragment.getResources().getString(R.string.toast_default_profiles_generated), 
						Toast.LENGTH_SHORT);
				msg.show();
			}
		}
	}
	
	//@Override
	//public void onActivityCreated(Bundle savedInstanceState)
	public void doOnViewCreated(View view, Bundle savedInstanceState)
	{
		//super.onActivityCreated(savedInstanceState);
		
		// az tu mame layout, tak mozeme ziskat view-y
	/*	activeProfileName = (TextView)getActivity().findViewById(R.id.activated_profile_name);
		activeProfileIcon = (ImageView)getActivity().findViewById(R.id.activated_profile_icon);
		listView = (DragSortListView)getActivity().findViewById(R.id.editor_profiles_list);
		listView.setEmptyView(getActivity().findViewById(R.id.editor_profiles_list_empty));
	*/
		activeProfileName = (TextView)view.findViewById(R.id.activated_profile_name);
		activeProfileIcon = (ImageView)view.findViewById(R.id.activated_profile_icon);
		listView = (DragSortListView)view.findViewById(R.id.editor_profiles_list);
		listView.setEmptyView(view.findViewById(R.id.editor_profiles_list_empty));

		if (profileList == null)
		{
			Log.e("EditorProfileListFragment.doOnViewCreated", "getProfileList");
			LoadProfilesTask task = new LoadProfilesTask(this);
			task.execute();
		}
		else
			listView.setAdapter(profileListAdapter);
			
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				//Log.d("EditorProfileListFragment.onItemClick", "xxxx");

				startProfilePreferencesActivity((Profile)profileListAdapter.getItem(position));
				
			}
			
		}); 
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				//Log.d("EditorProfileListFragment.onItemLongClick", "xxxx");
				
				activateProfile((Profile)profileListAdapter.getItem(position), true);
				
				return true;
			}
			
		});
		
        listView.setDropListener(new DragSortListView.DropListener() {
            public void drop(int from, int to) {
            	profileListAdapter.changeItemOrder(from, to); // swap profiles
            	databaseHandler.setPOrder(profileList);  // set profiles _porder and write it into db
				activateProfileHelper.updateWidget();
        		//Log.d("EditorProfileListFragment.drop", "xxxx");
            }
        });
        
		Profile profile;
		
		// pre profil, ktory je prave aktivny, treba aktualizovat aktivitu
		profile = dataWrapper.getActivatedProfile();
		updateHeader(profile);
		
		//Log.e("EditorProfileListFragment.doOnViewCreated", "xxx");
        
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		// this is really important in order to save the state across screen
		// configuration changes for example
		setRetainInstance(true);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();

		//Log.d("EditorProfileListFragment.onStart", "xxxx");
		
	}
	
	@Override
	public void onDestroy()
	{
		if (listView != null)
			listView.setAdapter(null);
		if (profileListAdapter != null)
			profileListAdapter.release();
		
		activateProfileHelper = null;
		profileList = null;
		databaseHandler = null;
		
		if (dataWrapper != null)
			dataWrapper.invalidateDataWrapper();
		dataWrapper = null;
		
		super.onDestroy();
		
		Log.e("EditorProfileListFragment.onDestroy","xxx");
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

			startProfilePreferencesActivity(null);
			
			return true;
		case R.id.menu_delete_all_profiles:
			//Log.d("EditorProfileListFragment.onOptionsItemSelected", "menu_delete_all_profiles");
			
			deleteAllProfiles();
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startProfilePreferencesActivity(Profile profile)
	{
		Profile _profile = profile;
		int editMode;
		
		if (_profile != null)
		{
			// editacia profilu
			int profilePos = profileListAdapter.getItemPosition(_profile);
			listView.setSelection(profilePos);
			listView.setItemChecked(profilePos, true);
			editMode = EDIT_MODE_EDIT;
		}
		else
		{
			// pridanie noveho profilu
			editMode = EDIT_MODE_INSERT;
		}

		//Log.d("EditorProfileListFragment.startProfilePreferencesActivity", profile.getID()+"");
		
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) one must start profile preferences
		onStartProfilePreferencesCallback.onStartProfilePreferences(_profile, editMode, filterType);
	}

	public void duplicateProfile(Profile origProfile)
	{
		/*
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
				   origProfile._deviceAutosync,
				   origProfile._showInActivator);

		// add profile into db and set id and order
		databaseHandler.addProfile(newProfile); 
		// add profile into listview
		profileListAdapter.addItem(newProfile, false);
		
		updateListView(newProfile, false);
		
		activateProfileHelper.updateWidget();
		
    	// generate bitmaps
		newProfile.generateIconBitmap(getActivity().getBaseContext(), false, 0);
		newProfile.generatePreferencesIndicator(getActivity().getBaseContext(), false, 0);
		
		startProfilePreferencesActivity(newProfile);
		*/
		
		int editMode;

		// zduplikovanie profilu
		editMode = EDIT_MODE_DUPLICATE;

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) one must start profile preferences
		onStartProfilePreferencesCallback.onStartProfilePreferences(origProfile, editMode, filterType);
		
	}
	
	public void deleteProfile(Profile profile)
	{
		final Profile _profile = profile;
		final Activity activity = getActivity();

		if (dataWrapper.getProfileById(profile._id) == null)
			// profile not exists
			return;
		
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
				
				try {
			        this.dialog.setMessage(getResources().getString(R.string.delete_profile_progress_title) + "...");
			        this.dialog.show();						
				} catch (Exception e) {
				}
			}
			
			@Override
			protected Integer doInBackground(Void... params) {
				
				dataWrapper.stopEventsForProfile(_profile, true);
				profileListAdapter.deleteItemNoNotify(_profile);
				databaseHandler.unlinkEventsFromProfile(_profile);
				databaseHandler.deleteProfile(_profile);
				
				return 1;
			}
			
			@Override
			protected void onPostExecute(Integer result)
			{
				super.onPostExecute(result);
				
				try {
				    if (dialog.isShowing())
			            dialog.dismiss();
			 	} catch (Exception e) {
			 	}
				
				if (result == 1)
				{
					if (!profileListAdapter.released)
					{
						profileListAdapter.notifyDataSetChanged();
						// v pripade, ze sa odmaze aktivovany profil, nastavime, ze nic nie je aktivovane
						//Profile profile = databaseHandler.getActivatedProfile();
						Profile profile = profileListAdapter.getActivatedProfile();
						updateHeader(profile);
						activateProfileHelper.showNotification(profile);
						activateProfileHelper.updateWidget();
						
						onStartProfilePreferencesCallback.onStartProfilePreferences(null, EDIT_MODE_DELETE, filterType);
					}
				}
			}
			
		}
		
		new DeleteAsyncTask().execute();
	}

	public void showEditMenu(View view)
	{
		Context context = ((ActionBarActivity)getActivity()).getSupportActionBar().getThemedContext();
		PopupMenu popup = new PopupMenu(context, view);
		getActivity().getMenuInflater().inflate(R.menu.profile_list_item_edit, popup.getMenu());
		
		final Profile profile = (Profile)view.getTag();
		
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

			public boolean onMenuItemClick(android.view.MenuItem item) {
				switch (item.getItemId()) {
				case R.id.profile_list_item_menu_activate:
					activateProfile(profile, true);
					return true;
				case R.id.profile_list_item_menu_duplicate:
					duplicateProfile(profile);
					return true;
				case R.id.profile_list_item_menu_delete:
					deleteProfileWithAlert(profile);
					return true;
				default:
					return false;
				}
			}
			});		
		
		
		popup.show();		
	}
	
	public void deleteProfileWithAlert(Profile profile)
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
		dialogBuilder.setTitle(getResources().getString(R.string.profile_string_0) + ": " + profile._name);
		dialogBuilder.setMessage(getResources().getString(R.string.delete_profile_alert_message) + "?");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		
		final Profile _profile = profile;
		
		dialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				deleteProfile(_profile);
			}
		});
		dialogBuilder.setNegativeButton(R.string.alert_button_no, null);
		dialogBuilder.show();
	}

	private void deleteAllProfiles()
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
		dialogBuilder.setTitle(getResources().getString(R.string.alert_title_delete_all_profiles));
		dialogBuilder.setMessage(getResources().getString(R.string.alert_message_delete_all_profiles) + "?");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		
		final Activity activity = getActivity();
		
		dialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
			
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
						
						dataWrapper.stopAllEvents(true);
						profileListAdapter.clearNoNotify();
						databaseHandler.deleteAllProfiles();
						databaseHandler.unlinkAllEvents();
						
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
							// v pripade, ze sa odmaze aktivovany profil, nastavime, ze nic nie je aktivovane
							//Profile profile = databaseHandler.getActivatedProfile();
							//Profile profile = profileListAdapter.getActivatedProfile();
							updateHeader(null);
							activateProfileHelper.removeNotification();
							activateProfileHelper.updateWidget();
							
							onStartProfilePreferencesCallback.onStartProfilePreferences(null, EDIT_MODE_DELETE, filterType);
						}
					}
					
				}
				
				new DeleteAsyncTask().execute();
				
			}
		});
		dialogBuilder.setNegativeButton(R.string.alert_button_no, null);
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
				int res = getResources().getIdentifier(profile.getIconIdentifier(), "drawable", getActivity().getPackageName());
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
			ImageView profilePrefIndicatorImageView = (ImageView)getActivity().findViewById(R.id.activated_profile_pref_indicator);
			if (profilePrefIndicatorImageView != null)
			{
				//profilePrefIndicatorImageView.setImageBitmap(ProfilePreferencesIndicator.paint(profile, getActivity().getBaseContext()));
				if (profile == null)
					profilePrefIndicatorImageView.setImageResource(R.drawable.ic_empty);
				else
					profilePrefIndicatorImageView.setImageBitmap(profile._preferencesIndicator);
			}
		}
	}

	public void doOnActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == GlobalData.REQUEST_CODE_ACTIVATE_PROFILE)
		{
			if(resultCode == Activity.RESULT_OK)
			{      
		    	long profile_id = data.getLongExtra(GlobalData.EXTRA_PROFILE_ID, -1);
		    	Profile profile = dataWrapper.getProfileById(profile_id);
		    	
		    	if (profileListAdapter != null)
		    		profileListAdapter.activateProfile(profile);
				updateHeader(profile);
		     }
		     if (resultCode == Activity.RESULT_CANCELED)
		     {    
		         //Write your code if there's no result
		     }
		}
	}
	
	public void activateProfile(Profile profile, boolean interactive)
	{
		Intent intent = new Intent(getActivity().getBaseContext(), BackgroundActivateProfileActivity.class);
		intent.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_EDITOR);
		intent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile._id);
		getActivity().startActivityForResult(intent, GlobalData.REQUEST_CODE_ACTIVATE_PROFILE);
	}

	// called from profile list adapter
	public void finishProfilePreferencesActionMode()
	{
		onFinishProfilePreferencesActionModeCallback.onFinishProfilePreferencesActionMode();
	}
	
	public void updateListView(Profile profile, boolean newProfile)
	{
		if (profileListAdapter != null)
		{
			if ((newProfile) && (profile != null))
				// add profile into listview
				profileListAdapter.addItem(profile, false);
		}
		
		if (profileList != null)
		{
			// sort list
			if (filterType != EditorProfileListFragment.FILTER_TYPE_SHOW_IN_ACTIVATOR)
				sortAlphabetically(profileList);
			else
				sortByPOrder(profileList);
		}

		if (profileListAdapter != null)
		{
			int profilePos;
			
			if (profile != null)
				profilePos = profileListAdapter.getItemPosition(profile);
			else
				profilePos = listView.getCheckedItemPosition();
			
			profileListAdapter.notifyDataSetChanged();

			if (profilePos != ListView.INVALID_POSITION)
			{
				// set event visible in list
				listView.setSelection(profilePos);
				listView.setItemChecked(profilePos, true);
			}
		}
	}
	
	public int getFilterType()
	{
		return filterType;
	}

	private class AlphabeticallyComparator implements Comparator<Profile> {

		public int compare(Profile lhs, Profile rhs) {

		    int res = GUIData.collator.compare(lhs._name, rhs._name);
	        return res;
	    }
	}
	
	public void sortAlphabetically(List<Profile> profileList)
	{
	    Collections.sort(profileList, new AlphabeticallyComparator());
	}

	private class ByPOrderComparator implements Comparator<Profile> {

		public int compare(Profile lhs, Profile rhs) {

		    int res =  lhs._porder - rhs._porder;
	        return res;
	    }
	}
	
	public void sortByPOrder(List<Profile> profileList)
	{
	    Collections.sort(profileList, new ByPOrderComparator());
	}

	public void refreshGUI()
	{
		if (profileListAdapter == null)
			return;
		
		Profile profileFromAdapter = profileListAdapter.getActivatedProfile();
		if (profileFromAdapter != null)
			profileFromAdapter._checked = false;

		Profile profileFromDB = dataWrapper.getDatabaseHandler().getActivatedProfile();
		if (profileFromDB != null)
		{
			Profile profileFromDataWrapper = dataWrapper.getProfileById(profileFromDB._id);
			if (profileFromDataWrapper != null)
				profileFromDataWrapper._checked = true;
			updateHeader(profileFromDataWrapper);
			updateListView(profileFromDataWrapper, false);
		}
		else
		{
			updateHeader(null);
			updateListView(null, false);
		}
			
	}
	
}
