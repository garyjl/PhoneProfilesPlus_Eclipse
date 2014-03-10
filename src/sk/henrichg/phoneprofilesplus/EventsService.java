package sk.henrichg.phoneprofilesplus;

import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

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
		
		if (procedure == ESP_RESTART_EVENTS)
		{
			dataWrapper.firstStartEvents(true, false);
		}
		else
		{
			// in intent is event_id
			long event_id = intent.getLongExtra(GlobalData.EXTRA_EVENT_ID, 0);
			GlobalData.logE("EventsService.onHandleIntent","event_id="+event_id);
			Event event = dataWrapper.getEventById(event_id);

			
			if (procedure == ESP_STOP_EVENT)
			{
				event.stopEvent(dataWrapper, eventTimelineList, true, false, true);
			}
			else
			{
				switch (eventType)
				{
					case Event.ETYPE_TIME:
						doEvent(dataWrapper, eventTimelineList, event, procedure);
						break;
					case Event.ETYPE_BATTERY:
						boolean powerChangeReceived = intent.getBooleanExtra(GlobalData.EXTRA_POWER_CHANGE_RECEIVED, false);
						doBatteryEvent(dataWrapper, eventTimelineList, event, powerChangeReceived);
						break;
					default:
						break;
				}
			}
		}

		doEndService(intent);
		
		dataWrapper.invalidateDataWrapper();
		
	}

	private void doEvent(DataWrapper dataWrapper, 
							List<EventTimeline> eventTimelineList,
							Event event, int procedure)
	{
		if (event == null)
			return;
		
		switch (procedure)
		{
			case ESP_START_EVENT:
				event.startEvent(dataWrapper, eventTimelineList, false);
				break;
			case ESP_PAUSE_EVENT:
				event.pauseEvent(dataWrapper, eventTimelineList, true, false, false);
				break;
			default:
				break;
		}
	}
	
	private void _doBatteryEvent(DataWrapper dataWrapper, 
								List<EventTimeline> eventTimelineList, 
								Event event, boolean powerChangeReceived)
	{
		// get battery status
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, ifilter);
		
		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		GlobalData.logE("EventService.doBatteryEvent","status="+status);
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
		                     status == BatteryManager.BATTERY_STATUS_FULL;
		GlobalData.logE("EventService.doBatteryEvent","isCharging="+isCharging);

		EventPreferencesBattery eventPreferences = (EventPreferencesBattery)event._eventPreferences;

		if (powerChangeReceived)
		{
			GlobalData.logE("EventService.doBatteryEvent","powerChangeReceived");
			// unblock starting battery event
			eventPreferences._blocked = false;
			dataWrapper.getDatabaseHandler().updateEventPreferencesBatteryBlocked(event);
		}
		
		if (isCharging != eventPreferences._charging)
		{
			event.pauseEvent(dataWrapper, eventTimelineList, true, false, false);
		}
		else
		{
			int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			
			float batteryPct = level / (float)scale;		
			
			GlobalData.logE("EventService.doBatteryEvent","batteryPct="+batteryPct);
			
			if ((batteryPct >= (eventPreferences._levelLow / (float)100)) && 
			    (batteryPct <= (eventPreferences._levelHight / (float)100))) 
			{
				GlobalData.logE("EventService.doBatteryEvent","inlevel blocked="+eventPreferences._blocked);
				if (!eventPreferences._blocked)
				{
					// starting battery level unblocked
					if (event.getStatus() != Event.ESTATUS_RUNNING)
						event.startEvent(dataWrapper, eventTimelineList, false);
				}
			}
			else
			{
				GlobalData.logE("EventService.doBatteryEvent","outlevel blocked="+eventPreferences._blocked);
				// unblock starting battery event
				eventPreferences._blocked = false;
				dataWrapper.getDatabaseHandler().updateEventPreferencesBatteryBlocked(event);

				event.pauseEvent(dataWrapper, eventTimelineList, true, false, false);
			}
		}
	}

	private void doBatteryEvent(DataWrapper dataWrapper, 
								List<EventTimeline> eventTimelineList,
								Event event, boolean powerChangeReceived)
	{
		List<Event> eventList = dataWrapper.getEventList();
		
		if (event == null)
		{
			for (Event _event : eventList)
			{
				GlobalData.logE("EventService.doBatteryEvent","event._type="+_event._type);
				GlobalData.logE("EventService.doBatteryEvent","event.getStatus()="+_event.getStatus());
				
				if ((_event._type == Event.ETYPE_BATTERY) && (_event.getStatus() != Event.ESTATUS_STOP))
				{
					_doBatteryEvent(dataWrapper, eventTimelineList, _event, powerChangeReceived);
				}
			}
		}
		else
			_doBatteryEvent(dataWrapper, eventTimelineList, event, powerChangeReceived);
		
	}
	

	private void doEndService(Intent intent)
	{
		// refresh GUI
		Intent refreshIntent = new Intent();
		refreshIntent.setAction(RefreshGUIBroadcastReceiver.INTENT_REFRESH_GUI);
		context.sendBroadcast(refreshIntent);

		// completting wake
		switch (eventType)
		{
			case Event.ETYPE_TIME:
				EventsAlarmBroadcastReceiver.completeWakefulIntent(intent);
				
				break;
			case Event.ETYPE_BATTERY:
				BatteryEventsAlarmBroadcastReceiver.completeWakefulIntent(intent);
				
				break;
			default:
				break;
		}
	}
}
