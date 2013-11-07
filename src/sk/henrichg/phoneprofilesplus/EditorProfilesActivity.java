package sk.henrichg.phoneprofilesplus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Map.Entry;

import sk.henrichg.phoneprofilesplus.EditorEventListFragment.OnFinishEventPreferencesActionMode;
import sk.henrichg.phoneprofilesplus.EditorEventListFragment.OnStartEventPreferences;
import sk.henrichg.phoneprofilesplus.EditorProfileListFragment.OnFinishProfilePreferencesActionMode;
import sk.henrichg.phoneprofilesplus.EditorProfileListFragment.OnStartProfilePreferences;
import sk.henrichg.phoneprofilesplus.EventPreferencesFragment.OnRedrawEventListFragment;
import sk.henrichg.phoneprofilesplus.EventPreferencesFragment.OnRestartEventPreferences;
import sk.henrichg.phoneprofilesplus.PreferenceListFragment.OnPreferenceAttachedListener;
import sk.henrichg.phoneprofilesplus.ProfilePreferencesFragment.OnRedrawProfileListFragment;
import sk.henrichg.phoneprofilesplus.ProfilePreferencesFragment.OnRestartProfilePreferences;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceScreen;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class EditorProfilesActivity extends SherlockFragmentActivity
                                    implements OnStartProfilePreferences,
                                               OnPreferenceAttachedListener,
                                               OnRestartProfilePreferences,
                                               OnRedrawProfileListFragment,
                                               OnFinishProfilePreferencesActionMode,
                                               OnStartEventPreferences,
                                               OnRestartEventPreferences,
                                               OnRedrawEventListFragment,
                                               OnFinishEventPreferencesActionMode
{

	public static DataWrapper dataWrapper;
	private static ApplicationsCache applicationsCache;
	private int editModeProfile;
	private int editModeEvent;

	private static final String SP_RESET_PREFERENCES_FRAGMENT = "editor_restet_preferences_fragment";
	private static final String SP_RESET_PREFERENCES_FRAGMENT_DATA_ID = "editor_restet_preferences_fragment_data_id";
	private static final String SP_RESET_PREFERENCES_FRAGMENT_EDIT_MODE = "editor_restet_preferences_fragment_edit_mode";
	private static final int RESET_PREFERENCE_FRAGMENT_RESET_PROFILE = 1;
	private static final int RESET_PREFERENCE_FRAGMENT_RESET_EVENT = 2;
	private static final int RESET_PREFERENCE_FRAGMENT_REMOVE = 3;
	
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	public static boolean mTwoPane;
	
	DrawerLayout drawerLayout;
	RelativeLayout drawerRoot;
	ListView drawerListView;
	ActionBarDrawerToggle drawerToggle;
	TextView filterStatusbarTitle;
	TextView orderLabel;
	Spinner orderSpinner;
	
	String[] drawerItemsTitle;
	String[] drawerItemsSubtitle;
	Integer[] drawerItemsIcon;
	EditorDrawerListAdapter drawerAdapter;
	
	private int drawerSelectedItem = 1;
	private int orderSelectedItem = 0;
	private int profilesFilterType = EditorProfileListFragment.FILTER_TYPE_SHOW_IN_ACTIVATOR;
	private int eventsFilterType = EditorEventListFragment.FILTER_TYPE_ALL;
	private int eventsOrderType = EditorEventListFragment.ORDER_TYPE_EVENT_NAME;
	
	private static final int COUNT_DRAWER_PROFILE_ITEMS = 3; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		GUIData.setTheme(this, false);
		GUIData.setLanguage(getBaseContext());

		dataWrapper = new DataWrapper(getBaseContext(), true, false, 0);
		dataWrapper.getActivateProfileHelper().initialize(this, getBaseContext());
		applicationsCache = new ApplicationsCache();
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_editor_list_onepane);
		
	/*	// add profile list into list container
		EditorProfileListFragment fragment = new EditorProfileListFragment();
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.editor_list_container, fragment).commit(); */
		
		
		if (findViewById(R.id.editor_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			if (savedInstanceState == null)
				onStartProfilePreferences(null, EditorProfileListFragment.EDIT_MODE_EDIT, profilesFilterType);
			else
			{
				// reset preferences fragment
		    	SharedPreferences preferences = getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Activity.MODE_PRIVATE);
		    	int resetMode = preferences.getInt(SP_RESET_PREFERENCES_FRAGMENT, 0);
		    	if (resetMode == RESET_PREFERENCE_FRAGMENT_RESET_PROFILE)
		    	{
					// restart profile preferences fragmentu
		    		long profile_id = preferences.getLong(SP_RESET_PREFERENCES_FRAGMENT_DATA_ID, 0);
		    		int editMode =  preferences.getInt(SP_RESET_PREFERENCES_FRAGMENT_EDIT_MODE, EditorProfileListFragment.EDIT_MODE_UNDEFINED);
					Bundle arguments = new Bundle();
					arguments.putLong(GlobalData.EXTRA_PROFILE_ID, profile_id);
					//arguments.putBoolean(GlobalData.EXTRA_FIRST_START_ACTIVITY, true);
					arguments.putInt(GlobalData.EXTRA_NEW_PROFILE_MODE, editMode);
					arguments.putBoolean(GlobalData.EXTRA_PREFERENCES_ACTIVITY, false);
					ProfilePreferencesFragment fragment = new ProfilePreferencesFragment();
					fragment.setArguments(arguments);
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.editor_detail_container, fragment).commit();
		    	}
		    	if (resetMode == RESET_PREFERENCE_FRAGMENT_RESET_EVENT)
		    	{
					// restart profile preferences fragmentu
		    		long event_id = preferences.getLong(SP_RESET_PREFERENCES_FRAGMENT_DATA_ID, 0);
		    		int editMode =  preferences.getInt(SP_RESET_PREFERENCES_FRAGMENT_EDIT_MODE, EditorProfileListFragment.EDIT_MODE_UNDEFINED);
					Bundle arguments = new Bundle();
					arguments.putLong(GlobalData.EXTRA_EVENT_ID, event_id);
					//arguments.putBoolean(GlobalData.EXTRA_FIRST_START_ACTIVITY, true);
					arguments.putInt(GlobalData.EXTRA_NEW_EVENT_MODE, editMode);
					arguments.putBoolean(GlobalData.EXTRA_PREFERENCES_ACTIVITY, false);
					EventPreferencesFragment fragment = new EventPreferencesFragment();
					fragment.setArguments(arguments);
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.editor_detail_container, fragment).commit();
		    	}
		    	else
		    	if (resetMode == RESET_PREFERENCE_FRAGMENT_REMOVE)
		    	{
					Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.editor_detail_container);
					if (fragment != null)
					{
						getSupportFragmentManager().beginTransaction()
							.remove(fragment).commit();
					}
		    	}
		    	// remove preferences
		    	Editor editor = preferences.edit();
		    	editor.remove(SP_RESET_PREFERENCES_FRAGMENT);
		    	editor.remove(SP_RESET_PREFERENCES_FRAGMENT_DATA_ID);
		    	editor.remove(SP_RESET_PREFERENCES_FRAGMENT_EDIT_MODE);
				editor.commit();
			}
		}
		else
			mTwoPane = false;
		
		drawerLayout = (DrawerLayout) findViewById(R.id.editor_list_drawer_layout);
		drawerRoot = (RelativeLayout) findViewById(R.id.editor_drawer_root);
		drawerListView = (ListView) findViewById(R.id.editor_drawer_list);
		
		int drawerShadowId;
        if (GlobalData.applicationTheme.equals("dark"))
        	drawerShadowId = R.drawable.drawer_shadow_dark;
        else
        	drawerShadowId = R.drawable.drawer_shadow;
		drawerLayout.setDrawerShadow(drawerShadowId, GravityCompat.START);

		// actionbar titles
		drawerItemsTitle = new String[] { 
				getResources().getString(R.string.editor_drawer_title_profiles), 
				getResources().getString(R.string.editor_drawer_title_profiles),
				getResources().getString(R.string.editor_drawer_title_profiles),
				getResources().getString(R.string.editor_drawer_title_events),
				getResources().getString(R.string.editor_drawer_title_events),
				getResources().getString(R.string.editor_drawer_title_events),
				getResources().getString(R.string.editor_drawer_title_events)
              };
		
		// drawer item titles
		drawerItemsSubtitle = new String[] { 
				getResources().getString(R.string.editor_drawer_list_item_profiles_all), 
				getResources().getString(R.string.editor_drawer_list_item_profiles_show_in_activator),
				getResources().getString(R.string.editor_drawer_list_item_profiles_no_show_in_activator),
				getResources().getString(R.string.editor_drawer_list_item_events_all),
				getResources().getString(R.string.editor_drawer_list_item_events_running),
				getResources().getString(R.string.editor_drawer_list_item_events_paused),
				getResources().getString(R.string.editor_drawer_list_item_events_stopped)
              };
		
		drawerItemsIcon = new Integer[] {
				R.drawable.ic_events_drawer_profile_filter_2,
				R.drawable.ic_events_drawer_profile_filter_0,
				R.drawable.ic_events_drawer_profile_filter_1,
				R.drawable.ic_events_drawer_event_filter_2,
				R.drawable.ic_events_drawer_event_filter_0,
				R.drawable.ic_events_drawer_event_filter_1,
				R.drawable.ic_events_drawer_event_filter_3,
			  };
		
		
        // Pass string arrays to EditorDrawerListAdapter
		// use sherlock action bar themed context
        drawerAdapter = new EditorDrawerListAdapter(drawerListView, getSupportActionBar().getThemedContext(), drawerItemsTitle, drawerItemsSubtitle, drawerItemsIcon);
        
        // Set the MenuListAdapter to the ListView
        drawerListView.setAdapter(drawerAdapter);
 
        // Capture listview menu item click
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());
		
		 // Enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        int drawerIconId;
        if (GlobalData.applicationTheme.equals("light"))
        	drawerIconId = R.drawable.ic_drawer;
        else
        	drawerIconId = R.drawable.ic_drawer_dark;
        
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
								                drawerIconId, 
								                R.string.editor_drawer_open,
								                R.string.editor_drawer_close) 
        {
 
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
 
            public void onDrawerOpened(View drawerView) {
                // Set the title on the action when drawer open
                //getSupportActionBar().setTitle(mDrawerTitle);
                super.onDrawerOpened(drawerView);
            }
        };
 
        drawerLayout.setDrawerListener(drawerToggle);
        
        filterStatusbarTitle = (TextView) findViewById(R.id.editor_filter_title);
       
        orderLabel = (TextView) findViewById(R.id.editor_drawer_order_title);
        
        orderSpinner = (Spinner) findViewById(R.id.editor_drawer_order);
        ArrayAdapter<CharSequence> orderSpinneAadapter = ArrayAdapter.createFromResource(
        							getSupportActionBar().getThemedContext(), 
        							R.array.drawerOrderEvents, 
        							R.layout.editor_drawer_spinner);
        if (android.os.Build.VERSION.SDK_INT >= 11)
        	orderSpinneAadapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        else
        	orderSpinneAadapter.setDropDownViewResource(R.layout.editor_drawer_spinner_dropdown);
        orderSpinner.setAdapter(orderSpinneAadapter);
        orderSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				changeEventOrder(position);
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
        
		//getSupportActionBar().setDisplayShowTitleEnabled(false);
		//getSupportActionBar().setTitle(R.string.title_activity_phone_profiles);
		
	/*	
		// Create an array adapter to populate dropdownlist 
	    ArrayAdapter<CharSequence> navigationAdapter =
	            ArrayAdapter.createFromResource(getSupportActionBar().getThemedContext(), R.array.phoneProfilesNavigator, R.layout.sherlock_spinner_item);

	    // Enabling dropdown list navigation for the action bar 
	    getSupportActionBar().setNavigationMode(com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_LIST);

	    // Defining Navigation listener 
	    ActionBar.OnNavigationListener navigationListener = new ActionBar.OnNavigationListener() {

	        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
	            switch(itemPosition) {
	            case 0:
	        		EditorProfileListFragment profileFragment = new EditorProfileListFragment();
	        		getSupportFragmentManager().beginTransaction()
	        			.replace(R.id.editor_list_container, profileFragment).commit();
	    			onStartProfilePreferences(-1, false);
	                break;
	            case 1:
	        		EditorEventListFragment eventFragment = new EditorEventListFragment();
	        		getSupportFragmentManager().beginTransaction()
	        			.replace(R.id.editor_list_container, eventFragment).commit();
	    			onStartEventPreferences(-1, false);
	                break;
	            }
	            return false;
	        }
	    };

	    // Setting dropdown items and item navigation listener for the actionbar 
	    getSupportActionBar().setListNavigationCallbacks(navigationAdapter, navigationListener);
	    navigationAdapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
	*/	

		// select first drawer item
        if (savedInstanceState == null) {
            selectDrawerItem(1, true); // show profile filter FILTER_TYPE_PROFILES_SHOW_IN_ACTIVATOR 
        }
        
		//Log.e("EditorProfilesActivity.onCreate", "xxxx");
		
		
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();

		//Log.d("EditorProfilesActivity.onStart", "xxxx");
		
	}
	
	@Override
	protected void onDestroy()
	{
		if (applicationsCache != null)
			applicationsCache.clearCache();
		applicationsCache = null;
		if (dataWrapper != null)
			dataWrapper.invalidateDataWrapper();
		dataWrapper = null;

		super.onDestroy();

		//Log.e("EditorProfilesActivity.onDestroy","xxx");
	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_editor_profiles, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Intent intent;
			
		switch (item.getItemId()) {
		case android.R.id.home:
            if (drawerLayout.isDrawerOpen(drawerRoot)) {
                drawerLayout.closeDrawer(drawerRoot);
            } else {
                drawerLayout.openDrawer(drawerRoot);
            }	
			return super.onOptionsItemSelected(item);
		case R.id.menu_settings:
			//Log.d("EditorProfilesActivity.onOptionsItemSelected", "menu_settings");
			
			intent = new Intent(getBaseContext(), PhoneProfilesPreferencesActivity.class);

			startActivityForResult(intent, GlobalData.REQUEST_CODE_APPLICATION_PREFERENCES);

			return true;
		case R.id.menu_export:
			//Log.d("EditorProfilesActivity.onOptionsItemSelected", "menu_export");

			exportData();
			
			return true;
		case R.id.menu_import:
			//Log.d("EditorProfilesActivity.onOptionsItemSelected", "menu_import");

			importData();
			
			return true;
		case R.id.menu_exit:
			//Log.d("EditorProfilesActivity.onOptionsItemSelected", "menu_exit");
			
			// zrusenie notifikacie
			dataWrapper.getActivateProfileHelper().showNotification(null);
			
			finish();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
    // ListView click listener in the navigation drawer
    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {

        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            selectDrawerItem(position, true);
        }
    }
 
    private void selectDrawerItem(int position, boolean removePreferences) {
 
    	drawerSelectedItem = position;
    	
    	Fragment fragment;
	    Bundle arguments;
	    
        switch (position) {
        case 0:
        	profilesFilterType = EditorProfileListFragment.FILTER_TYPE_ALL;
    		fragment = new EditorProfileListFragment();
    	    arguments = new Bundle();
   		    arguments.putInt(EditorProfileListFragment.FILTER_TYPE_ARGUMENT, profilesFilterType);
   		    fragment.setArguments(arguments);
    		getSupportFragmentManager().beginTransaction()
    			.replace(R.id.editor_list_container, fragment).commit();
    		if (removePreferences)
    			onStartProfilePreferences(null, EditorProfileListFragment.EDIT_MODE_EDIT, profilesFilterType);
            break;
        case 1:
        	profilesFilterType = EditorProfileListFragment.FILTER_TYPE_SHOW_IN_ACTIVATOR;
    		fragment = new EditorProfileListFragment();
    	    arguments = new Bundle();
   		    arguments.putInt(EditorProfileListFragment.FILTER_TYPE_ARGUMENT, profilesFilterType);
   		    fragment.setArguments(arguments);
    		getSupportFragmentManager().beginTransaction()
    			.replace(R.id.editor_list_container, fragment).commit();
    		if (removePreferences)
    			onStartProfilePreferences(null, EditorProfileListFragment.EDIT_MODE_EDIT, profilesFilterType);
            break;
        case 2:
        	profilesFilterType = EditorProfileListFragment.FILTER_TYPE_NO_SHOW_IN_ACTIVATOR;
    		fragment = new EditorProfileListFragment();
    	    arguments = new Bundle();
   		    arguments.putInt(EditorProfileListFragment.FILTER_TYPE_ARGUMENT, profilesFilterType);
   		    fragment.setArguments(arguments);
    		getSupportFragmentManager().beginTransaction()
    			.replace(R.id.editor_list_container, fragment).commit();
    		if (removePreferences)
    			onStartProfilePreferences(null, EditorProfileListFragment.EDIT_MODE_EDIT, profilesFilterType);
            break;
        case 3:
        	eventsFilterType = EditorEventListFragment.FILTER_TYPE_ALL;
    		fragment = new EditorEventListFragment();
    	    arguments = new Bundle();
   		    arguments.putInt(EditorEventListFragment.FILTER_TYPE_ARGUMENT, eventsFilterType);
   		    arguments.putInt(EditorEventListFragment.ORDER_TYPE_ARGUMENT, eventsOrderType);
   		    fragment.setArguments(arguments);
    		getSupportFragmentManager().beginTransaction()
    			.replace(R.id.editor_list_container, fragment).commit();
    		if (removePreferences)
    			onStartEventPreferences(null, EditorEventListFragment.EDIT_MODE_EDIT, eventsFilterType, eventsOrderType);
			break;	
        case 4:
        	eventsFilterType = EditorEventListFragment.FILTER_TYPE_RUNNING;
    		fragment = new EditorEventListFragment();
    	    arguments = new Bundle();
   		    arguments.putInt(EditorEventListFragment.FILTER_TYPE_ARGUMENT, eventsFilterType);
   		    arguments.putInt(EditorEventListFragment.ORDER_TYPE_ARGUMENT, eventsOrderType);
   		    fragment.setArguments(arguments);
    		getSupportFragmentManager().beginTransaction()
    			.replace(R.id.editor_list_container, fragment).commit();
    		if (removePreferences)
    			onStartEventPreferences(null, EditorEventListFragment.EDIT_MODE_EDIT, eventsFilterType, eventsOrderType);
			break;	
        case 5:
        	eventsFilterType = EditorEventListFragment.FILTER_TYPE_PAUSED;
    		fragment = new EditorEventListFragment();
    	    arguments = new Bundle();
   		    arguments.putInt(EditorEventListFragment.FILTER_TYPE_ARGUMENT, eventsFilterType);
   		    arguments.putInt(EditorEventListFragment.ORDER_TYPE_ARGUMENT, eventsOrderType);
   		    fragment.setArguments(arguments);
    		getSupportFragmentManager().beginTransaction()
    			.replace(R.id.editor_list_container, fragment).commit();
    		if (removePreferences)
    			onStartEventPreferences(null, EditorEventListFragment.EDIT_MODE_EDIT, eventsFilterType, eventsOrderType);
			break;	
        case 6:
        	eventsFilterType = EditorEventListFragment.FILTER_TYPE_STOPPED;
    		fragment = new EditorEventListFragment();
    	    arguments = new Bundle();
   		    arguments.putInt(EditorEventListFragment.FILTER_TYPE_ARGUMENT, eventsFilterType);
   		    arguments.putInt(EditorEventListFragment.FILTER_TYPE_ARGUMENT, eventsFilterType);
   		    fragment.setArguments(arguments);
    		getSupportFragmentManager().beginTransaction()
    			.replace(R.id.editor_list_container, fragment).commit();
    		if (removePreferences)
    			onStartEventPreferences(null, EditorEventListFragment.EDIT_MODE_EDIT, eventsFilterType, eventsOrderType);
			break;	
        }
        drawerListView.setItemChecked(position, true);
 
        // Get the title followed by the position
        setTitle(drawerItemsTitle[position]);
        
        // show/hide order
        if (position < 3)
        {
        	orderLabel.setVisibility(View.GONE);
        	orderSpinner.setVisibility(View.GONE);
        }
        else
        {
        	orderLabel.setVisibility(View.VISIBLE);
        	orderSpinner.setVisibility(View.VISIBLE);
        }

        // set filter statusbar title
        setStatusBarTitle();
        
        
        // Close drawer
		if (GlobalData.applicationEditorAutoCloseDrawer)
			drawerLayout.closeDrawer(drawerRoot);
    }
    
    private void changeEventOrder(int position)
    {
    	orderSelectedItem = position;
    	
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.editor_list_container);
		if ((fragment != null) && (fragment instanceof EditorEventListFragment))
		{
			eventsOrderType = EditorEventListFragment.ORDER_TYPE_EVENT_NAME;
			switch (position)
			{
				case 0: eventsOrderType = EditorEventListFragment.ORDER_TYPE_EVENT_NAME; break;
				case 1: eventsOrderType = EditorEventListFragment.ORDER_TYPE_PROFILE_NAME; break;
				case 2: eventsOrderType = EditorEventListFragment.ORDER_TYPE_EVENT_TYPE_EVENT_NAME; break;
				case 3: eventsOrderType = EditorEventListFragment.ORDER_TYPE_EVENT_TYPE_PROFILE_NAME; break;
			}
			((EditorEventListFragment)fragment).changeListOrder(eventsOrderType);
			
			setStatusBarTitle();
			
	        // Close drawer
			if (GlobalData.applicationEditorAutoCloseDrawer)
				drawerLayout.closeDrawer(drawerRoot);
		}
    	
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == GlobalData.REQUEST_CODE_ACTIVATE_PROFILE)
		{
			EditorProfileListFragment fragment = (EditorProfileListFragment)getSupportFragmentManager().findFragmentById(R.id.editor_list_container);
			if (fragment != null)
				fragment.doOnActivityResult(requestCode, resultCode, data);
		}
		else
		if (requestCode == GlobalData.REQUEST_CODE_PROFILE_PREFERENCES)
		{
			// redraw list fragment after finish ProfilePreferencesFragmentActivity
			long profile_id = data.getLongExtra(GlobalData.EXTRA_PROFILE_ID, 0);
			int newProfileMode = data.getIntExtra(GlobalData.EXTRA_NEW_PROFILE_MODE, EditorProfileListFragment.EDIT_MODE_UNDEFINED);
			
			if (profile_id > 0)
			{
				Profile profile;
				if ((newProfileMode == EditorProfileListFragment.EDIT_MODE_INSERT) ||
					(newProfileMode == EditorProfileListFragment.EDIT_MODE_DUPLICATE))
				{
					profile = dataWrapper.getDatabaseHandler().getProfile(profile_id);
			    	// generate bitmaps
					profile.generateIconBitmap(getBaseContext(), false, 0);
					profile.generatePreferencesIndicator(getBaseContext(), false, 0);
				}
				else
					profile = dataWrapper.getProfileById(profile_id);	
	
				// redraw list fragment , notifications, widgets after finish ProfilePreferencesFragmentActivity
				onRedrawProfileListFragment(profile, newProfileMode);
			}
		}
		else
		if (requestCode == GlobalData.REQUEST_CODE_EVENT_PREFERENCES)
		{
			// redraw list fragment after finish EventPreferencesFragmentActivity
			long event_id = data.getLongExtra(GlobalData.EXTRA_EVENT_ID, 0);
			int newEventMode = data.getIntExtra(GlobalData.EXTRA_NEW_EVENT_MODE, EditorEventListFragment.EDIT_MODE_UNDEFINED);
			
			if (event_id > 0)
			{
				Event event;
				if ((newEventMode == EditorEventListFragment.EDIT_MODE_INSERT) ||
					(newEventMode == EditorEventListFragment.EDIT_MODE_DUPLICATE))
					event = dataWrapper.getDatabaseHandler().getEvent(event_id);
				else
					event = dataWrapper.getEventById(event_id);	
	
				// redraw list fragment , notifications, widgets after finish ProfilePreferencesFragmentActivity
				onRedrawEventListFragment(event, newEventMode);
			}
		}
		else
		if (requestCode == GlobalData.REQUEST_CODE_APPLICATION_PREFERENCES)
		{
			boolean restart = data.getBooleanExtra(GlobalData.EXTRA_RESET_EDITOR, false); 

			dataWrapper.getActivateProfileHelper().showNotification(dataWrapper.getActivatedProfile());
			dataWrapper.getActivateProfileHelper().updateWidget();
			
			if (restart)
			{
				// refresh activity for special changes
				GUIData.reloadActivity(this);
			}
		}
		else
		if (requestCode == GlobalData.REQUEST_CODE_REMOTE_EXPORT)
		{
			//Log.e("EditorProfilesActivity.onActivityResult","resultCode="+resultCode);

			if (resultCode == RESULT_OK)
			{
				doImportData(GUIData.REMOTE_EXPORT_PATH);
			}	
		}
		else
		{
			// send other activity results into preference fragment
			if (drawerSelectedItem < COUNT_DRAWER_PROFILE_ITEMS)
			{
				ProfilePreferencesFragment fragment = (ProfilePreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.editor_detail_container);
				if (fragment != null)
					fragment.doOnActivityResult(requestCode, resultCode, data);
			}
			else
			{
				EventPreferencesFragment fragment = (EventPreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.editor_detail_container);
				if (fragment != null)
					fragment.doOnActivityResult(requestCode, resultCode, data);
			}
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
	        // handle your back button code here
    		if (mTwoPane) {
	    		if (drawerSelectedItem < COUNT_DRAWER_PROFILE_ITEMS)
	    		{
		    		ProfilePreferencesFragment fragment = (ProfilePreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.editor_detail_container);
		    		if ((fragment != null) && (fragment.isActionModeActive()))
		    		{
	    	        	fragment.finishActionMode(ProfilePreferencesFragment.BUTTON_CANCEL);
	    		        return true; // consumes the back key event - ActionMode is not finished
		    		}
		    		else
		    		    return super.dispatchKeyEvent(event);
	    		}
	    		else
	    		{
		    		EventPreferencesFragment fragment = (EventPreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.editor_detail_container);
		    		if ((fragment != null) && (fragment.isActionModeActive()))
		    		{
	    	        	fragment.finishActionMode(EventPreferencesFragment.BUTTON_CANCEL);
		    			return true; // consumes the back key event - ActionMode is not finished
		    		}
		    		else
		    		    return super.dispatchKeyEvent(event);
	    		}
    		}
    		else
    		    return super.dispatchKeyEvent(event);
        }

	    return super.dispatchKeyEvent(event);
	}
	
	private void importExportErrorDialog(int importExport)
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		String resString;
		if (importExport == 1)
			resString = getResources().getString(R.string.import_profiles_alert_title);
		else
			resString = getResources().getString(R.string.export_profiles_alert_title);
		dialogBuilder.setTitle(resString);
		if (importExport == 1)
			resString = getResources().getString(R.string.import_profiles_alert_error);
		else
			resString = getResources().getString(R.string.export_profiles_alert_error);
		dialogBuilder.setMessage(resString + "!");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dialogBuilder.setPositiveButton(android.R.string.ok, null);
		dialogBuilder.show();
	}
	
	@SuppressWarnings({ "unchecked" })
	private boolean importApplicationPreferences(File src) {
	    boolean res = false;
	    ObjectInputStream input = null;
	    try {
	        	input = new ObjectInputStream(new FileInputStream(src));
	            Editor prefEdit = getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, MODE_PRIVATE).edit();
	            prefEdit.clear();
	            Map<String, ?> entries = (Map<String, ?>) input.readObject();
	            for (Entry<String, ?> entry : entries.entrySet()) {
	                Object v = entry.getValue();
	                String key = entry.getKey();

	                if (v instanceof Boolean)
	                    prefEdit.putBoolean(key, ((Boolean) v).booleanValue());
	                else if (v instanceof Float)
	                    prefEdit.putFloat(key, ((Float) v).floatValue());
	                else if (v instanceof Integer)
	                    prefEdit.putInt(key, ((Integer) v).intValue());
	                else if (v instanceof Long)
	                    prefEdit.putLong(key, ((Long) v).longValue());
	                else if (v instanceof String)
	                    prefEdit.putString(key, ((String) v));
	            }
	            prefEdit.commit();
	        res = true;         
	    } catch (FileNotFoundException e) {
	    	// no error, this is OK
	        //e.printStackTrace();
	    	res = true;
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    }finally {
	        try {
	            if (input != null) {
	                input.close();
	            }
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	    return res;
	}	
	
	private void doImportData(String applicationDataPath)
	{
		final Activity activity = this;
		final String _applicationDataPath = applicationDataPath;
		
		class ImportAsyncTask extends AsyncTask<Void, Integer, Integer> 
		{
			private ProgressDialog dialog;
			
			ImportAsyncTask()
			{
		         this.dialog = new ProgressDialog(activity);
			}
			
			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				
			     this.dialog.setMessage(getResources().getString(R.string.import_profiles_alert_title));
			     this.dialog.show();						
				
				// check root, this set GlobalData.rooted for doInBackgroud()
				GlobalData.isRooted();
			}
			
			@Override
			protected Integer doInBackground(Void... params) {
				int ret = dataWrapper.getDatabaseHandler().importDB(_applicationDataPath);
				
				if (ret == 1)
				{
					// check for hardware capability and update data
					ret = dataWrapper.getDatabaseHandler().updateForHardware(getBaseContext());
				}
				if (ret == 1)
				{
					File sd = Environment.getExternalStorageDirectory();
					File exportFile = new File(sd, _applicationDataPath + "/" + GUIData.EXPORT_APP_PREF_FILENAME);
					if (!importApplicationPreferences(exportFile))
						ret = 0;
				}
				
				return ret;
			}
			
			@Override
			protected void onPostExecute(Integer result)
			{
				super.onPostExecute(result);
				
			    if (dialog.isShowing())
		            dialog.dismiss();
				
				if (result == 1)
				{
					GlobalData.loadPreferences(getBaseContext());

					dataWrapper.invalidateProfileList();
					dataWrapper.getActivateProfileHelper().updateWidget();

					// toast notification
					Toast msg = Toast.makeText(getBaseContext(), 
							getResources().getString(R.string.toast_import_ok), 
							Toast.LENGTH_LONG);
					msg.show();

					// refresh activity
					GUIData.reloadActivity(activity);
				
				}
				else
				{
					importExportErrorDialog(1);
				}
			}
			
		}
		
		new ImportAsyncTask().execute();
	}
	
	private void importDataAlert(boolean remoteExport)
	{
		final boolean _remoteExport = remoteExport;
		AlertDialog.Builder dialogBuilder2 = new AlertDialog.Builder(this);
		if (remoteExport)
		{
			dialogBuilder2.setTitle(getResources().getString(R.string.import_profiles_from_phoneprofiles_alert_title2));
			dialogBuilder2.setMessage(getResources().getString(R.string.import_profiles_alert_message) + "?");
			//dialogBuilder2.setIcon(android.R.drawable.ic_dialog_alert);
		}
		else
		{
			dialogBuilder2.setTitle(getResources().getString(R.string.import_profiles_alert_title));
			dialogBuilder2.setMessage(getResources().getString(R.string.import_profiles_alert_message) + "?");
			//dialogBuilder2.setIcon(android.R.drawable.ic_dialog_alert);
		}

		dialogBuilder2.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (_remoteExport)
				{
					// start RemoteExportDataActivity
					Intent intent = new Intent("phoneprofiles.intent.action.EXPORTDATA");
				    startActivityForResult(intent, GlobalData.REQUEST_CODE_REMOTE_EXPORT);		
				}
				else
					doImportData(GUIData.EXPORT_PATH);
			}
		});
		dialogBuilder2.setNegativeButton(R.string.alert_button_no, null);
		dialogBuilder2.show();
	}

	private void importData()
	{
		// test whether the PhoneProfile is installed
		PackageManager packageManager = getBaseContext().getPackageManager();
		Intent phoneProfiles = packageManager.getLaunchIntentForPackage("sk.henrichg.phoneprofiles");
		if (phoneProfiles != null)
		{
			// PhoneProfiles is istalled

			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle(getResources().getString(R.string.import_profiles_from_phoneprofiles_alert_title));
			dialogBuilder.setMessage(getResources().getString(R.string.import_profiles_from_phoneprofiles_alert_message) + "?");
			//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
			
			dialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					importDataAlert(true);
				}
			});
			dialogBuilder.setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					importDataAlert(false);
				}
			});
			dialogBuilder.show();
		}
		else
			importDataAlert(false);
	}
	
	private boolean exportApplicationPreferences(File dst) {
	    boolean res = false;
	    ObjectOutputStream output = null;
	    try {
	        output = new ObjectOutputStream(new FileOutputStream(dst));
	        SharedPreferences pref = 
	                            getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, MODE_PRIVATE);
	        output.writeObject(pref.getAll());

	        res = true;
	    } catch (FileNotFoundException e) {
	    	// this is OK
	        //e.printStackTrace();
	    	res = true;
	    } catch (IOException e) {
	        e.printStackTrace();
	    }finally {
	        try {
	            if (output != null) {
	                output.flush();
	                output.close();
	            }
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	    return res;
	}

	private void exportData()
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(getResources().getString(R.string.export_profiles_alert_title));
		dialogBuilder.setMessage(getResources().getString(R.string.export_profiles_alert_message) + "?");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);

		final Activity activity = this;
		
		dialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				class ExportAsyncTask extends AsyncTask<Void, Integer, Integer> 
				{
					private ProgressDialog dialog;
					
					ExportAsyncTask()
					{
				         this.dialog = new ProgressDialog(activity);
					}
					
					@Override
					protected void onPreExecute()
					{
						super.onPreExecute();
						
					     this.dialog.setMessage(getResources().getString(R.string.export_profiles_alert_title));
					     this.dialog.show();						
					}
					
					@Override
					protected Integer doInBackground(Void... params) {
						
						int ret = dataWrapper.getDatabaseHandler().exportDB();
						if (ret == 1)
						{
							File sd = Environment.getExternalStorageDirectory();
							File exportFile = new File(sd, GUIData.EXPORT_PATH + "/" + GUIData.EXPORT_APP_PREF_FILENAME);
							if (!exportApplicationPreferences(exportFile))
								ret = 0;
						}

						return ret;
					}
					
					@Override
					protected void onPostExecute(Integer result)
					{
						super.onPostExecute(result);
						
					    if (dialog.isShowing())
				            dialog.dismiss();
						
						if (result == 1)
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
					
				}
				
				new ExportAsyncTask().execute();
				
			}
		});
		dialogBuilder.setNegativeButton(R.string.alert_button_no, null);
		dialogBuilder.show();
	}
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
 
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		// activity will restarted
        /*super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig); */
		
		getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
		GUIData.reloadActivity(this);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);
	    drawerSelectedItem = savedInstanceState.getInt("editor_drawer_selected_item", -1);
	    selectDrawerItem(drawerSelectedItem, false);
	    orderSelectedItem = savedInstanceState.getInt("editor_order_selected_item", -1);
	    changeEventOrder(orderSelectedItem);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    // Save the values you need from your textview into "outState"-object
	    outState.putInt("editor_drawer_selected_item", drawerSelectedItem);
	    outState.putInt("editor_order_selected_item", orderSelectedItem);
	    super.onSaveInstanceState(outState);
	}	
	
	 @Override
	 public void setTitle(CharSequence title) {
	     //mTitle = title;
	     getSupportActionBar().setTitle(title);
	 }	
	 
	 private void setStatusBarTitle()
	 {
        // set filter statusbar title
		String text = "";
        if (drawerSelectedItem < COUNT_DRAWER_PROFILE_ITEMS)
        {
        	text = drawerItemsSubtitle[drawerSelectedItem];
        }
        else
        {
        	String[] orderItems = getResources().getStringArray(R.array.drawerOrderEvents);
        	text = drawerItemsSubtitle[drawerSelectedItem] + 
        			"; " +
        			orderItems[orderSelectedItem];
        }
        filterStatusbarTitle.setText(text);
	 }

	public void onStartProfilePreferences(Profile profile, int editMode, int filterType) {

		editModeProfile = editMode;

		onFinishProfilePreferencesActionMode();
		
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.

			if ((profile != null) || 
				(editMode == EditorProfileListFragment.EDIT_MODE_INSERT) ||
				(editMode == EditorProfileListFragment.EDIT_MODE_DUPLICATE))
			{
				Bundle arguments = new Bundle();
				if (editMode == EditorProfileListFragment.EDIT_MODE_INSERT)
					arguments.putLong(GlobalData.EXTRA_PROFILE_ID, 0);
				else
					arguments.putLong(GlobalData.EXTRA_PROFILE_ID, profile._id);
				//arguments.putBoolean(GlobalData.EXTRA_FIRST_START_ACTIVITY, true);
				arguments.putInt(GlobalData.EXTRA_NEW_PROFILE_MODE, editMode);
				arguments.putBoolean(GlobalData.EXTRA_PREFERENCES_ACTIVITY, false);
				ProfilePreferencesFragment fragment = new ProfilePreferencesFragment();
				fragment.setArguments(arguments);
				getSupportFragmentManager().beginTransaction()
					.replace(R.id.editor_detail_container, fragment).commit();
			}
			else
			{
				Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.editor_detail_container);
				if (fragment != null)
				{
					getSupportFragmentManager().beginTransaction()
						.remove(fragment).commit();
				}
			}

		} else {
			// In single-pane mode, simply start the profile preferences activity
			// for the profile position.
			if (((profile != null) || 
				 (editMode == EditorProfileListFragment.EDIT_MODE_INSERT) ||
				 (editMode == EditorProfileListFragment.EDIT_MODE_DUPLICATE))
				&& (editMode != EditorProfileListFragment.EDIT_MODE_DELETE))
			{
				Intent intent = new Intent(getBaseContext(), ProfilePreferencesFragmentActivity.class);
				if (editMode == EditorProfileListFragment.EDIT_MODE_INSERT)
					intent.putExtra(GlobalData.EXTRA_PROFILE_ID, 0);
				else
					intent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile._id);
				//intent.putExtra(GlobalData.EXTRA_FIRST_START_ACTIVITY, true);
				intent.putExtra(GlobalData.EXTRA_NEW_PROFILE_MODE, editMode);
				startActivityForResult(intent, GlobalData.REQUEST_CODE_PROFILE_PREFERENCES);
			}
		}
	}

	public void onRestartProfilePreferences(Profile profile, int newProfileMode) {
		if (mTwoPane) {
			if ((newProfileMode != EditorProfileListFragment.EDIT_MODE_INSERT) &&
			    (newProfileMode != EditorProfileListFragment.EDIT_MODE_DUPLICATE))
			{
				// restart profile preferences fragmentu
				Bundle arguments = new Bundle();
				arguments.putLong(GlobalData.EXTRA_PROFILE_ID, profile._id);
				//arguments.putBoolean(GlobalData.EXTRA_FIRST_START_ACTIVITY, true);
				arguments.putInt(GlobalData.EXTRA_NEW_PROFILE_MODE, editModeProfile);
				arguments.putBoolean(GlobalData.EXTRA_PREFERENCES_ACTIVITY, false);
				ProfilePreferencesFragment fragment = new ProfilePreferencesFragment();
				fragment.setArguments(arguments);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.editor_detail_container, fragment).commit();
			}
			else
			{
				Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.editor_detail_container);
				if (fragment != null)
				{
					getSupportFragmentManager().beginTransaction()
						.remove(fragment).commit();
				}
			}
		}
		else
		{
	    	SharedPreferences preferences = getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Activity.MODE_PRIVATE);
			
			if ((newProfileMode != EditorProfileListFragment.EDIT_MODE_INSERT) &&
			    (newProfileMode != EditorProfileListFragment.EDIT_MODE_DUPLICATE))
			{
				//TODO save into shared preferences, preferences fragment must loadPreferences 
		    	Editor editor = preferences.edit();
		    	editor.putInt(SP_RESET_PREFERENCES_FRAGMENT, RESET_PREFERENCE_FRAGMENT_RESET_PROFILE);
		    	editor.putLong(SP_RESET_PREFERENCES_FRAGMENT_DATA_ID, profile._id);
		    	editor.putInt(SP_RESET_PREFERENCES_FRAGMENT_EDIT_MODE, editModeProfile);
				editor.commit();
			}
			else
			{
				//TODO save into shared preferences, preference fragment must by removed from
				//     activity ?????
		    	Editor editor = preferences.edit();
		    	editor.putInt(SP_RESET_PREFERENCES_FRAGMENT, RESET_PREFERENCE_FRAGMENT_REMOVE);
		    	editor.putLong(SP_RESET_PREFERENCES_FRAGMENT_DATA_ID, profile._id);
		    	editor.putInt(SP_RESET_PREFERENCES_FRAGMENT_EDIT_MODE, editModeProfile);
				editor.commit();
			}
		}
	}

	public void onRedrawProfileListFragment(Profile profile, int newProfileMode) {
		// redraw headeru list fragmentu, notifikacie a widgetov
		EditorProfileListFragment fragment = (EditorProfileListFragment)getSupportFragmentManager().findFragmentById(R.id.editor_list_container);
		if (fragment != null)
		{
			// update profile, this rewrite profile in profileList
			dataWrapper.updateProfile(profile);
			
			boolean newProfile = ((newProfileMode == EditorProfileListFragment.EDIT_MODE_INSERT) ||
		              			  (newProfileMode == EditorProfileListFragment.EDIT_MODE_DUPLICATE));
			fragment.updateListView(profile, newProfile);

			Profile activeProfile = dataWrapper.getActivatedProfile();
			fragment.updateHeader(activeProfile);
			dataWrapper.getActivateProfileHelper().showNotification(activeProfile);
			dataWrapper.getActivateProfileHelper().updateWidget();
			
		}
		onRestartProfilePreferences(profile, newProfileMode);
	}

	public void onFinishProfilePreferencesActionMode() {
		//if (mTwoPane) {
			Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.editor_detail_container);
			if (fragment != null)
			{
				if (fragment instanceof ProfilePreferencesFragment)
					((ProfilePreferencesFragment)fragment).finishActionMode(EventPreferencesFragment.BUTTON_CANCEL);
				else
					((EventPreferencesFragment)fragment).finishActionMode(EventPreferencesFragment.BUTTON_CANCEL);
			}
		//}
	}
	
	public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
		return;
	}
	
	public void onFinishEventPreferencesActionMode() {
		//if (mTwoPane) {
			Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.editor_detail_container);
			if (fragment != null)
			{
				if (fragment instanceof ProfilePreferencesFragment)
					((ProfilePreferencesFragment)fragment).finishActionMode(EventPreferencesFragment.BUTTON_CANCEL);
				else
					((EventPreferencesFragment)fragment).finishActionMode(EventPreferencesFragment.BUTTON_CANCEL);
			}
		//}
	}

	public void onStartEventPreferences(Event event, int editMode, int filterType, int orderType) {

		editModeEvent = editMode;
		
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.

			onFinishEventPreferencesActionMode();
			
			if ((event != null) || 
				(editMode == EditorEventListFragment.EDIT_MODE_INSERT) ||
				(editMode == EditorEventListFragment.EDIT_MODE_DUPLICATE))
			{
				Bundle arguments = new Bundle();
				if (editMode == EditorEventListFragment.EDIT_MODE_INSERT)
					arguments.putLong(GlobalData.EXTRA_EVENT_ID, 0);
				else
					arguments.putLong(GlobalData.EXTRA_EVENT_ID, event._id);
				//arguments.putBoolean(GlobalData.EXTRA_FIRST_START_ACTIVITY, true);
				arguments.putInt(GlobalData.EXTRA_NEW_EVENT_MODE, editMode);
				arguments.putBoolean(GlobalData.EXTRA_PREFERENCES_ACTIVITY, false);
				EventPreferencesFragment fragment = new EventPreferencesFragment();
				fragment.setArguments(arguments);
				getSupportFragmentManager().beginTransaction()
					.replace(R.id.editor_detail_container, fragment).commit();
			}
			else
			{
				Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.editor_detail_container);
				if (fragment != null)
				{
					getSupportFragmentManager().beginTransaction()
						.remove(fragment).commit();
				}
			}

		} else {
			// In single-pane mode, simply start the profile preferences activity
			// for the event id.
			if (((event != null) || 
				 (editMode == EditorEventListFragment.EDIT_MODE_INSERT) ||
				 (editMode == EditorEventListFragment.EDIT_MODE_DUPLICATE))
				&& (editMode != EditorEventListFragment.EDIT_MODE_DELETE))
			{
				Intent intent = new Intent(getBaseContext(), EventPreferencesFragmentActivity.class);
				if (editMode == EditorEventListFragment.EDIT_MODE_INSERT)
					intent.putExtra(GlobalData.EXTRA_EVENT_ID, 0);
				else
					intent.putExtra(GlobalData.EXTRA_EVENT_ID, event._id);
				//intent.putExtra(GlobalData.EXTRA_FIRST_START_ACTIVITY, true);
				intent.putExtra(GlobalData.EXTRA_NEW_EVENT_MODE, editMode);
				startActivityForResult(intent, GlobalData.REQUEST_CODE_EVENT_PREFERENCES);
			}
		}
	}

	public void onRedrawEventListFragment(Event event, int newEventMode) {
		// redraw headeru list fragmentu, notifikacie a widgetov
		EditorEventListFragment fragment = (EditorEventListFragment)getSupportFragmentManager().findFragmentById(R.id.editor_list_container);
		if (fragment != null)
		{
			// update event, this rewrite event in eventList
			dataWrapper.updateEvent(event);
			
			boolean newEvent = ((newEventMode == EditorEventListFragment.EDIT_MODE_INSERT) ||
         			            (newEventMode == EditorEventListFragment.EDIT_MODE_DUPLICATE));
			fragment.updateListView(event, newEvent);
		}
		onRestartEventPreferences(event, newEventMode);
	}

	public void onRestartEventPreferences(Event event, int newEventMode) {
		if (mTwoPane) {
			if ((newEventMode != EditorEventListFragment.EDIT_MODE_INSERT) &&
			    (newEventMode != EditorEventListFragment.EDIT_MODE_DUPLICATE))
			{
				// restart event preferences fragmentu
				Bundle arguments = new Bundle();
				arguments.putLong(GlobalData.EXTRA_EVENT_ID, event._id);
				//arguments.putBoolean(GlobalData.EXTRA_FIRST_START_ACTIVITY, true);
				arguments.putInt(GlobalData.EXTRA_NEW_EVENT_MODE, editModeEvent);
				arguments.putBoolean(GlobalData.EXTRA_PREFERENCES_ACTIVITY, false);
				EventPreferencesFragment fragment = new EventPreferencesFragment();
				fragment.setArguments(arguments);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.editor_detail_container, fragment).commit();
			}
			else
			{
				Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.editor_detail_container);
				if (fragment != null)
				{
					getSupportFragmentManager().beginTransaction()
						.remove(fragment).commit();
				}
			}
		}
		else
		{
	    	SharedPreferences preferences = getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Activity.MODE_PRIVATE);
			
			if ((newEventMode != EditorEventListFragment.EDIT_MODE_INSERT) &&
			    (newEventMode != EditorEventListFragment.EDIT_MODE_DUPLICATE))
			{
				//TODO save into shared preferences, preferences fragment must loadPreferences 
		    	Editor editor = preferences.edit();
		    	editor.putInt(SP_RESET_PREFERENCES_FRAGMENT, RESET_PREFERENCE_FRAGMENT_RESET_EVENT);
		    	editor.putLong(SP_RESET_PREFERENCES_FRAGMENT_DATA_ID, event._id);
		    	editor.putInt(SP_RESET_PREFERENCES_FRAGMENT_EDIT_MODE, editModeEvent);
				editor.commit();
			}
			else
			{
				//TODO save into shared preferences, preference fragment must by removed from
				//     activity ?????
		    	Editor editor = preferences.edit();
		    	editor.putInt(SP_RESET_PREFERENCES_FRAGMENT, RESET_PREFERENCE_FRAGMENT_REMOVE);
		    	editor.putLong(SP_RESET_PREFERENCES_FRAGMENT_DATA_ID, event._id);
		    	editor.putInt(SP_RESET_PREFERENCES_FRAGMENT_EDIT_MODE, editModeEvent);
				editor.commit();
			}
		}
	}
	
	public static ApplicationsCache getApplicationsCache()
	{
		return applicationsCache;
	}

}