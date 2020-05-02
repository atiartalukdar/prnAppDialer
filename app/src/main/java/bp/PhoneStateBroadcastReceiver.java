package bp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
public class PhoneStateBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getSimpleName() + "Atiar - ";

    Context mContext;
    String incoming_number;
    private int prev_state;

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); //TelephonyManager object
        CustomPhoneStateListener customPhoneListener = new CustomPhoneStateListener();
        telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE); //Register our listener with TelephonyManager

        Bundle bundle = intent.getExtras();
        String phoneNr = bundle.getString("outgoing_number");
        Log.v(TAG, "phoneNr: "+phoneNr);
        mContext = context;
    }

    /* Custom PhoneStateListener */
    public class CustomPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber){

            if( incomingNumber != null && incomingNumber.length() > 0 )
                incoming_number = incomingNumber;

            switch(state){
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.e(TAG, "CALL_STATE_RINGING");
                    prev_state=state;
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.e(TAG, "CALL_STATE_OFFHOOK");
                    prev_state=state;
                    break;

                case TelephonyManager.CALL_STATE_IDLE:

                    Log.e(TAG, "CALL_STATE_IDLE==>"+incoming_number);

                    if((prev_state == TelephonyManager.CALL_STATE_OFFHOOK)){
                        prev_state=state;
                        //Answered Call which is ended
                    }
                    if((prev_state == TelephonyManager.CALL_STATE_RINGING)){
                        prev_state=state;
                        //Rejected or Missed call
                    }
                    break;
            }
        }
    }
}