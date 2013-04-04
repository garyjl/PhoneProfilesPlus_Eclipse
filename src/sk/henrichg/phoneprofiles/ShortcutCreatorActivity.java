package sk.henrichg.phoneprofiles;

import java.util.List;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	
	private void createShortcut(int position)
	{
		Profile profile = profileList.get(position);
		boolean isIconResourceID;
		String iconIdentifier;
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

		Intent shortcutIntent = new Intent(this, PhoneProfilesActivity.class);
		// PhoneProfilesActivity musi toto testovat, a len spravit aktivaciu profilu
		shortcutIntent.putExtra(PhoneProfilesActivity.INTENT_START_APP_SOURCE, PhoneProfilesActivity.STARTUP_SOURCE_SHORTCUT);
		shortcutIntent.putExtra(PhoneProfilesActivity.INTENT_PROFILE_ID, profile.getID());
		
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, profileName);
		
        if (isIconResourceID)
        {
        	int iconResource = getResources().getIdentifier(iconIdentifier, "drawable", getPackageName());
    		ShortcutIconResource shortcutIconResource = ShortcutIconResource.fromContext(this, iconResource);
    		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIconResource);
        }
        else
        {
        	Bitmap bitmap = BitmapFactory.decodeFile(iconIdentifier);
    		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
        }
				
		setResult(RESULT_OK, intent);
		
		finish();
	}

}
