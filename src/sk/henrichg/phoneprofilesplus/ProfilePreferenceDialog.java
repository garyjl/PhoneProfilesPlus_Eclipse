package sk.henrichg.phoneprofilesplus;

import android.app.Dialog;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
//import android.preference.Preference;
//import android.preference.Preference.OnPreferenceChangeListener;
import android.view.View;


public class ProfilePreferenceDialog extends Dialog
{

	public ProfilePreference profilePreference;
	public int addNoActivateItem;
	private ProfilePreferenceAdapter profilePreferenceAdapter;
	
	String profileId;
	
	private Context _context;
	
	private ListView listView;

	public ProfilePreferenceDialog(Context context) {
		super(context);
	}
	
	public ProfilePreferenceDialog(Context context, ProfilePreference preference, String profileId)
	{
		super(context);
		
		profilePreference = preference;
		this.profileId = profileId;
		
		addNoActivateItem = profilePreference.addNoActivateItem;


		_context = context;
		
		setContentView(R.layout.activity_profile_pref_dialog);
		
		listView = (ListView)findViewById(R.id.profile_pref_dlg_listview);
		
		profilePreferenceAdapter = new ProfilePreferenceAdapter(this, _context, profileId); 
		listView.setAdapter(profilePreferenceAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				doOnItemSelected(position);
			}

		});
		
	}

	public void doOnItemSelected(int position)
	{
		if (addNoActivateItem == 1)
		{
			long profileId;
			if (position == 0)
				profileId = Event.PROFILE_END_NO_ACTIVATE;
			else
				profileId = profilePreferenceAdapter.profileList.get(position-1)._id;
			profilePreference.setProfileId(profileId);	
		}
		else
			profilePreference.setProfileId(profilePreferenceAdapter.profileList.get(position)._id);
		ProfilePreferenceDialog.this.dismiss();
	}
	
}
