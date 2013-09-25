package sk.henrichg.phoneprofiles;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

@SuppressLint("NewApi")
public class ProfileListWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

	private Context ctxt=null;
	//private int appWidgetId;
	private List<Profile> profileList;

	public ProfileListWidgetFactory(Context ctxt, Intent intent) {
		this.ctxt=ctxt;
		/*appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                       AppWidgetManager.INVALID_APPWIDGET_ID);*/
	}
  
	public void onCreate() {
		// no-op
	}
  
	public void onDestroy() {
		// no-op
	}

	public int getCount() {
		return(profileList.size());
	}

	public RemoteViews getViewAt(int position) {
		RemoteViews row=new RemoteViews(ctxt.getPackageName(), R.layout.profile_list_widget_item);
    
		Profile profile = profileList.get(position);

		if (profile.getIsIconResourceID())
		{
			row.setImageViewResource(R.id.widget_profile_list_item_profile_icon, 
					ctxt.getResources().getIdentifier(profile.getIconIdentifier(), "drawable", ctxt.getPackageName()));
		}
		else
		{
    		row.setImageViewBitmap(R.id.widget_profile_list_item_profile_icon, profile._iconBitmap);
		}
		row.setTextViewText(R.id.widget_profile_list_item_profile_name, profile._name);
		int red = 0xFF;
		int green = 0xFF;
		int blue = 0xFF;
		if (GlobalData.applicationWidgetListLightnessT.equals("0")) red = 0x00;
		if (GlobalData.applicationWidgetListLightnessT.equals("25")) red = 0x40;
		if (GlobalData.applicationWidgetListLightnessT.equals("50")) red = 0x80;
		if (GlobalData.applicationWidgetListLightnessT.equals("75")) red = 0xC0;
		if (GlobalData.applicationWidgetListLightnessT.equals("100")) red = 0xFF;
		green = red; blue = red;
		if (!GlobalData.applicationWidgetListHeader)
		{
			if (profile._checked)
			{
				row.setTextViewTextSize(R.id.widget_profile_list_item_profile_name, TypedValue.COMPLEX_UNIT_SP, 17);
		        if (GlobalData.applicationWidgetListIconColor.equals("1"))
					row.setTextColor(R.id.widget_profile_list_item_profile_name, Color.argb(0xFF, red, green, blue));
		        else
					row.setTextColor(R.id.widget_profile_list_item_profile_name, Color.parseColor("#33b5e5"));
			}
			else
			{
				row.setTextViewTextSize(R.id.widget_profile_list_item_profile_name, TypedValue.COMPLEX_UNIT_SP, 15);
				
		        if (GlobalData.applicationWidgetListIconColor.equals("1"))
		        	row.setTextColor(R.id.widget_profile_list_item_profile_name, Color.argb(0xCC, red, green, blue));
		        else
		        	row.setTextColor(R.id.widget_profile_list_item_profile_name, Color.argb(0xFF, red, green, blue));
			}
		}
		else
		{
			row.setTextColor(R.id.widget_profile_list_item_profile_name, Color.argb(0xFF, red, green, blue));
		}
		if (GlobalData.applicationWidgetListPrefIndicator)
			row.setImageViewBitmap(R.id.widget_profile_list_profile_pref_indicator, profile._preferencesIndicator);
		else
			row.setImageViewResource(R.id.widget_profile_list_profile_pref_indicator, R.drawable.ic_empty);

		Intent i=new Intent();
		Bundle extras=new Bundle();
    
		extras.putLong(GlobalData.EXTRA_PROFILE_ID, profile._id);
		extras.putInt(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_SHORTCUT);
		i.putExtras(extras);
		row.setOnClickFillInIntent(R.id.widget_profile_list_item, i);

		return(row);
	}

	public RemoteViews getLoadingView() {
		return(null);
	}
  
	public int getViewTypeCount() {
		return(1);
	}

	public long getItemId(int position) {
		return(position);
	}

	public boolean hasStableIds() {
		return(true);
	}

	public void onDataSetChanged() {
		ProfileListWidgetProvider.createProfilesDataWrapper();
		
		profileList = ProfileListWidgetProvider.profilesDataWrapper.getProfileList(DatabaseHandler.FILTER_TYPE_PROFILES_SHOW_IN_ACTIVATOR);
	}

}