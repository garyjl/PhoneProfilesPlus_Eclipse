package sk.henrichg.phoneprofiles;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
//import android.preference.Preference;
//import android.preference.Preference.OnPreferenceChangeListener;
import android.view.View;
import android.content.DialogInterface.OnShowListener;


public class EventTypePreferenceDialog extends Dialog implements OnShowListener {

	private EventTypePreference eventTypePreference;
	private EventTypePreferenceAdapter eventTypePreferenceAdapter;
	
	private Context _context;
	
	private ListView listView;
	public EventTypePreferenceDialog(Context context) {
		super(context);
	}
	
	public EventTypePreferenceDialog(Context context, EventTypePreference preference)
	{
		super(context);
		
		eventTypePreference = preference;

		_context = context;
		
		setContentView(R.layout.activity_event_type_pref_dialog);
		
		listView = (ListView)findViewById(R.id.event_type_pref_dlg_listview);
		
		eventTypePreferenceAdapter = new EventTypePreferenceAdapter(_context); 
	
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				eventTypePreference.setEventType(EventTypePreferenceAdapter.eventTypes[position]);
				EventTypePreferenceDialog.this.dismiss();
			}

		});

/*		applicationPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
			}	
		}); */

		setOnShowListener(this);
	}

	
	public void onShow(DialogInterface dialog) {
		listView.setAdapter(eventTypePreferenceAdapter);
	}
	
	

}
