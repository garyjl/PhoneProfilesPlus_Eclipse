package sk.henrichg.phoneprofiles;

import sk.henrichg.phoneprofiles.EditorEventListFragment.OnEventCountChanged;
import sk.henrichg.phoneprofiles.EditorEventListFragment.OnFinishEventPreferencesActionMode;
import sk.henrichg.phoneprofiles.EditorEventListFragment.OnStartEventPreferences;
import sk.henrichg.phoneprofiles.EditorProfileListFragment.OnFinishProfilePreferencesActionMode;
import sk.henrichg.phoneprofiles.EditorProfileListFragment.OnProfileCountChanged;
import sk.henrichg.phoneprofiles.EditorProfileListFragment.OnProfileOrderChanged;
import sk.henrichg.phoneprofiles.EditorProfileListFragment.OnStartProfilePreferences;
import sk.henrichg.phoneprofiles.EventPreferencesFragment.OnRedrawEventListFragment;
import sk.henrichg.phoneprofiles.EventPreferencesFragment.OnRestartEventPreferences;
import sk.henrichg.phoneprofiles.PreferenceListFragment.OnPreferenceAttachedListener;
import sk.henrichg.phoneprofiles.ProfilePreferencesFragment.OnRedrawProfileListFragment;
import sk.henrichg.phoneprofiles.ProfilePreferencesFragment.OnRestartProfilePreferences;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
                                               OnProfileCountChanged,
                                               OnProfileOrderChanged,
                                               OnStartEventPreferences,
                                               OnRestartEventPreferences,
                                               OnRedrawEventListFragment,
                                               OnFinishEventPreferencesActionMode,
                                               OnEventCountChanged
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
	ListView drawerListView;
	ActionBarDrawerToggle drawerToggle;
	
	String[] drawerItemsTitle;
	EditorDrawerListAdapter drawerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		GUIData.setTheme(this, false);
		GUIData.setLanguage(getBaseContext());

		super.onCreate(savedInstanceState);
		
		profilesDataWrapper = new ProfilesDataWrapper(GlobalData.context, true, false, 0, GlobalData.applicationEditorPrefIndicator, false, false);
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
			onStartProfilePreferences(-1, false);

		}
		
		drawerLayout = (DrawerLayout) findViewById(R.id.editor_list_drawer_layout);
		drawerListView = (ListView) findViewById(R.id.editor_list_drawer);
		
		int drawerShadowId;
        if (GlobalData.applicationTheme.equals("dark"))
        	drawerShadowId = R.drawable.drawer_shadow_dark;
        else
        	drawerShadowId = R.drawable.drawer_shadow;
		drawerLayout.setDrawerShadow(drawerShadowId, GravityCompat.START);

		// drawer item titles
		drawerItemsTitle = new String[] { "Profiles - all", 
                "Profiles - show in Activator",
				  "Profiles - no show in Activator",
                "Editor - all",
                "Editor - enabled",
                "Editor - disabled"
              };

		
        // Pass string arrays to MenuListAdapter
        drawerAdapter = new EditorDrawerListAdapter(getBaseContext(), drawerItemsTitle);
        
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
								                R.string.editor_list_drawer_open,
								                R.string.editor_list_drawer_close) 
        {
 
            public void onDrawerClosed(View view) {
                // TODO Auto-generated method stub
                super.onDrawerClosed(view);
            }
 
            public void onDrawerOpened(View drawerView) {
                // TODO Auto-generated method stub
                // Set the title on the action when drawer open
                //getSupportActionBar().setTitle(mDrawerTitle);
                super.onDrawerOpened(drawerView);
            }
        };
 
        drawerLayout.setDrawerListener(drawerToggle);
        
		//getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setTitle(R.string.title_activity_phone_profiles);
		
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
            selectItem(0);
        }
        
		//Log.d("EditorProfilesActivity.onCreate", "xxxx");
		
		
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();

		if (PhoneProfilesPreferencesActivity.getInvalidateEditor(true))
		{
			//Log.d("EditorProfilesActivity.onStart", "invalidate");

			// refresh activity
			Intent refresh = new Intent(getBaseContext(), EditorProfilesActivity.class);
			startActivity(refresh);
			finish();
			
			return;
		}
		
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
            if (drawerLayout.isDrawerOpen(drawerListView)) {
                drawerLayout.closeDrawer(drawerListView);
            } else {
                drawerLayout.openDrawer(drawerListView);
            }	
			return super.onOptionsItemSelected(item);
		case R.id.menu_settings:
			//Log.d("EditorProfilesActivity.onOptionsItemSelected", "menu_settings");
			
			intent = new Intent(getBaseContext(), PhoneProfilesPreferencesActivity.class);

			startActivity(intent);

			return true;
		case R.id.menu_export:
			//Log.d("EditorProfileListFragment.onOptionsItemSelected", "menu_export");

			exportData();
			
			return true;
		case R.id.menu_import:
			//Log.d("EditorProfileListFragment.onOptionsItemSelected", "menu_import");

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
            selectItem(position);
        }
    }
 
    private void selectItem(int position) {
 
        // Locate Position
    	Fragment fragment;
    	
        switch (position) {
        case 0:
    		fragment = new EditorProfileListFragment();
    		getSupportFragmentManager().beginTransaction()
    			.replace(R.id.editor_list_container, fragment).commit();
			onStartProfilePreferences(-1, false);
            break;
        case 1:
    		fragment = new EditorProfileListFragment();
    		getSupportFragmentManager().beginTransaction()
    			.replace(R.id.editor_list_container, fragment).commit();
			onStartProfilePreferences(-1, false);
            break;
        case 2:
    		fragment = new EditorProfileListFragment();
    		getSupportFragmentManager().beginTransaction()
    			.replace(R.id.editor_list_container, fragment).commit();
			onStartProfilePreferences(-1, false);
            break;
        case 3:
    		fragment = new EditorEventListFragment();
    		getSupportFragmentManager().beginTransaction()
    			.replace(R.id.editor_list_container, fragment).commit();
			onStartEventPreferences(-1, false);
			break;	
        case 4:
    		fragment = new EditorEventListFragment();
    		getSupportFragmentManager().beginTransaction()
    			.replace(R.id.editor_list_container, fragment).commit();
			onStartEventPreferences(-1, false);
			break;	
        case 5:
    		fragment = new EditorEventListFragment();
    		getSupportFragmentManager().beginTransaction()
    			.replace(R.id.editor_list_container, fragment).commit();
			onStartEventPreferences(-1, false);
			break;	
        }
        drawerListView.setItemChecked(position, true);
 
        // Get the title followed by the position
        setTitle(drawerItemsTitle[position]);
        // Close drawer
        drawerLayout.closeDrawer(drawerListView);
    }	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		ProfilePreferencesFragment fragment = (ProfilePreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.editor_detail_container);
		if (fragment != null)
			fragment.doOnActivityResult(requestCode, resultCode, data);
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
	
	private void importData()
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(getResources().getString(R.string.import_profiles_alert_title));
		dialogBuilder.setMessage(getResources().getString(R.string.import_profiles_alert_message) + "?");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);

		final Activity activity = this;
		
		dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
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
							profilesDataWrapper.invalidateProfileList();
							onProfileCountChanged();

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
		});
		dialogBuilder.setNegativeButton(android.R.string.no, null);
		dialogBuilder.show();
	}

	private void exportData()
	{
		new AsyncTask<Void, Integer, Integer>() {
			
			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
			}
			
			@Override
			protected Integer doInBackground(Void... params) {
				return profilesDataWrapper.getDatabaseHandler().exportDB();
			}
			
			@Override
			protected void onPostExecute(Integer result)
			{
				super.onPostExecute(result);
				
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
			
		}.execute();
		
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
	 public void setTitle(CharSequence title) {
	     //mTitle = title;
	     getSupportActionBar().setTitle(title);
	 }	

	public void onStartProfilePreferences(int position, boolean afterDelete) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.

			if (position >= 0)
			{
				Bundle arguments = new Bundle();
				arguments.putInt(GlobalData.EXTRA_PROFILE_POSITION, position);
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
			if (position >= 0 && (!afterDelete))
			{
				Intent intent = new Intent(getBaseContext(), ProfilePreferencesFragmentActivity.class);
				intent.putExtra(GlobalData.EXTRA_PROFILE_POSITION, position);
				startActivity(intent);
			}
		}
	}

	public void onRestartProfilePreferences(int position) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.

			// restart profile preferences fragmentu
			Bundle arguments = new Bundle();
			arguments.putInt(GlobalData.EXTRA_PROFILE_POSITION, position);
			ProfilePreferencesFragment fragment = new ProfilePreferencesFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.editor_detail_container, fragment).commit();
		}
	}

	public void onRedrawProfileListFragment() {
		// redraw headeru list fragmentu, notifikacie a widgetov
		EditorProfileListFragment fragment = (EditorProfileListFragment)getSupportFragmentManager().findFragmentById(R.id.editor_list_container);
		if (fragment != null)
		{
			fragment.updateListView();
			Profile profile = profilesDataWrapper.getActivatedProfile();
			fragment.updateHeader(profile);
			profilesDataWrapper.getActivateProfileHelper().showNotification(profile);
			profilesDataWrapper.getActivateProfileHelper().updateWidget();
			
			// send message into service
	        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
			serviceCommunication.sendMessageIntoService(PhoneProfilesService.MSG_RELOAD_DATA);
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
		// send message into service
        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
		serviceCommunication.sendMessageIntoService(PhoneProfilesService.MSG_RELOAD_DATA);
		profilesDataWrapper.getActivateProfileHelper().updateWidget();
	}

	public void onProfileCountChanged() {
		// send message into service
        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
		serviceCommunication.sendMessageIntoService(PhoneProfilesService.MSG_RELOAD_DATA);
	}
	
	public void onEventCountChanged() {
		// send message into service
        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
		serviceCommunication.sendMessageIntoService(PhoneProfilesService.MSG_RELOAD_DATA);
	}

	public void onFinishEventPreferencesActionMode() {
		if (mTwoPane) {
			EventPreferencesFragment fragment = (EventPreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.editor_detail_container);
			if (fragment != null)
				fragment.finishActionMode();
		}
	}

	public void onStartEventPreferences(int position, boolean afterDelete) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.

			if (position >= 0)
			{
				Bundle arguments = new Bundle();
				arguments.putInt(GlobalData.EXTRA_EVENT_POSITION, position);
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
			if (position >= 0 && (!afterDelete))
			{
				Intent intent = new Intent(getBaseContext(), EventPreferencesFragmentActivity.class);
				intent.putExtra(GlobalData.EXTRA_EVENT_POSITION, position);
				startActivity(intent);
			}
		}
	}

	public void onRedrawEventListFragment() {
		// redraw headeru list fragmentu, notifikacie a widgetov
		EditorEventListFragment fragment = (EditorEventListFragment)getSupportFragmentManager().findFragmentById(R.id.editor_list_container);
		if (fragment != null)
		{
			fragment.updateListView();
			
			// send message into service
	        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
			serviceCommunication.sendMessageIntoService(PhoneProfilesService.MSG_RELOAD_DATA);
		}
	}

	public void onRestartEventPreferences(int position) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.

			// restart event preferences fragmentu
			Bundle arguments = new Bundle();
			arguments.putInt(GlobalData.EXTRA_EVENT_POSITION, position);
			EventPreferencesFragment fragment = new EventPreferencesFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.editor_detail_container, fragment).commit();
		}
	}
	
	public static void updateListView(SherlockFragmentActivity activity)
	{
		EditorProfileListFragment fragment = (EditorProfileListFragment)activity.getSupportFragmentManager().findFragmentById(R.id.editor_list_container);
		if (fragment != null)
		{
			//Log.d("EditorProfileActivity.updateListView","");
			fragment.updateListView();
		}
	}
	
	public static ApplicationsCache getApplicationsCache()
	{
		return applicationsCache;
	}

}
