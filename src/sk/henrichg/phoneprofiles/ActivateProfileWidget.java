package sk.henrichg.phoneprofiles;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

public class ActivateProfileWidget extends AppWidgetProvider {
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		// ziskanie vsetkych wigetov tejtor triedy na plochach lauchera
		ComponentName thisWidget = new ComponentName(context, ActivateProfileWidget.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		
		// prechadzame vsetky ziskane widgety
		for (int widgetId : allWidgetIds)
		{
			DatabaseHandler databaseHandler = new DatabaseHandler(context);
			Profile profile = databaseHandler.getActivatedProfile();
			boolean isIconResourceID;
			String iconIdentifier;
			String profileName;
			if (profile != null)
			{
				isIconResourceID = profile.getIsIconResourceID();
				iconIdentifier = profile.getIconIdentifier();
				profileName = profile.getName();
			}
			else
			{
				isIconResourceID = true;
				iconIdentifier = PhoneProfilesActivity.PROFILE_ICON_DEFAULT;
				profileName = context.getResources().getString(R.string.profile_name_default);
			}
			
			// priprava view-u na aktualizacia widgetu
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.activate_profile_widget);
	        if (isIconResourceID)
	        {
	        	int iconResource = context.getResources().getIdentifier(iconIdentifier, "drawable", context.getPackageName());
	        	remoteViews.setImageViewResource(R.id.activate_profile_widget_icon, iconResource);
	        }
	        else
	        {
	        	remoteViews.setImageViewBitmap(R.id.activate_profile_widget_icon, BitmapFactory.decodeFile(iconIdentifier));
	        }
			remoteViews.setTextViewText(R.id.activate_profile_widget_name, profileName);
			
			// konfiguracia, ze ma spustit hlavnu aktivitu zoznamu profilov, ked kliknme na widget
			Intent intent = new Intent(context, PhoneProfilesActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
			remoteViews.setOnClickPendingIntent(R.id.activate_profile_widget_icon, pendingIntent);
			remoteViews.setOnClickPendingIntent(R.id.activate_profile_widget_name, pendingIntent);
			
			// aktualizacia widgetu
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
		
	}
}
