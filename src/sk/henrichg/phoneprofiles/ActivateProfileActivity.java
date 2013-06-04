package sk.henrichg.phoneprofiles;

import java.util.List;
import com.actionbarsherlock.app.SherlockActivity;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivateProfileActivity extends SherlockActivity {

	private DatabaseHandler databaseHandler;
	private ActivateProfileHelper activateProfileHelper;
	private List<Profile> profileList;
	private static ActivateProfileListAdapter profileListAdapter;
	private LinearLayout linlayoutHeader;
	private ListView listView;
	private TextView activeProfileName;
	private ImageView activeProfileIcon;
	private int startupSource = 0;
	private static boolean applicationStarted;
	private Intent intent;
	
	private int popupWidth;
	private int popupMaxHeight;
	private int popupHeight;
	private int actionBarHeight;
	

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PhoneProfilesActivity.setLanguage(getBaseContext(), false);
		
		setContentView(R.layout.activity_activate_profile);
		
		//getSupportActionBar().setHomeButtonEnabled(true);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		intent = getIntent();
		startupSource = intent.getIntExtra(PhoneProfilesActivity.EXTRA_START_APP_SOURCE, 0);
		
		activateProfileHelper = new ActivateProfileHelper(this, getBaseContext());

		databaseHandler = new DatabaseHandler(this);
		
		linlayoutHeader = (LinearLayout)findViewById(R.id.act_prof_linlayout_header);
		activeProfileName = (TextView)findViewById(R.id.act_prof_activated_profile_name);
		activeProfileIcon = (ImageView)findViewById(R.id.act_prof_activated_profile_icon);
		listView = (ListView)findViewById(R.id.act_prof_profiles_list);
		
		profileList = databaseHandler.getAllProfiles();

		profileListAdapter = new ActivateProfileListAdapter(this, profileList);
		
		listView.setAdapter(profileListAdapter);
		
		//listView.setLongClickable(false);

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				//Log.d("ActivateProfilesActivity.onItemClick", "xxxx");

				SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
				if (!preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_APPLICATION_LONG_PRESS_ACTIVATION, false))
					activateProfileWithAlert(position);

			}
			
		}); 
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				//Log.d("ActivateProfilesActivity.onItemLongClick", "xxxx");
				
				SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
				if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_APPLICATION_LONG_PRESS_ACTIVATION, false))
					activateProfileWithAlert(position);

				return false;
			}
			
		});
		
        //listView.setRemoveListener(onRemove);
		

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		LayoutParams params = getWindow().getAttributes();
		params.alpha = 1.0f;
		params.dimAmount = 0.5f;
		getWindow().setAttributes(params);
		
		// display dimensions
		Display display = getWindowManager().getDefaultDisplay();
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
		if (getTheme().resolveAttribute(com.actionbarsherlock.R.attr.actionBarSize, tv, true))
		{
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
		}
		
		// set max. dimensions for display orientation
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			popupWidth = Math.round(popupWidth / 100f * 50f);
			popupMaxHeight = Math.round(popupMaxHeight / 100f * 90f);
		}
		else
		{
			popupWidth = Math.round(popupWidth / 100f * 70f);
			popupMaxHeight = Math.round(popupMaxHeight / 100f * 90f);
		}

		// add action bar height
		popupHeight = popupHeight + actionBarHeight; 
		
		// get views height and add it into popupHeight
		final Activity activity = this;
		
		linlayoutHeader.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			public void onGlobalLayout() {
				linlayoutHeader.getViewTreeObserver().removeGlobalOnLayoutListener(this);

	        	final float scale = getResources().getDisplayMetrics().density;

				popupHeight = popupHeight + linlayoutHeader.getHeight();
				// add 1dp
				popupHeight = popupHeight + (int) (12 * scale + 0.5f);

			}
		});
		
		listView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			public void onGlobalLayout() {
				listView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

				popupHeight = popupHeight + listView.getHeight();

				// only last view set window popup dimensions
				if (popupHeight > popupMaxHeight)
					popupHeight = popupMaxHeight;

				// set popup window dimensions
				activity.getWindow().setLayout(popupWidth, popupHeight);
			}
		});
		
		
		//Log.d("PhoneProfileActivity.onCreate", "xxxx");
		
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		
		//Log.d("ActivateProfilesActivity.onStart", "startupSource="+startupSource);
		
		boolean actProfile = false;
		if (startupSource == 0)
		{
			
			// aktivita nebola spustena z notifikacie, ani z widgetu
			// lebo v tychto pripadoch sa nesmie spravit aktivacia profilu
			// pri starte aktivity
			
			if (!applicationStarted)
			{
				// aplikacia este nie je nastartovana, takze mozeme
				// aktivovat profil, ak je nastavene, ze sa tak ma stat 
				SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
				if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_APPLICATION_ACTIVATE, true))
				{
					// je nastavene, ze pri starte sa ma aktivita aktivovat
					actProfile = true;
				}
			}
		}
		//Log.d("ActivateProfilesActivity.onStart", "actProfile="+String.valueOf(actProfile));

		Profile profile;
		
		// pre profil, ktory je prave aktivny, treba aktualizovat aktivitu
		profile = databaseHandler.getActivatedProfile();
		updateHeader(profile);
		activateProfileHelper.showNotification(profile);
		activateProfileHelper.updateWidget();
		
		if (actProfile && (profile != null))
		{
			// aktivacia profilu
			activateProfile(profile, false);
		}
		
		// reset, aby sa to dalej chovalo ako normalne spustenie z lauchera
		startupSource = 0;

		// na onStart dame, ze aplikacia uz je nastartovana
		applicationStarted = true;
		
		//Log.d("PhoneProfileActivity.onStart", "xxxx");
		
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
	}
	
	@Override
	protected void onResume()
	{
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
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_activate_profile, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_edit_profiles:
			//Log.d("PhoneProfilesActivity.onOptionsItemSelected", "menu_settings");
			
			Intent intent = new Intent(getBaseContext(), PhoneProfilesActivity.class);

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
		Intent intent = getIntent();
		startActivity(intent);
		finish();
	}

	private void updateHeader(Profile profile)
	{
		if (profile == null)
		{
			activeProfileName.setText(getResources().getString(R.string.profiles_header_profile_name_no_activated));
	    	activeProfileIcon.setImageResource(R.drawable.ic_profile_default);
		}
		else
		{
			activeProfileName.setText(profile.getName());
	        if (profile.getIsIconResourceID())
	        {
				int res = getResources().getIdentifier(profile.getIconIdentifier(), "drawable", getPackageName());
				activeProfileIcon.setImageResource(res); // resource na ikonu
	        }
	        else
	        {
        		Resources resources = getResources();
        		int height = (int) resources.getDimension(android.R.dimen.app_icon_size);
        		int width = (int) resources.getDimension(android.R.dimen.app_icon_size);
        		Bitmap bitmap = BitmapResampler.resample(profile.getIconIdentifier(), width, height);

	        	activeProfileIcon.setImageBitmap(bitmap);
	        }
		}
		
		ImageView profilePrefIndicatorImageView = (ImageView)findViewById(R.id.act_prof_activated_profile_pref_indicator);
		profilePrefIndicatorImageView.setImageBitmap(ProfilePreferencesIndicator.paint(profile, getBaseContext()));
		
	}
	
	private void activateProfileWithAlert(int position)
	{
		SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);

		if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_APPLICATION_ALERT, true))
		{	
			final int _position = position;
			final Profile profile = profileList.get(_position);

			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle(getResources().getString(R.string.profile_string_0) + ": " + profile.getName());
			dialogBuilder.setMessage(getResources().getString(R.string.activate_profile_alert_message) + "?");
			//dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
			dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					activateProfile(_position, true);
				}
			});
			dialogBuilder.setNegativeButton(android.R.string.no, null);
			dialogBuilder.show();
		}
		else
			activateProfile(position, true);
	}
	
	private void activateProfile(Profile profile, boolean interactive)
	{
		SharedPreferences preferences = getSharedPreferences(PhoneProfilesPreferencesActivity.PREFS_NAME, MODE_PRIVATE);
		
		profileListAdapter.activateProfile(profile);
		databaseHandler.activateProfile(profile);
		
		activateProfileHelper.execute(profile, interactive);

		updateHeader(profile);
		activateProfileHelper.updateWidget();

		if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_NOTIFICATION_TOAST, true))
		{	
			// toast notification
			Toast msg = Toast.makeText(getBaseContext(), 
					getResources().getString(R.string.toast_profile_activated_0) + ": " + profile.getName() + " " +
					getResources().getString(R.string.toast_profile_activated_1), 
					Toast.LENGTH_LONG);
			msg.show();
		}

		activateProfileHelper.showNotification(profile);

		if (preferences.getBoolean(PhoneProfilesPreferencesActivity.PREF_APPLICATION_CLOSE, true))
		{	
			// ma sa zatvarat aktivita po aktivacii
			if (applicationStarted)
				// aplikacia je uz spustena, mozeme aktivitu zavriet
				// tymto je vyriesene, ze pri spusteni aplikacie z launchera
				// sa hned nezavrie
				finish();
		}
	}
	
	private void activateProfile(int position, boolean interactive)
	{
		Profile profile = profileList.get(position);
		activateProfile(profile, interactive);
	}
	
}
