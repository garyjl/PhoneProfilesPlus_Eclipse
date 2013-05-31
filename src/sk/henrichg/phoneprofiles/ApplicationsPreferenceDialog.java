package sk.henrichg.phoneprofiles;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.os.AsyncTask;
//import android.preference.Preference;
//import android.preference.Preference.OnPreferenceChangeListener;
import android.view.View;
import android.content.DialogInterface.OnShowListener;


public class ApplicationsPreferenceDialog extends Dialog implements OnShowListener {

	private ApplicationsPreference applicationsPreference;
	private ApplicationsPreferenceAdapter applicationsPreferenceAdapter;
	
	private Context _context;
	
	private ListView listView;
	private LinearLayout linlaProgress;
	
	public ApplicationsPreferenceDialog(Context context) {
		super(context);
	}
	
	public ApplicationsPreferenceDialog(Context context, ApplicationsPreference preference)
	{
		super(context);
		
		applicationsPreference = preference;

		_context = context;
		
		setContentView(R.layout.activity_applications_pref_dialog);
		
		linlaProgress = (LinearLayout)findViewById(R.id.applications_pref_dlg_linla_progress);
		listView = (ListView)findViewById(R.id.applications_pref_dlg_listview);
		
		applicationsPreferenceAdapter = new ApplicationsPreferenceAdapter(_context); 
	
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				String packageName = applicationsPreferenceAdapter.getApplicationPackageName(position);
				applicationsPreference.setPackageName(packageName);
				ApplicationsPreferenceDialog.this.dismiss();
			}

		});

/*		applicationPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
			}	
		}); */

		setOnShowListener(this);
	}

	
	public void onShow(DialogInterface dialog) {

		if (!PhoneProfilesActivity.getApplicationsCache().isCached())
		{
			new AsyncTask<Void, Integer, Void>() {
	
				@Override
				protected void onPreExecute()
				{
					super.onPreExecute();
					linlaProgress.setVisibility(View.VISIBLE);
				}
				
				@Override
				protected Void doInBackground(Void... params) {
					PhoneProfilesActivity.getApplicationsCache().getApplicationsList(_context);
					
					return null;
				}
				
				@Override
				protected void onPostExecute(Void result)
				{
					super.onPostExecute(result);
					
					listView.setAdapter(applicationsPreferenceAdapter);
					linlaProgress.setVisibility(View.GONE);
				}
				
			}.execute();
		}
		else
		{
			listView.setAdapter(applicationsPreferenceAdapter);
		}

	}
	
	

}
