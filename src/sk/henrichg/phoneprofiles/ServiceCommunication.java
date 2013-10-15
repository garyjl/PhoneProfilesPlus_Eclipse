package sk.henrichg.phoneprofiles;

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

public class ServiceCommunication {

	private Context context = null;
	
	private int msgForBind;
	private long dataId;
	private int activatedProfileStartupSource;

	public Messenger phoneProfilesService = null;
	
    public ServiceConnection serviceConnection = new ServiceConnection() {

    	public void onServiceConnected(ComponentName className, IBinder service) {

    		Log.e("ServiceConnection.onServiceConnected","msgForBind="+msgForBind);

    		phoneProfilesService = new Messenger(service);

            switch (msgForBind) {
        /*    case PhoneProfilesService.MSG_RELOAD_DATA:
        		_sendMessageIntoService();
                break;
            case PhoneProfilesService.MSG_ACTIVATE_PROFILE:
            case PhoneProfilesService.MSG_ACTIVATE_PROFILE_INTERACTIVE: */
            case PhoneProfilesService.MSG_PROFILE_ACTIVATED:
            	_sendMessageIntoServiceProfileActivated();
            	break;
            case PhoneProfilesService.MSG_PROFILE_ADDED:
            case PhoneProfilesService.MSG_PROFILE_UPDATED:
            case PhoneProfilesService.MSG_PROFILE_DELETED:
            	_sendMessageIntoServiceDataChange();
            	break;
            case PhoneProfilesService.MSG_ALL_PROFILES_DELETED:
        		_sendMessageIntoService();
            	break;
            case PhoneProfilesService.MSG_EVENT_ADDED:
            case PhoneProfilesService.MSG_EVENT_UPDATED:
            case PhoneProfilesService.MSG_EVENT_DELETED:
            	_sendMessageIntoServiceDataChange();
            	break;
            case PhoneProfilesService.MSG_ALL_EVENTS_DELETED:
        		_sendMessageIntoService();
            	break;
            case PhoneProfilesService.MSG_DATA_IMPORTED:
        		_sendMessageIntoService();
            	break;
            default:
        		_sendMessageIntoService();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
    		//Log.d("ProfilesDataWrapper.onServiceDisconnected","xxx");
        	phoneProfilesService = null;
        }

    };
	
	ServiceCommunication(Context c)
	{
		context = c;
	}
    
    final Messenger mMessenger = new Messenger(new IncomingHandler());
	
	static class IncomingHandler extends Handler {

		@Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            default:
                super.handleMessage(msg);
            }
        }
    }
	
    private void _sendMessageIntoService()
	{
    	try {
            Message msg = Message.obtain(null, msgForBind);
            msg.replyTo = mMessenger;
            phoneProfilesService.send(msg);
        } catch (RemoteException e) {
            // In this case the service has crashed before we could even do anything with it
        }
	}

    private void _sendMessageIntoServiceProfileActivated()
    {
    	//Log.e("ServiceCommunication.sendMessageIntoServiceLong","data="+data);
    	try {
            Bundle b = new Bundle();
            b.putLong(GlobalData.EXTRA_PROFILE_ID, dataId);
            b.putInt(GlobalData.EXTRA_START_APP_SOURCE, activatedProfileStartupSource);
            Message msg = Message.obtain(null, msgForBind);
            msg.setData(b);	            
            phoneProfilesService.send(msg);
        } catch (RemoteException e) {
            // In this case the service has crashed before we could even do anything with it
        }
    }

    private void _sendMessageIntoServiceDataChange()
    {
    	try {
            Bundle b = new Bundle();
            switch (msgForBind)
            {
            	case PhoneProfilesService.MSG_PROFILE_ADDED:
            	case PhoneProfilesService.MSG_PROFILE_UPDATED:
            	case PhoneProfilesService.MSG_PROFILE_DELETED:
                    b.putLong(GlobalData.EXTRA_PROFILE_ID, this.dataId);
                    break;
            	case PhoneProfilesService.MSG_EVENT_ADDED:
            	case PhoneProfilesService.MSG_EVENT_UPDATED:
            	case PhoneProfilesService.MSG_EVENT_DELETED:
                    b.putLong(GlobalData.EXTRA_EVENT_ID, this.dataId);
                    break;
            }
            Message msg = Message.obtain(null, msgForBind);
            msg.setData(b);	            
            phoneProfilesService.send(msg);
        } catch (RemoteException e) {
            // In this case the service has crashed before we could even do anything with it
        }
    }
    
    public void sendMessageIntoService(int message)
	{
   		msgForBind = message;
   		
    	if (phoneProfilesService == null)
    		context.bindService(new Intent(context, PhoneProfilesService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    	else 
    		_sendMessageIntoService();
	}

    public void sendMessageIntoServiceProfileActivated(long profileId, int startupSource)
    {
   		msgForBind = PhoneProfilesService.MSG_PROFILE_ACTIVATED;
   		dataId = profileId;
   		activatedProfileStartupSource = startupSource; 
   		
    	if (phoneProfilesService == null)
    		context.bindService(new Intent(context, PhoneProfilesService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    	else
    		_sendMessageIntoServiceProfileActivated();
    }

    public void sendMessageIntoServiceDataChange(int message, long dataId)
    {
   		msgForBind = message;
   		this.dataId = dataId;
   		
    	if (phoneProfilesService == null)
    		context.bindService(new Intent(context, PhoneProfilesService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    	else
    		_sendMessageIntoServiceDataChange();
    }
    
}
