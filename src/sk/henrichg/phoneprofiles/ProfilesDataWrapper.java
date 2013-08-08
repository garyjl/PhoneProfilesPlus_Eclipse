package sk.henrichg.phoneprofiles;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class ProfilesDataWrapper {

	private Context context = null;
	private boolean forGUI = false;
	private boolean generateIndicators = false;

	private DatabaseHandler databaseHandler = null;
	private ActivateProfileHelper activateProfileHelper = null;
	private List<Profile> profileList = null;
	private List<Event> eventList = null;
	
	ProfilesDataWrapper(Context c, boolean fgui, boolean generIndicators, boolean loadProfileList, boolean loadEventList)
	{
		context = c;
		forGUI = fgui;
		generateIndicators = generIndicators;
		databaseHandler = getDatabaseHandler();
		activateProfileHelper = getActivateProfileHelper();
		if (loadProfileList)
			profileList = getProfileList();
		if (loadEventList)
			eventList = getEventList();
	}
	
	public DatabaseHandler getDatabaseHandler()
	{
		if (databaseHandler == null)
			databaseHandler = new DatabaseHandler(context);
			
		return databaseHandler;
	}

	public ActivateProfileHelper getActivateProfileHelper()
	{
		if (activateProfileHelper == null)
			activateProfileHelper = new ActivateProfileHelper(); 

		return activateProfileHelper;
	}
	
	public List<Profile> getProfileList()
	{
		if (profileList == null)
		{
			profileList = getDatabaseHandler().getAllProfiles();
		
			if (forGUI)
			{
				for (Profile profile : profileList)
				{
					profile.generateIconBitmap(context);
					profile.generatePreferencesIndicator(context);
				}
			}
		}

		return profileList;
	}
	
	public List<Profile> getProfileListForActivator()
	{
		getProfileList();
		
		List<Profile> profileListForActivator = new ArrayList<Profile>();
		
		for (Profile profile : profileList)
		{
			if (profile._showInActivator)
				profileListForActivator.add(profile);
		}
		
		return profileListForActivator;
	}

	public void invalidateProfileList()
	{
		if (profileList != null)
			profileList.clear();
		profileList = null;
	}
	
	public Profile getActivatedProfile()
	{
		if (profileList == null)
		{
			//Log.d("ProfilesDataWrapper.getActivatedProfile","profileList=null");
			Profile profile = getDatabaseHandler().getActivatedProfile();
			if (forGUI && (profile != null))
			{
				//Log.d("ProfilesDataWrapper.getActivatedProfile","forGUI=true");
				profile.generateIconBitmap(context);
				profile.generatePreferencesIndicator(context);
			}
			return profile;
		}
		else
		{
			//Log.d("ProfilesDataWrapper.getActivatedProfile","profileList!=null");
			Profile profile;
			for (int i = 0; i < profileList.size(); i++)
			{
				profile = profileList.get(i); 
				if (profile._checked)
					return profile;
			}
		}
		
		return null;
	}
	
	public Profile getFirstProfile()
	{
		if (profileList == null)
		{
			Profile profile = getDatabaseHandler().getFirstProfile();
			if (forGUI && (profile != null))
			{
				profile.generateIconBitmap(context);
				profile.generatePreferencesIndicator(context);
			}
			return profile;
		}
		else
		{
			Profile profile;
			if (profileList.size() > 0)
				profile = profileList.get(0);
			else
				profile = null;
			
			return profile;
		}
	}
	
	public int getProfileItemPosition(Profile profile)
	{
		if (profileList == null)
			return getDatabaseHandler().getProfilePosition(profile);
		else
		{
			for (int i = 0; i < profileList.size(); i++)
			{
				if (profileList.get(i)._id == profile._id)
					return i;
			}
			return -1;
		}
	}
	
	public void activateProfile(Profile profile)
	{
		if (profileList == null)
			return;
		
		for (Profile p : profileList)
		{
			p._checked = false;
		}
		
		// teraz musime najst profile v profileList 
		int position = getProfileItemPosition(profile);
		if (position != -1)
		{
			// najdenemu objektu nastavime _checked
			Profile _profile = profileList.get(position);
			if (_profile != null)
				_profile._checked = true;
		}
	}
	
	public Profile getProfileById(long id)
	{
		if (profileList == null)
		{
			Profile profile = getDatabaseHandler().getProfile(id);
			if (forGUI && (profile != null))
			{
				profile.generateIconBitmap(context);
				profile.generatePreferencesIndicator(context);
			}
			return profile;
		}
		else
		{
			Profile profile;
			for (int i = 0; i < profileList.size(); i++)
			{
				profile = profileList.get(i); 
				if (profile._id == id)
					return profile;
			}
			
			return null;
		}
	}
	
	public void reloadProfilesData()
	{
		invalidateProfileList();
		getProfileList();
	}
	
	public void deleteProfile(Profile profile)
	{
		profileList.remove(profile);
		if (eventList == null)
			eventList = getEventList();
		// unlink profile from events
		for (Event event : eventList)
		{
			if (event._fkProfile == profile._id) 
				event._fkProfile = 0;
		}
	}
	
	public void deleteAllProfiles()
	{
		profileList.clear();
		if (eventList == null)
			eventList = getEventList();
		// unlink profiles from events
		for (Event event : eventList)
		{
			event._fkProfile = 0;
		}
	}
	
//---------------------------------------------------

	public List<Event> getEventList()
	{
		if (eventList == null)
		{
			eventList = getDatabaseHandler().getAllEvents();
		}

		return eventList;
	}

	public void invalidateEventList()
	{
		if (eventList != null)
			eventList.clear();
		eventList = null;
	}
	
	public Event getFirstEvent()
	{
		if (eventList == null)
		{
			Event event = getDatabaseHandler().getFirstEvent();
			return event;
		}
		else
		{
			Event event;
			if (eventList.size() > 0)
				event = eventList.get(0);
			else
				event = null;
			
			return event;
		}
	}
	
	public int getEventItemPosition(Event event)
	{
		if (eventList == null)
			return getDatabaseHandler().getEventPosition(event);
		else
		{
			for (int i = 0; i < eventList.size(); i++)
			{
				if (eventList.get(i)._id == event._id)
					return i;
			}
			return -1;
		}
	}
	
	public Event getEventById(long id)
	{
		if (eventList == null)
		{
			Event event = getDatabaseHandler().getEvent(id);
			return event;
		}
		else
		{
			Event event;
			for (int i = 0; i < eventList.size(); i++)
			{
				event = eventList.get(i); 
				if (event._id == id)
					return event;
			}
			
			return null;
		}
	}
	
	public void reloadEventsData()
	{
		invalidateEventList();
		getEventList();
	}
	
	
	//---------------------------------------------------------------------------
	
	private int msgForBind;
	private long longDataForBind;

	public Messenger phoneProfilesService = null;
	
    public ServiceConnection serviceConnection = new ServiceConnection() {

    	public void onServiceConnected(ComponentName className, IBinder service) {
    		//Log.d("ProfilesDataWrapper.onServiceConnected","xxx");
    		phoneProfilesService = new Messenger(service);

            switch (msgForBind) {
            case PhoneProfilesService.MSG_RELOAD_DATA:
        		sendMessageIntoService(msgForBind);
                break;
            case PhoneProfilesService.MSG_ACTIVATE_PROFILE:
            case PhoneProfilesService.MSG_ACTIVATE_PROFILE_INTERACTIVE:
            //case PhoneProfilesService.MSG_PROFILE_ACTIVATED:
        		sendMessageIntoServiceLong(msgForBind, longDataForBind);
            	break;
            default:
        		sendMessageIntoService(msgForBind);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
    		//Log.d("ProfilesDataWrapper.onServiceDisconnected","xxx");
        	phoneProfilesService = null;
        }

    };
	
    final Messenger mMessenger = new Messenger(new IncomingHandler());
	
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            default:
                super.handleMessage(msg);
            }
        }
    }
    
    public void sendMessageIntoService(int message)
	{
   		msgForBind = message;
        context.bindService(new Intent(context, PhoneProfilesService.class), serviceConnection, Context.BIND_AUTO_CREATE);

	    if (phoneProfilesService != null)
    	{
	
	    	try {
	            Message msg = Message.obtain(null, message);
	            msg.replyTo = mMessenger;
	            phoneProfilesService.send(msg);
	        } catch (RemoteException e) {
	            // In this case the service has crashed before we could even do anything with it
	        }
    	}
	}
	
    public void sendMessageIntoServiceLong(int message, long data)
	{
   		msgForBind = message;
   		longDataForBind = data;
        context.bindService(new Intent(context, PhoneProfilesService.class), serviceConnection, Context.BIND_AUTO_CREATE);

	    if (phoneProfilesService != null)
    	{
	    	//Log.d("ProfilesDataWrapper.sendMessageIntoServiceLong","data="+data);
	    	try {
                Bundle b = new Bundle();
                b.putLong(GlobalData.EXTRA_PROFILE_ID, data);
	            Message msg = Message.obtain(null, message);
                msg.setData(b);	            
	            phoneProfilesService.send(msg);
	        } catch (RemoteException e) {
	            // In this case the service has crashed before we could even do anything with it
	        }
    	}
	}
	
}
