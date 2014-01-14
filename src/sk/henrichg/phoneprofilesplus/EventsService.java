package sk.henrichg.phoneprofilesplus;

import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class EventsService extends IntentService
{
	public static final int ESP_START_EVENT = 1;
	public static final int ESP_PAUSE_EVENT = 2;
	public static final int ESP_STOP_EVENT = 3;
	
	public static final int ESP_RESTART_EVENTS = 99;

	public EventsService() {
		super("EventsService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Context context = getBaseContext();
		
		if (!GlobalData.getApplicationStarted(context))
			// application is not started
			return;
		
		if (!GlobalData.getGlobalEventsRuning(context))
			// events are globally stopped
			return;
		
		DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
		List<EventTimeline> eventTimelineList = dataWrapper.getEventTimelineList();

		Log.e("EventsService.onHandleIntent","eventTimelineList.size()="+eventTimelineList.size());
		
		int procedure = intent.getIntExtra(GlobalData.EXTRA_EVENTS_SERVICE_PROCEDURE, 0);
		int eventType = intent.getIntExtra(GlobalData.EXTRA_EVENT_TYPE, 0);
		
		Log.e("EventsService.onHandleIntent","procedure="+procedure);
		Log.e("EventsService.onHandleIntent","eventType="+eventType);
		
		switch (eventType)
		{
			case Event.ETYPE_TIME:
				// in intent is event_id
				long event_id = intent.getLongExtra(GlobalData.EXTRA_EVENT_ID, 0);
				Log.e("EventsService.onHandleIntent","event_id="+event_id);

				Event event = dataWrapper.getEventById(event_id);
				doEvent_Time(dataWrapper, eventTimelineList, event, procedure);
				
				// completting wake
				EventsAlarmBroadcastReceiver.completeWakefulIntent(intent);
				
				break;
			default:
				break;
		}
		
		dataWrapper.invalidateDataWrapper();
		
	}

	private void doEvent_Time(DataWrapper dataWrapper, 
								List<EventTimeline> eventTimelineList,
								Event event, int procedure)
	{
		if (event == null)
			return;
		
		switch (procedure)
		{
			case ESP_RESTART_EVENTS:
				dataWrapper.firstStartEvents();
				break;
			case ESP_START_EVENT:
				event.startEvent(dataWrapper, eventTimelineList, false);
				break;
			case ESP_PAUSE_EVENT:
				event.pauseEvent(dataWrapper, eventTimelineList, true, false, false);
				break;
			case ESP_STOP_EVENT:
				event.stopEvent(dataWrapper, eventTimelineList, true, false, true);
				break;
			default:
				break;
		}
	}
}
