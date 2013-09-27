package sk.henrichg.phoneprofiles;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

public class GUIData {

	public static boolean applicationStarted = false;
	
	static final String PROFILE_ICON_DEFAULT = "ic_profile_default";

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
		
		//languageChanged = restart;
	}
	
	public static void setTheme(Activity activity, boolean forPopup)
	{
		if (GlobalData.applicationTheme.equals("light"))
		{
			//Log.d("EditorProfilesActivity.setTheme","light");
			if (forPopup)
				activity.setTheme(R.style.PopupTheme);
			else
				activity.setTheme(R.style.Theme_Phoneprofilestheme);
		}
		else
		if (GlobalData.applicationTheme.equals("dark"))
		{
			//Log.d("EditorProfilesActivity.setTheme","dark");
			if (forPopup)
				activity.setTheme(R.style.PopupTheme_dark);
			else
				activity.setTheme(R.style.Theme_Phoneprofilestheme_dark);
		}
		else
		if (GlobalData.applicationTheme.equals("dlight"))
		{
			//Log.d("EditorProfilesActivity.setTheme","dark");
			if (forPopup)
				activity.setTheme(R.style.PopupTheme_dlight);
			else
				activity.setTheme(R.style.Theme_Phoneprofilestheme_dlight);
		}
	}
	
}
