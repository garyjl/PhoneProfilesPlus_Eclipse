package sk.henrichg.phoneprofiles;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PhoneProfilesService extends Service {
	
	private final IBinder mBinder = new MyBinder();

	
	private Context context = null;
	
	public static ProfilesDataWrapper profilesDataWrapper = null;
	
    public static boolean started = false; 
	
	@Override
	public void onCreate()
	{
		Log.d("PhoneProfilesService.onCreate", "xxx");
		
		// initialization
  	    context = getApplicationContext();
  	    profilesDataWrapper = new ProfilesDataWrapper(context, false);
		
		started = true;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("PhoneProfilesService.onStartCommand", "xxx");
		
		//return Service.START_NOT_STICKY;
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy()
	{
		Log.d("PhoneProfilesService.onDestroy", "xxx");
		started = false;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	public class MyBinder extends Binder {
		PhoneProfilesService getService() {
	      return PhoneProfilesService.this;
	    }
	}
	
    //-------------------------------------------

}
