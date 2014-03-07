/**
 * Copyright CMW Mobile.com, 2010.
 */
package sk.henrichg.phoneprofilesplus;

import android.content.Context;
import android.content.DialogInterface;

import android.content.res.TypedArray;

import android.preference.DialogPreference;
import android.provider.Settings.SettingNotFoundException;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CompoundButton;

/**
 * The SeekBarDialogPreference class is a DialogPreference based and provides a
 * seekbar preference.
 * @author Casper Wakkers
 */
public class BrightnessDialogPreference extends
		DialogPreference implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

	Context _context = null;
	
	// Layout widgets.
	private SeekBar seekBar = null;
	private TextView valueText = null;
	private CheckBox noChangeChBox = null; 
	private CheckBox automaticChBox = null; 
	private CheckBox defaultProfileChBox = null;

	// Custom xml attributes.
	private int noChange = 0;
	private int automatic = 0;
	private int defaultProfile = 0;
	private int disableDefaultProfile = 0;
	
	private int maximumValue = 255;
	private int minimumValue = 0;
	private int stepSize = 1;

	private String sValue = "";
	private int value = 0;

	Profile _defaultProfile;
	
	/**
	 * The SeekBarDialogPreference constructor.
	 * @param context of this preference.
	 * @param attrs custom xml attributes.
	 */
	public BrightnessDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		_context = context;

		TypedArray typedArray = context.obtainStyledAttributes(attrs,
			R.styleable.BrightnessDialogPreference);

		noChange = typedArray.getInteger(
			R.styleable.BrightnessDialogPreference_bNoChange, 1);
		automatic = typedArray.getInteger(
			R.styleable.BrightnessDialogPreference_bAutomatic, 1);
		defaultProfile = typedArray.getInteger(
				R.styleable.BrightnessDialogPreference_bDefaultProfile, 0);
		disableDefaultProfile = typedArray.getInteger(
				R.styleable.BrightnessDialogPreference_bDisableDefaultProfile, 0);

		typedArray.recycle();
		
		_defaultProfile = GlobalData.getDefaultProfile(_context);
		
	}
	/**
	 * {@inheritDoc}
	 */
	protected View onCreateDialogView() {
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());

		View view = layoutInflater.inflate(
			R.layout.activity_brightness_pref_dialog, null);

		seekBar = (SeekBar)view.findViewById(R.id.brightnessPrefDialogSeekbar);
		valueText = (TextView)view.findViewById(R.id.brightnessPrefDialogValueText);
		noChangeChBox = (CheckBox)view.findViewById(R.id.brightnessPrefDialogNoChange);
		automaticChBox = (CheckBox)view.findViewById(R.id.brightnessPrefDialogAutomatic);
		defaultProfileChBox = (CheckBox)view.findViewById(R.id.brightnessPrefDialogDefaultProfile);

		seekBar.setOnSeekBarChangeListener(this);
		seekBar.setKeyProgressIncrement(stepSize);
		seekBar.setMax(maximumValue - minimumValue);
		
		getValueBDP();
		
		seekBar.setProgress(value);
		
		noChangeChBox.setOnCheckedChangeListener(this);
		noChangeChBox.setChecked((noChange == 1));

		automaticChBox.setOnCheckedChangeListener(this);
		automaticChBox.setChecked((automatic == 1));
		
		defaultProfileChBox.setOnCheckedChangeListener(this);
		defaultProfileChBox.setChecked((defaultProfile == 1));
		defaultProfileChBox.setEnabled(disableDefaultProfile == 0);

		if (noChange == 1)
			defaultProfileChBox.setChecked(false);
		if (defaultProfile == 1)
			noChangeChBox.setChecked(false);
		
		valueText.setEnabled((noChange == 0) && (defaultProfile == 0) && (automatic == 0));
		seekBar.setEnabled((noChange == 0) && (defaultProfile == 0) && (automatic == 0));
		automaticChBox.setEnabled((noChange == 0) && (defaultProfile == 0));
		
		return view;
	}

	protected void onDialogClosed (boolean positiveResult)
	{
		Window win = ProfilePreferencesFragment.getPreferencesActivity().getWindow();
		WindowManager.LayoutParams layoutParams = win.getAttributes();
		layoutParams.screenBrightness = LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
		win.setAttributes(layoutParams);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void onProgressChanged(SeekBar seek, int newValue,
			boolean fromTouch) {
		// Round the value to the closest integer value.
		if (stepSize >= 1) {
			value = Math.round(newValue/stepSize)*stepSize;
		}
		else {
			value = newValue;
		}

		// Set the valueText text.
		valueText.setText(String.valueOf(value + minimumValue));
		
		Window win = ProfilePreferencesFragment.getPreferencesActivity().getWindow();
		WindowManager.LayoutParams layoutParams = win.getAttributes();
		layoutParams.screenBrightness = (float)(value + minimumValue) / maximumValue;
		win.setAttributes(layoutParams);

		callChangeListener(value);
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		//Log.d("SeekBarNoChangeDialogPreference.onCheckedChanged", Boolean.toString(isChecked));
		
		if (buttonView.getId() == R.id.brightnessPrefDialogNoChange)
		{
			noChange = (isChecked)? 1 : 0;

			valueText.setEnabled((noChange == 0) && (defaultProfile == 0) && (automatic == 0));
			seekBar.setEnabled((noChange == 0) && (defaultProfile == 0) && (automatic == 0));
			automaticChBox.setEnabled((noChange == 0) && (defaultProfile == 0));
			if (isChecked)
				defaultProfileChBox.setChecked(false);
		}

		if (buttonView.getId() == R.id.brightnessPrefDialogDefaultProfile)
		{
			defaultProfile = (isChecked)? 1 : 0;

			valueText.setEnabled((noChange == 0) && (defaultProfile == 0) && (automatic == 0));
			seekBar.setEnabled((noChange == 0) && (defaultProfile == 0) && (automatic == 0));
			automaticChBox.setEnabled((noChange == 0) && (defaultProfile == 0));
			if (isChecked)
				noChangeChBox.setChecked(false);
		}
		
		if (buttonView.getId() == R.id.brightnessPrefDialogAutomatic)
		{
			automatic = (isChecked)? 1 : 0;

			valueText.setEnabled((noChange == 0) && (defaultProfile == 0) && (automatic == 0));
			seekBar.setEnabled((noChange == 0) && (defaultProfile == 0) && (automatic == 0));
		}
		
		int _automatic = automatic;
		int _noChange = noChange;
		int _value = value;
		if (defaultProfile == 1)
		{
			_automatic = (_defaultProfile.getDeviceBrightnessAutomatic()) ? 1 : 0;
			_noChange = (_defaultProfile.getDeviceBrightnessChange()) ? 0 : 1;
			_value = _defaultProfile.getDeviceBrightnessValue();
		}
		
		if ((_automatic == 1) || (_noChange == 1)) 
		{
			Window win = ProfilePreferencesFragment.getPreferencesActivity().getWindow();
			WindowManager.LayoutParams layoutParams = win.getAttributes();
			layoutParams.screenBrightness = LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
			win.setAttributes(layoutParams);
		}
		else
		{
			Window win = ProfilePreferencesFragment.getPreferencesActivity().getWindow();
			WindowManager.LayoutParams layoutParams = win.getAttributes();
			layoutParams.screenBrightness = (float)(_value + minimumValue) / maximumValue;;
			win.setAttributes(layoutParams);
		}
		
		callChangeListener(noChange);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void onStartTrackingTouch(SeekBar seek) {
	}
	/**
	 * {@inheritDoc}
	 */
	public void onStopTrackingTouch(SeekBar seek) {
	}
	/**
	 * {@inheritDoc}
	 */
	public void onClick(DialogInterface dialog, int which) {
		// if the positive button is clicked, we persist the value.
		if (which == DialogInterface.BUTTON_POSITIVE) {
			if (shouldPersist()) {
				persistString(Integer.toString(value + minimumValue)
						+ "|" + Integer.toString(noChange)
						+ "|" + Integer.toString(automatic)
						+ "|" + Integer.toString(defaultProfile));
				setSummaryBDP();
			}
		}

		super.onClick(dialog, which);
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
	{
		if (restoreValue) {
			// restore state
			getValueBDP();
		}
		else {
			// set state
			value = 0;
			noChange = 1;
			automatic = 1;
			defaultProfile = 0;
			persistString(Integer.toString(value + minimumValue)
					+ "|" + Integer.toString(noChange)
					+ "|" + Integer.toString(automatic)
					+ "|" + Integer.toString(defaultProfile));
		}
		setSummaryBDP();
	}
	
	private void getValueBDP()
	{
		// Get the persistent value and correct it for the minimum value.
		sValue = getPersistedString(sValue);
		
		String[] splits = sValue.split("\\|");
		try {
			value = Integer.parseInt(splits[0]);
			if (value == -1)
			{
				try {
					value = android.provider.Settings.System.getInt(_context.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
				} catch (SettingNotFoundException e) {
					value = 0;
				}
			}	
		} catch (Exception e) {
			value = 0;
		}
		value = value - minimumValue;
		try {
			noChange = Integer.parseInt(splits[1]);
		} catch (Exception e) {
			noChange = 1;
		}
		try {
			automatic = Integer.parseInt(splits[2]);
		} catch (Exception e) {
			automatic = 1;
		}
		try {
			defaultProfile = Integer.parseInt(splits[3]);
		} catch (Exception e) {
			defaultProfile = 0;
		}
		
		//value = getPersistedInt(minimumValue) - minimumValue;

		// You're never know...
		if (value < 0) {
			value = 0;
		}
	}
	
	private void setSummaryBDP()
	{
		String prefVolumeDataSummary;
		if (noChange == 1)
			prefVolumeDataSummary = _context.getResources().getString(R.string.preference_profile_no_change);
		else
		if (defaultProfile == 1)
			prefVolumeDataSummary = _context.getResources().getString(R.string.preference_profile_default_profile);
		else
		if (automatic == 1)
			prefVolumeDataSummary = _context.getResources().getString(R.string.preference_profile_autobrightness);
		else
			prefVolumeDataSummary = String.valueOf(value + minimumValue) + " / " + String.valueOf(maximumValue);
		setSummary(prefVolumeDataSummary);
	}
	
}
