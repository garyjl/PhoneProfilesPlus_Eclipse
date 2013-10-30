package sk.henrichg.phoneprofilesplus;
 
import sk.henrichg.phoneprofilesplus.ProfilePreferencesFragment.OnDeleteNewNonEditedProfile;
import sk.henrichg.phoneprofilesplus.PreferenceListFragment.OnPreferenceAttachedListener;
import sk.henrichg.phoneprofilesplus.ProfilePreferencesFragment.OnRedrawProfileListFragment;
import sk.henrichg.phoneprofilesplus.ProfilePreferencesFragment.OnRestartProfilePreferences;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.KeyEvent;
 
public class ProfilePreferencesFragmentActivity extends SherlockFragmentActivity
												implements OnPreferenceAttachedListener,
	                                                       OnRestartProfilePreferences,
	                                                       OnRedrawProfileListFragment,
	                                                       OnDeleteNewNonEditedProfile
{
	private long profile_id = 0;
	boolean newProfile = false;
	boolean deleteNewNoneditedProfile = false;
	
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
        boolean first_start_activity = getIntent().getBooleanExtra(GlobalData.EXTRA_FIRST_START_ACTIVITY, false);
        getIntent().removeExtra(GlobalData.EXTRA_FIRST_START_ACTIVITY);
        newProfile = getIntent().getBooleanExtra(GlobalData.EXTRA_NEW_PROFILE, false);

        //Log.e("ProfilePreferencesFragmentActivity.onCreate","profile_id="+profile_id);
        
		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			arguments.putLong(GlobalData.EXTRA_PROFILE_ID, profile_id);
			arguments.putBoolean(GlobalData.EXTRA_FIRST_START_ACTIVITY, first_start_activity);
			arguments.putBoolean(GlobalData.EXTRA_NEW_PROFILE, newProfile);
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
		if (deleteNewNoneditedProfile)
			setResult(RESULT_CANCELED,returnIntent);
		else
		{
			ProfilePreferencesFragment fragment = (ProfilePreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.activity_profile_preferences_container);

			Log.e("ProfilePreferencesFragmentActivity.finish","fragment="+fragment);
			if (fragment != null)
				Log.e("ProfilePreferencesFragmentActivity.finish","fragment.profileNonEdited="+fragment.profileNonEdited);
			Log.e("ProfilePreferencesFragmentActivity.finish","newProfile="+newProfile);
			
			if ((fragment != null) && fragment.profileNonEdited && newProfile)
				setResult(RESULT_CANCELED,returnIntent);
			else
				setResult(RESULT_OK,returnIntent);
		}

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
	
	/*
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
		Intent intent = getIntent();
		startActivity(intent);
		finish();
	}
	*/
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		ProfilePreferencesFragment fragment = (ProfilePreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.activity_profile_preferences_container);
		if (fragment != null)
			fragment.doOnActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            // handle your back button code here
        	ProfilePreferencesFragment fragment = (ProfilePreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.activity_profile_preferences_container);
    		if ((fragment != null) && (fragment.isActionModeActive()))
    		{
    			fragment.finishActionMode(ProfilePreferencesFragment.BUTTON_CANCEL);
	            return true; // consumes the back key event - ActionMode is not finished
    		}
    		else
    		    return super.dispatchKeyEvent(event);
        }
	    return super.dispatchKeyEvent(event);
	}
	
	public void onRestartProfilePreferences(Profile profile) {
		Bundle arguments = new Bundle();
		arguments.putLong(GlobalData.EXTRA_PROFILE_ID, profile._id);
		arguments.putBoolean(GlobalData.EXTRA_FIRST_START_ACTIVITY, true);
		arguments.putBoolean(GlobalData.EXTRA_NEW_PROFILE, newProfile);
		ProfilePreferencesFragment fragment = new ProfilePreferencesFragment();
		fragment.setArguments(arguments);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.activity_profile_preferences_container, fragment).commit();
	}

	public void onRedrawProfileListFragment(Profile profile) {
		// all redraws are in EditorProfilesActivity.onActivityResult()
	}
	
	public void onDeleteNewNonEditedProfile(Profile profile) {
		// delete are in EditorProfilesActivity.onActivityResult()
		deleteNewNoneditedProfile = true;
	}
	
	public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
	}

}
