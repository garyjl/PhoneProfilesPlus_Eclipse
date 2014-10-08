package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.ListPreference;
import android.preference.PreferenceManager;

public class EventPreferencesSMS extends EventPreferences {

	public String _contacts;
	public int _contactListType;
	
	static final String PREF_EVENT_SMS_ENABLED = "eventSMSEnabled";
	static final String PREF_EVENT_SMS_CONTACTS = "eventSMSContacts";
	static final String PREF_EVENT_SMS_CONTACT_LIST_TYPE = "eventSMSContactListType";
	
	static final int CONTACT_LIST_TYPE_WHITE_LIST = 0;
	static final int CONTACT_LIST_TYPE_BLACK_LIST = 1;
	static final int CONTACT_LIST_TYPE_NOT_USE = 2;
	
	public EventPreferencesSMS(Event event, 
									boolean enabled,
									String contacts,
									int contactListType)
	{
		super(event, enabled);
		
		this._contacts = contacts;
		this._contactListType = contactListType;
	}
	
	@Override
	public void copyPreferences(Event fromEvent)
	{
		this._enabled = ((EventPreferencesSMS)fromEvent._eventPreferencesSMS)._enabled;
		this._contacts = ((EventPreferencesSMS)fromEvent._eventPreferencesSMS)._contacts;
		this._contactListType = ((EventPreferencesSMS)fromEvent._eventPreferencesSMS)._contactListType;
	}
	
	@Override
	public void loadSharedPrefereces(SharedPreferences preferences)
	{
		Editor editor = preferences.edit();
        editor.putBoolean(PREF_EVENT_SMS_ENABLED, _enabled);
		editor.putString(PREF_EVENT_SMS_CONTACTS, this._contacts);
		editor.putString(PREF_EVENT_SMS_CONTACT_LIST_TYPE, String.valueOf(this._contactListType));
		editor.commit();
	}
	
	@Override
	public void saveSharedPrefereces(SharedPreferences preferences)
	{
		this._enabled = preferences.getBoolean(PREF_EVENT_SMS_ENABLED, false);
		this._contacts = preferences.getString(PREF_EVENT_SMS_CONTACTS, ""); 
		this._contactListType = Integer.parseInt(preferences.getString(PREF_EVENT_SMS_CONTACT_LIST_TYPE, "0"));
	}
	
	@Override
	public String getPreferencesDescription(String description, Context context)
	{
		String descr = description + context.getString(R.string.event_type_sms) + ": ";
		
		if (!this._enabled)
			descr = descr + context.getString(R.string.event_preferences_not_enabled);
		else
		{
			descr = descr + context.getString(R.string.pref_event_sms_contactListType);
			String[] cntactListTypes = context.getResources().getStringArray(R.array.eventSMSContactListTypeArray);
			descr = descr + ": " + cntactListTypes[this._contactListType];
		}
		
		return descr;
	}
	
	@Override
	public void setSummary(PreferenceManager prefMng, String key, String value, Context context)
	{
		if (key.equals(PREF_EVENT_SMS_CONTACT_LIST_TYPE))
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
		if (key.equals(PREF_EVENT_SMS_CONTACT_LIST_TYPE))
		{
			setSummary(prefMng, key, preferences.getString(key, ""), context);
		}
	}
	
	@Override
	public void setAllSummary(PreferenceManager prefMng, Context context)
	{
		setSummary(prefMng, PREF_EVENT_SMS_CONTACT_LIST_TYPE, Integer.toString(_contactListType), context);
	}
	
	@Override
	public boolean isRunable()
	{
		
		boolean runable = super.isRunable();

		runable = runable && (_contactListType == CONTACT_LIST_TYPE_NOT_USE || (!_contacts.isEmpty()));

		return runable;
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
	}

	@Override
	public void setSystemPauseEvent(Context context)
	{
		// set alarm for state RUNNING
	}
	
	@Override
	public void removeSystemEvent(Context context)
	{
	}

}
