package sk.henrichg.phoneprofiles;

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
	
	// TOTO JE PROBLEM!!!
	// KED PRESUNIE OS SERVICE DO NOVEHO PROCESU, STATIC KOMPONENTY
	// NEFUNGUJU
	//public static ProfilesDataWrapper profilesDataWrapper = null;
	private ProfilesDataWrapper profilesDataWrapper = null;

	// messages from GUI
	public static final int MSG_RELOAD_DATA = 1;
	public static final int MSG_ACTIVATE_PROFILE = 2;
	
	// Target we publish for clients to send messages to IncomingHandler.
	final Messenger messenger = new Messenger(new IncomingHandler());   	    

    class IncomingHandler extends Handler { // Handler of incoming messages from clients.
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_RELOAD_DATA:
                reloadData();
                break;
            case MSG_ACTIVATE_PROFILE:
            	activateProfile(msg.getData().getLong(GlobalData.EXTRA_PROFILE_ID));
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
  	    profilesDataWrapper = new ProfilesDataWrapper(context, false);
  	    
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
	}
	
	private void activateProfile(long profile_id)
	{
		//Log.d("PhoneProfilesService.activateProfile",profile_id+"");
		Intent intent = new Intent(this, BackgroundActivateProfileActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_SERVICE);
		intent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile_id);
	    startActivity(intent);		
	}
}
