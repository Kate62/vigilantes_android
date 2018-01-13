package casarder.vigilantes_androidapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import casarder.vigilantes_androidapp.utils.ParsingUtils;

public class LogoActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String saved_uname = "username";
    public static final String saved_upass = "password";
    final String url = "http://casardercdio.ddns.net:22000/users/";
    ImageView image;
    Animation uptodown;
    EditText login_username, login_password;
    String username, password;
    ProgressBar progressBar;
    Button log_bt;
    String bodystr;
    int status;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo_activity);
        getWindow().setBackgroundDrawableResource(R.drawable.login_background);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        image = (ImageView) findViewById(R.id.image1);
        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        image.setAnimation(uptodown);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        login_username = (EditText) findViewById(R.id.username_txt);
        login_username.setText(sharedpreferences.getString("username", ""));
        login_password = (EditText) findViewById(R.id.password_txt);
        login_password.setText(sharedpreferences.getString("password", ""));
        progressBar = (ProgressBar) findViewById(R.id.login_progress_bar);
        log_bt = (Button) findViewById(R.id.login_button);

        log_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = login_username.getText().toString();
                password = login_password.getText().toString();
                loginRequest();
            }
        });

    }

    private void loginRequest() {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bodystr = jsonBody.toString();

        String addon_url = "login/";

        StringRequest request = new StringRequest(Request.Method.POST, url + addon_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_CREATED) {
                    String token = ParsingUtils.getDataFromJson(response, "token");
                    if (token != null) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(saved_uname, username);
                        editor.putString(saved_upass, password);
                        editor.commit();
                        Intent i = new Intent(LogoActivity.this, MenuActivity.class);
                        i.putExtra("token", token);
                        startActivity(i);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        log_bt.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    int status = response.statusCode;
                    if (status == HttpURLConnection.HTTP_INTERNAL_ERROR || status == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        Toast.makeText(getApplicationContext(), "O dados inserido não existem ou estão errados. ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Ocorreu um erro. Verifique a sua ligação internet ou tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
                log_bt.setVisibility(View.VISIBLE);
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return bodystr == null ? null : bodystr.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {

                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                status = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };
        RequestQueueSingleton.getInstance(this).addToQueue(request);
        log_bt.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

    }
}
