package sk.henrichg.phoneprofiles;

import java.util.List;

import sk.henrichg.phoneprofiles.EditorProfileListAdapter.ViewHolder;

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
	
	static class ViewHolder {
		  RelativeLayout listItemRoot;
		  ImageView eventIcon;
		  TextView eventName;
		  ImageView profileIcon;
		  TextView profileName;
		  TextView eventPreferencesDescription;
		  ImageView profileIndicator;
		  ImageView eventEnabled;
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
            holder.eventEnabled = (ImageView)vi.findViewById(R.id.event_list_item_enabled);
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
        
		
        Event event = eventList.get(position);

       	if (GlobalData.applicationTheme.equals("light"))
       		holder.listItemRoot.setBackgroundResource(R.drawable.card);
       	else
       	if (GlobalData.applicationTheme.equals("dark"))
       		holder.listItemRoot.setBackgroundResource(R.drawable.card_dark);
        
       	if (event._enabled)
       		holder.eventEnabled.setImageResource(R.drawable.ic_profile_activated);
       	else
       		holder.eventEnabled.setImageResource(R.drawable.ic_empty);
       	
        holder.eventName.setText(event._name);
        if (event._eventPreferences != null)
        	holder.eventIcon.setImageResource(event._eventPreferences._iconResourceID); // resource na ikonu
        else
        	holder.eventIcon.setImageResource(R.drawable.ic_empty);
        
	    if (GlobalData.applicationEditorPrefIndicator)
	    	holder.eventPreferencesDescription.setText(event.getPreferecesDescription(vi.getContext()));

        Profile profile =  EditorProfilesActivity.profilesDataWrapper.getProfileById(event._fkProfile);
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
        	holder.profileIcon.setImageResource(R.drawable.ic_empty);
			if (GlobalData.applicationEditorPrefIndicator)
			{
				//profilePrefIndicatorImageView.setImageBitmap(null);
				//Bitmap bitmap = ProfilePreferencesIndicator.paint(profile, vi.getContext());
				//profilePrefIndicatorImageView.setImageBitmap(bitmap);
				holder.profileIndicator.setImageResource(R.drawable.ic_empty);
			}
        }
        
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
