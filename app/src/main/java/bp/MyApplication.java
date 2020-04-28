package bp;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }




}