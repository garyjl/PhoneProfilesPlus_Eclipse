package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class EventPreferencesScreen extends EventPreferences {

	public int _delay;
	
	static final String PREF_EVENT_SCREEN_ENABLED = "eventScreenEnabled";
	static final String PREF_EVENT_SCREEN_DELAY = "eventScreenDelay";
	
	public EventPreferencesScreen(Event event, 
									boolean enabled,
									int delay)
	{
		super(event, enabled);
	
		this._delay = delay;
	}
	
	@Override
	public void copyPreferences(Event fromEvent)
	{
		this._enabled = ((EventPreferencesScreen)fromEvent._eventPreferencesScreen)._enabled;
		this._delay = ((EventPreferencesScreen)fromEvent._eventPreferencesScreen)._delay;
	}
	
	@Override
	public void loadSharedPrefereces(SharedPreferences preferences)
	{
		Editor editor = preferences.edit();
        editor.putBoolean(PREF_EVENT_SCREEN_ENABLED, _enabled);
		editor.putString(PREF_EVENT_SCREEN_DELAY, String.valueOf(this._delay));
		editor.commit();
	}
	
	@Override
	public void saveSharedPrefereces(SharedPreferences preferences)
	{
		this._enabled = preferences.getBoolean(PREF_EVENT_SCREEN_ENABLED, false);
		
		String sDelay = preferences.getString(PREF_EVENT_SCREEN_DELAY, "0");
		if (sDelay.isEmpty()) sDelay = "0";
		int iDelay = Integer.parseInt(sDelay);
		if (iDelay < 0) iDelay = 0;
		this._delay = iDelay;
	}
	
	@Override
	public String getPreferencesDescription(String description, Context context)
	{
		String descr = description + context.getString(R.string.event_type_screen) + ": ";
		
		if (!this._enabled)
			descr = descr + context.getString(R.string.event_preferences_not_enabled);
		else
		{
			descr = descr + context.getString(R.string.pref_event_screen_delay);
			descr = descr + ": " + this._delay;
		}
		
		return descr;
	}
	
	@Override
	public void setSummary(PreferenceManager prefMng, String key, String value, Context context)
	{
		if (key.equals(PREF_EVENT_SCREEN_DELAY))
		{	
	        prefMng.findPreference(key).setSummary(value);
		}
	}
	
	@Override
	public void setSummary(PreferenceManager prefMng, String key, SharedPreferences preferences, Context context)
	{
		if (key.equals(PREF_EVENT_SCREEN_DELAY))
		{
			setSummary(prefMng, key, preferences.getString(key, ""), context);
		}
	}
	
	@Override
	public void setAllSummary(PreferenceManager prefMng, Context context)
	{
		setSummary(prefMng, PREF_EVENT_SCREEN_DELAY, Integer.toString(_delay), context);
	}
	
	@Override
	public boolean activateReturnProfile()
	{
		return true;
	}
	
	@Override
	public void setSystemRunningEvent(Context context)
	{
	}

	@Override
	public void setSystemPauseEvent(Context context)
	{
	}
	
	@Override
	public void removeSystemEvent(Context context)
	{
	}

}
