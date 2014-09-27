package sk.henrichg.phoneprofilesplus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.TypedArray;
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
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

public class BluetoothNamePreference extends DialogPreference {
	
	private String value;
	public List<BluetoothDeviceData> bluetoothList = null;
	
	BluetoothAdapter bluetooth;
	boolean isBluetoothEnabled = false;
	
	Context context;
	
	private LinearLayout progressLinearLayout;
	private RelativeLayout dataRelativeLayout;
	private EditText bluetoothName;
	private Button rescanButton;
	private ListView bluetoothListView;
	private BluetoothNamePreferenceAdapter listAdapter;
	
	private AsyncTask<Void, Integer, Void> rescanAsincTask; 
	
    public BluetoothNamePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        this.context = context;
        
        bluetoothList = new ArrayList<BluetoothDeviceData>();
    }

    @Override
    protected View onCreateDialogView() {

        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_bluetooth_name_pref_dialog, null);

        progressLinearLayout = (LinearLayout) view.findViewById(R.id.bluetooth_name_pref_dlg_linla_progress);
        dataRelativeLayout = (RelativeLayout) view.findViewById(R.id.bluetooth_name_pref_dlg_rella_data);
        
        bluetoothName = (EditText) view.findViewById(R.id.bluetooth_name_pref_dlg_bt_name);
        bluetoothName.setText(value);
        
        rescanButton = (Button) view.findViewById(R.id.bluetooth_name_pref_dlg_rescan);
        rescanButton.setOnClickListener(new View.OnClickListener()
    	{
            public void onClick(View v) {
            	GlobalData.setForceOneBluetoothScan(context, true);
                refreshListView(true);
            }
        });
        
        bluetoothListView = (ListView) view.findViewById(R.id.bluetooth_name_pref_dlg_listview);
        listAdapter = new BluetoothNamePreferenceAdapter(context, this);
        bluetoothListView.setAdapter(listAdapter);
        
		bluetooth = (BluetoothAdapter) BluetoothAdapter.getDefaultAdapter();

        isBluetoothEnabled = bluetooth.isEnabled();
		
        refreshListView(false);
        
		bluetoothListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				BluetoothNamePreferenceAdapter.ViewHolder viewHolder = 
						(BluetoothNamePreferenceAdapter.ViewHolder)v.getTag();
				viewHolder.radioBtn.setChecked(true);
            	setBluetoothName(bluetoothList.get(position).name);
			}

		});
		
        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
    	
    	if (!rescanAsincTask.isCancelled())
    		rescanAsincTask.cancel(true);
    	
    	if (!isBluetoothEnabled)
    		bluetooth.disable();
    	
        if (positiveResult) {

        	bluetoothName.clearFocus();
        	value = bluetoothName.getText().toString();
        	
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

    public String getBluetoothName()
    {
    	return value;
    }
    
    public void setBluetoothName(String bluetoothName)
    {
    	value = bluetoothName;
    	this.bluetoothName.setText(value);
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
				if (!isBluetoothEnabled)
				{
					bluetooth.enable();
			        try {
			        	Thread.sleep(1500);
				    } catch (InterruptedException e) {
				        System.out.println(e);
				    }
				}
				
				if (isCancelled())
				{
					if (!isBluetoothEnabled)
			    		bluetooth.disable();
					return null;
				}
				
				bluetoothList.clear();
				
		        Set<BluetoothDevice> boundedDeviced = bluetooth.getBondedDevices();
		        for (BluetoothDevice device : boundedDeviced)
		        {
		        	bluetoothList.add(new BluetoothDeviceData(device.getName(), device.getAddress()));
		        }
		        
				if (_forRescan)
	            	BluetoothScanAlarmBroadcastReceiver.sendBroadcast(context);
				
		        if (_forRescan)
		        {
		        	for (int i = 0; i < 5 * 20; i++) // 20 seconds for bluetooth scan
		        	{
		        		if (isCancelled())
		        			break;
		        		
				        try {
				        	Thread.sleep(200);
					    } catch (InterruptedException e) {
					        System.out.println(e);
					    }
			        	if (!GlobalData.getForceOneBluetoothScan(context))
			        		break;
		        	}
		        	GlobalData.setForceOneBluetoothScan(context, false);
		        }

				if (isCancelled())
				{
					if (!isBluetoothEnabled)
			    		bluetooth.disable();
					return null;
				}
		        
		        if (BluetoothScanAlarmBroadcastReceiver.scanResults != null)
		        {
			        for (BluetoothDeviceData device : BluetoothScanAlarmBroadcastReceiver.scanResults)
			        {
			        	if (!device.name.isEmpty())
			        	{
				        	boolean exists = false;
				        	for (BluetoothDeviceData _device : bluetoothList)
				        	{
				        		if (_device.name.equals(device.name))
				        		{
				        			exists = true;
				        			break;
				        		}
				        	}
				        	if (!exists)
				        		bluetoothList.add(new BluetoothDeviceData(device.name, device.address));
			        	}
			        }
		        }

				if (!isBluetoothEnabled)
		    		bluetooth.disable();
		        
		        return null;
			}
			
			@Override
			protected void onPostExecute(Void result)
			{
				super.onPostExecute(result);

				listAdapter.notifyDataSetChanged();
				progressLinearLayout.setVisibility(View.GONE);
				dataRelativeLayout.setVisibility(View.VISIBLE);
				
				for (int position = 0; position < bluetoothList.size()-1; position++)
				{
					if (bluetoothList.get(position).name.equals(value))
					{
						bluetoothListView.setSelection(position);
						bluetoothListView.setItemChecked(position, true);
						bluetoothListView.smoothScrollToPosition(position);
						break;
					}
				}
			}
			
		};
		
		rescanAsincTask.execute();
    }
    
}