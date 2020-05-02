package info.atiar.prnappdialer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.karumi.dexter.Dexter;

import java.util.ArrayList;
import java.util.List;

import adapter.NumbersAdapter;
import bp.BP;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.NumberModel;
import model.WebsitesModel;

public class NumberDialActivity extends AppCompatActivity {
    final String tag = getClass().getSimpleName() + "Atiar - ";

    @BindView(R.id.numberListView)    ListView _numberListView;

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    String userId,websiteID;

    NumbersAdapter numbersAdapter;
    private List<NumberModel> numberList = new ArrayList<>();

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
        mDatabase = FirebaseDatabase.getInstance().getReference("allnumbers").child(userId).child(websiteID);

        numbersAdapter = new NumbersAdapter(this, numberList);
        _numberListView.setAdapter(numbersAdapter);

        numbersFromDB();


    }

    private void numbersFromDB(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberList.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    NumberModel numberModel = dataSnapshot1.getValue(NumberModel.class);
                    numberList.add(numberModel);
                }
                numbersAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(tag, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void removeItem(int position){

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
                                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                    appleSnapshot.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(tag, "onCancelled", databaseError.toException());
                            }
                        });                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what should happen when negative button is clicked
                        Toast.makeText(getApplicationContext(),"Nothing Happened",Toast.LENGTH_LONG).show();
                    }
                })
                .show();

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(NumberDialActivity.this,MainActivity.class));
        finish();
    }
}
