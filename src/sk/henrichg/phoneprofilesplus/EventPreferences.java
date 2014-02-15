package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class EventPreferences {
	
	public Event _event;
	public int _preferencesResourceID;
	public int _iconResourceID;
	
	public EventPreferences()
	{
		
	}
	
	public EventPreferences(Event event)
	{
		_event = event;
	}
	
	public void copyPreferences(Event fromEvent)
	{
		
	}
	
	public boolean isRunable()
	{
		return true;
	}
	
	public boolean activateReturnProfile()
	{
		return true;
	}
	
	public void loadSharedPrefereces(SharedPreferences preferences)
	{
	}

	public void saveSharedPrefereces(SharedPreferences preferences)
	{
	}

	public String getPreferencesDescription(String description, Context context)
	{
		return description;
	}

	public void setSummary(PreferenceManager prefMng, String key, String value, Context context)
	{
	}
	
	public void setSummary(PreferenceManager prefMng, String key, SharedPreferences preferences, Context context)
	{
	}
	
	public void setAllSummary(PreferenceManager prefMng, Context context)
	{
	}
	
	public void setSystemRunningEvent(Context context)
	{
		
	}

	public void setSystemPauseEvent(Context context)
	{
		
	}
	
	public void removeSystemEvent(Context context)
	{
		
	}
	
}
