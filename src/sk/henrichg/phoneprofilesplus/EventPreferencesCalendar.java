package sk.henrichg.phoneprofilesplus;

import java.sql.Date;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.format.DateFormat;

public class EventPreferencesCalendar extends EventPreferences {

	public String _calendars;
	public int _searchField;
	public String _searchString;
	
	public long _startTime;
	public long _endTime;
	
	static final String PREF_EVENT_CALENDAR_ENABLED = "eventCalendarEnabled";
	static final String PREF_EVENT_CALENDAR_CALENDARS = "eventCalendarCalendars";
	static final String PREF_EVENT_CALENDAR_SEARCH_FIELD = "eventCalendarSearchField";
	static final String PREF_EVENT_CALENDAR_SEARCH_STRING = "eventCalendarSearchString";
	
	public EventPreferencesCalendar(Event event,
			                    boolean enabled,
								String calendars,
								int searchField,
								String searchString)
	{
		super(event, enabled);

		this._calendars = calendars;
		this._searchField = searchField;
		this._searchString = searchString;
		
		this._startTime = 0;
		this._endTime = 0;
	}
	
	@Override
	public void copyPreferences(Event fromEvent)
	{
		this._enabled = ((EventPreferencesCalendar)fromEvent._eventPreferencesCalendar)._enabled;
		this._calendars = ((EventPreferencesCalendar)fromEvent._eventPreferencesCalendar)._calendars;
		this._searchField = ((EventPreferencesCalendar)fromEvent._eventPreferencesCalendar)._searchField;
		this._searchString = ((EventPreferencesCalendar)fromEvent._eventPreferencesCalendar)._searchString;
		
		this._startTime = ((EventPreferencesCalendar)fromEvent._eventPreferencesCalendar)._startTime;
		this._endTime = ((EventPreferencesCalendar)fromEvent._eventPreferencesCalendar)._endTime;
	}
	
	@Override
	public void loadSharedPrefereces(SharedPreferences preferences)
	{
    	Editor editor = preferences.edit();
        editor.putBoolean(PREF_EVENT_CALENDAR_ENABLED, _enabled);
        editor.putString(PREF_EVENT_CALENDAR_ENABLED, _calendars);
        editor.putString(PREF_EVENT_CALENDAR_SEARCH_FIELD, String.valueOf(_searchField));
        editor.putString(PREF_EVENT_CALENDAR_SEARCH_STRING, _searchString);
		editor.commit();
	}

	@Override
	public void saveSharedPrefereces(SharedPreferences preferences)
	{
		this._enabled = preferences.getBoolean(PREF_EVENT_CALENDAR_ENABLED, false);
		this._calendars = preferences.getString(PREF_EVENT_CALENDAR_CALENDARS, "");
		this._searchField = Integer.parseInt(preferences.getString(PREF_EVENT_CALENDAR_SEARCH_FIELD, "0"));
		this._searchString = preferences.getString(PREF_EVENT_CALENDAR_SEARCH_STRING, "");
	}
	
	@Override
	public String getPreferencesDescription(String description, Context context)
	{
		String descr = description + context.getString(R.string.event_type_calendar) + ": ";

		if (!this._enabled)
			descr = descr + context.getString(R.string.event_preferences_not_enabled);
		else
		{
			//TODO tu pridat zobrazenie searchField a searchString
			
	        Calendar calendar = Calendar.getInstance();
	
	   		if (GlobalData.getGlobalEventsRuning(context))
	   		{
	   			long alarmTime;
	   		    //SimpleDateFormat sdf = new SimpleDateFormat("EEd/MM/yy HH:mm");
	   		    String alarmTimeS = "";
	   			if (_event.getStatus() == Event.ESTATUS_PAUSE)
	   			{
	   				alarmTime = computeAlarm(true);
	   				// date and time format by user system settings configuration
	   	   		    alarmTimeS = "(st) " + DateFormat.getDateFormat(context).format(alarmTime) +
	   	   		    			 " " + DateFormat.getTimeFormat(context).format(alarmTime);
	   	   		    descr = descr + '\n';
	   	   		    descr = descr + "-> " + alarmTimeS;
	   			}
	   			else
	   			if (_event.getStatus() == Event.ESTATUS_RUNNING)
	   			{
	   				alarmTime = computeAlarm(false);
	   				// date and time format by user system settings configuration
	   	   		    alarmTimeS = "(et) " + DateFormat.getDateFormat(context).format(alarmTime) +
	   	   		    			 " " + DateFormat.getTimeFormat(context).format(alarmTime);
	   	   		    descr = descr + '\n';
	   	   		    descr = descr + "-> " + alarmTimeS;
	   			}
	   		}
		}
   		
		return descr;
	}
	
	@Override
	public boolean isRunable()
	{
		
		boolean runable = super.isRunable();

		runable = runable && (!_calendars.isEmpty());
		runable = runable && (!_searchString.isEmpty());

		return runable;
	}
	
	@Override
	public boolean activateReturnProfile()
	{
		return true;
	}
	
	public long computeAlarm(boolean startEvent)
	{
		GlobalData.logE("EventPreferencesTime.computeAlarm","startEvent="+startEvent);

		boolean[] daysOfWeek =  new boolean[8];
		daysOfWeek[Calendar.SUNDAY] = this._sunday;
		daysOfWeek[Calendar.MONDAY] = this._monday;
		daysOfWeek[Calendar.TUESDAY] = this._tuesday;
		daysOfWeek[Calendar.WEDNESDAY] = this._wendesday;
		daysOfWeek[Calendar.THURSDAY] = this._thursday;
		daysOfWeek[Calendar.FRIDAY] = this._friday;
		daysOfWeek[Calendar.SATURDAY] = this._saturday;
		
		Calendar now = Calendar.getInstance();
		
		///// set calendar for startTime and endTime
		Calendar calStartTime = Calendar.getInstance();
		Calendar calEndTime = Calendar.getInstance();

		int gmtOffset = TimeZone.getDefault().getRawOffset();
		
		calStartTime.setTimeInMillis(_startTime - gmtOffset);
		calStartTime.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
		calStartTime.set(Calendar.MONTH, now.get(Calendar.MONTH)); 
		calStartTime.set(Calendar.YEAR,  now.get(Calendar.YEAR));
		calStartTime.set(Calendar.SECOND, 0);
		calStartTime.set(Calendar.MILLISECOND, 0);

		long computedEndTime = _endTime - gmtOffset;
		if (!_useEndTime)
			computedEndTime = (_startTime - gmtOffset) + (5 * 1000);
		calEndTime.setTimeInMillis(computedEndTime);
		calEndTime.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
		calEndTime.set(Calendar.MONTH, now.get(Calendar.MONTH)); 
		calEndTime.set(Calendar.YEAR,  now.get(Calendar.YEAR));
		calEndTime.set(Calendar.SECOND, 0);
		calEndTime.set(Calendar.MILLISECOND, 0);

		if (calStartTime.getTimeInMillis() >= calEndTime.getTimeInMillis())
	    {
			// endTime is over midnight
			GlobalData.logE("EventPreferencesTime.computeAlarm","startTime >= endTime");
			
			if (now.getTimeInMillis() < calEndTime.getTimeInMillis())
			{
				// now is before endTime
				// decrease start/end time
				calStartTime.add(Calendar.DAY_OF_YEAR, -1);
				calEndTime.add(Calendar.DAY_OF_YEAR, -1);
			}
				
			calEndTime.add(Calendar.DAY_OF_YEAR, 1);
	    }
		
		if (calEndTime.getTimeInMillis() < now.getTimeInMillis())
		{
			// endTime is before actual time, compute for future
			calStartTime.add(Calendar.DAY_OF_YEAR, 1);
			calEndTime.add(Calendar.DAY_OF_YEAR, 1);
		}	
		////////////////////////////

		//// update calendar for startTime a endTime by selected day of week
		int startDayOfWeek = calStartTime.get(Calendar.DAY_OF_WEEK);
		if (daysOfWeek[startDayOfWeek])
		{
			// startTime of week is selected
			GlobalData.logE("EventPreferencesTime.computeAlarm","startTime of week is selected");
		}
		else
		{
			// startTime of week is not selected,
			GlobalData.logE("EventPreferencesTime.computeAlarm","startTime of week is NOT selected");
			GlobalData.logE("EventPreferencesTime.computeAlarm","startDayOfWeek="+startDayOfWeek);
			
			// search for selected day of week
			boolean found = false;
			int daysToAdd = 0;
			for (int i = startDayOfWeek+1; i < 8; i++)
			{
				++daysToAdd;
				if (daysOfWeek[i])
				{
					found = true;
					break;
				}
			}
			if (!found)
			{
				for (int i = 1; i < startDayOfWeek; i++)
				{
					++daysToAdd;
					if (daysOfWeek[i])
					{
						found = true;
						break;
					}
				}
			}
			if (found)
			{
				GlobalData.logE("EventPreferencesTime.computeAlarm","daysToAdd="+daysToAdd);
				calStartTime.add(Calendar.DAY_OF_YEAR, daysToAdd);
				calEndTime.add(Calendar.DAY_OF_YEAR, daysToAdd);
			}
		}
		//////////////////////

		long alarmTime;
		if (startEvent)
			alarmTime = calStartTime.getTimeInMillis();
		else
			alarmTime = calEndTime.getTimeInMillis();
	    
	    return alarmTime;
		
	}
	
	@Override
	public void setSystemRunningEvent(Context context)
	{
		// set alarm for state PAUSE
		
		// this alarm generates broadcast, that change state into RUNNING
		// from broadcast will by called EventsService with 
		// EXTRA_EVENTS_SERVICE_PROCEDURE == ESP_START_EVENT
		

		removeAlarm(context);
		
		if (!(isRunable() && _enabled)) 
			return;

		setAlarm(true, computeAlarm(true), context);
	}

	@Override
	public void setSystemPauseEvent(Context context)
	{
		// set alarm for state RUNNING

		// this alarm generates broadcast, that change state into PAUSE
		// from broadcast will by called EventsService with 
		// EXTRA_EVENTS_SERVICE_PROCEDURE == ESP_PAUSE_EVENT

		removeAlarm(context);
		
		if (!(isRunable() && _enabled)) 
			return;
		
		setAlarm(false, computeAlarm(false), context);
	}
	
	@Override
	public void removeSystemEvent(Context context)
	{
		// remove alarms for state STOP

		removeAlarm(context);
		
		GlobalData.logE("EventPreferencesTime.removeSystemEvent","xxx");
	}

	public void removeAlarm(Context context)
	{
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);

		Intent intent = new Intent(context, EventsAlarmBroadcastReceiver.class);
	    
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), (int) _event._id, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null)
        {
       		GlobalData.logE("EventPreferencesTime.removeAlarm","alarm found");
        		
        	alarmManager.cancel(pendingIntent);
        	pendingIntent.cancel();
        }
	}
	
	@SuppressLint("SimpleDateFormat")
	private void setAlarm(boolean startEvent, long alarmTime, Context context)
	{
	    SimpleDateFormat sdf = new SimpleDateFormat("EE d.MM.yyyy HH:mm:ss:S");
	    String result = sdf.format(alarmTime);
	    if (startEvent)
	    	GlobalData.logE("EventPreferencesTime.setAlarm","startTime="+result);
	    else
	    	GlobalData.logE("EventPreferencesTime.setAlarm","endTime="+result);
	    
	    Intent intent = new Intent(context, EventsAlarmBroadcastReceiver.class);
	    intent.putExtra(GlobalData.EXTRA_EVENT_ID, _event._id);
	    intent.putExtra(GlobalData.EXTRA_START_SYSTEM_EVENT, startEvent);
	    
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), (int) _event._id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmTime, 24 * 60 * 60 * 1000 , pendingIntent);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, 24 * 60 * 60 * 1000 , pendingIntent);
        
	}
	
}
