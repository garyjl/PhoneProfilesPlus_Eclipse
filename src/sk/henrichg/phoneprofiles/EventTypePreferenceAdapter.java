package sk.henrichg.phoneprofiles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventTypePreferenceAdapter extends BaseAdapter {

	//private Context context;
	
	private LayoutInflater inflater = null;
	
	static final int[] eventTypes = {
		Event.ETYPE_TIME_RANGE, Event.ETYPE_TIME_REPEAT
	};

	static final int[] eventTypeIconIds = {
		R.drawable.ic_event_time_range, R.drawable.ic_event_time_repeat
	};

	static final int[] eventTypeNameIds = {
		R.string.event_type_time_range, R.string.event_type_time_repeat
	};
	
	public EventTypePreferenceAdapter(Context c)
	{
		//context = c;

		inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {
		return eventTypes.length;
	}

	public Object getItem(int position) {
		return eventTypes[position];
	}

	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		  ImageView eventTypeIcon;
		  TextView eventTypeLabel;
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
	        vi.setTag(holder);        
	    }
	    else
	    {
	      	holder = (ViewHolder)vi.getTag();
	    }
		
		holder.eventTypeLabel.setText(eventTypeNameIds[position]);
		holder.eventTypeIcon.setImageResource(eventTypeIconIds[position]);

		return vi;
	}

}
