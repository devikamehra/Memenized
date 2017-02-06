package awe.devikamehra.memenized.rest.model;

import java.io.Serializable;

/**
 * Created by Devika on 30-01-2017.
 */

public class Meme implements Serializable{

    private String imageName;
    private String imageUrl;
    private String topText;
    private String bottomText;
    private String font;
    private int fontSize;

    public Meme(String imageUrl) {
        this(imageUrl, "", "", "", 50);
    }

    public Meme(String imageName, String imageUrl, String topText, String bottomText, String font, int fontSize) {
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.topText = topText;
        this.bottomText = bottomText;
        this.font = font;
        this.fontSize = fontSize;
    }

    public Meme(String imageUrl, String topText, String bottomText, String font, int fontSize) {
        this.imageUrl = imageUrl;
        this.topText = topText;
        this.bottomText = bottomText;
        this.font = font;
        this.fontSize = fontSize;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTopText() {
        return topText;
    }

    public void setTopText(String topText) {
        this.topText = topText;
    }

    public String getBottomText() {
        return bottomText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
}
