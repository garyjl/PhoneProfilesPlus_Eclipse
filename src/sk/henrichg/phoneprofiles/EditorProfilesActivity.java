package sk.henrichg.phoneprofiles;

import sk.henrichg.phoneprofiles.EditorProfileListFragment.OnFinishProfilePreferencesActionMode;
import sk.henrichg.phoneprofiles.EditorProfileListFragment.OnProfileCountChanged;
import sk.henrichg.phoneprofiles.EditorProfileListFragment.OnProfileOrderChanged;
import sk.henrichg.phoneprofiles.EditorProfileListFragment.OnStartProfilePreferences;
import sk.henrichg.phoneprofiles.PreferenceListFragment.OnPreferenceAttachedListener;
import sk.henrichg.phoneprofiles.ProfilePreferencesFragment.OnRedrawListFragment;
import sk.henrichg.phoneprofiles.ProfilePreferencesFragment.OnRestartProfilePreferences;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceScreen;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		GUIData.setTheme(this, false);
		GUIData.setLanguage(getBaseContext());

		super.onCreate(savedInstanceState);
		
		applicationsCache = new ApplicationsCache();
		
		setContentView(R.layout.activity_editor_profile_list);
		
		if (findViewById(R.id.editor_profile_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			Profile profile = ActivateProfileActivity.profilesDataWrapper.getFirstProfile();
			
			//if (profile != null)
				onStartProfilePreferences(ActivateProfileActivity.profilesDataWrapper.getProfileItemPosition(profile), false);

		}
		
		
		//getSupportActionBar().setHomeButtonEnabled(true);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.title_activity_phone_profiles);

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
			ActivateProfileActivity.profilesDataWrapper.getActivateProfileHelper().showNotification(null);
			
			// zrusenie service
			GlobalData.stopService(getApplicationContext());
			
			finish();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
						int ret = ActivateProfileActivity.profilesDataWrapper.getDatabaseHandler().importDB();
						
						if (ret == 1)
						{
							// check for hardware capability and update data
							ret = ActivateProfileActivity.profilesDataWrapper.getDatabaseHandler().updateForHardware(GlobalData.context);
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
							ActivateProfileActivity.profilesDataWrapper.clearProfileList();
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
				return ActivateProfileActivity.profilesDataWrapper.getDatabaseHandler().exportDB();
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
			Profile profile = ActivateProfileActivity.profilesDataWrapper.getActivatedProfile();
			fragment.updateHeader(profile);
			ActivateProfileActivity.profilesDataWrapper.getActivateProfileHelper().showNotification(profile);
			ActivateProfileActivity.profilesDataWrapper.getActivateProfileHelper().updateWidget();
			
			// send message into service
	        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
			ActivateProfileActivity.profilesDataWrapper.sendMessageIntoService(PhoneProfilesService.MSG_RELOAD_DATA);
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
        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
		ActivateProfileActivity.profilesDataWrapper.sendMessageIntoService(PhoneProfilesService.MSG_RELOAD_DATA);
	}

	public void onProfileCountChanged() {
		// send message into service
        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
		ActivateProfileActivity.profilesDataWrapper.sendMessageIntoService(PhoneProfilesService.MSG_RELOAD_DATA);
	}
	

	public void finishProfilePreferencesActionMode()
	{
		
	}
	
	public static void updateListView(SherlockFragmentActivity activity)
	{
		EditorProfileListFragment fragment = (EditorProfileListFragment)activity.getSupportFragmentManager().findFragmentById(R.id.editor_profile_list);
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
