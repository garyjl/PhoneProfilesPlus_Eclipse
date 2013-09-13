package sk.henrichg.phoneprofiles;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class EventPreferencesTimeRange extends EventPreferences {

	public int _startDay; // day of week
	public int _endDay;   // day of week
	public long _startTime;
	public long _endTime;

	static final String PREF_EVENT_TIME_RANGE_DAYS = "eventTimeRangeDays";
	static final String PREF_EVENT_TIME_RANGE_START_TIME = "eventTimeRangeStartTime";
	static final String PREF_EVENT_TIME_RANGE_END_TIME = "eventTimeRangeEndTime";
	
	public EventPreferencesTimeRange(Event event,
									int startDay,
									int endDay,
									long startTime,
									long endTime)
	{
		super(event);

		this._startDay = startDay;
		this._endDay = endDay;
		this._startTime = startTime;
		this._endTime = endTime;
		
		_preferencesResourceID = R.xml.event_preferences_time_range;
		_iconResourceID = R.drawable.ic_event_time_range; 
	}
	
	@Override
	public void loadSharedPrefereces(SharedPreferences preferences)
	{
    	Editor editor = preferences.edit();
        editor.putString(PREF_EVENT_TIME_RANGE_DAYS, this._startDay + "|" + this._endDay);
        editor.putLong(PREF_EVENT_TIME_RANGE_START_TIME, this._startTime);
        editor.putLong(PREF_EVENT_TIME_RANGE_END_TIME, this._endTime);
		editor.commit();
	}

	@Override
	public void saveSharedPrefereces(SharedPreferences preferences)
	{
		String sDays = preferences.getString(PREF_EVENT_TIME_RANGE_DAYS, "0|0");
		String[] splits = sDays.split("\\|");
		try {
			this._startDay = Integer.parseInt(splits[0]);
		} catch (Exception e) {
			this._startDay = 0;
		}
		try {
			this._endDay = Integer.parseInt(splits[1]);
		} catch (Exception e) {
			this._endDay = 0;
		}
		this._startTime = preferences.getLong(PREF_EVENT_TIME_RANGE_START_TIME, System.currentTimeMillis());
		this._endTime = preferences.getLong(PREF_EVENT_TIME_RANGE_END_TIME, System.currentTimeMillis());
	}
	
}
