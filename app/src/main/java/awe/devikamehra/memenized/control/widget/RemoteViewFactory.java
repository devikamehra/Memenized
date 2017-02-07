package awe.devikamehra.memenized.control.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import awe.devikamehra.memenized.Application;
import awe.devikamehra.memenized.R;
import awe.devikamehra.memenized.view.activity.MainActivity;
import awe.devikamehra.memenized.view.activity.MemeDisplayActivity;

/**
 * Created by Devika on 06-02-2017.
 */

public class RemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context ctxt=null;

    public RemoteViewFactory(Context ctxt) {
        this.ctxt=ctxt;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return Application.getStrings() == null ? 0 : Application.getStrings().size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        final RemoteViews row = new RemoteViews(ctxt.getPackageName(),
                R.layout.simple_image_view);

        if (Application.getList() != null && (position < Application.getList().size())) {
            row.setImageViewBitmap(R.id.imageView, Application.getList().get(position).getBitmap());
            Intent i = new Intent(ctxt, MemeDisplayActivity.class);
            i.putExtra(MainActivity.SHARE, Application.getList().get(position).getUrl());
            row.setOnClickFillInIntent(R.id.imageView, i);
        }
        return(row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return(null);
    }

    @Override
    public int getViewTypeCount() {
        return(1);
    }

    @Override
    public long getItemId(int position) {
        return(position);
    }

    @Override
    public boolean hasStableIds() {
        return(true);
    }

    @Override
    public void onDataSetChanged() {
    }
}