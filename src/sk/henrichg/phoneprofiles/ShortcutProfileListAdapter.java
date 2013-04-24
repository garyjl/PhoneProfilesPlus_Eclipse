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

public class ShortcutProfileListAdapter extends BaseAdapter {

	private Activity activity;
	private List<Profile> profileList;
	
	private static LayoutInflater inflater = null;
	
	public ShortcutProfileListAdapter(Activity a, List<Profile> pl)
	{
		activity = a;
		profileList = pl;
		
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}   
	
	public int getCount() {
		return profileList.size();
	}

	public Object getItem(int position) {
		return profileList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
        	vi = inflater.inflate(R.layout.shortcut_list_item, null);
		
        TextView profileName = (TextView)vi.findViewById(R.id.shortcut_list_item_profile_name);
        ImageView profileIcon = (ImageView)vi.findViewById(R.id.shortcut_list_item_profile_icon);
        
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
        LinearLayout profilePrefIndicatorLayout = (LinearLayout)vi.findViewById(R.id.shortcut_list_profile_pref_indicator);
        profilePreferenceIndicator.paint(profilePrefIndicatorLayout, profile);
        
        Log.d("ShortcutProfileListAdapter.getView", profile.getName());
        
		return vi;
	}

	public void setList(List<Profile> pl) {
		profileList = pl;
		notifyDataSetChanged();
	}

}
