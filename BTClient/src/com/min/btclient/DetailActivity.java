package com.min.btclient;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.min.btclient.BluetoothConnector.BluetoothSocketWrapper;

public class DetailActivity extends Activity {

	private static final String tag = "UUID_Detail";
	BluetoothDevice dev = null;
	UuidAdapter ua = new UuidAdapter();
	ParcelUuid[] uuids = null;
	ListView lv = null;
	public static ArrayList<UUID> uuid_list;

	class UuidRecv extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			uuids = dev.getUuids();
        	ua.notifyDataSetChanged();
		}
	};
	UuidRecv recv = new UuidRecv();

	private class RfcommConnect extends AsyncTask<Integer,Void,Boolean> {
		@Override
		protected void onPreExecute() {
			lv.setEnabled(false);
		}
		@Override
		protected Boolean doInBackground(Integer... arg0) {
			// TODO Auto-generated method stub
			Boolean result = false;
			try {
				Log.d(tag, "It is going to connect to "+uuids[arg0[0]].toString());
				/*
				BluetoothSocket bsocket = dev.createRfcommSocketToServiceRecord(uuids[arg0[0]].getUuid());
				BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
				bsocket.connect();
				OutputStream out = bsocket.getOutputStream();
				out.write(("BtClient::RfcommTest!"+SystemClock.uptimeMillis()).getBytes());
				bsocket.close();*/
				uuid_list = new ArrayList<UUID>();
				uuid_list.clear();
				uuid_list.add(uuids[arg0[0]].getUuid());
				BluetoothConnector bc = new BluetoothConnector(dev, true, BluetoothAdapter.getDefaultAdapter(), uuid_list);
				BluetoothSocketWrapper bs =  bc.connect();
				OutputStream out = bs.getOutputStream();
				//out.write(("BtClient::RfcommTest!"+SystemClock.uptimeMillis()).getBytes());
				out.write(("1"+System.currentTimeMillis()).getBytes());
				bs.close();
				result = true;
			} catch (NullPointerException n) {
				Log.e(tag, "NullPointerException");
			} catch (Throwable e) {
				Log.e(tag, e.getLocalizedMessage());
			}
			return result;
		}
		@Override
		protected void onPostExecute(Boolean b) {
			Toast.makeText(DetailActivity.this, (b)?"Connected":"NOT Connected", Toast.LENGTH_SHORT).show();
			lv.setEnabled(true);
		}
	};

	AdapterView.OnItemClickListener ll = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if(null != dev && null != uuids) {
				//new RfcommConnect().execute(arg2);
				uuid_list = new ArrayList<UUID>();
				uuid_list.clear();
				uuid_list.add(uuids[arg2].getUuid());
				Intent i = new Intent();
				i.setClass(DetailActivity.this, CommunicationActivity.class);
				startActivity(i);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		//	Register Broadcast Receiver
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_UUID);
		this.registerReceiver(recv, filter);
		//	Set Listview Adapter
		lv = (ListView)findViewById(R.id.listView1);
        lv.setAdapter(ua);
        lv.setOnItemClickListener(ll);
        //	Refresh UUIDs
        dev = MainActivity.currDev;
        this.setTitle(dev.getName());
        dev.fetchUuidsWithSdp();
	}
	@Override
	protected void onDestroy() {
		this.unregisterReceiver(recv);
		super.onDestroy ();
	}
	@Override
	protected void onResume() {
		super.onResume();
		dev.fetchUuidsWithSdp();
	}

	class UuidAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(null == uuids) {
				return 0;
			}
			return uuids.length;
		}
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			if(null == uuids) {
				return null;
			}
			return uuids[arg0];
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
			if(null != uuids) {
				TextView tv1 = (TextView)result.findViewById(R.id.textView1);
				tv1.setText(String.format("#%d#", arg0));
				TextView tv2 = (TextView)result.findViewById(R.id.textView2);
				tv2.setText(uuids[arg0].toString());
			}
			return result;
		}
    }

}
