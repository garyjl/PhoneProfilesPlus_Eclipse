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

import sk.henrichg.phoneprofilesplus.EditorEventListFragment.OnAllEventsDeleted;
import sk.henrichg.phoneprofilesplus.EditorEventListFragment.OnEventAdded;
import sk.henrichg.phoneprofilesplus.EditorEventListFragment.OnEventDeleted;
import sk.henrichg.phoneprofilesplus.EditorEventListFragment.OnFinishEventPreferencesActionMode;
import sk.henrichg.phoneprofilesplus.EditorEventListFragment.OnStartEventPreferences;
import sk.henrichg.phoneprofilesplus.EditorProfileListFragment.OnAllProfilesDeleted;
import sk.henrichg.phoneprofilesplus.EditorProfileListFragment.OnFinishProfilePreferencesActionMode;
import sk.henrichg.phoneprofilesplus.EditorProfileListFragment.OnProfileAdded;
import sk.henrichg.phoneprofilesplus.EditorProfileListFragment.OnProfileDeleted;
import sk.henrichg.phoneprofilesplus.EditorProfileListFragment.OnProfileOrderChanged;
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
import android.content.ComponentName;
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
                                               OnProfileAdded,
                                               OnProfileDeleted,
                                               OnAllProfilesDeleted,
                                               OnProfileOrderChanged,
                                               OnStartEventPreferences,
                                               OnRestartEventPreferences,
                                               OnRedrawEventListFragment,
                                               OnFinishEventPreferencesActionMode,
                                               OnEventAdded,
                                               OnEventDeleted,
                                               OnAllEventsDeleted
{

	public static ProfilesDataWrapper profilesDataWrapper;
	public static ServiceCommunication serviceCommunication;
	private static ApplicationsCache applicationsCache;
	
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	
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
	
	private int drawerSelectedItem = -1;
	private int profilesFilterType = EditorProfileListFragment.FILTER_TYPE_SHOW_IN_ACTIVATOR;
	private int eventsFilterType = EditorEventListFragment.FILTER_TYPE_ALL;
	private int eventsOrderType = EditorEventListFragment.ORDER_TYPE_EVENT_NAME;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		GUIData.setTheme(this, false);
		GUIData.setLanguage(getBaseContext());

		super.onCreate(savedInstanceState);
		
		profilesDataWrapper = new ProfilesDataWrapper(GlobalData.context, true, false, 0);
		
		serviceCommunication = new ServiceCommunication(GlobalData.context);
		
		applicationsCache = new ApplicationsCache();
		
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

			//Profile profile = profilesDataWrapper.getFirstProfile();
			
			//onStartProfilePreferences(profilesDataWrapper.getProfileItemPosition(profile), false);
			onStartProfilePreferences(null, profilesFilterType, false);

		}
		
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
            selectDrawerItem(1); // show profile filter FILTER_TYPE_PROFILES_SHOW_IN_ACTIVATOR 
        }
        
		//Log.d("EditorProfilesActivity.onCreate", "xxxx");
		
		
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
		applicationsCache.clearCache();
		super.onDestroy();
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
			profilesDataWrapper.getActivateProfileHelper().showNotification(null);
			
			// zrusenie service
			GlobalData.stopService(getApplicationContext());
			
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
            selectDrawerItem(position);
        }
    }
 
    private void selectDrawerItem(int position) {
 
        // Locate Position

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
			onStartProfilePreferences(null, profilesFilterType, false);
            break;
        case 1:
        	profilesFilterType = EditorProfileListFragment.FILTER_TYPE_SHOW_IN_ACTIVATOR;
    		fragment = new EditorProfileListFragment();
    	    arguments = new Bundle();
   		    arguments.putInt(EditorProfileListFragment.FILTER_TYPE_ARGUMENT, profilesFilterType);
   		    fragment.setArguments(arguments);
    		getSupportFragmentManager().beginTransaction()
    			.replace(R.id.editor_list_container, fragment).commit();
			onStartProfilePreferences(null, profilesFilterType, false);
            break;
        case 2:
        	profilesFilterType = EditorProfileListFragment.FILTER_TYPE_NO_SHOW_IN_ACTIVATOR;
    		fragment = new EditorProfileListFragment();
    	    arguments = new Bundle();
   		    arguments.putInt(EditorProfileListFragment.FILTER_TYPE_ARGUMENT, profilesFilterType);
   		    fragment.setArguments(arguments);
    		getSupportFragmentManager().beginTransaction()
    			.replace(R.id.editor_list_container, fragment).commit();
			onStartProfilePreferences(null, profilesFilterType, false);
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
			onStartEventPreferences(null, eventsFilterType, eventsOrderType, false);
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
			onStartEventPreferences(null, eventsFilterType, eventsOrderType, false);
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
			onStartEventPreferences(null, eventsFilterType, eventsOrderType, false);
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
			onStartEventPreferences(null, eventsFilterType, eventsOrderType, false);
			break;	
        }
        drawerListView.setItemChecked(position, true);
 
        // Get the title followed by the position
        setTitle(drawerItemsTitle[position]);
        // set filter statusbar title
        filterStatusbarTitle.setText(drawerItemsSubtitle[position]);
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
        // Close drawer
		if (GlobalData.applicationEditorAutoCloseDrawer)
			drawerLayout.closeDrawer(drawerRoot);
    }
    
    private void changeEventOrder(int position)
    {
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
			onRedrawProfileListFragment(profilesDataWrapper.getProfileById(profile_id));
		}
		else
		if (requestCode == GlobalData.REQUEST_CODE_EVENT_PREFERENCES)
		{
			// redraw list fragment after finish EventPreferencesFragmentActivity
			long event_id = data.getLongExtra(GlobalData.EXTRA_EVENT_ID, 0);
			onRedrawEventListFragment(profilesDataWrapper.getEventById(event_id));
		}
		else
		if (requestCode == GlobalData.REQUEST_CODE_APPLICATION_PREFERENCES)
		{
			boolean restart = data.getBooleanExtra(GlobalData.EXTRA_RESET_EDITOR, false); 

			profilesDataWrapper.getActivateProfileHelper().showNotification(profilesDataWrapper.getActivatedProfile());
			profilesDataWrapper.getActivateProfileHelper().updateWidget();
			
			if (restart)
			{
				// refresh activity for special changes
				Intent refresh = new Intent(getBaseContext(), EditorProfilesActivity.class);
				startActivity(refresh);
				finish();
			}
		}
		else
		if (requestCode == GlobalData.REQUEST_CODE_REMOTE_EXPORT)
		{
			if (resultCode == RESULT_OK)
				doImportData();
		}
		else
		{
			ProfilePreferencesFragment fragment = (ProfilePreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.editor_detail_container);
			if (fragment != null)
				fragment.doOnActivityResult(requestCode, resultCode, data);
		}
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
	
	private void doImportData()
	{
		final Activity activity = this;
		
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
				int ret = profilesDataWrapper.getDatabaseHandler().importDB();
				
				if (ret == 1)
				{
					// check for hardware capability and update data
					ret = profilesDataWrapper.getDatabaseHandler().updateForHardware(GlobalData.context);
				}
				if (ret == 1)
				{
					File sd = Environment.getExternalStorageDirectory();
					File exportFile = new File(sd, GUIData.EXPORT_PATH + "/" + GUIData.EXPORT_APP_PREF_FILENAME);
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
					GlobalData.loadPreferences(getApplicationContext());

					profilesDataWrapper.invalidateProfileList();
					profilesDataWrapper.getActivateProfileHelper().updateWidget();

					// send message into service
			        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
					serviceCommunication.sendMessageIntoService(PhoneProfilesService.MSG_DATA_IMPORTED);
					
					// toast notification
					Toast msg = Toast.makeText(getBaseContext(), 
							getResources().getString(R.string.toast_import_ok), 
							Toast.LENGTH_LONG);
					msg.show();

					// refresh activity
					Intent refresh = new Intent(getBaseContext(), EditorProfilesActivity.class);
					startActivity(refresh);
					finish();
				
				}
				else
				{
					importExportErrorDialog(1);
				}
			}
			
		}
		
		new ImportAsyncTask().execute();
	}
	
	private void importDataWithAlert()
	{
		AlertDialog.Builder dialogBuilder2 = new AlertDialog.Builder(this);
		dialogBuilder2.setTitle(getResources().getString(R.string.import_profiles_alert_title));
		dialogBuilder2.setMessage(getResources().getString(R.string.import_profiles_alert_message) + "?");
		//dialogBuilder2.setIcon(android.R.drawable.ic_dialog_alert);

		dialogBuilder2.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				doImportData();
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
					// start RemoteExportDataActivity
					Intent intent = new Intent("phoneprofiles.intent.action.EXPORTDATA");
				    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    startActivityForResult(intent, GlobalData.REQUEST_CODE_REMOTE_EXPORT);		
				}
			});
			dialogBuilder.setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					importDataWithAlert();
				}
			});
			dialogBuilder.show();
		}
		else
			importDataWithAlert();
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
						
						int ret = profilesDataWrapper.getDatabaseHandler().exportDB();
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
		Intent intent = getIntent();
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);
	    // Read values from the "savedInstanceState"-object and put them in your textview
	    drawerSelectedItem = savedInstanceState.getInt("editor_drawer_selected_item", -1);
	    selectDrawerItem(drawerSelectedItem);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    // Save the values you need from your textview into "outState"-object
	    outState.putInt("editor_drawer_selected_item", drawerSelectedItem);
	    super.onSaveInstanceState(outState);
	}	
	
	 @Override
	 public void setTitle(CharSequence title) {
	     //mTitle = title;
	     getSupportActionBar().setTitle(title);
	 }	

	public void onStartProfilePreferences(Profile profile, int filterType, boolean afterDelete) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.

			if (profile != null)
			{
				Bundle arguments = new Bundle();
				arguments.putLong(GlobalData.EXTRA_PROFILE_ID, profile._id);
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
			if ((profile != null) && (!afterDelete))
			{
				Intent intent = new Intent(getBaseContext(), ProfilePreferencesFragmentActivity.class);
				intent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile._id);
				startActivityForResult(intent, GlobalData.REQUEST_CODE_PROFILE_PREFERENCES);
			}
		}
	}

	public void onRestartProfilePreferences(Profile profile) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.

			// restart profile preferences fragmentu
			Bundle arguments = new Bundle();
			arguments.putLong(GlobalData.EXTRA_PROFILE_ID, profile._id);
			ProfilePreferencesFragment fragment = new ProfilePreferencesFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.editor_detail_container, fragment).commit();
		}
	}

	public void onRedrawProfileListFragment(Profile profile) {
		// redraw headeru list fragmentu, notifikacie a widgetov
		EditorProfileListFragment fragment = (EditorProfileListFragment)getSupportFragmentManager().findFragmentById(R.id.editor_list_container);
		if (fragment != null)
		{
			fragment.updateListView(profile);

			Profile activeProfile = profilesDataWrapper.getActivatedProfile();
			fragment.updateHeader(activeProfile);
			profilesDataWrapper.getActivateProfileHelper().showNotification(activeProfile);
			profilesDataWrapper.getActivateProfileHelper().updateWidget();
			
			// send message into service
	        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
			serviceCommunication.sendMessageIntoServiceDataChange(PhoneProfilesService.MSG_PROFILE_UPDATED, profile._id);
		}
	}

	public void onFinishProfilePreferencesActionMode() {
		if (mTwoPane) {
			ProfilePreferencesFragment fragment = (ProfilePreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.editor_detail_container);
			if (fragment != null)
				fragment.finishActionMode();
		}
	}
	
	public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
		return;
	}
	
	public void onProfileOrderChanged() {
		// no needed send message into service
		// send message into service
        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
		//serviceCommunication.sendMessageIntoService(PhoneProfilesService.MSG_RELOAD_DATA);
	}

	public void onProfileAdded(Profile profile) {
		// send message into service
        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
		serviceCommunication.sendMessageIntoServiceDataChange(PhoneProfilesService.MSG_PROFILE_ADDED, profile._id);
	}

	public void onProfileDeleted(Profile profile) {
		// send message into service
        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
		serviceCommunication.sendMessageIntoServiceDataChange(PhoneProfilesService.MSG_PROFILE_DELETED, profile._id);
	}
	
	public void onAllProfilesDeleted() {
		// send message into service
        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
		serviceCommunication.sendMessageIntoService(PhoneProfilesService.MSG_ALL_PROFILES_DELETED);
	}
	
	public void onFinishEventPreferencesActionMode() {
		if (mTwoPane) {
			EventPreferencesFragment fragment = (EventPreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.editor_detail_container);
			if (fragment != null)
				fragment.finishActionMode();
		}
	}

	public void onStartEventPreferences(Event event, int filterType, int orderType, boolean afterDelete) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.

			if (event != null)
			{
				Bundle arguments = new Bundle();
				arguments.putLong(GlobalData.EXTRA_EVENT_ID, event._id);
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
			// for the profile position.
			if ((event != null) && (!afterDelete))
			{
				Intent intent = new Intent(getBaseContext(), EventPreferencesFragmentActivity.class);
				intent.putExtra(GlobalData.EXTRA_EVENT_ID, event._id);
				startActivityForResult(intent, GlobalData.REQUEST_CODE_EVENT_PREFERENCES);
			}
		}
	}

	public void onRedrawEventListFragment(Event event) {
		// redraw headeru list fragmentu, notifikacie a widgetov
		EditorEventListFragment fragment = (EditorEventListFragment)getSupportFragmentManager().findFragmentById(R.id.editor_list_container);
		if (fragment != null)
		{
			fragment.updateListView(event);
			
			// send message into service
	        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
			serviceCommunication.sendMessageIntoServiceDataChange(PhoneProfilesService.MSG_EVENT_UPDATED, event._id);
		}
	}

	public void onRestartEventPreferences(Event event) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.

			// restart event preferences fragmentu
			Bundle arguments = new Bundle();
			arguments.putLong(GlobalData.EXTRA_EVENT_ID, event._id);
			EventPreferencesFragment fragment = new EventPreferencesFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.editor_detail_container, fragment).commit();
		}
	}
	
	public void onEventAdded(Event event) {
		// send message into service
        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
		serviceCommunication.sendMessageIntoServiceDataChange(PhoneProfilesService.MSG_EVENT_ADDED, event._id);
	}

	public void onEventDeleted(Event event) {
		// send message into service
        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
		serviceCommunication.sendMessageIntoServiceDataChange(PhoneProfilesService.MSG_EVENT_DELETED, event._id);
	}
	
	public void onAllEventsDeleted() {
		// send message into service
        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
		serviceCommunication.sendMessageIntoService(PhoneProfilesService.MSG_ALL_EVENTS_DELETED);
	}
	
	
	
/*
	public static void updateListView(SherlockFragmentActivity activity)
	{
		EditorProfileListFragment fragment = (EditorProfileListFragment)activity.getSupportFragmentManager().findFragmentById(R.id.editor_list_container);
		if (fragment != null)
		{
			//Log.d("EditorProfileActivity.updateListView","");
			fragment.updateListView();
		}
	}
*/	
	public static ApplicationsCache getApplicationsCache()
	{
		return applicationsCache;
	}

}
