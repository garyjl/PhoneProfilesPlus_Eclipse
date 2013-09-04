package sk.henrichg.phoneprofiles;

import android.annotation.SuppressLint;
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

public class ServiceCommunication {

	private Context context = null;
	
	private int msgForBind;
	private long longDataForBind;

	public Messenger phoneProfilesService = null;
	
    public ServiceConnection serviceConnection = new ServiceConnection() {

    	public void onServiceConnected(ComponentName className, IBinder service) {
    		//Log.d("ProfilesDataWrapper.onServiceConnected","xxx");
    		phoneProfilesService = new Messenger(service);

            switch (msgForBind) {
            case PhoneProfilesService.MSG_RELOAD_DATA:
        		sendMessageIntoService(msgForBind);
                break;
            case PhoneProfilesService.MSG_ACTIVATE_PROFILE:
            case PhoneProfilesService.MSG_ACTIVATE_PROFILE_INTERACTIVE:
            //case PhoneProfilesService.MSG_PROFILE_ACTIVATED:
        		sendMessageIntoServiceLong(msgForBind, longDataForBind);
            	break;
            default:
        		sendMessageIntoService(msgForBind);
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
	
    @SuppressLint("HandlerLeak")
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
	
    public void sendMessageIntoServiceLong(int message, long data)
	{
   		msgForBind = message;
   		longDataForBind = data;
        context.bindService(new Intent(context, PhoneProfilesService.class), serviceConnection, Context.BIND_AUTO_CREATE);

	    if (phoneProfilesService != null)
    	{
	    	//Log.d("ProfilesDataWrapper.sendMessageIntoServiceLong","data="+data);
	    	try {
                Bundle b = new Bundle();
                b.putLong(GlobalData.EXTRA_PROFILE_ID, data);
	            Message msg = Message.obtain(null, message);
                msg.setData(b);	            
	            phoneProfilesService.send(msg);
	        } catch (RemoteException e) {
	            // In this case the service has crashed before we could even do anything with it
	        }
    	}
	}
	
}
