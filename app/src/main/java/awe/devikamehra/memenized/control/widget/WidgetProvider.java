package awe.devikamehra.memenized.control.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import awe.devikamehra.memenized.R;
import awe.devikamehra.memenized.view.activity.MainActivity;

/**
 * Created by Devika on 06-02-2017.
 */
public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        for (int i=0; i<appWidgetIds.length; i++) {
            Intent svcIntent=new Intent(ctxt, WidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews widget = new RemoteViews(ctxt.getPackageName(),
                    R.layout.widget_layout);
            widget.setRemoteAdapter(R.id.widget_grid,
                    svcIntent);

            Intent clickIntent = new Intent(ctxt, MainActivity.class);
            PendingIntent clickPI = PendingIntent
                    .getActivity(ctxt, 0,
                            clickIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            widget.setPendingIntentTemplate(R.id.widget_grid, clickPI);
            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
        }

        super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
    }
}
