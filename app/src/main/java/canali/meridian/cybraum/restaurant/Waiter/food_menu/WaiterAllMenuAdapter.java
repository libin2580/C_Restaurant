package canali.meridian.cybraum.restaurant.Waiter.food_menu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import canali.meridian.cybraum.restaurant.R;
import es.dmoral.toasty.Toasty;

/**
 * Created by Anvin on 11/20/2017.
 */

public class WaiterAllMenuAdapter extends RecyclerView.Adapter<WaiterAllMenuAdapter.ViewHolder> {
    private Context appContext;
    private ArrayList<MenuModel> arrayValues;
    DatabaseHelper db;
    Typeface custom_font;
    ArrayList<MenuModel>dbArraylist;
    ArrayList<String>dbIdArrayLsit;


    public WaiterAllMenuAdapter(Context context, ArrayList<MenuModel> values) {
        this.appContext = context;
        this.arrayValues = values;
        db = new DatabaseHelper(context);
        dbIdArrayLsit=new ArrayList<>();
        System.out.println("Arraylist Size : " + arrayValues.size());
        custom_font = Typeface.createFromAsset(context.getAssets(),  "fonts/Raleway-Regular.ttf");


        /*for (MenuModel tm : arrayValues) {
            System.out.println("##############################################################################");
            System.out.println("ID = " + tm.getItem_id());
            System.out.println("TITLE = " + tm.getItem());
            System.out.println("##############################################################################");
        }*/
        dbArraylist=db.getAllDatas();
        if(dbArraylist.size()>0)
        {
            for(MenuModel mm:dbArraylist){
                dbIdArrayLsit.add(mm.getItem_id());
            }
        }

        MenuPage.txt_cart_number.setText(Integer.toString(dbArraylist.size()));

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waiter_menu_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        MenuPage.txt_cart_number.setText(Integer.toString(dbArraylist.size()));

        if(dbIdArrayLsit.contains(arrayValues.get(position).getItem_id()))
        {
            holder.linear_tikmark.setBackgroundColor(Color.parseColor("#dcae63"));
            holder.row_image_tick.setVisibility(View.VISIBLE);
            holder.linear_row.setBackgroundColor(Color.parseColor("#c09a5b"));

        }else {
            holder.linear_tikmark.setBackgroundColor(Color.TRANSPARENT);
            holder.linear_row.setBackgroundColor(Color.TRANSPARENT);
            holder.row_image_tick.setVisibility(View.GONE);
        }

        holder.row_item.setText(arrayValues.get(position).getItem());
        holder.row_price.setText("\u20B9 "+arrayValues.get(position).getPrice());
        holder.linear_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dbIdArrayLsit.contains(arrayValues.get(position).getItem_id()))
                {
                    Toasty.warning(appContext,"Already Added",Toast.LENGTH_SHORT).show();
                }else {
                    MenuPage.row_item_id = arrayValues.get(position).getItem_id();
                    MenuPage.row_image = arrayValues.get(position).getImage();
                    MenuPage.row_price = arrayValues.get(position).getPrice();
                    MenuPage.row_sub_category = arrayValues.get(position).getSub_category();
                    MenuPage.row_item = arrayValues.get(position).getItem();

                    MenuPage.displayPopup();
                }
            }

        });

        /*if(position==arrayValues.size()-1)
        {
            holder.view_seperator.setVisibility(View.GONE);
        }*/
    }

    @Override
    public int getItemCount() {
        return arrayValues.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView row_item,row_price;
        ImageView row_image_tick;
        LinearLayout linear_row,linear_tikmark;
        View view_seperator;
        public ViewHolder(View itemView) {
            super(itemView);
            row_item=(TextView)itemView.findViewById(R.id.row_item);
            row_price=(TextView)itemView.findViewById(R.id.row_price);
            row_image_tick=(ImageView) itemView.findViewById(R.id.row_image_tick);
            linear_row=(LinearLayout)itemView.findViewById(R.id.linear_row);
            view_seperator=(View)itemView.findViewById(R.id.view_seperator);
            linear_tikmark=(LinearLayout)itemView.findViewById(R.id.linear_tikmark);

            row_item.setTypeface(custom_font);




        }
    }
    public void filterList(ArrayList<MenuModel> filterdNames) {
       // this.arrayValues.clear();
        this.arrayValues = filterdNames;
        notifyDataSetChanged();
    }
    /*public void setFilter(List<MenuModel> doctorModels){
        arrayValues = new ArrayList<>();
        arrayValues.addAll(doctorModels);
        notifyDataSetChanged();
    }*/
}
