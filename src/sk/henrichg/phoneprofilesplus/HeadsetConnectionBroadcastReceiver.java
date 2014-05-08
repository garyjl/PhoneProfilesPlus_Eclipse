package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.content.WakefulBroadcastReceiver;

public class HeadsetConnectionBroadcastReceiver extends WakefulBroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "headsetConnection";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		GlobalData.logE("#### HeadsetConnectionBroadcastReceiver.onReceive","xxx");
		
		GlobalData.loadPreferences(context);
		
		if (GlobalData.getGlobalEventsRuning(context))
		{
			DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
			boolean peripheralEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_PERIPHERAL) > 0;
			dataWrapper.invalidateDataWrapper();
	
			if (peripheralEventsExists)
			{
				boolean connectedHeadphones = (intent.getIntExtra("state", 0) == 1);
				boolean connectedMicrophone = (intent.getIntExtra("microphone", 0) == 1);
				
				SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putBoolean(GlobalData.PREF_EVENT_HEADSET_CONNECTED, connectedHeadphones);
				editor.putBoolean(GlobalData.PREF_EVENT_HEADSET_MICROPHONE, connectedMicrophone);
				editor.commit();
				
				// start service
				Intent eventsServiceIntent = new Intent(context, EventsService.class);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_EVENT_ID, 0L);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
				startWakefulService(context, eventsServiceIntent);
			}
			
		}
	}
}
