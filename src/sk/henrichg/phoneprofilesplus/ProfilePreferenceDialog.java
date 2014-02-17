package sk.henrichg.phoneprofilesplus;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
//import android.preference.Preference;
//import android.preference.Preference.OnPreferenceChangeListener;
import android.view.View;
import android.content.DialogInterface.OnShowListener;


public class ProfilePreferenceDialog extends Dialog implements OnShowListener {

	public ProfilePreference profilePreference;
	private ProfilePreferenceAdapter profilePreferenceAdapter;
	
	private Context _context;
	
	private ListView listView;
	public ProfilePreferenceDialog(Context context) {
		super(context);
	}
	
	public ProfilePreferenceDialog(Context context, ProfilePreference preference, String profileId)
	{
		super(context);
		
		profilePreference = preference;

		_context = context;
		
		setContentView(R.layout.activity_profile_pref_dialog);
		
		listView = (ListView)findViewById(R.id.profile_pref_dlg_listview);
		
		profilePreferenceAdapter = new ProfilePreferenceAdapter(this, _context, profileId); 
	
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				profilePreference.setProfileId(profilePreferenceAdapter.profileList.get(position)._id);
				ProfilePreferenceDialog.this.dismiss();
			}

		});
		

/*		applicationPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
			}	
		}); */

		setOnShowListener(this);
	}

	
	public void onShow(DialogInterface dialog) {
		listView.setAdapter(profilePreferenceAdapter);
	}
	
	

}
