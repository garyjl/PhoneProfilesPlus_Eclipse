package sk.henrichg.phoneprofiles;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class ProfilesDataWrapper {

	private Context context = null;
	private boolean forGUI = false;

	private DatabaseHandler databaseHandler = null;
	private ActivateProfileHelper activateProfileHelper = null;
	private List<Profile> profileList = null;
	
	ProfilesDataWrapper(Context c, boolean fgui)
	{
		context = c;
		forGUI = fgui;
		databaseHandler = getDatabaseHandler();
		activateProfileHelper = getActivateProfileHelper();
		profileList = getProfileList();
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

	public void clearProfileList()
	{
		if (profileList != null)
			profileList.clear();
		profileList = null;
	}
	
	public Profile getActivatedProfile()
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
	
	public Profile getFirstProfile()
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
	
	public int getItemPosition(Profile profile)
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
	
	public void activateProfile(Profile profile)
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
	
	public Profile getProfileById(long id)
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
	
	public void reloadProfilesData()
	{
		clearProfileList();
		getProfileList();
	}
	
	//---------------------------------------------------------------------------
	
	private int msgForBind;

	public Messenger phoneProfilesService = null;
	
    public ServiceConnection serviceConnection = new ServiceConnection() {

    	public void onServiceConnected(ComponentName className, IBinder service) {
    		Log.d("ProfilesDataWrapper.onServiceConnected","xxx");
    		phoneProfilesService = new Messenger(service);
    		sendMessageIntoService(msgForBind);
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
    		Log.d("ProfilesDataWrapper.onServiceDisconnected","xxx");
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
	
	
}
