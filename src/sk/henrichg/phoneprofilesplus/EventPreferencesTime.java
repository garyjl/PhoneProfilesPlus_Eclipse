package sk.henrichg.phoneprofilesplus;

import java.sql.Date;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.format.DateFormat;

public class EventPreferencesTime extends EventPreferences {

	public boolean _sunday;
	public boolean _monday;
	public boolean _tuesday;
	public boolean _wendesday;
	public boolean _thursday;
	public boolean _friday;
	public boolean _saturday;
	public long _startTime;
	public long _endTime;
	public boolean _useEndTime;
	
	static final String PREF_EVENT_TIME_ENABLED = "eventTimeEnabled";
	static final String PREF_EVENT_TIME_DAYS = "eventTimeDays";
	static final String PREF_EVENT_TIME_START_TIME = "eventTimeStartTime";
	static final String PREF_EVENT_TIME_END_TIME = "eventTimeEndTime";
	static final String PREF_EVENT_TIME_USE_END_TIME = "eventTimeUseEndTime";
	
	public EventPreferencesTime(Event event,
			                    boolean enabled,
								boolean sunday,
								boolean monday,
								boolean tuesday,
								boolean wendesday,
								boolean thursday,
								boolean friday,
								boolean saturday,
								long startTime,
								long endTime,
								boolean useEndTime)
	{
		super(event, enabled);

		this._sunday = sunday;
		this._monday = monday;
		this._tuesday = tuesday;
		this._wendesday = wendesday;
		this._thursday = thursday;
		this._friday = friday;
		this._saturday = saturday;
		this._startTime = startTime;
		this._endTime = endTime;
		this._useEndTime = useEndTime;
	}
	
	@Override
	public void copyPreferences(Event fromEvent)
	{
		this._enabled = ((EventPreferencesTime)fromEvent._eventPreferencesTime)._enabled;
		this._sunday = ((EventPreferencesTime)fromEvent._eventPreferencesTime)._sunday;
		this._monday = ((EventPreferencesTime)fromEvent._eventPreferencesTime)._monday;
		this._tuesday = ((EventPreferencesTime)fromEvent._eventPreferencesTime)._tuesday;
		this._wendesday = ((EventPreferencesTime)fromEvent._eventPreferencesTime)._wendesday;
		this._thursday = ((EventPreferencesTime)fromEvent._eventPreferencesTime)._thursday;
		this._friday = ((EventPreferencesTime)fromEvent._eventPreferencesTime)._friday;
		this._saturday = ((EventPreferencesTime)fromEvent._eventPreferencesTime)._saturday;
		this._startTime = ((EventPreferencesTime)fromEvent._eventPreferencesTime)._startTime;
		this._endTime = ((EventPreferencesTime)fromEvent._eventPreferencesTime)._endTime;
		this._useEndTime = ((EventPreferencesTime)fromEvent._eventPreferencesTime)._useEndTime;
	}
	
	@Override
	public void loadSharedPrefereces(SharedPreferences preferences)
	{
    	Editor editor = preferences.edit();
        editor.putBoolean(PREF_EVENT_TIME_ENABLED, _enabled);
    	String sValue = "";
    	if (this._sunday) sValue = sValue + "0|";
    	if (this._monday) sValue = sValue + "1|";
    	if (this._tuesday) sValue = sValue + "2|";
    	if (this._wendesday) sValue = sValue + "3|";
    	if (this._thursday) sValue = sValue + "4|";
    	if (this._friday) sValue = sValue + "5|";
    	if (this._saturday) sValue = sValue + "6|";
		//Log.e("EventPreferencesTime.loadSharedPreferences",sValue);
        editor.putString(PREF_EVENT_TIME_DAYS, sValue);
        editor.putLong(PREF_EVENT_TIME_START_TIME, this._startTime);
        editor.putLong(PREF_EVENT_TIME_END_TIME, this._endTime);
        editor.putBoolean(PREF_EVENT_TIME_USE_END_TIME, this._useEndTime);
		editor.commit();
	}

	@Override
	public void saveSharedPrefereces(SharedPreferences preferences)
	{
		this._enabled = preferences.getBoolean(PREF_EVENT_TIME_ENABLED, false);
		
		String sDays = preferences.getString(PREF_EVENT_TIME_DAYS, DaysOfWeekPreference.allValue);
		//Log.e("EventPreferencesTime.saveSharedPreferences",sDays);
		String[] splits = sDays.split("\\|");
		if (splits[0].equals(DaysOfWeekPreference.allValue))
		{
			this._sunday = true;
			this._monday = true;
			this._tuesday = true;
			this._wendesday = true;
			this._thursday = true;
			this._friday = true;
			this._saturday = true;
		}
		else
		{
			this._sunday = false;
			this._monday = false;
			this._tuesday = false;
			this._wendesday = false;
			this._thursday = false;
			this._friday = false;
			this._saturday = false;
			for (String value : splits)
			{
				this._sunday = this._sunday || value.equals("0");
				this._monday = this._monday || value.equals("1");
				this._tuesday = this._tuesday || value.equals("2");
				this._wendesday = this._wendesday || value.equals("3");
				this._thursday = this._thursday || value.equals("4");
				this._friday = this._friday || value.equals("5");
				this._saturday = this._saturday || value.equals("6");
			}
		}
		this._startTime = preferences.getLong(PREF_EVENT_TIME_START_TIME, System.currentTimeMillis());
		this._endTime = preferences.getLong(PREF_EVENT_TIME_END_TIME, System.currentTimeMillis());
		this._useEndTime = preferences.getBoolean(PREF_EVENT_TIME_USE_END_TIME, false);
	}
	
	@Override
	public String getPreferencesDescription(String description, Context context)
	{
		String descr = description + context.getString(R.string.event_type_time) + ": ";

		if (!this._enabled)
			descr = descr + context.getString(R.string.event_preferences_not_enabled);
		else
		{
	    	boolean[] daySet = new boolean[7];
			daySet[0] = this._sunday;
			daySet[1] = this._monday;
			daySet[2] = this._tuesday;
			daySet[3] = this._wendesday;
			daySet[4] = this._thursday;
			daySet[5] = this._friday;
			daySet[6] = this._saturday;
	    	
			boolean allDays = true;
	    	for (int i = 0; i < 7; i++)
	    		allDays = allDays && daySet[i]; 
			
	    	if (allDays)
	    	{
	    		descr = descr + context.getString(R.string.array_pref_event_all);
	    		descr = descr + " ";
	    	}
	    	else
	    	{
		    	String[] namesOfDay = DateFormatSymbols.getInstance().getShortWeekdays();
		    	
		    	int dayOfWeek;
		    	for (int i = 0; i < 7; i++)
		    	{
		    		dayOfWeek = getDayOfWeekByLocale(i);
		    		
		    		if (daySet[dayOfWeek])
		    			descr = descr + namesOfDay[dayOfWeek+1] + " ";
		    	}
	    	}
	    	
	        Calendar calendar = Calendar.getInstance();
	
	        calendar.setTimeInMillis(this._startTime);
			descr = descr + "- ";
			descr = descr + DateFormat.getTimeFormat(context).format(new Date(calendar.getTimeInMillis()));
			if (this._useEndTime)
			{
		        calendar.setTimeInMillis(this._endTime);
				descr = descr + "-";
				descr = descr + DateFormat.getTimeFormat(context).format(new Date(calendar.getTimeInMillis()));
			}
			
			
	   		if (GlobalData.getGlobalEventsRuning(context))
	   		{
	   			long alarmTime;
	   		    //SimpleDateFormat sdf = new SimpleDateFormat("EEd/MM/yy HH:mm");
	   		    String alarmTimeS = "";
	   			if (_event.getStatus() == Event.ESTATUS_PAUSE)
	   			{
	   				int daysToAdd = computeDaysForAdd(true);
	   				alarmTime = computeAlarm(true, daysToAdd);
	   				// date and time format by user system settings configuration
	   	   		    alarmTimeS = "(st) " + DateFormat.getDateFormat(context).format(alarmTime) +
	   	   		    			 " " + DateFormat.getTimeFormat(context).format(alarmTime);
	   	   		    descr = descr + '\n';
	   	   		    descr = descr + alarmTimeS;
	   			}
	   			else
	   			if ((_event.getStatus() == Event.ESTATUS_RUNNING) && _useEndTime)
	   			{
	   				int daysToAdd = computeDaysForAdd(false);
	   				alarmTime = computeAlarm(false, daysToAdd);
	   				// date and time format by user system settings configuration
	   	   		    alarmTimeS = "(et) " + DateFormat.getDateFormat(context).format(alarmTime) +
	   	   		    			 " " + DateFormat.getTimeFormat(context).format(alarmTime);
	   	   		    descr = descr + '\n';
	   	   		    descr = descr + alarmTimeS;
	   			}
	   		}
		}
   		
		return descr;
	}
	
    // dayOfWeek: value are (for exapmple) Calendar.SUNDAY-1
    // return: value are (for exapmple) Calendar.MONDAY-1
    public static int getDayOfWeekByLocale(int dayOfWeek)
    {
    	
    	Calendar cal = Calendar.getInstance(); 
    	int firstDayOfWeek = cal.getFirstDayOfWeek();
    	
    	int resDayOfWeek = dayOfWeek + (firstDayOfWeek-1);
    	if (resDayOfWeek > 6)
    		resDayOfWeek = resDayOfWeek - 7;

    	//Log.e("DaysOfWeekPreference.getDayOfWeekByLocale","resDayOfWeek="+resDayOfWeek);
    	
    	return resDayOfWeek;
    }

	@Override
	public boolean isRunable()
	{
		
		boolean runable = super.isRunable();

		boolean dayOfWeek = false;
		dayOfWeek = dayOfWeek || this._sunday;
		dayOfWeek = dayOfWeek || this._monday;
		dayOfWeek = dayOfWeek || this._tuesday;
		dayOfWeek = dayOfWeek || this._wendesday;
		dayOfWeek = dayOfWeek || this._thursday;
		dayOfWeek = dayOfWeek || this._friday;
		dayOfWeek = dayOfWeek || this._saturday;
		runable = runable && dayOfWeek;

		return runable;
	}
	
	@Override
	public boolean activateReturnProfile()
	{
		return _useEndTime;
	}
	
	public int computeDaysForAdd(boolean startEvent)
	{
		boolean[] daysOfWeek =  new boolean[8];
		daysOfWeek[Calendar.SUNDAY] = this._sunday;
		daysOfWeek[Calendar.MONDAY] = this._monday;
		daysOfWeek[Calendar.TUESDAY] = this._tuesday;
		daysOfWeek[Calendar.WEDNESDAY] = this._wendesday;
		daysOfWeek[Calendar.THURSDAY] = this._thursday;
		daysOfWeek[Calendar.FRIDAY] = this._friday;
		daysOfWeek[Calendar.SATURDAY] = this._saturday;
		
		Calendar calendar = Calendar.getInstance();
		
		int daysToAdd;
		
		if (startEvent)
		{
			int thisDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			int setDayOfWeek = thisDayOfWeek;

			GlobalData.logE("EventPreferencesTime.computeDaysForAdd","thisDayOfWeek="+thisDayOfWeek);
			
			boolean setNextDayOfWeek = false;
			
			if (daysOfWeek[thisDayOfWeek])
			{
				// current day of week is set in event preferences
				GlobalData.logE("EventPreferencesTime.computeDaysForAdd","thisDayOfWeek=true");

				Calendar now = Calendar.getInstance();
				if (startEvent)
					calendar.setTimeInMillis(_startTime);
				else
					calendar.setTimeInMillis(_endTime);
			    calendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
			    calendar.set(Calendar.MONTH, now.get(Calendar.MONTH)); 
			    calendar.set(Calendar.YEAR,  now.get(Calendar.YEAR));

				long thisTime = now.getTimeInMillis();

				/*
			    SimpleDateFormat sdf = new SimpleDateFormat("EE d.MM.yyyy HH:mm:ss:S");
			    String result = sdf.format(thisTime);
			    Log.e("EventPreferencesTime.setSystemRunningEvent","thisTime="+result);	    
			    result = sdf.format(calendar.getTimeInMillis());
			    Log.e("EventPreferencesTime.setSystemRunningEvent","calendar.Time="+result);	    
				*/
			    
			    setNextDayOfWeek = (calendar.getTimeInMillis() <= thisTime);
				
			}
			else
				setNextDayOfWeek = true;
			
			if (setNextDayOfWeek)
			{
				// find next day of week 

				GlobalData.logE("EventPreferencesTime.computeDaysForAdd","setNextDayOfWeek=true");
				
				for (int i = thisDayOfWeek+1; i < 8; i++)
				{
					if (daysOfWeek[i])
					{
						setDayOfWeek = i;
						break;
					}
				}
				if (setDayOfWeek == thisDayOfWeek)
				{
					for (int i = 1; i < thisDayOfWeek; i++)
					{
						if (daysOfWeek[i])
						{
							setDayOfWeek = i;
							break;
						}
					}
				}
			}

			GlobalData.logE("EventPreferencesTime.computeDaysForAdd","setDayOfWeek="+setDayOfWeek);
			
			daysToAdd = setDayOfWeek - thisDayOfWeek;
			if ((setDayOfWeek <= thisDayOfWeek) && setNextDayOfWeek)
				daysToAdd = 7 + daysToAdd;
			
		}
		else
		{
			if (_useEndTime && (_startTime >= _endTime))
				daysToAdd = 1;
			else
				daysToAdd = 0;
		}

		GlobalData.logE("EventPreferencesTime.computeDaysForAdd","daysToAdd="+daysToAdd);
		
		return daysToAdd;
		
	}
	
	@Override
	public void setSystemRunningEvent(Context context)
	{
		// set alarm for state PAUSE
		
		// this alarm generates broadcast, that change state into RUNNING
		// from broadcast will by called EventsService with 
		// EXTRA_EVENTS_SERVICE_PROCEDURE == ESP_START_EVENT

		int daysToAdd = computeDaysForAdd(true);
		
		removeAlarm(context);
		setAlarm(true, daysToAdd, context);
	}

	@Override
	public void setSystemPauseEvent(Context context)
	{
		// set alarm for state RUNNING

		// this alarm generates broadcast, that change state into PAUSE
		// from broadcast will by called EventsService with 
		// EXTRA_EVENTS_SERVICE_PROCEDURE == ESP_PAUSE_EVENT
		
		int daysToAdd = computeDaysForAdd(false);
		
		removeAlarm(context);
		setAlarm(false, daysToAdd, context);
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

	public long computeAlarm(boolean startEvent, int daysToAdd)
	{
		Calendar now = Calendar.getInstance();
		Calendar alarmCalendar = Calendar.getInstance();
		
	    if (startEvent)
	    	alarmCalendar.setTimeInMillis(_startTime);
	    else
	    {
	    	if (_useEndTime)
	    		alarmCalendar.setTimeInMillis(_endTime);
	    	else
	    		alarmCalendar.setTimeInMillis(_startTime + (5 * 1000)); 
	    }
	    
	    alarmCalendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
	    alarmCalendar.set(Calendar.MONTH, now.get(Calendar.MONTH)); 
	    alarmCalendar.set(Calendar.YEAR,  now.get(Calendar.YEAR));
	    

	    // Add this day of the week line to your existing code
	    alarmCalendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
	    
	    long alarmTime = alarmCalendar.getTimeInMillis();
	    
	    return alarmTime;
	}
	
	@SuppressLint("SimpleDateFormat")
	private void setAlarm(boolean startEvent, int daysToAdd, Context context)
	{

	    long alarmTime = computeAlarm(startEvent, daysToAdd);

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
	
	@Override
	public boolean invokeBroadcastReceiver(Context context)
	{
		return false;
	}

}
