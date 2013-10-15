package sk.henrichg.phoneprofiles;

import java.lang.ref.WeakReference;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class PhoneProfilesService extends Service {
	
	//private final IBinder mBinder = new MyBinder();
	
	private Context context = null;
	
	private ProfilesDataWrapper profilesDataWrapper = null;

	// messages from GUI
	//public static final int MSG_RELOAD_DATA = 1;
	//public static final int MSG_ACTIVATE_PROFILE = 2;
	//public static final int MSG_ACTIVATE_PROFILE_INTERACTIVE = 3;
	public static final int MSG_PROFILE_ACTIVATED = 4;
	public static final int MSG_PROFILE_ADDED = 5;
	public static final int MSG_PROFILE_UPDATED = 6;
	public static final int MSG_PROFILE_DELETED = 7;
	public static final int MSG_ALL_PROFILES_DELETED = 8;
	public static final int MSG_EVENT_ADDED = 9;
	public static final int MSG_EVENT_UPDATED = 10;
	public static final int MSG_EVENT_DELETED = 11;
	public static final int MSG_ALL_EVENTS_DELETED = 12;
	public static final int MSG_DATA_IMPORTED = 13;
	
	// Target we publish for clients to send messages to IncomingHandler.
	final Messenger messenger = new Messenger(new IncomingHandler(this));   	    

    static class IncomingHandler extends Handler { // Handler of incoming messages from clients.

    	private final WeakReference<PhoneProfilesService> serviceWakeReference; 
    	
    	IncomingHandler(PhoneProfilesService service) {
            this.serviceWakeReference = new WeakReference<PhoneProfilesService>(service);
        }    	
    	
    	@Override
        public void handleMessage(Message msg) {
    		
    		Log.e("PhoneProfilesService.IncommingHandler.handleMessage",msg.what+"");
    		
    		PhoneProfilesService service = serviceWakeReference.get();
    		
            switch (msg.what) {
        /*    case MSG_RELOAD_DATA:
                service.reloadData();
                break;
            case MSG_ACTIVATE_PROFILE:
            	service.activateProfile(msg.getData().getLong(GlobalData.EXTRA_PROFILE_ID), false);
            	break;
            case MSG_ACTIVATE_PROFILE_INTERACTIVE:
            	service.activateProfile(msg.getData().getLong(GlobalData.EXTRA_PROFILE_ID), true);
            	break;   */
            case MSG_PROFILE_ACTIVATED:
            	service.setActivatedProfile(msg.getData().getLong(GlobalData.EXTRA_PROFILE_ID),
            								msg.getData().getInt(GlobalData.EXTRA_START_APP_SOURCE)
            			                    );
            	break;
            case MSG_PROFILE_ADDED:
            	service.profileAdded(msg.getData().getLong(GlobalData.EXTRA_PROFILE_ID));
            	break;
            case MSG_PROFILE_UPDATED:
            	service.profileUpdated(msg.getData().getLong(GlobalData.EXTRA_PROFILE_ID));
            	break;
            case MSG_PROFILE_DELETED:
            	service.profileDeleted(msg.getData().getLong(GlobalData.EXTRA_PROFILE_ID));
            	break;
            case MSG_ALL_PROFILES_DELETED:
            	service.allProfilesDeleted();
            	break;
            case MSG_EVENT_ADDED:
            	service.eventAdded(msg.getData().getLong(GlobalData.EXTRA_EVENT_ID));
            	break;
            case MSG_EVENT_UPDATED:
            	service.eventUpdated(msg.getData().getLong(GlobalData.EXTRA_EVENT_ID));
            	break;
            case MSG_EVENT_DELETED:
            	service.eventDeleted(msg.getData().getLong(GlobalData.EXTRA_EVENT_ID));
            	break;
            case MSG_ALL_EVENTS_DELETED:
            	service.allEventsDeleted();
            	break;
            case MSG_DATA_IMPORTED:
            	service.dataImported();
            	break;

            default:
                super.handleMessage(msg);
            }
        }
    }	
	
	@Override
	public void onCreate()
	{
		//Log.d("PhoneProfilesService.onCreate", "xxx");
		
		// initialization
  	    context = getApplicationContext();
  	    profilesDataWrapper = new ProfilesDataWrapper(context, false, false, 0);
  	    reloadData();
  	    
  	    GlobalData.loadPreferences(context);
  	    
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Log.d("PhoneProfilesService.onStartCommand", "xxx");
		
		//return Service.START_NOT_STICKY;
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy()
	{
		//Log.d("PhoneProfilesService.onDestroy", "xxx");
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		//Log.d("PhoneProfilesService.onBind","xxx");
		return messenger.getBinder();
	}
	
	@Override
	public boolean onUnbind(Intent intent)
	{
		//Log.d("PhoneProfilesService.onUnbind","xxx");
		return false;
	}

/*	public class MyBinder extends Binder {
		PhoneProfilesService getService() {
	      return PhoneProfilesService.this;
	    } 
	}*/
	
    //-------------------------------------------

	private void reloadData()
	{
		//Log.d("PhoneProfilesService.reloadData","xxx");
		profilesDataWrapper.reloadProfilesData();
		profilesDataWrapper.reloadEventsData();
		profilesDataWrapper.reloadProfileStack();
		//TODO - tu spravit testy a spustenie eventov
	}
	
	private void activateProfile(long profile_id, boolean interactive)
	{
		//Log.d("PhoneProfilesService.activateProfile",profile_id+"");
		Intent intent = new Intent(this, BackgroundActivateProfileActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    if (interactive)
			intent.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_SERVICE_INTERACTIVE);
	    else
	    	intent.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_SERVICE);
		intent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile_id);
	    startActivity(intent);		

	    Profile profile = profilesDataWrapper.getProfileById(profile_id); 
    	profilesDataWrapper.activateProfile(profile);
	}
	
	private void setActivatedProfile(long profile_id, int startupSource)
	{
		//Log.e("PhoneProfilesService.setActivatedProfile",profile_id+"");
		Profile profile = profilesDataWrapper.getProfileById(profile_id); 
    	profilesDataWrapper.activateProfile(profile);
    	pauseAllEvents();
	} 
	
	// pauses all events without activating profiles from profileStack
	// for manual activation from gui
	private void pauseAllEvents()
	{
		for (Event event : profilesDataWrapper.getEventList())
		{
			if (event._status == Event.ESTATUS_RUNNING)
				event.pauseEvent(profilesDataWrapper);
		}
	}
	
	private void profileAdded(long profile_id)
	{
		Profile profile = profilesDataWrapper.getDatabaseHandler().getProfile(profile_id);
		// profile order not relevant for service
		if (profile != null)
			profilesDataWrapper.getProfileList().add(profile);
	}
	
	private void profileUpdated(long profile_id)
	{
		Profile profileInDB = profilesDataWrapper.getDatabaseHandler().getProfile(profile_id);
		Profile profileInList = profilesDataWrapper.getProfileById(profile_id);
		int location = profilesDataWrapper.getProfileList().indexOf(profileInList);
		if (location != -1)
			profilesDataWrapper.getProfileList().set(location, profileInDB);
	}
	
	private void profileDeleted(long profile_id)
	{
		Profile profileInList = profilesDataWrapper.getProfileById(profile_id);
		profilesDataWrapper.deleteProfile(profileInList);
	}
	
	private void allProfilesDeleted()
	{
		profilesDataWrapper.deleteAllProfiles();
	}
	
	private void eventAdded(long event_id)
	{
		Event eventInDB = profilesDataWrapper.getDatabaseHandler().getEvent(event_id);
		Event eventInList = profilesDataWrapper.getEventById(event_id);
		// event order not relevant for service
		if (eventInDB != null)
		{
			int location = profilesDataWrapper.getEventList().indexOf(eventInList);
			if (location != -1)
			{
				// event exists in list, do update
				eventUpdated(event_id);
			}
			else
			{
				profilesDataWrapper.getEventList().add(eventInDB);
				if (eventInDB._status == Event.ESTATUS_STOP)
					// from gui is set status into stop
					eventInDB.stopEvent(profilesDataWrapper);
				else
				{
					Profile profile = eventInDB.pauseEvent(profilesDataWrapper);
					//TODO - aktivuj vrateny profil
				}
			}
		}
	}
	
	private void eventUpdated(long event_id)
	{
		Event eventInDB = profilesDataWrapper.getDatabaseHandler().getEvent(event_id);
		Event eventInList = profilesDataWrapper.getEventById(event_id);
		int location = profilesDataWrapper.getEventList().indexOf(eventInList);
		if (location != -1)
		{
			// stop old event
			eventInList.stopEvent(profilesDataWrapper);
			profilesDataWrapper.getEventList().set(location, eventInDB);
			// set status for new event
			if (eventInDB._status == Event.ESTATUS_STOP)
				// from gui is set status into stop
				eventInDB.stopEvent(profilesDataWrapper);
			else
			{
				Profile profile = eventInDB.pauseEvent(profilesDataWrapper);
				//TODO - aktivuj vrateny profil
			}
		}
		else
		{
			// event not exists in list do add
			eventAdded(event_id);
		}
	}
	
	private void eventDeleted(long event_id)
	{
		Event eventInList = profilesDataWrapper.getEventById(event_id);
		int location = profilesDataWrapper.getEventList().indexOf(eventInList);
		if (location != -1)
		{
			eventInList.stopEvent(profilesDataWrapper);
			profilesDataWrapper.getEventList().remove(location);
		}
	}
	
	private void allEventsDeleted()
	{
		while (profilesDataWrapper.getEventList().size() > 0)
		{
			eventDeleted(profilesDataWrapper.getEventList().get(0)._id);
		}
	}
	
	private void dataImported()
	{
		// delete all events with stopping events
		allEventsDeleted();
		// reload all datas
		reloadData();
	}
}
