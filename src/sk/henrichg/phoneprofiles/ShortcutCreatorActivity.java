package sk.henrichg.phoneprofiles;

import java.util.List;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ShortcutCreatorActivity extends SherlockActivity {

	private DatabaseHandler databaseHandler;
	
	private List<Profile> profileList;
	private ShortcutProfileListAdapter profileListAdapter;
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PhoneProfilesActivity.setLanguage(getBaseContext(), false);
		
		setContentView(R.layout.activity_shortcut_creator);

		databaseHandler = new DatabaseHandler(this);
		
		listView = (ListView)findViewById(R.id.shortcut_profiles_list);

		profileList = databaseHandler.getAllProfiles();

		profileListAdapter = new ShortcutProfileListAdapter(this, profileList);
		listView.setAdapter(profileListAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Log.d("ShortcutCreatorActivity.onItemClick", "xxxx");
				
				createShortcut(position);

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
		Intent shortcutIntent = new Intent(this, ActivateProfileActivity.class);
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
        	//ShortcutIconResource shortcutIconResource = ShortcutIconResource.fromContext(this, iconResource);
    		//intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIconResource);
        }
        else
        {
        	profileBitmap = BitmapFactory.decodeFile(iconIdentifier);
        	//Bitmap bitmap = BitmapFactory.decodeFile(iconIdentifier);
    		//intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
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
