package sk.henrichg.phoneprofiles;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PhoneProfilesService extends Service {
	
	private final IBinder mBinder = new MyBinder();

	@Override
	public void onCreate()
	{
		Log.d("PhoneProfilesService.onCreate", "xxx");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("PhoneProfilesService.onStartCommand", "xxx");
		
		// initialization
		GlobalData.getDatabaseHandler();
		GlobalData.getActivateProfileHelper();
		GlobalData.getProfileList();

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
	
}
