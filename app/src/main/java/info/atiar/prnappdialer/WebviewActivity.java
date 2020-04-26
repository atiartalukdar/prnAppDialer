package info.atiar.prnappdialer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.Date;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.atiar.prnappdialer.bp.BP;
import info.atiar.prnappdialer.model.NumberStoringModel;

public class WebviewActivity extends AppCompatActivity {
    @BindView(R.id.addNumberEd)         EditText _addNumberEd;
    @BindView(R.id.addNumberButton)     ImageButton _addNumberButton;
    @BindView(R.id.webview)             WebView _webview;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;


    String linkToOpen = "https://www.callingreport.net/";
    String MyUA = "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 5 Build/LMY48B; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/43.0.2357.65 Mobile Safari/537.36";
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;
    Context mContext;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        mContext = this;
        renderWebPage(linkToOpen);

        //Firebase stuff
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("numbers");
        userId = auth.getUid();

    }

    public void addNumber(View view) {
        if (!_addNumberEd.getText().equals("") || _addNumberEd.getText().equals(null)){
            NumberStoringModel numberStoringModel = new NumberStoringModel(linkToOpen,_addNumberEd.getText().toString(), BP.getCurrentDateTime());
            mDatabase.child(userId).child(BP.getCurrentDateTime()).setValue(numberStoringModel);
        }
    }

    // Custom method to render a web page
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void renderWebPage(String urlToRender){
        //WEBVIEW

        _webview.getSettings().setJavaScriptEnabled(true); // enable javascript
        _webview.getSettings().setUserAgentString(MyUA);
        _webview.getSettings().setDomStorageEnabled(true);
        _webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        _webview.getSettings().setLoadsImagesAutomatically(true);
        _webview.getSettings().setAppCacheEnabled(true);
        _webview.getSettings().setAppCachePath(getApplication().getCacheDir().toString());
        _webview.setWebViewClient(new WebViewClient(){


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url){
                // Do something when page loading finished
                _webview.setVisibility(View.VISIBLE);
            }

        });

        _webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        _webview.loadUrl(urlToRender);
        //mWebview.reload();
    }

    @Override
    public void onBackPressed() {
        if(_webview.canGoBack()) {
            _webview.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
