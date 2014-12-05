package com.min.btclient;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends Activity {

	private static final String tag = "BTClient";
	BluetoothAdapter ba;
	ArrayList<BluetoothDevice> devs = new ArrayList<BluetoothDevice>();
	DevAdapter da;
	static public BluetoothDevice currDev = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ba = BluetoothAdapter.getDefaultAdapter();
        devs.clear();
        da = new DevAdapter();
        ListView lv = (ListView)findViewById(R.id.listView1);
        lv.setAdapter(da);
        
        AdapterView.OnItemClickListener ll = new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				currDev = devs.get(arg2);
				Intent i = new Intent();
				boolean oldMode = false;
				if(oldMode) {
					i.setClass(MainActivity.this, DetailActivity.class);
				} else {
					i.setClass(MainActivity.this, MessageCenterActivity.class);
				}
				MainActivity.this.startActivity(i);
			}
        	
        };
        lv.setOnItemClickListener(ll);
        
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				refreshBondDev();
			}
		});
        
    }
    
    void refreshBondDev() {
    	devs = new ArrayList<BluetoothDevice>(ba.getBondedDevices());
    	da.notifyDataSetChanged();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	refreshBondDev();
    }

    class DevAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return devs.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return devs.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			View result = arg1;
			if(null == result) {
				result = getLayoutInflater().inflate(R.layout.view_item, null);
			}
			TextView tv1 = (TextView)result.findViewById(R.id.textView1);
			tv1.setText(devs.get(arg0).getName());
			TextView tv2 = (TextView)result.findViewById(R.id.textView2);
			tv2.setText(devs.get(arg0).getAddress());
			return result;
		}
    }
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
