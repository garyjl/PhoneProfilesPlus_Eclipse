package sk.henrichg.phoneprofilesplus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class EventPreferencesBattery extends EventPreferences {

	public int _levelLow;
	public int _levelHight;
	public boolean _charging;
	
	public boolean _blocked;
	
	static final String PREF_EVENT_BATTERY_LEVEL_LOW = "eventBatteryLevelLow";
	static final String PREF_EVENT_BATTERY_LEVEL_HIGHT = "eventBatteryLevelHight";
	static final String PREF_EVENT_BATTERY_CHARGING = "eventBatteryCharging";
	
	public EventPreferencesBattery(Event event, 
									int levelLow,
									int levelHight,
									boolean charging)
	{
		super(event);
		
		this._levelLow = levelLow;
		this._levelHight = levelHight;
		this._charging = charging;
		this._blocked = false;
		
		_preferencesResourceID = R.xml.event_preferences_battery;
		_iconResourceID = R.drawable.ic_event_battery; 
	}
	
	@Override
	public void copyPreferences(Event fromEvent)
	{
		this._levelLow = ((EventPreferencesBattery)fromEvent._eventPreferences)._levelLow;
		this._levelHight = ((EventPreferencesBattery)fromEvent._eventPreferences)._levelHight;
		this._charging = ((EventPreferencesBattery)fromEvent._eventPreferences)._charging;
		this._blocked = false;
	}
	
	@Override
	public void loadSharedPrefereces(SharedPreferences preferences)
	{
		Editor editor = preferences.edit();
		editor.putString(PREF_EVENT_BATTERY_LEVEL_LOW, String.valueOf(this._levelLow));
		editor.putString(PREF_EVENT_BATTERY_LEVEL_HIGHT, String.valueOf(this._levelHight));
		editor.putBoolean(PREF_EVENT_BATTERY_CHARGING, this._charging);
		editor.commit();
	}
	
	@Override
	public void saveSharedPrefereces(SharedPreferences preferences)
	{
		String sLevel;
		int iLevel;
		
		sLevel = preferences.getString(PREF_EVENT_BATTERY_LEVEL_LOW, "0");
		if (sLevel == "") sLevel = "0";
		iLevel = Integer.parseInt(sLevel);
		if ((iLevel < 0) || (iLevel > 100)) iLevel = 0;
		this._levelLow= iLevel;

		sLevel = preferences.getString(PREF_EVENT_BATTERY_LEVEL_HIGHT, "100");
		if (sLevel == "") sLevel = "100";
		iLevel = Integer.parseInt(sLevel);
		if ((iLevel < 0) || (iLevel > 100)) iLevel = 100;
		this._levelHight= iLevel;
		
		this._charging = preferences.getBoolean(PREF_EVENT_BATTERY_CHARGING, false);
		
		this._blocked = false;
	
	}
	
	@Override
	public String getPreferencesDescription(String description, Context context)
	{
		String descr = description;
		
		descr = descr + context.getString(R.string.pref_event_battery_level);
		descr = descr + ": " + this._levelLow + "% - " + this._levelHight + "%";
		if (this._charging)
			descr = descr + ", " + context.getString(R.string.pref_event_battery_charging);
		
		return descr;
	}
	
	@Override
	public void setSummary(PreferenceManager prefMng, String key, String value, Context context)
	{
		if (key.equals(PREF_EVENT_BATTERY_LEVEL_LOW) || key.equals(PREF_EVENT_BATTERY_LEVEL_HIGHT))
		{	
	        prefMng.findPreference(key).setSummary(value + "%");
		}
	}
	
	@Override
	public void setSummary(PreferenceManager prefMng, String key, SharedPreferences preferences, Context context)
	{
		if (key.equals(PREF_EVENT_BATTERY_LEVEL_LOW) || key.equals(PREF_EVENT_BATTERY_LEVEL_HIGHT))
		{
			setSummary(prefMng, key, preferences.getString(key, ""), context);
		}
	}
	
	@Override
	public void setAllSummary(PreferenceManager prefMng, Context context)
	{
		setSummary(prefMng, PREF_EVENT_BATTERY_LEVEL_LOW, Integer.toString(_levelLow), context);
		setSummary(prefMng, PREF_EVENT_BATTERY_LEVEL_HIGHT, Integer.toString(_levelHight), context);
	}
	
	@Override
	public boolean activateReturnProfile()
	{
		return true;
	}
	
	@Override
	public void setSystemRunningEvent(Context context)
	{
		// set alarm for state PAUSE
		setAlarm(context);
	}

	@Override
	public void setSystemPauseEvent(Context context)
	{
		// set alarm for state RUNNING
		setAlarm(context);
	}
	
	@Override
	public void removeSystemEvent(Context context)
	{
	}

	private boolean isAlarmSet(Context context)
	{
		Intent intent = new Intent(context, BatteryEventsAlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);
		return (pendingIntent != null);
	}
	
	private void setAlarm(Context context)
	{
		GlobalData.logE("EventPreferencesBattery.setAlarm","xxx");
		
		if (!isAlarmSet(context))
		{
			GlobalData.logE("EventPreferencesBattery.setAlarm","set");

			AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			
			Intent intent = new Intent(context, BatteryEventsAlarmBroadcastReceiver.class);
			
			/*
			Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 10);
            */
            
			PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
			alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
											5 * 1000,
											AlarmManager.INTERVAL_FIFTEEN_MINUTES,
											alarmIntent);
		}
	}

	@Override
	public boolean invokeBroadcastReceiver(Context context)
	{
		BatteryEventsAlarmBroadcastReceiver.doOnReceive(context, _event._id);
		
		return true;
	}
	
	
}
