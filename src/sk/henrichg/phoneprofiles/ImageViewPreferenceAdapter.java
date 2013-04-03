package sk.henrichg.phoneprofiles;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageViewPreferenceAdapter extends BaseAdapter {

	private Context context;
	
	static final String[] ThumbsIds = {
		"ic_profile_default", "ic_profile_home", 
		"ic_profile_outdoors_1", "ic_profile_outdoors_2", "ic_profile_outdoors_3",
		"ic_profile_meeting", "ic_profile_work_1", "ic_profile_work_2", "ic_profile_work_4",
		"ic_profile_sleep", "ic_profile_mute"
	};
	
	public ImageViewPreferenceAdapter(Context c)
	{
		context = c;
	}
	
	public int getCount() {
		return ThumbsIds.length;
	}

	public Object getItem(int position) {
		return ThumbsIds[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		ImageView imageView;
		
		if (convertView == null)
		{
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(120, 120));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		}
		else
		{
			imageView = (ImageView)convertView;
		}
		
		imageView.setImageResource(context.getResources().getIdentifier(ThumbsIds[position], "drawable", context.getPackageName()));
		
		return imageView;
	}

}
