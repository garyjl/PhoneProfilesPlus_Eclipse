package sk.henrichg.phoneprofiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 23;

	// Database Name
	private static final String DATABASE_NAME = "phoneProfilesManager";

	// Profiles table name
	private static final String TABLE_PROFILES = "profiles";
	
	// import/export
	private final String EXPORT_DBPATH = "/PhoneProfiles";
	private final String EXPORT_FILENAME = DATABASE_NAME + ".backup";
	private final String DB_FILEPATH = "/data/" + GlobalData.PACKAGE_NAME + "/databases";

	
	// Profiles Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_ICON = "icon";
	private static final String KEY_CHECKED = "checked";
	private static final String KEY_PORDER = "porder";
	private static final String KEY_VOLUME_RINGER_MODE = "volumeRingerMode";
	private static final String KEY_VOLUME_RINGTONE = "volumeRingtone";
	private static final String KEY_VOLUME_NOTIFICATION = "volumeNotification";
	private static final String KEY_VOLUME_MEDIA = "volumeMedia";
	private static final String KEY_VOLUME_ALARM = "volumeAlarm";
	private static final String KEY_VOLUME_SYSTEM = "volumeSystem";
	private static final String KEY_VOLUME_VOICE = "volumeVoice";
	private static final String KEY_SOUND_RINGTONE_CHANGE = "soundRingtoneChange";
	private static final String KEY_SOUND_RINGTONE = "soundRingtone";
	private static final String KEY_SOUND_NOTIFICATION_CHANGE = "soundNotificationChange";
	private static final String KEY_SOUND_NOTIFICATION = "soundNotification";
	private static final String KEY_SOUND_ALARM_CHANGE = "soundAlarmChange";
	private static final String KEY_SOUND_ALARM = "soundAlarm";
	private static final String KEY_DEVICE_AIRPLANE_MODE = "deviceAirplaneMode";
	private static final String KEY_DEVICE_WIFI = "deviceWiFi";
	private static final String KEY_DEVICE_BLUETOOTH = "deviceBluetooth";
	private static final String KEY_DEVICE_SCREEN_TIMEOUT = "deviceScreenTimeout";
	private static final String KEY_DEVICE_BRIGHTNESS = "deviceBrightness";
	private static final String KEY_DEVICE_WALLPAPER_CHANGE = "deviceWallpaperChange";
	private static final String KEY_DEVICE_WALLPAPER = "deviceWallpaper";
	private static final String KEY_DEVICE_MOBILE_DATA = "deviceMobileData";
	private static final String KEY_DEVICE_MOBILE_DATA_PREFS = "deviceMobileDataPrefs";
	private static final String KEY_DEVICE_GPS = "deviceGPS";
	private static final String KEY_DEVICE_RUN_APPLICATION_CHANGE = "deviceRunApplicationChange";
	private static final String KEY_DEVICE_RUN_APPLICATION_PACKAGE_NAME = "deviceRunApplicationPackageName";
	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PROFILES_TABLE = "CREATE TABLE " + TABLE_PROFILES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY,"
				+ KEY_NAME + " TEXT,"
				+ KEY_ICON + " TEXT," 
				+ KEY_CHECKED + " INTEGER," 
				+ KEY_PORDER + " INTEGER," 
				+ KEY_VOLUME_RINGER_MODE + " INTEGER,"
				+ KEY_VOLUME_RINGTONE + " TEXT,"
				+ KEY_VOLUME_NOTIFICATION + " TEXT,"
				+ KEY_VOLUME_MEDIA + " TEXT,"
				+ KEY_VOLUME_ALARM + " TEXT,"
				+ KEY_VOLUME_SYSTEM + " TEXT,"
				+ KEY_VOLUME_VOICE + " TEXT,"
				+ KEY_SOUND_RINGTONE_CHANGE + " INTEGER,"
				+ KEY_SOUND_RINGTONE + " TEXT,"
				+ KEY_SOUND_NOTIFICATION_CHANGE + " INTEGER,"
				+ KEY_SOUND_NOTIFICATION + " TEXT,"
				+ KEY_SOUND_ALARM_CHANGE + " INTEGER,"
				+ KEY_SOUND_ALARM + " TEXT,"
				+ KEY_DEVICE_AIRPLANE_MODE + " INTEGER,"
				+ KEY_DEVICE_WIFI + " INTEGER,"
				+ KEY_DEVICE_BLUETOOTH + " INTEGER,"
				+ KEY_DEVICE_SCREEN_TIMEOUT + " INTEGER,"
				+ KEY_DEVICE_BRIGHTNESS + " TEXT,"
				+ KEY_DEVICE_WALLPAPER_CHANGE + " INTEGER,"
				+ KEY_DEVICE_WALLPAPER + " TEXT,"
				+ KEY_DEVICE_MOBILE_DATA + " INTEGER,"
				+ KEY_DEVICE_MOBILE_DATA_PREFS + " INTEGER,"
				+ KEY_DEVICE_GPS + " INTEGER,"
				+ KEY_DEVICE_RUN_APPLICATION_CHANGE + " INTEGER,"
				+ KEY_DEVICE_RUN_APPLICATION_PACKAGE_NAME + " TEXT"
				+ ")";
		db.execSQL(CREATE_PROFILES_TABLE);
		
		db.execSQL("CREATE INDEX IDX_PORDER ON " + TABLE_PROFILES + " (" + KEY_PORDER + ")");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		//Log.d("DatabaseHandler.onUpgrade", "xxxx");
		
		/*
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);

		// Create tables again
		onCreate(db);
		*/
		
		if (oldVersion < 16)
		{
			// pridame nove stlpce
			db.execSQL("ALTER TABLE " + TABLE_PROFILES + " ADD COLUMN " + KEY_DEVICE_WALLPAPER_CHANGE + " INTEGER");
			db.execSQL("ALTER TABLE " + TABLE_PROFILES + " ADD COLUMN " + KEY_DEVICE_WALLPAPER + " TEXT");
			
			// updatneme zaznamy
			db.beginTransaction();
			try {
				db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_DEVICE_WALLPAPER_CHANGE + "=0");
				db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_DEVICE_WALLPAPER + "='-'");
				db.setTransactionSuccessful();
		     } catch (Exception e){
		         //Error in between database transaction 
		     } finally {
		    	db.endTransaction();
	         }	
		}
		
		if (oldVersion < 18)
		{
			db.beginTransaction();
			try {
				db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_ICON + "=replace(" + KEY_ICON + ",':','|')");
				db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_VOLUME_RINGTONE + "=replace(" + KEY_VOLUME_RINGTONE + ",':','|')");
				db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_VOLUME_NOTIFICATION + "=replace(" + KEY_VOLUME_NOTIFICATION + ",':','|')");
				db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_VOLUME_MEDIA + "=replace(" + KEY_VOLUME_MEDIA + ",':','|')");
				db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_VOLUME_ALARM + "=replace(" + KEY_VOLUME_ALARM + ",':','|')");
				db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_VOLUME_SYSTEM + "=replace(" + KEY_VOLUME_SYSTEM + ",':','|')");
				db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_VOLUME_VOICE + "=replace(" + KEY_VOLUME_VOICE + ",':','|')");
				db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_DEVICE_BRIGHTNESS + "=replace(" + KEY_DEVICE_BRIGHTNESS + ",':','|')");
				db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_DEVICE_WALLPAPER + "=replace(" + KEY_DEVICE_WALLPAPER + ",':','|')");
				db.setTransactionSuccessful();
		     } catch (Exception e){
		         //Error in between database transaction 
		     } finally {
		    	db.endTransaction();
	         }	
		}
		
		if (oldVersion < 19)
		{
			// pridame nove stlpce
			db.execSQL("ALTER TABLE " + TABLE_PROFILES + " ADD COLUMN " + KEY_DEVICE_MOBILE_DATA + " INTEGER");
			
			// updatneme zaznamy
			db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_DEVICE_MOBILE_DATA + "=0");
		}

		if (oldVersion < 20)
		{
			// pridame nove stlpce
			db.execSQL("ALTER TABLE " + TABLE_PROFILES + " ADD COLUMN " + KEY_DEVICE_MOBILE_DATA_PREFS + " INTEGER");
			
			// updatneme zaznamy
			db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_DEVICE_MOBILE_DATA_PREFS + "=0");
		}
		
		if (oldVersion < 21)
		{
			// pridame nove stlpce
			db.execSQL("ALTER TABLE " + TABLE_PROFILES + " ADD COLUMN " + KEY_DEVICE_GPS + " INTEGER");
			
			// updatneme zaznamy
			db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_DEVICE_GPS + "=0");
		}

		if (oldVersion < 22)
		{
			// pridame nove stlpce
			db.execSQL("ALTER TABLE " + TABLE_PROFILES + " ADD COLUMN " + KEY_DEVICE_RUN_APPLICATION_CHANGE + " INTEGER");
			db.execSQL("ALTER TABLE " + TABLE_PROFILES + " ADD COLUMN " + KEY_DEVICE_RUN_APPLICATION_PACKAGE_NAME + " TEXT");
			
			// updatneme zaznamy
			db.beginTransaction();
			try {
				db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_DEVICE_RUN_APPLICATION_CHANGE + "=0");
				db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_DEVICE_RUN_APPLICATION_PACKAGE_NAME + "=\"-\"");
				db.setTransactionSuccessful();
		     } catch (Exception e){
		         //Error in between database transaction 
		     } finally {
		    	db.endTransaction();
	         }	
		}
		
		if (oldVersion < 23)
		{
			// index na PORDER
			db.execSQL("CREATE INDEX IDX_PORDER ON " + TABLE_PROFILES + " (" + KEY_PORDER + ")");
		}
		
		
		
	}
	
	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new profile
	void addProfile(Profile profile) {
	
		//int porder = getMaxPOrder() + 1;

		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, profile._name); // Profile Name
		values.put(KEY_ICON, profile._icon); // Icon
		values.put(KEY_CHECKED, (profile._checked) ? 1 : 0); // Checked
		//values.put(KEY_PORDER, porder); // POrder
		values.put(KEY_PORDER, profile._porder); // POrder
		values.put(KEY_VOLUME_RINGER_MODE, profile._volumeRingerMode);
		values.put(KEY_VOLUME_RINGTONE, profile._volumeRingtone);
		values.put(KEY_VOLUME_NOTIFICATION, profile._volumeNotification);
		values.put(KEY_VOLUME_MEDIA, profile._volumeMedia);
		values.put(KEY_VOLUME_ALARM, profile._volumeAlarm);
		values.put(KEY_VOLUME_SYSTEM, profile._volumeSystem);
		values.put(KEY_VOLUME_VOICE, profile._volumeVoice);
		values.put(KEY_SOUND_RINGTONE_CHANGE, (profile._soundRingtoneChange) ? 1 : 0);
		values.put(KEY_SOUND_RINGTONE, profile._soundRingtone);
		values.put(KEY_SOUND_NOTIFICATION_CHANGE, (profile._soundNotificationChange) ? 1 : 0);
		values.put(KEY_SOUND_NOTIFICATION, profile._soundNotification);
		values.put(KEY_SOUND_ALARM_CHANGE, (profile._soundAlarmChange) ? 1 : 0);
		values.put(KEY_SOUND_ALARM, profile._soundAlarm);
		values.put(KEY_DEVICE_AIRPLANE_MODE, profile._deviceAirplaneMode);
		values.put(KEY_DEVICE_WIFI, profile._deviceWiFi);
		values.put(KEY_DEVICE_BLUETOOTH, profile._deviceBluetooth);
		values.put(KEY_DEVICE_SCREEN_TIMEOUT, profile._deviceScreenTimeout);
		values.put(KEY_DEVICE_BRIGHTNESS, profile._deviceBrightness);
		values.put(KEY_DEVICE_WALLPAPER_CHANGE, (profile._deviceWallpaperChange) ? 1 : 0);
		values.put(KEY_DEVICE_WALLPAPER, profile._deviceWallpaper);
		values.put(KEY_DEVICE_MOBILE_DATA, profile._deviceMobileData);
		values.put(KEY_DEVICE_MOBILE_DATA_PREFS, (profile._deviceMobileDataPrefs) ? 1 : 0);
		values.put(KEY_DEVICE_GPS, profile._deviceGPS);
		values.put(KEY_DEVICE_RUN_APPLICATION_CHANGE, (profile._deviceRunApplicationChange) ? 1 : 0);
		values.put(KEY_DEVICE_RUN_APPLICATION_PACKAGE_NAME, profile._deviceRunApplicationPackageName);
		

		// Inserting Row
		long id = db.insert(TABLE_PROFILES, null, values);
		db.close(); // Closing database connection
		
		profile._id = id;
		//profile.setPOrder(porder);
	}

	// Getting single profile
	Profile getProfile(long profile_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_PROFILES, 
				                 new String[] { KEY_ID, 
												KEY_NAME, 
												KEY_ICON, 
												KEY_CHECKED, 
												KEY_PORDER, 
												KEY_VOLUME_RINGER_MODE,
								         		KEY_VOLUME_RINGTONE,
								         		KEY_VOLUME_NOTIFICATION,
								         		KEY_VOLUME_MEDIA,
								         		KEY_VOLUME_ALARM,
								         		KEY_VOLUME_SYSTEM,
								         		KEY_VOLUME_VOICE,
								         		KEY_SOUND_RINGTONE_CHANGE,
								         		KEY_SOUND_RINGTONE,
								         		KEY_SOUND_NOTIFICATION_CHANGE,
								         		KEY_SOUND_NOTIFICATION,
								         		KEY_SOUND_ALARM_CHANGE,
								         		KEY_SOUND_ALARM,
								         		KEY_DEVICE_AIRPLANE_MODE,
								         		KEY_DEVICE_WIFI,
								         		KEY_DEVICE_BLUETOOTH,
								         		KEY_DEVICE_SCREEN_TIMEOUT,
								         		KEY_DEVICE_BRIGHTNESS,
								         		KEY_DEVICE_WALLPAPER_CHANGE,
								         		KEY_DEVICE_WALLPAPER,
								         		KEY_DEVICE_MOBILE_DATA,
								         		KEY_DEVICE_MOBILE_DATA_PREFS,
								         		KEY_DEVICE_GPS,
								         		KEY_DEVICE_RUN_APPLICATION_CHANGE,
								         		KEY_DEVICE_RUN_APPLICATION_PACKAGE_NAME
												}, 
				                 KEY_ID + "=?",
				                 new String[] { String.valueOf(profile_id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Profile profile = new Profile(Long.parseLong(cursor.getString(0)),
				                      cursor.getString(1), 
				                      cursor.getString(2),
				                      (Integer.parseInt(cursor.getString(3)) == 1) ? true : false,
				                      Integer.parseInt(cursor.getString(4)),
				                      Integer.parseInt(cursor.getString(5)),
				                      cursor.getString(6),
				                      cursor.getString(7),
				                      cursor.getString(8),
				                      cursor.getString(9),
				                      cursor.getString(10),
				                      cursor.getString(11),
				                      (Integer.parseInt(cursor.getString(12)) == 1) ? true : false,
				                      cursor.getString(13),
				                      (Integer.parseInt(cursor.getString(14)) == 1) ? true : false,
				                      cursor.getString(15),
				                      (Integer.parseInt(cursor.getString(16)) == 1) ? true : false,
				                      cursor.getString(17),
				                      Integer.parseInt(cursor.getString(18)),
				                      Integer.parseInt(cursor.getString(19)),
				                      Integer.parseInt(cursor.getString(20)),
				                      Integer.parseInt(cursor.getString(21)),
				                      cursor.getString(22),
				                      (Integer.parseInt(cursor.getString(23)) == 1) ? true : false,
				                      cursor.getString(24),
				                      Integer.parseInt(cursor.getString(25)),
				                      (Integer.parseInt(cursor.getString(26)) == 1) ? true : false,
				                      Integer.parseInt(cursor.getString(27)),
				                      (Integer.parseInt(cursor.getString(28)) == 1) ? true : false,
				                      cursor.getString(29)
				                      );

		cursor.close();
		db.close();

		// return profile
		return profile;
	}
	
	// Getting All Profiles
	public List<Profile> getAllProfiles() {
		List<Profile> profileList = new ArrayList<Profile>();
		// Select All Query
		String selectQuery = "SELECT " + KEY_ID + "," +
				                         KEY_NAME + "," +
				                         KEY_ICON + "," +
				                         KEY_CHECKED + "," +
				                         KEY_PORDER + "," +
										 KEY_VOLUME_RINGER_MODE + "," +
						         		 KEY_VOLUME_RINGTONE + "," +
						         		 KEY_VOLUME_NOTIFICATION + "," +
						         		 KEY_VOLUME_MEDIA + "," +
						         		 KEY_VOLUME_ALARM + "," +
						         		 KEY_VOLUME_SYSTEM + "," +
						         		 KEY_VOLUME_VOICE + "," +
						         		 KEY_SOUND_RINGTONE_CHANGE + "," +
						         		 KEY_SOUND_RINGTONE + "," +
						         		 KEY_SOUND_NOTIFICATION_CHANGE + "," +
						         		 KEY_SOUND_NOTIFICATION + "," +
						         		 KEY_SOUND_ALARM_CHANGE + "," +
						         		 KEY_SOUND_ALARM + "," +
						         		 KEY_DEVICE_AIRPLANE_MODE + "," +
						         		 KEY_DEVICE_WIFI + "," +
						         		 KEY_DEVICE_BLUETOOTH + "," +
						         		 KEY_DEVICE_SCREEN_TIMEOUT + "," +
						         		 KEY_DEVICE_BRIGHTNESS + "," +
						         		 KEY_DEVICE_WALLPAPER_CHANGE + "," +
						         		 KEY_DEVICE_WALLPAPER + "," +
						         		 KEY_DEVICE_MOBILE_DATA + "," +
						         		 KEY_DEVICE_MOBILE_DATA_PREFS + "," +
						         		 KEY_DEVICE_GPS + "," +
						         		 KEY_DEVICE_RUN_APPLICATION_CHANGE + "," +
						         		 KEY_DEVICE_RUN_APPLICATION_PACKAGE_NAME +
		                     " FROM " + TABLE_PROFILES + " ORDER BY " + KEY_PORDER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Profile profile = new Profile();
				profile._id = Long.parseLong(cursor.getString(0));
				profile._name = cursor.getString(1);
				profile._icon = (cursor.getString(2));
				profile._checked = ((Integer.parseInt(cursor.getString(3)) == 1) ? true : false);
				profile._porder = (Integer.parseInt(cursor.getString(4)));
				profile._volumeRingerMode = Integer.parseInt(cursor.getString(5));
				profile._volumeRingtone = cursor.getString(6);
				profile._volumeNotification = cursor.getString(7);
                profile._volumeMedia = cursor.getString(8);
                profile._volumeAlarm = cursor.getString(9);
                profile._volumeSystem = cursor.getString(10);
                profile._volumeVoice = cursor.getString(11);
                profile._soundRingtoneChange = (Integer.parseInt(cursor.getString(12)) == 1) ? true : false;
                profile._soundRingtone = cursor.getString(13);
                profile._soundNotificationChange = (Integer.parseInt(cursor.getString(14)) == 1) ? true : false;
                profile._soundNotification = cursor.getString(15);
                profile._soundAlarmChange = (Integer.parseInt(cursor.getString(16)) == 1) ? true : false;
                profile._soundAlarm = cursor.getString(17);
                profile._deviceAirplaneMode = Integer.parseInt(cursor.getString(18));
                profile._deviceWiFi = Integer.parseInt(cursor.getString(19));
                profile._deviceBluetooth = Integer.parseInt(cursor.getString(20));
                profile._deviceScreenTimeout = Integer.parseInt(cursor.getString(21));
                profile._deviceBrightness = cursor.getString(22);
                profile._deviceWallpaperChange = (Integer.parseInt(cursor.getString(23)) == 1) ? true : false;
                profile._deviceWallpaper = cursor.getString(24);
                profile._deviceMobileData = Integer.parseInt(cursor.getString(25));
                profile._deviceMobileDataPrefs = (Integer.parseInt(cursor.getString(26)) == 1) ? true : false;
                profile._deviceGPS = Integer.parseInt(cursor.getString(27));
                profile._deviceRunApplicationChange = (Integer.parseInt(cursor.getString(28)) == 1) ? true : false;
                profile._deviceRunApplicationPackageName = cursor.getString(29);
				// Adding contact to list
				profileList.add(profile);
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();
		
		// return profile list
		return profileList;
	}

	// Updating single profile
	public int updateProfile(Profile profile) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, profile._name);
		values.put(KEY_ICON, profile._icon);
		values.put(KEY_CHECKED, (profile._checked) ? 1 : 0);
		values.put(KEY_PORDER, profile._porder);
		values.put(KEY_VOLUME_RINGER_MODE, profile._volumeRingerMode);
		values.put(KEY_VOLUME_RINGTONE, profile._volumeRingtone);
		values.put(KEY_VOLUME_NOTIFICATION, profile._volumeNotification);
		values.put(KEY_VOLUME_MEDIA, profile._volumeMedia);
		values.put(KEY_VOLUME_ALARM, profile._volumeAlarm);
		values.put(KEY_VOLUME_SYSTEM, profile._volumeSystem);
		values.put(KEY_VOLUME_VOICE, profile._volumeVoice);
		values.put(KEY_SOUND_RINGTONE_CHANGE, (profile._soundRingtoneChange) ? 1 : 0);
		values.put(KEY_SOUND_RINGTONE, profile._soundRingtone);
		values.put(KEY_SOUND_NOTIFICATION_CHANGE, (profile._soundNotificationChange) ? 1 : 0);
		values.put(KEY_SOUND_NOTIFICATION, profile._soundNotification);
		values.put(KEY_SOUND_ALARM_CHANGE, (profile._soundAlarmChange) ? 1 : 0);
		values.put(KEY_SOUND_ALARM, profile._soundAlarm);
		values.put(KEY_DEVICE_AIRPLANE_MODE, profile._deviceAirplaneMode);
		values.put(KEY_DEVICE_WIFI, profile._deviceWiFi);
		values.put(KEY_DEVICE_BLUETOOTH, profile._deviceBluetooth);
		values.put(KEY_DEVICE_SCREEN_TIMEOUT, profile._deviceScreenTimeout);
		values.put(KEY_DEVICE_BRIGHTNESS, profile._deviceBrightness);
		values.put(KEY_DEVICE_WALLPAPER_CHANGE, (profile._deviceWallpaperChange) ? 1 : 0);
		values.put(KEY_DEVICE_WALLPAPER, profile._deviceWallpaper);
		values.put(KEY_DEVICE_MOBILE_DATA, profile._deviceMobileData);
		values.put(KEY_DEVICE_MOBILE_DATA_PREFS, (profile._deviceMobileDataPrefs) ? 1 : 0);
		values.put(KEY_DEVICE_GPS, profile._deviceGPS);
		values.put(KEY_DEVICE_RUN_APPLICATION_CHANGE, (profile._deviceRunApplicationChange) ? 1 : 0);
		values.put(KEY_DEVICE_RUN_APPLICATION_PACKAGE_NAME, profile._deviceRunApplicationPackageName);
		

		// updating row
		int r = db.update(TABLE_PROFILES, values, KEY_ID + " = ?",
				        new String[] { String.valueOf(profile._id) });
        db.close();
        
		return r;
	}

	// Deleting single profile
	public void deleteProfile(Profile profile) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PROFILES, KEY_ID + " = ?",
				new String[] { String.valueOf(profile._id) });
		db.close();
	}

	// Deleting all profile2
	public void deleteAllProfiles() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PROFILES, null,	null);
		db.close();
	}

	// Getting profiles Count
	public int getProfilesCount() {
		String countQuery = "SELECT  * FROM " + TABLE_PROFILES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		// return count
		int r = cursor.getCount();

		cursor.close();
		db.close();
		
		return r;
	}
	
/*	// Getting max(porder)
	public int getMaxPOrder() {
		String countQuery = "SELECT MAX(PORDER) FROM " + TABLE_PROFILES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int r;
		
		if (cursor.getCount() == 0)
			r = 0;
		else
		{	
			if (cursor.moveToFirst())
				// return max(porder)
				r = cursor.getInt(0);
			else
				r = 0;
		}

		cursor.close();
		db.close();
		
		return r;
		
	}
*/	
	public void activateProfile(Profile profile)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		
		db.beginTransaction();
		try {
			// update all profiles checked to false
			ContentValues valuesAll = new ContentValues();
			valuesAll.put(KEY_CHECKED, 0);
			db.update(TABLE_PROFILES, valuesAll, null, null);

			// updating checked = true for profile
			//profile.setChecked(true);
			
			ContentValues values = new ContentValues();
			//values.put(KEY_CHECKED, (profile.getChecked()) ? 1 : 0);
			values.put(KEY_CHECKED, 1);

			db.update(TABLE_PROFILES, values, KEY_ID + " = ?",
					        new String[] { String.valueOf(profile._id) });

			
			db.setTransactionSuccessful();
	     } catch (Exception e){
	         //Error in between database transaction 
	     } finally {
	    	db.endTransaction();
         }	
		
         db.close();
	}
	
	public Profile getActivatedProfile()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Profile profile;

		Cursor cursor = db.query(TABLE_PROFILES, 
				                 new String[] { KEY_ID, 
												KEY_NAME, 
												KEY_ICON, 
												KEY_CHECKED, 
												KEY_PORDER,
												KEY_VOLUME_RINGER_MODE,
								         		KEY_VOLUME_RINGTONE,
								         		KEY_VOLUME_NOTIFICATION,
								         		KEY_VOLUME_MEDIA,
								         		KEY_VOLUME_ALARM,
								         		KEY_VOLUME_SYSTEM,
								         		KEY_VOLUME_VOICE,
								         		KEY_SOUND_RINGTONE_CHANGE,
								         		KEY_SOUND_RINGTONE,
								         		KEY_SOUND_NOTIFICATION_CHANGE,
								         		KEY_SOUND_NOTIFICATION,
								         		KEY_SOUND_ALARM_CHANGE,
								         		KEY_SOUND_ALARM,
								         		KEY_DEVICE_AIRPLANE_MODE,
								         		KEY_DEVICE_WIFI,
								         		KEY_DEVICE_BLUETOOTH,
								         		KEY_DEVICE_SCREEN_TIMEOUT,
								         		KEY_DEVICE_BRIGHTNESS,
								         		KEY_DEVICE_WALLPAPER_CHANGE,
								         		KEY_DEVICE_WALLPAPER,
								         		KEY_DEVICE_MOBILE_DATA,
								         		KEY_DEVICE_MOBILE_DATA_PREFS,
								         		KEY_DEVICE_GPS,
								         		KEY_DEVICE_RUN_APPLICATION_CHANGE,
								         		KEY_DEVICE_RUN_APPLICATION_PACKAGE_NAME
												}, 
				                 KEY_CHECKED + "=?",
				                 new String[] { "1" }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		
		int rc = cursor.getCount();
		
		if (rc == 1)
		{

			profile = new Profile(Long.parseLong(cursor.getString(0)),
					                      cursor.getString(1), 
					                      cursor.getString(2),
					                      (Integer.parseInt(cursor.getString(3)) == 1) ? true : false,
					                      Integer.parseInt(cursor.getString(4)),
					                      Integer.parseInt(cursor.getString(5)),
					                      cursor.getString(6),
					                      cursor.getString(7),
					                      cursor.getString(8),
					                      cursor.getString(9),
					                      cursor.getString(10),
					                      cursor.getString(11),
					                      (Integer.parseInt(cursor.getString(12)) == 1) ? true : false,
					                      cursor.getString(13),
					                      (Integer.parseInt(cursor.getString(14)) == 1) ? true : false,
					                      cursor.getString(15),
					                      (Integer.parseInt(cursor.getString(16)) == 1) ? true : false,
					                      cursor.getString(17),
					                      Integer.parseInt(cursor.getString(18)),
					                      Integer.parseInt(cursor.getString(19)),
					                      Integer.parseInt(cursor.getString(20)),
					                      Integer.parseInt(cursor.getString(21)),
					                      cursor.getString(22),
					                      (Integer.parseInt(cursor.getString(23)) == 1) ? true : false,
					                      cursor.getString(24),
					                      Integer.parseInt(cursor.getString(25)),
					                      (Integer.parseInt(cursor.getString(26)) == 1) ? true : false,
					                      Integer.parseInt(cursor.getString(27)),
					                      (Integer.parseInt(cursor.getString(28)) == 1) ? true : false,
					                      cursor.getString(29)
					                      );
		}
		else
			profile = null;

		cursor.close();
		db.close();

		// return profile
		return profile;
		
	}
	
	public Profile getFirstProfile()
	{
		String selectQuery = "SELECT " + KEY_ID + "," +
						                 KEY_NAME + "," +
						                 KEY_ICON + "," +
						                 KEY_CHECKED + "," +
						                 KEY_PORDER + "," +
										 KEY_VOLUME_RINGER_MODE + "," +
						        		 KEY_VOLUME_RINGTONE + "," +
						        		 KEY_VOLUME_NOTIFICATION + "," +
						        		 KEY_VOLUME_MEDIA + "," +
						        		 KEY_VOLUME_ALARM + "," +
						        		 KEY_VOLUME_SYSTEM + "," +
						        		 KEY_VOLUME_VOICE + "," +
						        		 KEY_SOUND_RINGTONE_CHANGE + "," +
						        		 KEY_SOUND_RINGTONE + "," +
						        		 KEY_SOUND_NOTIFICATION_CHANGE + "," +
						        		 KEY_SOUND_NOTIFICATION + "," +
						        		 KEY_SOUND_ALARM_CHANGE + "," +
						        		 KEY_SOUND_ALARM + "," +
						        		 KEY_DEVICE_AIRPLANE_MODE + "," +
						        		 KEY_DEVICE_WIFI + "," +
						        		 KEY_DEVICE_BLUETOOTH + "," +
						        		 KEY_DEVICE_SCREEN_TIMEOUT + "," +
						        		 KEY_DEVICE_BRIGHTNESS + "," +
						        		 KEY_DEVICE_WALLPAPER_CHANGE + "," +
						        		 KEY_DEVICE_WALLPAPER + "," +
						        		 KEY_DEVICE_MOBILE_DATA + "," +
						        		 KEY_DEVICE_MOBILE_DATA_PREFS + "," +
						        		 KEY_DEVICE_GPS + "," +
						        		 KEY_DEVICE_RUN_APPLICATION_CHANGE + "," +
						        		 KEY_DEVICE_RUN_APPLICATION_PACKAGE_NAME +
						    " FROM " + TABLE_PROFILES + " ORDER BY " + KEY_PORDER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		Profile profile = null; 
		
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			profile = new Profile();
			profile._id = Long.parseLong(cursor.getString(0));
			profile._name = cursor.getString(1);
			profile._icon = (cursor.getString(2));
			profile._checked = ((Integer.parseInt(cursor.getString(3)) == 1) ? true : false);
			profile._porder = (Integer.parseInt(cursor.getString(4)));
			profile._volumeRingerMode = Integer.parseInt(cursor.getString(5));
			profile._volumeRingtone = cursor.getString(6);
			profile._volumeNotification = cursor.getString(7);
			profile._volumeMedia = cursor.getString(8);
			profile._volumeAlarm = cursor.getString(9);
			profile._volumeSystem = cursor.getString(10);
			profile._volumeVoice = cursor.getString(11);
			profile._soundRingtoneChange = (Integer.parseInt(cursor.getString(12)) == 1) ? true : false;
			profile._soundRingtone = cursor.getString(13);
			profile._soundNotificationChange = (Integer.parseInt(cursor.getString(14)) == 1) ? true : false;
			profile._soundNotification = cursor.getString(15);
			profile._soundAlarmChange = (Integer.parseInt(cursor.getString(16)) == 1) ? true : false;
			profile._soundAlarm = cursor.getString(17);
			profile._deviceAirplaneMode = Integer.parseInt(cursor.getString(18));
			profile._deviceWiFi = Integer.parseInt(cursor.getString(19));
			profile._deviceBluetooth = Integer.parseInt(cursor.getString(20));
			profile._deviceScreenTimeout = Integer.parseInt(cursor.getString(21));
			profile._deviceBrightness = cursor.getString(22);
			profile._deviceWallpaperChange = (Integer.parseInt(cursor.getString(23)) == 1) ? true : false;
			profile._deviceWallpaper = cursor.getString(24);
			profile._deviceMobileData = Integer.parseInt(cursor.getString(25));
			profile._deviceMobileDataPrefs = (Integer.parseInt(cursor.getString(26)) == 1) ? true : false;
			profile._deviceGPS = Integer.parseInt(cursor.getString(27));
			profile._deviceRunApplicationChange = (Integer.parseInt(cursor.getString(28)) == 1) ? true : false;
			profile._deviceRunApplicationPackageName = cursor.getString(29);
		}
		
		cursor.close();
		db.close();
		
		// return profile list
		return profile;
		
	}
	
	public int getProfilePosition(Profile profile)
	{
		String selectQuery = "SELECT " + KEY_ID +
							   " FROM " + TABLE_PROFILES + " ORDER BY " + KEY_PORDER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		// looping through all rows and adding to list
		long lid;
		int position = 0;
		if (cursor.moveToFirst()) {
			do {
				lid = Long.parseLong(cursor.getString(0));
				if (lid == profile._id)
					return position;
				position++;
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		db.close();
		
		// return profile list
		return -1;
		
		
	}
	
	public void setPOrder(List<Profile> list)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();

		db.beginTransaction();
		try {

			for (Profile profile : list)
			{
				values.put(KEY_PORDER, profile._porder);

				db.update(TABLE_PROFILES, values, KEY_ID + " = ?",
					        new String[] { String.valueOf(profile._id) });
			}
			
			db.setTransactionSuccessful();
	     } catch (Exception e){
	         //Error in between database transaction 
	     } finally {
	    	db.endTransaction();
         }	
		
        db.close();
	}
	
	public void setChecked(List<Profile> list)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		
		db.beginTransaction();
		try {

			for (Profile profile : list)
			{
				values.put(KEY_CHECKED, profile._checked);

				db.update(TABLE_PROFILES, values, KEY_ID + " = ?",
					        new String[] { String.valueOf(profile._id) });
			}
			
			db.setTransactionSuccessful();
	     } catch (Exception e){
	         //Error in between database transaction 
	     } finally {
	    	db.endTransaction();
         }	
		
        db.close();
	}
	
	//@SuppressWarnings("resource")
	public int importDB()
	{
		int ret = 0;
		
		// Close SQLiteOpenHelper so it will commit the created empty
		// database to internal storage
		//close();

		try {
			
			File sd = Environment.getExternalStorageDirectory();
			//File data = Environment.getDataDirectory();
			
			//File dataDB = new File(data, DB_FILEPATH + "/" + DATABASE_NAME);
			File exportedDB = new File(sd, EXPORT_DBPATH + "/" + EXPORT_FILENAME);
			
			if (exportedDB.exists())
			{
				// zistenie verzie zalohy
				SQLiteDatabase exportedDBObj = SQLiteDatabase.openDatabase(exportedDB.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
				//Log.d("DatabaseHandler.importDB", "databaseVersion="+exportedDBObj.getVersion());
				//if (exportedDBObj.getVersion() == DATABASE_VERSION)
				if (exportedDBObj.getVersion() <= DATABASE_VERSION)
				{	
					
					// db z SQLiteOpenHelper
					SQLiteDatabase db = this.getWritableDatabase();
					
					try {
						db.beginTransaction();
						
						db.execSQL("DELETE FROM " + TABLE_PROFILES);
						
						// cusor na data exportedDB
						Cursor cursor = exportedDBObj.rawQuery("SELECT * FROM "+TABLE_PROFILES, null);
						String[] columnNames = cursor.getColumnNames();
						ContentValues values = new ContentValues();
	
						if (cursor.moveToFirst()) {
							do {
									values.clear();
									for (int i = 0; i < columnNames.length; i++)
									{
										values.put(columnNames[i], cursor.getString(i));
										//Log.d("DatabaseHandler.importDB", "cn="+columnNames[i]+" val="+cursor.getString(i));
									}
									
									// for non existent fields set default value
									if (exportedDBObj.getVersion() < 19)
									{
										values.put(KEY_DEVICE_MOBILE_DATA, 0);
									}
									if (exportedDBObj.getVersion() < 20)
									{
										values.put(KEY_DEVICE_MOBILE_DATA_PREFS, 0);
									}
									if (exportedDBObj.getVersion() < 21)
									{
										values.put(KEY_DEVICE_GPS, 0);
									}
									if (exportedDBObj.getVersion() < 22)
									{
										values.put(KEY_DEVICE_RUN_APPLICATION_CHANGE, 0);
										values.put(KEY_DEVICE_RUN_APPLICATION_PACKAGE_NAME, "-");
									}
									
									
									
									// Inserting Row do db z SQLiteOpenHelper
									db.insert(TABLE_PROFILES, null, values);
							} while (cursor.moveToNext());
						}
	
						cursor.close();
						
						db.setTransactionSuccessful();
						
						ret = 1;
					}
					finally {
						db.endTransaction();
					}
					db.close();
					
					//FileChannel src = new FileInputStream(exportedDB).getChannel();
					//FileChannel dst = new FileOutputStream(dataDB).getChannel();
					//dst.transferFrom(src, 0, src.size());
					//src.close();
					//dst.close();
					
					// Access the copied database so SQLiteHelper will cache it and mark
					// it as created
					//getWritableDatabase().close();
				}
				else
				{
					Log.w("DatabaseHandler.importDB", "wrong exported db version");
				}
			}
		} catch (Exception e) {
			Log.e("DatabaseHandler.importDB", e.getMessage());
		}
		
		return ret;
	}
	
	@SuppressWarnings("resource")
	public int exportDB()
	{
		int ret = 0;
		
		try {
			
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();
			
			File dataDB = new File(data, DB_FILEPATH + "/" + DATABASE_NAME);
			File exportedDB = new File(sd, EXPORT_DBPATH + "/" + EXPORT_FILENAME);
			
			//Log.d("DatabaseHandler.exportDB", "dataDB="+dataDB.getAbsolutePath());
			//Log.d("DatabaseHandler.exportDB", "exportedDB="+exportedDB.getAbsolutePath());
			
			if (dataDB.exists())
			{
				
				File exportDir = new File(sd, EXPORT_DBPATH);
				if (!(exportDir.exists() && exportDir.isDirectory()))
				{
					exportDir.mkdirs();
					//Log.d("DatabaseHandler.exportDB", "mkdir="+exportDir.getAbsolutePath());
				}
				
				FileChannel src = new FileInputStream(dataDB).getChannel();
				FileChannel dst = new FileOutputStream(exportedDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				
				ret = 1;
			}
		} catch (Exception e) {
			Log.e("DatabaseHandler.exportDB", e.getMessage());
		}

		return ret;
	}
}
