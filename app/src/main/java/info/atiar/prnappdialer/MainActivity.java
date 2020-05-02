package info.atiar.prnappdialer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapter.NumbersAdapter;
import adapter.WebsitesAdapter;
import bp.BP;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.NumberModel;
import model.WebsitesModel;

public class MainActivity extends AppCompatActivity {
    final String tag = getClass().getSimpleName() + "Atiar - ";

    @BindView(R.id.websiteLists)    ListView _websiteLists;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    String userId,uniqueKey;

    WebsitesAdapter websitesAdapter;
    private List<WebsitesModel> websiteLists = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Firebase stuff
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("websites");
        userId = auth.getUid();

        websitesAdapter = new WebsitesAdapter(this, websiteLists);
        _websiteLists.setAdapter(websitesAdapter);
        websitesAdapter.notifyDataSetChanged();

        websiteDataFromDB();
    }


    public void visitWebsite(View view) {
        Intent intent = new Intent(MainActivity.this,WebviewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    public void allNumbersButton(View view) {
        startActivity(new Intent(MainActivity.this,NumberDialActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_add_website:
                popUpEditText();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        finish();
    }

    private void popUpEditText() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Comments");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        input.setText("https://www.");

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (input.getText().equals("https://www.")||input.getText().equals("")){
                    Log.e(tag, "No Input added.");
                }else {
                    uniqueKey = mDatabase.push().getKey();
                    //websiteLists.add(new WebsitesModel(input.getText().toString(),4+"",uniqueKey, BP.getCurrentDateTime()));
                    WebsitesModel websitesModel = new WebsitesModel(input.getText().toString(),uniqueKey,userId, BP.getCurrentDateTime());
                    mDatabase.child(uniqueKey).setValue(websitesModel);
                }


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void websiteDataFromDB(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                websiteLists.clear();

                for (DataSnapshot websiteData : dataSnapshot.getChildren()){
                    WebsitesModel websitesModel = websiteData.getValue(WebsitesModel.class);
                    websiteLists.add(websitesModel);
                }
                websitesAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(tag, "Failed to read value.", error.toException());
            }
        });
    }


}
