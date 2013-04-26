package sk.henrichg.phoneprofiles;

public class Profile {
	
	//private variables
	long _id;
	String _name;
	String _icon;
	boolean _checked;
	int _porder;
	int _volumeRingerMode;
	String _volumeRingtone;
	String _volumeNotification;
	String _volumeMedia;
	String _volumeAlarm;
	String _volumeSystem;
	String _volumeVoice;
	boolean _soundRingtoneChange;
	String _soundRingtone;
	boolean _soundNotificationChange;
	String _soundNotification;
	boolean _soundAlarmChange;
	String _soundAlarm;
	int _deviceAirplaneMode;
	int _deviceMobileData;
	int _deviceWiFi;
	int _deviceBluetooth;
	int _deviceScreenTimeout;
	String _deviceBrightness;
	boolean _deviceWallpaperChange;
	String _deviceWallpaper;
	
	
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
			   	   int deviceMobileData)
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
		this._deviceWiFi = deviceWiFi;
		this._deviceBluetooth = deviceBluetooth;
		this._deviceScreenTimeout = deviceScreenTimeout;
		this._deviceBrightness = deviceBrightness;
		this._deviceWallpaperChange = deviceWallpaperChange;
		this._deviceWallpaper = deviceWallpaper;
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
			   	   int deviceMobileData)
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
		this._deviceWiFi = deviceWiFi;
		this._deviceBluetooth = deviceBluetooth;
		this._deviceScreenTimeout = deviceScreenTimeout;
		this._deviceBrightness = deviceBrightness;
		this._deviceWallpaperChange = deviceWallpaperChange;
		this._deviceWallpaper = deviceWallpaper;
	}
	
	// getting ID
	public long getID()
	{
		return this._id;
	}
	
	// getting name
	public String getName()
	{
		return this._name;
	}
	
	// getting icon
	public String getIcon()
	{
		return this._icon;
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
	
	// getting checked
	public Boolean getChecked()
	{
		return this._checked;
	}
	
	// getting porder
	public int getPOrder()
	{
		return this._porder;
	}
	
	public int getVolumeRingerMode()
	{
		return _volumeRingerMode;
	}
	
	public String getVolumeRingtone()
	{
		return _volumeRingtone;
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
	
	public String getVolumeNotification()
	{
		return _volumeNotification;
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
	
	public String getVolumeMedia()
	{
		return _volumeMedia;
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
	
	public String getVolumeAlarm()
	{
		return _volumeAlarm;
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
	
	public String getVolumeSystem()
	{
		return _volumeSystem;
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
	
	public String getVolumeVoice()
	{
		return _volumeVoice;
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
	
	public boolean getSoundRingtoneChange()
	{
		return _soundRingtoneChange;
	}
	
	public String getSoundRingtone()
	{
		return _soundRingtone;
	}
	
	public boolean getSoundNotificationChange()
	{
		return _soundNotificationChange;
	}
	
	public String getSoundNotification()
	{
		return _soundNotification;
	}
	
	public boolean getSoundAlarmChange()
	{
		return _soundAlarmChange;
	}
	
	public String getSoundAlarm()
	{
		return _soundAlarm;
	}
	
	public int getDeviceAirplaneMode()
	{
		return _deviceAirplaneMode;
	}
	
	public int getDeviceMobileData()
	{
		return _deviceMobileData;
	}
	
	public int getDeviceWiFi()
	{
		return _deviceWiFi;
	}
	
	public int getDeviceBluetooth()
	{
		return _deviceBluetooth;
	}
	
	public int getDeviceScreenTimeout()
	{
		return _deviceScreenTimeout;
	}
	
	public String getDeviceBrightness()
	{
		return _deviceBrightness;
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
	
	public boolean getDeviceWallpaperChange()
	{
		return this._deviceWallpaperChange;
	}
	
	public String getDeviceWallpaper()
	{
		return this._deviceWallpaper;
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
	
	// setting id
	public void setID(long id)
	{
		this._id = id;
	}
	
	// setting name
	public void setName(String name)
	{
		this._name = name;
	}
	
	// setting icon
	public void setIcon(String icon)
	{
		this._icon = icon;
	}
	
	// setting checked
	public void setChecked(Boolean checked)
	{
		this._checked = checked;
	}

	// setting porder
	public void setPOrder(int porder)
	{
		this._porder = porder;
	}
	
	public void setVolumeRingerMode(int volumeRingerMode)
	{
		_volumeRingerMode = volumeRingerMode;
	}
	
	public void setVolumeRingtone(String volumeRingtone)
	{
		_volumeRingtone = volumeRingtone;
	}
	
	public void setVolumeNotification(String volumeNotification)
	{
		_volumeNotification = volumeNotification;
	}
	
	public void setVolumeMedia(String volumeMedia)
	{
		_volumeMedia = volumeMedia;
	}
	
	public void setVolumeAlarm(String volumeAlarm)
	{
		_volumeAlarm = volumeAlarm;
	}
	
	public void setVolumeSystem(String volumeSystem)
	{
		_volumeSystem = volumeSystem;
	}
	
	public void setVolumeVoice(String volumeVoice)
	{
		_volumeVoice = volumeVoice;
	}
	
	public void setSoundRingtoneChange(boolean soundRingtoneChange)
	{
		_soundRingtoneChange = soundRingtoneChange;
	}
	
	public void setSoundRingtone(String soundRingtone)
	{
		_soundRingtone = soundRingtone;
	}
	
	public void setSoundNotificationChange(boolean soundNotificationChange)
	{
		_soundNotificationChange = soundNotificationChange;
	}
	
	public void setSoundNotification(String soundNotification)
	{
		_soundNotification = soundNotification;
	}
	
	public void setSoundAlarmChange(boolean soundAlarmChange)
	{
		_soundAlarmChange = soundAlarmChange;
	}
	
	public void setSoundAlarm(String soundAlarm)
	{
		_soundAlarm = soundAlarm;
	}
	
	public void setDeviceAirplaneMode(int deviceAirplaneMode)
	{
		_deviceAirplaneMode = deviceAirplaneMode;
	}
	
	public void setDeviceMobileData(int deviceMobileData)
	{
		_deviceMobileData = deviceMobileData;
	}
	
	public void setDeviceWiFi(int deviceWiFi)
	{
		_deviceWiFi = deviceWiFi;
	}
	
	public void setDeviceBluetooth(int deviceBluetooth)
	{
		_deviceBluetooth = deviceBluetooth;
	}
	
	public void setDeviceScreenTimeout(int deviceScreenTimeout)
	{
		_deviceScreenTimeout = deviceScreenTimeout;
	}
	
	public void setDeviceBrightness(String deviceBrightness)
	{
		_deviceBrightness = deviceBrightness;
	}

	public void setDeviceWallpaperChange(boolean deviceWallpaperChange)
	{
		this._deviceWallpaperChange = deviceWallpaperChange;
	}
	
	public void setDeviceWallpaper(String deviceWallpaper)
	{
		_deviceWallpaper = deviceWallpaper;
	}

}
