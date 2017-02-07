package awe.devikamehra.memenized.view.fragment;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import awe.devikamehra.memenized.Application;
import awe.devikamehra.memenized.R;
import awe.devikamehra.memenized.control.utils.EndlessRecyclerViewScrollListener;
import awe.devikamehra.memenized.rest.model.ImageDetail;
import awe.devikamehra.memenized.rest.model.Meme;
import awe.devikamehra.memenized.view.viewholder.MemeImageViewHolder;
import io.github.prashantsolanki3.snaplibrary.snap.adapter.AbstractSnapSelectableAdapter;
import io.github.prashantsolanki3.snaplibrary.snap.adapter.SnapSelectableAdapter;
import io.github.prashantsolanki3.snaplibrary.snap.layout.viewholder.SnapSelectableViewHolder;
import io.github.prashantsolanki3.snaplibrary.snap.layout.wrapper.SnapSelectableLayoutWrapper;
import io.github.prashantsolanki3.snaplibrary.snap.listeners.selection.SelectionListener;
import io.github.prashantsolanki3.snaplibrary.snap.listeners.touch.SnapSelectableOnItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MemeImageSelectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MemeImageSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemeImageSelectionFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "meme_data";

    private Meme meme;
    private ArrayList<ImageDetail> imageList = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    private DatabaseReference mImageDatabaseReference;
    private ValueEventListener mValueEventListener;
    private StorageReference mStorageReference;
    SnapSelectableAdapter<ImageDetail> imageDetailSnapAdapter;
    RecyclerView recyclerView;
    private String key = "";
    private ProgressBar progressBar;

    public MemeImageSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param meme Parameter 1.
     * @return A new instance of fragment MemeImageSelectionFragment.
     */
    public static MemeImageSelectionFragment newInstance(Meme meme) {
        MemeImageSelectionFragment fragment = new MemeImageSelectionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, meme);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            meme = (Meme) getArguments().getSerializable(ARG_PARAM1);
        }
        setFirebaseReferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.fragment_meme_image_selection, container, false);
        View view = inflater.inflate(R.layout.fragment_meme_image_selection, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        attachChildEventListener();
        return view;
    }

    public void onMemeDataModified(Meme meme) {
        if (mListener != null) {
            mListener.onFragmentInteraction(meme);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        detachChildEventListener();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Meme meme);
    }

    private void setFirebaseReferences() {
        mImageDatabaseReference = Application.getFirebaseDatabase().getReference().child(getString(R.string.image_database_name));
        mStorageReference = Application.getFirebaseStorage().getReference().child(getString(R.string.image_bucket_name));
    }

    private void attachChildEventListener(){
        if (mValueEventListener == null){
            mValueEventListener =  new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        imageList.add(new ImageDetail(childDataSnapshot.child("name").getValue().toString(),
                                childDataSnapshot.child("downloadUrl").getValue().toString()));
                        key = childDataSnapshot.getKey();
                    }
                    setupRecycler();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mImageDatabaseReference.addValueEventListener(mValueEventListener);
        }
    }

    private void setupRecycler() {
        Point point = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(point);
        int width = point.x;
        int var = width/250;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), var);
        recyclerView.setLayoutManager(gridLayoutManager);
        SnapSelectableLayoutWrapper selectableLayoutWrapper =
                new SnapSelectableLayoutWrapper(ImageDetail.class,
                        MemeImageViewHolder.class,
                        R.layout.meme_image_recyclerview_item,
                        1,
                        true);
        imageDetailSnapAdapter = new SnapSelectableAdapter<>(
                getContext(),
                selectableLayoutWrapper,
                recyclerView,
                AbstractSnapSelectableAdapter.SelectionType.SINGLE);
        ArrayList<ImageDetail> list = new ArrayList<>();
        list.addAll(imageList.subList(0, 40));
        imageDetailSnapAdapter.addAll(list);
        recyclerView.setAdapter(imageDetailSnapAdapter);
        progressBar.setVisibility(View.GONE);

        imageDetailSnapAdapter.setOnSelectionListener(new SelectionListener<ImageDetail>() {
            @Override
            public void onSelectionModeEnabled(AbstractSnapSelectableAdapter.SelectionType selectionType) {}

            @Override
            public void onSelectionModeDisabled(AbstractSnapSelectableAdapter.SelectionType selectionType) {}

            @Override
            public void onItemSelected(ImageDetail imageDetail, int i) {
                meme.setImageUrl(imageDetail.getDownloadUrl());
                meme.setImageName(imageDetail.getName());
                onMemeDataModified(meme);
            }

            @Override
            public void onItemDeselected(ImageDetail imageDetail, int i){}

            @Override
            public void onSelectionLimitReached() {}

            @Override
            public void onSelectionLimitExceeding() {}
            @Override
            public void onNoneSelected() {}

        });
        imageDetailSnapAdapter.setOnItemClickListener(new SnapSelectableOnItemClickListener(imageDetailSnapAdapter) {
            @Override
            public void onItemClick(SnapSelectableViewHolder snapSelectableViewHolder, View view, int i) {
                imageDetailSnapAdapter.selectItem(i);
            }

            @Override
            public void onItemLongPress(SnapSelectableViewHolder snapSelectableViewHolder, View view, int i) {

            }
        });

        recyclerView.setOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {

            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                for (int index = page * 40; index < ((page + 1) * 40) && index < imageList.size(); index++) {
                    imageDetailSnapAdapter.add(imageList.get(index));
                }
            }
        });
    }

    private void detachChildEventListener() {
        if (mValueEventListener != null){
            mImageDatabaseReference.removeEventListener(mValueEventListener);
        }
    }
}
