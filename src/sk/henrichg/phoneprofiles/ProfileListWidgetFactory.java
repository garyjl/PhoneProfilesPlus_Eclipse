package sk.henrichg.phoneprofiles;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class ProfileListWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

	private Context ctxt=null;
	//private int appWidgetId;
	private List<Profile> profileList;

	public ProfileListWidgetFactory(Context ctxt, Intent intent) {
		this.ctxt=ctxt;
		/*appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                       AppWidgetManager.INVALID_APPWIDGET_ID);*/
		ProfileListWidgetProvider.profilesDataWrapper.getProfileListForActivator();
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
			int iconResource = ctxt.getResources().getIdentifier(profile.getIconIdentifier(), "drawable", ctxt.getPackageName());
			Bitmap icon = BitmapFactory.decodeResource(ctxt.getResources(), iconResource);
			row.setImageViewBitmap(R.id.widget_profile_list_item_profile_icon, icon);
		}
		row.setTextViewText(R.id.widget_profile_list_item_profile_name, profile._name);
        row.setImageViewBitmap(R.id.widget_profile_list_profile_pref_indicator, profile._preferencesIndicator);

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
		ProfileListWidgetProvider.profilesDataWrapper.reloadProfilesData();
		profileList = ProfileListWidgetProvider.profilesDataWrapper.getProfileList();
	}

}