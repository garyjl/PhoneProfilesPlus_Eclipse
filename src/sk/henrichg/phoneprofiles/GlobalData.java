package sk.henrichg.phoneprofiles;

import java.util.List;

import android.app.Application;

public class GlobalData extends Application {
	
	private static DatabaseHandler databaseHandler = null;
	private static List<Profile> profileList = null;
	private static boolean applicationStarted = false;

	static final String EXTRA_PROFILE_POSITION = "profile_position";
	static final String EXTRA_PROFILE_ID = "profile_id";
	static final String EXTRA_START_APP_SOURCE = "start_app_source";

	static final int STARTUP_SOURCE_NOTIFICATION = 1;
	static final int STARTUP_SOURCE_WIDGET = 2;
	static final int STARTUP_SOURCE_SHORTCUT = 3;
	static final int STARTUP_SOURCE_BOOT = 4;
	static final int STARTUP_SOURCE_ACTIVATOR = 5;

	static final int NOTIFICATION_ID = 700420;

	static final String PROFILE_ICON_DEFAULT = "ic_profile_default";
	
	
	static String PACKAGE_NAME;
	
	public void onCreate()
	{
		super.onCreate();
		
		PACKAGE_NAME = getApplicationContext().getPackageName();
		
		databaseHandler = new DatabaseHandler(this);
		
	}
	
	public static boolean getApplicationStarted()
	{
		return applicationStarted;
	}
	
	public static void setApplicationStarted(boolean started)
	{
		applicationStarted = started;
	}

	public static DatabaseHandler getDatabaseHandler()
	{
		return databaseHandler;
	}
	
	public static List<Profile> getProfileList()
	{
		if (profileList == null)
			profileList = databaseHandler.getAllProfiles();

		return profileList;
	}
	
	public static Profile getActivatedProfile()
	{
		if (profileList == null)
			profileList = databaseHandler.getAllProfiles();

		Profile profile;
		for (int i = 0; i < profileList.size(); i++)
		{
			profile = profileList.get(i); 
			if (profile.getChecked())
				return profile;
		}
		
		return null;
	}
	
	public static int getItemPosition(Profile profile)
	{
		for (int i = 0; i < profileList.size(); i++)
		{
			if (profileList.get(i).getID() == profile.getID())
				return i;
		}
		return -1;
	}
	
	public static void activateProfile(Profile profile)
	{
		for (Profile p : profileList)
		{
			p.setChecked(false);
		}
		
		// teraz musime najst profile v profileList 
		int position = getItemPosition(profile);
		if (position != -1)
		{
			// najdenemu objektu nastavime _checked
			Profile _profile = profileList.get(position);
			if (_profile != null)
				_profile.setChecked(true);
		}
	}
	
	public static Profile getProfileById(long id)
	{
		if (profileList == null)
			profileList = databaseHandler.getAllProfiles();

		Profile profile;
		for (int i = 0; i < profileList.size(); i++)
		{
			profile = profileList.get(i); 
			if (profile.getID() == id)
				return profile;
		}
		
		return null;
	}

}
