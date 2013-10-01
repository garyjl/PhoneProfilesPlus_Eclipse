package sk.henrichg.phoneprofiles;
 
import sk.henrichg.phoneprofiles.PreferenceListFragment.OnPreferenceAttachedListener;
import sk.henrichg.phoneprofiles.ProfilePreferencesFragment.OnRedrawProfileListFragment;
import sk.henrichg.phoneprofiles.ProfilePreferencesFragment.OnRestartProfilePreferences;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.util.Log;
 
public class ProfilePreferencesFragmentActivity extends SherlockFragmentActivity
												implements OnPreferenceAttachedListener,
	                                                       OnRestartProfilePreferences,
	                                                       OnRedrawProfileListFragment
{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		// must by called before super.onCreate() for PreferenceActivity
		GUIData.setTheme(this, false); // must by called before super.onCreate()
		GUIData.setLanguage(getBaseContext());

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_profile_preferences);


		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.title_activity_profile_preferences);

        long profile_id = getIntent().getLongExtra(GlobalData.EXTRA_PROFILE_ID, -1);
        int filter_type = getIntent().getIntExtra(GlobalData.EXTRA_FILTER_TYPE, EditorProfileListFragment.FILTER_TYPE_ALL);

        //Log.e("ProfilePreferencesFragmentActivity.onCreate","profile_id="+profile_id);
        
		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			arguments.putLong(GlobalData.EXTRA_PROFILE_ID, profile_id);
			arguments.putInt(GlobalData.EXTRA_FILTER_TYPE, filter_type);
			ProfilePreferencesFragment fragment = new ProfilePreferencesFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.activity_profile_preferences_container, fragment).commit();
		}
		
    	//Log.d("ProfilePreferencesFragmentActivity.onCreate", "xxxx");
		
    }
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		ProfilePreferencesFragment fragment = (ProfilePreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.activity_profile_preferences_container);
		if (fragment != null)
			fragment.doOnActivityResult(requestCode, resultCode, data);
	}

	public void onRestartProfilePreferences(Profile profile, int filterType) {
		Bundle arguments = new Bundle();
		arguments.putLong(GlobalData.EXTRA_PROFILE_ID, profile._id);
		arguments.putInt(GlobalData.EXTRA_FILTER_TYPE, filterType);
		ProfilePreferencesFragment fragment = new ProfilePreferencesFragment();
		fragment.setArguments(arguments);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.activity_profile_preferences_container, fragment).commit();
	}

	public void onRedrawProfileListFragment(Profile profile) {
		// all redraws are in EditorProfilesActivity.onStart()

		// send message into service
        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
		EditorProfilesActivity.serviceCommunication.sendMessageIntoService(PhoneProfilesService.MSG_RELOAD_DATA);
	}
	
	public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
	}

}