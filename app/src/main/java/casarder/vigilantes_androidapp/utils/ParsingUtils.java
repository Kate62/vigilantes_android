package casarder.vigilantes_androidapp.utils;

import android.widget.Toast;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rita on 11/12/2017.
 */

public class ParsingUtils {

    public static int getCurrentTime() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return (hour * 60) + minute;
    }

    public static String getDataFromJson(String response, String param) {
        String st;
        try {
            JSONObject js = new JSONObject(response);
            st = js.getString(param);
        }catch(Throwable tx) {
            st = null;
        }
        return st;
    }

    public static String hourToString(int minutes, String chara) {
        float hora = minutes / 60f;
        float min = (hora - (int) hora) * 60;
        return String.format("%02d%s%02d",(int)hora,chara,(int)min);
    }

    public static String[] splitString(String str, String sep) {
        return str.split(sep);
    }

}
