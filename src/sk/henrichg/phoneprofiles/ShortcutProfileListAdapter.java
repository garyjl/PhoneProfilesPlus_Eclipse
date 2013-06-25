package sk.henrichg.phoneprofiles;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShortcutProfileListAdapter extends BaseAdapter {

	private Context context;
	private List<Profile> profileList;
	
	private LayoutInflater inflater = null;
	
	public ShortcutProfileListAdapter(Context c, List<Profile> pl)
	{
		context = c;
		profileList = pl;
		
		inflater = LayoutInflater.from(context);
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
        {
        	if (GlobalData.applicationActivatorPrefIndicator)
        		vi = inflater.inflate(R.layout.shortcut_list_item, null);
        	else
        		vi = inflater.inflate(R.layout.shortcut_list_item_no_indicator, null);
        }

        TextView profileName = (TextView)vi.findViewById(R.id.shortcut_list_item_profile_name);
        ImageView profileIcon = (ImageView)vi.findViewById(R.id.shortcut_list_item_profile_icon);
        
        Profile profile = profileList.get(position);
        
        profileName.setText(profile._name);
        if (profile.getIsIconResourceID())
        {
        	profileIcon.setImageResource(0);
        	int res = vi.getResources().getIdentifier(profile.getIconIdentifier(), "drawable", 
        				vi.getContext().getPackageName());
        	profileIcon.setImageResource(res); // resource na ikonu
        }
        else
        {
        	profileIcon.setImageBitmap(null);
    		Resources resources = vi.getResources();
    		int height = (int) resources.getDimension(android.R.dimen.app_icon_size);
    		int width = (int) resources.getDimension(android.R.dimen.app_icon_size);
    		Bitmap bitmap = BitmapResampler.resample(profile.getIconIdentifier(), width, height);
        	profileIcon.setImageBitmap(bitmap);
        }
        
		if (GlobalData.applicationActivatorPrefIndicator)
		{
			ImageView profilePrefIndicatorImageView = (ImageView)vi.findViewById(R.id.shortcut_list_profile_pref_indicator);
			profilePrefIndicatorImageView.setImageBitmap(null);
			Bitmap bitmap = ProfilePreferencesIndicator.paint(profile, vi.getContext());
			profilePrefIndicatorImageView.setImageBitmap(bitmap);
		}
        
        //Log.d("ShortcutProfileListAdapter.getView", profile.getName());
        
		return vi;
	}

	public void setList(List<Profile> pl) {
		profileList = pl;
		notifyDataSetChanged();
	}

}
