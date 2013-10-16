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

	// singleton fields 
    private static DatabaseHandler instance;
    private static SQLiteDatabase writableDb;	
    
	// Database Version
	private static final int DATABASE_VERSION = 34;
	// starting version when added Events table
	private static final int DATABASE_VERSION_EVENTS = 25;

	// Database Name
	private static final String DATABASE_NAME = "phoneProfilesManager";

	// Profiles table name
	private static final String TABLE_PROFILES = "profiles";
	private static final String TABLE_EVENTS = "events";
	private static final String TABLE_EVENT_TIMELINE = "event_timeline";
	
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
	private static final String KEY_DEVICE_AUTOSYNC = "deviceAutosync";
	private static final String KEY_SHOW_IN_ACTIVATOR = "showInActivator";

	private static final String KEY_E_ID = "id";
	private static final String KEY_E_NAME = "name";
	private static final String KEY_E_TYPE = "type";
	private static final String KEY_E_FK_PROFILE = "fkProfile";
	private static final String KEY_E_STATUS = "status";
	private static final String KEY_E_START_TIME = "startTime";
	private static final String KEY_E_END_TIME = "endTime";
	private static final String KEY_E_DAYS_OF_WEEK = "daysOfWeek";
	private static final String KEY_E_USE_END_TIME = "useEndTime";
	
	private static final String KEY_ET_ID = "id";
	private static final String KEY_ET_EORDER = "eorder";
	private static final String KEY_ET_FK_EVENT = "fkEvent";
	private static final String KEY_ET_FK_PROFILE_RETURN = "fkProfileReturn";
	
	/**
     * Constructor takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     *
     * @param context
     *            the application context
     */	
	private DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/**
     * Get default instance of the class to keep it a singleton
     *
     * @param context
     *            the application context
     */
    public static DatabaseHandler getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHandler(context);
        }
        return instance;
    }
    
    /**
     * Returns a writable database instance in order not to open and close many
     * SQLiteDatabase objects simultaneously
     *
     * @return a writable instance to SQLiteDatabase
     */
    public SQLiteDatabase getMyWritableDatabase() {
        if ((writableDb == null) || (!writableDb.isOpen())) {
            writableDb = this.getWritableDatabase();
        }
 
        return writableDb;
    }
 
    @Override
    public synchronized void close() {
        super.close();
        if (writableDb != null) {
            writableDb.close();
            writableDb = null;
        }
    }
    
    // be sure to call this method by: DatabaseHandler.getInstance().closeConnecion() 
    // when application is closed by somemeans most likely
    // onDestroy method of application
    public synchronized void closeConnecion() {
    	if (instance != null)
    	{
    		instance.close();
            instance = null;
        }
    }    
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		final String CREATE_PROFILES_TABLE = "CREATE TABLE " + TABLE_PROFILES + "("
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
				+ KEY_DEVICE_RUN_APPLICATION_PACKAGE_NAME + " TEXT,"
				+ KEY_DEVICE_AUTOSYNC + " INTEGER,"
				+ KEY_SHOW_IN_ACTIVATOR + " INTEGER"
				+ ")";
		db.execSQL(CREATE_PROFILES_TABLE);
		
		db.execSQL("CREATE INDEX IDX_PORDER ON " + TABLE_PROFILES + " (" + KEY_PORDER + ")");
		db.execSQL("CREATE INDEX IDX_SHOW_IN_ACTIVATOR ON " + TABLE_PROFILES + " (" + KEY_SHOW_IN_ACTIVATOR + ")");
		db.execSQL("CREATE INDEX IDX_P_NAME ON " + TABLE_PROFILES + " (" + KEY_NAME + ")");

		final String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
				+ KEY_E_ID + " INTEGER PRIMARY KEY,"
				+ KEY_E_NAME + " TEXT,"
				+ KEY_E_TYPE + " INTEGER,"
				+ KEY_E_FK_PROFILE + " INTEGER,"
				+ KEY_E_START_TIME + " INTEGER,"
				+ KEY_E_END_TIME + " INTEGER,"
				+ KEY_E_DAYS_OF_WEEK + " TEXT,"
				+ KEY_E_USE_END_TIME + " INTEGER,"
				+ KEY_E_STATUS + " INTEGER"
				+ ")";
		db.execSQL(CREATE_EVENTS_TABLE);

		db.execSQL("CREATE INDEX IDX_FK_PROFILE ON " + TABLE_EVENTS + " (" + KEY_E_FK_PROFILE + ")");
		db.execSQL("CREATE INDEX IDX_E_NAME ON " + TABLE_EVENTS + " (" + KEY_E_NAME + ")");

		final String CREATE_EVENTTIME_TABLE = "CREATE TABLE " + TABLE_EVENT_TIMELINE + "("
				+ KEY_ET_ID + " INTEGER PRIMARY KEY,"
				+ KEY_ET_EORDER + " INTEGER," 
				+ KEY_ET_FK_EVENT + " INTEGER"
				+ KEY_ET_FK_PROFILE_RETURN + " INTEGER"
				+ ")";
		db.execSQL(CREATE_EVENTTIME_TABLE);

		db.execSQL("CREATE INDEX IDX_ET_PORDER ON " + TABLE_EVENT_TIMELINE + " (" + KEY_ET_EORDER + ")");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		//Log.d("DatabaseHandler.onUpgrade", "oldVersion="+oldVersion);
		//Log.d("DatabaseHandler.onUpgrade", "newVersion="+newVersion);
		
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

		if (oldVersion < 24)
		{
			// pridame nove stlpce
			db.execSQL("ALTER TABLE " + TABLE_PROFILES + " ADD COLUMN " + KEY_DEVICE_AUTOSYNC + " INTEGER");
			
			// updatneme zaznamy
			db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_DEVICE_AUTOSYNC + "=0");
			
			db.execSQL("CREATE INDEX IDX_P_NAME ON " + TABLE_PROFILES + " (" + KEY_NAME + ")");
			db.execSQL("CREATE INDEX IDX_E_NAME ON " + TABLE_EVENTS + " (" + KEY_E_NAME + ")");
		}
		
		if (oldVersion < 25)
		{
			final String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
					+ KEY_E_ID + " INTEGER PRIMARY KEY,"
					+ KEY_E_NAME + " TEXT,"
					+ KEY_E_TYPE + " INTEGER,"
					+ KEY_E_FK_PROFILE + " INTEGER"
					+ ")";
			db.execSQL(CREATE_EVENTS_TABLE);
		}

		if (oldVersion < 26)
		{
			// pridame nove stlpce
			db.execSQL("ALTER TABLE " + TABLE_PROFILES + " ADD COLUMN " + KEY_SHOW_IN_ACTIVATOR + " INTEGER");
			
			// updatneme zaznamy
			db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + KEY_SHOW_IN_ACTIVATOR + "=1");
		}

		if (oldVersion < 28)
		{
			// index na SHOW_IN_ACTIVATOR
			db.execSQL("CREATE INDEX IDX_SHOW_IN_ACTIVATOR ON " + TABLE_PROFILES + " (" + KEY_SHOW_IN_ACTIVATOR + ")");
		}
		
		if (oldVersion < 29)
		{
			// pridame nove stlpce
			db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + KEY_E_START_TIME + " INTEGER");
			db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + KEY_E_END_TIME + " INTEGER");
			db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + KEY_E_DAYS_OF_WEEK + " TEXT");
			
			// updatneme zaznamy
			db.execSQL("UPDATE " + TABLE_EVENTS + " SET " + KEY_E_START_TIME + "=0");
			db.execSQL("UPDATE " + TABLE_EVENTS + " SET " + KEY_E_END_TIME + "=0");
			db.execSQL("UPDATE " + TABLE_EVENTS + " SET " + KEY_E_DAYS_OF_WEEK + "=\"#ALL#\"");

			// pridame index
			db.execSQL("CREATE INDEX IDX_FK_PROFILE ON " + TABLE_EVENTS + " (" + KEY_E_FK_PROFILE + ")");
		}

		if (oldVersion < 30)
		{
			// pridame nove stlpce
			db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + KEY_E_USE_END_TIME + " INTEGER");
			
			// updatneme zaznamy
			db.execSQL("UPDATE " + TABLE_EVENTS + " SET " + KEY_E_USE_END_TIME + "=0");
		}
		
		if (oldVersion < 32)
		{
			// pridame nove stlpce
			db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + KEY_E_STATUS + " INTEGER");
			
			// updatneme zaznamy
			db.execSQL("UPDATE " + TABLE_EVENTS + " SET " + KEY_E_STATUS + "=0");
		}
		
		if (oldVersion < 34)
		{
			final String CREATE_EVENTTIME_TABLE = "CREATE TABLE " + TABLE_EVENT_TIMELINE + "("
					+ KEY_ET_ID + " INTEGER PRIMARY KEY,"
					+ KEY_ET_EORDER + " INTEGER," 
					+ KEY_ET_FK_EVENT + " INTEGER,"
					+ KEY_ET_FK_PROFILE_RETURN + " INTEGER"
					+ ")";
			db.execSQL(CREATE_EVENTTIME_TABLE);
			
			db.execSQL("CREATE INDEX IDX_ET_PORDER ON " + TABLE_EVENT_TIMELINE + " (" + KEY_ET_EORDER + ")");
		}
		
	}
	

// PROFILES --------------------------------------------------------------------------------
	
	// Adding new profile
	void addProfile(Profile profile) {
	
		int porder = getMaxPOrder() + 1;

		//SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, profile._name); // Profile Name
		values.put(KEY_ICON, profile._icon); // Icon
		values.put(KEY_CHECKED, (profile._checked) ? 1 : 0); // Checked
		values.put(KEY_PORDER, porder); // POrder
		//values.put(KEY_PORDER, profile._porder); // POrder
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
		values.put(KEY_DEVICE_AUTOSYNC, profile._deviceAutosync);
		values.put(KEY_SHOW_IN_ACTIVATOR, (profile._showInActivator) ? 1 : 0);
		

		// Inserting Row
		long id = db.insert(TABLE_PROFILES, null, values);
		//db.close(); // Closing database connection
		
		profile._id = id;
		profile._porder = porder;
	}

	// Getting single profile
	Profile getProfile(long profile_id) {
		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();

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
								         		KEY_DEVICE_RUN_APPLICATION_PACKAGE_NAME,
								         		KEY_DEVICE_AUTOSYNC, 
								         		KEY_SHOW_IN_ACTIVATOR
												}, 
				                 KEY_ID + "=?",
				                 new String[] { String.valueOf(profile_id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		
		Profile profile = null;
		
		if (cursor.getCount() > 0)
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
					                      cursor.getString(29),
					                      Integer.parseInt(cursor.getString(30)),
					                      (Integer.parseInt(cursor.getString(31)) == 1) ? true : false
					                      );
		}

		cursor.close();
		//db.close();

		// return profile
		return profile;
	}
	
	// Getting All Profiles
	public List<Profile> getAllProfiles() {
		List<Profile> profileList = new ArrayList<Profile>();
		
		// Select All Query
		final String selectQuery = "SELECT " + KEY_ID + "," +
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
						         		 KEY_DEVICE_RUN_APPLICATION_PACKAGE_NAME + "," +
						         		 KEY_DEVICE_AUTOSYNC + "," +
						         		 KEY_SHOW_IN_ACTIVATOR +
		                     " FROM " + TABLE_PROFILES;

		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		
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
                profile._deviceAutosync = Integer.parseInt(cursor.getString(30));
                profile._showInActivator = (Integer.parseInt(cursor.getString(31)) == 1) ? true : false;
				// Adding contact to list
				profileList.add(profile);
			} while (cursor.moveToNext());
		}

		cursor.close();
		//db.close();
		
		// return profile list
		return profileList;
	}

	// Updating single profile
	public int updateProfile(Profile profile) {
		//SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();

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
		values.put(KEY_DEVICE_AUTOSYNC, profile._deviceAutosync);
		values.put(KEY_SHOW_IN_ACTIVATOR, (profile._showInActivator) ? 1 : 0);
		

		// updating row
		int r = db.update(TABLE_PROFILES, values, KEY_ID + " = ?",
				        new String[] { String.valueOf(profile._id) });
        //db.close();
        
		return r;
	}

	// Deleting single profile
	public void deleteProfile(Profile profile) {
		//SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		
		db.beginTransaction();
		try {
			db.delete(TABLE_PROFILES, KEY_ID + " = ?",
					new String[] { String.valueOf(profile._id) });
	
			// unlink profile from events
			ContentValues values = new ContentValues();
			values.put(KEY_E_FK_PROFILE, 0);
			db.update(TABLE_EVENTS, values, KEY_E_FK_PROFILE + " = ?",
			        new String[] { String.valueOf(profile._id) });

			db.setTransactionSuccessful();
	    } catch (Exception e){
	        //Error in between database transaction 
	    } finally {
	    	db.endTransaction();
        }	
		
		//db.close();
	}

	// Deleting all profiles
	public void deleteAllProfiles() {
		//SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		
		db.beginTransaction();

		try {
			db.delete(TABLE_PROFILES, null,	null);
			
			// unlink profiles from events
			ContentValues values = new ContentValues();
			values.put(KEY_E_FK_PROFILE, 0);
			db.update(TABLE_EVENTS, values, null, null);
			
			db.setTransactionSuccessful();
	    } catch (Exception e){
	        //Error in between database transaction 
	    } finally {
	    	db.endTransaction();
        }	

		//db.close();
	}

	// Getting profiles Count
	public int getProfilesCount(boolean forActivator) {
		final String countQuery;
		if (forActivator)
		  countQuery = "SELECT  count(*) FROM " + TABLE_PROFILES + " WHERE " + KEY_SHOW_IN_ACTIVATOR + "=1";
		else
		  countQuery = "SELECT  count(*) FROM " + TABLE_PROFILES;

		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		
		Cursor cursor = db.rawQuery(countQuery, null);
		
		int r;
		
		if (cursor != null)
		{
			cursor.moveToFirst();
			r = Integer.parseInt(cursor.getString(0));
		}
		else
			r = 0;

		cursor.close();
		//db.close();
		
		return r;
	}
	
	// Getting max(porder)
	public int getMaxPOrder() {
		String countQuery = "SELECT MAX("+KEY_PORDER+") FROM " + TABLE_PROFILES;
		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();

		Cursor cursor = db.rawQuery(countQuery, null);

		int r;
		
		if (cursor.getCount() == 0)
		{
			//Log.e("DatabaseHandler.getMaxPOrder","count=0");
			r = 0;
		}
		else
		{	
			if (cursor.moveToFirst())
			{
				r = cursor.getInt(0);
				//Log.e("DatabaseHandler.getMaxPOrder","porder="+r);
			}
			else
			{
				r = 0;
				//Log.e("DatabaseHandler.getMaxPOrder","moveToFirst=false");
			}
		}

		cursor.close();
		//db.close();
		
		return r;
		
	}
	
	public void doActivateProfile(Profile profile, boolean activate)
	{
		//SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		
		db.beginTransaction();
		try {
			// update all profiles checked to false
			ContentValues valuesAll = new ContentValues();
			valuesAll.put(KEY_CHECKED, 0);
			db.update(TABLE_PROFILES, valuesAll, null, null);

			// updating checked = true for profile
			//profile.setChecked(true);
			
			if (activate)
			{
				ContentValues values = new ContentValues();
				//values.put(KEY_CHECKED, (profile.getChecked()) ? 1 : 0);
				values.put(KEY_CHECKED, 1);
	
				db.update(TABLE_PROFILES, values, KEY_ID + " = ?",
						        new String[] { String.valueOf(profile._id) });
			}
			
			db.setTransactionSuccessful();
	     } catch (Exception e){
	         //Error in between database transaction 
	     } finally {
	    	db.endTransaction();
         }	
		
         //db.close();
	}
	
	public void activateProfile(Profile profile)
	{
		doActivateProfile(profile, true);
	}

	public void deactivateProfile()
	{
		doActivateProfile(null, false);
	}
	
	public Profile getActivatedProfile()
	{
		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();

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
								         		KEY_DEVICE_RUN_APPLICATION_PACKAGE_NAME,
								         		KEY_DEVICE_AUTOSYNC,
								         		KEY_SHOW_IN_ACTIVATOR
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
					                      cursor.getString(29),
					                      Integer.parseInt(cursor.getString(30)),
					                      (Integer.parseInt(cursor.getString(31)) == 1) ? true : false
					                      );
		}
		else
			profile = null;

		cursor.close();
		//db.close();

		// return profile
		return profile;
		
	}
/*	
	public Profile getFirstProfile()
	{
		final String selectQuery = "SELECT " + KEY_ID + "," +
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
						        		 KEY_DEVICE_RUN_APPLICATION_PACKAGE_NAME + "," +
						        		 KEY_DEVICE_AUTOSYNC + "," +
						        		 KEY_SHOW_IN_ACTIVATOR +
						    " FROM " + TABLE_PROFILES;

		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		
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
			profile._deviceAutosync = Integer.parseInt(cursor.getString(30));
			profile._showInActivator = (Integer.parseInt(cursor.getString(31)) == 1) ? true : false;
		}
		
		cursor.close();
		//db.close();
		
		// return profile list
		return profile;
		
	}
*/
/*
	public int getProfilePosition(Profile profile)
	{
		final String selectQuery = "SELECT " + KEY_ID +
							   " FROM " + TABLE_PROFILES + " ORDER BY " + KEY_PORDER;

		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		
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
		//db.close();
		
		// return profile list
		return -1;
		
	}
*/	
	public void setPOrder(List<Profile> list)
	{
		//SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		
		ContentValues values = new ContentValues();

		db.beginTransaction();
		try {

			for (int i = 0; i < list.size(); i++)
			{
				Profile profile = list.get(i);
				profile._porder = i+1;
						
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
		
        //db.close();
	}
	
	public void setChecked(List<Profile> list)
	{
		//SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		
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
		
        //db.close();
	}

	public int updateForHardware(Context context)
	{
		int ret = 0;
		
		final String selectQuery = "SELECT " + KEY_ID + "," +
										KEY_DEVICE_AIRPLANE_MODE + "," +
										KEY_DEVICE_WIFI + "," +
										KEY_DEVICE_BLUETOOTH + "," +
										KEY_DEVICE_MOBILE_DATA + "," +
										KEY_DEVICE_MOBILE_DATA_PREFS + "," +
										KEY_DEVICE_GPS + 
							" FROM " + TABLE_PROFILES;

		//SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		
		ContentValues values = new ContentValues();

		Cursor cursor = db.rawQuery(selectQuery, null);
		
		db.beginTransaction();
		try {

			if (cursor.moveToFirst()) {
				do {
						if ((Integer.parseInt(cursor.getString(1)) != 0) &&	
							(!GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_AIRPLANE_MODE, context)))
						{
							values.put(KEY_DEVICE_AIRPLANE_MODE, 0);
							db.update(TABLE_PROFILES, values, KEY_ID + " = ?",
							   new String[] { String.valueOf(Integer.parseInt(cursor.getString(0))) });							
						}
							
						if ((Integer.parseInt(cursor.getString(2)) != 0) &&
							(!GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_WIFI, context)))
						{
							values.put(KEY_DEVICE_WIFI, 0);
							db.update(TABLE_PROFILES, values, KEY_ID + " = ?",
							   new String[] { String.valueOf(Integer.parseInt(cursor.getString(0))) });							
						}
						
						if ((Integer.parseInt(cursor.getString(3)) != 0) &&
							(!GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_BLUETOOTH, context)))
						{
							values.put(KEY_DEVICE_BLUETOOTH, 0);
							db.update(TABLE_PROFILES, values, KEY_ID + " = ?",
							   new String[] { String.valueOf(Integer.parseInt(cursor.getString(0))) });							
						}
						
						if ((Integer.parseInt(cursor.getString(4)) != 0) &&
							(!GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_MOBILE_DATA, context)))
						{
							values.put(KEY_DEVICE_MOBILE_DATA, 0);
							db.update(TABLE_PROFILES, values, KEY_ID + " = ?",
							   new String[] { String.valueOf(Integer.parseInt(cursor.getString(0))) });							
						}

						if ((Integer.parseInt(cursor.getString(5)) != 0) &&
							(!GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_MOBILE_DATA_PREFS, context)))
						{
							values.put(KEY_DEVICE_MOBILE_DATA_PREFS, 0);
							db.update(TABLE_PROFILES, values, KEY_ID + " = ?",
							   new String[] { String.valueOf(Integer.parseInt(cursor.getString(0))) });							
						}
						
						if ((Integer.parseInt(cursor.getString(6)) != 0) &&
							(!GlobalData.hardwareCheck(GlobalData.PREF_PROFILE_DEVICE_GPS, context)))
						{
							values.put(KEY_DEVICE_GPS, 0);
							db.update(TABLE_PROFILES, values, KEY_ID + " = ?",
							   new String[] { String.valueOf(Integer.parseInt(cursor.getString(0))) });							
						}

				} while (cursor.moveToNext());
			}

			cursor.close();

			db.setTransactionSuccessful();

			ret = 1;
	   } catch (Exception e){
	        //Error in between database transaction
		   ret = 0;
	   } finally {
		   db.endTransaction();
       }	
		
       //db.close();
		
       return ret;
	}
	
// EVENTS --------------------------------------------------------------------------------
	
	// Adding new event
	void addEvent(Event event) {
	
		//SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_E_NAME, event._name); // Event Name
		values.put(KEY_E_TYPE, event._type); // Event type
		values.put(KEY_E_FK_PROFILE, event._fkProfile); // profile
		values.put(KEY_E_STATUS, event._status); // event status
		
		db.beginTransaction();
		
		try {
			// Inserting Row
			event._id = db.insert(TABLE_EVENTS, null, values);
			updateEventPreferences(event, db);
			
			db.setTransactionSuccessful();

		} catch (Exception e){
			//Error in between database transaction
		} finally {
			db.endTransaction();
		}	

		//db.close(); // Closing database connection
	}

	// Getting single event
	Event getEvent(long event_id) {
		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();

		Cursor cursor = db.query(TABLE_EVENTS, 
				                 new String[] { KEY_E_ID, 
												KEY_E_NAME, 
												KEY_E_TYPE, 
												KEY_E_FK_PROFILE, 
												KEY_E_STATUS
												}, 
				                 KEY_ID + "=?",
				                 new String[] { String.valueOf(event_id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Event event = null;
		
		if (cursor.getCount() > 0)
		{
		
			event = new Event(Long.parseLong(cursor.getString(0)),
				                      cursor.getString(1), 
				                      Integer.parseInt(cursor.getString(2)),
				                      Long.parseLong(cursor.getString(3)),
				                      Integer.parseInt(cursor.getString(4))
				                      );
		}

		cursor.close();
		
		getEventPreferences(event, db);
		
		//db.close();

		// return profile
		return event;
	}
	
	// Getting All Events
	public List<Event> getAllEvents() {
		List<Event> eventList = new ArrayList<Event>();
		
        //Log.e("DatabaseHandler.getAllEvents","filterType="+filterType);
		
		// Select All Query
		final String selectQuery = "SELECT " + KEY_E_ID + "," +
				                         KEY_E_NAME + "," +
				                         KEY_E_TYPE + "," +
				                         KEY_E_FK_PROFILE + "," +
				                         KEY_E_STATUS +
		                     " FROM " + TABLE_EVENTS;

		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();

		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Event event = new Event();
				event._id = Long.parseLong(cursor.getString(0));
				event._name = cursor.getString(1);
				event._type = Integer.parseInt(cursor.getString(2));
				//Log.e("DatabaseHandler.getAllEvents","type="+event._type);
				event._fkProfile = Long.parseLong(cursor.getString(3));
				event._status = Integer.parseInt(cursor.getString(4));
				event.createEventPreferences();
				getEventPreferences(event, db);
				// Adding contact to list
				eventList.add(event);
			} while (cursor.moveToNext());
		}

		cursor.close();
		//db.close();
		
		// return evemt list
		return eventList;
	}

	// Updating single event
	public int updateEvent(Event event) {
		//SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_E_NAME, event._name);
		values.put(KEY_E_TYPE, event._type);
		values.put(KEY_E_FK_PROFILE, event._fkProfile);
		values.put(KEY_E_STATUS, event._status);

		int r = 0;
		
		db.beginTransaction();
		
		try {
			// updating row
			r = db.update(TABLE_EVENTS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(event._id) });
			updateEventPreferences(event, db);
		
			db.setTransactionSuccessful();

		} catch (Exception e){
			//Error in between database transaction
			Log.e("DatabaseHandler.updateEvent", e.toString());
			r = 0;
		} finally {
			db.endTransaction();
		}	
		
        //db.close();
        
		return r;
	}

	// Deleting single event
	public void deleteEvent(Event event) {
		//SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		db.delete(TABLE_EVENTS, KEY_ID + " = ?",
				new String[] { String.valueOf(event._id) });
		//db.close();
	}

	// Deleting all events
	public void deleteAllEvents() {
		//SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		db.delete(TABLE_EVENTS, null,	null);
		//db.close();
	}

	// Getting events Count
	public int getEventsCount() {
		final String countQuery = "SELECT  count(*) FROM " + TABLE_EVENTS;
		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();

		Cursor cursor = db.rawQuery(countQuery, null);
		
		int r;
		
		if (cursor != null)
		{
			cursor.moveToFirst();
			r = Integer.parseInt(cursor.getString(0));
		}
		else
			r = 0;

		cursor.close();
		//db.close();
		
		return r;	
	}
	
	public void unlinkEventsFromProfile(Profile profile)
	{
		SQLiteDatabase db = getMyWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_E_FK_PROFILE, 0);

		// updating row
		db.update(TABLE_EVENTS, values, KEY_E_FK_PROFILE + " = ?",
				new String[] { String.valueOf(profile._id) });
		
        //db.close();
	}
	
	public void unlinkAllEvents()
	{
		SQLiteDatabase db = getMyWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_E_FK_PROFILE, 0);

		// updating row
		db.update(TABLE_EVENTS, values, null, null);
		
        //db.close();
	}
/*	
	public Event getFirstEvent()
	{
		final String selectQuery = "SELECT " + KEY_E_ID + "," +
						                 KEY_E_NAME + "," +
						                 KEY_E_TYPE + "," +
						                 KEY_E_FK_PROFILE + "," +
						                 KEY_E_ENABLED + "," +
						                 KEY_E_STATUS +
						    " FROM " + TABLE_EVENTS;
						    

		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		
		Cursor cursor = db.rawQuery(selectQuery, null);

		Event event = null; 
		
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			event = new Event();
			event._id = Long.parseLong(cursor.getString(0));
			event._name = cursor.getString(1);
			event._type = Integer.parseInt(cursor.getString(2));
			event._fkProfile = Long.parseLong(cursor.getString(3));
			event.setEnabled((Integer.parseInt(cursor.getString(4)) == 1) ? true : false);
			event.setStatus(Integer.parseInt(cursor.getString(5)));
		}
		
		cursor.close();
		//db.close();
		
		// return profile list
		return event;
		
	}
*/	
/*
	public int getEventPosition(Event event)
	{
		String selectQuery = "SELECT " + KEY_E_ID +
							   " FROM " + TABLE_EVENTS;

		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		// looping through all rows and adding to list
		long lid;
		int position = 0;
		if (cursor.moveToFirst()) {
			do {
				lid = Long.parseLong(cursor.getString(0));
				if (lid == event._id)
					return position;
				position++;
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		//db.close();
		
		// return profile list
		return -1;
		
	}
*/	
	public void getEventPreferences(Event event) {
		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		getEventPreferences(event, db);
		//db.close();
	}

	public void getEventPreferences(Event event, SQLiteDatabase db) {
		switch (event._type)
        {
        case Event.ETYPE_TIME:
        	getEventPreferencesTime(event, db);
        	break;
        }
	}
	
	private void getEventPreferencesTime(Event event, SQLiteDatabase db) {
		Cursor cursor = db.query(TABLE_EVENTS, 
				                 new String[] { KEY_E_DAYS_OF_WEEK,
				         						KEY_E_START_TIME,
				         						KEY_E_END_TIME,
				         						KEY_E_USE_END_TIME
												}, 
				                 KEY_ID + "=?",
				                 new String[] { String.valueOf(event._id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		EventPreferencesTime eventPreferences = (EventPreferencesTime)event._eventPreferences;
		
		String daysOfWeek = cursor.getString(0);
		//Log.e("DatabaseHandler.getEventPreferencesTime","daysOfWeek="+daysOfWeek);

		if (daysOfWeek != null)
		{
		String[] splits = daysOfWeek.split("\\|");
		if (splits[0].equals(DaysOfWeekPreference.allValue))
		{
			eventPreferences._sunday = true;
			eventPreferences._monday = true;
			eventPreferences._tuesday = true;
			eventPreferences._wendesday = true;
			eventPreferences._thursday = true;
			eventPreferences._friday = true;
			eventPreferences._saturday = true;
		}
		else
		{
			eventPreferences._sunday = false;
			eventPreferences._monday = false;
			eventPreferences._tuesday = false;
			eventPreferences._wendesday = false;
			eventPreferences._thursday = false;
			eventPreferences._friday = false;
			eventPreferences._saturday = false;
			for (String value : splits)
			{
				eventPreferences._sunday = eventPreferences._sunday || value.equals("0");
				eventPreferences._monday = eventPreferences._monday || value.equals("1");
				eventPreferences._tuesday = eventPreferences._tuesday || value.equals("2");
				eventPreferences._wendesday = eventPreferences._wendesday || value.equals("3");
				eventPreferences._thursday = eventPreferences._thursday || value.equals("4");
				eventPreferences._friday = eventPreferences._friday || value.equals("5");
				eventPreferences._saturday = eventPreferences._saturday || value.equals("6");
			}
		}
		}
		eventPreferences._startTime = Long.parseLong(cursor.getString(1));
		eventPreferences._endTime = Long.parseLong(cursor.getString(2));
		eventPreferences._useEndTime = (Integer.parseInt(cursor.getString(3)) == 1) ? true : false;
		
		cursor.close();
	}
	
	public int updateEventPreferences(Event event) {
		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		int r = updateEventPreferences(event, db);
		//db.close();
		return r;
	}

	public int updateEventPreferences(Event event, SQLiteDatabase db) {
		int r;
		//Log.e("DatabaseHandler.updateEventPreferences","type="+event._type);
		
		switch (event._type)
        {
        case Event.ETYPE_TIME:
        	r = updateEventPreferencesTime(event, db);
        	break;
        default:
        	r = 0;
        }
		return r;
	}
	
	private int updateEventPreferencesTime(Event event, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		
		EventPreferencesTime eventPreferences = (EventPreferencesTime)event._eventPreferences; 

		//Log.e("DatabaseHandler.updateEventPreferencesTime","type="+event._type);
		
    	String daysOfWeek = "";
    	if (eventPreferences._sunday) daysOfWeek = daysOfWeek + "0|";
    	if (eventPreferences._monday) daysOfWeek = daysOfWeek + "1|";
    	if (eventPreferences._tuesday) daysOfWeek = daysOfWeek + "2|";
    	if (eventPreferences._wendesday) daysOfWeek = daysOfWeek + "3|";
    	if (eventPreferences._thursday) daysOfWeek = daysOfWeek + "4|";
    	if (eventPreferences._friday) daysOfWeek = daysOfWeek + "5|";
    	if (eventPreferences._saturday) daysOfWeek = daysOfWeek + "6|";

		//Log.e("DatabaseHandler.updateEventPreferencesTime","daysOfWeek="+daysOfWeek);
    	
		values.put(KEY_E_TYPE, event._type);
		values.put(KEY_E_DAYS_OF_WEEK, daysOfWeek);
		values.put(KEY_E_START_TIME, eventPreferences._startTime);
		values.put(KEY_E_END_TIME, eventPreferences._endTime);
		values.put(KEY_E_USE_END_TIME, (eventPreferences._useEndTime) ? 1 : 0);

		// updating row
		int r = db.update(TABLE_EVENTS, values, KEY_ID + " = ?",
				        new String[] { String.valueOf(event._id) });
        
		return r;
	}
	
// EVENT TIMELINE ------------------------------------------------------------------
	
	// Adding new event
	void addEventTimeline(EventTimeline eventTimeline) {
		
		//SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_ET_FK_EVENT, eventTimeline._fkEvent); // Event id
		values.put(KEY_ET_FK_PROFILE_RETURN, eventTimeline._fkProfileReturn); // Profile id returned on pause/stop event
		values.put(KEY_ET_EORDER, getMaxEOrderET()+1); // event running order 
		
		db.beginTransaction();
		
		try {
			// Inserting Row
			eventTimeline._id = db.insert(TABLE_EVENT_TIMELINE, null, values);
			
			db.setTransactionSuccessful();

		} catch (Exception e){
			//Error in between database transaction
		} finally {
			db.endTransaction();
		}	

		//db.close(); // Closing database connection
	}
	
	// Getting max(eorder)
	public int getMaxEOrderET() {
		String countQuery = "SELECT MAX("+KEY_ET_EORDER+") FROM " + TABLE_EVENT_TIMELINE;
		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();

		Cursor cursor = db.rawQuery(countQuery, null);

		int r;
		
		if (cursor.getCount() == 0)
		{
			//Log.e("DatabaseHandler.getMaxPOrder","count=0");
			r = 0;
		}
		else
		{	
			if (cursor.moveToFirst())
			{
				r = cursor.getInt(0);
				//Log.e("DatabaseHandler.getMaxPOrder","porder="+r);
			}
			else
			{
				r = 0;
				//Log.e("DatabaseHandler.getMaxPOrder","moveToFirst=false");
			}
		}

		cursor.close();
		//db.close();
		
		return r;
		
	}

	// Getting all event timeline 
	public List<EventTimeline> getAllEventTimelines() {
		List<EventTimeline> eventTimelineList = new ArrayList<EventTimeline>();
		
        //Log.e("DatabaseHandler.getAllEvents","filterType="+filterType);
		
		// Select All Query
		final String selectQuery = "SELECT " + KEY_ET_ID + "," +
					                           KEY_ET_FK_EVENT + "," +
					                           KEY_ET_FK_PROFILE_RETURN + "," +
					                           KEY_ET_EORDER +
				                   " FROM " + TABLE_EVENT_TIMELINE +
				                   " ORDER BY " + KEY_ET_EORDER;

		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();

		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				EventTimeline eventTimeline = new EventTimeline();
				
				eventTimeline._id = Long.parseLong(cursor.getString(0));
				eventTimeline._fkEvent = Long.parseLong(cursor.getString(1));
				eventTimeline._fkProfileReturn = Long.parseLong(cursor.getString(2));
				eventTimeline._eorder = Integer.parseInt(cursor.getString(3));
				
				// Adding event timeline to list
				eventTimelineList.add(eventTimeline);
			} while (cursor.moveToNext());
		}

		cursor.close();
		//db.close();
		
		// return event timeline list
		return eventTimelineList;
	}

	// Deleting event timeline
	public void deleteEventTimeline(EventTimeline eventTimeline) {
		
		//SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		db.delete(TABLE_EVENT_TIMELINE, KEY_ET_ID + " = ?",
				new String[] { String.valueOf(eventTimeline._id) });
		//db.close();
	}

	// Deleting all events from timeline
	public void deleteAllEventTimelines() {
		//SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();
		db.delete(TABLE_EVENT_TIMELINE, null,	null);
		//db.close();
	}

	// Getting Count in timelines
	public int getEventTimelineCount() {
		final String countQuery = "SELECT  count(*) FROM " + TABLE_EVENT_TIMELINE;
		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();

		Cursor cursor = db.rawQuery(countQuery, null);
		
		int r;
		
		if (cursor != null)
		{
			cursor.moveToFirst();
			r = Integer.parseInt(cursor.getString(0));
		}
		else
			r = 0;

		cursor.close();
		//db.close();
		
		return r;	
	}
	
	public void updateProfileReturnET(List<EventTimeline> eventTimelineList)
	{
		//SQLiteDatabase db = this.getWritableDatabase();
		SQLiteDatabase db = getMyWritableDatabase();

		ContentValues values = new ContentValues();

		db.beginTransaction();
		
		try {
			
			for (EventTimeline eventTimeline : eventTimelineList)
			{
				values.put(KEY_ET_FK_PROFILE_RETURN, eventTimeline._fkProfileReturn);

				// updating row
				db.update(TABLE_EVENT_TIMELINE, values, KEY_ID + " = ?",
					new String[] { String.valueOf(eventTimeline._id) });
			}
		
			db.setTransactionSuccessful();

		} catch (Exception e){
			//Error in between database transaction
			Log.e("DatabaseHandler.updateProfileReturnET", e.toString());
		} finally {
			db.endTransaction();
		}	
		
        //db.close();
        
	}
	
	
// OTHERS -------------------------------------------------------------------------
	
	//@SuppressWarnings("resource")
	public int importDB()
	{
		int ret = 0;
		List<Long> exportedDBEventProfileIds = new ArrayList<Long>();
		List<Long> importDBEventProfileIds = new ArrayList<Long>();
		long profileId;
		
		
		
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
			//	if (exportedDBObj.getVersion() <= DATABASE_VERSION)
			//	{	
					
					// db z SQLiteOpenHelper
					//SQLiteDatabase db = this.getWritableDatabase();
					SQLiteDatabase db = getMyWritableDatabase();

					Cursor cursorExportedDB = null;
					String[] columnNamesExportedDB;
					Cursor cursorImportDB = null;
					ContentValues values = new ContentValues();
					
					try {
						db.beginTransaction();
						
						db.execSQL("DELETE FROM " + TABLE_PROFILES);

						// cursor for profiles exportedDB
						cursorExportedDB = exportedDBObj.rawQuery("SELECT * FROM "+TABLE_PROFILES, null);
						columnNamesExportedDB = cursorExportedDB.getColumnNames();

						// cursor for profiles of destination db  
						cursorImportDB = db.rawQuery("SELECT * FROM "+TABLE_PROFILES, null);
						
						if (cursorExportedDB.moveToFirst()) {
							do {
									values.clear();
									for (int i = 0; i < columnNamesExportedDB.length; i++)
									{
										// put only when columnNamesExportedDB[i] exists in cursorImportDB
										if (cursorImportDB.getColumnIndex(columnNamesExportedDB[i]) != -1)
											values.put(columnNamesExportedDB[i], cursorExportedDB.getString(i));
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
									if (exportedDBObj.getVersion() < 24)
									{
										values.put(KEY_DEVICE_AUTOSYNC, 0);
									}
									if (exportedDBObj.getVersion() < 25)
									{
										values.put(KEY_SHOW_IN_ACTIVATOR, 1);
									}
									if (exportedDBObj.getVersion() < 31)
									{
										values.put(KEY_DEVICE_AUTOSYNC, 0);
									}
									
									// Inserting Row do db z SQLiteOpenHelper
									profileId = db.insert(TABLE_PROFILES, null, values);
									// save profile ids
									exportedDBEventProfileIds.add(cursorExportedDB.getLong(cursorExportedDB.getColumnIndex(KEY_ID)));
									importDBEventProfileIds.add(profileId);
									
							} while (cursorExportedDB.moveToNext());
						}
						cursorExportedDB.close();
						cursorImportDB.close();

						if (exportedDBObj.getVersion() >= DATABASE_VERSION_EVENTS)
						{
							db.execSQL("DELETE FROM " + TABLE_EVENTS);
						
							// cusor for events exportedDB
							cursorExportedDB = exportedDBObj.rawQuery("SELECT * FROM "+TABLE_EVENTS, null);
							columnNamesExportedDB = cursorExportedDB.getColumnNames();
							
							// cursor for profiles of destination db  
							cursorImportDB = db.rawQuery("SELECT * FROM "+TABLE_EVENTS, null);
							
							if (cursorExportedDB.moveToFirst()) {
								do {
										values.clear();
										for (int i = 0; i < columnNamesExportedDB.length; i++)
										{
											// put only when columnNamesExportedDB[i] exists in cursorImportDB
											if (cursorImportDB.getColumnIndex(columnNamesExportedDB[i]) != -1)
											{
												if (columnNamesExportedDB[i].equals(KEY_E_FK_PROFILE))
												{
													// importnuty profil ma nove id
													// ale mame mapovacie polia, z ktorych vieme
													// ktore povodne id za zmenilo na ktore nove
													int profileIdx = exportedDBEventProfileIds.indexOf(cursorExportedDB.getLong(i));
													values.put(columnNamesExportedDB[i], importDBEventProfileIds.get(profileIdx));
												}
												else
													values.put(columnNamesExportedDB[i], cursorExportedDB.getString(i));
											}
											//Log.d("DatabaseHandler.importDB", "cn="+columnNames[i]+" val="+cursor.getString(i));
										}
										
										// for non existent fields set default value
										if (exportedDBObj.getVersion() < 30)
										{
											values.put(KEY_E_USE_END_TIME, 0);
										}
										if (exportedDBObj.getVersion() < 32)
										{
											values.put(KEY_E_STATUS, 0);
										}

										// Inserting Row do db z SQLiteOpenHelper
										db.insert(TABLE_EVENTS, null, values);
										
								} while (cursorExportedDB.moveToNext());
							}
							cursorExportedDB.close();
							cursorImportDB.close();
						}
						
						db.setTransactionSuccessful();
						
						ret = 1;
					}
					finally {
						db.endTransaction();
						if ((cursorExportedDB != null) && (!cursorExportedDB.isClosed()))
							cursorExportedDB.close();
						if ((cursorImportDB != null) && (!cursorImportDB.isClosed()))
							cursorImportDB.close();
						//db.close();
					}
					
					//FileChannel src = new FileInputStream(exportedDB).getChannel();
					//FileChannel dst = new FileOutputStream(dataDB).getChannel();
					//dst.transferFrom(src, 0, src.size());
					//src.close();
					//dst.close();
					
					// Access the copied database so SQLiteHelper will cache it and mark
					// it as created
					//getWritableDatabase().close();
			//	}
			//	else
			//	{
			//		Log.w("DatabaseHandler.importDB", "wrong exported db version");
			//	}
			}
		} catch (Exception e) {
			Log.e("DatabaseHandler.importDB", e.toString());
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
				// close db
				close();
				
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
			Log.e("DatabaseHandler.exportDB", e.toString());
		}

		return ret;
	}
	
}
