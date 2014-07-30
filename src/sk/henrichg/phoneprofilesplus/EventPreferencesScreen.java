package sk.henrichg.phoneprofilesplus;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.ListPreference;
import android.preference.PreferenceManager;

public class EventPreferencesScreen extends EventPreferences {

	public int _eventType;
	public int _delay;
	
	public static final int ETYPE_SCREENON = 0;
	public static final int ETYPE_SCREENOFF = 1;
	
	static final String PREF_EVENT_SCREEN_ENABLED = "eventScreenEnabled";
	static final String PREF_EVENT_SCREEN_EVENT_TYPE = "eventScreenEventType";
	static final String PREF_EVENT_SCREEN_DELAY = "eventScreenDelay";
	
	public EventPreferencesScreen(Event event, 
									boolean enabled,
									int eventType,
									int delay)
	{
		super(event, enabled);
	
		this._eventType = eventType;
		this._delay = delay;
	}
	
	@Override
	public void copyPreferences(Event fromEvent)
	{
		this._enabled = ((EventPreferencesScreen)fromEvent._eventPreferencesScreen)._enabled;
		this._eventType = ((EventPreferencesScreen)fromEvent._eventPreferencesScreen)._eventType;
		this._delay = ((EventPreferencesScreen)fromEvent._eventPreferencesScreen)._delay;
	}
	
	@Override
	public void loadSharedPrefereces(SharedPreferences preferences)
	{
		Editor editor = preferences.edit();
        editor.putBoolean(PREF_EVENT_SCREEN_ENABLED, _enabled);
		editor.putString(PREF_EVENT_SCREEN_EVENT_TYPE, String.valueOf(this._eventType));
		editor.putString(PREF_EVENT_SCREEN_DELAY, String.valueOf(this._delay));
		editor.commit();
	}
	
	@Override
	public void saveSharedPrefereces(SharedPreferences preferences)
	{
		this._enabled = preferences.getBoolean(PREF_EVENT_SCREEN_ENABLED, false);
		this._eventType = Integer.parseInt(preferences.getString(PREF_EVENT_SCREEN_EVENT_TYPE, "0"));
		
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
			descr = descr + context.getString(R.string.pref_event_screen_eventType);
			String[] eventListTypes = context.getResources().getStringArray(R.array.eventScreenEventTypeArray);
			descr = descr + ": " + eventListTypes[this._eventType] + "; ";
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
		if (key.equals(PREF_EVENT_SCREEN_EVENT_TYPE))
		{	
			ListPreference listPreference = (ListPreference)prefMng.findPreference(key);
			int index = listPreference.findIndexOfValue(value);
			CharSequence summary = (index >= 0) ? listPreference.getEntries()[index] : null;
			listPreference.setSummary(summary);
		}
	}
	
	@Override
	public void setSummary(PreferenceManager prefMng, String key, SharedPreferences preferences, Context context)
	{
		if (key.equals(PREF_EVENT_SCREEN_DELAY) || 
				key.equals(PREF_EVENT_SCREEN_EVENT_TYPE))
		{
			setSummary(prefMng, key, preferences.getString(key, ""), context);
		}
	}
	
	@Override
	public void setAllSummary(PreferenceManager prefMng, Context context)
	{
		setSummary(prefMng, PREF_EVENT_SCREEN_DELAY, Integer.toString(_delay), context);
		setSummary(prefMng, PREF_EVENT_SCREEN_EVENT_TYPE, Integer.toString(_eventType), context);
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
		
		//removeAlarm(context);
		
	}

	@Override
	public void setSystemPauseEvent(Context context)
	{
	}
	
	@Override
	public void removeSystemEvent(Context context)
	{
		// remove alarms for state STOP

		//	removeAlarm(context);
	}

	/*
	public void removeAlarm(Context context)
	{
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);

		Intent intent = new Intent(context, ScreenAlarmBroadcastReceiver.class);
	    
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), (int) _event._id, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null)
        {
       		GlobalData.logE("EventPreferencesScreen.removeAlarm","alarm found");
        		
        	alarmManager.cancel(pendingIntent);
        	pendingIntent.cancel();
        }
	}
	
	@SuppressLint("SimpleDateFormat")
	private void setAlarm(boolean startEvent, long alarmTime, Context context)
	{
	    SimpleDateFormat sdf = new SimpleDateFormat("EE d.MM.yyyy HH:mm:ss:S");
	    String result = sdf.format(alarmTime);
	    if (startEvent)
	    	GlobalData.logE("EventPreferencesScreen.setAlarm","startTime="+result);
	    else
	    	GlobalData.logE("EventPreferencesScreen.setAlarm","endTime="+result);
	    
	    Intent intent = new Intent(context, EventsTimeBroadcastReceiver.class);
	    intent.putExtra(GlobalData.EXTRA_EVENT_ID, _event._id);
	    intent.putExtra(GlobalData.EXTRA_START_SYSTEM_EVENT, startEvent);
	    
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), (int) _event._id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmTime, 24 * 60 * 60 * 1000 , pendingIntent);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, 24 * 60 * 60 * 1000 , pendingIntent);
        
	}
	*/
	
}
