package canali.meridian.cybraum.restaurant.Waiter.category;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Callback;
import com.afollestad.bridge.Request;
import com.afollestad.bridge.Response;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import canali.meridian.cybraum.restaurant.R;
import canali.meridian.cybraum.restaurant.Splsh_Login.LoginSelection;
import canali.meridian.cybraum.restaurant.Waiter.food_menu.ViewOrders;
import es.dmoral.toasty.Toasty;

import static canali.meridian.cybraum.restaurant.CanaliConstans.Canali_Bas;

/**
 * Created by Ansal on 11/18/2017.
 */

public class Select_catogory extends AppCompatActivity {
   RecyclerView recyclerView;
   String status,user_id;
   Catogory_model Cm;
   Catogory_adaptor cat_adaptor;
   ArrayList<Catogory_model> Cast_list=new ArrayList<>();
   LinearLayout Cat_logout,linear_view_orders;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_catogory);
        recyclerView = (RecyclerView)findViewById(R.id.grid_recyclerview);
        Cat_logout = (LinearLayout)findViewById(R.id.cat_logout);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(layoutManager);
        linear_view_orders=(LinearLayout)findViewById(R.id.linear_view_orders);
        if(haveNetworkConnection()){
            catogory();
        }
        else {
            Toasty.error(getApplicationContext(), "No internet connection ", Toast.LENGTH_SHORT, true).show();

        }
        preferences =getApplicationContext().getSharedPreferences("login_method",MODE_PRIVATE);
        user_id=preferences.getString("user_id", null);
        Cat_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        linear_view_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Select_catogory.this,ViewOrders.class);
                startActivity(i);
            }
        });

    }

    private void catogory() {
        final KProgressHUD hud1= KProgressHUD.create(Select_catogory.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
        Bridge
                .get(Canali_Bas+"loc_categories.php")
                .throwIfNotSuccess()
                .request(new Callback() {
                    @Override
                    public void response(@NonNull Request request, Response response, BridgeException e) {
                        if (e != null) {
                            int reason = e.reason();
                        } else {
                            hud1.dismiss();
                            String responseContent = response.asString();
                            try {
                                JSONObject jsonObj = new JSONObject(responseContent);
                                status = jsonObj.getString("status");
                                JSONArray array = jsonObj.getJSONArray("data");
                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject j = array.getJSONObject(i);
                                    String cat_name = j.getString("category");
                                    String cat_image = j.getString("image");
                                    Cm = new Catogory_model();
                                    Cm.setcat_image(cat_image);
                                    Cm.setcat_name(cat_name);
                                    Cast_list.add(Cm);
                                }
                                cat_adaptor = new Catogory_adaptor(Cast_list, getApplicationContext());
                                recyclerView.setAdapter(cat_adaptor);
                            } catch (Exception w) {
                                w.printStackTrace();
                            }
                        }
                    }
                });

    }
    public void logout() {

        final KProgressHUD hud1= KProgressHUD.create(Select_catogory.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
        String URL=Canali_Bas+"log_out.php";
        System.out.println("______________________");
        System.out.println("______________________");
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,URL,
                new com.android.volley.Response.Listener<String>() {
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
                                Intent intent = new Intent(Select_catogory.this, LoginSelection.class);
                                startActivity(intent);
                                Toasty.success(getApplicationContext(), "Successfully Logout", Toast.LENGTH_SHORT, true).show();
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.remove("signin");
                                editor.commit();
                                finish();
                            }


                        }
                        catch (Exception e){
                            e.printStackTrace();
                            hud1.dismiss();
                        }


                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hud1.show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
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

        new AlertDialog.Builder(Select_catogory.this)

                .setMessage("Exit from the app?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        ActivityCompat.finishAffinity(Select_catogory.this);

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
// do nothing
                    }
                })

                .show();
    }
}