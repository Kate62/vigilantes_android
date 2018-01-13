package casarder.vigilantes_androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MenuActivity extends AppCompatActivity {

    private final String url = "http://casardercdio.ddns.net:22000/";
    private String token;

    private HorarioFragment horarioFragment;
    private PercursoFragment percursoFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_horario:
                    fragmentTransaction.replace(R.id.content, new HorarioFragment()).commit();
                    return true;
                case R.id.navigation_percurso:
                    fragmentTransaction.replace(R.id.content, new PercursoFragment()).commit();
                    return true;
                /*case R.id.navigation_notifications:
                    fragmentTransaction.replace(R.id.content,new NotificacaoFragment()).commit();
                    return true;
                case R.id.qr_code:
                    readQrCode();
                    return true;*/
                case R.id.qr_code:
                    fragmentTransaction.replace(R.id.content, new QrCodeFragment()).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, new HorarioFragment()).commit();

        /*getUserRoundsRequest();*/
    }

    private void getUserRoundsRequest() {
        token = getIntent().getStringExtra("token");
        String addon_url = "rounds/today";

        StringRequest request = new StringRequest(Request.Method.GET, url + addon_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if(response != null) {
                        JSONObject actualRounds = new JSONObject(response);
                        horarioFragment.createHoursButton(actualRounds);
                        percursoFragment.createLinearLayoutPercurso(actualRounds);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ROUND ERROR", Toast.LENGTH_SHORT).show();
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        RequestQueueSingleton.getInstance(getApplicationContext()).addToQueue(request);
    }
}
