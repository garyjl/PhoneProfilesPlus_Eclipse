package sk.henrichg.phoneprofiles;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ApplicationsPreferenceAdapter extends BaseAdapter {

	private Context context;
	
	private static LayoutInflater inflater = null;
	
	
	public ApplicationsPreferenceAdapter(Context c)
	{
		context = c;

		inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {
		return PhoneProfilesActivity.getApplicationsCache().getLength();
	}

	public Object getItem(int position) {
		return PhoneProfilesActivity.getApplicationsCache().getPackageName(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		View vi = convertView;
        if (convertView == null)
        	vi = inflater.inflate(R.layout.applications_preference_list_item, null);
		
		ImageView applicationIcon = (ImageView)vi.findViewById(R.id.applications_pref_dlg_item_icon);
		TextView applicationLabel = (TextView)vi.findViewById(R.id.applications_pref_dlg_item_label);
		
		//Log.d("ApplicationsPreferenceAdapter.getView", PhoneProfilesActivity.getApplicationsCache().getApplicationLabel(position).toString());
		//Log.d("ApplicationsPreferenceAdapter.getView", PhoneProfilesActivity.getApplicationsCache().getApplicationIcon(position).toString());
		
		applicationLabel.setText(PhoneProfilesActivity.getApplicationsCache().getApplicationLabel(position));

		Drawable icon = PhoneProfilesActivity.getApplicationsCache().getApplicationIcon(position);
		//Resources resources = context.getResources();
		//int height = (int) resources.getDimension(android.R.dimen.app_icon_size);
		//int width = (int) resources.getDimension(android.R.dimen.app_icon_size);
		//icon.setBounds(0, 0, width, height);
		//applicationIcon.setCompoundDrawables(icon, null, null, null);
		applicationIcon.setImageDrawable(icon);

		return vi;
	}

	public String getApplicationPackageName(int position)
	{
		return PhoneProfilesActivity.getApplicationsCache().getPackageName(position);
	}
}
