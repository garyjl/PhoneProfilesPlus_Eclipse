package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Profile {
	
	public long _id;
	public String _name;
	public String _icon;
	public boolean _checked;
	public int _porder;
	public int _volumeRingerMode;
	public String _volumeRingtone;
	public String _volumeNotification;
	public String _volumeMedia;
	public String _volumeAlarm;
	public String _volumeSystem;
	public String _volumeVoice;
	public boolean _soundRingtoneChange;
	public String _soundRingtone;
	public boolean _soundNotificationChange;
	public String _soundNotification;
	public boolean _soundAlarmChange;
	public String _soundAlarm;
	public int _deviceAirplaneMode;
	public int _deviceMobileData;
	public boolean _deviceMobileDataPrefs;
	public int _deviceWiFi;
	public int _deviceBluetooth;
	public int _deviceGPS;
	public int _deviceScreenTimeout;
	public String _deviceBrightness;
	public boolean _deviceWallpaperChange;
	public String _deviceWallpaper;
	public boolean _deviceRunApplicationChange;
	public String _deviceRunApplicationPackageName;
	public int _deviceAutosync;
	public boolean _showInActivator;
	
	public Bitmap _iconBitmap;
	public Bitmap _preferencesIndicator;
	
	
	// Empty constructorn
	public Profile(){
		
	}
	
	// constructor
	public Profile(long id, 
			       String name, 
			       String icon, 
			       Boolean checked, 
			       int porder,
 			   	   int volumeRingerMode,
			   	   String volumeRingtone,
			   	   String volumeNotification,
			   	   String volumeMedia,
			   	   String volumeAlarm,
			   	   String volumeSystem,
			   	   String volumeVoice,
			   	   boolean soundRingtoneChange,
			   	   String soundRingtone,
			   	   boolean soundNotificationChange,
			   	   String soundNotification,
			   	   boolean soundAlarmChange,
			   	   String soundAlarm,
			   	   int deviceAirplaneMode,
			   	   int deviceWiFi,
			   	   int deviceBluetooth,
			   	   int deviceScreenTimeout,
			   	   String deviceBrightness,
			   	   boolean deviceWallpaperChange,
			   	   String deviceWallpaper,
			   	   int deviceMobileData,
			   	   boolean deviceMobileDataPrefs,
			   	   int deviceGPS,
			   	   boolean deviceRunApplicationChange,
			   	   String deviceRunApplicationPackageName,
			   	   int deviceAutosync,
			   	   boolean showInActivator)
	{
		this._id = id;
		this._name = name;
		this._icon = icon;
		this._checked = checked; 
		this._porder = porder;
		this._volumeRingerMode = volumeRingerMode;
		this._volumeRingtone = volumeRingtone;
		this._volumeNotification = volumeNotification;
		this._volumeMedia = volumeMedia;
		this._volumeAlarm = volumeAlarm;
		this._volumeSystem = volumeSystem;
		this._volumeVoice = volumeVoice;
		this._soundRingtoneChange = soundRingtoneChange;
		this._soundRingtone = soundRingtone;
		this._soundNotificationChange = soundNotificationChange;
		this._soundNotification = soundNotification;
		this._soundAlarmChange = soundAlarmChange;
		this._soundAlarm = soundAlarm;
		this._deviceAirplaneMode = deviceAirplaneMode;
		this._deviceMobileData = deviceMobileData;
		this._deviceMobileDataPrefs = deviceMobileDataPrefs;
		this._deviceWiFi = deviceWiFi;
		this._deviceBluetooth = deviceBluetooth;
		this._deviceGPS = deviceGPS;
		this._deviceScreenTimeout = deviceScreenTimeout;
		this._deviceBrightness = deviceBrightness;
		this._deviceWallpaperChange = deviceWallpaperChange;
		this._deviceWallpaper = deviceWallpaper;
		this._deviceRunApplicationChange = deviceRunApplicationChange;
		this._deviceRunApplicationPackageName = deviceRunApplicationPackageName;
		this._deviceAutosync = deviceAutosync;
		this._showInActivator = showInActivator;
		
		this._iconBitmap = null;
		this._preferencesIndicator = null;
	}
	
	// constructor
	public Profile(String name, 
			       String icon, 
			       Boolean checked, 
			       int porder,
 			   	   int volumeRingerMode,
			   	   String volumeRingtone,
			   	   String volumeNotification,
			   	   String volumeMedia,
			   	   String volumeAlarm,
			   	   String volumeSystem,
			   	   String volumeVoice,
			   	   boolean soundRingtoneChange,
			   	   String soundRingtone,
			   	   boolean soundNotificationChange,
			   	   String soundNotification,
			   	   boolean soundAlarmChange,
			   	   String soundAlarm,
			   	   int deviceAirplaneMode,
			   	   int deviceWiFi,
			   	   int deviceBluetooth,
			   	   int deviceScreenTimeout,
			   	   String deviceBrightness,
			   	   boolean deviceWallpaperChange,
			   	   String deviceWallpaper,
			   	   int deviceMobileData,
			   	   boolean deviceMobileDataPrefs,
			   	   int deviceGPS,
			   	   boolean deviceRunApplicationChange,
			   	   String deviceRunApplicationPackageName,
			   	   int deviceAutosync,
			   	   boolean showInActivator)
	{
		this._name = name;
		this._icon = icon;
		this._checked = checked; 
		this._porder = porder;
		this._volumeRingerMode = volumeRingerMode;
		this._volumeRingtone = volumeRingtone;
		this._volumeNotification = volumeNotification;
		this._volumeMedia = volumeMedia;
		this._volumeAlarm = volumeAlarm;
		this._volumeSystem = volumeSystem;
		this._volumeVoice = volumeVoice;
		this._soundRingtoneChange = soundRingtoneChange;
		this._soundRingtone = soundRingtone;
		this._soundNotificationChange = soundNotificationChange;
		this._soundNotification = soundNotification;
		this._soundAlarmChange = soundAlarmChange;
		this._soundAlarm = soundAlarm;
		this._deviceAirplaneMode = deviceAirplaneMode;
		this._deviceMobileData = deviceMobileData;
		this._deviceMobileDataPrefs = deviceMobileDataPrefs;
		this._deviceWiFi = deviceWiFi;
		this._deviceBluetooth = deviceBluetooth;
		this._deviceGPS = deviceGPS;
		this._deviceScreenTimeout = deviceScreenTimeout;
		this._deviceBrightness = deviceBrightness;
		this._deviceWallpaperChange = deviceWallpaperChange;
		this._deviceWallpaper = deviceWallpaper;
		this._deviceRunApplicationChange = deviceRunApplicationChange;
		this._deviceRunApplicationPackageName = deviceRunApplicationPackageName;
		this._deviceAutosync = deviceAutosync;
		this._showInActivator = showInActivator;
		
		this._iconBitmap = null;
		this._preferencesIndicator = null;
	}
	
	// getting icon identifier
	public String getIconIdentifier()
	{
		String value;
		try {
			String[] splits = _icon.split("\\|");
			value = splits[0];
		} catch (Exception e) {
			value = "ic_profile_default";
		}
		return value;
	}
	
	// getting where icon is resource id
	public boolean getIsIconResourceID()
	{
		boolean value;
		try {
			String[] splits = _icon.split("\\|");
			value = (splits[1].equals("1")) ? true : false;

		} catch (Exception e) {
			value = true;
		}
		return value;
	}
	
	public int getVolumeRingtoneValue()
	{
		int value;
		try {
			String[] splits = _volumeRingtone.split("\\|");
			value = Integer.parseInt(splits[0]);
		} catch (Exception e) {
			value = 0;
		}
		return value;
	}

	public boolean getVolumeRingtoneChange()
	{
		int value;
		try {
			String[] splits = _volumeRingtone.split("\\|");
			value = Integer.parseInt(splits[1]);
		} catch (Exception e) {
			value = 1;
		}
		return (value == 0) ? true : false;
	}
	
	public int getVolumeNotificationValue()
	{
		int value;
		try {
			String[] splits = _volumeNotification.split("\\|");
			value = Integer.parseInt(splits[0]);
		} catch (Exception e) {
			value = 0;
		}
		return value;
	}
	
	public boolean getVolumeNotificationChange()
	{
		int value;
		try {
			String[] splits = _volumeNotification.split("\\|");
			value = Integer.parseInt(splits[1]);
		} catch (Exception e) {
			value = 1;
		}
		return (value == 0) ? true : false;
	}
	
	public int getVolumeMediaValue()
	{
		int value;
		try {
			String[] splits = _volumeMedia.split("\\|");
			value = Integer.parseInt(splits[0]);
		} catch (Exception e) {
			value = 0;
		}
		return value;
	}
	
	public boolean getVolumeMediaChange()
	{
		int value;
		try {
			String[] splits = _volumeMedia.split("\\|");
			value = Integer.parseInt(splits[1]);
		} catch (Exception e) {
			value = 1;
		}
		return (value == 0) ? true : false;
	}
	
	public int getVolumeAlarmValue()
	{
		int value;
		try {
			String[] splits = _volumeAlarm.split("\\|");
			value = Integer.parseInt(splits[0]);
		} catch (Exception e) {
			value = 0;
		}
		return value;
	}
	
	public boolean getVolumeAlarmChange()
	{
		int value;
		try {
			String[] splits = _volumeAlarm.split("\\|");
			value = Integer.parseInt(splits[1]);
		} catch (Exception e) {
			value = 1;
		}
		return (value == 0) ? true : false;
	}
	
	public int getVolumeSystemValue()
	{
		int value;
		try {
			String[] splits = _volumeSystem.split("\\|");
			value = Integer.parseInt(splits[0]);
		} catch (Exception e) {
			value = 0;
		}
		return value;
	}
	
	public boolean getVolumeSystemChange()
	{
		int value;
		try {
			String[] splits = _volumeSystem.split("\\|");
			value = Integer.parseInt(splits[1]);
		} catch (Exception e) {
			value = 1;
		}
		return (value == 0) ? true : false;
	}
	
	public int getVolumeVoiceValue()
	{
		int value;
		try {
			String[] splits = _volumeVoice.split("\\|");
			value = Integer.parseInt(splits[0]);
		} catch (Exception e) {
			value = 0;
		}
		return value;
	}
	
	public boolean getVolumeVoiceChange()
	{
		int value;
		try {
			String[] splits = _volumeVoice.split("\\|");
			value = Integer.parseInt(splits[1]);
		} catch (Exception e) {
			value = 1;
		}
		return (value == 0) ? true : false;
	}
	
	public int getDeviceBrightnessValue()
	{
		int value;
		try {
			String[] splits = _deviceBrightness.split("\\|");
			value = Integer.parseInt(splits[0]);
		} catch (Exception e) {
			value = 0;
		}
		return value;
	}

	public boolean getDeviceBrightnessChange()
	{
		int value;
		try {
			String[] splits = _deviceBrightness.split("\\|");
			value = Integer.parseInt(splits[1]);
		} catch (Exception e) {
			value = 1;
		}
		return (value == 0) ? true : false;
	}

	public boolean getDeviceBrightnessAutomatic()
	{
		int value;
		try {
			String[] splits = _deviceBrightness.split("\\|");
			value = Integer.parseInt(splits[2]);
		} catch (Exception e) {
			value = 1;
		}
		return (value == 1) ? true : false;
	}
	
	// getting wallpaper identifikator
	public String getDeviceWallpaperIdentifier()
	{
		String value;
		try {
			String[] splits = _deviceWallpaper.split("\\|");
			value = splits[0];
		} catch (Exception e) {
			value = "-";
		}
		return value;
	}
	
	
	//----------------------------------
	
	public void generateIconBitmap(Context context, boolean monochrome, int monochromeValue)
	{
        if (!getIsIconResourceID())
        {
        	if (_iconBitmap != null)
        		_iconBitmap.recycle();
        	
        	Resources resources = context.getResources();
    		int height = (int) resources.getDimension(android.R.dimen.app_icon_size);
    		int width = (int) resources.getDimension(android.R.dimen.app_icon_size);
    		_iconBitmap = BitmapManipulator.resampleBitmap(getIconIdentifier(), width, height);

    		if (monochrome)
    			_iconBitmap = BitmapManipulator.grayscaleBitmap(_iconBitmap);
        }
        else
        if (monochrome)
        {
        	int iconResource = context.getResources().getIdentifier(getIconIdentifier(), "drawable", context.getPackageName());
        	Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), iconResource);
        	_iconBitmap = BitmapManipulator.monochromeBitmap(bitmap, monochromeValue, context);
        	// getIsIconResourceID must return false
        	_icon = getIconIdentifier() + "|0";
        }
        else
        	_iconBitmap = null;
	}
	
	public void generatePreferencesIndicator(Context context, boolean monochrome, int monochromeValue)
	{
    	if (_preferencesIndicator != null)
    		_preferencesIndicator.recycle();

    	_preferencesIndicator = ProfilePreferencesIndicator.paint(this, context);

    	if (monochrome)
    		_preferencesIndicator = BitmapManipulator.monochromeBitmap(_preferencesIndicator, monochromeValue, context);

	}
	
}
