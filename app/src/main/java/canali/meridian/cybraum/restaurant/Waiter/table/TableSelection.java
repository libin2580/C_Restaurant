package canali.meridian.cybraum.restaurant.Waiter.table;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import canali.meridian.cybraum.restaurant.CheckAndRequestPermission;
import canali.meridian.cybraum.restaurant.NetworkCheckingClass;
import canali.meridian.cybraum.restaurant.R;
import canali.meridian.cybraum.restaurant.Splsh_Login.LoginSelection;
import es.dmoral.toasty.Toasty;

import static canali.meridian.cybraum.restaurant.CanaliConstans.Canali_Bas;


public class TableSelection extends AppCompatActivity {
    RecyclerView grid_recyclerview;
    TableSelectionModel tm;
    ArrayList<TableSelectionModel>arrayValuies;
    KProgressHUD hud1;
    LinearLayout Tab_logout;
    SharedPreferences preference;
    String user_id;
    TextView Selection_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_selection);
        grid_recyclerview=(RecyclerView)findViewById(R.id.grid_recyclerview);
        Selection_txt = (TextView) findViewById(R.id.tab_selection);
        Tab_logout = (LinearLayout)findViewById(R.id.tab_logout);
        grid_recyclerview.setHasFixedSize(true);
        if (getApplicationContext().getResources ().getBoolean (R.bool.isTablet)) {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),6);
            grid_recyclerview.setLayoutManager(layoutManager);
        } else {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),4);
            grid_recyclerview.setLayoutManager(layoutManager);
        }

        arrayValuies=new ArrayList<>();
        CheckAndRequestPermission cp=new CheckAndRequestPermission();
        cp.checkAndRequestPermissions(TableSelection.this);
        SharedPreferences preferences =getApplicationContext().getSharedPreferences("Category",MODE_PRIVATE);
        String cat_type=preferences.getString("category", null);
        NetworkCheckingClass nc=new NetworkCheckingClass(TableSelection.this);
        if(nc.ckeckinternet()) {


            if(cat_type.equals("ac")){
                table_ac();
                Selection_txt.setText("Select Table");
            }
            if(cat_type.equals("hut")){
                table_hut();
                Selection_txt.setText("Select Hut");
            }
        }
        else
        {
            Toast.makeText(TableSelection.this,"Please enable internet connection",Toast.LENGTH_SHORT).show();
        }
        preference =getApplicationContext().getSharedPreferences("login_method",MODE_PRIVATE);
        user_id=preference.getString("user_id", null);
        Tab_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

    }


    private void table_ac() {
        final KProgressHUD hud1= KProgressHUD.create(TableSelection.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
        Bridge
                .get(Canali_Bas+"tables.php")
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
                                JSONObject jsonObj=new JSONObject(responseContent);
                                String status=jsonObj.getString("status");
                                if(status.equalsIgnoreCase("true")){
                                    JSONArray jsonArray=jsonObj.getJSONArray("data");
                                    if(jsonArray.length()>0){
                                        for(int i=0;i<jsonArray.length();i++){
                                            JSONObject dataObj=jsonArray.getJSONObject(i);
                                            tm=new TableSelectionModel();
                                            if(dataObj.has("id"))
                                                tm.setId(dataObj.getString("id"));
                                            if(dataObj.has("table_name"))
                                                tm.setNumber(dataObj.getString("table_name"));

                                            arrayValuies.add(tm);
                                            TableSelectionAdapter adapter = new TableSelectionAdapter(getApplicationContext(), arrayValuies);
                                            grid_recyclerview.setAdapter(adapter);
                                        }
                                    }
                                }

                            } catch (Exception ew) {
                                ew.printStackTrace();
                            }
                        }
                    }
                });

    }
    private void table_hut() {
        final KProgressHUD hud1= KProgressHUD.create(TableSelection.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
        Bridge
                .get(Canali_Bas+"huts.php")
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
                                JSONObject jsonObj=new JSONObject(responseContent);
                                String status=jsonObj.getString("status");
                                if(status.equalsIgnoreCase("true")){
                                    JSONArray jsonArray=jsonObj.getJSONArray("data");
                                    if(jsonArray.length()>0){
                                        for(int i=0;i<jsonArray.length();i++){
                                            JSONObject dataObj=jsonArray.getJSONObject(i);
                                            tm=new TableSelectionModel();
                                            if(dataObj.has("id"))
                                                tm.setId(dataObj.getString("id"));
                                            if(dataObj.has("hut_name"))
                                                tm.setNumber(dataObj.getString("hut_name"));

                                            arrayValuies.add(tm);
                                            TableSelectionAdapter adapter = new TableSelectionAdapter(getApplicationContext(), arrayValuies);
                                            grid_recyclerview.setAdapter(adapter);
                                        }
                                    }
                                }

                            } catch (Exception ew) {
                                ew.printStackTrace();
                            }
                        }
                    }
                });

    }
    public void logout() {

        final KProgressHUD hud1= KProgressHUD.create(TableSelection.this)
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
                                Intent intent = new Intent(TableSelection.this, LoginSelection.class);
                                startActivity(intent);
                                Toasty.success(getApplicationContext(), "Successfully Logout", Toast.LENGTH_SHORT, true).show();
                                SharedPreferences.Editor editor = preference.edit();
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

}
