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

	private static DatabaseHandler databaseHandler = null;
	private static ActivateProfileHelper activateProfileHelper = null;
	private static List<Profile> profileList = null;
	
	private static Context context = null;
	
    public static boolean started = false; 
	
	@Override
	public void onCreate()
	{
		Log.d("PhoneProfilesService.onCreate", "xxx");
		
		// initialization
		if (context == null)
		  context = getApplicationContext();
		
		databaseHandler = getDatabaseHandler();
		activateProfileHelper = getActivateProfileHelper();
		profileList = getProfileList();
		
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

	public static void setContext(Context c)
	{
		context = c;
	}
	
	public static DatabaseHandler getDatabaseHandler()
	{
		if (databaseHandler == null)
			databaseHandler = new DatabaseHandler(context);
			
		return databaseHandler;
	}

	public static ActivateProfileHelper getActivateProfileHelper()
	{
		if (activateProfileHelper == null)
			activateProfileHelper = new ActivateProfileHelper(); 

		return activateProfileHelper;
	}
	
	public static List<Profile> getProfileList()
	{
		if (profileList == null)
		{
			profileList = getDatabaseHandler().getAllProfiles();
		
			for (Profile profile : profileList)
			{
				profile.generateIconBitmap(context);
				profile.generatePreferencesIndicator(context);
			}
		}

		return profileList;
	}

	public static void clearProfileList()
	{
		if (profileList != null)
			profileList.clear();
		profileList = null;
	}
	
	public static Profile getActivatedProfile()
	{
		if (profileList == null)
			getProfileList();

		Profile profile;
		for (int i = 0; i < profileList.size(); i++)
		{
			profile = profileList.get(i); 
			if (profile._checked)
				return profile;
		}
		
		return null;
	}
	
	public static Profile getFirstProfile()
	{
		if (profileList == null)
			getProfileList();
		
		Profile profile;
		if (profileList.size() > 0)
			profile = profileList.get(0);
		else
			profile = null;
		
		return profile;
	}
	
	public static int getItemPosition(Profile profile)
	{
		if (profileList == null)
			getProfileList();
		
		for (int i = 0; i < profileList.size(); i++)
		{
			if (profileList.get(i)._id == profile._id)
				return i;
		}
		return -1;
	}
	
	public static void activateProfile(Profile profile)
	{
		if (profileList == null)
			getProfileList();
		
		for (Profile p : profileList)
		{
			p._checked = false;
		}
		
		// teraz musime najst profile v profileList 
		int position = getItemPosition(profile);
		if (position != -1)
		{
			// najdenemu objektu nastavime _checked
			Profile _profile = profileList.get(position);
			if (_profile != null)
				_profile._checked = true;
		}
	}
	
	public static Profile getProfileById(long id)
	{
		if (profileList == null)
			getProfileList();

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
