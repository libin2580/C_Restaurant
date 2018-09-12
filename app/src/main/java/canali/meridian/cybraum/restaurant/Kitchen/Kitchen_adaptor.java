package canali.meridian.cybraum.restaurant.Kitchen;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import canali.meridian.cybraum.restaurant.R;

/**
 * Created by Ansal on 11/20/2017.
 */

public class Kitchen_adaptor extends RecyclerView.Adapter<Kitchen_adaptor.ViewHolder> {

    ArrayList<Kit_model> Kitchen_list;
    Context mcontext;
    Typeface custom_font;
    public Kitchen_adaptor(ArrayList<Kit_model> list, Context context ) {
        Kitchen_list =list;
        mcontext=context;
        custom_font = Typeface.createFromAsset(mcontext.getAssets(),  "fonts/Raleway-Regular.ttf");

    }

    @Override
    public Kitchen_adaptor.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.kitchen_adapter, viewGroup, false);
        return new Kitchen_adaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final Kitchen_adaptor.ViewHolder viewHolder, final int i) {
        viewHolder.num_txt.setText(String.valueOf(i+1)+".");
        viewHolder.Kot_txt.setText(Kitchen_list.get(i).getkot_no());
        viewHolder.order_sts.setText(Kitchen_list.get(i).getorder_status());
        if(Kitchen_list.get(i).getorder_status().equals("Pending")){
            viewHolder.Sts.setBackgroundResource(R.drawable.pending_box);
        }
        if(Kitchen_list.get(i).getorder_status().equals("Accepted")){
            viewHolder.Sts.setBackgroundResource(R.drawable.accepted_box);
        }
        if(Kitchen_list.get(i).getorder_status().equals("Delivered")){
            viewHolder.Sts.setBackgroundResource(R.drawable.deliverd_box);
        }
    }


    @Override
    public int getItemCount() {
        return Kitchen_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView Kot_txt,order_sts,num_txt;
        LinearLayout Sts;

        public ViewHolder(View view) {
            super(view);

            Kot_txt = (TextView) view.findViewById(R.id.kot_text);
            order_sts = (TextView) view.findViewById(R.id.status);
            num_txt = (TextView) view.findViewById(R.id.num_txt);
            Sts = (LinearLayout) view.findViewById(R.id.sts);


            order_sts .setTypeface(custom_font);


        }
    }

}