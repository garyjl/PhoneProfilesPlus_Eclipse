package sk.henrichg.phoneprofiles;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ApplicationsPreferenceAdapter extends BaseAdapter {

	private Context context;
	
	public ApplicationsPreferenceAdapter(Context c)
	{
		context = c;
	}
	
	public int getCount() {
		return ApplicationsCache.getLength();
	}

	public Object getItem(int position) {
		return ApplicationsCache.getApplication(position);
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
		
		applicationIcon.setText(ApplicationsCache.getApplicationName(position));
		applicationIcon.setCompoundDrawablesWithIntrinsicBounds(0, ApplicationsCache.getApplicationIcon(position), 0, 0);

		return applicationIcon;
	}

}
