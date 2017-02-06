package awe.devikamehra.memenized.view.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import awe.devikamehra.memenized.R;
import io.github.prashantsolanki3.snaplibrary.snap.adapter.AbstractSnapSelectableAdapter;
import io.github.prashantsolanki3.snaplibrary.snap.layout.viewholder.SnapSelectableViewHolder;

public class FontSelectViewHolder extends SnapSelectableViewHolder<String>{

    private ImageView imageView, imageViewSelected;
    private TextView textView;

    public FontSelectViewHolder(View itemView, Context context, AbstractSnapSelectableAdapter adapter) {
        super(itemView, context, adapter);
        imageView = (ImageView) itemView.findViewById(R.id.image);
        imageViewSelected = (ImageView) itemView.findViewById(R.id.imageTop);
        textView = (TextView) itemView.findViewById(R.id.font_name);
    }

    public FontSelectViewHolder(View itemView, Context context) {
        super(itemView, context);
        imageView = (ImageView) itemView.findViewById(R.id.image);
        imageViewSelected = (ImageView) itemView.findViewById(R.id.imageTop);
        textView = (TextView) itemView.findViewById(R.id.font_name);
    }

    @Override
    public void onSelectionEnabled(SnapSelectableViewHolder snapSelectableViewHolder, String s, int i) {

    }

    @Override
    public void onSelectionDisabled(SnapSelectableViewHolder snapSelectableViewHolder, String s, int i) {

    }

    @Override
    public void onSelected(SnapSelectableViewHolder snapSelectableViewHolder, String s, int i) {
        imageViewSelected.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeselected(SnapSelectableViewHolder snapSelectableViewHolder, String s, int i) {
        imageViewSelected.setVisibility(View.GONE);
    }

    @Override
    public void populateViewHolder(String s, int i) {
        ColorGenerator generator = ColorGenerator.MATERIAL;
        final int color = generator.getColor(s);
        final TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRect(String.valueOf(s.charAt(0)), color);

        imageView.setImageDrawable(drawable);
        textView.setText(s);
    }
}