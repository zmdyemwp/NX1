package com.fih.nxbtrunner;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BTStatusReceiver extends BroadcastReceiver {

    private String TAG = "NXBT";
    private Communicator mBTCommunicator;
    public BTStatusReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, " onReceive");
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            switch (bluetoothState)
            {
                case BluetoothAdapter.STATE_CONNECTED:
                    Log.i(TAG," BluetoothAdapter.STATE_CONNECTED");

                    break;
                case BluetoothAdapter.STATE_DISCONNECTED:
                    Log.i(TAG," BluetoothAdapter.STATE_DISCONNECTED");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    if (mBTCommunicator != null)
                        mBTCommunicator.stop();
                    break;
                case BluetoothAdapter.STATE_OFF:
                    Log.i(TAG," BluetoothAdapter.STATE_TURNING_OFF");
                    break;

                case BluetoothAdapter.STATE_ON:
                    Log.i(TAG," BluetoothAdapter.STATE_ON") ;
                    mBTCommunicator =  new Communicator(context);
                    mBTCommunicator.start();
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.i(TAG," BluetoothAdapter.STATE_TURNING_ON") ;
                    break;
                default:
                    break;
            }
        }
    }
}
