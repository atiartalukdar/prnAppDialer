package info.atiar.prnappdialer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import adapter.NumbersAdapter;
import bp.BP;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.NumberModel;
import model.WebsitesModel;

// https://stackoverflow.com/questions/30521487/how-to-get-the-callers-number

public class NumberDialActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName() + "Atiar - ";
    public static Context context;
    @BindView(R.id.numberListView)
    ListView _numberListView;
    KProgressHUD kProgressHUD;

    private static DatabaseReference mDatabase;
    private FirebaseAuth auth;
    public static String userId, websiteID;

    NumbersAdapter numbersAdapter;
    private List<NumberModel> numberList = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        context  = NumberDialActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_dial);
        ButterKnife.bind(this);
        websiteID = getIntent().getStringExtra("websiteID");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Firebase stuff
        auth = FirebaseAuth.getInstance();
        userId = auth.getUid();
        //mDatabase = FirebaseDatabase.getInstance().getReference("allnumbers").child(userId).child(websiteID);
        mDatabase = FirebaseDatabase.getInstance().getReference("allnumbers").child(userId).child(websiteID);

        numbersAdapter = new NumbersAdapter(this, numberList);
        _numberListView.setAdapter(numbersAdapter);
        numbersFromDB();

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.WRITE_CALL_LOG)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();

    }

    private void numbersFromDB() {
        mDatabase = FirebaseDatabase.getInstance().getReference("allnumbers").child(userId).child(websiteID);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberList.clear();
                if (!BP.isCallRunning){
                    BP.queue.clear();
                }

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    NumberModel numberModel = dataSnapshot1.getValue(NumberModel.class);
                    numberList.add(numberModel);
                    if (!BP.isCallRunning){
                        BP.queue.add(numberModel.getNumber());
                    }
                }
                numbersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.call, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_call_all:
                callAllTheNumbers();
                break;
            case R.id.menu_sms:
                smsAllTheNumbers();
                break;
            case R.id.menu_delete_all:
                deleteAllNumbers();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int mMessageSentParts;
    private int mMessageSentTotalParts;
    private int mMessageSentCount;
    String message = "test message";
    private void smsAllTheNumbers() {
        kProgressHUD.create(NumberDialActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Sending SMS")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        registerBroadCastReceivers();

        mMessageSentCount = 0;
        sendSMS(numberList.get(mMessageSentCount).getNumber(), message);
    }

    private void deleteAllNumbers() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure to Delete All the numbers?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        mDatabase = FirebaseDatabase.getInstance().getReference("allnumbers").child(userId).child(websiteID);
                        mDatabase.removeValue();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what should happen when negative button is clicked
                        Toast.makeText(getApplicationContext(), "Nothing Happened", Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }

    private void callAllTheNumbers() {
        BP.isSingleNumber  = false;
        BP.isCallRunning = true;
        BP.callNumberFromNumberDialActivity(NumberDialActivity.this);
    }

    public void removeItem(int position) {
        mDatabase = FirebaseDatabase.getInstance().getReference("allnumbers").child(userId).child(websiteID);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure to Delete this number?")
                .setMessage(numberList.get(position).getNumber())
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        Query applesQuery = mDatabase.orderByChild("number").equalTo(numberList.get(position).number);
                        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                    if (appleSnapshot != null){
                                        appleSnapshot.getRef().removeValue();
                                    }
                                }
                                numbersAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what should happen when negative button is clicked
                        Toast.makeText(getApplicationContext(), "Nothing Happened", Toast.LENGTH_LONG).show();
                    }
                })
                .show();

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(NumberDialActivity.this, MainActivity.class));
        finish();
    }

    public static void updatePhoneNumberStatus(NumberModel numberModel){
        mDatabase = FirebaseDatabase.getInstance().getReference("allnumbers").child(userId).child(websiteID);
        mDatabase.child(numberModel.getNumber()).setValue(numberModel);
    }

    private void sendNextMessage(){
        if(thereAreSmsToSend()){
            sendSMS(numberList.get(mMessageSentCount).getNumber(), message);
        }else{
            kProgressHUD.dismiss();
            Toast.makeText(getBaseContext(), "All SMS have been sent",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean thereAreSmsToSend(){
        return mMessageSentCount < numberList.size();
    }

    String SENT = "SMS_SENT";

    String DELIVERED = "SMS_DELIVERED";
    private void sendSMS(final String phoneNumber, String message) {

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);
        mMessageSentTotalParts = parts.size();

        Log.i("Message Count", "Message Count: " + mMessageSentTotalParts);

        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        for (int j = 0; j < mMessageSentTotalParts; j++) {
            sentIntents.add(sentPI);
            deliveryIntents.add(deliveredPI);
        }

        mMessageSentParts = 0;
        sms.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveryIntents);
    }

    private void registerBroadCastReceivers(){
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:

                        mMessageSentParts++;
                        if ( mMessageSentParts == mMessageSentTotalParts ) {
                            mMessageSentCount++;
                            sendNextMessage();
                        }

                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {

                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "SMS delivered");
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

    }
}
