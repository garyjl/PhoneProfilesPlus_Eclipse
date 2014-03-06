package sk.henrichg.phoneprofilesplus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class EventPreferencesBattery extends EventPreferences {

	public int _level;
	public int _detectorType;
	
	public boolean _blocked;
	
	static final int DETECTOR_TYPE_LOW_LEVEL = 0;
	static final int DETECTOR_TYPE_HIGHT_LEVEL = 1;
	static final int DETECTOR_TYPE_PLUG = 2;
	
	static final String PREF_EVENT_BATTERY_LEVEL = "eventBatteryLevel";
	static final String PREF_EVENT_BATTERY_DETECTOR_TYPE = "eventBatteryDetectorType";
	
	public EventPreferencesBattery(Event event, 
									int level,
									int detectorType)
	{
		super(event);
		
		this._level = level;
		this._detectorType = detectorType;
		this._blocked = false;
		
		// removed detector type unplug
		if (detectorType > DETECTOR_TYPE_PLUG)
			this._detectorType = DETECTOR_TYPE_PLUG;
			
		
		_preferencesResourceID = R.xml.event_preferences_battery;
		_iconResourceID = R.drawable.ic_event_battery; 
	}
	
	@Override
	public void copyPreferences(Event fromEvent)
	{
		this._level = ((EventPreferencesBattery)fromEvent._eventPreferences)._level;
		this._detectorType = ((EventPreferencesBattery)fromEvent._eventPreferences)._detectorType;
		this._blocked = false;
	}
	
	@Override
	public void loadSharedPrefereces(SharedPreferences preferences)
	{
		Editor editor = preferences.edit();
		editor.putString(PREF_EVENT_BATTERY_LEVEL, String.valueOf(this._level));
		editor.putString(PREF_EVENT_BATTERY_DETECTOR_TYPE, String.valueOf(this._detectorType));
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

		sLevel = preferences.getString(PREF_EVENT_BATTERY_DETECTOR_TYPE, "0");
		this._detectorType = Integer.parseInt(sLevel);
		this._blocked = false;
	
	}
	
	@Override
	public String getPreferencesDescription(String description, Context context)
	{
		String descr = description;
		
		if (this._detectorType == 0)
		{
			descr = descr + context.getString(R.string.array_pref_event_battery_detector_type_low_level);
			descr = descr + ": " + this._level + "%";
		}
		else
		if (this._detectorType == 1)
		{
			descr = descr + context.getString(R.string.array_pref_event_battery_detector_type_hight_level);
			descr = descr + ": " + this._level + "%";
		}
		else
		if (this._detectorType == 2)
			descr = descr + context.getString(R.string.array_pref_event_battery_detector_type_plug);

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
		if (key.equals(PREF_EVENT_BATTERY_DETECTOR_TYPE))
		{	
			if (value.equals("0"))
		        prefMng.findPreference(key).setSummary(R.string.array_pref_event_battery_detector_type_low_level);
			else
			if (value.equals("1"))
				prefMng.findPreference(key).setSummary(R.string.array_pref_event_battery_detector_type_hight_level);
			else
			if (value.equals("2"))
				prefMng.findPreference(key).setSummary(R.string.array_pref_event_battery_detector_type_plug);
		}
	}
	
	@Override
	public void setSummary(PreferenceManager prefMng, String key, SharedPreferences preferences, Context context)
	{
		if (key.equals(PREF_EVENT_BATTERY_LEVEL) ||
			key.equals(PREF_EVENT_BATTERY_DETECTOR_TYPE))
		{
			setSummary(prefMng, key, preferences.getString(key, ""), context);
			
			if (key.equals(PREF_EVENT_BATTERY_DETECTOR_TYPE))
				disableDependedPref(prefMng, PREF_EVENT_BATTERY_DETECTOR_TYPE, preferences.getString(key, ""));
		}
	}
	
	@Override
	public void setAllSummary(PreferenceManager prefMng, Context context)
	{
		setSummary(prefMng, PREF_EVENT_BATTERY_LEVEL, Integer.toString(_level), context);
		setSummary(prefMng, PREF_EVENT_BATTERY_DETECTOR_TYPE, Integer.toString(_detectorType), context);
		
		disableDependedPref(prefMng, PREF_EVENT_BATTERY_DETECTOR_TYPE, Integer.toString(_detectorType));
	}
	
	private void disableDependedPref(PreferenceManager prefMng, String key, Object value)
	{
		String sValue = value.toString();
		
		if (key.equals(PREF_EVENT_BATTERY_DETECTOR_TYPE))
		{
			boolean enabled = (sValue.equals("0") || sValue.equals("1"));
			prefMng.findPreference(PREF_EVENT_BATTERY_LEVEL).setEnabled(enabled);
		}
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
		if ((_detectorType == DETECTOR_TYPE_LOW_LEVEL) || (_detectorType == DETECTOR_TYPE_HIGHT_LEVEL))
			setAlarm(context);
	}

	@Override
	public void setSystemPauseEvent(Context context)
	{
		// set alarm for state RUNNING
		if ((_detectorType == DETECTOR_TYPE_LOW_LEVEL) || (_detectorType == DETECTOR_TYPE_HIGHT_LEVEL))
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
		if ((_detectorType == DETECTOR_TYPE_LOW_LEVEL) || (_detectorType == DETECTOR_TYPE_HIGHT_LEVEL))
			BatteryEventsAlarmBroadcastReceiver.doOnReceive(context, _event._id);
		else
			PowerConnectionReceiver.doOnReceive(context, _event._id);
		
		return true;
	}
	
	
}
