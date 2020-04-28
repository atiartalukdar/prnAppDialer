package info.atiar.prnappdialer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import model.UserModel;

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.emailED)         EditText _emailED;
    @BindView(R.id.fullNameED)      EditText _fullNameED;
    @BindView(R.id.passwordED)      EditText _passwordED;
    @BindView(R.id.passwordED1)     EditText _passwordED1;


    FirebaseAuth mAuth;
    DatabaseReference mdatabase;
    String Name,Email,Password, Password1;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        getSupportActionBar().hide();

        // for authentication using FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    public void signup(View view) {
        UserRegister();
    }

    public void backToLogin(View view) {
        onBackPressed();
    }

    private void UserRegister() {
        Name = _fullNameED.getText().toString().trim();
        Email = _emailED.getText().toString().trim();
        Password = _passwordED.getText().toString().trim();
        Password1 = _passwordED1.getText().toString().trim();

        if (TextUtils.isEmpty(Name)){
            Toast.makeText(RegisterActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(Email)){
            Toast.makeText(RegisterActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(Password)){
            Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }else if (Password.length()<6) {
            Toast.makeText(RegisterActivity.this, "Password must be greater then 6 digit", Toast.LENGTH_SHORT).show();
            return;
        }else if (!Password.equals(Password1)){
            Toast.makeText(RegisterActivity.this, "Pass didn't match. Check again", Toast.LENGTH_SHORT).show();
        }
        mDialog.setMessage("Creating UserModel please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mDialog.dismiss();

                if (!task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this,"Error on creating user - " + task.getException(),Toast.LENGTH_SHORT).show();

                }else{
                    sendEmailVerification();
                    OnAuth(task.getResult().getUser());
                    mAuth.signOut();
                    onBackPressed();
                }
            }
        });
    }

    //Email verification code using FirebaseUser object and using isSucccessful()function.
    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this,"Check your email for verification",Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
        }
    }

    private void OnAuth(FirebaseUser user) {
        createAnewUser(user.getUid());
    }

    private void createAnewUser(String uid) {
        UserModel user = BuildNewuser();
        mdatabase.child(uid).setValue(user);
    }

    private UserModel BuildNewuser(){
        return new UserModel(
                getFullname(),
                getUserEmail(),
                new Date().getTime()
        );
    }

    public String getFullname() {
        return Name;
    }

    public String getUserEmail() {
        return Email;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDialog.dismiss();
    }


}
