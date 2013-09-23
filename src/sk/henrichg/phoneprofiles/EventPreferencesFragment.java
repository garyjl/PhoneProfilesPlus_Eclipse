package sk.henrichg.phoneprofiles;
 
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
 
public class EventPreferencesFragment extends PreferenceListFragment 
										implements SharedPreferences.OnSharedPreferenceChangeListener
{
	
	private Event event;
	private int event_position;
	private PreferenceManager prefMng;
	private SharedPreferences preferences;
	private Context context;
	private ActionMode actionMode;
	private Callback actionModeCallback;
	
	private boolean restart; 
	
	private static Activity preferencesActivity = null;
		
	static final String PREFS_NAME = "event_preferences";
	
	private OnRestartEventPreferences onRestartEventPreferencesCallback = sDummyOnRestartEventPreferencesCallback;
	private OnRedrawEventListFragment onRedrawEventListFragmentCallback = sDummyOnRedrawEventListFragmentCallback;

	public interface OnRestartEventPreferences {
		/**
		 * Callback for restart fragment.
		 */
		public void onRestartEventPreferences(int position);
	}

	private static OnRestartEventPreferences sDummyOnRestartEventPreferencesCallback = new OnRestartEventPreferences() {
		public void onRestartEventPreferences(int position) {
		}
	};
	
	public interface OnRedrawEventListFragment {
		/**
		 * Callback for redraw event list fragment.
		 */
		public void onRedrawEventListFragment();
	}

	private static OnRedrawEventListFragment sDummyOnRedrawEventListFragmentCallback = new OnRedrawEventListFragment() {
		public void onRedrawEventListFragment() {
		}
	};
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (!(activity instanceof OnRestartEventPreferences)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		onRestartEventPreferencesCallback = (OnRestartEventPreferences) activity;
		
		if (!(activity instanceof OnRedrawEventListFragment)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		onRedrawEventListFragmentCallback = (OnRedrawEventListFragment) activity;
		
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		onRestartEventPreferencesCallback = sDummyOnRestartEventPreferencesCallback;
		onRedrawEventListFragmentCallback = sDummyOnRedrawEventListFragmentCallback;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		preferencesActivity = getSherlockActivity();
		
        // getting attached fragment data
		if (getArguments().containsKey(GlobalData.EXTRA_EVENT_POSITION))
			event_position = getArguments().getInt(GlobalData.EXTRA_EVENT_POSITION);
    	Log.e("EventPreferencesFragment.onCreate", "event_position=" + event_position);
		
        context = getSherlockActivity().getBaseContext();

    	event = (Event)EditorProfilesActivity.profilesDataWrapper.getEventList().get(event_position);
		
		prefMng = getPreferenceManager();
		prefMng.setSharedPreferencesName(PREFS_NAME);
		prefMng.setSharedPreferencesMode(Activity.MODE_PRIVATE);

        loadPreferences();
		
    	// get preference resource id from EventPreference
		addPreferencesFromResource(event._eventPreferences._preferencesResourceID);

        preferences = prefMng.getSharedPreferences();
        
        preferences.registerOnSharedPreferenceChangeListener(this);  
        
        createActionMode();
        

    	//Log.d("EventPreferencesFragment.onCreate", "xxxx");
    }
	
	@Override
	public void onStart()
	{
		super.onStart();

		updateSharedPreference();

		//Log.e("EventPreferencesFragment.onStart",String.valueOf(event._typeOld));
		// _typeOld is set, event type changed
		// _typeOld si reset in Event.saveSharedPrefereces() and Event.undoEventType()
		if (event._typeOld != 0)
			showActionMode();
		
    	//Log.d("EventPreferencesFragment.onStart", preferences.getString(PREF_EVENT_NAME, ""));

	}
	
	@Override
	public void onPause()
	{
		super.onPause();

		if (actionMode != null)
		{
			restart = false; // nerestartovat fragment
			actionMode.finish();
		}
		
    	//Log.d("EventPreferencesFragment.onPause", "xxxx");
		
	}
	
	@Override
	public void onDestroy()
	{
        preferences.unregisterOnSharedPreferenceChangeListener(this);        
		super.onDestroy();
	}

	public void doOnActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		doOnActivityResult(requestCode, resultCode, data);
	}
	
	private void loadPreferences()
	{
    	if (event != null)
    	{
	    	SharedPreferences preferences = getSherlockActivity().getSharedPreferences(EventPreferencesFragment.PREFS_NAME, Activity.MODE_PRIVATE);
	    	event.loadSharedPrefereces(preferences);
    	}
		
	}
	
	private void savePreferences()
	{
        if (event_position > -1) 
        {
        	event.saveSharedPrefereces(preferences);
        	
			EditorProfilesActivity.profilesDataWrapper.getDatabaseHandler().updateEvent(event);
        	
        	//Log.d("EventPreferencesFragment.onPause", "updateEvent");

        }

        onRedrawEventListFragmentCallback.onRedrawEventListFragment();
	}
	
	private void updateSharedPreference()
	{
        if (event_position > -1) 
        {	

	    	// updating activity with selected event preferences
	    	
        	//Log.d("EventPreferencesActivity.updateSharedPreference", event.getName());
    		event.setAllSummary(prefMng, context);
			
        }
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		//eventTypeChanged = false;
		
		if (key.equals(Event.PREF_EVENT_TYPE))
		{
			// event type changed
			// change event._eventpreferences
			String sEventType = sharedPreferences.getString(key, "");
			int iEventType;
			try {
				iEventType = Integer.parseInt(sEventType);
			} catch (Exception e) {
				iEventType = 1;
			}
			
			event.changeEventType(iEventType);
     	    onRestartEventPreferencesCallback.onRestartEventPreferences(event_position);
			
		}

		event.setSummary(prefMng, key, sharedPreferences, context);
		
		showActionMode();
	}
	
	private void createActionMode()
	{
		actionModeCallback = new ActionMode.Callback() {
			 
            /** Invoked whenever the action mode is shown. This is invoked immediately after onCreateActionMode */
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
 
            /** Called when user exits action mode */
            public void onDestroyActionMode(ActionMode mode) {
               actionMode = null;
               if (restart)
               {
            	   event.undoEventType();
            	   onRestartEventPreferencesCallback.onRestartEventPreferences(event_position);
               }   
            }
 
            /** This is called when the action mode is created. This is called by startActionMode() */
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                //mode.setTitle(R.string.phone_preferences_actionmode_title);
                //getSherlockActivity().getSupportMenuInflater().inflate(R.menu.context_menu, menu);
                return true;
            }
 
            /** This is called when an item in the context menu is selected */
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            /*    switch(item.getItemId()){
                    case R.id.action1:
                        Toast.makeText(getBaseContext(), "Selected Action1 ", Toast.LENGTH_LONG).show();
                        mode.finish();  // Automatically exists the action mode, when the user selects this action
                        break;
                    case R.id.action2:
                        Toast.makeText(getBaseContext(), "Selected Action2 ", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.action3:
                        Toast.makeText(getBaseContext(), "Selected Action3 ", Toast.LENGTH_LONG).show();
                        break;
                }  */
                return false;
            }

        };		
	}
	
	public void showActionMode()
	{
        if (actionMode == null)
        {
        	
        	restart = true;
        	
        	LayoutInflater inflater = LayoutInflater.from(getSherlockActivity());
        	View actionView = inflater.inflate(R.layout.event_preferences_action_mode, null);

            actionMode = getSherlockActivity().startActionMode(actionModeCallback);
            actionMode.setCustomView(actionView); 
            
            actionMode.getCustomView().findViewById(R.id.event_preferences_action_menu_cancel).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					//Log.d("actionMode.onClick", "cancel");
					
					actionMode.finish();
					
				}
           	});

            actionMode.getCustomView().findViewById(R.id.event_preferences_action_menu_save).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					//Log.d("actionMode.onClick", "save");

					savePreferences();
					
					restart = false; // nerestartovat fragment
					actionMode.finish();
					
				}
           	});
        }
		
	}
	
	public void finishActionMode()
	{
		if (actionMode != null)
		{	
			restart = true;
			actionMode.finish();
		}
	}

	static public Activity getPreferencesActivity()
	{
		return preferencesActivity;
	}
	
}