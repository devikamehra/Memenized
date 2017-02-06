package awe.devikamehra.memenized.rest.model;

/**
 * Created by Devika on 06-02-2017.
 */

public class MemeUpload{

    private String imageUrl;
    private String topText;
    private String bottomText;
    private String font;
    private Integer fontSize;
    private String uid;
    private String memeUrl;

    public MemeUpload() {
    }

    public MemeUpload(String imageUrl, String topText, String bottomText, String font, Integer fontSize, String uid, String memeUrl) {
        this.imageUrl = imageUrl;
        this.topText = topText;
        this.bottomText = bottomText;
        this.font = font;
        this.fontSize = fontSize;
        this.uid = uid;
        this.memeUrl = memeUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMemeUrl() {
        return memeUrl;
    }

    public void setMemeUrl(String memeUrl) {
        this.memeUrl = memeUrl;
    }
}
