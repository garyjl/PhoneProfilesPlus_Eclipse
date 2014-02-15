package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class EventPreferencesBattery extends EventPreferences {

	public int _level;
	public int _levelType;
	
	static final String PREF_EVENT_BATTERY_LEVEL = "eventBatteryLevel";
	static final String PREF_EVENT_BATTERY_LEVEL_TYPE = "eventBatteryLevelType";
	
	public EventPreferencesBattery(Event event, 
									int level,
									int levelType)
	{
		super(event);
		
		this._level = level;
		this._levelType = levelType;
		
		_preferencesResourceID = R.xml.event_preferences_battery;
		_iconResourceID = R.drawable.ic_event_battery; 
	}
	
	@Override
	public void copyPreferences(Event fromEvent)
	{
		this._level = ((EventPreferencesBattery)fromEvent._eventPreferences)._level;
		this._levelType = ((EventPreferencesBattery)fromEvent._eventPreferences)._levelType;
	}
	
	@Override
	public void loadSharedPrefereces(SharedPreferences preferences)
	{
		Editor editor = preferences.edit();
		editor.putString(PREF_EVENT_BATTERY_LEVEL, String.valueOf(this._level));
		editor.putString(PREF_EVENT_BATTERY_LEVEL_TYPE, String.valueOf(this._levelType));
		editor.commit();
	}
	
	@Override
	public void saveSharedPrefereces(SharedPreferences preferences)
	{
		String sLevel;
		int iLevel;
		
		sLevel = preferences.getString(PREF_EVENT_BATTERY_LEVEL, "15");
		if (sLevel == "") sLevel = "15";
		iLevel = Integer.parseInt(sLevel);
		if ((iLevel < 0) || (iLevel > 100)) iLevel = 15;
		this._level= iLevel;

		sLevel = preferences.getString(PREF_EVENT_BATTERY_LEVEL_TYPE, "0");
		this._levelType = Integer.parseInt(sLevel);
	
	}
	
	@Override
	public String getPreferencesDescription(String description, Context context)
	{
		String descr = description;
		
		if (this._levelType == 0)
			descr = descr + context.getString(R.string.array_pref_event_battery_level_type_low);
		else
			descr = descr + context.getString(R.string.array_pref_event_battery_level_type_hight);
		descr = descr + ": " + this._level + "%";

		return descr;
	}
	
	@Override
	public void setSummary(PreferenceManager prefMng, String key, String value, Context context)
	{
		if (key.equals(PREF_EVENT_BATTERY_LEVEL))
		{	
	        prefMng.findPreference(key).setSummary(value + "%");
		}
		else
		if (key.equals(PREF_EVENT_BATTERY_LEVEL_TYPE))
		{	
			if (value.equals("0"))
		        prefMng.findPreference(key).setSummary(R.string.array_pref_event_battery_level_type_low);
			else
				prefMng.findPreference(key).setSummary(R.string.array_pref_event_battery_level_type_hight);
		}
	}
	
	@Override
	public void setSummary(PreferenceManager prefMng, String key, SharedPreferences preferences, Context context)
	{
		if (key.equals(PREF_EVENT_BATTERY_LEVEL) ||
			key.equals(PREF_EVENT_BATTERY_LEVEL_TYPE))
			setSummary(prefMng, key, preferences.getString(key, ""), context);
	}
	
	@Override
	public void setAllSummary(PreferenceManager prefMng, Context context)
	{
		setSummary(prefMng, PREF_EVENT_BATTERY_LEVEL, Integer.toString(_level), context);
		setSummary(prefMng, PREF_EVENT_BATTERY_LEVEL_TYPE, Integer.toString(_levelType), context);
	}
	
	@Override
	public boolean activateReturnProfile()
	{
		// one shot event, return profile
		return false;
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
