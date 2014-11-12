package sk.henrichg.phoneprofilesplus;
import android.app.IntentService;
import android.content.Intent;


public class GrantRootService extends IntentService {

	public GrantRootService()
	{
		super("GrantRootService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		if (GlobalData.isRooted(false))
		{
			if (GlobalData.grantRoot(true))
			{
				GlobalData.settingsBinaryExists();
				GlobalData.getSUVersion();
			}
		}
	}

}
