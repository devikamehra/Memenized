package awe.devikamehra.memenized.view.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import awe.devikamehra.memenized.Application;
import awe.devikamehra.memenized.R;
import awe.devikamehra.memenized.control.utils.RecyclerItemClickListener;
import awe.devikamehra.memenized.rest.model.ImageDetail;
import awe.devikamehra.memenized.rest.model.User;
import awe.devikamehra.memenized.view.adapter.MemeDisplayAdapter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 8447;
    public static final String SHARE = "share";
    private StorageReference mImageStorageReference;
    private DatabaseReference mImageDatabaseReference, mUserDatabaseReference, mMemeDatabaseReference;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private ValueEventListener mValueEventListener, mMemeValueEventListener;
    private ChildEventListener mAuthChildEventListener;

    List<String> imageList = new ArrayList<>();
    public static List<String> fontList = new ArrayList<>();
    private String mUserName;
    private final AppCompatActivity appCompactActivity = MainActivity.this;
    private boolean shouldUpload = true;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MemeDisplayAdapter memeDisplayAdapter;
    private List<String> strings = new ArrayList<>();
    private AdView adView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mUserName = getString(R.string.default_user_name);
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);
        setFirebaseReferences();
        attachAuthStateListener();
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.add_new_meme);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), MemeGenerationActivity.class));
            }
        });
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        textView = (TextView) findViewById(R.id.empty_list);
        setupRecycler();
        attachMemeValueEventListener();
    }

    private void setupRecycler() {
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getBaseContext(), MemeDisplayActivity.class);
                intent.putExtra(SHARE, strings.get(position));
                startActivity(intent);
            }
        }));
    }

    private void setFirebaseReferences() {
        mImageStorageReference = Application.getFirebaseStorage().getReference().child(getString(R.string.image_bucket_name));
        mImageDatabaseReference = Application.getFirebaseDatabase().getReference().child(getString(R.string.image_database_name));
        mUserDatabaseReference = Application.getFirebaseDatabase().getReference().child(getString(R.string.user_database_image));
        mMemeDatabaseReference = Application.getFirebaseDatabase().getReference().child(getString(R.string.meme_database_name));
    }

    private void attachAuthStateListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //signed in
                    onSignedInInitialize(user.getDisplayName());
                    Application.setUid(user.getUid());
                    Map<String, Object> map = new HashMap<>();
                    map.put(user.getUid(), new User(user.getUid(), user.getDisplayName(), user.getEmail()));
                    mUserDatabaseReference.updateChildren(map);
                }else{
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Collections.singletonList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    private void onSignedInInitialize(String userName) {
        mUserName = userName;
        attachAuthChildEventListener();
        attachChildEventListener();
    }

    private void attachMemeValueEventListener() {
        if (mMemeValueEventListener == null){
            mMemeValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<String> list = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        if (child.child("uid").getValue() != null && child.child("uid").getValue().toString().equals(Application.getUid())){
                            list.add(child.child(getString(R.string.memeUrl_field_tag)).getValue().toString());

                        }
                    }
                    if (list.size() == 0){
                        progressBar.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                    }else{
                        if (textView.getVisibility() == View.VISIBLE){
                            textView.setVisibility(View.GONE);
                        }
                    }
                    strings = list;
                    Application.setStrings(strings);
                    memeDisplayAdapter = new MemeDisplayAdapter(getBaseContext(), strings);
                    recyclerView.setAdapter(memeDisplayAdapter);
                    memeDisplayAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        mMemeDatabaseReference.addValueEventListener(mMemeValueEventListener);
    }


    private void attachAuthChildEventListener() {
        if (mAuthChildEventListener == null){
            mAuthChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mUserDatabaseReference.addChildEventListener(mAuthChildEventListener);
        }
    }

    private void onSignedOutCleanup() {
        mUserName = getString(R.string.default_user_name);
        //mMessageAdapter.clear();
        detachDatabaseReadListener();
        detachMemeReader();
    }

    private void detachMemeReader() {
        if (mMemeValueEventListener != null){
            mMemeDatabaseReference.removeEventListener(mMemeValueEventListener);
            mMemeValueEventListener = null;
        }
    }

    private void attachChildEventListener(){
        if (mValueEventListener == null){
            mValueEventListener =  new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String key = "";
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        imageList.add(childDataSnapshot.child(getString(R.string.name_field_tag)).getValue().toString());
                        key = childDataSnapshot.getKey();

                    }

                    if (shouldUpload) {
                        shouldUpload = false;
                        setImage(Integer.valueOf(key));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mImageDatabaseReference.addValueEventListener(mValueEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mValueEventListener != null){
            mImageDatabaseReference.removeEventListener(mValueEventListener);
            mValueEventListener = null;
        }
    }

    private void setImage(final int key){

        Application.getMemeApi().getImageList(getString(R.string.meme_api_key))
                .enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {

                        List<String> list = response.body();

                        for(int i = key; i < list.size(); i++){
                            getImage(Integer.toString(i), list.get(i));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private void getImage(final String id, final String name){

        Application.getMemeApi().getImage(getString(R.string.meme_api_key), name)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (!name.isEmpty()) {
                                StorageReference photoReference = mImageStorageReference.child(name);
                                ResponseBody body = response.body();
                                if (body != null) {
                                    if (body.contentType().toString().equals(getString(R.string.image_type_file)) && !appCompactActivity.isDestroyed()) {
                                        photoReference.putBytes(response.body().bytes()).addOnSuccessListener(appCompactActivity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                try {
                                                    Map<String, Object> map = new HashMap<>();
                                                    String s = taskSnapshot.getDownloadUrl().toString();
                                                    map.put(id, new ImageDetail(name, s));
                                                    mImageDatabaseReference.updateChildren(map);
                                                } catch (NullPointerException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Log.i("Auth data", "signed in");
            }else if (resultCode == RESULT_CANCELED){
                Log.i("Auth", "Signed In cancelled");
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.sign_out: AuthUI.getInstance().signOut(this);
                finish();
        }
        return true;
    }

    @Override
    protected void onResume() {
        if(adView != null){
            adView.resume();
        }
        Application.getFirebaseAuth().addAuthStateListener(mAuthStateListener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(adView != null){
            adView.pause();
        }
        Application.getFirebaseAuth().removeAuthStateListener(mAuthStateListener);
        super.onPause();
    }

    public static List<String> getFontList() {
        return fontList;
    }

    public static void setFontList(List<String> fontList) {
        MainActivity.fontList = fontList;
    }

    @Override
    protected void onDestroy() {
        if (adView != null){
            adView.destroy();
        }
        super.onDestroy();
    }
}
