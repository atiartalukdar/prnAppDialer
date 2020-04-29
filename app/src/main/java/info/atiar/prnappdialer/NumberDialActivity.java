package info.atiar.prnappdialer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

}
