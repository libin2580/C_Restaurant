package canali.meridian.cybraum.restaurant.Kitchen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import canali.meridian.cybraum.restaurant.R;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Ansal on 11/20/2017.
 */

class Kitchen_pop_adaptor extends RecyclerView.Adapter<Kitchen_pop_adaptor.ViewHolder> {

    ArrayList<Kit_model> Kitchen_pop_list;
    Context mcontext;
    public Kitchen_pop_adaptor(ArrayList<Kit_model> list, Context context ) {
        Kitchen_pop_list =list;
        mcontext=context;
    }

    @Override
    public Kitchen_pop_adaptor.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.kitchen_pop_adapter, viewGroup, false);
        return new Kitchen_pop_adaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final Kitchen_pop_adaptor.ViewHolder viewHolder, final int i) {
        viewHolder.sts.setText(Kitchen_pop_list.get(i).getsts());
        viewHolder.Item_name.setText(Kitchen_pop_list.get(i).getpop_item_name());
        viewHolder.order_no.setText(Kitchen_pop_list.get(i).getpop_count()+"nos");
        viewHolder.prize.setText("\u20B9 "+Kitchen_pop_list.get(i).getpop_price());
        MultiTransformation<Bitmap> multi = new MultiTransformation<>(
                new RoundedCornersTransformation(50, 0, RoundedCornersTransformation.CornerType.TOP),
                new RoundedCornersTransformation(50, 0, RoundedCornersTransformation.CornerType.BOTTOM));
        Glide.with(mcontext)
                .load(Kitchen_pop_list.get(i).getpopimage())
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
                .into(viewHolder.Image);

    }


    @Override
    public int getItemCount() {
        return Kitchen_pop_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView Item_name,order_no,prize,sts;
        ImageView Image;


        public ViewHolder(View view) {
            super(view);

            Item_name = (TextView) view.findViewById(R.id.item_name);
            order_no = (TextView) view.findViewById(R.id.order_no);
            prize = (TextView) view.findViewById(R.id.prize);
            sts = (TextView) view.findViewById(R.id.sts);
            Image = (ImageView) view.findViewById(R.id.img);

        }
    }

}