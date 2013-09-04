package sk.henrichg.phoneprofiles;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class ActivateProfileWidget extends AppWidgetProvider {
	
	private ProfilesDataWrapper profilesDataWrapper;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		
		GlobalData.loadPreferences(GlobalData.context);
		
		profilesDataWrapper = new ProfilesDataWrapper(GlobalData.context, true, false, false, false);
		
		Profile profile = profilesDataWrapper.getActivatedProfile();

		// ziskanie vsetkych wigetov tejtor triedy na plochach lauchera
		ComponentName thisWidget = new ComponentName(context, ActivateProfileWidget.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

		// prechadzame vsetky ziskane widgety
		for (int widgetId : allWidgetIds)
		{
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
				profileName = context.getResources().getString(R.string.profile_name_default);
			}
			
			// priprava view-u na aktualizacia widgetu
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.activate_profile_widget);
	        if (isIconResourceID)
	        {
	        	//remoteViews.setImageViewResource(R.id.activate_profile_widget_icon, 0);
	        	int iconResource = context.getResources().getIdentifier(iconIdentifier, "drawable", context.getPackageName());
	        	remoteViews.setImageViewResource(R.id.activate_profile_widget_icon, iconResource);
	        }
	        else
	        {
	    		//Resources resources = context.getResources();
	        	//remoteViews.setImageViewBitmap(R.id.activate_profile_widget_icon, null);
	    		//int height = (int) resources.getDimension(android.R.dimen.app_icon_size);
	    		//int width = (int) resources.getDimension(android.R.dimen.app_icon_size);
	    		//Bitmap bitmap = BitmapResampler.resample(iconIdentifier, width, height);
	        	//remoteViews.setImageViewBitmap(R.id.activate_profile_widget_icon, bitmap);
	        	remoteViews.setImageViewBitmap(R.id.activate_profile_widget_icon, profile._iconBitmap);
	        }
			remoteViews.setTextViewText(R.id.activate_profile_widget_name, profileName);
			
			// konfiguracia, ze ma spustit hlavnu aktivitu zoznamu profilov, ked kliknme na widget
			Intent intent = new Intent(context, ActivateProfileActivity.class);
			intent.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_WIDGET);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.activate_profile_widget_icon, pendingIntent);
			remoteViews.setOnClickPendingIntent(R.id.activate_profile_widget_name, pendingIntent);
			
			// aktualizacia widgetu
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
		
	}
}
