package sk.henrichg.phoneprofiles;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.CommandCapture;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.provider.Settings.Global;

@TargetApi(17)
public class AirPlaneMode_SDK17 {

	private static final String TAG = AirPlaneMode_SDK17.class.getSimpleName();
	
	static boolean isSystemApp(Context context)
	{
		ApplicationInfo ai = context.getApplicationInfo();
		
		if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
		{
			Log.d(TAG, "isSystemApp==true");
			return true;
		}
		return false;
	}
	
	static boolean isUpdatedSystemApp(Context context)
	{
		ApplicationInfo ai = context.getApplicationInfo();
		
		if ((ai.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
		{
			Log.d(TAG, "isUpdatedSystemApp==true");
			return true;
		}
		return false;
		
	}
	
	static boolean isAdminUser(Context context)
	{
		UserHandle uh = Process.myUserHandle();
		UserManager um = (UserManager)context.getSystemService(Context.USER_SERVICE);
		if (um != null)
		{
			long userSerialNumber = um.getSerialNumberForUser(uh);
			Log.d(TAG, "userSerialNumber="+userSerialNumber);
			return userSerialNumber == 0;
		}
		else
			return false;
	}
	
	static boolean getAirplaneMode(Context context)
	{
		return Settings.Global.getInt(context.getContentResolver(), Global.AIRPLANE_MODE_ON, 0) != 0;
	}
	
	static void setAirplaneMode(Context context, boolean mode)
	{
		if (mode != getAirplaneMode(context))
		{
			// it is only possible to set AIRPLANE_MODE programmatically for Android >= 4.2.x
			// if app runs:
			// - as system app (located on /system/app)
			// - and if current user is the admin user (not sure about that...)
			if (isSystemApp(context) && isAdminUser(context))
			{
				Settings.Global.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, mode ? 1 : 0);
				Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
				intent.putExtra("state", mode);
				context.sendBroadcast(intent);
			}
			else
			if (RootTools.isAccessGiven())
			{
				// zariadenie je rootnute
				String command1;
				String command2;
				if (mode)
				{
					command1 = "settings put global airplane_mode_on 1";
					command2 = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true";
				}
				else
				{
					command1 = "settings put global airplane_mode_on 0";
					command2 = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false";
				}
				CommandCapture command = new CommandCapture(0, command1, command2);
				try {
					RootTools.getShell(true).add(command).waitForFinish();
				} catch (Exception e) {
					Log.e("AirPlaneMode_SDK17.setAirplaneMode", "Error on run su");
				}
			}
			else
			{
				// for normal apps it is only possible to open the system settings dialog
			/*	Intent intent = new Intent(android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent); */ 
			}
			
		}
	}
}
