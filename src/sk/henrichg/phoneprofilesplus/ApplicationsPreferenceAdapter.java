package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ApplicationsPreferenceAdapter extends BaseAdapter {

	//private Context context;
	
	private LayoutInflater inflater = null;
	
	
	public ApplicationsPreferenceAdapter(Context c)
	{
		//context = c;

		inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {
		return EditorProfilesActivity.getApplicationsCache().getLength();
	}

	public Object getItem(int position) {
		return EditorProfilesActivity.getApplicationsCache().getPackageName(position);
	}

	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		  ImageView applicationIcon;
		  TextView applicationLabel;
		  int position;
		}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		
		View vi = convertView;
      if (convertView == null)
      {
      	vi = inflater.inflate(R.layout.applications_preference_list_item, null);
        holder = new ViewHolder();
  		holder.applicationIcon = (ImageView)vi.findViewById(R.id.applications_pref_dlg_item_icon);
  		holder.applicationLabel = (TextView)vi.findViewById(R.id.applications_pref_dlg_item_label);
          vi.setTag(holder);        
      }
      else
      {
      	holder = (ViewHolder)vi.getTag();
      }
		
		//Log.d("ApplicationsPreferenceAdapter.getView", EditorProfilesActivity.getApplicationsCache().getApplicationLabel(position).toString());
		//Log.d("ApplicationsPreferenceAdapter.getView", EditorProfilesActivity.getApplicationsCache().getApplicationIcon(position).toString());
		
		holder.applicationLabel.setText(EditorProfilesActivity.getApplicationsCache().getApplicationLabel(position));

		Drawable icon = EditorProfilesActivity.getApplicationsCache().getApplicationIcon(position);
		//Resources resources = context.getResources();
		//int height = (int) resources.getDimension(android.R.dimen.app_icon_size);
		//int width = (int) resources.getDimension(android.R.dimen.app_icon_size);
		//icon.setBounds(0, 0, width, height);
		//applicationIcon.setCompoundDrawables(icon, null, null, null);
		holder.applicationIcon.setImageDrawable(icon);

		return vi;
	}

	public String getApplicationPackageName(int position)
	{
		return EditorProfilesActivity.getApplicationsCache().getPackageName(position);
	}
}