package sk.henrichg.phoneprofilesplus;

import java.util.List;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ContactsMultiselectPreferenceAdapter extends ArrayAdapter<Contact> 
{
    private LayoutInflater inflater;
    private Context context;

    public ContactsMultiselectPreferenceAdapter(Context context, List<Contact> planetList) 
    {
        super(context, R.layout.contacts_multiselect_preference_list_item, R.id.contacts_multiselect_pref_dlg_item_label, planetList);
        // Cache the LayoutInflate to avoid asking for a new one each time.
        inflater = LayoutInflater.from(context);
        this.context = context; 
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Contact to display
        Contact planet = (Contact) this.getItem(position);
        System.out.println(String.valueOf(position));

        // The child views in each row.
        CheckBox checkBox;
        TextView textView;

        // Create a new row view
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contacts_multiselect_preference_list_item, null);

            // Find the child views.
            textView = (TextView) convertView.findViewById(R.id.contacts_multiselect_pref_dlg_item_label);
            checkBox = (CheckBox) convertView.findViewById(R.id.contacts_multiselect_pref_dlg_item_checkbox);

            // Optimization: Tag the row with it's child views, so we don't
            // have to
            // call findViewById() later when we reuse the row.
            convertView.setTag(new ContactViewHolder(textView, checkBox));

            // If CheckBox is toggled, update the Contact it is tagged with.
            checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    Contact contact = (Contact) cb.getTag();
                    contact.checked = cb.isChecked();
                }
            });
        }
        // Reuse existing row view
        else {
            // Because we use a ViewHolder, we avoid having to call
            // findViewById().
            ContactViewHolder viewHolder = (ContactViewHolder) convertView
                    .getTag();
            checkBox = viewHolder.getCheckBox();
            textView = viewHolder.getTextView();
        }

        // Tag the CheckBox with the Contact it is displaying, so that we
        // can
        // access the Contact in onClick() when the CheckBox is toggled.
        checkBox.setTag(planet);

        // Display Contact data
        checkBox.setChecked(planet.checked);
        textView.setText(planet.name);

        return convertView;
    }

	/**
	 * @return the photo URI
	 */
	private Uri getPhotoUri(long photoId)
	{
	    try {
	        Cursor cur = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null,
	                		ContactsContract.Data.CONTACT_ID + "=" + photoId + " AND "
	                        + ContactsContract.Data.MIMETYPE + "='"
	                        + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
	                        null);
	        if (cur != null) 
	        {
	            if (!cur.moveToFirst()) 
	            {
	                return null; // no photo
	            }
	        } 
	        else 
	            return null; // error in cursor process
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, photoId);
	    return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
	}
	
    
}
