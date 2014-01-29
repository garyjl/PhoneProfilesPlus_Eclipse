package sk.henrichg.phoneprofilesplus;

import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class EventsService extends IntentService
{
	Context context;
	DataWrapper dataWrapper;
	List<EventTimeline> eventTimelineList;
	int procedure;
	int eventType;
	
	public static final int ESP_START_EVENT = 1;
	public static final int ESP_PAUSE_EVENT = 2;
	public static final int ESP_STOP_EVENT = 3;
	
	public static final int ESP_RESTART_EVENTS = 99;

	public EventsService() {
		super("EventsService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		context = getBaseContext();
		
		if (!GlobalData.getApplicationStarted(context))
			// application is not started
			return;
		
		if (!GlobalData.getGlobalEventsRuning(context))
			// events are globally stopped
			return;
		
		dataWrapper = new DataWrapper(context, false, false, 0);
		eventTimelineList = dataWrapper.getEventTimelineList();

		GlobalData.logE("EventsService.onHandleIntent","eventTimelineList.size()="+eventTimelineList.size());
		
		procedure = intent.getIntExtra(GlobalData.EXTRA_EVENTS_SERVICE_PROCEDURE, 0);
		eventType = intent.getIntExtra(GlobalData.EXTRA_EVENT_TYPE, 0);
		
		GlobalData.logE("EventsService.onHandleIntent","procedure="+procedure);
		GlobalData.logE("EventsService.onHandleIntent","eventType="+eventType);
		
		switch (eventType)
		{
			case Event.ETYPE_TIME:
				// in intent is event_id
				long event_id = intent.getLongExtra(GlobalData.EXTRA_EVENT_ID, 0);
				GlobalData.logE("EventsService.onHandleIntent","event_id="+event_id);

				Event event = dataWrapper.getEventById(event_id);
				doEventTime(dataWrapper, eventTimelineList, event, procedure);
				
				break;
			default:
				break;
		}
		
		doEndService();
		
		dataWrapper.invalidateDataWrapper();
		
	}

	private void doEventTime(DataWrapper dataWrapper, 
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
	
	private void doEndService()
	{
		// refresh GUI
		Intent intent = new Intent();
	    intent.setAction(RefreshGUIBroadcastReceiver.INTENT_REFRESH_GUI);
		context.sendBroadcast(intent);

		switch (eventType)
		{
			case Event.ETYPE_TIME:
				// completting wake
				EventsAlarmBroadcastReceiver.completeWakefulIntent(intent);
				
				break;
			default:
				break;
		}
	}
}
