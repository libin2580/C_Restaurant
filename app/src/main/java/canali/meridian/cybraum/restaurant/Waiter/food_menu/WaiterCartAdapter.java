package canali.meridian.cybraum.restaurant.Waiter.food_menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import canali.meridian.cybraum.restaurant.R;
import es.dmoral.toasty.Toasty;

/**
 * Created by Anvin on 11/20/2017.
 */

public class WaiterCartAdapter extends RecyclerView.Adapter<WaiterCartAdapter.ViewHolder> {
    private Context appContext;
    private ArrayList<MenuModel> arrayValues;
    DatabaseHelper db;
    Activity appactivity;

    Typeface custom_font;

    public WaiterCartAdapter(Context context, ArrayList<MenuModel> values,Activity act) {
        this.appactivity=act;
        this.appContext = context;
        this.arrayValues = values;
        custom_font = Typeface.createFromAsset(appContext.getAssets(),  "fonts/Raleway-Regular.ttf");
        db = new DatabaseHelper(context);

        System.out.println("Arraylist Size : " + arrayValues.size());
        for (MenuModel tm : arrayValues) {
            System.out.println("##############################################################################");
            System.out.println("ID = " + tm.getItem_id());
            System.out.println("TITLE = " + tm.getItem());
            System.out.println("##############################################################################");
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waiter_cart_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.txt_item.setText(arrayValues.get(position).getItem());
        holder.txt_count.setText(arrayValues.get(position).getCount());
        int count,prize;
        count= Integer.parseInt(arrayValues.get(position).getCount());
        prize= Integer.parseInt(arrayValues.get(position).getPrice());
        prize=count*prize;
        holder.txt_price.setText("\u20B9 "+String.valueOf(prize));
        Glide
                .with(appContext)
                .load(arrayValues.get(position).getImage())
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
                .into(holder.item_image);


        holder.linear_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count=Integer.parseInt(holder.txt_count.getText().toString());
                if(count<=1)
                {
                    holder.txt_price.setText("\u20B9 "+arrayValues.get(position).getPrice());

                    count=1;
                    holder.txt_count.setText(Integer.toString(count));
                    Toasty.warning(appContext,"minimum count is 1",Toast.LENGTH_SHORT).show();
                }
                else {

                    count=count-1;
                    holder.txt_count.setText(Integer.toString(count));
                    int price=count*Integer.parseInt(arrayValues.get(position).getPrice());

                            holder.txt_price.setText("\u20B9 "+Integer.toString(price));
                }

            }
        });
        holder.linear_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count=Integer.parseInt(holder.txt_count.getText().toString());
                if(count>=20)
                {


                    count=20;
                    holder.txt_count.setText(Integer.toString(count));
                    Toasty.warning(appContext,"maximum count is 20",Toast.LENGTH_SHORT).show();
                    int price=count*Integer.parseInt(arrayValues.get(position).getPrice());

                    holder.txt_price.setText("\u20B9 "+Integer.toString(price));
                }
                else {
                    count=count+1;
                    holder.txt_count.setText(Integer.toString(count));
                    int price=count*Integer.parseInt(arrayValues.get(position).getPrice());

                    holder.txt_price.setText("\u20B9 "+Integer.toString(price));
                }

            }
        });

        holder.linear_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(appactivity)

                        .setMessage("Are you sure you want to delete this item")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
// continue with delete



                                db.DeleteRow(arrayValues.get(position).getTable_id());
                                arrayValues.remove(position);
                                notifyDataSetChanged();
                                int size=db.getAllDatas().size();
                                CartPage.txt_cart_number.setText(Integer.toString(size));
                                if(size==0)
                                {
                                    appactivity.finish();
                                }

                                Toasty.warning(appContext, "Deleted", Toast.LENGTH_SHORT).show();
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
       /* if(position==arrayValues.size()-1)
        {
            holder.view_seperator.setVisibility(View.GONE);
        }*/
    }
    @Override
    public int getItemCount() {
        return arrayValues.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_item,txt_count,txt_price;
        ImageView item_image;
        LinearLayout linear_minus,linear_plus,linear_delete;
        View view_seperator;
        public ViewHolder(View itemView) {
            super(itemView);
            txt_item=(TextView)itemView.findViewById(R.id.txt_item);
            txt_count=(TextView)itemView.findViewById(R.id.txt_count);
            txt_price=(TextView)itemView.findViewById(R.id.txt_price);
            linear_minus=(LinearLayout) itemView.findViewById(R.id.linear_minus);
            linear_plus=(LinearLayout) itemView.findViewById(R.id.linear_plus);
            linear_delete=(LinearLayout) itemView.findViewById(R.id.linear_delete);

            item_image=(ImageView) itemView.findViewById(R.id.item_image);
            view_seperator=(View)itemView.findViewById(R.id.view_seperator);

            txt_item .setTypeface(custom_font);
        }
    }
}
