package sk.henrichg.phoneprofiles;

import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ActivateProfileActivity extends Activity {

	private static DatabaseHandler databaseHandler;
	private NotificationManager notificationManager;
	private ActivateProfileHelper activateProfileHelper;
	
	private int startupSource = 0;
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		intent = getIntent();
		startupSource = intent.getIntExtra(PhoneProfilesActivity.INTENT_START_APP_SOURCE, 0);
		
		activateProfileHelper = new ActivateProfileHelper(this, getBaseContext());

		databaseHandler = new DatabaseHandler(this);
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		Log.d("ActivateProfileActivity.onStart", "startupSource="+startupSource);
		
		boolean actProfile = false;
		if (startupSource == PhoneProfilesActivity.STARTUP_SOURCE_SHORTCUT)
		{
			// aktivita spustena z shortcutu, profil aktivujeme
			actProfile = true;
		}
		else
		if (startupSource == PhoneProfilesActivity.STARTUP_SOURCE_BOOT)
		{
			// aktivita bola spustena po boote telefonu
			
			SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
			if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_APPLICATION_ACTIVATE, true))
			{
				// je nastavene, ze pri starte sa ma aktivita aktivovat
				actProfile = true;
			}
		}
		Log.d("PhoneProfilesActivity.onStart", "actProfile="+String.valueOf(actProfile));

		Profile profile;
		
		// pre profil, ktory je prave aktivny, treba aktualizovat aktivitu
		profile = databaseHandler.getActivatedProfile();
		showNotification(profile);
		updateWidget();
		
		if (startupSource == PhoneProfilesActivity.STARTUP_SOURCE_SHORTCUT)
		{
			long profile_id = intent.getLongExtra(PhoneProfilesActivity.INTENT_PROFILE_ID, 0);
			if (profile_id == 0)
				profile = null;
			else
				profile = databaseHandler.getProfile(profile_id);
		}
		
		if (actProfile && (profile != null))
		{
			// aktivacia profilu
			activateProfile(profile);
		}
		
		Log.d("ActivateProfileActivity.onStart", "xxxx");
		
		finish();
	}
	
	private void activateProfile(Profile profile)
	{
		SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
		
		databaseHandler.activateProfile(profile);
		showNotification(profile);

		activateProfileHelper.execute(profile);

		updateWidget();

		if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_NOTIFICATION_TOAST, true))
		{	
			// toast notification
			Toast msg = Toast.makeText(this, 
					getResources().getString(R.string.toast_profile_activated_0) + ": " + profile.getName() + " " +
					getResources().getString(R.string.toast_profile_activated_1), 
					Toast.LENGTH_LONG);
			msg.show();
		}

		
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("InlinedApi")
	private void showNotification(Profile profile)
	{

		SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
		
		if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_NOTIFICATION_STATUS_BAR, true))
		{	
			if (profile == null)
			{
				notificationManager.cancel(PhoneProfilesActivity.NOTIFICATION_ID);
			}
			else
			{
				// vytvorenie intentu na aktivitu, ktora sa otvori na kliknutie na notifikaciu
				Intent intent = new Intent(this, PhoneProfilesActivity.class);
				// nastavime, ze aktivita sa spusti z notifikacnej listy
				intent.putExtra(PhoneProfilesActivity.INTENT_START_APP_SOURCE, PhoneProfilesActivity.STARTUP_SOURCE_NOTIFICATION);
				PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				// vytvorenie samotnej notifikacie
				NotificationCompat.Builder notificationBuilder;
		        if (profile.getIsIconResourceID())
		        {
		        	int iconResource = getResources().getIdentifier(profile.getIconIdentifier(), "drawable", getPackageName());
		        
					notificationBuilder = new NotificationCompat.Builder(this)
						.setContentText(getResources().getString(R.string.active_profile_notification_label))
						.setContentTitle(profile.getName())
						.setContentIntent(pIntent)
						.setSmallIcon(iconResource);
		        }
		        else
		        {
		        	if (android.os.Build.VERSION.SDK_INT >= 11)
		        	{
		        		Bitmap bitmap = BitmapFactory.decodeFile(profile.getIconIdentifier());
		        		Resources resources = getResources();
		        		int height = (int) resources.getDimension(android.R.dimen.notification_large_icon_height);
		        		int width = (int) resources.getDimension(android.R.dimen.notification_large_icon_width);
		        		bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
						notificationBuilder = new NotificationCompat.Builder(this)
							.setContentText(getResources().getString(R.string.active_profile_notification_label))
							.setContentTitle(profile.getName())
							.setContentIntent(pIntent)
							.setLargeIcon(bitmap)
							.setSmallIcon(R.drawable.ic_launcher);
		        	}
		        	else
		        	{
						notificationBuilder = new NotificationCompat.Builder(this)
						.setContentText(getResources().getString(R.string.active_profile_notification_label))
						.setContentTitle(profile.getName())
						.setContentIntent(pIntent)
						.setSmallIcon(R.drawable.ic_launcher);
		        	}
		        }
				Notification notification = notificationBuilder.getNotification();
				notification.flags |= Notification.FLAG_NO_CLEAR; 
				notificationManager.notify(PhoneProfilesActivity.NOTIFICATION_ID, notification);
			}
		}
		else
		{
			notificationManager.cancel(PhoneProfilesActivity.NOTIFICATION_ID);
		}
	}
	
	private void updateWidget()
	{
		Intent intent = new Intent(this, ActivateProfileWidget.class);
		intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
		int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), ActivateProfileWidget.class));
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		sendBroadcast(intent);
	}
	
	
}
