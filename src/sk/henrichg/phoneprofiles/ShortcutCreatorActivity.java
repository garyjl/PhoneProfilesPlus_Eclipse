package sk.henrichg.phoneprofiles;

import java.util.List;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
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
		
		Intent shortcutIntent = new Intent(this, PhoneProfilesActivity.class);
		
		// tu musime spravit nacitanie ikony z profilu + bacha na ikony z sd karty
		ShortcutIconResource iconResource = ShortcutIconResource.fromContext(this, R.drawable.ic_profile_default);
		
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, profile.getName());
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
		
		// PhoneProfilesActivity musi toto testovat, a len spravit aktivaciu profilu
		intent.putExtra("shortcut_profile_id", profile.getID());
		
		setResult(RESULT_OK, intent);
		
		finish();
	}

}
