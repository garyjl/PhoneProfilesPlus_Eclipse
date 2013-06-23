package sk.henrichg.phoneprofiles;

import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;

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

public class EditorProfileListAdapter extends BaseAdapter
{

	private SherlockFragment fragment;
	private List<Profile> profileList;
	
	private static LayoutInflater inflater = null;
	
	public static boolean editIconClicked = false;
	
	public EditorProfileListAdapter(SherlockFragment f, List<Profile> pl)
	{
		fragment = f;
		profileList = pl;
		
		inflater = (LayoutInflater)fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}   
	
	public int getCount()
	{
		return profileList.size();
	}

	public Object getItem(int position)
	{
		if (profileList.size() == 0)
			return null;
		else
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
			if (profileList.get(i)._id == profile._id)
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
			pOrder = p._porder;
			if (pOrder > maxPOrder) maxPOrder = pOrder;
		}
		profile._porder = maxPOrder+1;
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
			profileList.get(i)._porder = i+1;
		}
		notifyDataSetChanged();
	}
	
	public Profile getActivatedProfile()
	{
		for (Profile p : profileList)
		{
			if (p._checked)
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
			p._checked = false;
		}
		
		// teraz musime najst profile v profileList 
		int position = getItemId(profile);
		if (position != -1)
		{
			// najdenemu objektu nastavime _checked
			Profile _profile = profileList.get(position);
			if (_profile != null)
				_profile._checked = true;
		}
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		View vi = convertView;
        if (convertView == null)
        {
        	if (GlobalData.applicationEditorPrefIndicator)
        		vi = inflater.inflate(R.layout.editor_profile_list_item, null);
        	else
        		vi = inflater.inflate(R.layout.editor_profile_list_item_no_indicator, null);
        }
		
        TextView profileName = (TextView)vi.findViewById(R.id.main_list_item_profile_name);
        ImageView profileIcon = (ImageView)vi.findViewById(R.id.main_list_item_profile_icon);
        
        Profile profile = profileList.get(position);
        
        profileName.setText(profile._name);
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
        
		if (GlobalData.applicationEditorPrefIndicator)
		{
			ImageView profilePrefIndicatorImageView = (ImageView)vi.findViewById(R.id.main_list_profile_pref_indicator);
			profilePrefIndicatorImageView.setImageBitmap(ProfilePreferencesIndicator.paint(profile, vi.getContext()));
		}
        
        final int _position = position;
		
		ImageView profileItemActivate = (ImageView)vi.findViewById(R.id.main_list_item_activate);
		profileItemActivate.setTag(R.id.main_list_item_activate);
		profileItemActivate.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					editIconClicked = true;
					Log.d("EditorProfileListAdapter.onClick", "activate");
					((EditorProfileListFragment)fragment).finishProfilePreferencesActionMode();
					((EditorProfileListFragment)fragment).activateProfileWithAlert(_position);
				}
			}); 

		ImageView profileItemDuplicate = (ImageView)vi.findViewById(R.id.main_list_item_duplicate);
		profileItemDuplicate.setTag(R.id.main_list_item_duplicate);
		profileItemDuplicate.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					editIconClicked = true;
					Log.d("EditorProfileListAdapter.onClick", "duplicate");
					((EditorProfileListFragment)fragment).finishProfilePreferencesActionMode();
					((EditorProfileListFragment)fragment).duplicateProfile(_position);
				}
			}); 

		ImageView profileItemDelete = (ImageView)vi.findViewById(R.id.main_list_item_delete);
		profileItemDelete.setTag(R.id.main_list_item_delete);
		profileItemDelete.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					editIconClicked = true;
					Log.d("EditorProfileListAdapter.onClick", "delete");
					((EditorProfileListFragment)fragment).finishProfilePreferencesActionMode();
					((EditorProfileListFragment)fragment).deleteProfile(_position);
				}
			}); 
		
        //Log.d("ProfileListAdapter.getView", profile.getName());
        
		return vi;
	}

}
