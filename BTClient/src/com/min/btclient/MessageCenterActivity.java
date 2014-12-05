package com.min.btclient;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.TimeZone;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MessageCenterActivity extends Activity {

	private static final String BT_CLIENT_MESSAGE_CENTER_SHARED_PREFERENCES = "BT_CLIENT_MESSAGE_CENTER_SHARED_PREFERENCES";
	private static final String SHARED_PREFERECES_EDITTEXT = "SHARED_PREFERECES_EDITTEXT";
	BluetoothReflect btreflect;
	SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.message_center);
		
		if(null == MainActivity.currDev) {
			finish();
			return;
		}
		sp = this.getSharedPreferences(BT_CLIENT_MESSAGE_CENTER_SHARED_PREFERENCES, 0);
		btreflect = new BluetoothReflect(MainActivity.currDev);
		
		findViewById(R.id.button1).setOnClickListener(doClick);
		findViewById(R.id.button2).setOnClickListener(doClick);
		findViewById(R.id.button3).setOnClickListener(doClick);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		((TextView)findViewById(R.id.editText1)).setText(sp.getString(SHARED_PREFERECES_EDITTEXT, ""));
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(false == btreflect.BtReflectConnect(1)) {
			finish();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		btreflect.BtReflectDisconnect();
	}
	
	View.OnClickListener doClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String msg = "";
			switch(v.getId()) {
			case R.id.button1:
				msg = "1"+System.currentTimeMillis();
				break;
			case R.id.button2:
				msg = "2"+TimeZone.getDefault().getID();
				break;
			case R.id.button3:
				msg = ((TextView)findViewById(R.id.editText1)).getText().toString();
				sp.edit().putString(SHARED_PREFERECES_EDITTEXT, msg).apply();
				break;
			default:
				break;
			}
			if(0 < msg.length()) {
				btreflect.BtReflectWrite(msg);
			}
		}
	};
	
	/**
	 * class BluetoothReflect
	 * methods
	 * 		BtReflectConnect
	 * 		BtReflectDisconnect
	 */
	private class BluetoothReflect {
		private static final String TAG = "BluetoothReflect";
		BluetoothDevice dev = null;
		BluetoothSocket bsocket = null;
		OutputStream out = null;
		
		public BluetoothReflect(BluetoothDevice d) {
			dev = d;
		}
		
		public boolean BtReflectConnect(int channel) {
			boolean result = false;
			bsocket = null;
			try {
				Method method = dev.getClass()
						.getMethod("createRfcommSocket", new Class[] { int.class });
				try {
					bsocket = (BluetoothSocket) method.invoke(dev, new Object[] { 1 });
					bsocket.connect();
					out = bsocket.getOutputStream();
					result = true;
				} catch (Exception e) {
					Log.d(TAG, e.toString());
				}
			} catch (Exception e) {
				Log.d(TAG, e.toString());
			}
			return result;
		}
		
		public void BtReflectDisconnect() {
			if(null != bsocket) {
				try {
					bsocket.close();
					bsocket = null;
				} catch(Exception e) {
					Log.d(TAG, e.toString());
				}
			}
		}
		
		public void BtReflectWrite(String msg) {
			try {
				out.write(msg.getBytes());
				Log.d(TAG, "BtReflectWrite::"+msg);
			} catch(Exception e) {
				Log.d(TAG, e.toString());
			}
		}
	}
}
