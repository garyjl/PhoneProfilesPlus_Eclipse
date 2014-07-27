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
import android.widget.RadioButton;
import android.widget.TextView;

public class ProfilePreferenceAdapter extends BaseAdapter {

	public List<Profile> profileList;
	long profileId;
	ProfilePreferenceDialog dialog;
	
	//private Context context;
	
	private LayoutInflater inflater = null;
	
	public ProfilePreferenceAdapter(ProfilePreferenceDialog dialog, Context c, String profileId)
	{
		//context = c;

		this.dialog = dialog;
		
		if (profileId.isEmpty())
			this.profileId = 0;
		else
			this.profileId = Long.valueOf(profileId);
			
		inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		profileList = ProfilePreference.dataWrapper.getProfileList();
	    Collections.sort(profileList, new AlphabeticallyComparator());

	}
	
	public int getCount() {
		int count = profileList.size();
		if (dialog.addNoActivateItem == 1)
			count++;
		return count;
	}

	public Object getItem(int position) {
		Profile profile;
		if (dialog.addNoActivateItem == 1)
		{
			if (position == 0)
				profile = null;
			else
				profile = profileList.get(position-1);
		}
		else
			profile = profileList.get(position);
		return profile;
	}

	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		  ImageView profileIcon;
		  TextView profileLabel;
		  ImageView profileIndicator;
		  RadioButton radioBtn;
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
	        holder.radioBtn = (RadioButton)vi.findViewById(R.id.profile_pref_dlg_item_radiobtn);
	        vi.setTag(holder);        
	    }
	    else
	    {
	      	holder = (ViewHolder)vi.getTag();
	    }
	    
	    Profile profile;
	    if (dialog.addNoActivateItem == 1)
	    {
	    	if (position == 0)
	    		profile = null;
	    	else
		    	profile = profileList.get(position-1);
	    }
	    else
	    	profile = profileList.get(position);
	    
	    if (profile != null)
	    {
	    	holder.radioBtn.setChecked(profileId == profile._id);

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
	    	if ((dialog.addNoActivateItem == 1) && (position == 0))
	    	{
		    	holder.radioBtn.setChecked((profileId == Event.PROFILE_END_NO_ACTIVATE));
		    	holder.profileLabel.setText(vi.getResources().getString(R.string.event_preferences_profile_end_no_activate));
		    	holder.profileIcon.setImageResource(R.drawable.ic_profile_default);
				if (GlobalData.applicationEditorPrefIndicator)
					holder.profileIndicator.setImageResource(R.drawable.ic_empty);
	    	}
	    	else
	    	{
		    	holder.radioBtn.setChecked(false);
		    	holder.profileLabel.setText("");
		    	holder.profileIcon.setImageResource(R.drawable.ic_empty);
				if (GlobalData.applicationEditorPrefIndicator)
					holder.profileIndicator.setImageResource(R.drawable.ic_empty);
	    	}
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
