package info.atiar.prnappdialer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.emailED)         EditText _emailED;
    @BindView(R.id.passwordED)      EditText _passwordED;
    @BindView(R.id.passwordTV)      TextView _passwordTV;
    @BindView(R.id.loginButton)     Button _loginBtn;
    @BindView(R.id.forgetPassTV)    TextView _forgetPassTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
    }


    public void forgetPassword(View view) {
        if (_forgetPassTV.getTag().toString().toLowerCase().equals("forgetpass")){
            _forgetPassTV.setTag("login");
            _forgetPassTV.setText("Login again");
            _passwordED.setVisibility(View.GONE);
            _passwordTV.setVisibility(View.GONE);
            _loginBtn.setText("Rest");
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
}
