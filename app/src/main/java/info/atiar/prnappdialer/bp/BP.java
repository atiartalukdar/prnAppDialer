package info.atiar.prnappdialer.bp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BP {

    public static String getCurrentDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }
}
