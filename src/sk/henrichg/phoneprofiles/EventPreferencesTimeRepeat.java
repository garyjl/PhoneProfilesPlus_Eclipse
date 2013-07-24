package sk.henrichg.phoneprofiles;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class EventPreferencesTimeRepeat extends EventPreferences {

	public boolean _sunday;
	public boolean _monday;
	public boolean _tuesday;
	public boolean _wendesday;
	public boolean _thursday;
	public boolean _friday;
	public boolean _saturday;
	public int _startHour;
	public int _startMinute;
	public int _endHour;
	public int _endMinute;
	
	public EventPreferencesTimeRepeat(Event event,
										boolean sunday,
										boolean monday,
										boolean tuesday,
										boolean wendesday,
										boolean thursday,
										boolean friday,
										boolean saturday,
										int startHour,
										int startMinute,
										int endHour,
										int endMinute)
	{
		super(event);

		this._sunday = sunday;
		this._monday = monday;
		this._tuesday = tuesday;
		this._wendesday = wendesday;
		this._thursday = thursday;
		this._friday = friday;
		this._saturday = saturday;
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
