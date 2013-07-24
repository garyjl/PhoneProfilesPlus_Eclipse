package sk.henrichg.phoneprofiles;

import android.content.SharedPreferences;

public class EventPreferences {
	
	public Event _event;
	public int _preferencesResourceID;
	
	public EventPreferences()
	{
		
	}
	
	public EventPreferences(Event event)
	{
		_event = event;
	}

	public void loadSharedPrefereces(SharedPreferences preferences)
	{
	}

	public void saveSharedPrefereces(SharedPreferences preferences)
	{
	}

}
