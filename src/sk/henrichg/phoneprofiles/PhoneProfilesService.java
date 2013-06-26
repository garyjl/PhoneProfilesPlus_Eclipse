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

	public static DatabaseHandler databaseHandler = null;
	public static List<Profile> profileList = null;
	public static ActivateProfileHelper activateProfileHelper = null;
	
	public static Context applicationContext = null;
	
	@Override
	public void onCreate()
	{
		Log.d("PhoneProfilesService.onCreate", "xxx");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("PhoneProfilesService.onStartCommand", "xxx");

		return Service.START_NOT_STICKY;
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
	
	public static void setApplicationContext(Context context)
	{
		applicationContext = context;
	}
}
