package awe.devikamehra.memenized.rest.model;

/**
 * Created by Devika on 01-02-2017.
 */

public class ImageDetail {

    String name;
    String downloadUrl;

    public ImageDetail() {
    }

    public ImageDetail(String name, String downloadUrl) {
        this.name = name;
        this.downloadUrl = downloadUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
