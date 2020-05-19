package bp;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.atiar.prnappdialer.NumberDialActivity;
import model.NumberModel;

public class PhoneStateBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = getClass().getSimpleName() + "Atiar - ";
    public boolean currentState = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            // Outgoing call
            /*final String outgoingCallNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.e(TAG, outgoingCallNumber);*/
        }

        else {
            // Incoming call
           /* String incomingCallNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.e(TAG, incomingCallNumber+"");*/
        }

        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            /*if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                Toast.makeText(context, "Ringing State", Toast.LENGTH_SHORT).show();
                //BP.savePreviousState(false);

            }*/
            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                Toast.makeText(context, "Received State", Toast.LENGTH_SHORT).show();
                BP.savePreviousState(true);
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                currentState=true;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Log.e(TAG, "Ideal state");
                        //Log.e(TAG,"curr = " + currentState + " prev = "+BP.getPreviousState()+ " size ="+ BP.queue.size());
                        LastCall();
                        if ( !BP.isSingleNumber && BP.queue.size()>0 && BP.getPreviousState() && currentState  ){
                            BP.callNumberFromNumberDialActivity();
                        }else {
                            BP.isCallRunning = false;
                        }
                    }
                }, 4000);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private int getCallduration(String callDuration){
        try{
            return Integer.parseInt(callDuration);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public void LastCall() {
        String callDura = "0";
        StringBuffer sb = new StringBuffer();
        Uri contacts = CallLog.Calls.CONTENT_URI;
        if (ActivityCompat.checkSelfPermission(NumberDialActivity.context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            callDura = null;
        }
        else {
            Cursor managedCursor = NumberDialActivity.context.getApplicationContext().getContentResolver().query(
                    contacts, null, null, null, android.provider.CallLog.Calls.DATE + " DESC limit 1;");
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int duration1 = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            if (managedCursor.moveToFirst() == true) {
                String phNumber = managedCursor.getString(number);
                callDura = managedCursor.getString(duration1);
                String callDate = managedCursor.getString(date);
                String callDayTime = new Date(Long.valueOf(callDate)).toString();
                String dir = null;
                if (BP.isCallFromApp && getCallduration(callDura)>0){
                    NumberModel numberModel = new NumberModel(NumberDialActivity.websiteID,phNumber,callDura,NumberDialActivity.userId,BP.getCurrentDateTime());
                    NumberDialActivity.updatePhoneNumberStatus(numberModel);
                }
                BP.isCallFromApp = false;
                Log.e("DUR", "\nPhone Number:--- " + phNumber + " \nCall duration in sec :--- " + callDura + " \nCall Date in sec :--- " + callDayTime);
            }
            managedCursor.close();
        }
    }
}