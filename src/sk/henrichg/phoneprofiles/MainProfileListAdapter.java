package sk.henrichg.phoneprofiles;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainProfileListAdapter extends BaseAdapter
{

	private Activity activity;
	private List<Profile> profileList;
	
	private static LayoutInflater inflater = null;
	
	public MainProfileListAdapter(Activity a, List<Profile> pl)
	{
		activity = a;
		profileList = pl;
		
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}   
	
	public int getCount()
	{
		return profileList.size();
	}

	public Object getItem(int position)
	{
		return profileList.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public int getItemId(Profile profile)
	{
		for (int i = 0; i < profileList.size(); i++)
		{
			if (profileList.get(i).getID() == profile.getID())
				return i;
		}
		return -1;
	}
	
	public void setList(List<Profile> pl)
	{
		profileList = pl;
		notifyDataSetChanged();
	}
	
	public void addItem(Profile profile)
	{
		int maxPOrder = 0;
		int pOrder;
		for (Profile p : profileList)
		{
			pOrder = p.getPOrder();
			if (pOrder > maxPOrder) maxPOrder = pOrder;
		}
		profile.setPOrder(maxPOrder+1);
		profileList.add(profile);
		notifyDataSetChanged();
	}

	public void updateItem(Profile profile)
	{
		notifyDataSetChanged();
	}
	
	public void deleteItem(Profile profile)
	{
		profileList.remove(profile);
		notifyDataSetChanged();
	}
	
	public void changeItemOrder(int from, int to)
	{
		Profile profile = profileList.get(from);
		profileList.remove(from);
		profileList.add(to, profile);
		for (int i = 0; i < profileList.size(); i++)
		{
			profileList.get(i).setPOrder(i+1);
		}
		notifyDataSetChanged();
	}
	
	public Profile getActivatedProfile()
	{
		for (Profile p : profileList)
		{
			if (p.getChecked())
			{
				return p;
			}
		}
		
		return null;
	}
	
	public void activateProfile(Profile profile)
	{
		for (Profile p : profileList)
		{
			p.setChecked(false);
		}
		if (profile != null)
			profile.setChecked(true);
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		View vi = convertView;
        if (convertView == null)
        	vi = inflater.inflate(R.layout.main_list_item, null);
		
        TextView profileName = (TextView)vi.findViewById(R.id.main_list_item_profile_name);
        ImageView profileIcon = (ImageView)vi.findViewById(R.id.main_list_item_profile_icon);
        
        Profile profile = profileList.get(position);
        
        profileName.setText(profile.getName());
        if (profile.getIsIconResourceID())
        {
        	int res = vi.getResources().getIdentifier(profile.getIconIdentifier(), "drawable", 
        				vi.getContext().getPackageName());
        	profileIcon.setImageResource(res); // resource na ikonu
        }
        else
        {
        	profileIcon.setImageBitmap(BitmapFactory.decodeFile(profile.getIconIdentifier()));
        }
        
        ProfilePreferencesIndicator profilePreferenceIndicator = new ProfilePreferencesIndicator();
        LinearLayout profilePrefIndicatorLayout = (LinearLayout)vi.findViewById(R.id.main_list_profile_pref_indicator);
        profilePreferenceIndicator.paint(profilePrefIndicatorLayout, profile);
        
        Log.d("ProfileListAdapter.getView", profile.getName());
        
		return vi;
	}

}
