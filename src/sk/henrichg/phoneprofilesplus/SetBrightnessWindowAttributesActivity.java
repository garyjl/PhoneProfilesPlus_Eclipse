package sk.henrichg.phoneprofilesplus;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.WindowManager;

public class SetBrightnessWindowAttributesActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    refreshBrightness();

	    Thread t = new Thread() {
	        public void run() {
	            try {
	                sleep(10);
	            } catch (InterruptedException e) {
	            }
	            finish();
	        }
	    };
	    t.start();
	}

	private void refreshBrightness()
	{
		int mode = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
		int brightness = 128;
		try {
			mode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
			brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    WindowManager.LayoutParams lp = getWindow().getAttributes();
	    if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
	        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
	    } else {
	        lp.screenBrightness = brightness / 255.0f;
	    }
	    getWindow().setAttributes(lp);
	}

}
