package sk.henrichg.phoneprofiles;
 
import sk.henrichg.phoneprofiles.PreferenceListFragment.OnPreferenceAttachedListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceScreen;
 
public class ProfilePreferencesFragmentActivity extends SherlockFragmentActivity
												implements OnPreferenceAttachedListener
{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		// must by called before super.onCreate() for PreferenceActivity
		EditorProfilesActivity.setTheme(this, false); // must by called before super.onCreate()
		EditorProfilesActivity.setLanguage(getBaseContext());

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

	public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
	}
	
}