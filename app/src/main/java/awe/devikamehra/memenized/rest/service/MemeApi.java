package awe.devikamehra.memenized.rest.service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by Devika on 31-01-2017.
 */

public interface MemeApi {

    @GET("/meme?bottom=&font=Impact&font_size=50&top=")
    Call<ResponseBody> getImage(@Header("X-Mashape-Key") String key, @Query("meme") String imageName);

    @GET("/images")
    Call<List<String>> getImageList(@Header("X-Mashape-Key") String key);

    @GET("/fonts")
    Call<List<String>> getFontList(@Header("X-Mashape-Key") String key);

    @GET("/meme")
    Call<ResponseBody> createMeme(@Header("X-Mashape-Key") String key,
                                  @Query("meme") String imageName,
                                  @Query("top") String topText,
                                  @Query("bottom") String bottomText,
                                  @Query("font") String font,
                                  @Query("font_size") Integer fontSize);

}
