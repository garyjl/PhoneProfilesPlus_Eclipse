package sk.henrichg.phoneprofiles;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainProfileListAdapter extends BaseAdapter
{

	private Activity activity;
	private List<Profile> profileList;
	
	private static LayoutInflater inflater = null;
	
	public static boolean editIconClicked = false;
	
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
	
	public void clear()
	{
		profileList.clear();
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
		
		// teraz musime najst profile v profileList 
		int position = getItemId(profile);
		if (position != -1)
		{
			// najdenemu objektu nastavime _checked
			Profile _profile = profileList.get(position);
			if (_profile != null)
				_profile.setChecked(true);
		}
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
    		Resources resources = vi.getResources();
    		int height = (int) resources.getDimension(android.R.dimen.app_icon_size);
    		int width = (int) resources.getDimension(android.R.dimen.app_icon_size);
    		Bitmap bitmap = BitmapResampler.resample(profile.getIconIdentifier(), width, height);
        	
        	profileIcon.setImageBitmap(bitmap);
        }
        
		ImageView profilePrefIndicatorImageView = (ImageView)vi.findViewById(R.id.main_list_profile_pref_indicator);
		if (GlobalData.applicationEditorPrefIndicator)
			profilePrefIndicatorImageView.setImageBitmap(ProfilePreferencesIndicator.paint(profile, vi.getContext()));
		else
			profilePrefIndicatorImageView.setImageBitmap(null);
        
/*		ImageView profileItemEditMenu = (ImageView)vi.findViewById(R.id.main_list_item_edit_menu);
		profileItemEditMenu.setTag(position);
		profileItemEditMenu.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					editMenuClicked = true;
					//Log.d("MainProfileAdapter.onClick", "x");
					activity.openContextMenu(v);
				}
			});
*/

        final int _position = position;
		
		ImageView profileItemActivate = (ImageView)vi.findViewById(R.id.main_list_item_activate);
		profileItemActivate.setTag(R.id.main_list_item_activate);
		profileItemActivate.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					editIconClicked = true;
					Log.d("MainProfileAdapter.onClick", "activate");
					((PhoneProfilesActivity)activity).activateProfileWithAlert(_position);
				}
			}); 

		ImageView profileItemDuplicate = (ImageView)vi.findViewById(R.id.main_list_item_duplicate);
		profileItemDuplicate.setTag(R.id.main_list_item_duplicate);
		profileItemDuplicate.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					editIconClicked = true;
					Log.d("MainProfileAdapter.onClick", "duplicate");
					((PhoneProfilesActivity)activity).duplicateProfile(_position);
				}
			}); 

		ImageView profileItemDelete = (ImageView)vi.findViewById(R.id.main_list_item_delete);
		profileItemDelete.setTag(R.id.main_list_item_delete);
		profileItemDelete.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					editIconClicked = true;
					Log.d("MainProfileAdapter.onClick", "delete");
					((PhoneProfilesActivity)activity).deleteProfile(_position);
				}
			}); 
		
        //Log.d("ProfileListAdapter.getView", profile.getName());
        
		return vi;
	}

}
