package com.fih.nxbtrunner;

import android.app.AlarmManager;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import java.util.TimeZone;

/**
 * Created by kinden on 2014/9/10.
 */
public class MessageHandler {
    private static final String TAG = "MsgHandler";
    private Context mContext;
    public MessageHandler(Context context)
    {
        mContext = context;
    }

    public void parse(byte[] inBuffer, int len)
    {
        Integer MsgType =Integer.parseInt(new String(inBuffer, 0, 1)) ;
        String readMessage = new String(inBuffer, 0, len);

        TimeZone timeZone = TimeZone.getDefault();
        timeZone.getID();
        switch(MsgType)
        {
            case Message.TIME:
                SystemClock.setCurrentTimeMillis(Long.parseLong(readMessage.substring(1)));
                break;
            case Message.TIMEZONE:
                AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                alarm.setTimeZone(readMessage.substring(1));
                Log.d(TAG,"TIMEZONE:"+readMessage.substring(1));
                break;
            case Message.INCOMMINGCALL:
                break;
            case Message.CALLEND:
                break;
            case Message.NOTIFICATION:
                break;
            case Message.FINDDEVICE:
                break;
        }

        //Log.i(TAG, "IN: " + readMessage + " T c: " + System.currentTimeMillis());
        //SystemClock.setCurrentTimeMillis(Long.parseLong(readMessage)) ;
        //Log.i(TAG,"IN: "+readMessage+" T a: "+System.currentTimeMillis());
    }




}
