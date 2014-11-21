package com.min.btclient;

import java.io.OutputStream;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.min.btclient.BluetoothConnector.BluetoothSocketWrapper;

public class CommunicationActivity extends Activity {
	private static final String tag = "CommunicationActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_communication);
		
		findViewById(R.id.button1).setOnClickListener(vBtnClick);
		findViewById(R.id.button2).setOnClickListener(vBtnClick);
		findViewById(R.id.button3).setOnClickListener(vBtnClick);
	}

	View.OnClickListener vBtnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {
				switch(v.getId()) {
					case R.id.button1:
						String str2send = 
								((TextView)findViewById(R.id.editText1)).getText().toString();
						out.write(str2send.getBytes());
						return;
					case R.id.button2:
						out.write(("1"+System.currentTimeMillis()).getBytes());
						return;
					case R.id.button3:
						int hr = 0;
						int min = 0;
						TextView tv = (TextView)findViewById(R.id.editText2);
						try {
							String str = tv.getText().toString();
							String[] strs = str.split(":");
							if(2 > strs.length) {
								hr = 0; min = 0;
							} else {
								hr = Integer.valueOf(strs[0]);	hr %= 24;
								min = Integer.valueOf(strs[1]); min %= 60;
							}
							out.write((String.format("1%d001", 3600*hr+60*min)).getBytes());
							tv.setText(String.format("%d:%02d", hr, min));
						} catch(Throwable e) {
							hr = 0;
							min = 0;
							out.write((String.format("1%d000", 3600*hr+60*min)).getBytes());
							tv.setText(String.format("%d:%02d", hr, min));
						}
						break;
					default:
						break;
				}
			} catch (NullPointerException n) {
				Log.e(tag, "NullPointerException");
			} catch (Throwable e) {
				Log.e(tag, e.getLocalizedMessage());
			}
		}
	};
	
	BluetoothConnector bc;
	BluetoothSocketWrapper bs;
	OutputStream out;
	
	@Override
	protected void onStart() {
		super.onStart();
		
		bc = new BluetoothConnector
				(MainActivity.currDev, true, BluetoothAdapter.getDefaultAdapter(), DetailActivity.uuid_list);
		try {
			bs =  bc.connect();
			out = bs.getOutputStream();
		} catch (NullPointerException n) {
			Log.e(tag, "NullPointerException");
			finish();
		} catch (Throwable e) {
			Log.e(tag, e.getLocalizedMessage());
			finish();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		try {
			bs.close();
		} catch(Throwable e) {}
	}
	
}
