package canali.meridian.cybraum.restaurant.Kitchen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import canali.meridian.cybraum.restaurant.R;
import canali.meridian.cybraum.restaurant.RecyclerItemClickListener;
import canali.meridian.cybraum.restaurant.Splsh_Login.LoginSelection;
import es.dmoral.toasty.Toasty;

import static canali.meridian.cybraum.restaurant.CanaliConstans.Canali_Bas;

/**
 * Created by Ansal on 11/20/2017.
 */

public class Kitchen_orderlist extends AppCompatActivity {
    RecyclerView recyclerView;
    String status,user_id,pop_user_id;
    Kit_model Km;
    Kitchen_adaptor kitch_adaptor;
    Kitchen_pop_adaptor kitch_pop_adaptor;
    ArrayList<Kit_model> Kitch_list=new ArrayList<>();
    ArrayList<Kit_model> Kitch_list_pop=new ArrayList<>();
    LinearLayout Kit_logout,Kit_layout;
    SharedPreferences preferences;
    PopupWindow popupwindow;
    View custompopup_view;
    Typeface custom_font;
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kitchen_layout);
        recyclerView = (RecyclerView)findViewById(R.id.grid_recyclerview);
        Kit_logout = (LinearLayout)findViewById(R.id.kit_logout);
        Kit_layout = (LinearLayout)findViewById(R.id.kit_layout);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        if(haveNetworkConnection()){
            catogory();
        }
        else {


        }

        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Raleway-Regular.ttf");

        preferences =getApplicationContext().getSharedPreferences("login_method",MODE_PRIVATE);
        user_id=preferences.getString("user_id", null);
        Kit_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        final LayoutInflater inflator = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        custompopup_view = inflator.inflate(R.layout.kitchen_popup_layout, null);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        catogory();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    },500);
                }

        });
    }

    private void catogory() {
        Kitch_list.clear();
        final KProgressHUD hud1= KProgressHUD.create(Kitchen_orderlist.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
        Bridge
                .get(Canali_Bas+"all_orders.php")
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
                                    String kot_no = j.getString("kot_no");
                                    String order_status = j.getString("order_status");
                                    String date = j.getString("date");
                                    String user_name = j.getString("user_name");
                                    String details = j.getString("details");
                                    String user_id = j.getString("user_id");
                                    String table = j.getString("table");
                                    String hut = j.getString("hut");
                                    String location = j.getString("location");
                                    Km = new Kit_model();
                                    Km.setkot_no(kot_no);
                                    Km.setpop_user_name(user_name);
                                    Km.setpop_date(date);
                                    Km.setpop_details(details);
                                    Km.setpop_user_id(user_id);
                                    Km.setpop_table(table);
                                    Km.setorder_status(order_status);
                                    Km.sethut(hut);
                                    Km.setlocation(location);
                                    Kitch_list.add(Km);
                                }
                                kitch_adaptor = new Kitchen_adaptor(Kitch_list, getApplicationContext());
                                recyclerView.setAdapter(kitch_adaptor);
                            } catch (Exception w) {
                                w.printStackTrace();
                            }

                            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view,final int i) {
                                    Kitch_list_pop.clear();
                                    displaypopup();
                                    ImageView close_img=(ImageView)custompopup_view.findViewById(R.id.close_img);
                                    close_img.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            popupwindow.dismiss();
                                        }
                                    });

                                    RecyclerView pop_recy=(RecyclerView)custompopup_view.findViewById(R.id.recy);
                                    pop_recy.setHasFixedSize(true);
                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(custompopup_view.getContext());
                                    pop_recy.setLayoutManager(layoutManager);
                                    LinearLayout Accept=(LinearLayout)custompopup_view.findViewById(R.id.accept);
                                    LinearLayout Delivry=(LinearLayout)custompopup_view.findViewById(R.id.deliver);
                                    LinearLayout Delivered=(LinearLayout)custompopup_view.findViewById(R.id.delivered);
                                    LinearLayout Table_layout=(LinearLayout)custompopup_view.findViewById(R.id.tab_layout);
                                    LinearLayout Hut_layout=(LinearLayout)custompopup_view.findViewById(R.id.hut_layout);
                                    TextView kot_no=(TextView)custompopup_view.findViewById(R.id.kot_no);
                                    TextView   date=(TextView)custompopup_view.findViewById(R.id.date);
                                    TextView   Table=(TextView)custompopup_view.findViewById(R.id.table_no);
                                    TextView   Hut=(TextView)custompopup_view.findViewById(R.id.hut_no);
                                    TextView   Waitr_nmae=(TextView)custompopup_view.findViewById(R.id.waitr);
                                    TextView   Location=(TextView)custompopup_view.findViewById(R.id.location);
                                    TextView   Time=(TextView)custompopup_view.findViewById(R.id.time);
                                    kot_no.setText(":  "+Kitch_list.get(i).getkot_no());
                                    if(!Kitch_list.get(i).getpop_date().equals("")||Kitch_list.get(i).getpop_date()!=null||!Kitch_list.get(i).getpop_date().equals("null")){
                                        String output=Kitch_list.get(i).getpop_date();


                                        try {


                                            System.out.println("time_frm_response------------------" + output);
                                            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aaa");
                                            System.out.println("time_frm_response" + output);
                                            Date newDate= sdf.parse(output);
                                            sdf= new SimpleDateFormat("dd-MM-yyyy hh:mm aaa");
                                            System.out.println("newDatefdsa-fsd-----------------" + newDate);
                                            String time_frm_responsee=sdf.format(newDate);
                                            String str_date=time_frm_responsee.substring(0,10);
                                            String time=time_frm_responsee.substring(time_frm_responsee.indexOf(" "));
                                            date.setText(":  "+str_date);
                                            Time.setText(":  "+time);

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    else {
                                        date.setText(":  ");
                                        Time.setText(":  ");
                                    }
                                    Table.setText(":  "+Kitch_list.get(i).getpop_table());
                                    Hut.setText(":  "+Kitch_list.get(i).gethut());
                                    Waitr_nmae.setText(":  "+Kitch_list.get(i).getpop_user_name());
                                    Location.setText(":  "+Kitch_list.get(i).getlocation());
                                    pop_user_id=Kitch_list.get(i).getkot_no();
                                    if(Kitch_list.get(i).getorder_status().equals("Pending")){
                                        Accept.setVisibility(View.VISIBLE);
                                        Delivry.setVisibility(View.GONE);
                                        Delivered.setVisibility(View.GONE);
                                    }
                                    if(Kitch_list.get(i).getorder_status().equals("Accepted")){
                                        Accept.setVisibility(View.GONE);
                                        Delivry.setVisibility(View.VISIBLE);
                                        Delivered.setVisibility(View.GONE);
                                    }
                                    if(Kitch_list.get(i).getorder_status().equals("Delivered")){
                                        Accept.setVisibility(View.GONE);
                                        Delivry.setVisibility(View.GONE);
                                        Delivered.setVisibility(View.VISIBLE);
                                    }
                                    if(Kitch_list.get(i).gethut().equals("")||Kitch_list.get(i).gethut()==null||Kitch_list.get(i).gethut().equals("null")){
                                        Table_layout.setVisibility(View.VISIBLE);
                                        Hut_layout.setVisibility(View.GONE);
                                    }
                                    else {
                                        Table_layout.setVisibility(View.GONE);
                                        Hut_layout.setVisibility(View.VISIBLE);
                                    }
                                    try {

                                        JSONArray array =new JSONArray(Kitch_list.get(i).getpop_details());
                                        for (int k = 0; k < array.length(); k++) {

                                            JSONObject j = array.getJSONObject(k);
                                            String item_name = j.getString("item_name");
                                            String image = j.getString("image");
                                            String count = j.getString("quantity");
                                            String price = j.getString("price");
                                            String sts = j.getString("status");
                                            Km = new Kit_model();
                                            Km.setpop_item_name(item_name);
                                            Km.setpopimage(image);
                                            Km.setpop_count(count);
                                            Km.setpop_price(price);
                                            Km.setsts(sts);
                                            Kitch_list_pop.add(Km);
                                        }
                                        kitch_pop_adaptor = new Kitchen_pop_adaptor(Kitch_list_pop, getApplicationContext());
                                        pop_recy.setAdapter(kitch_pop_adaptor);
                                    } catch (Exception w) {
                                        w.printStackTrace();
                                    }

                                    Accept.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            sts();
                                        }
                                    });
                                    Delivry.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            sts1();
                                        }
                                    });
                                    Delivered.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            popupwindow.dismiss();
                                        }
                                    });
                                }
                            }));



                        }
                    }
                });

    }
    public void logout() {

        final KProgressHUD hud1= KProgressHUD.create(Kitchen_orderlist.this)
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
                                Intent intent = new Intent(Kitchen_orderlist.this, LoginSelection.class);
                                startActivity(intent);
                                Toasty.success(getApplicationContext(), "Successfully Logout", Toast.LENGTH_SHORT, true).show();
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.remove("login");
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
    public void displaypopup() {
        try {
            popupwindow =new PopupWindow(custompopup_view, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            if (Build.VERSION.SDK_INT >= 21) {
                popupwindow.setElevation(5.0f);
            }
            popupwindow.setFocusable(true);
            popupwindow.setAnimationStyle(R.style.AppTheme);
            popupwindow.showAtLocation(Kit_layout, Gravity.CENTER,0,0);

        }
        catch (Exception e){
            e.printStackTrace();
        }}

    public void sts() {

        String URL=Canali_Bas+"order_status.php  ";
        System.out.println("______________________");
        System.out.println("______________________");
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object=new JSONObject(response);
                            String status=object.getString("status");
                            if (!status.equals("true")){
                                popupwindow.dismiss();
                                String messege=object.getString("message");
                                Toasty.error(getApplicationContext(), messege, Toast.LENGTH_SHORT, true).show();

                            }
                            else {
                                popupwindow.dismiss();
                                Toasty.success(getApplicationContext(), "Accepted", Toast.LENGTH_SHORT, true).show();
                                catogory();
                            }


                        }
                        catch (Exception e){
                            e.printStackTrace();

                        }


                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("order_id",pop_user_id);
                params.put("status","Accepted");
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
    public void sts1() {

        String URL=Canali_Bas+"order_status.php";
        System.out.println("______________________");
        System.out.println("______________________");
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object=new JSONObject(response);
                            String status=object.getString("status");
                            if (!status.equals("true")){
                                String messege=object.getString("message");
                                Toasty.error(getApplicationContext(), messege, Toast.LENGTH_SHORT, true).show();
                                popupwindow.dismiss();
                            }
                            else {
                                popupwindow.dismiss();
                                Toasty.success(getApplicationContext(), "Delivered", Toast.LENGTH_SHORT, true).show();
                                catogory();
                            }


                        }
                        catch (Exception e){
                            e.printStackTrace();

                        }


                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("order_id",pop_user_id);
                params.put("status","Delivered");
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
    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(Kitchen_orderlist.this)

                .setMessage("Exit from the app?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        ActivityCompat.finishAffinity(Kitchen_orderlist.this);

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