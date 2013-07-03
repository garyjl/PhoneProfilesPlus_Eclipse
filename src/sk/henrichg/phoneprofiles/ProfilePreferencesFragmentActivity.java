package sk.henrichg.phoneprofiles;
 
import sk.henrichg.phoneprofiles.PreferenceListFragment.OnPreferenceAttachedListener;
import sk.henrichg.phoneprofiles.ProfilePreferencesFragment.OnRedrawListFragment;
import sk.henrichg.phoneprofiles.ProfilePreferencesFragment.OnRestartProfilePreferences;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceScreen;
 
public class ProfilePreferencesFragmentActivity extends SherlockFragmentActivity
												implements OnPreferenceAttachedListener,
	                                                       OnRestartProfilePreferences,
	                                                       OnRedrawListFragment
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

        int profile_position = getIntent().getIntExtra(GlobalData.EXTRA_PROFILE_POSITION, -1);

		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			arguments.putInt(GlobalData.EXTRA_PROFILE_POSITION, profile_position);
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

	public void onRestartProfilePreferences(int position) {
		Bundle arguments = new Bundle();
		arguments.putInt(GlobalData.EXTRA_PROFILE_POSITION, position);
		ProfilePreferencesFragment fragment = new ProfilePreferencesFragment();
		fragment.setArguments(arguments);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.activity_profile_preferences_container, fragment).commit();
	}

	public void onRedrawListFragment() {
		// all redraws are in EditorProfilesActivity.onStart()

		// send message into service
        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
		ActivateProfileActivity.profilesDataWrapper.sendMessageIntoService(PhoneProfilesService.MSG_RELOAD_DATA);
	}
	
	public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
	}

}