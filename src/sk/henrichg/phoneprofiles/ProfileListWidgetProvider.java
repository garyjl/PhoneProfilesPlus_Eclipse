package sk.henrichg.phoneprofiles;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;


public class ProfileListWidgetProvider extends AppWidgetProvider {

	public static ProfilesDataWrapper profilesDataWrapper;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		
		GlobalData.loadPreferences(GlobalData.context);
		
		profilesDataWrapper = new ProfilesDataWrapper(GlobalData.context, true, true, false, false);
		
		for (int i=0; i<appWidgetIds.length; i++)
		{
			Intent svcIntent=new Intent(ctxt, ProfileListWidgetService.class);
			      
			svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
			      
			RemoteViews widget=new RemoteViews(ctxt.getPackageName(), R.layout.profile_list_widget);
			      
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
			updateWidget(context);
		}
	}

	private void updateWidget(Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
	    int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, ProfileListWidgetProvider.class));
	    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_profile_list);
	}	
	
}
