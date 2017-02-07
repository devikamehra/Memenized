package awe.devikamehra.memenized.view.adapter;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import awe.devikamehra.memenized.Application;
import awe.devikamehra.memenized.R;
import awe.devikamehra.memenized.control.widget.WidgetProvider;
import awe.devikamehra.memenized.rest.model.BitmapObject;

import static awe.devikamehra.memenized.Application.getApplication;

/**
 * Created by Devika on 06-02-2017.
 */

public class MemeDisplayAdapter extends RecyclerView.Adapter<MemeDisplayAdapter.MemeDisplayViewHolder> {

    private List<String> strings;
    private Context context;

    public MemeDisplayAdapter(Context context, List<String> strings) {
        this.context = context;
        this.strings = strings;
    }

    @Override
    public MemeDisplayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.meme_display_item, parent, false);
        return new MemeDisplayViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final MemeDisplayViewHolder holder, final int position) {
        Picasso.with(context)
                .load(strings.get(position))
                .error(new IconDrawable(context, MaterialIcons.md_error))
                .resize(200,200)
                .placeholder(R.color.colorBackground)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                        holder.imageView.setImageBitmap(bitmap);
                        if (Application.getList() == null){
                            Application.setList(new ArrayList<BitmapObject>());
                        }
                        if (!contains(strings.get(position), bitmap)){
                            Application.getList().add(new BitmapObject(strings.get(position), bitmap));
                            Intent intent = new Intent(context, WidgetProvider.class);
                            intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
                            int ids[] = AppWidgetManager.getInstance(getApplication())
                                    .getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
                            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
                            context.sendBroadcast(intent);
                        }

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        holder.imageView.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        holder.imageView.setImageDrawable(placeHolderDrawable);
                    }
                });
    }

    private boolean contains(String s, Bitmap bitmap) {
        for (BitmapObject bitmapObject: Application.getList()){
            if (bitmapObject.getUrl().equals(s)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public class MemeDisplayViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public MemeDisplayViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
