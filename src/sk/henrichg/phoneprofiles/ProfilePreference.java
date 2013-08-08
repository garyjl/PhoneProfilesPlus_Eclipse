package sk.henrichg.phoneprofiles;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ProfilePreference extends Preference {
	
	private long profileId;

	private ImageView profileIcon;

	private Context prefContext;
	
	CharSequence preferenceTitle;
	
	public ProfilePreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		/*
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.ApplicationsPreference);

		// resource, resource_file, file
		imageSource = typedArray.getString(
			R.styleable.ImageViewPreference_imageSource);
		*/
		

		profileId = 0;
		
		prefContext = context;
		
		preferenceTitle = getTitle();
		
		//Log.d("ApplicationsPreference", "title="+preferenceTitle);
		//Log.d("ApplicationsPreference", "imageSource="+imageSource);
		
		setWidgetLayoutResource(R.layout.profile_preference); // resource na layout custom preference - TextView-ImageView
		
		//pedArray.recycle();
		
	}
	
	//@Override
	protected void onBindView(View view)
	{
		super.onBindView(view);

		//Log.d("ApplicationsPreference.onBindView", "packageName="+packageName);
		
		//preferenceTitleView = (TextView)view.findViewById(R.id.applications_pref_label);  // resource na title
		//preferenceTitleView.setText(preferenceTitle);
		
		profileIcon = (ImageView)view.findViewById(R.id.profile_pref_icon); // resource na ImageView v custom preference layoute

	    if (profileIcon != null)
	    {
		    Profile profile = EditorProfilesActivity.profilesDataWrapper.getProfileById(profileId);
		    if (profile != null)
		    {
			    if (profile.getIsIconResourceID())
			    {
			      	profileIcon.setImageResource(0);
			      	int res = prefContext.getResources().getIdentifier(profile.getIconIdentifier(), "drawable", 
			      				prefContext.getPackageName());
			      	profileIcon.setImageResource(res); // resource na ikonu
			    }
			    else
			    {
			      	profileIcon.setImageBitmap(profile._iconBitmap);
			    }
		    	setSummary(profile._name);
		    }
		    else
		    {
		      	profileIcon.setImageResource(0); // resource na ikonu
		    	setSummary("");
		    }
	    }
	}
	
	@Override
	protected void onClick()
	{
		// klik na preference

		//Log.d("ApplicationsPreference.onClick", "packageName="+packageName);

		final ProfilePreferenceDialog dialog = new ProfilePreferenceDialog(prefContext, this);
		dialog.setTitle(R.string.title_activity_profile_preference_dialog);
		dialog.show();
	}
	
	@Override
	protected Object onGetDefaultValue(TypedArray a, int index)
	{
		super.onGetDefaultValue(a, index);
		
		return a.getString(index);  // packageName vratene ako retazec
	}
	
	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
	{
		if (restoreValue) {
			// restore state
			long value = getPersistedLong(profileId);
			profileId = value;
		}
		else {
			// set state
			long value = (Long) defaultValue;
			profileId = value;
			persistLong(value);
		}
	}
	
	@Override
	protected Parcelable onSaveInstanceState()
	{
		// ulozime instance state - napriklad kvoli zmene orientacie
		
		final Parcelable superState = super.onSaveInstanceState();
		if (isPersistent()) {
			// netreba ukladat, je ulozene persistentne
			return superState;
		}
		
		// ulozenie istance state
		final SavedState myState = new SavedState(superState);
		myState.profileId = profileId;
		return myState;
		
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable state)
	{
		if (!state.getClass().equals(SavedState.class)) {
			// Didn't save state for us in onSaveInstanceState
			super.onRestoreInstanceState(state);
			return;
		}
		
		// restore instance state
		SavedState myState = (SavedState)state;
		super.onRestoreInstanceState(myState.getSuperState());
		long value = (Long) myState.profileId;
		profileId = value;
		notifyChanged();
	}
	
	public long getProfileId()
	{
		return profileId;
	}
	
	public void setProfileId(long newProfileId)
	{
		long newValue = newProfileId;

		if (!callChangeListener(newValue)) {
			// nema sa nova hodnota zapisat
			return;
		}

		profileId = newValue;

		// set summary
	    Profile profile = EditorProfilesActivity.profilesDataWrapper.getProfileById(profileId);
	    if (profile != null)
	    {
	    	setSummary(profile._name);
	    }
	    else
	    {
	    	setSummary("");
	    }

		// zapis do preferences
		persistLong(newValue);
		
		// Data sa zmenili,notifikujeme
		notifyChanged();
		
	}
	
	// SavedState class
	private static class SavedState extends BaseSavedState
	{
		long profileId;
		
		public SavedState(Parcel source)
		{
			super(source);
			
			// restore eventType
			profileId = source.readLong();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags)
		{
			super.writeToParcel(dest, flags);
			
			// save eventType
			dest.writeLong(profileId);
		}
		
		public SavedState(Parcelable superState)
		{
			super(superState);
		}
		
		@SuppressWarnings("unused")
		public static final Parcelable.Creator<SavedState> CREATOR =
				new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) 
			{
				return new SavedState(in);
			}
			public SavedState[] newArray(int size)
			{
				return new SavedState[size];
			}
				
		};
	
	}
}
