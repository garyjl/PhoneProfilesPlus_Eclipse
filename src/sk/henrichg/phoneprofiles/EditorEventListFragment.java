package sk.henrichg.phoneprofiles;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sk.henrichg.phoneprofiles.EditorProfileListFragment.AlphabeticallyComparator;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class EditorEventListFragment extends SherlockFragment {

	private List<Event> eventList;
	private EditorEventListAdapter eventListAdapter;
	private ListView listView;
	private DatabaseHandler databaseHandler;

	public static final String FILTER_TYPE_ARGUMENT = "filter_type";

	public static final int FILTER_TYPE_ALL = 0;
	public static final int FILTER_TYPE_RUNNING = 1;
	public static final int FILTER_TYPE_PAUSED = 2;
	public static final int FILTER_TYPE_STOPPED = 3;
	
	private int filterType = FILTER_TYPE_ALL;  
	
	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private OnStartEventPreferences onStartEventPreferencesCallback = sDummyOnStartEventPreferencesCallback;
	private OnFinishEventPreferencesActionMode onFinishEventPreferencesActionModeCallback = sDummyOnFinishEventPreferencesActionModeCallback;
	private OnEventCountChanged onEventCountChangedCallback = sDummyOnEventCountChangedCallback; 
	
	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface OnStartEventPreferences {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onStartEventPreferences(Event event, int filterType, boolean afterDelete);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static OnStartEventPreferences sDummyOnStartEventPreferencesCallback = new OnStartEventPreferences() {
		public void onStartEventPreferences(Event event, int filterType, boolean afterDelete) {
		}
	};
	
	public interface OnFinishEventPreferencesActionMode {
		public void onFinishEventPreferencesActionMode();
	}

	private static OnFinishEventPreferencesActionMode sDummyOnFinishEventPreferencesActionModeCallback = new OnFinishEventPreferencesActionMode() {
		public void onFinishEventPreferencesActionMode() {
		}
	};

	public interface OnEventCountChanged {
		public void onEventCountChanged();
	}

	private static OnEventCountChanged sDummyOnEventCountChangedCallback = new OnEventCountChanged() {
		public void onEventCountChanged() {
		}
	};

	public interface OnEventOrderChanged {
		public void onEventOrderChanged();
	}

	public EditorEventListFragment() {
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof OnStartEventPreferences)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		onStartEventPreferencesCallback = (OnStartEventPreferences) activity;

		if (!(activity instanceof OnEventCountChanged)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		onEventCountChangedCallback = (OnEventCountChanged) activity;
		
		
		if (activity instanceof OnFinishEventPreferencesActionMode)
			onFinishEventPreferencesActionModeCallback = (OnFinishEventPreferencesActionMode) activity;

	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		onStartEventPreferencesCallback = sDummyOnStartEventPreferencesCallback;
		onFinishEventPreferencesActionModeCallback = sDummyOnFinishEventPreferencesActionModeCallback;
		onEventCountChangedCallback = sDummyOnEventCountChangedCallback;
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
        filterType = getArguments() != null ? 
        		getArguments().getInt(FILTER_TYPE_ARGUMENT, EditorEventListFragment.FILTER_TYPE_ALL) : 
        			EditorEventListFragment.FILTER_TYPE_ALL;
        //Log.e("EditorEventListFragment.onCreate","filterType="+filterType);
		
		databaseHandler = EditorProfilesActivity.profilesDataWrapper.getDatabaseHandler();
		
		getSherlockActivity().getIntent();
		
		final EditorEventListFragment fragment = this;
		
		new AsyncTask<Void, Integer, Void>() {
			
			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				//updateHeader(null);
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				eventList = EditorProfilesActivity.profilesDataWrapper.getEventList();
				sortAlphabetically();
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result)
			{
				super.onPostExecute(result);

				if (listView != null)
				{
					eventListAdapter = new EditorEventListAdapter(fragment, EditorProfilesActivity.profilesDataWrapper, filterType);
					listView.setAdapter(eventListAdapter);
				}
				
				doOnStart();
				
			}
			
		}.execute();
		
		setHasOptionsMenu(true);

		//Log.d("EditorEventListFragment.onCreate", "xxxx");
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView;
		
		rootView = inflater.inflate(R.layout.editor_event_list, container, false); 

		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		// az tu mame layout, tak mozeme ziskat view-y
		listView = (ListView)getSherlockActivity().findViewById(R.id.editor_events_list);
		listView.setEmptyView(getSherlockActivity().findViewById(R.id.editor_events_list_empty));
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				//Log.d("EditorEventListFragment.onItemClick", "xxxx");

				startEventPreferencesActivity((Event)eventListAdapter.getItem(position));
				
			}
			
		}); 
		
		if (eventList != null)
		{
			if (eventListAdapter == null)
			{
				eventListAdapter = new EditorEventListAdapter(this, EditorProfilesActivity.profilesDataWrapper, filterType);
				listView.setAdapter(eventListAdapter);
			}
		}
		
		
		//Log.d("EditorEventListFragment.onActivityCreated", "xxx");
        
	}
	
	private void doOnStart()
	{
		// ak sa ma refreshnut aktivita, nebudeme robit nic, co je v onStart
		if (PhoneProfilesPreferencesActivity.getInvalidateEditor(false))
			return;
		
		if (eventListAdapter != null)
		{
			// update checked event in listview
			Event event = null;
			for (int i = 0; i < eventListAdapter.eventList.size(); i++)
			{
				if (listView.isItemChecked(i))
					event = eventListAdapter.eventList.get(i);
			}
			updateListView(event);
		}
	}
	
	@Override
	public void onStart()
	{
		super.onStart();

		doOnStart();
		
		//Log.d("EditorEventListFragment.onStart", "xxxx");
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.fragment_editor_event_list, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_new_event:
			//Log.e("EditorEventListFragment.onOptionsItemSelected", "menu_new_event");

			startEventPreferencesActivity(null);
			
			return true;
		case R.id.menu_delete_all_events:
			//Log.d("EditorEventListFragment.onOptionsItemSelected", "menu_delete_all_events");
			
			deleteAllEvents();
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startEventPreferencesActivity(Event event)
	{

		Event _event = event;
		
		if (_event != null)
		{
			// editacia udalosti
			int profilePos = eventListAdapter.getItemPosition(_event);
			listView.setSelection(profilePos);
			listView.setItemChecked(profilePos, true);
		}
		else
		{
			// pridanie novej udalost
			_event = new Event(getResources().getString(R.string.event_name_default), 
								  Event.ETYPE_TIME, 
								  0,
					         	  false
					         );
			
			// add event into db and set id
			databaseHandler.addEvent(_event); 
			// add event into listview
			eventListAdapter.addItem(_event, false);
			
			updateListView(_event);
			
			onEventCountChangedCallback.onEventCountChanged();

		}

		//Log.d("EditorEventListFragment.startProfilePreferencesActivity", profile.getID()+"");
		
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) one must start profile preferences
		onStartEventPreferencesCallback.onStartEventPreferences(_event, filterType, false);
	}

	public void duplicateEvent(Event origEvent)
	{
		Event newEvent = new Event(
				   origEvent._name+"_d", 
				   origEvent._type, 
				   origEvent._fkProfile, 
				   origEvent.getEnabled()
					);
		newEvent.setStatus(origEvent.getStatus());
		newEvent.copyEventPreferences(origEvent);

		// add event into db and set id and order
		databaseHandler.addEvent(newEvent); 
		// add event into listview
		eventListAdapter.addItem(newEvent, false);
		
		updateListView(newEvent);

		onEventCountChangedCallback.onEventCountChanged();
		
		//updateListView();

		startEventPreferencesActivity(newEvent);
		
		
	}

	public void deleteEvent(Event event)
	{
		final Event _event = event;

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getSherlockActivity());
		dialogBuilder.setTitle(getResources().getString(R.string.event_string_0) + ": " + event._name);
		dialogBuilder.setMessage(getResources().getString(R.string.delete_event_alert_message) + "?");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				eventListAdapter.deleteItemNoNotify(_event);
				databaseHandler.deleteEvent(_event);
				eventListAdapter.notifyDataSetChanged();
				onEventCountChangedCallback.onEventCountChanged();
				//updateListView();
				
				onStartEventPreferencesCallback.onStartEventPreferences(null, filterType, true);
				
			}
		});
		dialogBuilder.setNegativeButton(android.R.string.no, null);
		dialogBuilder.show();
	}

	private void deleteAllEvents()
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getSherlockActivity());
		dialogBuilder.setTitle(getResources().getString(R.string.alert_title_delete_all_events));
		dialogBuilder.setMessage(getResources().getString(R.string.alert_message_delete_all_events) + "?");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				databaseHandler.deleteAllEvents();
				eventListAdapter.clear();
				onEventCountChangedCallback.onEventCountChanged();
				//updateListView();
				
				onStartEventPreferencesCallback.onStartEventPreferences(null, filterType, true);
				
			}
		});
		dialogBuilder.setNegativeButton(android.R.string.no, null);
		dialogBuilder.show();
	}
	
	public void finishEventPreferencesActionMode()
	{
		onFinishEventPreferencesActionModeCallback.onFinishEventPreferencesActionMode();
	}
	
	public void updateListView(Event event)
	{
		eventListAdapter.notifyDataSetChanged();

		// sort list
		sortAlphabetically();
		
		// set event visible in list
		if (event == null)
		{	
			int eventPos = eventList.indexOf(event);
			listView.setSelection(eventPos);
			listView.setItemChecked(eventPos, true);
		}
	}

	public int getFilterType()
	{
		return filterType;
	}
	
	class AlphabeticallyComparator implements Comparator<Event> {

		public int compare(Event lhs, Event rhs) {

		    int res =  (lhs._name).compareToIgnoreCase(rhs._name);
	        return res;
	    }
	}
	
	public void sortAlphabetically()
	{
	    Collections.sort(eventList, new AlphabeticallyComparator());
	}
	

}
