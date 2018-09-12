package canali.meridian.cybraum.restaurant.Waiter.food_menu;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import canali.meridian.cybraum.restaurant.Waiter.category.Catogory_adaptor;
import es.dmoral.toasty.Toasty;

import static canali.meridian.cybraum.restaurant.CanaliConstans.Canali_Bas;

/**
 * Created by Ansal on 11/23/2017.
 */

public class View_items extends AppCompatActivity {
    RecyclerView recyclerView;
    String status,user_id;
    View_model vm;
    Catogory_adaptor cat_adaptor;
    ArrayList<View_model> view_list=new ArrayList<>();
    LinearLayout linear_add_orders;
    View_items_adaptor view_adaptor;
    String kot_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_item);
        linear_add_orders = (LinearLayout)findViewById(R.id.linear_add_orders);
        recyclerView = (RecyclerView)findViewById(R.id.view_item_recyclerview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        kot_number=getIntent().getStringExtra("kot_number");
        if(haveNetworkConnection()){
            item();
        }
        else {
            Toasty.error(getApplicationContext(), "No internet connection ", Toast.LENGTH_SHORT, true).show();

        }
        linear_add_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(View_items.this,MenuPage.class);
                i.putExtra("kot_number",kot_number);
                startActivity(i);

            }
        });

    }

    public void item() {

        final KProgressHUD hud1= KProgressHUD.create(View_items.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
        String URL=Canali_Bas+"order_items.php";
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
                                Toasty.warning(getApplicationContext(), messege, Toast.LENGTH_SHORT, true).show();

                            }
                            else {

                                JSONArray array = object.getJSONArray("data");
                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject j = array.getJSONObject(i);
                                    vm = new View_model();
                                    vm.setorder_item_id(j.getString("order_item_id"));
                                    vm.setitem_name(j.getString("item_name"));
                                    vm.setprice(j.getString("price"));
                                    vm.setquantity(j.getString("quantity"));
                                    view_list.add(vm);
                                }
                                view_adaptor = new View_items_adaptor(View_items.this,view_list, getApplicationContext());
                                recyclerView.setAdapter(view_adaptor);
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
                params.put("kot_no", kot_number);
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
}