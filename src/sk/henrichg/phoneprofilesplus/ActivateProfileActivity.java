package sk.henrichg.phoneprofilesplus;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.Configuration;

import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ActivateProfileActivity extends ActionBarActivity {

	private static ActivateProfileActivity instance;
	
	private DataWrapper dataWrapper;
	private ActivateProfileHelper activateProfileHelper;
	private List<Profile> profileList = null;
	private ActivateProfileListAdapter profileListAdapter = null;
	private ListView listView = null;
	private TextView activeProfileName;
	private ImageView activeProfileIcon;
	
	private WeakReference<LoadProfileListAsyncTask> asyncTaskContext;
	
	private float popupWidth;
	private float popupMaxHeight;
	private float popupHeight;
	private int actionBarHeight;
	

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		instance = this;
		
		GUIData.setTheme(this, true);
		GUIData.setLanguage(getBaseContext());
		
		dataWrapper = new DataWrapper(getBaseContext(), true, false, 0);
		activateProfileHelper = dataWrapper.getActivateProfileHelper();
		activateProfileHelper.initialize(this, getBaseContext());

	// set window dimensions ----------------------------------------------------------
		
		Display display = getWindowManager().getDefaultDisplay();
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		LayoutParams params = getWindow().getAttributes();
		params.alpha = 1.0f;
		params.dimAmount = 0.5f;
		getWindow().setAttributes(params);
		
		// display dimensions
		popupWidth = display.getWidth();
		popupMaxHeight = display.getHeight();
		popupHeight = 0;
		actionBarHeight = 0;

		// action bar height
		TypedValue tv = new TypedValue();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		        actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
		}
		else 
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
		}
		
		// set max. dimensions for display orientation
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			//popupWidth = Math.round(popupWidth / 100f * 50f);
			//popupMaxHeight = Math.round(popupMaxHeight / 100f * 90f);
			popupWidth = popupWidth / 100f * 50f;
			popupMaxHeight = popupMaxHeight / 100f * 90f;
		}
		else
		{
			//popupWidth = Math.round(popupWidth / 100f * 70f);
			//popupMaxHeight = Math.round(popupMaxHeight / 100f * 90f);
			popupWidth = popupWidth / 100f * 70f;
			popupMaxHeight = popupMaxHeight / 100f * 90f;
		}

		// add action bar height
		popupHeight = popupHeight + actionBarHeight;
		
		final float scale = getResources().getDisplayMetrics().density;
		
		// add header height
		if (GlobalData.applicationActivatorHeader)
			popupHeight = popupHeight + 64f * scale;
		
		// add list items height
		int profileCount = dataWrapper.getDatabaseHandler().getProfilesCount(true);
		popupHeight = popupHeight + (50f * scale * profileCount); // item
		popupHeight = popupHeight + (5f * scale * (profileCount-1)); // divider

		popupHeight = popupHeight + (20f * scale); // listview padding
		
		if (popupHeight > popupMaxHeight)
			popupHeight = popupMaxHeight;
	
		// set popup window dimensions
		getWindow().setLayout((int) (popupWidth + 0.5f), (int) (popupHeight + 0.5f));
		
	//-----------------------------------------------------------------------------------

		//Debug.startMethodTracing("phoneprofiles");

	// Layout ---------------------------------------------------------------------------------
		
		//requestWindowFeature(Window.FEATURE_ACTION_BAR);
		
		//long nanoTimeStart = GlobalData.startMeasuringRunTime();
		
		if (GlobalData.applicationActivatorPrefIndicator && GlobalData.applicationActivatorHeader)
			setContentView(R.layout.activity_activate_profile);
		else
		if (GlobalData.applicationActivatorHeader)
			setContentView(R.layout.activity_activate_profile_no_indicator);
		else
			setContentView(R.layout.activity_activate_profile_no_header);
		
		//GlobalData.getMeasuredRunTime(nanoTimeStart, "ActivateProfileActivity.onCreate - setContnetView");

		//getSupportActionBar().setHomeButtonEnabled(true);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.title_activity_activator);

		activeProfileName = (TextView)findViewById(R.id.act_prof_activated_profile_name);
		activeProfileIcon = (ImageView)findViewById(R.id.act_prof_activated_profile_icon);
		listView = (ListView)findViewById(R.id.act_prof_profiles_list);
		
		//listView.setLongClickable(false);

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				//Log.d("ActivateProfilesActivity.onItemClick", "xxxx");

				if (!GlobalData.applicationLongClickActivation)
				{
					activateProfile((Profile)profileListAdapter.getItem(position), GlobalData.STARTUP_SOURCE_ACTIVATOR);
				}

			}
			
		}); 
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				//Log.d("ActivateProfilesActivity.onItemLongClick", "xxxx");
				
				if (GlobalData.applicationLongClickActivation)
					//activateProfileWithAlert(position);
					activateProfile((Profile)profileListAdapter.getItem(position), GlobalData.STARTUP_SOURCE_ACTIVATOR);

				return false;
			}
			
		});

        //listView.setRemoveListener(onRemove);
		
    //-----------------------------------------------------------------------------------------		
		
		this.asyncTaskContext = (WeakReference<LoadProfileListAsyncTask>) getLastNonConfigurationInstance();

	    if (asyncTaskContext != null && this.asyncTaskContext.get() != null
	        && !this.asyncTaskContext.get().getStatus().equals(AsyncTask.Status.FINISHED)) {
	        this.asyncTaskContext.get().attach(this);
	    } else {
	    	LoadProfileListAsyncTask myAsyncTask = new LoadProfileListAsyncTask (this);
	        this.asyncTaskContext = new WeakReference<ActivateProfileActivity.LoadProfileListAsyncTask>(myAsyncTask);
	        myAsyncTask.execute();
	    }
		
		
		//Log.d("PhoneProfileActivity.onCreate", "xxxx");
		
	}
	
	@Override
	public Object onRetainCustomNonConfigurationInstance() {
	        
	    WeakReference<LoadProfileListAsyncTask> weakReference = null;
	            
	    if (this.asyncTaskContext != null
	        && this.asyncTaskContext.get() != null
	        && !this.asyncTaskContext.get().getStatus().equals(AsyncTask.Status.FINISHED)) {
	        weakReference = this.asyncTaskContext;
	    }
	    return weakReference;
	}	
	
	static private class LoadProfileListAsyncTask extends AsyncTask<Void, Void, Void> {

	    private WeakReference<ActivateProfileActivity> myWeakContext;
		private DataWrapper dataWrapper; 
		
		private class ProfileComparator implements Comparator<Profile> {
			public int compare(Profile lhs, Profile rhs) {
			    int res = lhs._porder - rhs._porder;
		        return res;
		    }
		}

	    private LoadProfileListAsyncTask (ActivateProfileActivity activity) {
	        this.myWeakContext = new WeakReference<ActivateProfileActivity>(activity);
	        this.dataWrapper = new DataWrapper(activity.getBaseContext(), true, false, 0);
	    }

	    @Override
	    protected Void doInBackground(Void... params) {
	    	List<Profile> profileList = dataWrapper.getProfileList();
		    Collections.sort(profileList, new ProfileComparator());
			return null;
	    }

	    @Override
	    protected void onPostExecute(Void result) {
	        super.onPostExecute(result);
	            
	        ActivateProfileActivity activity = this.myWeakContext.get();

	        if (activity != null)
	        {
		        // get local profileList
		    	List<Profile> profileList = dataWrapper.getProfileList();
		    	// set copy local profile list into activity dataWrapper
		        activity.dataWrapper.setProfileList(profileList, false);
		        // set reference of profile list from dataWrapper
		        activity.profileList = activity.dataWrapper.getProfileList();
		        
				if (activity.profileList.size() == 0)
				{
					// nie je ziaden profile, startneme Editor
					
					Intent intent = new Intent(activity.getBaseContext(), EditorProfilesActivity.class);
					intent.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_ACTIVATOR_START);
					activity.startActivity(intent);
					
					activity.finish();
	
					return;
				}
				
				activity.profileListAdapter = new ActivateProfileListAdapter(activity.getBaseContext(), profileList);
				activity.listView.setAdapter(activity.profileListAdapter);
					
				activity.doOnStart();
	        }
	    }

	    public void attach(ActivateProfileActivity activity) {
	        this.myWeakContext = new WeakReference<ActivateProfileActivity>(activity);
	    }
	}	
	
	public static ActivateProfileActivity getInstance()
	{
		return instance;
	}
	
	private void doOnStart()
	{
		//long nanoTimeStart = GlobalData.startMeasuringRunTime();

		//Log.d("ActivateProfilesActivity.onStart", "startupSource="+startupSource);

		Profile profile = dataWrapper.getActivatedProfile();
		
		updateHeader(profile);
		endOnStart();

		//GlobalData.getMeasuredRunTime(nanoTimeStart, "ActivateProfileActivity.onStart");
		
		//Log.d("PhoneProfileActivity.onStart", "xxxx");
		
	}
	
	private void endOnStart()
	{
		//Log.e("ActivateProfileActivity.endOnStart","xxx");

		//  aplikacia uz je 1. krat spustena
		GlobalData.setApplicationStarted(getBaseContext(), true);
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
	}
	
	@Override
	protected void onResume()
	{
		//Debug.stopMethodTracing();

		super.onResume();
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
	}
	
	@Override
	protected void onDestroy()
	{
	//	Debug.stopMethodTracing();
		profileList = null;
		activateProfileHelper = null;
		dataWrapper.invalidateDataWrapper();
		dataWrapper = null;

		instance = null;
		
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_activate_profile, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_edit_profiles:
			//Log.d("ActivateProfileActivity.onOptionsItemSelected", "menu_settings");
			
			Intent intent = new Intent(getBaseContext(), EditorProfilesActivity.class);
			intent.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_ACTIVATOR);
			startActivity(intent);
			
			finish();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
		//setContentView(R.layout.activity_phone_profiles);
		GUIData.reloadActivity(this, false);
	}

	private void updateHeader(Profile profile)
	{
		if (!GlobalData.applicationActivatorHeader)
			return;
		
		if (profile == null)
		{
			activeProfileName.setText(getResources().getString(R.string.profiles_header_profile_name_no_activated));
	    	activeProfileIcon.setImageResource(R.drawable.ic_profile_default);
		}
		else
		{
			activeProfileName.setText(profile._name);
	        if (profile.getIsIconResourceID())
	        {
				int res = getResources().getIdentifier(profile.getIconIdentifier(), "drawable", getPackageName());
				activeProfileIcon.setImageResource(res); // resource na ikonu
	        }
	        else
	        {
        		//Resources resources = getResources();
        		//int height = (int) resources.getDimension(android.R.dimen.app_icon_size);
        		//int width = (int) resources.getDimension(android.R.dimen.app_icon_size);
        		//Bitmap bitmap = BitmapResampler.resample(profile.getIconIdentifier(), width, height);
	        	//activeProfileIcon.setImageBitmap(bitmap);
	        	activeProfileIcon.setImageBitmap(profile._iconBitmap);
	        }
		}
		
		if (GlobalData.applicationActivatorPrefIndicator)
		{
			ImageView profilePrefIndicatorImageView = (ImageView)findViewById(R.id.act_prof_activated_profile_pref_indicator);
			//profilePrefIndicatorImageView.setImageBitmap(ProfilePreferencesIndicator.paint(profile, getBaseContext()));
			if (profile == null)
				profilePrefIndicatorImageView.setImageResource(R.drawable.ic_empty);
			else
				profilePrefIndicatorImageView.setImageBitmap(profile._preferencesIndicator);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (requestCode == GlobalData.REQUEST_CODE_ACTIVATE_PROFILE)
		{
			//Log.e("ActivateProfileActivity.onActivityResult","xxx");

			if(resultCode == RESULT_OK)
			{      
		    	long profile_id = data.getLongExtra(GlobalData.EXTRA_PROFILE_ID, -1);
		    	Profile profile = dataWrapper.getProfileById(profile_id);
		    	 
		    	profileListAdapter.activateProfile(profile);
				updateHeader(profile);

				if (GlobalData.applicationClose)
				{	
					// ma sa zatvarat aktivita po aktivacii
					if (GlobalData.getApplicationStarted(getBaseContext()))
						// aplikacia je uz spustena, mozeme aktivitu zavriet
						// tymto je vyriesene, ze pri spusteni aplikacie z launchera
						// sa hned nezavrie
						finish();
				}
		     }
		     if (resultCode == RESULT_CANCELED)
		     {    
		         //Write your code if there's no result
		     }
		     
		     endOnStart();
		}
	}
	
	private void activateProfile(Profile profile, int startupSource)
	{
		boolean finish = false;
		
		if (GlobalData.applicationClose)
		{	
			// ma sa zatvarat aktivita po aktivacii
			if (startupSource != GlobalData.STARTUP_SOURCE_ACTIVATOR_START)
				finish = true;
		}

		Intent intent = new Intent(getBaseContext(), BackgroundActivateProfileActivity.class);
		intent.putExtra(GlobalData.EXTRA_START_APP_SOURCE, startupSource);
		if (profile != null)
			intent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile._id);
		else
			intent.putExtra(GlobalData.EXTRA_PROFILE_ID, 0);
		
		//Log.e("ActivateProfileActivity.activateProfile","finish="+finish);
		
		if (finish)
		{
			startActivity(intent);
			finish();
		}
		else
			startActivityForResult(intent, GlobalData.REQUEST_CODE_ACTIVATE_PROFILE);
	}
	
	public void refreshGUI()
	{
		Profile profileFromAdapter = profileListAdapter.getActivatedProfile();
		if (profileFromAdapter != null)
			profileFromAdapter._checked = false;

		Profile profileFromDB = dataWrapper.getDatabaseHandler().getActivatedProfile();
		if (profileFromDB != null)
		{
			Profile profileFromDataWrapper = dataWrapper.getProfileById(profileFromDB._id);
			if (profileFromDataWrapper != null)
				profileFromDataWrapper._checked = true;
			updateHeader(profileFromDataWrapper);
		}
		else
			updateHeader(null);

		profileListAdapter.notifyDataSetChanged();
	}
	
}
