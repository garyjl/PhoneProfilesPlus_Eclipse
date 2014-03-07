package sk.henrichg.phoneprofilesplus;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;

import android.preference.DialogPreference;
import android.provider.ContactsContract.Contacts;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ListView;

public class ContactsMultiSelectDialogPreference extends DialogPreference
{

	Context _context = null;
	String value = "";
	
	// Layout widgets.
	private ListView listView = null;

	private Contact[] contact_read;
	private Cursor mCursor;
	private ContactsMultiselectPreferenceAdapter listAdapter;
	
	public ContactsMultiSelectDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		_context = context;

	}

	protected View onCreateDialogView() {
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());

		View view = layoutInflater.inflate(
			R.layout.activity_contacts_multiselect_pref_dialog, null);
		
		listView = (ListView)view.findViewById(R.id.contacts_multiselect_pref_dlg_listview);
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View item, int position, long id) 
            {
                Contact planet = listAdapter.getItem(position);
                planet.toggleChecked();
                ContactViewHolder viewHolder = (ContactViewHolder) item
                        .getTag();
                viewHolder.getCheckBox().setChecked(planet.isChecked());
            }
        });		
		

		String[] projection = new String[] { Contacts.HAS_PHONE_NUMBER, Contacts._ID, Contacts.DISPLAY_NAME };

		//TODO toto sa da len z aktivity, musis to prerobit - AsyncTask!!! :-D
		/*
	    mCursor = managedQuery(Contacts.CONTENT_URI, projection, 
	            					Contacts.HAS_PHONE_NUMBER + "=?", new String[] { "1" },
	            					Contacts.DISPLAY_NAME);
		*/
	    if (mCursor != null) {
	        mCursor.moveToFirst();
	        contact_read = new Contact[mCursor.getCount()];

	        // Add Contacts to the Array

	        int j = 0;
	        do {

	            contact_read[j] = new Contact(mCursor.getString(mCursor
	                    .getColumnIndex(Contacts.DISPLAY_NAME)));
	            j++;
	        } while (mCursor.moveToNext());

	    } else {
	        System.out.println("Cursor is NULL");
	    }

	    // Add Contact Class to the Arraylist

	    ArrayList<Contact> planetList = new ArrayList<Contact>();
	    planetList.addAll(Arrays.asList(contact_read));

	    // Set our custom array adapter as the ListView's adapter.
	    listAdapter = new ContactsMultiselectPreferenceAdapter(_context, planetList);
	    listView.setAdapter(listAdapter);		
	    
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
