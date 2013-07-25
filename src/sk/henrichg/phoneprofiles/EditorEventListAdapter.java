package sk.henrichg.phoneprofiles;

import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EditorEventListAdapter extends BaseAdapter
{

	private SherlockFragment fragment;
	private List<Event> eventList;
	
	public static boolean editIconClicked = false;
	
	public EditorEventListAdapter(SherlockFragment f, List<Event> el)
	{
		fragment = f;
		eventList = el;
	}   
	
	public int getCount()
	{
		return eventList.size();
	}

	public Object getItem(int position)
	{
		if (eventList.size() == 0)
			return null;
		else
			return eventList.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public int getItemId(Event event)
	{
		for (int i = 0; i < eventList.size(); i++)
		{
			if (eventList.get(i)._id == event._id)
				return i;
		}
		return -1;
	}
	
	public void setList(List<Event> el)
	{
		eventList = el;
		notifyDataSetChanged();
	}
	
	public void addItem(Event event)
	{
		eventList.add(event);
		notifyDataSetChanged();
	}

/*	
	public void updateItem(Event event)
	{
		notifyDataSetChanged();
	}
*/	
	public void deleteItem(Event event)
	{
		eventList.remove(event);
		notifyDataSetChanged();
	}
	
	public void clear()
	{
		eventList.clear();
		notifyDataSetChanged();
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View vi = convertView;
        if (convertView == null)
        {
    		LayoutInflater inflater = LayoutInflater.from(fragment.getSherlockActivity());
       		vi = inflater.inflate(R.layout.editor_event_list_item, parent, false);
        }
		
        RelativeLayout listItemRoot = (RelativeLayout)vi.findViewById(R.id.event_list_item_root);
        TextView eventName = (TextView)vi.findViewById(R.id.event_list_item_event_name);
        ImageView eventIcon = (ImageView)vi.findViewById(R.id.event_list_item_event_icon);
        
        Event event = eventList.get(position);

       	if (GlobalData.applicationTheme.equals("light"))
       		listItemRoot.setBackgroundResource(R.drawable.card);
       	else
       	if (GlobalData.applicationTheme.equals("dark"))
       		listItemRoot.setBackgroundResource(R.drawable.card_dark);
        
        eventName.setText(event._name);
       	eventIcon.setImageResource(0);
       	int res;
       	switch (event._type) {
	       	case 0: res = 0;
	       	default: res = 0;
       	}
       	eventIcon.setImageResource(res); // resource na ikonu
        
        final int _position = position;
		
		ImageView eventItemDuplicate = (ImageView)vi.findViewById(R.id.event_list_item_duplicate);
		eventItemDuplicate.setTag(R.id.event_list_item_duplicate);
		eventItemDuplicate.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					editIconClicked = true;
					//Log.d("EditorProfileListAdapter.onClick", "duplicate");
					((EditorEventListFragment)fragment).finishEventPreferencesActionMode();
					((EditorEventListFragment)fragment).duplicateEvent(_position);
				}
			}); 

		ImageView eventItemDelete = (ImageView)vi.findViewById(R.id.event_list_item_delete);
		eventItemDelete.setTag(R.id.event_list_item_delete);
		eventItemDelete.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					editIconClicked = true;
					//Log.d("EditorProfileListAdapter.onClick", "delete");
					((EditorEventListFragment)fragment).finishEventPreferencesActionMode();
					((EditorEventListFragment)fragment).deleteEvent(_position);
				}
			}); 
		
        //Log.d("ProfileListAdapter.getView", profile.getName());
        
		return vi;
	}

}
