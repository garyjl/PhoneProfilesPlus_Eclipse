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
import android.util.Log;
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
	
	private AsyncTask<Void, Integer, Void> rescanAsyncTask; 
	
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
    	if (!rescanAsyncTask.isCancelled())
    		rescanAsyncTask.cancel(true);
    	
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
    	
    	rescanAsyncTask = new AsyncTask<Void, Integer, Void>() {

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
				
				WifiScanAlarmBroadcastReceiver.wifiConfigurationList = wifiManager.getConfiguredNetworks();
				if (WifiScanAlarmBroadcastReceiver.wifiConfigurationList != null)
				{
					for (WifiConfiguration wifiConfiguration : WifiScanAlarmBroadcastReceiver.wifiConfigurationList)
					{
			        	SSIDList.add(new WifiSSIDData(wifiConfiguration.SSID.replace("\"", ""), wifiConfiguration.BSSID));
					}
				}
				
				if (_forRescan)
				{
	            	GlobalData.setForceOneWifiScan(context, true);
	            	//WifiScanAlarmBroadcastReceiver.sendBroadcast(context);
					if (!isWifiEnabled)
						WifiScanAlarmBroadcastReceiver.setWifiEnabledForScan(context, true);
	            	WifiScanAlarmBroadcastReceiver.startScan(context);
				}
				
		        if (_forRescan)
		        {
		        	for (int i = 0; i < 5 * 60; i++) // 60 seconds for wifi scan
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
	            	WifiScanAlarmBroadcastReceiver.setWifiEnabledForScan(context, false);
		        	WifiScanAlarmBroadcastReceiver.setStartScan(context, false);
		        }

				if (!isWifiEnabled)
		    		wifiManager.setWifiEnabled(false);
		        
		        if (WifiScanAlarmBroadcastReceiver.scanResults != null)
		        {
			        for (ScanResult scanResult : WifiScanAlarmBroadcastReceiver.scanResults)
			        {
			        	if (!DataWrapper.getSSID(scanResult).isEmpty())
			        	{
				        	boolean exists = false;
				        	for (WifiSSIDData ssidData : SSIDList)
				        	{
				        		if (DataWrapper.compareSSID(scanResult, ssidData.ssid))
				        		{
				        			exists = true;
				        			break;
				        		}
				        	}
				        	if (!exists)
				        		SSIDList.add(new WifiSSIDData(DataWrapper.getSSID(scanResult), scanResult.BSSID));
			        	}
			        }
		        }

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
		
		rescanAsyncTask.execute();
    }
    
}