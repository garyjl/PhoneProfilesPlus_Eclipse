package sk.henrichg.phoneprofiles;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

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
	public void loadSharedPrefereces(SharedPreferences preferences)
	{
    	Editor editor = preferences.edit();
    	String sValue = "";
    	if (this._sunday) sValue = sValue + "1|"; else sValue = sValue + "0|";
    	if (this._monday) sValue = sValue + "1|"; else sValue = sValue + "0|";
    	if (this._tuesday) sValue = sValue + "1|"; else sValue = sValue + "0|";
    	if (this._wendesday) sValue = sValue + "1|"; else sValue = sValue + "0|";
    	if (this._thursday) sValue = sValue + "1|"; else sValue = sValue + "0|";
    	if (this._friday) sValue = sValue + "1|"; else sValue = sValue + "0|";
    	if (this._saturday) sValue = sValue + "1|"; else sValue = sValue + "0|";
        editor.putString(PREF_EVENT_TIME_DAYS, sValue);
        editor.putLong(PREF_EVENT_TIME_START_TIME, this._startTime);
        editor.putLong(PREF_EVENT_TIME_END_TIME, this._endTime);
        editor.putBoolean(PREF_EVENT_TIME_USE_END_TIME, this._useEndTime);
		editor.commit();
	}

	@Override
	public void saveSharedPrefereces(SharedPreferences preferences)
	{
		String sDays = preferences.getString(PREF_EVENT_TIME_DAYS, "0|0|0|0|0|0|0");
		String[] splits = sDays.split("\\|");
		this._sunday = splits[0].equals("1");
		this._monday = splits[1].equals("1");
		this._tuesday = splits[2].equals("1");
		this._wendesday = splits[3].equals("1");
		this._thursday = splits[4].equals("1");
		this._friday = splits[5].equals("1");
		this._saturday = splits[6].equals("1");
		this._startTime = preferences.getLong(PREF_EVENT_TIME_START_TIME, System.currentTimeMillis());
		this._endTime = preferences.getLong(PREF_EVENT_TIME_END_TIME, System.currentTimeMillis());
		this._useEndTime = preferences.getBoolean(PREF_EVENT_TIME_USE_END_TIME, false);
	}
	
	@Override
	public String getPreferencesDescription(String description)
	{
		String descr = description;
		
		descr = descr + "time";
		
		return descr;
	}
	
}
