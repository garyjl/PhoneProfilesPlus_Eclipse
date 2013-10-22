package sk.henrichg.phoneprofilesplus;

import java.sql.Date;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
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
	
	static final String PREF_EVENT_TIME_DAYS = "eventTimeDays";
	static final String PREF_EVENT_TIME_START_TIME = "eventTimeStartTime";
	static final String PREF_EVENT_TIME_END_TIME = "eventTimeEndTime";
	static final String PREF_EVENT_TIME_USE_END_TIME = "eventTimeUseEndTime";
	
	public EventPreferencesTime(Event event,
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
		super(event);

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
		
		_preferencesResourceID = R.xml.event_preferences_time;
		_iconResourceID = R.drawable.ic_event_time; 
	}
	
	@Override
	public void copyPreferences(Event fromEvent)
	{
		this._sunday = ((EventPreferencesTime)fromEvent._eventPreferences)._sunday;
		this._monday = ((EventPreferencesTime)fromEvent._eventPreferences)._monday;
		this._tuesday = ((EventPreferencesTime)fromEvent._eventPreferences)._tuesday;
		this._wendesday = ((EventPreferencesTime)fromEvent._eventPreferences)._wendesday;
		this._thursday = ((EventPreferencesTime)fromEvent._eventPreferences)._thursday;
		this._friday = ((EventPreferencesTime)fromEvent._eventPreferences)._friday;
		this._saturday = ((EventPreferencesTime)fromEvent._eventPreferences)._saturday;
		this._startTime = ((EventPreferencesTime)fromEvent._eventPreferences)._startTime;
		this._endTime = ((EventPreferencesTime)fromEvent._eventPreferences)._endTime;
		this._useEndTime = ((EventPreferencesTime)fromEvent._eventPreferences)._useEndTime;
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
	public void loadSharedPrefereces(SharedPreferences preferences)
	{
    	Editor editor = preferences.edit();
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
		String descr = description;

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
    	
        Calendar calendar = new GregorianCalendar();

        calendar.setTimeInMillis(this._startTime);
		descr = descr + "- ";
		descr = descr + DateFormat.getTimeFormat(context).format(new Date(calendar.getTimeInMillis()));
		if (this._useEndTime)
		{
	        calendar.setTimeInMillis(this._endTime);
			descr = descr + "-";
			descr = descr + DateFormat.getTimeFormat(context).format(new Date(calendar.getTimeInMillis()));
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
	
	
}
