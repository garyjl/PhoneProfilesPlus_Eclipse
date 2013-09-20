package sk.henrichg.phoneprofiles;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

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
    	if (this._sunday) sValue = sValue + "0|";
    	if (this._monday) sValue = sValue + "1|";
    	if (this._tuesday) sValue = sValue + "2|";
    	if (this._wendesday) sValue = sValue + "3|";
    	if (this._thursday) sValue = sValue + "4|";
    	if (this._friday) sValue = sValue + "5|";
    	if (this._saturday) sValue = sValue + "6|";
		Log.e("EventPreferencesTime.loadSharedPreferences",sValue);
        editor.putString(PREF_EVENT_TIME_DAYS, sValue);
        editor.putLong(PREF_EVENT_TIME_START_TIME, this._startTime);
        editor.putLong(PREF_EVENT_TIME_END_TIME, this._endTime);
        editor.putBoolean(PREF_EVENT_TIME_USE_END_TIME, this._useEndTime);
		editor.commit();
	}

	@Override
	public void saveSharedPrefereces(SharedPreferences preferences)
	{
		String sDays = preferences.getString(PREF_EVENT_TIME_DAYS, ListPreferenceMultiSelect.allValue);
		Log.e("EventPreferencesTime.saveSharedPreferences",sDays);
		String[] splits = sDays.split("\\|");
		if (splits[0].equals(ListPreferenceMultiSelect.allValue))
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
	public String getPreferencesDescription(String description)
	{
		String descr = description;
		
		descr = descr + "time";
		
		return descr;
	}
	
}
