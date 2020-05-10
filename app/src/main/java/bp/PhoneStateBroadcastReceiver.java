package bp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import model.NumberModel;

public class PhoneStateBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getSimpleName() + "Atiar - ";
    public  boolean prevState = false, currentState = false;
    public  List<NumberModel> numberModels = new ArrayList<>();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            // Outgoing call
            final String outgoingCallNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.i(TAG, outgoingCallNumber);
        }

        else {
            // Incoming call
            String incomingCallNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.i(TAG, incomingCallNumber+"");
        }

        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                Toast.makeText(context, "Ringing State", Toast.LENGTH_SHORT).show();
                //BP.savePreviousState(false);


            }
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
                        //Do something after 100ms
                        Log.e(TAG, "Ideal state");
                        Log.e(TAG,"curr = " + currentState + " prev = "+BP.getPreviousState()+ " size ="+ BP.queue.size());

                        if ( !BP.isSingleNumber && BP.queue.size()>0 && BP.getPreviousState() && currentState  ){
                            BP.callNumberFromNumberDialActivity();
                        }
                    }
                }, 4000);


            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}