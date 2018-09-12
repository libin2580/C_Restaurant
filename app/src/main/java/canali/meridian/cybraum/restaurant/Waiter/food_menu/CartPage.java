package canali.meridian.cybraum.restaurant.Waiter.food_menu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import canali.meridian.cybraum.restaurant.Waiter.category.Select_catogory;
import es.dmoral.toasty.Toasty;

import static canali.meridian.cybraum.restaurant.CanaliConstans.Canali_Bas;

public class CartPage extends AppCompatActivity {
    RecyclerView menu_items_recyclerview;
    LinearLayout linear_cancel,linear_submit;
    WaiterCartAdapter wca;
    ArrayList<MenuModel>cartArraylist;
    DatabaseHelper db;
    ProgressBar progress_bar;
    View customView;
    PopupWindow mPopupWindow;
    RelativeLayout parent_layout;
    TextView popup_kot_no;
    public static TextView txt_cart_number;
    LinearLayout popup_ok_button;
    String table_id,hut_id;
    ArrayList<MenuModel>dbArraylist;
    ImageView img_clear_cart;
    public String popup_visible_flag="false";
    String kot_no="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_page);

        menu_items_recyclerview=(RecyclerView)findViewById(R.id.menu_items_recyclerview);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        menu_items_recyclerview.setLayoutManager(layoutManager);
        progress_bar=(ProgressBar)findViewById(R.id.progress_bar);
        linear_cancel=(LinearLayout) findViewById(R.id.linear_cancel);
        linear_submit=(LinearLayout) findViewById(R.id.linear_submit);
        txt_cart_number=(TextView)findViewById(R.id.txt_cart_number);
        img_clear_cart=(ImageView)findViewById(R.id.img_clear_cart);

        cartArraylist=new ArrayList<>();
        db = new DatabaseHelper(getApplicationContext());
        cartArraylist=db.getAllDatas();
        wca=new WaiterCartAdapter(getApplicationContext(),cartArraylist,CartPage.this);
        menu_items_recyclerview.setAdapter(wca);
        parent_layout=(RelativeLayout)findViewById(R.id.parent_layout);
        LayoutInflater inflater2 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        customView = inflater2.inflate(R.layout.order_placed_popup, null);
        popup_kot_no=(TextView)customView.findViewById(R.id.popup_kot_number);
        popup_ok_button=(LinearLayout)customView.findViewById(R.id.popup_ok_button);

        table_id=getIntent().getStringExtra("table_id");
        hut_id=getIntent().getStringExtra("hut_id");
        kot_no=getIntent().getStringExtra("kot_no");
        if(hut_id==null){
            hut_id="";
        }
        if(table_id==null){
            table_id="";
        }
        dbArraylist=new ArrayList<>();
        dbArraylist=db.getAllDatas();

        txt_cart_number.setText(Integer.toString(dbArraylist.size()));

        popup_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mPopupWindow.dismiss();

                Intent intent=new Intent(CartPage.this, Select_catogory.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


            }
        });
        img_clear_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(CartPage.this)

                        .setMessage("Are you sure you want to clear cart")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.clearDatabase();

                                finish();
                                Toasty.warning(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
// do nothing
                            }
                        })

                        .show();

            }
        });
        linear_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //db.clearDatabase();

                finish();
            }
        });
        linear_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(popup_visible_flag.equalsIgnoreCase("true")){
                    Toasty.warning(CartPage.this, "Already Submitted", Toast.LENGTH_SHORT).show();

                }else {

                    NetworkCheckingClass nc = new NetworkCheckingClass(CartPage.this);
                    if (nc.ckeckinternet()) {
                        System.out.println("kot number in cartpage : "+kot_no);

                        if(!kot_no.equals("")){
                            new SendToServerExtraFoodRequest().execute();
                        }else {
                            new SendToServer().execute();
                        }
                    } else {
                        Toasty.error(CartPage.this, "Please enable internet connection", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

    public void displayPopup() {

        try {
            System.out.println("inside display popup");
            mPopupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (Build.VERSION.SDK_INT >= 21) {
                mPopupWindow.setElevation(5.0f);
            }
            mPopupWindow.setFocusable(true);
            mPopupWindow.setAnimationStyle(R.style.popupAnimation);


            mPopupWindow.showAtLocation(parent_layout, Gravity.CENTER, 0, 0);
            popup_visible_flag="true";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private class SendToServer extends AsyncTask<String,String,String> {


        final KProgressHUD hud1= KProgressHUD.create(CartPage.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            popup_visible_flag="false";
           // progress_bar.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(Canali_Bas+"order_submit.php");

                int total=0;
                String items="";

                String item_id="";
                String price="";
                String quantity="";
                for(int k=0; k<dbArraylist.size();k++){
                    int price_amt=Integer.parseInt(dbArraylist.get(k).getPrice());
                    int count=Integer.parseInt(dbArraylist.get(k).getCount());
                    total+=price_amt*count;

                    items+="{item_id"+":\""+dbArraylist.get(k).getItem_id()+","
                            +"price"+":\""+dbArraylist.get(k).getPrice()+","
                            +"quantity"+":\""+dbArraylist.get(k).getCount()+"}";

                    item_id+=""+dbArraylist.get(k).getItem_id().toString()+"";
                    price+=""+dbArraylist.get(k).getPrice().toString()+"";
                    quantity+=""+dbArraylist.get(k).getCount().toString()+"";

                    if(k!=dbArraylist.size()-1)
                    {
                        item_id+=",";
                        price+=",";
                        quantity+=",";
                        items+=",";
                    }

                }
                items="{"+"\""+"item_id"+"\""+":"+"\""+item_id+"\""+","
                        +"\""+"price"+"\""+":"+"\""+price+"\""+","
                        +"\""+"quantity"+"\""+":"+"\""+quantity+"\""+"}";

                System.out.println("total : "+total);
                System.out.println("items : "+"["+items+"]");

                JSONObject postDataParams=new JSONObject();
                postDataParams.put("user_id","2");
                postDataParams.put("order_category", "4");
                postDataParams.put("category_id", "4");
                postDataParams.put("table_name", table_id);
                postDataParams.put("hut_name", hut_id);

                postDataParams.put("total", Integer.toString(total));

                postDataParams.put("items","["+items+"]");




                Log.e("params",postDataParams.toString());

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

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("result : "+result);
            try {

                JSONObject jsonObj=new JSONObject(result);
                String status=jsonObj.getString("status");
                if(status.equalsIgnoreCase("true"))
                {
                    popup_kot_no.setText(jsonObj.getString("kot"));
                    db.clearDatabase();
                    displayPopup();
                }
                else {
                    Toasty.error(getApplicationContext(),"Something went wrong.",Toast.LENGTH_SHORT).show();
                }
                //progress_bar.setVisibility(View.GONE);
                hud1.dismiss();

            }
            catch (Exception e){
                //progress_bar.setVisibility(View.GONE);
                hud1.dismiss();
            }
        }
    }


    private class SendToServerExtraFoodRequest extends AsyncTask<String,String,String> {


        final KProgressHUD hud1= KProgressHUD.create(CartPage.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            popup_visible_flag="false";
            // progress_bar.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(Canali_Bas+"edit_order.php");

                int total=0;
                String items="";

                String item_id="";
                String price="";
                String quantity="";
                for(int k=0; k<dbArraylist.size();k++){
                    int price_amt=Integer.parseInt(dbArraylist.get(k).getPrice());
                    int count=Integer.parseInt(dbArraylist.get(k).getCount());
                    total+=price_amt*count;

                    items+="{item_id"+":\""+dbArraylist.get(k).getItem_id()+","
                            +"price"+":\""+dbArraylist.get(k).getPrice()+","
                            +"quantity"+":\""+dbArraylist.get(k).getCount()+"}";

                    item_id+=""+dbArraylist.get(k).getItem_id().toString()+"";
                    price+=""+dbArraylist.get(k).getPrice().toString()+"";
                    quantity+=""+dbArraylist.get(k).getCount().toString()+"";

                    if(k!=dbArraylist.size()-1)
                    {
                        item_id+=",";
                        price+=",";
                        quantity+=",";
                        items+=",";
                    }

                }
                items="{"+"\""+"item_id"+"\""+":"+"\""+item_id+"\""+","
                        +"\""+"price"+"\""+":"+"\""+price+"\""+","
                        +"\""+"quantity"+"\""+":"+"\""+quantity+"\""+"}";

                System.out.println("total : "+total);
                System.out.println("items : "+"["+items+"]");

                JSONObject postDataParams=new JSONObject();
                postDataParams.put("kot_no",kot_no);
                /*postDataParams.put("order_category", "4");
                postDataParams.put("category_id", "4");
                postDataParams.put("table_name", table_id);
                postDataParams.put("hut_name", hut_id);

                postDataParams.put("total", Integer.toString(total));*/

                postDataParams.put("items","["+items+"]");




                Log.e("params",postDataParams.toString());

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

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("result : "+result);
            try {
                JSONObject jsonObj=new JSONObject(result);
                String status=jsonObj.getString("status");
                if(status.equalsIgnoreCase("true"))
                {
                    popup_kot_no.setText(jsonObj.getString("kot"));
                    db.clearDatabase();
                    MenuPage.kot_number="";//resetting kot number value
                    displayPopup();
                }
                else {
                    Toasty.error(getApplicationContext(),"Something went wrong.",Toast.LENGTH_SHORT).show();
                }
                //progress_bar.setVisibility(View.GONE);
                hud1.dismiss();
            }
            catch (Exception e){
                //progress_bar.setVisibility(View.GONE);
                hud1.dismiss();
            }
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("inside onbackpressed");
        System.out.println("popup_visible_flag : "+popup_visible_flag);
        if(popup_visible_flag.equalsIgnoreCase("true")){
            Intent intent=new Intent(CartPage.this, Select_catogory.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else {
            finish();
        }
    }
}
