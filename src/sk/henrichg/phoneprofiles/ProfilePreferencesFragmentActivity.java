package sk.henrichg.phoneprofiles;
 
import sk.henrichg.phoneprofiles.PreferenceListFragment.OnPreferenceAttachedListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceScreen;
 
public class ProfilePreferencesFragmentActivity extends SherlockFragmentActivity
												implements OnPreferenceAttachedListener /*, 
												           OnPreferenceChangeListener */
{
	
	private SharedPreferences preferences;
	private OnSharedPreferenceChangeListener prefListener;

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
			ProfilePreferencesFragment fragment = new ProfilePreferencesFragment(R.xml.profile_preferences);
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.activity_profile_preferences_container, fragment).commit();
		}
		
		preferences = getSharedPreferences(ProfilePreferencesFragment.PREFS_NAME, MODE_PRIVATE);		
		
        prefListener = new OnSharedPreferenceChangeListener() {
        	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

    	    	// updating activity with selected profile preferences
    	    	//Log.d("ProfilePreferencesFragmentActivity.onSharedPreferenceChanged", key);
        		
        		ProfilePreferencesFragment fragment = (ProfilePreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.activity_profile_preferences_container);
        		if (fragment != null)
        			fragment.preferenceChanged(prefs, key);
        		
        	}
        };
        
        preferences.registerOnSharedPreferenceChangeListener(prefListener);

		
    	//Log.d("ProfilePreferencesFragmentActivity.onCreate", "xxxx");
		
    }

	@Override
	protected void onDestroy()
	{
    	//Log.d("ProfilePreferencesFragmentActivity.onDestroy", "xxxx");
    	preferences.unregisterOnSharedPreferenceChangeListener(prefListener);
		super.onDestroy();
	}
	
/*	
	public void onPreferenceAttached(PreferenceScreen root, int xmlId){
        if(root == null)
           return; //for whatever reason in very rare cases this is null
        root.findPreference("somePreference").setOnPreferenceChangeListener(this);
    }
    
    //handle your preferenceChanged events here (if needed)
    public boolean onPreferenceChange(Preference preference, Object newValue) {
		ProfilePreferencesFragment fragment = (ProfilePreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.activity_profile_preferences_container);
		if (fragment != null)
			fragment.preferenceChanged(preference, newValue);
    	
        return true;
    }
*/
	
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
		return;
	}

}