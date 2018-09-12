package canali.meridian.cybraum.restaurant.Waiter.table;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import canali.meridian.cybraum.restaurant.R;
import canali.meridian.cybraum.restaurant.Waiter.food_menu.MenuPage;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Anvin on 11/18/2017.
 */

public class TableSelectionAdapter extends RecyclerView.Adapter<TableSelectionAdapter.ViewHolder> {
    private Context appContext;
    private ArrayList<TableSelectionModel> arrayValues;
    int row_index=-1;



    public TableSelectionAdapter(Context context, ArrayList<TableSelectionModel> values) {
        this.appContext = context;
        this.arrayValues = values;


        System.out.println("Arraylist Size : " + arrayValues.size());


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_table_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SharedPreferences preferences =appContext.getSharedPreferences("Category",MODE_PRIVATE);
        String cat_type=preferences.getString("category", null);
        if(cat_type.equals("ac")){
            holder.img_android.setVisibility(View.VISIBLE);
            holder.img_android1.setVisibility(View.GONE);
            holder.number_text.setText(arrayValues.get(position).getNumber());
            holder.row_relative_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    row_index=position;
                    Intent i=new Intent(appContext, MenuPage.class);
                    i.putExtra("table_id",arrayValues.get(position).getId());
                    i.putExtra("kot_number","");
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    appContext.startActivity(i);
                    notifyDataSetChanged();
                }
            });
            if(row_index==position){

                holder.img_android.setBackgroundResource(R.drawable.yellow);
            }
            else
            {

                holder.img_android.setBackgroundResource(R.drawable.copy_rounded_rectangle);
            }
        }
        if(cat_type.equals("hut")){
            holder.img_android.setVisibility(View.GONE);
            holder.img_android1.setVisibility(View.VISIBLE);
            holder.number_text1.setText(arrayValues.get(position).getNumber());
            holder.row_relative_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    row_index=position;
                    Intent i=new Intent(appContext, MenuPage.class);
                    i.putExtra("hut_id",arrayValues.get(position).getId());
                    i.putExtra("kot_number","");
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    appContext.startActivity(i);
                    notifyDataSetChanged();
                /*    notifyDataSetChanged();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    },100);*/
                }
            });
            if(row_index==position){

                holder.img_android1.setBackgroundResource(R.drawable.yellow);
            }
            else
            {

                holder.img_android1.setBackgroundResource(R.drawable.copy_rounded_rectangle);
            }
        }

    }

    @Override
    public int getItemCount() {
        return arrayValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout img_android,img_android1;
        TextView number_text,number_text1;
        LinearLayout row_relative_layout;
        public ViewHolder(View itemView) {
            super(itemView);
            img_android=(LinearLayout)itemView.findViewById(R.id.img_android);
            number_text=(TextView) itemView.findViewById(R.id.number_text);
            row_relative_layout=(LinearLayout)itemView.findViewById(R.id.row_relative_layout);
            img_android1=(LinearLayout)itemView.findViewById(R.id.img_android1);
            number_text1=(TextView) itemView.findViewById(R.id.number_text1);

        }
    }
}