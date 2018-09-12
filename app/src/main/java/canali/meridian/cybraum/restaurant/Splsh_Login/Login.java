package canali.meridian.cybraum.restaurant.Splsh_Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import canali.meridian.cybraum.restaurant.Kitchen.Kitchen_orderlist;
import canali.meridian.cybraum.restaurant.R;
import canali.meridian.cybraum.restaurant.Waiter.category.Select_catogory;
import es.dmoral.toasty.Toasty;

import static canali.meridian.cybraum.restaurant.CanaliConstans.Canali_Bas;

/**
 * Created by Ansal on 11/17/2017.
 */

public class Login extends AppCompatActivity {
    LinearLayout layout_waiter,layout_kitchen,Waiter_login,Kitchen_login;
    EditText Waitr_username,Waitr_paswrd,Kitchen_username,Kitchen_paswrd;
    String str_w_user,str_w_pass,str_k_user,str_k_pass;
    SharedPreferences preferences;
    Typeface custom_font;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        layout_waiter=(LinearLayout)findViewById(R.id.layout_waiter);
        layout_kitchen=(LinearLayout)findViewById(R.id.layout_kitchen);
        Waiter_login=(LinearLayout)findViewById(R.id.waiter_login);
        Kitchen_login=(LinearLayout)findViewById(R.id.kitchen_login);
        Waitr_username=(EditText)findViewById(R.id.w_usrname);
        Waitr_paswrd=(EditText)findViewById(R.id.w_pass);
        Kitchen_username=(EditText)findViewById(R.id.k_usrname);
        Kitchen_paswrd=(EditText)findViewById(R.id.k_pass);
        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Raleway-Regular.ttf");

        Waitr_username .setTypeface(custom_font);
        Waitr_paswrd .setTypeface(custom_font);
        Kitchen_username .setTypeface(custom_font);
        Kitchen_paswrd .setTypeface(custom_font);






        String login=getIntent().getStringExtra("login");
        if(login.equals("waiter")){
            layout_waiter.setVisibility(View.VISIBLE);
            layout_kitchen.setVisibility(View.GONE);
        }
        if(login.equals("kitchen")){
            layout_waiter.setVisibility(View.GONE);
            layout_kitchen.setVisibility(View.VISIBLE);
        }
        preferences =getApplicationContext().getSharedPreferences("login_method",MODE_PRIVATE);
        Waiter_login.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                str_w_user=Waitr_username.getText().toString();
                str_w_pass=Waitr_paswrd.getText().toString();
                if(str_w_user.isEmpty()){
                    Toasty.warning(getApplicationContext(), "Username is empty", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                if(str_w_pass.isEmpty()){
                    Toasty.warning(getApplicationContext(), "Password is empty", Toast.LENGTH_SHORT, true).show();
                    return;

                }
                if (haveNetworkConnection()){
                    waiter();
                }
                else {
                    Toasty.error(getApplicationContext(), "No internet connection ", Toast.LENGTH_SHORT, true).show();
                }

            }
        });
        Kitchen_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_k_user=Kitchen_username.getText().toString();
                str_k_pass=Kitchen_paswrd.getText().toString();
                if(str_k_pass.isEmpty()){
                    Toasty.warning(getApplicationContext(), "ID NO is empty", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                if(str_k_user.isEmpty()){
                    Toasty.warning(getApplicationContext(), "Section Name is empty", Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if (haveNetworkConnection()){
                    kitchen();
                }
                else {
                    Toasty.error(getApplicationContext(), "No internet connection ", Toast.LENGTH_SHORT, true).show();

                }

            }
        });

    }
    public void waiter() {

        final KProgressHUD hud1= KProgressHUD.create(Login.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
        String URL=Canali_Bas+"login.php";
        System.out.println("______________________");
        System.out.println("______________________");
        StringRequest stringRequest = new StringRequest(Request.Method.POST,URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hud1.dismiss();
                        try {
                            JSONObject object=new JSONObject(response);
                            String status=object.getString("status");
                            if (!status.equals("true")){
                                String messege=object.getString("message");
                                Toasty.error(getApplicationContext(), messege, Toast.LENGTH_SHORT, true).show();

                            }
                            else {
                                final String jsonmessege = object.getJSONObject("data").getString("user_id");
                                SharedPreferences.Editor editor1 = preferences.edit();
                                editor1.putString("user_id",jsonmessege);
                                editor1.putString("signin","signin");
                                editor1.apply();
                                Intent intent = new Intent(Login.this, Select_catogory.class);
                                startActivity(intent);
                                Toasty.success(getApplicationContext(), "Successful Registration", Toast.LENGTH_SHORT, true).show();
                                finish();
                            }


                        }
                        catch (Exception e){
                            e.printStackTrace();
                            hud1.dismiss();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hud1.show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_name",str_w_user);
                params.put("password",str_w_pass);
                params.put("role", "waiter");
                Log.d("hash", String.valueOf(params));
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
    public void kitchen() {

        final KProgressHUD hud1= KProgressHUD.create(Login.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
        String URL=Canali_Bas+"login.php";
        System.out.println("______________________");
        System.out.println("______________________");
        StringRequest stringRequest = new StringRequest(Request.Method.POST,URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hud1.dismiss();
                        try {
                            JSONObject object=new JSONObject(response);
                            String status=object.getString("status");
                            if (!status.equals("true")){
                                String messege=object.getString("message");
                                Toasty.error(getApplicationContext(), messege, Toast.LENGTH_SHORT, true).show();

                            }
                            else {
                                final String jsonmessege = object.getJSONObject("data").getString("user_id");
                                SharedPreferences.Editor editor1 = preferences.edit();
                                editor1.putString("user_id",jsonmessege);
                                editor1.putString("login","signin");
                                editor1.apply();
                                Intent intent = new Intent(Login.this, Kitchen_orderlist.class);
                                startActivity(intent);
                                Toasty.success(getApplicationContext(), "Successful Registration", Toast.LENGTH_SHORT, true).show();
                                finish();
                            }


                        }
                        catch (Exception e){
                            e.printStackTrace();
                            hud1.dismiss();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hud1.show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_name",str_k_user);
                params.put("password",str_k_pass);
                params.put("role", "kitchen");
                Log.d("hash", String.valueOf(params));
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    @Override
    public void onBackPressed() {

        Intent i=new Intent(Login.this,LoginSelection.class);
        startActivity(i);
        finish();
    }

}
