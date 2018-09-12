package canali.meridian.cybraum.restaurant.Waiter.food_menu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import canali.meridian.cybraum.restaurant.R;
import es.dmoral.toasty.Toasty;

import static canali.meridian.cybraum.restaurant.CanaliConstans.Canali_Bas;

/**
 * Created by Ansal on 11/23/2017.
 */

public class View_items_adaptor extends RecyclerView.Adapter<View_items_adaptor.ViewHolder> {

    ArrayList<View_model> Kitchen_list;
    Context mcontext;
    Typeface custom_font;
    Activity appactivity;
    public View_items_adaptor(View_items view_items, ArrayList<View_model> list, Context context) {
        Kitchen_list =list;
        mcontext=context;
        appactivity=view_items;
        custom_font = Typeface.createFromAsset(mcontext.getAssets(),  "fonts/Raleway-Regular.ttf");

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_item_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.Item_txt.setText(Kitchen_list.get(i).getitem_name());
        viewHolder.price_txt.setText("\u20B9 "+Kitchen_list.get(i).getprice());
        viewHolder.qty_txt.setText(Kitchen_list.get(i).getquantity());
        viewHolder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(Kitchen_list.get(i).getorder_item_id());
                appactivity.finish();
                appactivity.startActivity(appactivity.getIntent());
            }
        });
    }

    public void delete(final String item_id) {


        String URL=Canali_Bas+"delete_item.php";
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
                                Toasty.warning(mcontext, messege, Toast.LENGTH_SHORT, true).show();

                            }
                            else {
                                String messege=object.getString("message");
                                Toasty.success(mcontext, messege, Toast.LENGTH_SHORT, true).show();

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
                params.put("item_id",item_id);
                Log.d("hash", String.valueOf(params));
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(mcontext);
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }


    @Override
    public int getItemCount() {
        return Kitchen_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView Item_txt,price_txt,qty_txt;
        LinearLayout Delete;
        public ViewHolder(View view) {
            super(view);

            Item_txt = (TextView) view.findViewById(R.id.txt_item);
            price_txt = (TextView) view.findViewById(R.id.price_txt);
            qty_txt = (TextView) view.findViewById(R.id.qty_text);
            Delete = (LinearLayout) view.findViewById(R.id.linear_delete);



        }
    }

}