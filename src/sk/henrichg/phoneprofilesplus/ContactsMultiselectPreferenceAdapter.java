package sk.henrichg.phoneprofilesplus;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ContactsMultiselectPreferenceAdapter extends ArrayAdapter<Contact> 
{

    private LayoutInflater inflater;

    public ContactsMultiselectPreferenceAdapter(Context context, List<Contact> planetList) 
    {
        super(context, R.layout.contacts_multiselect_preference_list_item, R.id.contacts_multiselect_pref_dlg_item_label, planetList);
        // Cache the LayoutInflate to avoid asking for a new one each time.
        inflater = LayoutInflater.from(context);
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
                    contact.setChecked(cb.isChecked());
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
        checkBox.setChecked(planet.isChecked());
        textView.setText(planet.getName());

        return convertView;
    }

}
