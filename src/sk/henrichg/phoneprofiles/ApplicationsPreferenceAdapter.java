package sk.henrichg.phoneprofiles;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class ApplicationsPreferenceAdapter extends BaseAdapter {

	private Context context;
	
	
	public ApplicationsPreferenceAdapter(Context c)
	{
		context = c;
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
		
		TextView applicationIcon;
		
		if (convertView == null)
		{
			applicationIcon = new TextView(context);
			applicationIcon.setLayoutParams(new GridView.LayoutParams(120, 120));
			applicationIcon.setPadding(8, 8, 8, 8);
		}
		else
		{
			applicationIcon = (TextView)convertView;
		}
		
		applicationIcon.setText(PhoneProfilesActivity.getApplicationsCache().getApplicationLabel(position));
		applicationIcon.setCompoundDrawables(null, PhoneProfilesActivity.getApplicationsCache().getApplicationIcon(position), null, null);

		return applicationIcon;
	}

	public String getApplicationPackageName(int position)
	{
		return PhoneProfilesActivity.getApplicationsCache().getPackageName(position);
	}
}
