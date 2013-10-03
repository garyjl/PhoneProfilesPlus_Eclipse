package sk.henrichg.phoneprofiles;
 
import sk.henrichg.phoneprofiles.PreferenceListFragment.OnPreferenceAttachedListener;
import sk.henrichg.phoneprofiles.EventPreferencesFragment.OnRedrawEventListFragment;
import sk.henrichg.phoneprofiles.EventPreferencesFragment.OnRestartEventPreferences;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceScreen;
 
public class EventPreferencesFragmentActivity extends SherlockFragmentActivity
												implements OnPreferenceAttachedListener,
	                                                       OnRestartEventPreferences,
	                                                       OnRedrawEventListFragment
{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		// must by called before super.onCreate() for PreferenceActivity
		GUIData.setTheme(this, false); // must by called before super.onCreate()
		GUIData.setLanguage(getBaseContext());

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_event_preferences);


		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.title_activity_event_preferences);

        long event_id = getIntent().getLongExtra(GlobalData.EXTRA_EVENT_ID, -1);

		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			arguments.putLong(GlobalData.EXTRA_EVENT_ID, event_id);
			EventPreferencesFragment fragment = new EventPreferencesFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.activity_event_preferences_container, fragment).commit();
		}
		
    	//Log.d("EventPreferencesFragmentActivity.onCreate", "xxxx");
		
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
		EventPreferencesFragment fragment = (EventPreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.activity_event_preferences_container);
		if (fragment != null)
			fragment.doOnActivityResult(requestCode, resultCode, data);
	}

	public void onRestartEventPreferences(Event event) {
		Bundle arguments = new Bundle();
		arguments.putLong(GlobalData.EXTRA_EVENT_ID, event._id);
		EventPreferencesFragment fragment = new EventPreferencesFragment();
		fragment.setArguments(arguments);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.activity_event_preferences_container, fragment).commit();
	}

	public void onRedrawEventListFragment(Event event) {
		// all redraws are in EditorProfilesActivity.onStart()

		// send message into service
        //bindService(new Intent(this, PhoneProfilesService.class), GUIData.profilesDataWrapper.serviceConnection, Context.BIND_AUTO_CREATE);
		EditorProfilesActivity.serviceCommunication.sendMessageIntoService(PhoneProfilesService.MSG_RELOAD_DATA);
	}
	
	public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
	}

}