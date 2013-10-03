package sk.henrichg.phoneprofiles;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class Event {
	
	public long _id;
	public String _name;
	public int _type;
	public long _fkProfile;
	public int _status;

	public EventPreferences _eventPreferences;
	public int _typeOld;
	public EventPreferences _eventPreferencesOld;
	
	public static final int ETYPE_TIME = 1;
	
	public static final int ESTATUS_STOP = 0;
	public static final int ESTATUS_PAUSE = 1;
	public static final int ESTATUS_RUNNING = 2;
	
    static final String PREF_EVENT_ENABLED = "eventEnabled";
    static final String PREF_EVENT_NAME = "eventName";
    static final String PREF_EVENT_TYPE = "eventType";
    static final String PREF_EVENT_PROFILE = "eventProfile";
	
	

	// Empty constructorn
	public Event(){
		
	}
	
	// constructor
	public Event(long id, 
		         String name,
		         int type,
		         long fkProfile,
		         int status)
	{
		this._id = id;
		this._name = name;
        this._type = type;
        this._fkProfile = fkProfile;
        this._status = status;
        
        createEventPreferences();
	}
	
	// constructor
	public Event(String name,
	         	 int type,
	         	 long fkProfile,
	         	 int status)
	{
		this._name = name;
	    this._type = type;
	    this._fkProfile = fkProfile;
        this._status = status;
	    
	    createEventPreferences();
	}
	
	public void createEventPreferences()
	{
		//Log.e("Event.createEventPreferences","type="+_type);
        switch (this._type)
        {
        case ETYPE_TIME:
        	this._eventPreferences = new EventPreferencesTime(this, false, false, false, false, false, false, false, 0, 0, false);
        	break;
        }
	}
	
	public void copyEventPreferences(Event fromEvent)
	{
		this._eventPreferences.copyPreferences(fromEvent);
	}
	
	public void changeEventType(int type)
	{
		this._typeOld = this._type;
		this._eventPreferencesOld = this._eventPreferences;
		
		this._type = type;
		createEventPreferences();
	}
	
	public void undoEventType()
	{
		if (this._typeOld != 0)
		{
			this._type = this._typeOld;
			this._eventPreferences = this._eventPreferencesOld;
		}
		
		this._typeOld = 0;
		this._eventPreferencesOld = null;
	}
	
	public boolean isRunable()
	{
		return  (this._fkProfile != 0) &&
				(this._eventPreferences != null) &&
				(this._eventPreferences.isRunable());
	}
	
	public void loadSharedPrefereces(SharedPreferences preferences)
	{
    	Editor editor = preferences.edit();
        editor.putString(PREF_EVENT_NAME, this._name);
        editor.putString(PREF_EVENT_TYPE, Integer.toString(this._type));
        editor.putString(PREF_EVENT_PROFILE, Long.toString(this._fkProfile));
        editor.putBoolean(PREF_EVENT_ENABLED, this._status == ESTATUS_PAUSE);
        this._eventPreferences.loadSharedPrefereces(preferences);
		editor.commit();
	}

	public void saveSharedPrefereces(SharedPreferences preferences)
	{
    	this._name = preferences.getString(PREF_EVENT_NAME, "");
		this._type = Integer.parseInt(preferences.getString(PREF_EVENT_TYPE, "0"));
		this._fkProfile = Long.parseLong(preferences.getString(PREF_EVENT_PROFILE, "0"));
		this._status = (preferences.getBoolean(PREF_EVENT_ENABLED, false)) ? ESTATUS_PAUSE : ESTATUS_STOP;
		this._eventPreferences.saveSharedPrefereces(preferences);
		
		if (!this.isRunable())
			this._status = ESTATUS_STOP;
		
		this._typeOld = 0;
		this._eventPreferencesOld = null;
	}
	
	public void setSummary(PreferenceManager prefMng, String key, String value, Context context)
	{
		if (key.equals(PREF_EVENT_NAME))
		{	
	        prefMng.findPreference(key).setSummary(value);
		}
		if (key.equals(PREF_EVENT_TYPE))
		{	
			String sEventType = value;
			int iEventType;
			try {
				iEventType = Integer.parseInt(sEventType);
			} catch (Exception e) {
				iEventType = 1;
			}
			
	    	for (int pos = 0; pos < EventTypePreferenceAdapter.eventTypes.length; pos++)
	    	{
	    		if (iEventType == EventTypePreferenceAdapter.eventTypes[pos])
	    		{
	    	        prefMng.findPreference(key).setSummary(EventTypePreferenceAdapter.eventTypeNameIds[pos]);
	    		}
	    	}
		}
		if (key.equals(PREF_EVENT_PROFILE))
		{
			String sProfileId = value;
			long lProfileId;
			try {
				lProfileId = Long.parseLong(sProfileId);
			} catch (Exception e) {
				lProfileId = 0;
			}
		    Profile profile = EditorProfilesActivity.profilesDataWrapper.getProfileById(lProfileId);
		    if (profile != null)
		    {
    	        prefMng.findPreference(key).setSummary(profile._name);
		    }
		    else
		    {
    	        prefMng.findPreference(key).setSummary(context.getResources().getString(R.string.event_preferences_profile_not_set));
		    }
		}
	}

	public void setSummary(PreferenceManager prefMng, String key, SharedPreferences preferences, Context context)
	{
		if (key.equals(PREF_EVENT_NAME) ||
			key.equals(PREF_EVENT_TYPE) ||
			key.equals(PREF_EVENT_PROFILE))
			setSummary(prefMng, key, preferences.getString(key, ""), context);
	}
	
	public void setAllSummary(PreferenceManager prefMng, Context context)
	{
		setSummary(prefMng, PREF_EVENT_NAME, _name, context);
		setSummary(prefMng, PREF_EVENT_TYPE, Integer.toString(_type), context);
		setSummary(prefMng, PREF_EVENT_PROFILE, Long.toString(_fkProfile), context);
	}
	
	public Profile startEvent(ProfilesDataWrapper profilesDataWrapper,
							Profile activeProfile, 
			                List<Profile> profileStack)
	{
		// spustenie eventu ma sposobit aktivaciu priradeneho profilu
		// do profileStack vlozime activeProfile
		// f. vrati profil, ktory service aktivuje
		// ak vrati null, service profil neaktivuje

		
		profileStack.add(activeProfile);
		Profile profile = profilesDataWrapper.getProfileById(_fkProfile);
		
		this._status = ESTATUS_RUNNING;
		
		return profile;
	}
	
	public Profile pauseEvent(ProfilesDataWrapper profilesDataWrapper, 
							List<Profile> profileStack)
	{
		// pozastavenie eventu ma sposobit aktivovanie profilu z profileStack
		// vyberieme posledny zo stackProfile
		// ak to nie je tymto eventom aktivovany profil, tak ho aktivujeme 
		// posledny profil zo stackProfile vyhodime
		// malo by to vyriesit vnorene aj prekrizene eventy
		// f. vrati profil, ktory service aktivuje
		// ak vrati null, service profil neaktivuje

		Profile profile = null;
		if (profileStack.size() > 0)
		{
			profile = profileStack.get(profileStack.size()-1);
			if (profile == profilesDataWrapper.getProfileById(_fkProfile))
			{
				// neaktivovat profile, lebo ten zo stack je aktivovany tymto eventom
				profile = null;
			}
		}

		this._status = ESTATUS_PAUSE;
		
		return profile;
	}
	
	public Profile stopEvent(ProfilesDataWrapper profilesDataWrapper, 
							List<Profile> profileStack)
	{
		// stopnutie eventu ma sposobit jeho disablovanie
		// ak event zrovna bezi, najprv ho zapauzujeme
		// f. vrati profil, ktory service aktivuje
		// ak vrati null, service profil neaktivuje
		
		Profile profile = null;
		
		if (this._status == ESTATUS_RUNNING)
		{
			// event zrovna bezi, zapauzujeme ho
			profile = pauseEvent(profilesDataWrapper, profileStack);
		}
		
		this._status = ESTATUS_STOP;
		
		return profile;
	}
	
	
	public String getPreferecesDescription(Context context)
	{
		String description;
		
		description = "";
		
		if (_eventPreferences != null)
			description = _eventPreferences.getPreferencesDescription(description, context);
		
		return description;
	}
	
}

