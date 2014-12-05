package com.min.btclient;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
		
		ps = this.getSharedPreferences(psName, 0);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		((TextView)findViewById(R.id.editText1)).setText(ps.getString(pssEditText, ""));
		((TextView)findViewById(R.id.editText2)).setText(ps.getString(pssTimeText, ""));
	}
	
	final static private String psName = "BT_CLIENT_PS_NAME";
	final static private String pssEditText = "BT_CLIENT_PS_STRING_EDITTEXT";
	final static private String pssTimeText = "BT_CLIENT_PS_TIME_TEXT";
	SharedPreferences ps;

	View.OnClickListener vBtnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {
				switch(v.getId()) {
					case R.id.button1:
						String str2send = 
								((TextView)findViewById(R.id.editText1)).getText().toString();
						ps.edit().putString(pssEditText, str2send).apply();
						out.write(str2send.getBytes());
						return;
					case R.id.button2:
						out.write(("1"+System.currentTimeMillis()).getBytes());
						return;
					case R.id.button3:
						int hr = 0;
						int min = 0;
						TextView tv = (TextView)findViewById(R.id.editText2);
						String time;
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
							time = String.format("%d:%02d", hr, min);
							tv.setText(time);
						} catch(Throwable e) {
							hr = 0;
							min = 0;
							out.write((String.format("1%d000", 3600*hr+60*min)).getBytes());
							time = String.format("%d:%02d", hr, min);
							tv.setText(time);
						}
						ps.edit().putString(pssTimeText, time).apply();
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
	void BluetoothConnectorConnect() {
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
	void BluetoothConnectorClose() {
		try {
			bs.close();
		} catch(Throwable e) {}
	}
	
	OutputStream out;
	BluetoothSocket bsocket;
	void BluetoothReflectConnect() throws Exception {
		try {
			BluetoothDevice dev = MainActivity.currDev; 
			Method method = dev.getClass()
					.getMethod("createRfcommSocket", new Class[] { int.class });
			try {
				bsocket = (BluetoothSocket) method.invoke(dev, new Object[] { 1 });
				//Thread.sleep(500);
				Log.d("BluetoothReflectConnect", "Before connect()");
				bsocket.connect();
				Log.d("BluetoothReflectConnect", "After connect()");
				out = bsocket.getOutputStream();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException io) {
				io.printStackTrace();
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			finish();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			finish();
		}
	}
	
	void BluetoothReflectClose() {
		try {
			bsocket.close();
		} catch(Throwable e) {}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		//BluetoothConnectorConnect()
		try {
			BluetoothReflectConnect();
		} catch(Exception e) {}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		//BluetoothConnectorClose();
		BluetoothReflectClose();
	}
	
}
