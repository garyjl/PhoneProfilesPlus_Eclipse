package sk.henrichg.phoneprofiles;

import java.util.List;
import com.actionbarsherlock.app.SherlockActivity;

import android.os.Build;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
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

	private ActivateProfileHelper activateProfileHelper;
	private List<Profile> profileList;
	private ActivateProfileListAdapter profileListAdapter;
	private LinearLayout linlayoutRoot;
	private LinearLayout linlayoutHeader;
	private ListView listView;
	private TextView activeProfileName;
	private ImageView activeProfileIcon;
	private int startupSource = 0;
	private Intent intent;
	
	private int popupWidth;
	private int popupMaxHeight;
	private int popupHeight;
	private int actionBarHeight;
	

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		//Debug.startMethodTracing("phoneprofiles");
		
		GlobalData.setTheme(this, true);
		GlobalData.setLanguage(getBaseContext());

		//requestWindowFeature(Window.FEATURE_ACTION_BAR);
		
		if (GlobalData.applicationActivatorPrefIndicator && GlobalData.applicationActivatorHeader)
			setContentView(R.layout.activity_activate_profile);
		else
		if (GlobalData.applicationActivatorHeader)
			setContentView(R.layout.activity_activate_profile_no_indicator);
		else
			setContentView(R.layout.activity_activate_profile_no_header);

		//getSupportActionBar().setHomeButtonEnabled(true);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		profileList = GlobalData.getProfileList();

	/*	if (profileList.size() == 0)
		{
			// nie je ziaden profile, staretnene Editor
			
			Intent intent = new Intent(getBaseContext(), EditorProfilesActivity.class);
			intent.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_ACTIVATOR);

			startActivity(intent);
			
			finish();

			return;
		} */

		intent = getIntent();
		startupSource = intent.getIntExtra(GlobalData.EXTRA_START_APP_SOURCE, 0);
		
		activateProfileHelper = GlobalData.getActivateProfileHelper();
		activateProfileHelper.initialize(this, getBaseContext());

		linlayoutRoot = (LinearLayout)findViewById(R.id.act_prof_linlayout_root);
		if (GlobalData.applicationActivatorHeader)
			linlayoutHeader = (LinearLayout)findViewById(R.id.act_prof_linlayout_header);
		activeProfileName = (TextView)findViewById(R.id.act_prof_activated_profile_name);
		activeProfileIcon = (ImageView)findViewById(R.id.act_prof_activated_profile_icon);
		listView = (ListView)findViewById(R.id.act_prof_profiles_list);

		profileListAdapter = new ActivateProfileListAdapter(this, profileList);
		listView.setAdapter(profileListAdapter);

		//listView.setLongClickable(false);

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				//Log.d("ActivateProfilesActivity.onItemClick", "xxxx");

				if (!GlobalData.applicationLongClickActivation)
					activateProfileWithAlert(position);

			}
			
		}); 
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				//Log.d("ActivateProfilesActivity.onItemLongClick", "xxxx");
				
				if (GlobalData.applicationLongClickActivation)
					activateProfileWithAlert(position);

				return false;
			}
			
		});

        //listView.setRemoveListener(onRemove);
		
		Display display = getWindowManager().getDefaultDisplay();
		
		// set popup width into display width - fix graphical glitch
		getWindow().setLayout(0, display.getHeight());

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
		final SherlockActivity activity = this;
		
		if (GlobalData.applicationActivatorHeader)
		{
			linlayoutHeader.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				
				public void onGlobalLayout() {
					linlayoutHeader.getViewTreeObserver().removeGlobalOnLayoutListener(this);
	
		        	final float scale = getResources().getDisplayMetrics().density;
	
					popupHeight = popupHeight + linlayoutHeader.getHeight();
					// add 1dp
					popupHeight = popupHeight + (int) (12 * scale + 0.5f);
	
					//Log.d("ActivateProfilesActivity.onGlobalLayout", "header");
					
				}
			});
		}
		
		listView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			public void onGlobalLayout() {
				listView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

				popupHeight = popupHeight + listView.getHeight();

				//Log.d("ActivateProfilesActivity.onGlobalLayout", "listview");
			}
		});
		
		linlayoutRoot.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			
			public boolean onPreDraw() {
				linlayoutRoot.getViewTreeObserver().removeOnPreDrawListener(this);

				if (popupHeight > popupMaxHeight)
					popupHeight = popupMaxHeight;

				// set popup window dimensions
				activity.getWindow().setLayout(popupWidth, popupHeight);
				
				ActivateProfileListAdapter.completeGetView = false;
				
				//Log.d("ActivateProfilesActivity.onPreDraw", "linlayoutRoot");
				return true;
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
			
			if (!GlobalData.getApplicationStarted())
			{
				// aplikacia este nie je nastartovana, takze mozeme
				// aktivovat profil, ak je nastavene, ze sa tak ma stat 
				if (GlobalData.applicationActivate)
				{
					// je nastavene, ze pri starte sa ma aktivita aktivovat
					actProfile = true;
				}
			}
		}
		//Log.d("ActivateProfilesActivity.onStart", "actProfile="+String.valueOf(actProfile));

		Profile profile = GlobalData.getActivatedProfile();

		if (actProfile && (profile != null))
		{
			// aktivacia profilu
			activateProfile(profile, false);
		}
		else
		{
			updateHeader(profile);
			if (startupSource == 0)
			{
				// aktivita nebola spustena z notifikacie, ani z widgetu
				// pre profil, ktory je prave aktivny, treba aktualizovat notifikaciu a widgety 
				activateProfileHelper.showNotification(profile);
				activateProfileHelper.updateWidget();
			}
		}
		
		// reset, aby sa to dalej chovalo ako normalne spustenie z lauchera
		startupSource = 0;

		// na onStart dame, ze aplikacia uz je nastartovana
		GlobalData.setApplicationStarted(true);
		
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
		Intent intent = getIntent();
		startActivity(intent);
		finish();
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
			profilePrefIndicatorImageView.setImageBitmap(profile._preferencesIndicator);
		}
	}
	
	private void activateProfileWithAlert(int position)
	{
		if (GlobalData.applicationActivateWithAlert)
		{	
			final int _position = position;
			final Profile profile = profileList.get(_position);

			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle(getResources().getString(R.string.profile_string_0) + ": " + profile._name);
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
		profileListAdapter.activateProfile(profile);
		GlobalData.getDatabaseHandler().activateProfile(profile);
		
		activateProfileHelper.execute(profile, interactive);

		updateHeader(profile);
		activateProfileHelper.updateWidget();

		if (GlobalData.notificationsToast)
		{	
			// toast notification
			Toast msg = Toast.makeText(getBaseContext(), 
					getResources().getString(R.string.toast_profile_activated_0) + ": " + profile._name + " " +
					getResources().getString(R.string.toast_profile_activated_1), 
					Toast.LENGTH_LONG);
			msg.show();
		}

		activateProfileHelper.showNotification(profile);

		if (GlobalData.applicationClose)
		{	
			// ma sa zatvarat aktivita po aktivacii
			if (GlobalData.getApplicationStarted())
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
