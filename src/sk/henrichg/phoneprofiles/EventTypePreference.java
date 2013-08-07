package sk.henrichg.phoneprofiles;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class EventTypePreference extends Preference {
	
	private int eventType;

	private ImageView eventTypeIcon;

	private Context prefContext;
	
	CharSequence preferenceTitle;
	
	public EventTypePreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		/*
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.ApplicationsPreference);

		// resource, resource_file, file
		imageSource = typedArray.getString(
			R.styleable.ImageViewPreference_imageSource);
		*/
		

		eventType = Event.ETYPE_TIME_RANGE;
		
		prefContext = context;
		
		preferenceTitle = getTitle();
		
		//Log.d("ApplicationsPreference", "title="+preferenceTitle);
		//Log.d("ApplicationsPreference", "imageSource="+imageSource);
		
		setWidgetLayoutResource(R.layout.event_type_preference); // resource na layout custom preference - TextView-ImageView
		
		//pedArray.recycle();
		
	}
	
	//@Override
	protected void onBindView(View view)
	{
		super.onBindView(view);

		//Log.d("ApplicationsPreference.onBindView", "packageName="+packageName);
		
		//preferenceTitleView = (TextView)view.findViewById(R.id.applications_pref_label);  // resource na title
		//preferenceTitleView.setText(preferenceTitle);
		
		eventTypeIcon = (ImageView)view.findViewById(R.id.event_type_pref_icon); // resource na ImageView v custom preference layoute

	    if (eventTypeIcon != null)
	    {
	    	eventTypeIcon.setImageResource(EventTypePreferenceAdapter.eventTypeIconIds[eventType]);
	    	setSummary(EventTypePreferenceAdapter.eventTypeNameIds[eventType]);
	    }
	}
	
	@Override
	protected void onClick()
	{
		// klik na preference

		//Log.d("ApplicationsPreference.onClick", "packageName="+packageName);

		final EventTypePreferenceDialog dialog = new EventTypePreferenceDialog(prefContext, this);
		dialog.setTitle(R.string.title_activity_event_type_preference_dialog);
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
			int value = getPersistedInt(eventType);
			eventType = value;
		}
		else {
			// set state
			int value = (Integer) defaultValue;
			eventType = value;
			persistInt(value);
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
		myState.eventType = eventType;
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
		int value = (Integer) myState.eventType;
		eventType = value;
		notifyChanged();
	}
	
	public int getEventType()
	{
		return eventType;
	}
	
	public void setEventType(int newEventType)
	{
		int newValue = newEventType;

		if (!callChangeListener(newValue)) {
			// nema sa nova hodnota zapisat
			return;
		}

		eventType = newValue;

		// set summary
    	setSummary(EventTypePreferenceAdapter.eventTypeNameIds[eventType]);

		// zapis do preferences
		persistInt(newValue);
		
		// Data sa zmenili,notifikujeme
		notifyChanged();
		
	}
	
	// SavedState class
	private static class SavedState extends BaseSavedState
	{
		int eventType;
		
		public SavedState(Parcel source)
		{
			super(source);
			
			// restore eventType
			eventType = source.readInt();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags)
		{
			super.writeToParcel(dest, flags);
			
			// save eventType
			dest.writeInt(eventType);
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
