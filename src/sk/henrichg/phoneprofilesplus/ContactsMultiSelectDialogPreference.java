package sk.henrichg.phoneprofilesplus;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;

import android.preference.DialogPreference;
import android.provider.ContactsContract;
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

	private ArrayList<Contact> planetList;
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
                ContactViewHolder viewHolder = (ContactViewHolder) item.getTag();
                viewHolder.getCheckBox().setChecked(planet.checked);
            }
        });		
		

		String[] projection = new String[] { ContactsContract.Contacts.HAS_PHONE_NUMBER, 
											 ContactsContract.Contacts._ID, 
											 ContactsContract.Contacts.DISPLAY_NAME,
										     ContactsContract.Contacts.PHOTO_ID };
		String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "='1'";
		String order = ContactsContract.Contacts.DISPLAY_NAME + " ASC";

		Cursor mCursor = _context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, selection, null, order);
		
	    while (mCursor.moveToNext()) 
	    {
	        try{
	        	Contact aContact = new Contact();
	        	String contactId = mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts._ID)); 
	        	String name = mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	        	//String hasPhone = mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
	        	String photoId = mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
	        	if (Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) 
	        	{
	        		Cursor phones = _context.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null);
	        		while (phones.moveToNext()) 
	        		{ 
	        			String phoneNumber = phones.getString(phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));
	        			aContact.name = name;
	        			aContact.phoneNumber = phoneNumber;
	        			try {
	        				aContact.photoId = Long.parseLong(photoId);
	        			} catch (Exception e) {
	        				aContact.photoId = 0;
	        			}
	        			
	        			planetList.add(aContact);
	        		} 
	        		phones.close(); 
	        	}
	        }catch(Exception e){}
	     }		
		
	    // Add Contact Class to the Arraylist

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
