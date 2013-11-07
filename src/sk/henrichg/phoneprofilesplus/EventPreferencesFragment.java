package sk.henrichg.phoneprofilesplus;
 
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
 
public class EventPreferencesFragment extends PreferenceListFragment 
										implements SharedPreferences.OnSharedPreferenceChangeListener
{
	
	private Event event;
	public long event_id;
	//private boolean first_start_activity;
	private int new_event_mode;
	public boolean eventNonEdited = true;
	private PreferenceManager prefMng;
	private SharedPreferences preferences;
	private Context context;
	private ActionMode actionMode;
	private Callback actionModeCallback;
	
	private int actionModeButtonClicked = BUTTON_UNDEFINED;
	
	private static Activity preferencesActivity = null;
		
	static final String PREFS_NAME_ACTIVITY = "event_preferences_activity";
	static final String PREFS_NAME_FRAGMENT = "event_preferences_fragment";
	private String PREFS_NAME;
	
	static final int BUTTON_UNDEFINED = 0;
	static final int BUTTON_CANCEL = 1;
	static final int BUTTON_SAVE = 2;
	
	private OnRestartEventPreferences onRestartEventPreferencesCallback = sDummyOnRestartEventPreferencesCallback;
	private OnRedrawEventListFragment onRedrawEventListFragmentCallback = sDummyOnRedrawEventListFragmentCallback;

	// invokes when restart of event preferences fragment needed (undo preference changes)
	public interface OnRestartEventPreferences {
		/**
		 * Callback for restart fragment.
		 */
		public void onRestartEventPreferences(Event event, int newEventMode);
	}

	private static OnRestartEventPreferences sDummyOnRestartEventPreferencesCallback = new OnRestartEventPreferences() {
		public void onRestartEventPreferences(Event event, int newEventMode) {
		}
	};
	
	// invokes when event list fragment redraw needed (preference changes accepted)
	public interface OnRedrawEventListFragment {
		/**
		 * Callback for redraw event list fragment.
		 */
		public void onRedrawEventListFragment(Event event, int newEventMode);
	}

	private static OnRedrawEventListFragment sDummyOnRedrawEventListFragmentCallback = new OnRedrawEventListFragment() {
		public void onRedrawEventListFragment(Event event, int newEventMode) {
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

		prefMng = getPreferenceManager();
		prefMng.setSharedPreferencesName(PREFS_NAME);
		prefMng.setSharedPreferencesMode(Activity.MODE_PRIVATE);
		
        // getting attached fragment data
		if (getArguments().containsKey(GlobalData.EXTRA_NEW_EVENT_MODE))
			new_event_mode = getArguments().getInt(GlobalData.EXTRA_NEW_EVENT_MODE);
		if (getArguments().containsKey(GlobalData.EXTRA_EVENT_ID))
			event_id = getArguments().getLong(GlobalData.EXTRA_EVENT_ID);
    	//Log.e("EventPreferencesFragment.onCreate", "event_position=" + event_position);
		if (new_event_mode == EditorEventListFragment.EDIT_MODE_INSERT)
		{
			// create new event
			event = new Event(getResources().getString(R.string.event_name_default), 
						Event.ETYPE_TIME, 
						0,
						Event.ESTATUS_STOP
		         );
			event_id = 0;
		}
		else
		if (new_event_mode == EditorEventListFragment.EDIT_MODE_DUPLICATE)
		{
			// duplicate event
			Event origEvent = (Event)EditorProfilesActivity.dataWrapper.getEventById(event_id);
			event = new Event(
						   origEvent._name+"_d", 
						   origEvent._type, 
						   origEvent._fkProfile, 
						   origEvent._status
							);
			event.copyEventPreferences(origEvent);
			event_id = 0;
		}
		else
			event = (Event)EditorProfilesActivity.dataWrapper.getEventById(event_id);
		/*
		if (getArguments().containsKey(GlobalData.EXTRA_FIRST_START_ACTIVITY))
		{
			first_start_activity = getArguments().getBoolean(GlobalData.EXTRA_FIRST_START_ACTIVITY);
	        getArguments().remove(GlobalData.EXTRA_FIRST_START_ACTIVITY);
		}
		else
			first_start_activity = false;
        if (first_start_activity)
        */
		if (savedInstanceState == null)
        	loadPreferences();
   	
        context = getSherlockActivity().getBaseContext();
		
    	// get preference resource id from EventPreference
		addPreferencesFromResource(event._eventPreferences._preferencesResourceID);

        preferences = prefMng.getSharedPreferences();
        
        preferences.registerOnSharedPreferenceChangeListener(this);  
        
        createActionModeCallback();
        
        if ((savedInstanceState != null) && savedInstanceState.getBoolean("action_mode_showed", false))
            showActionMode();
        else
        if (((new_event_mode == EditorEventListFragment.EDIT_MODE_INSERT) ||
             (new_event_mode == EditorEventListFragment.EDIT_MODE_DUPLICATE))
           	&& (savedInstanceState == null))
        	showActionMode();

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

		/*
		if (actionMode != null)
		{
			restart = false; // nerestartovat fragment
			actionMode.finish();
		}
		*/
		
    	//Log.d("EventPreferencesFragment.onPause", "xxxx");
		
	}
	
	@Override
	public void onDestroy()
	{
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        event = null;
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
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        outState.putBoolean("action_mode_showed", (actionMode != null));
	}	
	
	private void loadPreferences()
	{
    	if (event != null)
    	{
	    	SharedPreferences preferences = getSherlockActivity().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
	    	event.loadSharedPrefereces(preferences);
    	}
		
	}
	
	private void savePreferences()
	{
    	event.saveSharedPrefereces(preferences);

		if ((new_event_mode == EditorEventListFragment.EDIT_MODE_INSERT) ||
		    (new_event_mode == EditorEventListFragment.EDIT_MODE_DUPLICATE))
		{
			// add event into DB
			EditorProfilesActivity.dataWrapper.getDatabaseHandler().addEvent(event);
			event_id = event._id;

        	//Log.d("ProfilePreferencesFragment.savePreferences", "addEvent");
			
		}
		else
    	if (event_id > 0) 
        {
        	// udate event in DB
			EditorProfilesActivity.dataWrapper.getDatabaseHandler().updateEvent(event);
        	
        	//Log.d("EventPreferencesFragment.savePreferences", "updateEvent");

        }

        onRedrawEventListFragmentCallback.onRedrawEventListFragment(event, new_event_mode);
	}
	
	private void updateSharedPreference()
	{
        if (event != null) 
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
     	    onRestartEventPreferencesCallback.onRestartEventPreferences(event, new_event_mode);
			
		}

		event.setSummary(prefMng, key, sharedPreferences, context);
		
    	Activity activity = getSherlockActivity();
    	boolean canShow = (EditorProfilesActivity.mTwoPane) && (activity instanceof EditorProfilesActivity);
    	canShow = canShow || ((!EditorProfilesActivity.mTwoPane) && (activity instanceof EventPreferencesFragmentActivity));
    	if (canShow)
    		showActionMode();
	}
	
	private void createActionModeCallback()
	{
		actionModeCallback = new ActionMode.Callback() {
			 
            /** Invoked whenever the action mode is shown. This is invoked immediately after onCreateActionMode */
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
 
            /** Called when user exits action mode */
            public void onDestroyActionMode(ActionMode mode) {
                 actionMode = null;
                 if (actionModeButtonClicked == BUTTON_CANCEL)
            	     // cancel button clicked
                 {
            	     event.undoEventType();
            	     onRestartEventPreferencesCallback.onRestartEventPreferences(event, new_event_mode);
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
	
	private void showActionMode()
	{
		eventNonEdited = false;
		
        if (actionMode == null)
        {
        	
        	actionModeButtonClicked = BUTTON_UNDEFINED;
        	
        	LayoutInflater inflater = LayoutInflater.from(getSherlockActivity());
        	View actionView = inflater.inflate(R.layout.event_preferences_action_mode, null);

            actionMode = getSherlockActivity().startActionMode(actionModeCallback);
            actionMode.setCustomView(actionView); 
            
            actionMode.getCustomView().findViewById(R.id.event_preferences_action_menu_cancel).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					//Log.d("actionMode.onClick", "cancel");
					
					finishActionMode(BUTTON_CANCEL);
					
				}
           	});

            actionMode.getCustomView().findViewById(R.id.event_preferences_action_menu_save).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					//Log.d("actionMode.onClick", "save");

					savePreferences();
					
					finishActionMode(BUTTON_SAVE);
					
				}
           	});
        }
		
	}
	
	public boolean isActionModeActive()
	{
		return (actionMode != null);
	}
	
	public void finishActionMode(int button)
	{
		int _button = button;
		
		if (_button == BUTTON_SAVE)
			new_event_mode = EditorEventListFragment.EDIT_MODE_UNDEFINED;
		
		if (getSherlockActivity() instanceof EventPreferencesFragmentActivity)
		{
			actionModeButtonClicked = BUTTON_UNDEFINED;
			getSherlockActivity().finish(); // finish activity;
		}
		else
		if (actionMode != null)
		{	
			actionModeButtonClicked = _button;
			actionMode.finish();
		}
	}

	static public Activity getPreferencesActivity()
	{
		return preferencesActivity;
	}
	
}