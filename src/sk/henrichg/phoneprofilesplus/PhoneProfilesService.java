package sk.henrichg.phoneprofilesplus;

import java.lang.ref.WeakReference;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

public class PhoneProfilesService extends Service {
	
	//private final IBinder mBinder = new MyBinder();
	
	private Context context = null;
	
	private DataWrapper dataWrapper = null;

	// messages from GUI
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
    		
    		//Log.e("PhoneProfilesService.IncommingHandler.handleMessage",msg.what+"");
    		
    		PhoneProfilesService service = serviceWakeReference.get();
    		
            switch (msg.what) {
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
  	    dataWrapper = new DataWrapper(context, false, false, 0);
  	    reloadData();
		//TODO - tu spravit testy a spustenie eventov
  	    
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
		dataWrapper.reloadProfilesData();
		dataWrapper.reloadEventsData();
		dataWrapper.reloadEventTimelineList();
	}
	
	private void setActivatedProfile(long profile_id, int startupSource)
	{
		//Log.e("PhoneProfilesService.setActivatedProfile",profile_id+"");
		Profile profile = dataWrapper.getProfileById(profile_id); 
    	dataWrapper.activateProfile(profile);
    	pauseAllEvents();
	} 
	
	// pauses all events without activating profiles from Timeline
	// for manual activation from gui
	private void pauseAllEvents()
	{
		for (Event event : dataWrapper.getEventList())
		{
			if (event._status == Event.ESTATUS_RUNNING)
				event.pauseEvent(dataWrapper, false);
		}
	}
	
	private void profileAdded(long profile_id)
	{
		Profile profile = dataWrapper.getDatabaseHandler().getProfile(profile_id);
		// profile order not relevant for service
		if (profile != null)
			dataWrapper.getProfileList().add(profile);
	}
	
	private void profileUpdated(long profile_id)
	{
		Profile profileInDB = dataWrapper.getDatabaseHandler().getProfile(profile_id);
		Profile profileInList = dataWrapper.getProfileById(profile_id);
		int location = dataWrapper.getProfileList().indexOf(profileInList);
		if (location != -1)
			dataWrapper.getProfileList().set(location, profileInDB);
	}
	
	private void profileDeleted(long profile_id)
	{
		Profile profileInList = dataWrapper.getProfileById(profile_id);
		dataWrapper.deleteProfileFromService(profileInList);
	}
	
	private void allProfilesDeleted()
	{
		dataWrapper.deleteAllProfilesFromService();
	}
	
	private void eventAdded(long event_id)
	{
		Event eventInDB = dataWrapper.getDatabaseHandler().getEvent(event_id);
		Event eventInList = dataWrapper.getEventById(event_id);
		// event order not relevant for service
		if (eventInDB != null)
		{
			int location = dataWrapper.getEventList().indexOf(eventInList);
			if (location != -1)
			{
				// event exists in list, do update
				eventUpdated(event_id);
			}
			else
			{
				dataWrapper.getEventList().add(eventInDB);
				if (eventInDB._status == Event.ESTATUS_STOP)
					// from gui is set status into stop
					eventInDB.stopEvent(dataWrapper, false);
				else
				{
					eventInDB.pauseEvent(dataWrapper, false);
				}
			}
		}
	}
	
	private void eventUpdated(long event_id)
	{
		Event eventInDB = dataWrapper.getDatabaseHandler().getEvent(event_id);
		Event eventInList = dataWrapper.getEventById(event_id);
		int location = dataWrapper.getEventList().indexOf(eventInList);
		if (location != -1)
		{
			// stop old event
			eventInList.stopEvent(dataWrapper, false);
			dataWrapper.getEventList().set(location, eventInDB);
			// set status for new event
			if (eventInDB._status == Event.ESTATUS_STOP)
				// from gui is set status into stop
				eventInDB.stopEvent(dataWrapper, false);
			else
			{
				eventInDB.pauseEvent(dataWrapper, false);
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
		Event eventInList = dataWrapper.getEventById(event_id);
		int location = dataWrapper.getEventList().indexOf(eventInList);
		if (location != -1)
		{
			eventInList.stopEvent(dataWrapper, false);
			dataWrapper.getEventList().remove(location);
		}
	}
	
	private void allEventsDeleted()
	{
		while (dataWrapper.getEventList().size() > 0)
		{
			eventDeleted(dataWrapper.getEventList().get(0)._id);
		}
	}
	
	private void dataImported()
	{
		// delete all events with stopping events
		allEventsDeleted();
		// reload all datas
		reloadData();
		//TODO - tu spravit testy a spustenie eventov
	}
}
