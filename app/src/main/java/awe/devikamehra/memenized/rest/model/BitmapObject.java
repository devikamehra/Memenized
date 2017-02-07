package awe.devikamehra.memenized.rest.model;

import android.graphics.Bitmap;

/**
 * Created by Devika on 06-02-2017.
 */

public class BitmapObject {

    String url;
    Bitmap bitmap;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public BitmapObject(String url, Bitmap bitmap) {

        this.url = url;
        this.bitmap = bitmap;
    }
}
