package sk.henrichg.phoneprofilesplus;

import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class EventsService extends IntentService
{
	Context context;
	DataWrapper dataWrapper;
	List<EventTimeline> eventTimelineList;
	String broadcastReceiverType;
	
	public static int callEventType = PhoneCallBroadcastReceiver.CALL_EVENT_UNDEFINED;
	public static String phoneNumber = "";
	
	public EventsService() {
		super("EventsService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		context = getBaseContext();

		GlobalData.logE("EventsService.onHandleIntent","-- start --------------------------------");

		if (!GlobalData.getApplicationStarted(context))
			// application is not started
			return;
		
		if (!GlobalData.getGlobalEventsRuning(context))
			// events are globally stopped
			return;

		GlobalData.loadPreferences(context);
		
		dataWrapper = new DataWrapper(context, true, false, 0);
		
		// create a handler to post messages to the main thread
	    Handler handler = new Handler(getMainLooper());
	    dataWrapper.setToastHandler(handler);
		
		eventTimelineList = dataWrapper.getEventTimelineList();

		GlobalData.logE("EventsService.onHandleIntent","eventTimelineList.size()="+eventTimelineList.size());
		
		broadcastReceiverType = intent.getStringExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE);
		
		GlobalData.logE("EventsService.onHandleIntent","broadcastReceiverType="+broadcastReceiverType);
		
		// in intent is event_id
		long event_id = intent.getLongExtra(GlobalData.EXTRA_EVENT_ID, 0L);
		GlobalData.logE("EventsService.onHandleIntent","event_id="+event_id);
		Event event = dataWrapper.getEventById(event_id);
		
		// in intnet are phone call parameters
		callEventType = intent.getIntExtra(GlobalData.EXTRA_EVENT_CALL_EVENT_TYPE, PhoneCallBroadcastReceiver.CALL_EVENT_UNDEFINED);
		phoneNumber = intent.getStringExtra(GlobalData.EXTRA_EVENT_CALL_PHONE_NUMBER);
		
		if (event == null)
		{
			List<Event> eventList = dataWrapper.getEventList();
			dataWrapper.sortEventsByPriority();
			
			for (Event _event : eventList)
			{
				GlobalData.logE("EventsService.onHandleIntent","event._id="+_event._id);
				GlobalData.logE("EventsService.onHandleIntent","event.getStatus()="+_event.getStatus());
				
				if (_event.getStatus() != Event.ESTATUS_STOP)
					// nespusti, ak uz je v takom stave
					dataWrapper.doEventService(_event, false, 
							!broadcastReceiverType.equals(RestartEventsBroadcastReceiver.BROADCAST_RECEIVER_TYPE));
			}
		}
		else
		if (event.getStatus() != Event.ESTATUS_STOP)
			dataWrapper.doEventService(event, false, true);

		doEndService(intent);

		dataWrapper.invalidateDataWrapper();

		GlobalData.logE("EventsService.onHandleIntent","-- end --------------------------------");
		
	}

	private void doEndService(Intent intent)
	{
		// completting wake
		if (broadcastReceiverType.equals(RestartEventsBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			RestartEventsBroadcastReceiver.completeWakefulIntent(intent);
		if (broadcastReceiverType.equals(EventsAlarmBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			EventsAlarmBroadcastReceiver.completeWakefulIntent(intent);
		else
		if (broadcastReceiverType.equals(BatteryEventsAlarmBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			BatteryEventsAlarmBroadcastReceiver.completeWakefulIntent(intent);
		else
		if (broadcastReceiverType.equals(PowerConnectionReceiver.BROADCAST_RECEIVER_TYPE))
			PowerConnectionReceiver.completeWakefulIntent(intent);
		else
		if (broadcastReceiverType.equals(PhoneCallBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			PhoneCallBroadcastReceiver.completeWakefulIntent(intent);
	}
}
