package sk.henrichg.phoneprofilesplus;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

@SuppressLint("NewApi")
public class ProfileListWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

	private ProfilesDataWrapper profilesDataWrapper;

	private Context ctxt=null;
	//private int appWidgetId;
	private List<Profile> profileList;

	public ProfileListWidgetFactory(Context ctxt, Intent intent) {
		this.ctxt=ctxt;
		/*appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                       AppWidgetManager.INVALID_APPWIDGET_ID);*/
	}
  
	public void createProfilesDataWrapper()
	{
		GlobalData.loadPreferences(GlobalData.context);
		
		int monochromeValue = 0xFF;
		if (GlobalData.applicationWidgetListIconLightness.equals("0")) monochromeValue = 0x00;
		if (GlobalData.applicationWidgetListIconLightness.equals("25")) monochromeValue = 0x40;
		if (GlobalData.applicationWidgetListIconLightness.equals("50")) monochromeValue = 0x80;
		if (GlobalData.applicationWidgetListIconLightness.equals("75")) monochromeValue = 0xC0;
		if (GlobalData.applicationWidgetListIconLightness.equals("100")) monochromeValue = 0xFF;

		if (profilesDataWrapper == null)
		{
			profilesDataWrapper = new ProfilesDataWrapper(GlobalData.context, true,  
														GlobalData.applicationWidgetListIconColor.equals("1"), 
														monochromeValue);
		}
		else
		{
			profilesDataWrapper.setParameters(true, GlobalData.applicationWidgetListIconColor.equals("1"), 
														monochromeValue);
		}
	}
	
	public void onCreate() {
		//Log.e("ProfileListWidgetFactory.onCreate","xxx");
	}
  
	public void onDestroy() {
		// no-op
	}

	public int getCount() {

		int count = 0;
		for (Profile profile : profileList)
		{
			if (profile._showInActivator)
				++count;
		}
		return count;
	}

	private Profile getItem(int position)
	{
		if (getCount() == 0)
			return null;
		else
		{
			Profile _profile = null;
			
			int pos = -1;
			for (Profile profile : profileList)
			{
				if (profile._showInActivator)
					++pos;

				if (pos == position)
		        {
		        	_profile = profile;
		        	break;
		        }
			}
			
			return _profile;
		}
	}
	
	public RemoteViews getViewAt(int position) {
		//Log.e("ProfileListWidgetFactory.getViewAt","xxx");
		
		
		RemoteViews row=new RemoteViews(ctxt.getPackageName(), R.layout.profile_list_widget_item);
    
		Profile profile = getItem(position);

		if (profile.getIsIconResourceID())
		{
			row.setImageViewResource(R.id.widget_profile_list_item_profile_icon, 
					ctxt.getResources().getIdentifier(profile.getIconIdentifier(), "drawable", ctxt.getPackageName()));
		}
		else
		{
    		row.setImageViewBitmap(R.id.widget_profile_list_item_profile_icon, profile._iconBitmap);
		}
		if ((!GlobalData.applicationWidgetListHeader) && (profile._checked))
			row.setTextViewText(R.id.widget_profile_list_item_profile_name, 
					// hm, interesting, how to set bold style for RemoteView text ;-)
					new SpannableStringBuilder(Html.fromHtml("<b>"+profile._name+"</b>"))
					);
		else
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
		//Log.e("ProfileListWidgetFactory.onDataSetChanged","xxx");

		createProfilesDataWrapper();
		
		profilesDataWrapper.invalidateProfileList();
		profileList = profilesDataWrapper.getProfileList();
		//Log.e("ProfileListWidgetFactory.onDataSetChanged",""+profileList);
		
	    Collections.sort(profileList, new ProfileComparator());
	}
	
	private class ProfileComparator implements Comparator<Profile> {

		public int compare(Profile lhs, Profile rhs) {
		    int res = lhs._porder - rhs._porder;
	        return res;
	    }
	}

}
