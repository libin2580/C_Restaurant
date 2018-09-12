package canali.meridian.cybraum.restaurant.Waiter.food_menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

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

/**
 * Created by Anvin on 11/22/2017.
 */

public class ViewOrderAdapter extends RecyclerView.Adapter<ViewOrderAdapter.ViewHolder> {
    private Context appContext;
    private ArrayList<OrderViewModel> arrayValues;
    Activity appactivity;
    DatabaseHelper db;
    Typeface custom_font;

    public ViewOrderAdapter(Context context, ArrayList<OrderViewModel> values,Activity act) {
        this.appactivity=act;
        this.appContext = context;
        this.arrayValues = values;
        db = new DatabaseHelper(context);
        System.out.println("arrayValues.size(): " +arrayValues.size());

        for(OrderViewModel om:arrayValues){
            System.out.println("om.getTable() : "+om.getTable());
            System.out.println("om.getHut() : "+om.getHut());
        }
        custom_font = Typeface.createFromAsset(appContext.getAssets(),  "fonts/Raleway-Regular.ttf");


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_order_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txt_kot.setText(arrayValues.get(position).getKot_no());
        if(arrayValues.get(position).getHut().equals("")||arrayValues.get(position).getHut()==null||arrayValues.get(position).getHut().equals("null")) {
            holder.txt_table_name.setText(arrayValues.get(position).getTable());
        }else {
            holder.txt_table_name.setText(arrayValues.get(position).getHut());
        }
        if(arrayValues.get(position).getStatus().equalsIgnoreCase("delivered"))
        {
            holder.linear_bill_now.setVisibility(View.VISIBLE);
        }
        else {

            holder.linear_bill_now.setVisibility(View.INVISIBLE);
        }


        holder.linear_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    db.clearDatabase();
                Intent i =new Intent(appContext,View_items.class);
                i.putExtra("kot_number",arrayValues.get(position).getKot_no());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                appContext.startActivity(i);
                 //   appactivity.finish();
                 //   MenuPage.kot_number=arrayValues.get(position).getKot_no();
            }
        });

        holder.linear_bill_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkCheckingClass nc = new NetworkCheckingClass(appactivity);
                if (nc.ckeckinternet()) {
                    getBill(arrayValues.get(position).getKot_no());
                } else {
                    Toasty.error(appactivity, "Please enable internet connection", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }
    @Override
    public int getItemCount() {
        return arrayValues.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_kot,txt_table_name;
        LinearLayout linear_add_item,linear_bill_now;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_kot=(TextView)itemView.findViewById(R.id.txt_kot);
            txt_table_name=(TextView)itemView.findViewById(R.id.txt_table_name);
            linear_add_item=(LinearLayout) itemView.findViewById(R.id.linear_add_item);
            linear_bill_now=(LinearLayout) itemView.findViewById(R.id.linear_bill_now);


        }
    }
    public void getBill(final String kot) {

        class SendToServer extends AsyncTask<String, String, String> {

            final KProgressHUD hud1 = KProgressHUD.create(appactivity)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setCancellable(true)
                    .setAnimationSpeed(1)
                    .setDimAmount(0.5f)
                    .show();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();


            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    URL url = new URL("http://www.thecanalirestaurant.com.php56-27.phx1-2.websitetestlink.com/json/billing.php");

                    JSONObject postDataParams = new JSONObject();
                    postDataParams.put("kot_no",kot );

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
                if(!result.equals("")) {
                    try {

                        JSONObject jsonObj = new JSONObject(result);
                        String status = jsonObj.getString("status");
                        if (status.equalsIgnoreCase("true")) {
                            String kot_val = jsonObj.getString("kot_no");

                            Toasty.success(appactivity, "Billing Successfully done for " + kot_val, Toast.LENGTH_SHORT).show();


                        } else {
                            Toasty.error(appactivity, "Something went wrong.", Toast.LENGTH_SHORT).show();
                        }
                        //progress_bar.setVisibility(View.GONE);
                        if (!appactivity.isFinishing() && hud1 != null) {
                            hud1.dismiss();
                        }

                    } catch (Exception e) {
                        //progress_bar.setVisibility(View.GONE);
                        if (!appactivity.isFinishing() && hud1 != null) {
                            hud1.dismiss();
                        }
                    }
                }else {
                    hud1.dismiss();
                    Toasty.warning(appactivity, "Something went wrong.", Toast.LENGTH_SHORT).show();
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
