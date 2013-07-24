package sk.henrichg.phoneprofiles;

import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mobeta.android.dslv.DragSortListView;

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
	private Intent intent;
	private DatabaseHandler databaseHandler;

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
		public void onStartEventPreferences(int position, boolean afterDelete);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static OnStartEventPreferences sDummyOnStartEventPreferencesCallback = new OnStartEventPreferences() {
		public void onStartEventPreferences(int position, boolean afterDelete) {
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
		
		databaseHandler = EditorProfilesActivity.profilesDataWrapper.getDatabaseHandler();
		
		intent = getSherlockActivity().getIntent();
		
		final SherlockFragment fragment = this;
		
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
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result)
			{
				super.onPostExecute(result);

				if (listView != null)
				{
					eventListAdapter = new EditorEventListAdapter(fragment, eventList);
					listView.setAdapter(eventListAdapter);
				}
				
				doOnStart();
				
			}
			
		}.execute();
		
		setHasOptionsMenu(true);

		//Log.d("EditorProfileListFragment.onCreate", "xxxx");
		
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
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				//Log.d("EditorProfileListFragment.onItemClick", "xxxx");

				startEventPreferencesActivity(position);
				
			}
			
		}); 
		
		if (eventList != null)
		{
			if (eventListAdapter == null)
			{
				eventListAdapter = new EditorEventListAdapter(this, eventList);
				listView.setAdapter(eventListAdapter);
			}
		}
		
		
		//Log.d("EditorProfileListFragment.onActivityCreated", "xxx");
        
	}
	
	private void doOnStart()
	{
		// ak sa ma refreshnut aktivita, nebudeme robit nic, co je v onStart
		if (PhoneProfilesPreferencesActivity.getInvalidateEditor(false))
			return;
		
		if (eventListAdapter != null)		
			eventListAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onStart()
	{
		super.onStart();

		doOnStart();
		
		//Log.d("EditorProfileListFragment.onStart", "xxxx");
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.fragment_editor_event_list, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_new_profile:
			//Log.d("PhoneProfileActivity.onOptionsItemSelected", "menu_new_profile");

			startEventPreferencesActivity(-1);
			
			return true;
		case R.id.menu_delete_all_profiles:
			//Log.d("EditorProfileListFragment.onOptionsItemSelected", "menu_delete_all_profiles");
			
			deleteAllEvents();
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startEventPreferencesActivity(int position)
	{

		Event event;
		
		if (position != -1)
			// editacia udalosti
			event = eventList.get(position);
		else
		{
			// pridanie novej udalost
			event = new Event(getResources().getString(R.string.event_name_default), 
								  0, 
								  0,
								  0,
					         	  true
					         );
			eventListAdapter.addItem(event); // pridame udalost do listview
			databaseHandler.addEvent(event);
			onEventCountChangedCallback.onEventCountChanged();

		}

		//Log.d("EditorProfileListFragment.startProfilePreferencesActivity", profile.getID()+"");
		
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) one must start profile preferences
		onStartEventPreferencesCallback.onStartEventPreferences(eventListAdapter.getItemId(event), false);
	}

	public void duplicateEvent(int position)
	{
		Event origEvent = eventList.get(position);

		Event newEvent = new Event(
				   origEvent._name+"_d", 
				   origEvent._type, 
				   origEvent._fkProfile, 
				   origEvent._fkParams, 
				   origEvent._enabled
					);

		eventListAdapter.addItem(newEvent);
		databaseHandler.addEvent(newEvent);
		onEventCountChangedCallback.onEventCountChanged();
		
		//updateListView();

		startEventPreferencesActivity(eventList.size()-1);
		
		
	}

	public void deleteEvent(int position)
	{
		final Event event = eventList.get(position);

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getSherlockActivity());
		dialogBuilder.setTitle(getResources().getString(R.string.event_string_0) + ": " + event._name);
		dialogBuilder.setMessage(getResources().getString(R.string.delete_event_alert_message) + "?");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				eventListAdapter.deleteItem(event);
				databaseHandler.deleteEvent(event);
				onEventCountChangedCallback.onEventCountChanged();
				//updateListView();
				
				Event event = EditorProfilesActivity.profilesDataWrapper.getFirstEvent();
				onStartEventPreferencesCallback.onStartEventPreferences(eventListAdapter.getItemId(event), true);
				
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
				
				Event event = EditorProfilesActivity.profilesDataWrapper.getFirstEvent();
				onStartEventPreferencesCallback.onStartEventPreferences(eventListAdapter.getItemId(event), true);
				
			}
		});
		dialogBuilder.setNegativeButton(android.R.string.no, null);
		dialogBuilder.show();
	}
	
	public void finishEventPreferencesActionMode()
	{
		onFinishEventPreferencesActionModeCallback.onFinishEventPreferencesActionMode();
	}
	
	public void updateListView()
	{
		eventListAdapter.notifyDataSetChanged();
	}


}
