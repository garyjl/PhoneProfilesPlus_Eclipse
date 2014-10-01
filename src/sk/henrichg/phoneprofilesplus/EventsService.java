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

		GlobalData.logE("@@@ EventsService.onHandleIntent","-- start --------------------------------");

		WifiScanAlarmBroadcastReceiver.unlock();
		BluetoothScanAlarmBroadcastReceiver.unlock();
		
		// disabled for firstStartEvents
		//if (!GlobalData.getApplicationStarted(context))
			// application is not started
		//	return;

		GlobalData.setApplicationStarted(context, true);
		
		
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
		
		if (broadcastReceiverType.equals(CalendarProviderChangedBroadcastReceiver.BROADCAST_RECEIVER_TYPE) ||
			broadcastReceiverType.equals(SearchCalendarEventsBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
		{
			// search for calendar events
			GlobalData.logE("EventsService.onHandleIntent","search for calendar events");
			for (Event _event : eventList)
			{
				if (_event.getStatus() != Event.ESTATUS_STOP)
				{
					if (_event._eventPreferencesCalendar._enabled)
					{
						GlobalData.logE("EventsService.onHandleIntent","event._id="+_event._id);
						_event._eventPreferencesCalendar.searchEvent(context);
					}
				}
			}
		}
		
		boolean isRestart = (broadcastReceiverType.equals(RestartEventsBroadcastReceiver.BROADCAST_RECEIVER_TYPE) ||
							 broadcastReceiverType.equals(CalendarProviderChangedBroadcastReceiver.BROADCAST_RECEIVER_TYPE) ||
							 broadcastReceiverType.equals(SearchCalendarEventsBroadcastReceiver.BROADCAST_RECEIVER_TYPE));
		
		boolean interactive = !isRestart;
		
		boolean forDelayAlarm = broadcastReceiverType.equals(EventDelayBroadcastReceiver.BROADCAST_RECEIVER_TYPE);

		//GlobalData.logE("@@@ EventsService.onHandleIntent","isRestart="+isRestart);
		GlobalData.logE("@@@ EventsService.onHandleIntent","forDelayAlarm="+forDelayAlarm);
		
		if (isRestart)
		{
			// 1. pause events
			dataWrapper.sortEventsByPriorityDesc();
			for (Event _event : eventList)
			{
				GlobalData.logE("EventsService.onHandleIntent","state PAUSE");
				GlobalData.logE("EventsService.onHandleIntent","event._id="+_event._id);
				GlobalData.logE("EventsService.onHandleIntent","event.getStatus()="+_event.getStatus());
				
				if (_event.getStatus() != Event.ESTATUS_STOP)
					// len pauzuj eventy
					// pauzuj aj ked uz je zapauznuty
					dataWrapper.doEventService(_event, true, true, interactive, forDelayAlarm);
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
						dataWrapper.doEventService(_event, false, true, interactive, forDelayAlarm); 
				}
			}
			// 3. start no started events in point 2.
			dataWrapper.sortEventsByPriorityAsc();
			for (Event _event : eventList)
			{
				GlobalData.logE("EventsService.onHandleIntent","state RUNNING");
				GlobalData.logE("EventsService.onHandleIntent","event._id="+_event._id);
				GlobalData.logE("EventsService.onHandleIntent","event.getStatus()="+_event.getStatus());
				
				if (_event.getStatus() != Event.ESTATUS_STOP)
					// len spustaj eventy
					// spustaj len ak este nebezi
					dataWrapper.doEventService(_event, false, false, interactive, forDelayAlarm);
			}
		}
		else
		{
			//1. pause events
			dataWrapper.sortEventsByPriorityDesc();
			for (Event _event : eventList)
			{
				GlobalData.logE("EventsService.onHandleIntent","state PAUSE");
				GlobalData.logE("EventsService.onHandleIntent","event._id="+_event._id);
				GlobalData.logE("EventsService.onHandleIntent","event.getStatus()="+_event.getStatus());
				
				if (_event.getStatus() != Event.ESTATUS_STOP)
					// len pauzuj eventy
					// pauzuj len ak este nie je zapauznuty
					dataWrapper.doEventService(_event, true, false, interactive, forDelayAlarm);
			}
			//2. start events
			dataWrapper.sortEventsByPriorityAsc();
			for (Event _event : eventList)
			{
				GlobalData.logE("EventsService.onHandleIntent","state RUNNING");
				GlobalData.logE("EventsService.onHandleIntent","event._id="+_event._id);
				GlobalData.logE("EventsService.onHandleIntent","event.getStatus()="+_event.getStatus());
				
				if (_event.getStatus() != Event.ESTATUS_STOP)
					// len spustaj eventy
					// spustaj len ak este nebezi
					dataWrapper.doEventService(_event, false, false, interactive, forDelayAlarm); 
			}
		}

		if (GlobalData.getGlobalEventsRuning(context))
		{
			// when no events are running or manula activation, activate background profile
			// when no profile is activated
			if ((!GlobalData.getEventsBlocked(context)) || (GlobalData.getForceRunEventRunning(context)))
			{
				// no manual profile activation
				List<EventTimeline> eventTimelineList = dataWrapper.getEventTimelineList();
				if (eventTimelineList.size() == 0)
				{
					// no events running
					long profileId = Long.valueOf(GlobalData.applicationBackgroundProfile); 
					if (profileId != GlobalData.PROFILE_NO_ACTIVATE)
					{
						Profile profile = dataWrapper.getActivatedProfile();
						long activatedProfileId = 0;
						if (profile != null)
							activatedProfileId = profile._id;
						if (activatedProfileId != profileId)
							dataWrapper.activateProfileFromEvent(profileId, interactive, "");
					}
					else
						dataWrapper.activateProfileFromEvent(0, interactive, "");
				}
			}
			else
			{
				// manual profile activation
				long profileId = Long.valueOf(GlobalData.applicationBackgroundProfile); 
				if (profileId != GlobalData.PROFILE_NO_ACTIVATE)
				{
					Profile profile = dataWrapper.getActivatedProfile();
					if (profile == null)
						// if not profile activated, activate Default profile
						dataWrapper.activateProfileFromEvent(profileId, interactive, "");
				}
			}
		}
		
		doEndService(intent);

		// refresh GUI
		Intent refreshIntent = new Intent();
		refreshIntent.setAction(RefreshGUIBroadcastReceiver.INTENT_REFRESH_GUI);
		context.sendBroadcast(refreshIntent);
		
		dataWrapper.invalidateDataWrapper();

		
		GlobalData.logE("@@@ EventsService.onHandleIntent","-- end --------------------------------");
		
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
		else
		if (broadcastReceiverType.equals(ScreenOnOffBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			ScreenOnOffBroadcastReceiver.completeWakefulIntent(intent);
		else
		if (broadcastReceiverType.equals(BluetoothConnectionBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			BluetoothConnectionBroadcastReceiver.completeWakefulIntent(intent);
		else
		if (broadcastReceiverType.equals(BluetoothScanBroadcastReceiver.BROADCAST_RECEIVER_TYPE))
			BluetoothScanBroadcastReceiver.completeWakefulIntent(intent);
	}
	
}
