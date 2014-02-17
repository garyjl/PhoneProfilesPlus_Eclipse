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
			dataWrapper.firstStartEvents(true);
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
						doBatteryEvent(dataWrapper, eventTimelineList, event);
						break;
					default:
						break;
				}
			}
		}

		doEndService();
		
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
	
	private void doBatteryEvent(DataWrapper dataWrapper, 
								List<EventTimeline> eventTimelineList,
								Event event)
	{
	
		// get battery status
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, ifilter);
		
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
	
		float batteryPct = level / (float)scale;		
	
		GlobalData.logE("EventService.doBatteryEvent","batteryPct="+batteryPct);
		
		EventPreferencesBattery eventPreferences = (EventPreferencesBattery)event._eventPreferences;
		if (eventPreferences._levelType == EventPreferencesBattery.LEVELTYPE_LOW)
		{
			if (batteryPct <= (eventPreferences._level / (float)100))
			{
				if (event.getStatus() != Event.ESTATUS_RUNNING)
					event.startEvent(dataWrapper, eventTimelineList, false);
			}
			else
			{
				if (event.getStatus() != Event.ESTATUS_PAUSE)
					event.pauseEvent(dataWrapper, eventTimelineList, true, false, false);
			}
		}
		else
		{
			if (batteryPct >= (eventPreferences._level / (float)100))
			{
				if (event.getStatus() != Event.ESTATUS_RUNNING)
					event.startEvent(dataWrapper, eventTimelineList, false);
			}
			else
			{
				if (event.getStatus() != Event.ESTATUS_RUNNING)
					event.pauseEvent(dataWrapper, eventTimelineList, true, false, false);
			}
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
