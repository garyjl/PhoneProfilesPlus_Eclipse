package sk.henrichg.phoneprofilesplus;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

public class WifiSSIDPreference extends DialogPreference {
	
	private String value;
	public List<WifiSSIDData> SSIDList = null;
	
	WifiManager wifiManager;
	boolean isWifiEnabled = false;
	
	Context context;
	
	private LinearLayout progressLinearLayout;
	private RelativeLayout dataRelativeLayout;
	private EditText SSIDName;
	private Button rescanButton;
	private ListView SSIDListView;
	private WifiSSIDPreferenceAdapter listAdapter;
	
	private AsyncTask<Void, Integer, Void> rescanAsincTask; 
	
    public WifiSSIDPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        this.context = context;
        
        SSIDList = new ArrayList<WifiSSIDData>();
    }

    @Override
    protected View onCreateDialogView() {

        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_wifi_ssid_pref_dialog, null);

        progressLinearLayout = (LinearLayout) view.findViewById(R.id.wifi_ssid_pref_dlg_linla_progress);
        dataRelativeLayout = (RelativeLayout) view.findViewById(R.id.wifi_ssid_pref_dlg_rella_data);
        
        SSIDName = (EditText) view.findViewById(R.id.wifi_ssid_pref_dlg_bt_name);
        SSIDName.setText(value);
        
        rescanButton = (Button) view.findViewById(R.id.wifi_ssid_pref_dlg_rescan);
        rescanButton.setOnClickListener(new View.OnClickListener()
    	{
            public void onClick(View v) {
            	GlobalData.setForceOneWifiScan(context, true);
                refreshListView(true);
            }
        });
        
        SSIDListView = (ListView) view.findViewById(R.id.wifi_ssid_pref_dlg_listview);
        listAdapter = new WifiSSIDPreferenceAdapter(context, this);
        SSIDListView.setAdapter(listAdapter);
        
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        isWifiEnabled = wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
		
        refreshListView(false);
        
		SSIDListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				WifiSSIDPreferenceAdapter.ViewHolder viewHolder = 
						(WifiSSIDPreferenceAdapter.ViewHolder)v.getTag();
				viewHolder.radioBtn.setChecked(true);
            	setSSID(SSIDList.get(position).ssid);
			}

		});
		
        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
    	if (!rescanAsincTask.isCancelled())
    		rescanAsincTask.cancel(true);
    	
    	if (!isWifiEnabled)
    		wifiManager.setWifiEnabled(false);
    	
        if (positiveResult) {

        	SSIDName.clearFocus();
        	value = SSIDName.getText().toString();
        	
    		if (callChangeListener(value))
    		{
	            persistString(value);
    		}
        }
    }
    
    @Override 
    protected Object onGetDefaultValue(TypedArray ta, int index)
    {
		super.onGetDefaultValue(ta, index);
        return ta.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

        if(restoreValue)
        {
            value = getPersistedString(value);
        }
        else
        {
        	value = (String)defaultValue;
            persistString(value);
        }
        
    }    

    public String getSSID()
    {
    	return value;
    }
    
    public void setSSID(String SSID)
    {
    	value = SSID;
    	this.SSIDName.setText(value);
    }
    
    private void refreshListView(boolean forRescan)
    {
    	final boolean _forRescan = forRescan;
    	
    	rescanAsincTask = new AsyncTask<Void, Integer, Void>() {

			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();

				dataRelativeLayout.setVisibility(View.GONE);
				progressLinearLayout.setVisibility(View.VISIBLE);
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				if (!isWifiEnabled)
				{
		    		wifiManager.setWifiEnabled(true);
			        try {
			        	Thread.sleep(1500);
				    } catch (InterruptedException e) {
				        System.out.println(e);
				    }
				}
				
				if (isCancelled())
				{
					if (!isWifiEnabled)
			    		wifiManager.setWifiEnabled(false);
					return null;
				}
				
				SSIDList.clear();
				
				List<WifiConfiguration> wifiConfigurationList = wifiManager.getConfiguredNetworks();
				if (wifiConfigurationList != null)
				{
					for (WifiConfiguration wifiConfiguration : wifiConfigurationList)
					{
			        	SSIDList.add(new WifiSSIDData(wifiConfiguration.SSID.replace("\"", ""), wifiConfiguration.BSSID));
					}
				}
				
				if (_forRescan)
	            	WifiScanAlarmBroadcastReceiver.sendBroadcast(context);
				
		        if (_forRescan)
		        {
		        	for (int i = 0; i < 5 * 10; i++) // 10 seconds for wifi scan
		        	{
		        		if (isCancelled())
		        			break;
		        		
				        try {
				        	Thread.sleep(200);
					    } catch (InterruptedException e) {
					        System.out.println(e);
					    }
			        	if (!GlobalData.getForceOneWifiScan(context))
			        		break;
		        	}
		        	GlobalData.setForceOneWifiScan(context, false);
		        }
		        
				if (isCancelled())
				{
					if (!isWifiEnabled)
			    		wifiManager.setWifiEnabled(false);
					return null;
				}

		        if (WifiScanAlarmBroadcastReceiver.scanResults != null)
		        {
			        for (ScanResult scanResult : WifiScanAlarmBroadcastReceiver.scanResults)
			        {
			        	String ssid = scanResult.SSID.replace("\"", "");
			        	boolean exists = false;
			        	for (WifiSSIDData ssidData : SSIDList)
			        	{
			        		if (ssidData.ssid.equals(ssid))
			        		{
			        			exists = true;
			        			break;
			        		}
			        	}
			        	if (!exists)
			        		SSIDList.add(new WifiSSIDData(ssid, scanResult.BSSID));
			        }
		        }

				if (!isWifiEnabled)
		    		wifiManager.setWifiEnabled(false);
		        
		        return null;
			}
			
			@Override
			protected void onPostExecute(Void result)
			{
				super.onPostExecute(result);

				listAdapter.notifyDataSetChanged();
				progressLinearLayout.setVisibility(View.GONE);
				dataRelativeLayout.setVisibility(View.VISIBLE);
				
				for (int position = 0; position < SSIDList.size()-1; position++)
				{
					if (SSIDList.get(position).ssid.equals(value))
					{
						SSIDListView.setSelection(position);
						SSIDListView.setItemChecked(position, true);
						SSIDListView.smoothScrollToPosition(position);
						break;
					}
				}
			}
			
		};
		
		rescanAsincTask.execute();
    }
    
}