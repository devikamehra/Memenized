package awe.devikamehra.memenized.control.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Devika on 06-02-2017.
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return(new RemoteViewFactory(this.getApplicationContext()));
    }
}