package sk.henrichg.phoneprofilesplus;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfilePreferenceAdapter extends BaseAdapter {

	public List<Profile> profileList;
	
	//private Context context;
	
	private LayoutInflater inflater = null;
	
	public ProfilePreferenceAdapter(Context c)
	{
		//context = c;

		inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		profileList = ProfilePreference.profilesDataWrapper.getProfileList();
	    Collections.sort(profileList, new AlphabeticallyComparator());

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

	static class ViewHolder {
		  ImageView profileIcon;
		  TextView profileLabel;
		  ImageView profileIndicator;
		  int position;
		}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		
		View vi = convertView;
	    if (convertView == null)
	    {
	      	if (GlobalData.applicationEditorPrefIndicator)
	      		vi = inflater.inflate(R.layout.profile_preference_list_item, null);
	      	else
	      		vi = inflater.inflate(R.layout.profile_preference_list_item_no_indicator, null);
	      	
	        holder = new ViewHolder();
	  		holder.profileIcon = (ImageView)vi.findViewById(R.id.profile_pref_dlg_item_icon);
	  		holder.profileLabel = (TextView)vi.findViewById(R.id.profile_pref_dlg_item_label);
	  		if (GlobalData.applicationEditorPrefIndicator)
	  			holder.profileIndicator = (ImageView)vi.findViewById(R.id.profile_pref_dlg_item_indicator);
	        vi.setTag(holder);        
	    }
	    else
	    {
	      	holder = (ViewHolder)vi.getTag();
	    }
		
	    Profile profile = profileList.get(position);
	    if (profile != null)
	    {
			holder.profileLabel.setText(profile._name);
		    if (profile.getIsIconResourceID())
		    {
		      	holder.profileIcon.setImageResource(0);
		      	int res = vi.getResources().getIdentifier(profile.getIconIdentifier(), "drawable", 
		      				vi.getContext().getPackageName());
		      	holder.profileIcon.setImageResource(res); // resource na ikonu
		    }
		    else
		      	holder.profileIcon.setImageBitmap(profile._iconBitmap);
			if (GlobalData.applicationEditorPrefIndicator)
				holder.profileIndicator.setImageBitmap(profile._preferencesIndicator);
	    }
	    else
	    {
	    	holder.profileLabel.setText("");
	    	holder.profileIcon.setImageResource(0);
			holder.profileIndicator.setImageResource(0);
	    }
	    
		return vi;
	}

	private class AlphabeticallyComparator implements Comparator<Profile> {

		public int compare(Profile lhs, Profile rhs) {

		    int res = GUIData.collator.compare(lhs._name, rhs._name);
	        return res;
	    }
	}
	
}
