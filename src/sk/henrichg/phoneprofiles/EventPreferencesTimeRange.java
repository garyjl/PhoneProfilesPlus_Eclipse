package sk.henrichg.phoneprofiles;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class EventPreferencesTimeRange extends EventPreferences {

	public int _startDay; // day of week
	public int _endDay;   // day of week
	public int _startHour;
	public int _startMinute;
	public int _endHour;
	public int _endMinute;
	
	public EventPreferencesTimeRange(Event event,
									int startDay,
									int endDay,
									int startHour,
									int startMinute,
									int endHour,
									int endMinute)
	{
		super(event);

		this._startDay = startDay;
		this._endDay = endDay;
		this._startHour = startHour;
		this._startMinute = startMinute;
		this._endHour = endHour;
		this._endMinute = endMinute;
		
		//TODO set _preferencesResourceID
		_preferencesResourceID = 1;
	}

	@Override
	public void loadSharedPrefereces(SharedPreferences preferences)
	{
    	Editor editor = preferences.edit();
    	//TODO add for load into preferences
		editor.commit();
	}

	@Override
	public void saveSharedPrefereces(SharedPreferences preferences)
	{
		//TODO add for savie from preferences
	}
	
}
