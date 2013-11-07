package sk.henrichg.phoneprofilesplus;
 
import sk.henrichg.phoneprofilesplus.PreferenceListFragment.OnPreferenceAttachedListener;
import sk.henrichg.phoneprofilesplus.EventPreferencesFragment.OnRedrawEventListFragment;
import sk.henrichg.phoneprofilesplus.EventPreferencesFragment.OnRestartEventPreferences;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
 
public class EventPreferencesFragmentActivity extends SherlockFragmentActivity
												implements OnPreferenceAttachedListener,
	                                                       OnRestartEventPreferences,
	                                                       OnRedrawEventListFragment
{
	
	private long event_id = 0; 
	int newEventMode = EditorEventListFragment.EDIT_MODE_UNDEFINED;
			
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

        event_id = getIntent().getLongExtra(GlobalData.EXTRA_EVENT_ID, 0);
        //boolean first_start_activity = getIntent().getBooleanExtra(GlobalData.EXTRA_FIRST_START_ACTIVITY, false);
        //getIntent().removeExtra(GlobalData.EXTRA_FIRST_START_ACTIVITY);
        newEventMode = getIntent().getIntExtra(GlobalData.EXTRA_NEW_EVENT_MODE, EditorEventListFragment.EDIT_MODE_UNDEFINED);

		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			arguments.putLong(GlobalData.EXTRA_EVENT_ID, event_id);
			//arguments.putBoolean(GlobalData.EXTRA_FIRST_START_ACTIVITY, first_start_activity);
			arguments.putInt(GlobalData.EXTRA_NEW_EVENT_MODE, newEventMode);
			arguments.putBoolean(GlobalData.EXTRA_PREFERENCES_ACTIVITY, true);
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
	public void finish() {
		
		//Log.e("EventPreferencesFragmentActivity.finish","xxx");

		EventPreferencesFragment fragment = (EventPreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.activity_event_preferences_container);
		if (fragment != null)
			event_id = fragment.event_id;
		
		// for startActivityForResult
		Intent returnIntent = new Intent();
		returnIntent.putExtra(GlobalData.EXTRA_EVENT_ID, event_id);
		returnIntent.putExtra(GlobalData.EXTRA_NEW_EVENT_MODE, newEventMode);
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
		GUIData.reloadActivity(this);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		EventPreferencesFragment fragment = (EventPreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.activity_event_preferences_container);
		if (fragment != null)
			fragment.doOnActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            // handle your back button code here
        	EventPreferencesFragment fragment = (EventPreferencesFragment)getSupportFragmentManager().findFragmentById(R.id.activity_event_preferences_container);
    		if ((fragment != null) && (fragment.isActionModeActive()))
    		{
    			fragment.finishActionMode(EventPreferencesFragment.BUTTON_CANCEL);
	            return true; // consumes the back key event - ActionMode is not finished
    		}
    		else
    		    return super.dispatchKeyEvent(event);
        }
	    return super.dispatchKeyEvent(event);
	}

	public void onRestartEventPreferences(Event event, int newEventMode) {
		if ((newEventMode != EditorEventListFragment.EDIT_MODE_INSERT) &&
		    (newEventMode != EditorEventListFragment.EDIT_MODE_DUPLICATE))
		{
			Bundle arguments = new Bundle();
			arguments.putLong(GlobalData.EXTRA_EVENT_ID, event._id);
			//arguments.putBoolean(GlobalData.EXTRA_FIRST_START_ACTIVITY, true);
			arguments.putInt(GlobalData.EXTRA_NEW_EVENT_MODE, newEventMode);
			arguments.putBoolean(GlobalData.EXTRA_PREFERENCES_ACTIVITY, true);
			EventPreferencesFragment fragment = new EventPreferencesFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.activity_event_preferences_container, fragment).commit();
		}
		else
		{
			Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_event_preferences_container);
			if (fragment != null)
			{
				getSupportFragmentManager().beginTransaction()
					.remove(fragment).commit();
			}
		}
	}

	public void onRedrawEventListFragment(Event event, int newEventMode) {
		// all redraws are in EditorProfilesActivity.onActivityResult()
	}
	
	public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
	}

}