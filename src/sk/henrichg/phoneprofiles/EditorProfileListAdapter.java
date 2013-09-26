package sk.henrichg.phoneprofiles;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EditorProfileListAdapter extends BaseAdapter
{

	private EditorProfileListFragment fragment;
	private ProfilesDataWrapper profilesDataWrapper;
	private List<Profile> profileList;
	public static boolean editIconClicked = false;
	
	public EditorProfileListAdapter(EditorProfileListFragment f, ProfilesDataWrapper pdw)
	{
		fragment = f;
		profilesDataWrapper = pdw;
		profileList = profilesDataWrapper.getProfileList(fragment.getFilterType());
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

/*	public void updateItem(Profile profile)
	{
		notifyDataSetChanged();
	}
*/	
	public void deleteItemNoNotify(Profile profile)
	{
		profilesDataWrapper.deleteProfile(profile);
	}

	public void deleteItem(Profile profile)
	{
		deleteItemNoNotify(profile);
		notifyDataSetChanged();
	}

	public void clearNoNotify()
	{
		profilesDataWrapper.deleteAllProfiles();
	}
	
	public void clear()
	{
		clearNoNotify();
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
		notifyDataSetChanged();
	}

	static class ViewHolder {
		  RelativeLayout listItemRoot;
		  ImageView profileIcon;
		  TextView profileName;
		  ImageView profileIndicator;
		  ImageView profileItemActivate;
		  ImageView profileItemDuplicate;
		  ImageView profileItemDelete;
		  ImageView profileShowInActivator;
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
      		    vi = inflater.inflate(R.layout.editor_profile_list_item, parent, false);
      	    else
      		    vi = inflater.inflate(R.layout.editor_profile_list_item_no_indicator, parent, false);
            holder = new ViewHolder();
            holder.listItemRoot = (RelativeLayout)vi.findViewById(R.id.profile_list_item_root);
            holder.profileName = (TextView)vi.findViewById(R.id.profile_list_item_profile_name);
            holder.profileIcon = (ImageView)vi.findViewById(R.id.profile_list_item_profile_icon);
  		    holder.profileItemActivate = (ImageView)vi.findViewById(R.id.profile_list_item_activate);
  		    holder.profileItemDuplicate = (ImageView)vi.findViewById(R.id.profile_list_item_duplicate);
  		    holder.profileItemDelete = (ImageView)vi.findViewById(R.id.profile_list_item_delete);
  		    holder.profileShowInActivator = (ImageView)vi.findViewById(R.id.profile_list_item_show_in_activator);
  		    if (GlobalData.applicationEditorPrefIndicator)
  			    holder.profileIndicator = (ImageView)vi.findViewById(R.id.profile_list_profile_pref_indicator);
            vi.setTag(holder);        
        }
        else
        {
      	    holder = (ViewHolder)vi.getTag();
        }
		
        Profile profile = profileList.get(position);

        if (profile._checked && (!GlobalData.applicationEditorHeader))
        {
      	    if (GlobalData.applicationTheme.equals("light"))
      	    	holder.listItemRoot.setBackgroundResource(R.drawable.header_card);
      	    else
         	if (GlobalData.applicationTheme.equals("dark"))
         		holder.listItemRoot.setBackgroundResource(R.drawable.header_card_dark);
         	else
         	if (GlobalData.applicationTheme.equals("dlight"))
         		holder.listItemRoot.setBackgroundResource(R.drawable.header_card);
        }
        else
        {
        	if (GlobalData.applicationTheme.equals("light"))
        		holder.listItemRoot.setBackgroundResource(R.drawable.card);
        	else
        	if (GlobalData.applicationTheme.equals("dark"))
        		holder.listItemRoot.setBackgroundResource(R.drawable.card_dark);
         	else
         	if (GlobalData.applicationTheme.equals("dlight"))
         		holder.listItemRoot.setBackgroundResource(R.drawable.card);
        }
      
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
        	//profileIcon.setImageBitmap(null);
        	//Resources resources = vi.getResources();
        	//int height = (int) resources.getDimension(android.R.dimen.app_icon_size);
        	//int width = (int) resources.getDimension(android.R.dimen.app_icon_size);
        	//Bitmap bitmap = BitmapResampler.resample(profile.getIconIdentifier(), width, height);
        	//profileIcon.setImageBitmap(bitmap);
        	holder.profileIcon.setImageBitmap(profile._iconBitmap);
        }

        if (profile._showInActivator)
        	holder.profileShowInActivator.setImageResource(R.drawable.ic_profile_activated);
        else
        	holder.profileShowInActivator.setImageResource(R.drawable.ic_profile_deactivated);
        
		if (GlobalData.applicationEditorPrefIndicator)
		{
			//profilePrefIndicatorImageView.setImageBitmap(null);
			//Bitmap bitmap = ProfilePreferencesIndicator.paint(profile, vi.getContext());
			//profilePrefIndicatorImageView.setImageBitmap(bitmap);
			holder.profileIndicator.setImageBitmap(profile._preferencesIndicator);
		}
		
		final int _position = position;
		
		holder.profileItemActivate.setTag(R.id.profile_list_item_activate);
		holder.profileItemActivate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				editIconClicked = true;
				//Log.d("EditorProfileListAdapter.onClick", "activate");
				((EditorProfileListFragment)fragment).finishProfilePreferencesActionMode();
				((EditorProfileListFragment)fragment).activateProfile(_position, true);
			}
		}); 

		holder.profileItemDuplicate.setTag(R.id.profile_list_item_duplicate);
		holder.profileItemDuplicate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				editIconClicked = true;
				//Log.d("EditorProfileListAdapter.onClick", "duplicate");
				((EditorProfileListFragment)fragment).finishProfilePreferencesActionMode();
				((EditorProfileListFragment)fragment).duplicateProfile(_position);
			}
		}); 

		holder.profileItemDelete.setTag(R.id.profile_list_item_delete);
		holder.profileItemDelete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				editIconClicked = true;
				//Log.d("EditorProfileListAdapter.onClick", "delete");
				((EditorProfileListFragment)fragment).finishProfilePreferencesActionMode();
				((EditorProfileListFragment)fragment).deleteProfile(_position);
			}
		}); 
		
		//Log.d("ProfileListAdapter.getView", profile.getName());
      
		return vi;
	}

}
