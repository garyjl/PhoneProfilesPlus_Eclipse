package sk.henrichg.phoneprofiles;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

@SuppressLint("NewApi")
public class ProfileListWidgetProvider extends AppWidgetProvider {

	public static ProfilesDataWrapper profilesDataWrapper;
	
	private boolean isLargeLayout;
	private boolean isKeyguard;
	
	@SuppressWarnings("deprecation")
    private RemoteViews buildLayout(Context ctxt, AppWidgetManager appWidgetManager, int appWidgetId, boolean largeLayout)
	{
		Intent svcIntent=new Intent(ctxt, ProfileListWidgetService.class);
		      
		svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
		      
		RemoteViews widget;
		
		if (largeLayout)
		{
			if (GlobalData.applicationWidgetListHeader)
			{
				if (GlobalData.applicationWidgetListPrefIndicator)
					widget=new RemoteViews(ctxt.getPackageName(), R.layout.profile_list_widget);
				else
					widget=new RemoteViews(ctxt.getPackageName(), R.layout.profile_list_widget_no_indicator);
			}
			else
				widget=new RemoteViews(ctxt.getPackageName(), R.layout.profile_list_widget_no_header);
		}
		else
		{
			if (isKeyguard)
			{
				if (GlobalData.applicationWidgetListPrefIndicator)
					widget=new RemoteViews(ctxt.getPackageName(), R.layout.profile_list_widget_small_keyguard);
				else
					widget=new RemoteViews(ctxt.getPackageName(), R.layout.profile_list_widget_small_no_indicator_keyguard);
			}
			else
			{
				if (GlobalData.applicationWidgetListPrefIndicator)
					widget=new RemoteViews(ctxt.getPackageName(), R.layout.profile_list_widget_small);
				else
					widget=new RemoteViews(ctxt.getPackageName(), R.layout.profile_list_widget_small_no_indicator);
			}
		}

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
		widget.setInt(R.id.widget_profile_list_root, "setBackgroundColor", Color.argb(alpha, red, green, blue));
		
		// header
		if (GlobalData.applicationWidgetListHeader || (!largeLayout))
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
				profileName = ctxt.getResources().getString(R.string.profiles_header_profile_name_no_activated);
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
	        if (GlobalData.applicationWidgetListIconColor.equals("1"))
	        {
	    		red = 0xFF;
	    		green = 0xFF;
	    		blue = 0xFF;
	    		if (GlobalData.applicationWidgetListLightnessT.equals("0")) red = 0x00;
	    		if (GlobalData.applicationWidgetListLightnessT.equals("25")) red = 0x40;
	    		if (GlobalData.applicationWidgetListLightnessT.equals("50")) red = 0x80;
	    		if (GlobalData.applicationWidgetListLightnessT.equals("75")) red = 0xC0;
	    		if (GlobalData.applicationWidgetListLightnessT.equals("100")) red = 0xFF;
	    		green = red; blue = red;
	        	widget.setTextColor(R.id.widget_profile_list_header_profile_name, Color.argb(0xFF, red, green, blue));
	        }
	        else
	        {
				widget.setTextColor(R.id.widget_profile_list_header_profile_name, Color.parseColor("#33b5e5"));
	        }
			widget.setTextViewText(R.id.widget_profile_list_header_profile_name, profileName);
			if (GlobalData.applicationWidgetListPrefIndicator)
			{
				if (profile == null)
					widget.setImageViewResource(R.id.widget_profile_list_header_profile_pref_indicator, R.drawable.ic_empty);
				else
	        		widget.setImageViewBitmap(R.id.widget_profile_list_header_profile_pref_indicator, profile._preferencesIndicator);
			}
			if (largeLayout)
			{	
				red = 0xFF;
				green = 0xFF;
				blue = 0xFF;
				if (GlobalData.applicationWidgetListLightnessT.equals("0")) red = 0x00;
				if (GlobalData.applicationWidgetListLightnessT.equals("25")) red = 0x40;
				if (GlobalData.applicationWidgetListLightnessT.equals("50")) red = 0x80;
				if (GlobalData.applicationWidgetListLightnessT.equals("75")) red = 0xC0;
				if (GlobalData.applicationWidgetListLightnessT.equals("100")) red = 0xFF;
				green = red; blue = red;
				widget.setInt(R.id.widget_profile_list_header_separator, "setBackgroundColor", Color.argb(alpha, red, green, blue));
			}
	        if (GlobalData.applicationWidgetListIconColor.equals("1"))
	        {
				int monochromeValue = 0xFF;
				if (GlobalData.applicationWidgetListIconLightness.equals("0")) monochromeValue = 0x00;
				if (GlobalData.applicationWidgetListIconLightness.equals("25")) monochromeValue = 0x40;
				if (GlobalData.applicationWidgetListIconLightness.equals("50")) monochromeValue = 0x80;
				if (GlobalData.applicationWidgetListIconLightness.equals("75")) monochromeValue = 0xC0;
				if (GlobalData.applicationWidgetListIconLightness.equals("100")) monochromeValue = 0xFF;
	        	
	        	Bitmap bitmap = BitmapFactory.decodeResource(ctxt.getResources(), R.drawable.ic_profile_activated);
	        	bitmap = BitmapManipulator.monochromeBitmap(bitmap, monochromeValue, ctxt);
				widget.setImageViewBitmap(R.id.widget_profile_list_header_profile_activated, bitmap);
	        }
	        else
	        {
	        	widget.setImageViewResource(R.id.widget_profile_list_header_profile_activated, R.drawable.ic_profile_activated);
	        }
		}
		////////////////////////////////////////////////
		
		// clicks
		if (largeLayout)
		{
			//if (!isKeyguard)
			//{
				Intent intent = new Intent(ctxt, EditorProfilesActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(ctxt, 1, intent, 
	                    									PendingIntent.FLAG_UPDATE_CURRENT);
				widget.setOnClickPendingIntent(R.id.widget_profile_list_header, pendingIntent);
			//}

			widget.setRemoteAdapter(appWidgetId, R.id.widget_profile_list, svcIntent);
			
			Intent clickIntent=new Intent(ctxt, BackgroundActivateProfileActivity.class);
			PendingIntent clickPI=PendingIntent.getActivity(ctxt, 0,
			                                            clickIntent,
			                                            PendingIntent.FLAG_UPDATE_CURRENT);
			      
			widget.setPendingIntentTemplate(R.id.widget_profile_list, clickPI);

		}
		else
		{
			//if (!isKeyguard)
			//{
				Intent intent = new Intent(ctxt, ActivateProfileActivity.class);
				intent.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_WIDGET);
				PendingIntent pendingIntent = PendingIntent.getActivity(ctxt, 1, intent,  
															PendingIntent.FLAG_UPDATE_CURRENT);
				widget.setOnClickPendingIntent(R.id.widget_profile_list_header, pendingIntent);
			//}
		}
		
		return widget;
	}
	
	public static void createProfilesDataWrapper()
	{
		if (profilesDataWrapper == null)
		{
			int monochromeValue = 0xFF;
			if (GlobalData.applicationWidgetListIconLightness.equals("0")) monochromeValue = 0x00;
			if (GlobalData.applicationWidgetListIconLightness.equals("25")) monochromeValue = 0x40;
			if (GlobalData.applicationWidgetListIconLightness.equals("50")) monochromeValue = 0x80;
			if (GlobalData.applicationWidgetListIconLightness.equals("75")) monochromeValue = 0xC0;
			if (GlobalData.applicationWidgetListIconLightness.equals("100")) monochromeValue = 0xFF;
			
			profilesDataWrapper = new ProfilesDataWrapper(GlobalData.context, true,  
														GlobalData.applicationWidgetListIconColor.equals("1"), 
														monochromeValue);
		}
	}
	
	@Override
	public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		//Log.e("ProfileListWidgetProvider.onUpdate","xxx");
		
		GlobalData.loadPreferences(GlobalData.context);

		createProfilesDataWrapper();

		ProfileListWidgetProvider.profilesDataWrapper.reloadProfilesData();
		
		for (int i=0; i<appWidgetIds.length; i++)
		{
			Bundle myOptions;
			if (android.os.Build.VERSION.SDK_INT >= 16)
				myOptions = appWidgetManager.getAppWidgetOptions (appWidgetIds[i]);
			else
				myOptions = null;
	        setLayoutParams(ctxt, appWidgetManager, appWidgetIds[i], myOptions);

			RemoteViews widget = buildLayout(ctxt, appWidgetManager, appWidgetIds[i], isLargeLayout);			
			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
		}
			    
		super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		//Log.e("ProfileListWidgetProvider.onReceive","xxx");
		
		String action = intent.getAction();
		if ((action != null) &&
		    (action.equalsIgnoreCase("android.appwidget.action.APPWIDGET_UPDATE")))
		{
			updateWidget(context);
		}
	}
	
	private void setLayoutParams(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId, Bundle newOptions)
	{
		int minHeight;
		if (newOptions != null)
		{
			// Get the value of OPTION_APPWIDGET_HOST_CATEGORY
			int category = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1);
			// If the value is WIDGET_CATEGORY_KEYGUARD, it's a lockscreen widget
			isKeyguard = category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD;
	
	        //int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
	        //int maxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
	        minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
	        //int maxHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
		}
		else
		{
			isKeyguard = false;
			minHeight = appWidgetManager.getAppWidgetInfo(appWidgetId).minHeight;
		}

        if (isKeyguard)
        {
	        if (minHeight < 250) {
	            isLargeLayout = false;
	        } else {
	            isLargeLayout = true;
	        }
        }
        else
        {
	        if (minHeight < 110) {
	            isLargeLayout = false;
	        } else {
	            isLargeLayout = true;
	        }
    	}
	}
	
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId, Bundle newOptions) 
    {
		//Log.e("ProfileListWidgetProvider.onAppWidgetOptionsChanged","xxx");

        setLayoutParams(context, appWidgetManager, appWidgetId, newOptions);
        RemoteViews layout;
        layout = buildLayout(context, appWidgetManager, appWidgetId, isLargeLayout);
        appWidgetManager.updateAppWidget(appWidgetId, layout);
        if (isLargeLayout)
        {
	    	appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_profile_list);
        }
    }	

	private void updateWidget(Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
	    int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, ProfileListWidgetProvider.class));

	    if (profilesDataWrapper != null)
	    {
	    	profilesDataWrapper.invalidateProfileList();
	    	profilesDataWrapper = null;
	    }
	    
    	onUpdate(context, appWidgetManager, appWidgetIds);
	    if (isLargeLayout)
	    	appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_profile_list);
	}	
	
}
