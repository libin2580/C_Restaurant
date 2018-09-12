package canali.meridian.cybraum.restaurant.Waiter.food_menu;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import canali.meridian.cybraum.restaurant.NetworkCheckingClass;
import canali.meridian.cybraum.restaurant.R;
import es.dmoral.toasty.Toasty;

import static canali.meridian.cybraum.restaurant.CanaliConstans.Canali_Bas;

public class ViewOrders extends AppCompatActivity {
    RecyclerView view_orders_recyclerview;
    ArrayList<OrderViewModel>orderArraylist;
    ViewOrderAdapter voa;
    TextView txt_no_orders;
    ImageView img_refresh;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        txt_no_orders=(TextView)findViewById(R.id.txt_no_orders);
        img_refresh=(ImageView)findViewById(R.id.img_refresh);

        view_orders_recyclerview=(RecyclerView)findViewById(R.id.view_orders_recyclerview);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        view_orders_recyclerview.setLayoutManager(layoutManager);
        SharedPreferences preferences =getApplicationContext().getSharedPreferences("login_method",MODE_PRIVATE);

        orderArraylist=new ArrayList<>();

         uid=preferences.getString("user_id",null);
        NetworkCheckingClass nc = new NetworkCheckingClass(ViewOrders.this);
        if (nc.ckeckinternet()) {
            getOrders(uid);
        } else {
            Toasty.error(ViewOrders.this, "Please enable internet connection", Toast.LENGTH_SHORT).show();
        }


        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkCheckingClass nc = new NetworkCheckingClass(ViewOrders.this);
                if (nc.ckeckinternet()) {
                    getOrders(uid);
                } else {
                    Toasty.error(ViewOrders.this, "Please enable internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    public void getOrders(final String user_id) {

        class SendToServer extends AsyncTask<String, String, String> {

            final KProgressHUD hud1 = KProgressHUD.create(ViewOrders.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setCancellable(true)
                    .setAnimationSpeed(1)
                    .setDimAmount(0.5f)
                    .show();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                orderArraylist.clear();

            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    URL url = new URL(Canali_Bas+"view_orders.php");

                    JSONObject postDataParams = new JSONObject();
                    postDataParams.put("user_id",user_id );

                    Log.e("params", postDataParams.toString());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));

                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {

                        BufferedReader in = new BufferedReader(new
                                InputStreamReader(
                                conn.getInputStream()));

                        StringBuffer sb = new StringBuffer("");
                        String line = "";

                        while ((line = in.readLine()) != null) {

                            sb.append(line);
                            break;
                        }

                        in.close();
                        return sb.toString();

                    } else {
                        return new String("false : " + responseCode);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                System.out.println("result : " + result);
                try {

                    JSONObject jsonObj = new JSONObject(result);
                    String status = jsonObj.getString("status");
                    if (status.equalsIgnoreCase("true")) {

                        JSONArray dataArray=jsonObj.getJSONArray("data");
                        OrderViewModel ovm;
                        if(dataArray.length()>0);
                        {
                            for(int i=0;i<dataArray.length();i++) {
                                ovm=new OrderViewModel();
                                JSONObject dataObj = dataArray.getJSONObject(i);

                                if(dataObj.has("kot_no"))
                                    ovm.setKot_no(dataObj.getString("kot_no"));
                                if(dataObj.has("location"))
                                    ovm.setLocation(dataObj.getString("location"));
                                if(dataObj.has("table"))
                                    ovm.setTable(dataObj.getString("table"));
                                if(dataObj.has("hut"))
                                    ovm.setHut(dataObj.getString("hut"));
                                if(dataObj.has("status"))
                                    ovm.setStatus(dataObj.getString("status"));
                                orderArraylist.add(ovm);
                            }
                        }


                        if(orderArraylist.size()>0)
                        {
                            view_orders_recyclerview.setVisibility(View.VISIBLE);
                            txt_no_orders.setVisibility(View.GONE);
                            voa=new ViewOrderAdapter(ViewOrders.this,orderArraylist,ViewOrders.this);
                            view_orders_recyclerview.setAdapter(voa);
                        }
                        else {
                            view_orders_recyclerview.setVisibility(View.GONE);
                            txt_no_orders.setVisibility(View.VISIBLE);
                          // Toasty.warning(getApplicationContext(), "No Orders", Toast.LENGTH_SHORT).show();

                        }


                    } else {
                        view_orders_recyclerview.setVisibility(View.GONE);
                        txt_no_orders.setVisibility(View.VISIBLE);
                       // Toasty.warning(getApplicationContext(), "No Orders", Toast.LENGTH_SHORT).show();
                    }
                    //progress_bar.setVisibility(View.GONE);
                    if (!ViewOrders.this.isFinishing() && hud1 != null) {
                        hud1.dismiss();
                    }

                } catch (Exception e) {
                    //progress_bar.setVisibility(View.GONE);
                    if (!ViewOrders.this.isFinishing() && hud1 != null) {
                        hud1.dismiss();
                    }
                }
            }
        }

        SendToServer sts=new SendToServer();
        sts.execute();
    }
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
