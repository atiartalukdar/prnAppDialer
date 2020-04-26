package info.atiar.prnappdialer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kaopiz.kprogresshud.KProgressHUD;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.emailED)         EditText _emailED;
    @BindView(R.id.passwordED)      EditText _passwordED;
    @BindView(R.id.passwordTV)      TextView _passwordTV;
    @BindView(R.id.loginButton)     Button _loginBtn;
    @BindView(R.id.forgetPassTV)    TextView _forgetPassTV;

    private FirebaseAuth auth;
    KProgressHUD kProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        //set the view now.
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getSupportActionBar().hide();

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

    }

    public void forgetPassword(View view) {
        if (_forgetPassTV.getTag().toString().toLowerCase().equals("forgetpass")){
            _forgetPassTV.setTag("login");
            _forgetPassTV.setText("Login again");
            _passwordED.setVisibility(View.GONE);
            _passwordTV.setVisibility(View.GONE);
            _loginBtn.setText("Reset");

        }else {
            _forgetPassTV.setTag("forgetpass");
            _forgetPassTV.setText("Forgot your password?");
            _passwordED.setVisibility(View.VISIBLE);
            _passwordTV.setVisibility(View.VISIBLE);
            _loginBtn.setText("Login");
        }
    }

    public void register(View view) {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        if (_loginBtn.getText().toString().toLowerCase().equals("reset")){
            String email = _emailED.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                return;
            }

            kProgressHUD = progressDialog();
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                            }
                            kProgressHUD.dismiss();
                            _forgetPassTV.performClick();
                        }
                    });
        }else {
            String email = _emailED.getText().toString();
            final String password = _passwordED.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }

            kProgressHUD = progressDialog();

            //authenticate user
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            kProgressHUD.dismiss();
                            if (!task.isSuccessful()) {
                                // there was an error
                                if (password.length() < 6) {
                                    _passwordED.setError("Password must be greater then 6 digit");
                                } else {
                                    Toast.makeText(LoginActivity.this, "Authentication failed, check your email and password or sign up", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });


        }
    }

    private KProgressHUD progressDialog(){
      return KProgressHUD.create(LoginActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }
}
