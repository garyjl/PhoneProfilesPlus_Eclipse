package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class EventTypePreferenceAdapter extends BaseAdapter {

	//private Context context;
	
	private LayoutInflater inflater = null;
	int eventType;
	EventTypePreferenceDialog dialog;
	
	
	static final int[] eventTypes = {
		Event.ETYPE_TIME,
		Event.ETYPE_BATTERY
	};

	static final int[] eventTypeIconIds = {
		R.drawable.ic_event_time,
		R.drawable.ic_event_battery
	};

	static final int[] eventTypeNameIds = {
		R.string.event_type_time,
		R.string.event_type_battery
	};
	
	public EventTypePreferenceAdapter(EventTypePreferenceDialog dialog, Context c, String eventType)
	{
		//context = c;
		
		this.dialog = dialog;

		if (eventType.isEmpty())
			this.eventType = 0;
		else
			this.eventType = Integer.valueOf(eventType);
		
		inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {
		return eventTypes.length;
	}

	public Object getItem(int position) {
		return String.valueOf(eventTypes[position]);
	}

	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		  ImageView eventTypeIcon;
		  TextView eventTypeLabel;
		  RadioButton radioBtn;
		  int position;
		}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		
		View vi = convertView;
	    if (convertView == null)
	    {
	      	vi = inflater.inflate(R.layout.event_type_preference_list_item, null);
	        holder = new ViewHolder();
	  		holder.eventTypeIcon = (ImageView)vi.findViewById(R.id.event_type_pref_dlg_item_icon);
	  		holder.eventTypeLabel = (TextView)vi.findViewById(R.id.event_type_pref_dlg_item_label);
	        holder.radioBtn = (RadioButton)vi.findViewById(R.id.event_type_pref_dlg_item_radiobtn);
	        vi.setTag(holder);        
	    }
	    else
	    {
	      	holder = (ViewHolder)vi.getTag();
	    }
		
		holder.eventTypeLabel.setText(eventTypeNameIds[position]);
		holder.eventTypeIcon.setImageResource(eventTypeIconIds[position]);
    	holder.radioBtn.setChecked(eventTypes[position] == eventType);

		return vi;
	}

}
