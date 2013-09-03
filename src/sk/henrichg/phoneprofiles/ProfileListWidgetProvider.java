package sk.henrichg.phoneprofiles;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

@SuppressLint("NewApi")
public class ProfileListWidgetProvider extends AppWidgetProvider {

	public static ProfilesDataWrapper profilesDataWrapper;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		Log.e("ProfileListWidgetProvider.onUpdate","xxx");
		
		GlobalData.loadPreferences(GlobalData.context);

		if (profilesDataWrapper == null)
			profilesDataWrapper = new ProfilesDataWrapper(GlobalData.context, true, true, false, false);
		ProfileListWidgetProvider.profilesDataWrapper.reloadProfilesData();
		
		for (int i=0; i<appWidgetIds.length; i++)
		{
			Intent svcIntent=new Intent(ctxt, ProfileListWidgetService.class);
			      
			svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
			      
			RemoteViews widget;
			if (GlobalData.applicationWidgetListHeader)
			{
				if (GlobalData.applicationWidgetListPrefIndicator)
					widget=new RemoteViews(ctxt.getPackageName(), R.layout.profile_list_widget);
				else
					widget=new RemoteViews(ctxt.getPackageName(), R.layout.profile_list_widget_no_indicator);
			}
			else
				widget=new RemoteViews(ctxt.getPackageName(), R.layout.profile_list_widget_no_header);

			// set background
			int red = 0;
			int green = 0;
			int blue = 0;
			if (GlobalData.applicationWidgetListLightnessB.equals("0")) red = 0x00;
			if (GlobalData.applicationWidgetListLightnessB.equals("25")) red = 0x40;
			if (GlobalData.applicationWidgetListLightnessB.equals("50")) red = 0x80;
			if (GlobalData.applicationWidgetListLightnessB.equals("75")) red = 0xC0;
			if (GlobalData.applicationWidgetListLightnessB.equals("100")) red = 0xFF;
			green = red; blue = red;
			int alpha = 0x40;
			if (GlobalData.applicationWidgetListBackground.equals("0")) alpha = 0x00;
			if (GlobalData.applicationWidgetListBackground.equals("25")) alpha = 0x40;
			if (GlobalData.applicationWidgetListBackground.equals("50")) alpha = 0x80;
			if (GlobalData.applicationWidgetListBackground.equals("75")) alpha = 0xC0;
			if (GlobalData.applicationWidgetListBackground.equals("100")) alpha = 0xFF;
			widget.setImageViewBitmap(R.id.widget_profile_list_background, getBackground(Color.argb(alpha, red, green, blue)));
			
			// header
			if (GlobalData.applicationWidgetListHeader)
			{
				Profile profile = profilesDataWrapper.getActivatedProfile();
	
				boolean isIconResourceID;
				String iconIdentifier;
				String profileName;
				if (profile != null)
				{
					isIconResourceID = profile.getIsIconResourceID();
					iconIdentifier = profile.getIconIdentifier();
					profileName = profile._name;
				}
				else
				{
					isIconResourceID = true;
					iconIdentifier = GUIData.PROFILE_ICON_DEFAULT;
					profileName = ctxt.getResources().getString(R.string.profile_name_default);
				}
		        if (isIconResourceID)
		        {
		        	int iconResource = ctxt.getResources().getIdentifier(iconIdentifier, "drawable", ctxt.getPackageName());
		        	widget.setImageViewResource(R.id.widget_profile_list_header_profile_icon, iconResource);
		        }
		        else
		        {
		        	widget.setImageViewBitmap(R.id.widget_profile_list_header_profile_icon, profile._iconBitmap);
		        }
				widget.setTextViewText(R.id.widget_profile_list_header_profile_name, profileName);
				if (GlobalData.applicationWidgetListPrefIndicator)
				{
					if (profile == null)
						widget.setImageViewBitmap(R.id.widget_profile_list_header_profile_pref_indicator, null);
					else
						widget.setImageViewBitmap(R.id.widget_profile_list_header_profile_pref_indicator, profile._preferencesIndicator);
				}
				red = 0xFF;
				green = 0xFF;
				blue = 0xFF;
				if (GlobalData.applicationWidgetListLightnessT.equals("0")) red = 0x00;
				if (GlobalData.applicationWidgetListLightnessT.equals("25")) red = 0x40;
				if (GlobalData.applicationWidgetListLightnessT.equals("50")) red = 0x80;
				if (GlobalData.applicationWidgetListLightnessT.equals("75")) red = 0xC0;
				if (GlobalData.applicationWidgetListLightnessT.equals("100")) red = 0xFF;
				green = red; blue = red;
				widget.setImageViewBitmap(R.id.widget_profile_list_header_separator, getBackground(Color.argb(0x40, red, green, blue)));
			}
			////////////////////////////////////////////////
			      
			widget.setRemoteAdapter(appWidgetIds[i], R.id.widget_profile_list, svcIntent);

			Intent clickIntent=new Intent(ctxt, BackgroundActivateProfileActivity.class);
			PendingIntent clickPI=PendingIntent.getActivity(ctxt, 0,
			                                            clickIntent,
			                                            PendingIntent.FLAG_UPDATE_CURRENT);
			      
			widget.setPendingIntentTemplate(R.id.widget_profile_list, clickPI);

			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
		}
			    
		super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		String action = intent.getAction();
		if ((action != null) &&
		    (action.equalsIgnoreCase("android.appwidget.action.APPWIDGET_UPDATE")))
		{
			Log.e("ProfileListWidgetProvider.onReceive","xxx");
			updateWidget(context);
		}
	}

	private void updateWidget(Context context) {
		if (profilesDataWrapper == null)
			profilesDataWrapper = new ProfilesDataWrapper(GlobalData.context, true, true, false, false);
		
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
	    int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, ProfileListWidgetProvider.class));
	    
	    onUpdate(context, appWidgetManager, appWidgetIds);
	    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_profile_list);
	}	
	
	
	public static Bitmap getBackground (int bgcolor)
	{
	try
	    {
	        Bitmap.Config config = Bitmap.Config.ARGB_8888; // Bitmap.Config.ARGB_8888 Bitmap.Config.ARGB_4444 to be used as these two config constant supports transparency
	        Bitmap bitmap = Bitmap.createBitmap(2, 2, config); // Create a Bitmap
	 
	        Canvas canvas =  new Canvas(bitmap); // Load the Bitmap to the Canvas
	        canvas.drawColor(bgcolor); //Set the color
	 
	        return bitmap;
	    }
	    catch (Exception e)
	    {
	        return null;
	    }
	}	
}
