package awe.devikamehra.memenized.view.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import awe.devikamehra.memenized.Application;
import awe.devikamehra.memenized.R;
import awe.devikamehra.memenized.rest.model.Meme;
import awe.devikamehra.memenized.rest.model.MemeUpload;
import awe.devikamehra.memenized.view.fragment.SetTextFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemeDisplayActivity extends AppCompatActivity {

    Toolbar toolbar;
    ProgressBar progressBar;
    ImageView imageView;
    Button button;
    AppCompatActivity appCompatActivity = this;

    String displayString = "";
    Meme meme;
    MemeUpload memeUpload;
    private StorageReference mImageStorageReference;
    private DatabaseReference mImageDatabaseReference;
    private ChildEventListener childEventListener;
    final String key = String.valueOf(Calendar.getInstance().getTimeInMillis());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_display);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (getIntent().getBundleExtra(SetTextFragment.ARG_PARAM1) != null){
            meme = (Meme) getIntent().getBundleExtra(SetTextFragment.ARG_PARAM1).getSerializable(SetTextFragment.ARG_PARAM1);
        }else  {
            displayString = getIntent().getStringExtra(MainActivity.SHARE);
        }
        setFirebaseReferences();
        init();
        if (meme != null) {
            getImage();
        }else if (!displayString.equals("")){
            setImage(displayString);
        }
    }

    private void setFirebaseReferences() {

        mImageStorageReference = Application.getFirebaseStorage().getReference().child(getString(R.string.meme_bucket_name));
        mImageDatabaseReference = Application.getFirebaseDatabase().getReference().child(getString(R.string.meme_database_name));

    }

    private void getImage() {
        Application.getMemeApi().createMeme(getString(R.string.meme_api_key),
                                            meme.getImageName(),
                                            meme.getTopText(),
                                            meme.getBottomText(),
                                            meme.getFont(),
                                            meme.getFontSize())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (!meme.getImageName().isEmpty()) {
                                StorageReference photoReference = mImageStorageReference.child(key);
                                ResponseBody body = response.body();
                                if (body != null) {
                                    if (body.contentType().toString().equals(getString(R.string.image_type_file)) && !appCompatActivity.isDestroyed()) {
                                        photoReference.putBytes(response.body().bytes()).addOnSuccessListener(appCompatActivity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                try {
                                                    MemeUpload memeUpload = new MemeUpload(meme.getImageUrl(),
                                                                                            meme.getTopText(),
                                                                                            meme.getBottomText(),
                                                                                            meme.getFont(),
                                                                                            meme.getFontSize(),
                                                                                            Application.getUid(),
                                                                                            taskSnapshot.getDownloadUrl().toString());
                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put(key, memeUpload);
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

    public void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_display_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        button = (Button) findViewById(R.id.share);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        imageView = (ImageView)findViewById(R.id.display_image);

    }

    @Override
    protected void onResume() {
        super.onResume();
        addChildListener();
    }

    private void addChildListener() {
        if (childEventListener == null){
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    memeUpload = dataSnapshot.getValue(MemeUpload.class);
                    if (dataSnapshot.getKey().equals(key)) {
                        setImage(memeUpload.getMemeUrl());
                    }
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
            mImageDatabaseReference.addChildEventListener(childEventListener);
        }
    }

    private void setImage(String s) {
        Picasso.with(getBaseContext())
                .load(s)
                .error(new IconDrawable(getBaseContext(), MaterialIcons.md_error))
                .placeholder(R.color.colorBackground)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageBitmap(bitmap);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_SEND);
                                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                String path = MediaStore.Images.Media.insertImage(getBaseContext().getContentResolver(),
                                        bitmap, getString(R.string.title_vh), null);

                                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                                intent.setType(getString(R.string.image_type_file));
                                startActivity(Intent.createChooser(intent,
                                        getString(R.string.choose)));
                            }
                        });

                        button.setClickable(true);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        imageView.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        imageView.setImageDrawable(placeHolderDrawable);
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeChildListener();
    }

    private void removeChildListener() {
        if (childEventListener != null){
            mImageDatabaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
