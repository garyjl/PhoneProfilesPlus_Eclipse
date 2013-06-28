package sk.henrichg.phoneprofiles;

import sk.henrichg.phoneprofiles.EditorProfileListFragment.OnFinishProfilePreferencesActionMode;
import sk.henrichg.phoneprofiles.EditorProfileListFragment.OnProfileCountChanged;
import sk.henrichg.phoneprofiles.EditorProfileListFragment.OnProfileOrderChanged;
import sk.henrichg.phoneprofiles.EditorProfileListFragment.OnStartProfilePreferences;
import sk.henrichg.phoneprofiles.PreferenceListFragment.OnPreferenceAttachedListener;
import sk.henrichg.phoneprofiles.ProfilePreferencesFragment.OnRedrawListFragment;
import sk.henrichg.phoneprofiles.ProfilePreferencesFragment.OnRestartProfilePreferences;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceScreen;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.util.Log;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class EditorProfilesActivity extends SherlockFragmentActivity
                                    implements OnStartProfilePreferences,
                                               OnPreferenceAttachedListener,
                                               OnRestartProfilePreferences,
                                               OnRedrawListFragment,
                                               OnFinishProfilePreferencesActionMode,
                                               OnProfileCountChanged,
                                               OnProfileOrderChanged
{

	private static ApplicationsCache applicationsCache;
	
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	
	private boolean serviceConnected;

    private ServiceConnection serviceConnection = new ServiceConnection() {

    	public void onServiceConnected(ComponentName className, IBinder service) {
    		serviceConnected = true;
    		GUIData.profilesDataWrapper.sendMessageIntoService(service, PhoneProfilesService.MSG_RELOAD_DATA);
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
        	GUIData.profilesDataWrapper.phoneProfilesService = null;
        	serviceConnected = false;
        }

    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		GUIData.setTheme(this, false);
		GUIData.setLanguage(getBaseContext());

		super.onCreate(savedInstanceState);
		
		GUIData.getData(GlobalData.context);

		applicationsCache = new ApplicationsCache();
		
		setContentView(R.layout.activity_editor_profile_list);
		
		if (findViewById(R.id.editor_profile_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			Profile profile = GUIData.profilesDataWrapper.getFirstProfile();
			
			//if (profile != null)
				onStartProfilePreferences(GUIData.profilesDataWrapper.getItemPosition(profile), false);

		}
		
		
		//getSupportActionBar().setHomeButtonEnabled(true);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	/*	getSupportActionBar().setDisplayShowTitleEnabled(false);
		
		// Create an array adapter to populate dropdownlist 
	    ArrayAdapter<CharSequence> navigationAdapter =
	            ArrayAdapter.createFromResource(getBaseContext(), R.array.phoneProfilesNavigator, R.layout.sherlock_spinner_item);

	    // Enabling dropdown list navigation for the action bar 
	    getSupportActionBar().setNavigationMode(com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_LIST);
	*/

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

		
		//Log.d("EditorProfilesActivity.onCreate", "xxxx");
		
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();

		if (PhoneProfilesPreferencesActivity.getInvalidateEditor(true))
		{
			Log.d("EditorProfilesActivity.onStart", "invalidate");

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
		if (serviceConnected)
			unbindService(serviceConnection);
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
		case R.id.menu_settings:
			//Log.d("EditorProfilesActivity.onOptionsItemSelected", "menu_settings");
			
			intent = new Intent(getBaseContext(), PhoneProfilesPreferencesActivity.class);

			startActivity(intent);

			return true;
		case R.id.menu_exit:
			//Log.d("EditorProfilesActivity.onOptionsItemSelected", "menu_exit");
			
			// zrusenie notifikacie
			EditorProfileListFragment fragment = (EditorProfileListFragment)getSupportFragmentManager().findFragmentById(R.id.editor_profile_list);
			if (fragment != null)
				fragment.getActivateProfileHelper().showNotification(null);
			
			// zrusenie service
			GlobalData.stopService(getApplicationContext());
			
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
		Intent intent = getIntent();
		startActivity(intent);
		finish();
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
					.replace(R.id.editor_profile_detail_container, fragment).commit();
			}
			else
			{
				ProfilePreferencesFragment fragment = (ProfilePreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.editor_profile_detail_container);
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
					.replace(R.id.editor_profile_detail_container, fragment).commit();
		}
	}

	public void onRedrawListFragment() {
		// redraw headeru list fragmentu, notifikacie a widgetov
		EditorProfileListFragment fragment = (EditorProfileListFragment)getSupportFragmentManager().findFragmentById(R.id.editor_profile_list);
		if (fragment != null)
		{
			fragment.updateListView();
			Profile profile = GUIData.profilesDataWrapper.getActivatedProfile();
			fragment.updateHeader(profile);
			fragment.getActivateProfileHelper().showNotification(profile);
			fragment.getActivateProfileHelper().updateWidget();
			
			// send message into service
	        bindService(new Intent(this, PhoneProfilesService.class), serviceConnection, Context.BIND_AUTO_CREATE);
		}
	}

	public void onFinishProfilePreferencesActionMode() {
		if (mTwoPane) {
			ProfilePreferencesFragment fragment = (ProfilePreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.editor_profile_detail_container);
			if (fragment != null)
				fragment.finishActionMode();
		}
	}
	
	public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
		return;
	}
	
	public void onProfileOrderChanged() {
		// send message into service
        bindService(new Intent(this, PhoneProfilesService.class), serviceConnection, Context.BIND_AUTO_CREATE);
	}

	public void onProfileCountChanged() {
		// send message into service
        bindService(new Intent(this, PhoneProfilesService.class), serviceConnection, Context.BIND_AUTO_CREATE);
	}
	

	public void finishProfilePreferencesActionMode()
	{
		
	}
	
	public static void updateListView(SherlockFragmentActivity activity)
	{
		EditorProfileListFragment fragment = (EditorProfileListFragment)activity.getSupportFragmentManager().findFragmentById(R.id.editor_profile_list);
		if (fragment != null)
		{
			Log.d("EditorProfileActivity.updateListView","");
			fragment.updateListView();
		}
	}
	
	public static ApplicationsCache getApplicationsCache()
	{
		return applicationsCache;
	}

}
