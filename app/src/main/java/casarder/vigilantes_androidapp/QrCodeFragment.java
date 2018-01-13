package casarder.vigilantes_androidapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import casarder.vigilantes_androidapp.utils.ParsingUtils;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class QrCodeFragment extends Fragment implements ZXingScannerView.ResultHandler {

    private static int CAMERA_CODE = 1222;
    private ZXingScannerView scannerView;
    private QrCodeFragment instance;

    private String url = "https://casarder.azurewebsites.net/";

    public QrCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        instance = this;
        scannerView = new ZXingScannerView(getActivity());
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA}, CAMERA_CODE);
        }
        return scannerView;
    }

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("São necessárias permissões para aceder a esta funcionalidade.");
                builder.show();
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        createRequest(result.getText());
        //createDialog(result.getText());
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    private void createRequest(String qrCode) {
        String addon_url = "pt/api/TimeSlots?QRCode=";
        StringRequest request = new StringRequest(Request.Method.GET, url+ addon_url + qrCode, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if(response.equals("[]")) {
                        createDialog(null);
                    } else {
                        createDialog(response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    createDialog(null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(this.getContext()).addToQueue(request);
    }

    private void createDialog(String res) throws JSONException {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Resultados do QR Code");
        if (res != null ) {
            JSONArray arr = new JSONArray(res);
            String arr_res = arr.getJSONObject(0).toString();
            String date = ParsingUtils.getDataFromJson(arr_res, "beginTime");
            String endDate = ParsingUtils.getDataFromJson(arr_res, "endTime");
            if (checkBeginDate(date)) {
                if(checkEndDate(endDate)) {
                    builder.setIcon(R.drawable.ic_access_granted);
                    builder.setMessage("Token válido.");
                }else {
                    builder.setIcon(R.drawable.ic_access_denied);
                    builder.setMessage("Data expirada.");
                }
            }else {
                builder.setIcon(R.drawable.ic_access_denied);
                builder.setMessage("Data inválida.");
            }
        } else {
            builder.setMessage("Token inválido.");
            builder.setIcon(R.drawable.ic_access_denied);
        }
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                scannerView.resumeCameraPreview(instance);
            }
        });
        builder.show();
    }

    private boolean checkBeginDate(String date) {
        long max_time= 10 * 60000;
        Calendar today = Calendar.getInstance();
        Calendar c = getDate(date);
        if(today.before(c)) {
            long diff = c.getTimeInMillis() - today.getTimeInMillis();
            if(diff <= max_time ) {
                return true;
            }
            return false;
        }
        return true;
    }

    private boolean checkEndDate(String date) {
        Calendar today = Calendar.getInstance();
        Calendar c = getDate(date);
        if(today.after(c)) {
            return false;
        }
        return true;
    }
//
    private Calendar getDate(String date) {
        String[] sepDate = ParsingUtils.splitString(date, "/");
        String[] sepHDate = ParsingUtils.splitString(sepDate[2], " ");
        int ano = Integer.parseInt(sepHDate[0]);
        int mes = Integer.parseInt(sepDate[0]);
        int dia = Integer.parseInt(sepDate[1]);

        String[] sepEHour = ParsingUtils.splitString(sepHDate[1], ":");
        int ehour = Integer.parseInt(sepEHour[0]);
        if(sepHDate[2].equals("PM")) ehour+=12;
        int emin = Integer.parseInt(sepEHour[1]);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, ano);
        c.set(Calendar.MONTH, mes-1);
        c.set(Calendar.DAY_OF_MONTH, dia);
        c.set(Calendar.HOUR_OF_DAY, ehour);
        c.set(Calendar.MINUTE, emin);
        return c;
    }
//
//    private boolean checkTime(String date, String endDate) {
//        boolean dateDelay = false;
//
//        Calendar dataAtual = Calendar.getInstance();
//        Calendar dataEnd = dataAtual;
//
//        String[] sepEndDate = ParsingUtils.splitString(endDate, "/");
//        String[] sepEndDate2 = ParsingUtils.splitString(sepEndDate[2], " ");
//        int endano = Integer.parseInt(sepEndDate2[0]);
//        int endmes = Integer.parseInt(sepEndDate[0]);
//        int enddia = Integer.parseInt(sepEndDate[1]);
//        dataEnd.set(Calendar.YEAR, endano);
//        dataEnd.set(Calendar.MONTH, endmes-1);
//        dataEnd.set(Calendar.DAY_OF_MONTH, enddia);
//
//        if(dataAtual.before(dataEnd)) {
//            dateDelay = true;
//        } else if(dataAtual.before(dataEnd)) {
//            return false;
//        }
//
//        //end time
//        String[] sepEDate = ParsingUtils.splitString(endDate, " ");
//        String[] sepEHour = ParsingUtils.splitString(sepEDate[1], ":");
//        int ehour = Integer.parseInt(sepEHour[0]);
//        if(sepEDate[2].equals("PM")) ehour+=12;
//        int emin = Integer.parseInt(sepEHour[1]);
//        int totalEnd = (ehour*60) + emin;
//
//        //BEGIN TIME
//        String[] sepDate = ParsingUtils.splitString(date, " ");
//        String[] sepHour = ParsingUtils.splitString(sepDate[1], ":");
//        int hour = Integer.parseInt(sepHour[0]);
//        if(sepDate[2].equals("PM")) hour+=12;
//        int min = Integer.parseInt(sepHour[1]);
//        int totalBegin = (hour*60) + min;
//        int currentTime = ParsingUtils.getCurrentTime();
//        boolean verify = totalBegin - currentTime > 10;
//
//        if(!dateDelay) {
//            if(verify || totalEnd - currentTime < 0) {
//                return false;
//            }
//        } else {
//            if(verify) {
//                return false;
//            }
//        }
//        return true;
//    }

}
