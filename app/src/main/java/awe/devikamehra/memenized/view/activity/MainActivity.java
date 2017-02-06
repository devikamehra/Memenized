package awe.devikamehra.memenized.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import awe.devikamehra.memenized.Application;
import awe.devikamehra.memenized.R;
import awe.devikamehra.memenized.rest.model.ImageDetail;
import awe.devikamehra.memenized.rest.model.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 8447;
    private StorageReference mImageStorageReference;
    private DatabaseReference mImageDatabaseReference, mUserDatabaseReference;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private ValueEventListener mValueEventListener;
    private ChildEventListener mAuthChildEventListener;

    List<String> imageList = new ArrayList<>();
    public static List<String> fontList = new ArrayList<>();
    private String mUserName;
    private final AppCompatActivity appCompactActivity = MainActivity.this;
    private boolean shouldUpload = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserName = getString(R.string.default_user_name);
        setFirebaseReferences();
        attachAuthStateListener();
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.add_new_meme);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), MemeGenerationActivity.class));
            }
        });
    }

    private void setFirebaseReferences() {
        mImageStorageReference = Application.getFirebaseStorage().getReference().child(getString(R.string.image_bucket_name));
        mImageDatabaseReference = Application.getFirebaseDatabase().getReference().child(getString(R.string.image_database_name));
        mUserDatabaseReference = Application.getFirebaseDatabase().getReference().child(getString(R.string.user_database_image));
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

    private void attachAuthChildEventListener() {
        if (mAuthChildEventListener == null){
            mAuthChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d("Check", dataSnapshot.getChildrenCount() + " " + s);
                    Log.d("User_Data", dataSnapshot.getValue().toString());
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
                    Log.d("total",imageList.size() + "");
                    if (shouldUpload) {
                        shouldUpload = false;
                        setImage(Integer.valueOf(key));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("Error", databaseError.getMessage());
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
                        Log.d("TAG_123", " " + list.size());
                        Log.d("TAG_456", " " + imageList.size());

                        for(int i = key; i < list.size(); i++){
                            Log.d("Image_Load", list.get(i));
                            getImage(Integer.toString(i), list.get(i));
                        }
                        Log.d("TAG_123", "Success " + list.size());
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
                                    if (body.contentType().toString().equals(getString(R.string.image_type_file))) {
                                        photoReference.putBytes(response.body().bytes()).addOnSuccessListener(appCompactActivity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                try {
                                                    Map<String, Object> map = new HashMap<>();
                                                    String s = taskSnapshot.getDownloadUrl().toString();
                                                    map.put(id, new ImageDetail(name, s));
                                                    Log.d("TAG", s);
                                                    mImageDatabaseReference.updateChildren(map);
                                                } catch (NullPointerException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    } else {
                                        Log.d("TAG", "sry");
                                    }
                                } else {
                                    Log.d("TAG", "sry");
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
                Log.d("Auth data", "signed in");
            }else if (resultCode == RESULT_CANCELED){
                Log.d("Auth", "Signed In cancelled");
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
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Application.getFirebaseAuth().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Application.getFirebaseAuth().removeAuthStateListener(mAuthStateListener);
    }

    public static List<String> getFontList() {
        return fontList;
    }

    public static void setFontList(List<String> fontList) {
        MainActivity.fontList = fontList;
    }
}
