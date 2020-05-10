package bp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import info.atiar.prnappdialer.NumberDialActivity;

public class BP {
    private static final String PREFS_NAME = "pref";
    public static Activity numberDialActivity;
    public static Queue<String> queue = new LinkedList<>();
    public static boolean isSingleNumber = false;

    public static String getCurrentDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    public static void openDialPad(Activity activity, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        activity.startActivity(intent);
    }

    public static void callNumber(Activity activity, String phoneNumber) {
        numberDialActivity = activity;

        Intent intent = new Intent(Intent.ACTION_CALL);

        intent.setData(Uri.parse("tel:" + phoneNumber));
        activity.startActivity(intent);
    }

    public static void callNumberFromNumberDialActivity() {
        callNumber(numberDialActivity,queue.remove());
    }

    public static void callNumberFromNumberDialActivity(Activity activity) {
        numberDialActivity = activity;
        try {
            callNumber(numberDialActivity,queue.remove());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void savePreviousState(Boolean previousState){
            setPreference("prev",previousState);
    }

    public static boolean getPreviousState(){
            return getPreference("prev");
    }

    //SharedPreferences
    private static boolean setPreference(String key, boolean value) {
        SharedPreferences settings = MyApplication.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    private static boolean getPreference(String key) {
        SharedPreferences settings = MyApplication.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(key, false);
    }

    private static void removeSingleItem(String keyToRemove) {
        SharedPreferences settings = MyApplication.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        settings.edit().remove(keyToRemove).commit();
    }

    private static void removeAllItem() {
        SharedPreferences settings = MyApplication.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        settings.edit().clear().commit();
    }

}
