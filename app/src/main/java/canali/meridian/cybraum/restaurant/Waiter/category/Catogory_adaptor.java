package canali.meridian.cybraum.restaurant.Waiter.category;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import canali.meridian.cybraum.restaurant.Waiter.table.TableSelection;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Ansal on 11/18/2017.
 */

public class Catogory_adaptor extends RecyclerView.Adapter<Catogory_adaptor.ViewHolder> {

    ArrayList<Catogory_model> Catogory_list;
    Context mcontext;
    int row_index=-1;
    Typeface custom_font;

    public Catogory_adaptor(ArrayList<Catogory_model> list, Context context ) {
        Catogory_list =list;
        mcontext=context;
        custom_font = Typeface.createFromAsset(mcontext.getAssets(),  "fonts/Raleway-Regular.ttf");

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.catogory_adapter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
      final SharedPreferences  preferences =mcontext.getSharedPreferences("Category",MODE_PRIVATE);
        viewHolder.cat_text.setText(Catogory_list.get(i).getcat_name());
        Glide.with(mcontext)
                .load(Catogory_list.get(i).getcat_image())
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
                .into(viewHolder.cat_image);

                    viewHolder.Layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            row_index=i;
                            if(i==0){
                                /*Intent intent = new Intent(mcontext, MenuPage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mcontext.startActivity(intent);*/
                                notifyDataSetChanged();
                                Toasty.warning(mcontext, "No Service", Toast.LENGTH_SHORT, true).show();
                            }
                            if(i==1){
                                /*Intent intent = new Intent(mcontext, MenuPage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mcontext.startActivity(intent);*/
                                Toasty.warning(mcontext, "No Service", Toast.LENGTH_SHORT, true).show();
                                notifyDataSetChanged();
                            }
                            if(i==2){
                                SharedPreferences.Editor editor1 = preferences.edit();
                                editor1.putString("category","hut");
                                editor1.apply();
                                Intent intent = new Intent(mcontext, TableSelection.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mcontext.startActivity(intent);
                                notifyDataSetChanged();
                            }
                            if(i==3){
                                SharedPreferences.Editor editor1 = preferences.edit();
                                editor1.putString("category","ac");
                                editor1.apply();
                            Intent intent = new Intent(mcontext, TableSelection.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mcontext.startActivity(intent);
                                notifyDataSetChanged();
                            }
                            if(i==4){
                            /*    Intent intent = new Intent(mcontext, MenuPage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mcontext.startActivity(intent);*/
                                Toasty.warning(mcontext, "No Service", Toast.LENGTH_SHORT, true).show();
                                notifyDataSetChanged();
                            }



                        }
                    });

        if(row_index==i){

            viewHolder.tick_mark.setVisibility(View.VISIBLE);
        }
        else
        {

            viewHolder.tick_mark.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return Catogory_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView cat_image,tick_mark;
        TextView cat_text;
        LinearLayout Layout;

        public ViewHolder(View view) {
            super(view);
            tick_mark = (ImageView)view.findViewById(R.id.icon);
            cat_image = (ImageView)view.findViewById(R.id.cat_image);
            cat_text = (TextView) view.findViewById(R.id.cat_text);
            Layout = (LinearLayout) view.findViewById(R.id.layout);

            cat_text .setTypeface(custom_font);
        }
    }

}