package sk.henrichg.phoneprofilesplus;

import java.text.Collator;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;

public class GUIData {

	public static BrightnessView brightneesView = null;
	
	public static Collator collator = null;
	
	// import/export
	public static final String DB_FILEPATH = "/data/" + GlobalData.PACKAGE_NAME + "/databases";
	public static final String REMOTE_EXPORT_PATH = "/PhoneProfiles";
	public static final String EXPORT_APP_PREF_FILENAME = "ApplicationPreferences.backup";
	public static final String EXPORT_DEF_PROFILE_PREF_FILENAME = "DefaultProfilePreferences.backup";

	public static void setLanguage(Context context)//, boolean restart)
	{
		// jazyk na aky zmenit
		String lang = GlobalData.applicationLanguage;
		
		//Log.d("EditorProfilesActivity.setLanguauge", lang);

		Locale appLocale;
		
		if (!lang.equals("system"))
		{
			appLocale = new Locale(lang);
		}
		else
		{
			appLocale = Resources.getSystem().getConfiguration().locale;
		}
		
		Locale.setDefault(appLocale);
		Configuration appConfig = new Configuration();
		appConfig.locale = appLocale;
		context.getResources().updateConfiguration(appConfig, context.getResources().getDisplayMetrics());
		
		// collator for application locale sorting
		collator = getCollator();
		
		//languageChanged = restart;
	}
	
	public static Collator getCollator()
	{
		// get application Locale
		String lang = GlobalData.applicationLanguage;
		Locale appLocale;
		if (!lang.equals("system"))
		{
			appLocale = new Locale(lang);
		}
		else
		{
			appLocale = Resources.getSystem().getConfiguration().locale;
		}

		// get collator for application locale
		return Collator.getInstance(appLocale);
	}
	
	public static void setTheme(Activity activity, boolean forPopup, boolean withToolbar)
	{
		if (GlobalData.applicationTheme.equals("material"))
		{
			//Log.d("EditorProfilesActivity.setTheme","material");
			if (forPopup)
			{
				if (withToolbar)
					activity.setTheme(R.style.PopupTheme_withToolbar_material);
				else
					activity.setTheme(R.style.PopupTheme_material);
			}
			else
			{
				if (withToolbar)
					activity.setTheme(R.style.Theme_Phoneprofilestheme_withToolbar_material);
				else
					activity.setTheme(R.style.Theme_Phoneprofilestheme_material);
			}
		}
		else
		if (GlobalData.applicationTheme.equals("dark"))
		{
			//Log.d("EditorProfilesActivity.setTheme","dark");
			if (forPopup)
			{
				if (withToolbar)
					activity.setTheme(R.style.PopupTheme_withToolbar_dark);
				else
					activity.setTheme(R.style.PopupTheme_dark);
			}
			else
			{
				if (withToolbar)
					activity.setTheme(R.style.Theme_Phoneprofilestheme_withToolbar_dark);
				else
					activity.setTheme(R.style.Theme_Phoneprofilestheme_dark);
			}
		}
		else
		if (GlobalData.applicationTheme.equals("dlight"))
		{
			//Log.d("EditorProfilesActivity.setTheme","dark");
			if (forPopup)
			{
				if (withToolbar)
					activity.setTheme(R.style.PopupTheme_withToolbar_dlight);
				else
					activity.setTheme(R.style.PopupTheme_dlight);
			}
			else
			{
				if (withToolbar)
					activity.setTheme(R.style.Theme_Phoneprofilestheme_withToolbar_dlight);
				else
					activity.setTheme(R.style.Theme_Phoneprofilestheme_dlight);
			}
		}
	}
	
	public static void reloadActivity(Activity activity, boolean newIntent)
	{
		if (newIntent)
		{
		    Intent intent = activity.getIntent();
		    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		    activity.finish();
		    activity.startActivity(intent);
		    activity.overridePendingTransition(0, 0);
		}
		else
			activity.recreate();
	}
	
}
