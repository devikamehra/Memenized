package awe.devikamehra.memenized.view.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import awe.devikamehra.memenized.R;
import awe.devikamehra.memenized.rest.model.ImageDetail;
import io.github.prashantsolanki3.snaplibrary.snap.adapter.AbstractSnapSelectableAdapter;
import io.github.prashantsolanki3.snaplibrary.snap.layout.viewholder.SnapSelectableViewHolder;

/**
 * Created by Devika on 05-02-2017.
 */
public class MemeImageViewHolder extends SnapSelectableViewHolder<ImageDetail> {

    private ImageView imageView, selectionShell;

    public MemeImageViewHolder(View itemView, Context context, AbstractSnapSelectableAdapter adapter) {
        super(itemView, context, adapter);
        imageView = (ImageView) itemView.findViewById(R.id.image);
        selectionShell = (ImageView) itemView.findViewById(R.id.image_selected);
    }

    public MemeImageViewHolder(View itemView, Context context) {
        super(itemView, context);
        imageView = (ImageView) itemView.findViewById(R.id.image);
        selectionShell = (ImageView) itemView.findViewById(R.id.image_selected);
    }

    @Override
    public void onSelectionEnabled(SnapSelectableViewHolder snapSelectableViewHolder, ImageDetail imageDetail, int i) {
    }

    @Override
    public void onSelectionDisabled(SnapSelectableViewHolder snapSelectableViewHolder, ImageDetail imageDetail, int i) {
    }

    @Override
    public void onSelected(SnapSelectableViewHolder snapSelectableViewHolder, ImageDetail imageDetail, int i) {
        selectionShell.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeselected(SnapSelectableViewHolder snapSelectableViewHolder, ImageDetail imageDetail, int i) {
        selectionShell.setVisibility(View.GONE);
    }

    @Override
    public void populateViewHolder(ImageDetail imageDetail, int i) {
        Picasso.with(getContext())
                .load(imageDetail.getDownloadUrl())
                .error(new IconDrawable(getContext(), MaterialIcons.md_error))
                .resize(200,200)
                .placeholder(R.color.colorBackground)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Log.d("Picasso", "onBitmapLoaded");
                        imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Log.d("Picasso", "onBitmapFailed");
                        imageView.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        Log.d("Picasso", "onPrepareLoad");
                        imageView.setImageDrawable(placeHolderDrawable);
                    }
                });
    }
}

