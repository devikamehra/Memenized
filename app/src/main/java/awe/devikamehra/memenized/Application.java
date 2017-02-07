package awe.devikamehra.memenized;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;

import java.util.ArrayList;
import java.util.List;

import awe.devikamehra.memenized.rest.model.BitmapObject;
import awe.devikamehra.memenized.rest.service.MemeApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Devika on 31-01-2017.
 */

public class Application extends android.app.Application{

    public static final String BASE_URL = "https://ronreiter-meme-generator.p.mashape.com/";
    private static MemeApi memeApi;
    private static Application application;
    private static FirebaseDatabase firebaseDatabase;
    private static FirebaseStorage firebaseStorage;
    private static FirebaseAuth firebaseAuth;
    private static String uid;
    private static List<BitmapObject> list;
    private static List<String> strings = new ArrayList<>();

    private Context context;

    public static String getUid() {
        return uid;
    }

    public static void setUid(String uid) {
        Application.uid = uid;
    }

    public static List<String> getStrings() {
        return strings;
    }

    public static void setStrings(List<String> strings) {
        Application.strings = strings;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        context = getApplicationContext();

        Iconify.with(new MaterialModule());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        memeApi = retrofit.create(MemeApi.class);
    }

    public static MemeApi getMemeApi() {
        return memeApi;
    }

    public static synchronized Application getApplication() {
        return application;
    }

    public Context getContext() {
        return context;
    }


    public static FirebaseDatabase getFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    public static FirebaseStorage getFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }

    public static FirebaseAuth getFirebaseAuth() { return FirebaseAuth.getInstance(); }

    public static List<BitmapObject> getList() {
        return list;
    }

    public static void setList(List<BitmapObject> list) {
        Application.list = list;
    }
}
