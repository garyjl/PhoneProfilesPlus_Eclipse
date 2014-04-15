package sk.henrichg.phoneprofilesplus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import sk.henrichg.phoneprofilesplus.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;


public class PhoneProfilesHelper {

	public static int PPHelperVersion = -1;
	
	static public boolean isPPHelperInstalled(Context context, int minVersion)
	{
		// get package version
		PPHelperVersion = -1;
		PackageInfo pinfo = null;
		try {
			pinfo = context.getPackageManager().getPackageInfo("sk.henrichg.phoneprofileshelper", 0);
			PPHelperVersion = pinfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return PPHelperVersion >= minVersion;
	}
	
	static public void startPPHelper(Context context)
	{
		if (isPPHelperInstalled(context, 0))		// check PPHelper version
		{
			// start PPHelper 
			
        	//Log.e("PhoneProfilesHelper.startPPHelper","version OK");
			
			// start StartActivity
			Intent intent = new Intent("phoneprofileshelper.intent.action.START");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			final PackageManager packageManager = context.getPackageManager();
		    List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		    if (list.size() > 0)			
		    {
		    	context.startActivity(intent);
		    }
		    else
		    {
	        	//Log.e("PhoneProfilesHelper.startPPHelper","intent not found!");
		    }
		    
		}
		else
		{
        	//Log.e("PhoneProfilesHelper.startPPHelper","version BAD");
        }
	}
	
	private static boolean doInstallPPHelper(Activity activity)
	{
		boolean OK = true;
		
	    AssetManager assetManager = activity.getBaseContext().getAssets();
	    String[] files = null;
	    try {
	        files = assetManager.list("");
	    } catch (IOException e) {
	        Log.e("PhoneProfilesHelper.doInstallPPHelper", "Failed to get asset file list.", e);
	        OK = false;
	    }
	    
        //Log.e("PhoneProfilesHelper.doInstallPPHelper", "files.length="+files.length);

  		File sd = Environment.getExternalStorageDirectory();
		File exportDir = new File(sd, GlobalData.EXPORT_PATH);
		if (!(exportDir.exists() && exportDir.isDirectory()))
			exportDir.mkdirs();
	    
    	//// copy PhoneProfilesHelper.apk into sdcard
	    OK = false;
	    for(String filename : files) 
	    {
	        //Log.e("PhoneProfilesHelper.doInstallPPHelper", "filename="+filename);
	        
	        if (filename.equals("PhoneProfilesHelper.x"))
	        {
		        InputStream in = null;
		        OutputStream out = null;
		        try {
					File outFile = new File(sd, GlobalData.EXPORT_PATH + "/" + filename);
	
		        	in = assetManager.open(filename);
					out = new FileOutputStream(outFile);
					copyFile(in, out);
					in.close();
					in = null;
					out.flush();
					out.close();
					out = null;
					
					OK = true;
		        } catch(IOException e) {
		            Log.e("PhoneProfilesHelper.doInstallPPHelper", "Failed to copy asset file: " + filename, e);
		            OK = false;
		        }
		        
		        break;
	        }
	    }
	    
	    if (OK)
	    {
			//// copy PhoneProfilesHelper.apk from apk into system partition
		    OK = false;
		    
		    String sourceFile = System.getenv("EXTERNAL_STORAGE")+GlobalData.EXPORT_PATH+"/PhoneProfilesHelper.x";
		    //String sourceFile = sd+GlobalData.EXPORT_PATH+"/PhoneProfilesHelper.x";
		    String destinationFile = "PhoneProfilesHelper.apk"; 
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2)
			    destinationFile = "/system/priv-app/"+destinationFile; 
			else
			    destinationFile = "/system/app/"+destinationFile;
			
			//Log.e("PhoneProfilesHelper.doInstallPPHelper", "sourceFile="+sourceFile);
			//Log.e("PhoneProfilesHelper.doInstallPPHelper", "destionationFile="+destinationFile);
			

			OK = RootTools.remount("/system", "RW");
			//if (OK)
			//	Log.e("PhoneProfilesHelper.doInstallPPHelper", "remount RW OK");
			if (OK)
				RootTools.deleteFileOrDirectory(destinationFile, false);
			if (OK)
				OK = RootTools.copyFile(sourceFile, destinationFile, false, false);
			//if (OK)
			//	Log.e("PhoneProfilesHelper.doInstallPPHelper", "copy PPHelper.apk OK");
			if (OK)
			{
				CommandCapture command = new CommandCapture(1, "chmod 644 "+destinationFile);
				try {
					RootTools.getShell(true).add(command);
					OK = commandWait(command);
					OK = OK && command.getExitCode() == 0;
					RootTools.closeAllShells();
				} catch (Exception e) {
					e.printStackTrace();
					OK = false;
				}
			}
			//if (OK)
			//	Log.e("PhoneProfilesHelper.doInstallPPHelper", "chmod PPHelper.apk OK");
			if (OK)
				OK = RootTools.remount("/system", "RO");
			//if (OK)
			//	Log.e("PhoneProfilesHelper.doInstallPPHelper", "remount RO OK");

			/*
			if (OK)
				Log.e("PhoneProfilesHelper.doInstallPPHelper", "PhoneProfilesHelper installed");
			else
				Log.e("PhoneProfilesHelper.doInstallPPHelper", "PhoneProfilesHelper installation failed!");
		    */
		    
		    /*
			CommandCapture command;
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
				command = new CommandCapture(1,"mount -o remount,rw /system", 						//mounts the system partition to be writeable
						"rm /system/priv-app/PhoneProfilesHelper.apk",								//removes the old systemapp
						"cp $EXTERNAL_STORAGE/"+GlobalData.EXPORT_PATH+"/PhoneProfilesHelper.x /system/priv-app/PhoneProfilesHelper.apk",	//copies the apk of the app to the system-apps folder
						"chmod 644 /system/priv-app/PhoneProfilesHelper.apk",						//fixes the permissions
						"mount -o remount,ro /system");												//mounts the system partition to be read-only again
			} else{
				command = new CommandCapture(1,"mount -o remount,rw /system", 
						"rm /system/app/PhoneProfilesHelper.apk",
						"cp $EXTERNAL_STORAGE/"+GlobalData.EXPORT_PATH+"/PhoneProfilesHelper.x /system/app/PhoneProfilesHelper.apk",
						"chmod 644 /system/app/PhoneProfilesHelper.apk",		
						"mount -o remount,ro /system");									
			}
			
			try {
				RootTools.getShell(true).add(command);
				OK = commandWait(command);
				RootTools.closeAllShells();
				if (OK)
					Log.e("PhoneProfilesHelper.doInstallPPHelper", "PhoneProfilesHelper installed");
				else
					Log.e("PhoneProfilesHelper.doInstallPPHelper", "PhoneProfilesHelper installation failed!");
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("PhoneProfilesHelper.doInstallPPHelper", "PhoneProfilesHelper installation failed!");
				OK = false;
			}
			*/
	    }
	    
		return OK;
	}
	
	static public void installPPHelper(Activity activity)
	{
		final Activity _activity = activity;
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
		dialogBuilder.setTitle(activity.getResources().getString(R.string.phoneprofilehepler_install_title));
		dialogBuilder.setMessage(activity.getResources().getString(R.string.phoneprofilehepler_install_message) + "?");
		dialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				boolean OK = doInstallPPHelper(_activity);
				if (OK)
			    	restartAndroid(_activity);
				else
					installUnInstallPPhelperErrorDialog(_activity, 1);
			}
		});
		dialogBuilder.setNegativeButton(R.string.alert_button_no, null);
		dialogBuilder.show();
	}
	
	static private void copyFile(InputStream in, OutputStream out) throws IOException
	{
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
	
	static private boolean doUninstallPPHelper(Activity activity)
	{
		boolean OK = false;

	    String destinationFile = "PhoneProfilesHelper.apk"; 
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2)
		    destinationFile = "/system/priv-app/"+destinationFile; 
		else
		    destinationFile = "/system/app/"+destinationFile;
		
		OK = RootTools.deleteFileOrDirectory(destinationFile, true);
		//if (OK)
		//	Log.e("PhoneProfilesHelper.doInstallPPHelper", "remount RO OK");
		
		/*
		CommandCapture command;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
			command = new CommandCapture(1,"mount -o remount,rw /system", 						//mounts the system partition to be writeable
					"rm /system/priv-app/PhoneProfilesHelper.apk",								//removes the old systemapp
					"mount -o remount,ro /system");												//mounts the system partition to be read-only again
		} else{
			command = new CommandCapture(1,"mount -o remount,rw /system", 
					"rm /system/app/PhoneProfilesHelper.apk",
					"mount -o remount,ro /system");									
		}
		
		try {
			RootTools.getShell(true).add(command);
			OK = commandWait(command);
			RootTools.closeAllShells();
			if (OK)
				Log.e("PhoneProfilesHelper.doUninstallPPHelper", "PhoneProfilesHelper uninstalled");
			else
				Log.e("PhoneProfilesHelper.doUninstallPPHelper", "PhoneProfilesHelper uninstallation failed!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("PhoneProfilesHelper.doUninstallPPHelper", "PhoneProfilesHelper uninstallation failed!");
			OK = false;
		}
		*/
		
		return OK;
	}
	
	static public void uninstallPPHelper(Activity activity)
	{
		final Activity _activity = activity;
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
		dialogBuilder.setTitle(activity.getResources().getString(R.string.phoneprofilehepler_uninstall_title));
		dialogBuilder.setMessage(activity.getResources().getString(R.string.phoneprofilehepler_uninstall_message) + "?");
		dialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				boolean OK = doUninstallPPHelper(_activity);
				if (!OK)
					installUnInstallPPhelperErrorDialog(_activity, 2);
			}
		});
		dialogBuilder.setNegativeButton(R.string.alert_button_no, null);
		dialogBuilder.show();
	}
	
	static private void restartAndroid(Activity activity)
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
		dialogBuilder.setTitle(activity.getResources().getString(R.string.phoneprofilehepler_reboot_title));
		dialogBuilder.setMessage(activity.getResources().getString(R.string.phoneprofilehepler_reboot_message));
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		
		dialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
		    	// restart device
		    	RootTools.restartAndroid();
			}
		});
		dialogBuilder.setNegativeButton(R.string.alert_button_no, null);
		dialogBuilder.show();
	}
	
	static private boolean commandWait(Command cmd) throws Exception {
		boolean OK;
		
        int waitTill = 50;
        int waitTillMultiplier = 2;
        int waitTillLimit = 6400; //7 tries, 12750 msec
        //50+100+200+400+800+1600+3200+6400

        OK = true;
        
        while (!cmd.isFinished() && waitTill<=waitTillLimit) {
            synchronized (cmd) {
                try {
                    if (!cmd.isFinished()) {
                        cmd.wait(waitTill);
                        waitTill *= waitTillMultiplier;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    OK = false;
                }
            }
        }
        if (!cmd.isFinished()){
            //Log.e("PhoneProfilesHelper.commandWaid", "Could not finish root command in " + (waitTill/waitTillMultiplier));
            OK = false;
        }
        
        return OK;
    }	
	
	static private void installUnInstallPPhelperErrorDialog(Activity activity, int importExport)
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
		String resString;
		if (importExport == 1)
			resString = activity.getResources().getString(R.string.phoneprofilehepler_install_title);
		else
			resString = activity.getResources().getString(R.string.phoneprofilehepler_uninstall_title);
		dialogBuilder.setTitle(resString);
		if (importExport == 1)
			resString = activity.getResources().getString(R.string.phoneprofilehepler_install_error);
		else
			resString = activity.getResources().getString(R.string.phoneprofilehepler_uninstall_error);
		dialogBuilder.setMessage(resString + "!");
		//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dialogBuilder.setPositiveButton(android.R.string.ok, null);
		dialogBuilder.show();
	}
	
}
