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

import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CompoundButton;

public class ContactsMultiSelectDialogPreference extends DialogPreference
{

	Context _context = null;
	String value = "";
	
	// Layout widgets.
	private ListView listView = null;

	public ContactsMultiSelectDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		_context = context;

	}

	protected View onCreateDialogView() {
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());

		View view = layoutInflater.inflate(
			R.layout.activity_contacts_multiselect_pref_dialog, null);
		
		listView = (ListView)view.findViewById(R.id.contactsListView);
		
		//TODO a tu vytiahni kontakty, sprav adapter a nahod ho do listView
		// pravdepodobne asynctask

		getValueCMSDP();
		
		return view;
	}
	
	public void onClick(DialogInterface dialog, int which) {
		// if the positive button is clicked, we persist the value.
		if (which == DialogInterface.BUTTON_POSITIVE) {
			if (shouldPersist()) {
				//TODO persistString(...); sem narvi stringy kontatkov oddelenych |
				setSummaryCMSDP();
			}
		}

		super.onClick(dialog, which);
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
	{
		if (restoreValue) {
			// restore state
			getValueCMSDP();
		}
		else {
			// set state
			//TODO persistString(...);  -- sem narvi default string kontaktov oddeleny | 
		}
		setSummaryCMSDP();
	}
	
	private void getValueCMSDP()
	{
		// Get the persistent value and correct it for the minimum value.
		value = getPersistedString(value);
	}
	
	private void setSummaryCMSDP()
	{
		String prefVolumeDataSummary = "";
		//TODO tu vytvor string pre summary 
		setSummary(prefVolumeDataSummary);
	}
	
}
