package sk.henrichg.phoneprofilesplus;

import java.util.List;

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

	private EditorEventListFragment fragment;
	private DataWrapper dataWrapper;
	private int filterType;
	public List<Event> eventList;
	
	public static boolean editIconClicked = false;
	
	public EditorEventListAdapter(EditorEventListFragment f, DataWrapper pdw, int filterType)
	{
		fragment = f;
		dataWrapper = pdw;
		eventList = dataWrapper.getEventList();
		this.filterType = filterType;
	}   
	
	public int getCount()
	{
		if (filterType == EditorEventListFragment.FILTER_TYPE_ALL)
			return eventList.size();
		
		int count = 0;
		for (Event event : eventList)
		{
	        switch (filterType)
	        {
				case EditorEventListFragment.FILTER_TYPE_RUNNING:
					if (event._status == Event.ESTATUS_RUNNING)
						++count;
					break;
				case EditorEventListFragment.FILTER_TYPE_PAUSED:
					if (event._status == Event.ESTATUS_PAUSE)
						++count;
					break;
				case EditorEventListFragment.FILTER_TYPE_STOPPED:
					if (event._status == Event.ESTATUS_STOP)
						++count;
					break;
	        }
		}
		return count;
	}

	public Object getItem(int position)
	{
		if (getCount() == 0)
			return null;
		else
		{
			
			if (filterType == EditorEventListFragment.FILTER_TYPE_ALL)
				return eventList.get(position);
			
			Event _event = null;
			
			int pos = -1;
			for (Event event : eventList)
			{
				switch (filterType)
		        {
					case EditorEventListFragment.FILTER_TYPE_RUNNING:
						if (event._status == Event.ESTATUS_RUNNING)
							++pos;
						break;
					case EditorEventListFragment.FILTER_TYPE_PAUSED:
						if (event._status == Event.ESTATUS_PAUSE)
							++pos;
						break;
					case EditorEventListFragment.FILTER_TYPE_STOPPED:
						if (event._status == Event.ESTATUS_STOP)
							++pos;
						break;
		        }
		        if (pos == position)
		        {
		        	_event = event;
		        	break;
		        }
			}
			
			return _event;
		}
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
	
	public int getItemPosition(Event event)
	{
		if (event == null)
			return -1;
		
		if (filterType == EditorEventListFragment.FILTER_TYPE_ALL)
			return eventList.indexOf(event);
		
		int pos = -1;
		
		for (int i = 0; i < eventList.size(); i++)
		{
			switch (filterType)
	        {
				case EditorEventListFragment.FILTER_TYPE_RUNNING:
					if (event._status == Event.ESTATUS_RUNNING)
						++pos;
					break;
				case EditorEventListFragment.FILTER_TYPE_PAUSED:
					if (event._status == Event.ESTATUS_PAUSE)
						++pos;
					break;
				case EditorEventListFragment.FILTER_TYPE_STOPPED:
					if (event._status == Event.ESTATUS_STOP)
						++pos;
					break;
	        }
			
			if (eventList.get(i)._id == event._id)
				return pos;
		}
		return -1;
	}
	
	public void setList(List<Event> el)
	{
		eventList = el;
		notifyDataSetChanged();
	}
	
	public void addItem(Event event, boolean refresh)
	{
		eventList.add(event);
		if (refresh)
			notifyDataSetChanged();
	}

	public void deleteItemNoNotify(Event event)
	{
		eventList.remove(event);
	}

	public void deleteItem(Event event)
	{
		deleteItemNoNotify(event);
		notifyDataSetChanged();
	}
	
	public void clear()
	{
		eventList.clear();
		notifyDataSetChanged();
	}
	
	static class ViewHolder {
		  RelativeLayout listItemRoot;
		  ImageView eventIcon;
		  TextView eventName;
		  ImageView profileIcon;
		  TextView profileName;
		  TextView eventPreferencesDescription;
		  ImageView profileIndicator;
		  ImageView eventStatus;
		  int position;
		}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		
		View vi = convertView;
        if (convertView == null)
        {
    		LayoutInflater inflater = LayoutInflater.from(fragment.getSherlockActivity());
      	    if (GlobalData.applicationEditorPrefIndicator)
      		    vi = inflater.inflate(R.layout.editor_event_list_item, parent, false);
      	    else
      		    vi = inflater.inflate(R.layout.editor_event_list_item_no_indicator, parent, false);
            holder = new ViewHolder();
            holder.listItemRoot = (RelativeLayout)vi.findViewById(R.id.event_list_item_root);
            holder.eventName = (TextView)vi.findViewById(R.id.event_list_item_event_name);
            holder.eventIcon = (ImageView)vi.findViewById(R.id.event_list_item_event_icon);
            holder.profileName = (TextView)vi.findViewById(R.id.event_list_item_profile_name);
            holder.profileIcon = (ImageView)vi.findViewById(R.id.event_list_item_profile_icon);
            holder.eventStatus = (ImageView)vi.findViewById(R.id.event_list_item_status);
  		    if (GlobalData.applicationEditorPrefIndicator)
  		    {
  		    	holder.eventPreferencesDescription  = (TextView)vi.findViewById(R.id.event_list_item_preferences_description);
  		    	holder.profileIndicator = (ImageView)vi.findViewById(R.id.event_list_item_profile_pref_indicator);
  		    }
            vi.setTag(holder);        
        }
        else
        {
      	    holder = (ViewHolder)vi.getTag();
        }
        
		
        final Event event = (Event)getItem(position);

       	if (GlobalData.applicationTheme.equals("light"))
       		holder.listItemRoot.setBackgroundResource(R.drawable.card);
       	else
       	if (GlobalData.applicationTheme.equals("dark"))
       		holder.listItemRoot.setBackgroundResource(R.drawable.card_dark);
        
       	int statusRes = Event.ESTATUS_STOP; 
       	switch (event._status)
       	{
       		case Event.ESTATUS_RUNNING:
       			statusRes = R.drawable.ic_event_status_running;
       			break;
       		case Event.ESTATUS_PAUSE:
       			statusRes = R.drawable.ic_event_status_pause;
       			break;
       		case Event.ESTATUS_STOP:
       			statusRes = R.drawable.ic_event_status_stop;
       			break;
       	}
   		holder.eventStatus.setImageResource(statusRes);
       	
        holder.eventName.setText(event._name);
        if (event._eventPreferences != null)
        	holder.eventIcon.setImageResource(event._eventPreferences._iconResourceID); // resource na ikonu
        else
        	holder.eventIcon.setImageResource(R.drawable.ic_empty);
        
	    if (GlobalData.applicationEditorPrefIndicator)
	    	holder.eventPreferencesDescription.setText(event.getPreferecesDescription(vi.getContext()));

        Profile profile =  EditorProfilesActivity.dataWrapper.getProfileById(event._fkProfile);
        if (profile != null)
        {
        	holder.profileName.setText(profile._name);
		    if (profile.getIsIconResourceID())
		    {
		    	holder.profileIcon.setImageResource(0);
		      	int res = vi.getResources().getIdentifier(profile.getIconIdentifier(), "drawable", 
		      				vi.getContext().getPackageName());
		      	holder.profileIcon.setImageResource(res); // resource na ikonu
		    }
		    else
		    {
		      	holder.profileIcon.setImageBitmap(profile._iconBitmap);
		    }
		    
			if (GlobalData.applicationEditorPrefIndicator)
			{
				//profilePrefIndicatorImageView.setImageBitmap(null);
				//Bitmap bitmap = ProfilePreferencesIndicator.paint(profile, vi.getContext());
				//profilePrefIndicatorImageView.setImageBitmap(bitmap);
				holder.profileIndicator.setImageBitmap(profile._preferencesIndicator);
			}
        }
        else
        {
        	holder.profileName.setText(R.string.event_preferences_profile_not_set);
        	holder.profileIcon.setImageResource(R.drawable.ic_profile_default);
			if (GlobalData.applicationEditorPrefIndicator)
			{
				//profilePrefIndicatorImageView.setImageBitmap(null);
				//Bitmap bitmap = ProfilePreferencesIndicator.paint(profile, vi.getContext());
				//profilePrefIndicatorImageView.setImageBitmap(bitmap);
				holder.profileIndicator.setImageResource(R.drawable.ic_empty);
			}
        }
        
		ImageView eventItemDuplicate = (ImageView)vi.findViewById(R.id.event_list_item_duplicate);
		eventItemDuplicate.setTag(R.id.event_list_item_duplicate);
		eventItemDuplicate.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					editIconClicked = true;
					//Log.d("EditorProfileListAdapter.onClick", "duplicate");
					((EditorEventListFragment)fragment).finishEventPreferencesActionMode();
					((EditorEventListFragment)fragment).duplicateEvent(event);
				}
			}); 

		ImageView eventItemDelete = (ImageView)vi.findViewById(R.id.event_list_item_delete);
		eventItemDelete.setTag(R.id.event_list_item_delete);
		eventItemDelete.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					editIconClicked = true;
					//Log.d("EditorProfileListAdapter.onClick", "delete");
					((EditorEventListFragment)fragment).finishEventPreferencesActionMode();
					((EditorEventListFragment)fragment).deleteEvent(event);
				}
			}); 
		
        //Log.d("ProfileListAdapter.getView", profile.getName());
        
		return vi;
	}

}
