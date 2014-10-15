package sk.henrichg.phoneprofilesplus;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.SmsMessage;

public class SMSBroadcastReceiver extends WakefulBroadcastReceiver {

	public static final String BROADCAST_RECEIVER_TYPE = "SMS";
	
	private static ContentObserver observer;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		GlobalData.logE("#### SMSBroadcastReceiver.onReceive","xxx");

	    String origin = "";
	    String body = "";
		
		Bundle extras = intent.getExtras();
		Object[] pdus = (Object[]) extras.get("pdus");
		for (Object pdu : pdus)
		{
			SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdu);
		    origin = msg.getOriginatingAddress();
		    body = msg.getMessageBody();
		}	    
		
	    if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
	    {
			GlobalData.logE("SMSBroadcastReceiver.onReceive","SMS received");
	    }
	    else
	    if(intent.getAction().equals("android.provider.Telephony.SMS_SENT"))
	    {
			GlobalData.logE("SMSBroadcastReceiver.onReceive","sent");
	    }
	    else
	    if (intent.getAction().equals("android.provider.Telephony.WAP_PUSH_RECEIVED"))
	    {
			GlobalData.logE("SMSBroadcastReceiver.onReceive","MMS received");
	    }

		GlobalData.logE("SMSBroadcastReceiver.onReceive","from="+origin);
		GlobalData.logE("SMSBroadcastReceiver.onReceive","message="+body);

		SharedPreferences preferences = context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt(GlobalData.PREF_EVENT_SMS_EVENT_TYPE, EventPreferencesSMS.SMS_EVENT_INCOMING);
		editor.putString(GlobalData.PREF_EVENT_SMS_PHONE_NUMBER, origin);
        
		Calendar now = Calendar.getInstance();
        int gmtOffset = TimeZone.getDefault().getRawOffset();
		long time = now.getTimeInMillis() + gmtOffset;
		editor.putLong(GlobalData.PREF_EVENT_SMS_DATE, time);

		editor.commit();
		
		startService(context);
	}
	
	private static void startService(Context context)
	{
		if (!GlobalData.getApplicationStarted(context))
			// application is not started
			return;

		GlobalData.loadPreferences(context);
		
		if (GlobalData.getGlobalEventsRuning(context))
		{
			GlobalData.logE("@@@ SMSBroadcastReceiver.startService","xxx");

			boolean smsEventsExists = false;
			
			DataWrapper dataWrapper = new DataWrapper(context, false, false, 0);
			smsEventsExists = dataWrapper.getDatabaseHandler().getTypeEventsCount(DatabaseHandler.ETYPE_SMS) > 0;
			GlobalData.logE("SMSBroadcastReceiver.onReceive","timeEventsExists="+smsEventsExists);
			dataWrapper.invalidateDataWrapper();

			if (smsEventsExists)
			{
				// start service
				Intent eventsServiceIntent = new Intent(context, EventsService.class);
				eventsServiceIntent.putExtra(GlobalData.EXTRA_BROADCAST_RECEIVER_TYPE, BROADCAST_RECEIVER_TYPE);
				startWakefulService(context, eventsServiceIntent);
			}
		}
	}

	private static final String CONTENT_SMS = "content://sms";
	// Constant from Android SDK
	private static final int MESSAGE_TYPE_SENT = 2;
	
	
	// Register an observer for listening outgoing sms events.
	/**
	 * @author khoanguyen
	 */
	static public void registerContentObserver(Context context)
	{
		if (observer != null)
			return;

		final Context _context = context;
		
		observer = new ContentObserver(null)
		{
			public void onChange(boolean selfChange)
			{
				GlobalData.logE("SMSBroadcastReceiver.ContentObserver.onChange","xxx");

				// read outgoing sms from db
				Cursor cursor = _context.getContentResolver().query(Uri.parse(CONTENT_SMS), null, null, null, null);
				if (cursor.moveToNext())
				{
					String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
					int type = cursor.getInt(cursor.getColumnIndex("type"));
					// Only processing outgoing sms event & only when it
					// is sent successfully (available in SENT box).
					if (protocol != null || type != MESSAGE_TYPE_SENT)
					{
						GlobalData.logE("SMSBroadcastReceiver.ContentObserver.onChange","no SMS in SENT box");
						return;
					}
					int dateColumn = cursor.getColumnIndex("date");
					int bodyColumn = cursor.getColumnIndex("body");
					int addressColumn = cursor.getColumnIndex("address");

					String to = cursor.getString(addressColumn);
					Date date = new Date(cursor.getLong(dateColumn));
					String message = cursor.getString(bodyColumn);
					
					GlobalData.logE("SMSBroadcastReceiver.ContentObserver.onChange","sms sent");
					GlobalData.logE("SMSBroadcastReceiver.ContentObserver.onChange","to="+to);
					GlobalData.logE("SMSBroadcastReceiver.ContentObserver.onChange","date="+date);
					GlobalData.logE("SMSBroadcastReceiver.ContentObserver.onChange","message="+message);
					
					SharedPreferences preferences = _context.getSharedPreferences(GlobalData.APPLICATION_PREFS_NAME, Context.MODE_PRIVATE);
					Editor editor = preferences.edit();
					editor.putInt(GlobalData.PREF_EVENT_SMS_EVENT_TYPE, EventPreferencesSMS.SMS_EVENT_OUTGOING);
					editor.putString(GlobalData.PREF_EVENT_SMS_PHONE_NUMBER, to);
			        int gmtOffset = TimeZone.getDefault().getRawOffset();
					long time = date.getTime() + gmtOffset;
					editor.putLong(GlobalData.PREF_EVENT_SMS_DATE, time);
					editor.commit();
				}
				cursor.close();
				
				startService(_context);
			}
		};
		
		context.getContentResolver().registerContentObserver(Uri.parse(CONTENT_SMS), true, observer);
	}
		
	public static void unregisterContentObserver(Context context)
	{
		if (observer != null)
			context.getContentResolver().unregisterContentObserver(observer);		
	}
}
