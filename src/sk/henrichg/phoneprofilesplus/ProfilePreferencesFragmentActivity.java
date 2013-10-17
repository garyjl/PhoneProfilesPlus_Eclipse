package sk.henrichg.phoneprofilesplus;
 
import sk.henrichg.phoneprofilesplus.PreferenceListFragment.OnPreferenceAttachedListener;
import sk.henrichg.phoneprofilesplus.ProfilePreferencesFragment.OnRedrawProfileListFragment;
import sk.henrichg.phoneprofilesplus.ProfilePreferencesFragment.OnRestartProfilePreferences;

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
	private long profile_id = 0;
	
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

        profile_id = getIntent().getLongExtra(GlobalData.EXTRA_PROFILE_ID, -1);

        //Log.e("ProfilePreferencesFragmentActivity.onCreate","profile_id="+profile_id);
        
		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			arguments.putLong(GlobalData.EXTRA_PROFILE_ID, profile_id);
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
	public void finish() {
		
		//Log.e("ProfilePreferencesFragmentActivity.finish","xxx");

		// for startActivityForResult
		Intent returnIntent = new Intent();
		returnIntent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile_id);
		setResult(RESULT_OK,returnIntent);

	    super.finish();
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

	public void onRestartProfilePreferences(Profile profile) {
		Bundle arguments = new Bundle();
		arguments.putLong(GlobalData.EXTRA_PROFILE_ID, profile._id);
		ProfilePreferencesFragment fragment = new ProfilePreferencesFragment();
		fragment.setArguments(arguments);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.activity_profile_preferences_container, fragment).commit();
	}

	public void onRedrawProfileListFragment(Profile profile) {
		// all redraws are in EditorProfilesActivity.onActivityResult()
	}
	
	public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
	}

}
