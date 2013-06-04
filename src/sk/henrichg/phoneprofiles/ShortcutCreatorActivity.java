package sk.henrichg.phoneprofiles;

import java.util.List;

import com.actionbarsherlock.app.SherlockActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ShortcutCreatorActivity extends SherlockActivity {

	private DatabaseHandler databaseHandler;
	
	private List<Profile> profileList;
	private ShortcutProfileListAdapter profileListAdapter;
	private ListView listView;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PhoneProfilesActivity.setLanguage(getBaseContext(), false);

		//requestWindowFeature(Window.FEATURE_ACTION_BAR);

		setContentView(R.layout.activity_shortcut_creator);
		
		databaseHandler = new DatabaseHandler(this);
		
		listView = (ListView)findViewById(R.id.shortcut_profiles_list);

		profileList = databaseHandler.getAllProfiles();

		profileListAdapter = new ShortcutProfileListAdapter(this, profileList);
		listView.setAdapter(profileListAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				//Log.d("ShortcutCreatorActivity.onItemClick", "xxxx");
				
				createShortcut(position);

			}
			
		});
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		LayoutParams params = getWindow().getAttributes();
		params.alpha = 1.0f;
		params.dimAmount = 0.5f;
		getWindow().setAttributes(params);
		

		final Activity activity = this;
		
		listView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			public void onGlobalLayout() {
				listView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

				Display display = getWindowManager().getDefaultDisplay();
				int width = display.getWidth();
				int maxHeight = display.getHeight();
				int height = maxHeight;
				int actionBarHeight = 0;

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
				   
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
				{
					width = Math.round(width / 100f * 50f);
					maxHeight = Math.round(maxHeight / 100f * 90f);
					
					height = listView.getHeight();
					height = height + actionBarHeight; 
					
					if (height > maxHeight)
						height = maxHeight;
				}
				else
				{
					width = Math.round(width / 100f * 70f);
					maxHeight = Math.round(maxHeight / 100f * 90f);
					
					height = listView.getHeight();
					height = height + actionBarHeight; 
					
					if (height > maxHeight)
						height = maxHeight;
				}
				activity.getWindow().setLayout(width, height);
			}
		});
		
		
		
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
			profileName = profile.getName();
		}
		else
		{
			isIconResourceID = true;
			iconIdentifier = PhoneProfilesActivity.PROFILE_ICON_DEFAULT;
			profileName = getResources().getString(R.string.profile_name_default);
		}

	//	Intent shortcutIntent = new Intent(this, PhoneProfilesActivity.class);
		Intent shortcutIntent = new Intent(this, BackgroundActivateProfileActivity.class);
		// PhoneProfilesActivity musi toto testovat, a len spravit aktivaciu profilu
		shortcutIntent.putExtra(PhoneProfilesActivity.EXTRA_START_APP_SOURCE, PhoneProfilesActivity.STARTUP_SOURCE_SHORTCUT);
		shortcutIntent.putExtra(PhoneProfilesActivity.EXTRA_PROFILE_ID, profile.getID());
		
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
    		profileBitmap = BitmapResampler.resample(iconIdentifier, width, height);
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

}
