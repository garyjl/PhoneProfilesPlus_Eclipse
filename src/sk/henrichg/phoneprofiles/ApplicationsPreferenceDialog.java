package sk.henrichg.phoneprofiles;

import android.app.Dialog;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
//import android.preference.Preference;
//import android.preference.Preference.OnPreferenceChangeListener;
import android.view.View;


public class ApplicationsPreferenceDialog extends Dialog {

	private ApplicationsPreference applicationsPreference;
	private ApplicationsPreferenceAdapter applicationsPreferenceAdapter;
	
	public ApplicationsPreferenceDialog(Context context) {
		super(context);
	}
	
	public ApplicationsPreferenceDialog(Context context, ApplicationsPreference preference)
	{
		super(context);
		
		applicationsPreference = preference;

		final Context _context = context;
		
		GridView gridView;
		
		setContentView(R.layout.activity_applications_pref_dialog);
		gridView = (GridView)findViewById(R.id.applications_pref_dlg_gridview);
		
		applicationsPreferenceAdapter = new ApplicationsPreferenceAdapter(_context); 
		gridView.setAdapter(applicationsPreferenceAdapter);
	
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				String packageName = applicationsPreferenceAdapter.getApplicationPackageName(position);
				applicationsPreference.setPackageName(packageName);
				ApplicationsPreferenceDialog.this.dismiss();
			}

		});

		// TU NAPLN ApplicacionsCache !!!

/*		applicationPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
			}	
		}); */
		
			
	}


}
