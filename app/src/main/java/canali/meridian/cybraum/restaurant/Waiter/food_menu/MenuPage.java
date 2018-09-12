package canali.meridian.cybraum.restaurant.Waiter.food_menu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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

import canali.meridian.cybraum.restaurant.CheckAndRequestPermission;
import canali.meridian.cybraum.restaurant.HttpHandler;
import canali.meridian.cybraum.restaurant.NDSpinner;
import canali.meridian.cybraum.restaurant.NetworkCheckingClass;
import canali.meridian.cybraum.restaurant.R;
import es.dmoral.toasty.Toasty;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static canali.meridian.cybraum.restaurant.CanaliConstans.Canali_Bas;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MenuPage extends AppCompatActivity {
    EditText edt_search;
    TextView  txt_spinnert_content,txt_category_content;
    public static TextView txt_cart_number;
    NDSpinner menu_spinner,category_spinner;
    LinearLayout linear_spinner_layout,linear_category_layout,linear_category_container,linear_view_orders;
    ProgressBar progress_bar;
    public static PopupWindow mPopupWindow;
    public static View customView;
    String[] arraySpinner,arrayMenuIds;
    String menu_obj_value = "",all_menu_obj_value = "";
    MenuModel mm;
    ArrayList<MenuModel> menuArraylist,AllMenuArrayListForSearch;
    RecyclerView menu_items_recyclerview,all_search_recyclerview;
    WaiterMenuAdapter wma;
    WaiterAllMenuAdapter wAllma;
    public static RelativeLayout parent_layout;
    public static String row_price,row_image,row_sub_category,row_item,row_item_id,count;
    public static LinearLayout popup_add_to_cart,popup_minus,popup_plus;
    public static TextView popup_item,popup_price,popup_count;
    public static ImageView popup_close,popup_image;
    DatabaseHelper db;
    LinearLayout linear_cart;
    public static Context context;
    String table_id,hut_id;
    Typeface custom_font;
    ArrayList subCategoriesArraylist;
    String sub_category_status;
    public static String kot_number="";
    ArrayList<MenuModel> filterdNamesAllMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);
        edt_search = (EditText) findViewById(R.id.edt_search);
        txt_cart_number = (TextView) findViewById(R.id.txt_cart_number);
        txt_spinnert_content = (TextView) findViewById(R.id.txt_spinnert_content);
        txt_category_content=(TextView)findViewById(R.id.txt_category_content);
        linear_category_container=(LinearLayout)findViewById(R.id.linear_category_container);
        menu_spinner = (NDSpinner) findViewById(R.id.menu_spinner);
        category_spinner=(NDSpinner)findViewById(R.id.category_spinner);
        linear_spinner_layout = (LinearLayout) findViewById(R.id.linear_spinner_layout);
        linear_category_layout=(LinearLayout)findViewById(R.id.linear_category_layout);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        parent_layout=(RelativeLayout)findViewById(R.id.parent_layout);
        linear_cart=(LinearLayout)findViewById(R.id.linear_cart);
        linear_view_orders=(LinearLayout)findViewById(R.id.linear_view_orders);
        subCategoriesArraylist=new ArrayList();
        AllMenuArrayListForSearch=new ArrayList<>();
        filterdNamesAllMenu = new ArrayList<>();
        menuArraylist=new ArrayList<>();
        menu_items_recyclerview=(RecyclerView)findViewById(R.id.menu_items_recyclerview);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        menu_items_recyclerview.setLayoutManager(layoutManager);

        all_search_recyclerview=(RecyclerView)findViewById(R.id.all_search_recyclerview);
        RecyclerView.LayoutManager layoutManager2=new LinearLayoutManager(this);
        all_search_recyclerview.setLayoutManager(layoutManager2);

        db = new DatabaseHelper(getApplicationContext());

        table_id=getIntent().getStringExtra("table_id");
        hut_id=getIntent().getStringExtra("hut_id");
        if(kot_number!=null){
            kot_number=getIntent().getStringExtra("kot_number");
        }
        else {
            kot_number="";
        }



        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Raleway-Regular.ttf");
        edt_search.setTypeface(custom_font);
        txt_cart_number.setTypeface(custom_font);
        txt_spinnert_content.setTypeface(custom_font);
        txt_category_content.setTypeface(custom_font);


CheckAndRequestPermission cp=new CheckAndRequestPermission();
cp.checkAndRequestPermissions(MenuPage.this);


        context=getApplicationContext();

       /* NetworkCheckingClass nc = new NetworkCheckingClass(MenuPage.this);
        if (nc.ckeckinternet()) {
            new getAllMenusForSearch().execute();
        } else {
            Toasty.error(MenuPage.this, "Please enable internet connection", Toast.LENGTH_SHORT).show();
        }*/


        menu_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("arrayMenuIds[i] : "+arrayMenuIds[i]);
                txt_spinnert_content.setText(arraySpinner[i]);

                edt_search.setText("");

                if(!arrayMenuIds[i].equalsIgnoreCase("0"))
                {
                    menu_items_recyclerview.setVisibility(View.VISIBLE);
                    all_search_recyclerview.setVisibility(View.GONE);

                }else {
                    menu_items_recyclerview.setVisibility(View.GONE);
                    all_search_recyclerview.setVisibility(View.VISIBLE);
                }


                    NetworkCheckingClass nc = new NetworkCheckingClass(MenuPage.this);
                    if (nc.ckeckinternet()) {
                        get_categories(arrayMenuIds[i]);
                    } else {
                        Toasty.error(MenuPage.this, "Please enable internet connection", Toast.LENGTH_SHORT).show();
                    }


                //getSubmenussss(arraySpinner[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                txt_category_content.setText(subCategoriesArraylist.get(i).toString());
                getSubmenussss(subCategoriesArraylist.get(i).toString());

                System.out.println("<<<<<<<<<<<<<<<<<<subCategoriesArraylist>>>>>>>>>>>>>>>>>>>>>>"+subCategoriesArraylist.get(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input

                filter(editable.toString());
            }
        });


        linear_view_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MenuPage.this,ViewOrders.class);
                startActivity(i);
            }
        });

        LayoutInflater inflater2 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        customView = inflater2.inflate(R.layout.menu_popup, null);
        popup_add_to_cart=(LinearLayout) customView.findViewById(R.id.popup_add_to_cart);
        popup_minus=(LinearLayout)customView.findViewById(R.id.popup_minus);
        popup_plus=(LinearLayout)customView.findViewById(R.id.popup_plus);
        popup_item=(TextView)customView.findViewById(R.id.popup_item);
        popup_price=(TextView)customView.findViewById(R.id.popup_price);
        popup_count=(TextView)customView.findViewById(R.id.popup_count);
        popup_close=(ImageView)customView.findViewById(R.id.popup_close);
        popup_image=(ImageView)customView.findViewById(R.id.popup_image);

        popup_item.setTypeface(custom_font);




        popup_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!MenuPage.this.isFinishing() && mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }            }
        });


        popup_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count=popup_count.getText().toString();
                db.insertData(row_item,row_sub_category,row_image,row_price,row_item_id,count);
                if (!MenuPage.this.isFinishing() && mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
                   // mPopupWindow.dismiss();


                Toasty.success(getApplicationContext(), "Item added to cart", Toast.LENGTH_SHORT).show();

                if(sub_category_status.equalsIgnoreCase("true"))
                {
                    new refreshAllMenusForSearchWithoutNet().execute();//to mark selected row
                    getSubmenussss(txt_category_content.getText().toString());
                }else {
                    new refreshAllMenusForSearchWithoutNet().execute();//to mark selected row
                        getNoCategoryMenus();
                }
                //getSubmenussss(txt_spinnert_content.getText().toString());

            }
        });
        popup_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count=Integer.parseInt(popup_count.getText().toString());
                if(count<=1)
                {
                    popup_price.setText("\u20B9 "+row_price);
                    count=1;
                    popup_count.setText(Integer.toString(count));
                    Toasty.warning(getApplicationContext(),"minimum count is 1",Toast.LENGTH_SHORT).show();
                }
                else {
                    count=count-1;
                    popup_count.setText(Integer.toString(count));
                    int price=count*Integer.parseInt(row_price);

                    popup_price.setText("\u20B9 "+Integer.toString(price));
                }


            }
        });
        popup_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count=Integer.parseInt(popup_count.getText().toString());
                if(count>=20)
                {
                    count=20;
                    popup_count.setText(Integer.toString(count));
                    Toasty.warning(getApplicationContext(),"maximum count is 20",Toast.LENGTH_SHORT).show();
                    int price=count*Integer.parseInt(row_price);

                    popup_price.setText("\u20B9 "+Integer.toString(price));
                }
                else {
                    count=count+1;
                    popup_count.setText(Integer.toString(count));
                    int price=count*Integer.parseInt(row_price);

                    popup_price.setText("\u20B9 "+Integer.toString(price));
                }

            }
        });

        linear_spinner_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu_spinner.performClick();
            }
        });
        linear_category_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category_spinner.performClick();
            }
        });

        /*if (nc.ckeckinternet()) {
            new getMenuDetails().execute();
        } else {
            Toasty.error(MenuPage.this, "Please enable internet connection", Toast.LENGTH_SHORT).show();
        }*/

        linear_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txt_cart_number.getText().toString().equalsIgnoreCase("0")){
                    Toasty.warning(getApplicationContext(),"No items in cart",Toast.LENGTH_SHORT).show();
                }else {
                    Intent i = new Intent(MenuPage.this, CartPage.class);
                    i.putExtra("table_id", table_id);
                    i.putExtra("hut_id", hut_id);
                    i.putExtra("kot_no",kot_number);
                    startActivity(i);
                }
            }
        });
        edt_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                //filterdNamesAllMenu.clear();
                //menu_spinner.setSelection(0);
                AllMenuArrayListForSearch.clear();
                txt_spinnert_content.setText("Select Menu");
                linear_category_container.setVisibility(View.GONE);
                all_search_recyclerview.setVisibility(View.VISIBLE);
                menu_items_recyclerview.setVisibility(View.GONE);
                new refreshAllMenusForSearchWithoutNet().execute();
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); mgr.showSoftInput(view, InputMethodManager.SHOW_FORCED);
                return false;
            }
        });

    }

    public class getMenuDetails extends AsyncTask<String, String, String> {
        final KProgressHUD hud1= KProgressHUD.create(MenuPage.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress_bar.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                HttpHandler h = new HttpHandler();
                String jsonString = h.makeServiceCall(Canali_Bas+"menu.php");
                if (jsonString != null) {

                    try {
                        JSONObject jsonObj = new JSONObject(jsonString);
                        String status = jsonObj.getString("status");
                        if (status.equalsIgnoreCase("true")) {
                            JSONArray jsonArray = jsonObj.getJSONArray("menu");
                            //menu_obj_value = jsonObj.getJSONArray("menu").toString();//useed on selecting spinner
                            if (jsonArray.length() > 0) {
                                arraySpinner = new String[jsonArray.length()];
                                arrayMenuIds=new String[jsonArray.length()];
                                arraySpinner[0] = "Select Menu";
                                arrayMenuIds[0] = "0";
                                for (int i = 0; i <=jsonArray.length(); i++) {

                                    JSONObject menuObj = jsonArray.getJSONObject(i);
                                    arraySpinner[i+1] = menuObj.getString("menu_name");
                                    arrayMenuIds[i+1] = menuObj.getString("menu_id");

                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (arraySpinner.length > 0) {
                //for(int i=0;i<arraySpinner.length;i++){System.out.println("--------------- : "+arraySpinner[i]);}
                txt_spinnert_content.setText(arraySpinner[0]);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MenuPage.this,
                        R.layout.spinner_custom_layout, R.id.spinner_txtview, arraySpinner);
                menu_spinner.setAdapter(adapter);
                //System.out.println("menu_obj_value : " + menu_obj_value);
            } else {
                txt_spinnert_content.setText("Menu Empty");
            }
            if (!MenuPage.this.isFinishing() && hud1 != null) {
                hud1.dismiss();
            }
            //progress_bar.setVisibility(View.GONE);
        }
    }

    public void getSubmenussss(final String selected_value) {



     class getSubMenuss extends AsyncTask<String, String, String> {
         final KProgressHUD hud1= KProgressHUD.create(MenuPage.this)
                 .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                 .setLabel("Please wait")
                 .setCancellable(true)
                 .setAnimationSpeed(1)
                 .setDimAmount(0.5f)
                 .show();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            menuArraylist.clear();
            //progress_bar.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                   if (menu_obj_value != null) {
                       System.out.println("<<<<<<<<<<<<<<<<<<menu_obj_value>>>>>>>>>>>>>>>>>>>>>>"+menu_obj_value);

                       JSONArray jsonArray=new JSONArray(menu_obj_value);
                       if(jsonArray.length()>0){
                           for(int i=0;i<jsonArray.length();i++)
                           {
                               JSONObject jsonObj=jsonArray.getJSONObject(i);
                               if(jsonObj.getString("sub_category_name").equalsIgnoreCase(selected_value)){

                                   JSONArray jsonarray2=jsonObj.getJSONArray(selected_value);
                                   if(jsonarray2.length()>0) {
                                       for (int e = 0; e < jsonarray2.length(); e++) {
                                           JSONObject jsonObj2 = jsonarray2.getJSONObject(e);
                                           mm = new MenuModel();
                                           if (jsonObj2.has("item"))
                                               mm.setItem(jsonObj2.getString("item"));
                                           if (jsonObj2.has("id"))
                                               mm.setItem_id(jsonObj2.getString("id"));
                                           /*if (jsonObj2.has("sub_category"))
                                               mm.setSub_category(jsonObj2.getString("sub_category"));*/
                                           if (jsonObj2.has("Image"))
                                               mm.setImage(jsonObj2.getString("Image"));
                                           if (jsonObj2.has("price"))
                                               mm.setPrice(jsonObj2.getString("price"));

                                           menuArraylist.add(mm);
                                       }
                                   }
                               }
                           }
                       }


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(menuArraylist.size()>0)
            {
              /*  for(MenuModel mm:menuArraylist){
                    System.out.println("----------------------------------------------------");
                    System.out.println(mm.getItem_id());
                    System.out.println(mm.getImage());
                    System.out.println(mm.getItem());
                    System.out.println(mm.getPrice());
                    System.out.println(mm.getSub_category());
                }*/
                wma=new WaiterMenuAdapter(MenuPage.this,menuArraylist);
                menu_items_recyclerview.setAdapter(wma);
            }
            else {
                menuArraylist.clear();
                wma=new WaiterMenuAdapter(MenuPage.this,menuArraylist);
                menu_items_recyclerview.setAdapter(wma);
                Toasty.warning(MenuPage.this,"No items",Toast.LENGTH_SHORT).show();
            }

            //progress_bar.setVisibility(View.GONE);
            if (!MenuPage.this.isFinishing() && hud1 != null) {
                hud1.dismiss();
            }
        }
    }
        getSubMenuss gsm=new getSubMenuss();
        gsm.execute();
}

    public static void displayPopup() {
        popup_price.setText("\u20B9 "+row_price);
        popup_item.setText(row_item);
        popup_count.setText("1");
        MultiTransformation<Bitmap> multi = new MultiTransformation<>(
                new RoundedCornersTransformation(150, 0, RoundedCornersTransformation.CornerType.TOP));
        Glide
                .with(context)
                .load(row_image)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .apply(bitmapTransform(multi))
                .into(popup_image);
        try {
            System.out.println("inside display popup");
            mPopupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (Build.VERSION.SDK_INT >= 21) {
                mPopupWindow.setElevation(5.0f);
            }
            mPopupWindow.setFocusable(true);
            mPopupWindow.setAnimationStyle(R.style.popupAnimation);


            mPopupWindow.showAtLocation(parent_layout, Gravity.CENTER, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkCheckingClass nc = new NetworkCheckingClass(MenuPage.this);
        if (nc.ckeckinternet()) {
            new getMenuDetails().execute();
            new getAllMenusForSearch().execute();
        } else {
            Toasty.error(MenuPage.this, "Please enable internet connection", Toast.LENGTH_SHORT).show();
        }
    }
    private void filter(String text) {
        System.out.println("inside filter");
        //new array list that will hold the filtered data
        ArrayList<MenuModel> filterdNamesMenu = new ArrayList<>();

        //looping through existing elements
      // menuArraylist.clear();
       /* for (MenuModel s : menuArraylist) {
            //if the existing elements contains the search input
            if (s.getItem().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNamesMenu.add(s);
            }
        }*/

        filterdNamesAllMenu=new ArrayList<>();
        filterdNamesMenu.clear();

        for (MenuModel s : AllMenuArrayListForSearch) {
            //if the existing elements contains the search input
            if (s.getItem().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list

                filterdNamesMenu.add(s);
            }
        }
        //calling a method of the adapter class and passing the filtered list
        try {
           // wma.filterList(filterdNamesMenu);
            wAllma.filterList(filterdNamesMenu);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void get_categories(final String menu_id) {

        class SendToServer extends AsyncTask<String, String, String> {

            final KProgressHUD hud1 = KProgressHUD.create(MenuPage.this)
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
                    URL url = new URL(Canali_Bas+"menu_by_category.php");

                    JSONObject postDataParams = new JSONObject();
                    postDataParams.put("menu_id",menu_id );

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


                        String has_sub_category=jsonObj.getString("sub_category");

                        sub_category_status=has_sub_category;//for refersging recycl;erview inorder to show yellow box

                        if(has_sub_category.equalsIgnoreCase("true")){
                            linear_category_container.setVisibility(View.VISIBLE);
                                subCategoriesArraylist.clear();
                                menu_obj_value="";
                                try {
                                    menu_obj_value=jsonObj.getJSONArray("menu").toString();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                System.out.println("menu_obj_value : "+menu_obj_value);
                                JSONArray jsonArray=jsonObj.getJSONArray("menu");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject menuObj=jsonArray.getJSONObject(i);
                                    if(!subCategoriesArraylist.contains(menuObj.get("sub_category_name")))
                                    {
                                        subCategoriesArraylist.add(menuObj.get("sub_category_name"));

                                    }
                                }

                        //////////////////adding to category soinner --start
                            System.out.println("subCategoriesArraylist.size() : "+subCategoriesArraylist.size());
                            if (subCategoriesArraylist.size() > 0) {
                                txt_category_content.setText(subCategoriesArraylist.get(0).toString());
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MenuPage.this,
                                        R.layout.spinner_custom_layout, R.id.spinner_txtview, subCategoriesArraylist);
                                category_spinner.setAdapter(adapter);
                                //System.out.println("menu_obj_value : " + menu_obj_value);
                            } else {
                                txt_category_content.setText("Category Empty");
                            }
                            //////////////////adding to category soinner --end

                        }else{
                            menu_obj_value="";
                            try {
                                menu_obj_value = jsonObj.getJSONArray("menu").toString();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            System.out.println("menu_obj_value : "+menu_obj_value);
                            ///////////////////////// if there is no subcategory --start
                            linear_category_container.setVisibility(View.GONE);


                                getNoCategoryMenus();

                            ///////////////////////// if there is no subcategory --end
                        }

                    } else {
                        Toasty.error(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                    //progress_bar.setVisibility(View.GONE);
                    if (!MenuPage.this.isFinishing() && hud1 != null) {
                        hud1.dismiss();
                    }

                } catch (Exception e) {
                    //progress_bar.setVisibility(View.GONE);
                    if (!MenuPage.this.isFinishing() && hud1 != null) {
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


    public void getNoCategoryMenus()
    {

        class NoCategoryMenus extends AsyncTask<String, String, String> {
            final KProgressHUD hud1= KProgressHUD.create(MenuPage.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setCancellable(true)
                    .setAnimationSpeed(1)
                    .setDimAmount(0.5f)
                    .show();
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                menuArraylist.clear();
                //progress_bar.setVisibility(View.VISIBLE);

            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    if (menu_obj_value != null) {
                        JSONArray jsonArray=new JSONArray(menu_obj_value);
                        if(jsonArray.length()>0){
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObj=jsonArray.getJSONObject(i);

                                            mm = new MenuModel();
                                            if (jsonObj.has("item"))
                                                mm.setItem(jsonObj.getString("item"));
                                            if (jsonObj.has("id"))
                                                mm.setItem_id(jsonObj.getString("id"));
                                           /*if (jsonObj2.has("sub_category"))
                                               mm.setSub_category(jsonObj2.getString("sub_category"));*/
                                            if (jsonObj.has("Image"))
                                                mm.setImage(jsonObj.getString("Image"));
                                            if (jsonObj.has("price"))
                                                mm.setPrice(jsonObj.getString("price"));

                                            menuArraylist.add(mm);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(menuArraylist.size()>0)
                {
                   /* for(MenuModel mm:menuArraylist){
                        System.out.println("----------------------------------------------------");
                        System.out.println(mm.getItem_id());
                        System.out.println(mm.getImage());
                        System.out.println(mm.getItem());
                        System.out.println(mm.getPrice());
                        System.out.println(mm.getSub_category());
                    }*/
                    wma=new WaiterMenuAdapter(MenuPage.this,menuArraylist);
                    menu_items_recyclerview.setAdapter(wma);
                }
                else {
                    menuArraylist.clear();
                    wma=new WaiterMenuAdapter(MenuPage.this,menuArraylist);
                    menu_items_recyclerview.setAdapter(wma);
                    if(!txt_spinnert_content.getText().toString().equalsIgnoreCase("Select Menu")) {// if menu id not '0' ,that is if menu is not diaplaying 'Select Menu'

                        Toasty.warning(MenuPage.this,"No items",Toast.LENGTH_SHORT).show();
                    }

                }

                //progress_bar.setVisibility(View.GONE);
                if (!MenuPage.this.isFinishing() && hud1 != null) {
                    hud1.dismiss();
                }
            }
        }
        NoCategoryMenus gsm=new NoCategoryMenus();
        gsm.execute();
    }
    public class getAllMenusForSearch extends AsyncTask<String, String, String> {
        final KProgressHUD hud1= KProgressHUD.create(MenuPage.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress_bar.setVisibility(View.VISIBLE);
            all_menu_obj_value="";
            AllMenuArrayListForSearch.clear();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                HttpHandler h = new HttpHandler();
                String jsonString = h.makeServiceCall(Canali_Bas+"all_menu.php");
                if (jsonString != null) {

                    try {
                        JSONObject jsonObj = new JSONObject(jsonString);
                        String status = jsonObj.getString("status");
                        if (status.equalsIgnoreCase("true")) {
                            all_menu_obj_value="";
                            try {
                                all_menu_obj_value = jsonObj.getJSONArray("menu").toString();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            //JSONArray jsonArray = jsonObj.getJSONArray("menu");
                            //MenuModel mm;
                            /*if (jsonArray.length() > 0) {
                                for(int i=0;i<jsonArray.length();i++) {
                                    mm = new MenuModel();
                                    JSONObject dataObj = jsonArray.getJSONObject(i);
                                    if (dataObj.has("item"))
                                        mm.setItem(dataObj.getString("item"));
                                    if (dataObj.has("id"))
                                        mm.setItem_id(dataObj.getString("id"));
                                    if (dataObj.has("sub_category"))
                                        mm.setSub_category(dataObj.getString("sub_category"));
                                    if (dataObj.has("Image"))
                                        mm.setImage(dataObj.getString("Image"));
                                    if (dataObj.has("price"))
                                        mm.setPrice(dataObj.getString("price"));

                                    AllMenuArrayListForSearch.add(mm);


                                }

                            }*/
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!MenuPage.this.isFinishing() && hud1 != null) {
                hud1.dismiss();
            }
                new refreshAllMenusForSearchWithoutNet().execute();
            /*if(AllMenuArrayListForSearch.size()>0)
            {
                wAllma=new WaiterAllMenuAdapter(MenuPage.this,AllMenuArrayListForSearch);
                all_search_recyclerview.setAdapter(wAllma);
            }
            else {
                AllMenuArrayListForSearch.clear();
                wAllma=new WaiterAllMenuAdapter(MenuPage.this,AllMenuArrayListForSearch);
                all_search_recyclerview.setAdapter(wAllma);
                Toasty.warning(MenuPage.this,"No Menu Items",Toast.LENGTH_SHORT).show();
            }
*/


        }
    }
    public class refreshAllMenusForSearchWithoutNet extends AsyncTask<String, String, String> {
        final KProgressHUD hud1= KProgressHUD.create(MenuPage.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress_bar.setVisibility(View.VISIBLE);

            AllMenuArrayListForSearch.clear();

        }

        @Override
        protected String doInBackground(String... strings) {
            JSONArray jsonArray=null;
            try {


                    try {
                        if(all_menu_obj_value!="") {

                          jsonArray = new JSONArray(all_menu_obj_value);
                            //menu_obj_value = jsonObj.getJSONArray("menu").toString();//useed on selecting spinner
                            MenuModel mm;
                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    mm = new MenuModel();
                                    JSONObject dataObj = jsonArray.getJSONObject(i);
                                    if (dataObj.has("item"))
                                        mm.setItem(dataObj.getString("item"));
                                    if (dataObj.has("id"))
                                        mm.setItem_id(dataObj.getString("id"));
                                    if (dataObj.has("sub_category"))
                                        mm.setSub_category(dataObj.getString("sub_category"));
                                    if (dataObj.has("Image"))
                                        mm.setImage(dataObj.getString("Image"));
                                    if (dataObj.has("price"))
                                        mm.setPrice(dataObj.getString("price"));

                                    AllMenuArrayListForSearch.add(mm);


                                }

                            }
                        }



                    } catch (Exception e) {
                        e.printStackTrace();
                    }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);



            if(AllMenuArrayListForSearch.size()>0)
            {
                wAllma=new WaiterAllMenuAdapter(MenuPage.this,AllMenuArrayListForSearch);
                all_search_recyclerview.setAdapter(wAllma);
            }
            else {
                AllMenuArrayListForSearch.clear();
                wAllma=new WaiterAllMenuAdapter(MenuPage.this,AllMenuArrayListForSearch);
                all_search_recyclerview.setAdapter(wAllma);
                //Toasty.warning(MenuPage.this,"No Menu Items",Toast.LENGTH_SHORT).show();
            }

            if (!MenuPage.this.isFinishing() && hud1 != null) {
                hud1.dismiss();
            }
            //progress_bar.setVisibility(View.GONE);
        }
    }


}
