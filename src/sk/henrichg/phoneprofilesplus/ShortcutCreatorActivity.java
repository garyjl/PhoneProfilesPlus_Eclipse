package sk.henrichg.phoneprofilesplus;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ShortcutCreatorActivity extends SherlockActivity {

	private ProfilesDataWrapper profilesDataWrapper;
	private List<Profile> profileList;
	private ShortcutProfileListAdapter profileListAdapter;
	private ListView listView;
	
	private float popupWidth;
	private float popupMaxHeight;
	private float popupHeight;
	private float actionBarHeight;

	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GUIData.setTheme(this, true);
		GUIData.setLanguage(getBaseContext());

		profilesDataWrapper = new ProfilesDataWrapper(GlobalData.context, true, false, 0);

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
		if (getTheme().resolveAttribute(com.actionbarsherlock.R.attr.actionBarSize, tv, true))
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
		int profileCount = profilesDataWrapper.getDatabaseHandler().getProfilesCount(false);
		popupHeight = popupHeight + (50f * scale * profileCount); // item
		popupHeight = popupHeight + (5f * scale * (profileCount-1)); // divider

		popupHeight = popupHeight + (20f * scale); // listview padding
		
		if (popupHeight > popupMaxHeight)
			popupHeight = popupMaxHeight;
	
		// set popup window dimensions
		getWindow().setLayout((int) (popupWidth + 0.5f), (int) (popupHeight + 0.5f));
		
	//-----------------------------------------------------------------------------------

		new AsyncTask<Void, Integer, Void>() {
			
			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				profileList = profilesDataWrapper.getProfileList();
			    Collections.sort(profileList, new ProfileComparator());
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result)
			{
				super.onPostExecute(result);

				if (listView != null)
				{
					profileListAdapter = new ShortcutProfileListAdapter(getBaseContext(), profileList);
					listView.setAdapter(profileListAdapter);
				}
				
			}
			
		}.execute();
		
		
		setContentView(R.layout.activity_shortcut_creator);

		getSupportActionBar().setTitle(R.string.title_activity_shortcut_creator);

		//databaseHandler = new DatabaseHandler(this);
		
		listView = (ListView)findViewById(R.id.shortcut_profiles_list);

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				//Log.d("ShortcutCreatorActivity.onItemClick", "xxxx");
				
				createShortcut(position);

			}
			
		});
		
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		if (profileList != null)
		{
			if (profileListAdapter == null)
			{
				profileListAdapter = new ShortcutProfileListAdapter(getBaseContext(), profileList);
				listView.setAdapter(profileListAdapter);
			}
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
		Intent intent = getIntent();
		startActivity(intent);
		finish();
	}
	
	private void createShortcut(int position)
	{
		Profile profile = profileList.get(position);
		boolean isIconResourceID;
		String iconIdentifier;
		Bitmap profileBitmap;
		Bitmap shortcutOverlayBitmap;
		Bitmap profileShortcutBitmap;
		String profileName;

		if (profile != null)
		{
			isIconResourceID = profile.getIsIconResourceID();
			iconIdentifier = profile.getIconIdentifier();
			profileName = profile._name;
		}
		else
		{
			isIconResourceID = true;
			iconIdentifier = GUIData.PROFILE_ICON_DEFAULT;
			profileName = getResources().getString(R.string.profile_name_default);
		}

		Intent shortcutIntent = new Intent(this, BackgroundActivateProfileActivity.class);
		// BackgroundActivateProfileActivity musi toto testovat, a len spravit aktivaciu profilu
		shortcutIntent.putExtra(GlobalData.EXTRA_START_APP_SOURCE, GlobalData.STARTUP_SOURCE_SHORTCUT);
		shortcutIntent.putExtra(GlobalData.EXTRA_PROFILE_ID, profile._id);
		
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, profileName);
		
		int iconResource;
        if (isIconResourceID)
        {
        	iconResource = getResources().getIdentifier(iconIdentifier, "drawable", getPackageName());
			profileBitmap = BitmapFactory.decodeResource(getResources(), iconResource);
        }
        else
        {
    		Resources resources = getResources();
    		int height = (int) resources.getDimension(android.R.dimen.app_icon_size);
    		int width = (int) resources.getDimension(android.R.dimen.app_icon_size);
    		profileBitmap = BitmapManipulator.resampleBitmap(iconIdentifier, width, height);
        }
        
        if (GlobalData.applicationWidgetIconColor.equals("1"))
        {
    		int monochromeValue = 0xFF;
    		if (GlobalData.applicationWidgetIconLightness.equals("0")) monochromeValue = 0x00;
    		if (GlobalData.applicationWidgetIconLightness.equals("25")) monochromeValue = 0x40;
    		if (GlobalData.applicationWidgetIconLightness.equals("50")) monochromeValue = 0x80;
    		if (GlobalData.applicationWidgetIconLightness.equals("75")) monochromeValue = 0xC0;
    		if (GlobalData.applicationWidgetIconLightness.equals("100")) monochromeValue = 0xFF;
            
            if (isIconResourceID)
            	profileBitmap = BitmapManipulator.monochromeBitmap(profileBitmap, monochromeValue, getBaseContext());
            else
            	profileBitmap = BitmapManipulator.grayscaleBitmap(profileBitmap);
        }
        
    	shortcutOverlayBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_shortcut_overlay);
    	profileShortcutBitmap = combineImages(profileBitmap, shortcutOverlayBitmap);
    	intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, profileShortcutBitmap);
				
		setResult(RESULT_OK, intent);
		
		finish();
	}
	
	private Bitmap combineImages(Bitmap bitmap1, Bitmap bitmap2)
	{
		Bitmap combined;
		
		int width;
		int height;
		
		width = bitmap1.getWidth();
		height = bitmap1.getHeight();
		
		combined = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(combined);
		canvas.drawBitmap(bitmap1, 0f, 0f, null);
		canvas.drawBitmap(bitmap2, 0f, 0f, null);

		return combined;
	}

	private class ProfileComparator implements Comparator<Profile> {

		public int compare(Profile lhs, Profile rhs) {

		    int res = GUIData.collator.compare(lhs._name, rhs._name);
	        return res;
	    }

	}
	
}
