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
	String broadcastReceiverType;
	
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
		
		broadcastReceiverType = intent.getStringExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE);
		
		GlobalData.logE("EventsService.onHandleIntent","broadcastReceiverType="+broadcastReceiverType);
		
		List<Event> eventList = dataWrapper.getEventList();
		dataWrapper.sortEventsByPriority();
		
		if (broadcastReceiverType.equals(CalendarProviderChangedBroadcastReceiver.BROADCAST_RECEIVER_TYPE) ||
			broadcastReceiverType.equals(SearchCalendarEventsBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
		{
			// search for calendar events
			GlobalData.logE("EventsService.onHandleIntent","search for calendar events");
			for (Event _event : eventList)
			{
				if (_event._eventPreferencesCalendar._enabled)
				{
					GlobalData.logE("EventsService.onHandleIntent","event._id="+_event._id);
					_event._eventPreferencesCalendar.searchEvent(context);
				}
			}
		}
		
		boolean isRestart = (broadcastReceiverType.equals(RestartEventsBroadcastReceiver.BROADCAST_RECEIVER_TYPE) ||
							 broadcastReceiverType.equals(CalendarProviderChangedBroadcastReceiver.BROADCAST_RECEIVER_TYPE) ||
							 broadcastReceiverType.equals(SearchCalendarEventsBroadcastReceiver.BROADCAST_RECEIVER_TYPE));

		GlobalData.logE("EventsService.onHandleIntent","isRestart="+isRestart);
		
		if (isRestart)
		{
			// 1. pause events
			for (Event _event : eventList)
			{
				GlobalData.logE("EventsService.onHandleIntent","state PAUSE");
				GlobalData.logE("EventsService.onHandleIntent","event._id="+_event._id);
				GlobalData.logE("EventsService.onHandleIntent","event.getStatus()="+_event.getStatus());
				
				if (_event.getStatus() != Event.ESTATUS_STOP)
					// len pauzuj eventy
					// pauzuj aj ked uz je zapauznuty
					dataWrapper.doEventService(_event, true, true, false); 
			}
			// 2. start events in timeline order
			List<EventTimeline> eventTimelineList = dataWrapper.getEventTimelineList();
			GlobalData.logE("EventsService.onHandleIntent","eventTimeLineList.size()="+eventTimelineList.size());
			for (EventTimeline eventTimeline : eventTimelineList)
			{
				Event _event = dataWrapper.getEventById(eventTimeline._fkEvent);
				if (_event != null)
				{
					GlobalData.logE("EventsService.onHandleIntent","state RUNNING from eventTimeLine");
					GlobalData.logE("EventsService.onHandleIntent","event._id="+_event._id);
					GlobalData.logE("EventsService.onHandleIntent","event.getStatus()="+_event.getStatus());
					if (_event.getStatus() != Event.ESTATUS_STOP)
						// len spustaj eventy
						// spusatj aj ked uz je spusteny
						dataWrapper.doEventService(_event, false, true, false); 
				}
			}
			// 3. start no started events in point 2.
			for (Event _event : eventList)
			{
				GlobalData.logE("EventsService.onHandleIntent","state RUNNING");
				GlobalData.logE("EventsService.onHandleIntent","event._id="+_event._id);
				GlobalData.logE("EventsService.onHandleIntent","event.getStatus()="+_event.getStatus());
				
				if (_event.getStatus() != Event.ESTATUS_STOP)
					// len spustaj eventy
					// spustaj len ak este nebezi
					dataWrapper.doEventService(_event, false, false, false); 
			}
		}
		else
		{
			//1. pause events
			for (Event _event : eventList)
			{
				GlobalData.logE("EventsService.onHandleIntent","state PAUSE");
				GlobalData.logE("EventsService.onHandleIntent","event._id="+_event._id);
				GlobalData.logE("EventsService.onHandleIntent","event.getStatus()="+_event.getStatus());
				
				if (_event.getStatus() != Event.ESTATUS_STOP)
					// len pauzuj eventy
					// pauzuj len ak este nie je zapauznuty
					dataWrapper.doEventService(_event, true, false, true); 
			}
			//2. start events
			for (Event _event : eventList)
			{
				GlobalData.logE("EventsService.onHandleIntent","state RUNNING");
				GlobalData.logE("EventsService.onHandleIntent","event._id="+_event._id);
				GlobalData.logE("EventsService.onHandleIntent","event.getStatus()="+_event.getStatus());
				
				if (_event.getStatus() != Event.ESTATUS_STOP)
					// len spustaj eventy
					// spustaj len ak este nebezi
					dataWrapper.doEventService(_event, false, false, true); 
			}
		}

		doEndService(intent);

		// refresh GUI
		Intent refreshIntent = new Intent();
		refreshIntent.setAction(RefreshGUIBroadcastReceiver.INTENT_REFRESH_GUI);
		context.sendBroadcast(refreshIntent);
		
		dataWrapper.invalidateDataWrapper();

		
		GlobalData.logE("EventsService.onHandleIntent","-- end --------------------------------");
		
	}

	private void doEndService(Intent intent)
	{
		// completting wake
		if (broadcastReceiverType.equals(RestartEventsBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			RestartEventsBroadcastReceiver.completeWakefulIntent(intent);
		else
		if (broadcastReceiverType.equals(EventsTimeBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			EventsTimeBroadcastReceiver.completeWakefulIntent(intent);
		else
		if (broadcastReceiverType.equals(BatteryEventBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			BatteryEventBroadcastReceiver.completeWakefulIntent(intent);
		else
		if (broadcastReceiverType.equals(PhoneCallBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			PhoneCallBroadcastReceiver.completeWakefulIntent(intent);
		else
		if (broadcastReceiverType.equals(DockConnectionBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			DockConnectionBroadcastReceiver.completeWakefulIntent(intent);
		else
		if (broadcastReceiverType.equals(HeadsetConnectionBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			HeadsetConnectionBroadcastReceiver.completeWakefulIntent(intent);
		else
		if (broadcastReceiverType.equals(EventsCalendarBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			EventsCalendarBroadcastReceiver.completeWakefulIntent(intent);
		else
		if (broadcastReceiverType.equals(CalendarProviderChangedBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			CalendarProviderChangedBroadcastReceiver.completeWakefulIntent(intent);
		else
		if (broadcastReceiverType.equals(SearchCalendarEventsBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			SearchCalendarEventsBroadcastReceiver.completeWakefulIntent(intent);
		else
		if (broadcastReceiverType.equals(WifiConnectionBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			WifiConnectionBroadcastReceiver.completeWakefulIntent(intent);
		else
		if (broadcastReceiverType.equals(WifiScanBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			WifiScanBroadcastReceiver.completeWakefulIntent(intent);
	}
	
}
