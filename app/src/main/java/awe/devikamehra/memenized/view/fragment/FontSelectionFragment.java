package awe.devikamehra.memenized.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.List;

import awe.devikamehra.memenized.Application;
import awe.devikamehra.memenized.R;
import awe.devikamehra.memenized.rest.model.Meme;
import awe.devikamehra.memenized.view.viewholder.FontSelectViewHolder;
import io.github.prashantsolanki3.snaplibrary.snap.adapter.AbstractSnapSelectableAdapter;
import io.github.prashantsolanki3.snaplibrary.snap.adapter.SnapSelectableAdapter;
import io.github.prashantsolanki3.snaplibrary.snap.layout.viewholder.SnapSelectableViewHolder;
import io.github.prashantsolanki3.snaplibrary.snap.layout.wrapper.SnapSelectableLayoutWrapper;
import io.github.prashantsolanki3.snaplibrary.snap.listeners.selection.SelectionListener;
import io.github.prashantsolanki3.snaplibrary.snap.listeners.touch.SnapSelectableOnItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FontSelectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FontSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FontSelectionFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "meme_data";

    private Meme meme;

    private OnFragmentInteractionListener mListener;

    private List<String> fontList = new ArrayList<>();
    RecyclerView recyclerView;
    DiscreteSeekBar discreteSeekBar;

    DatabaseReference mFontsDatabaseReference;
    ValueEventListener valueEventListener;

    SnapSelectableAdapter<String> snapSelectableAdapter;
    private ProgressBar progressBar;

    public FontSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param meme Parameter 1.
     * @return A new instance of fragment FontSelectionFragment.
     */
    public static FontSelectionFragment newInstance(Meme meme) {
        FontSelectionFragment fragment = new FontSelectionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, meme);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            meme = (Meme)getArguments().getSerializable(ARG_PARAM1);
        }
        mFontsDatabaseReference = Application.getFirebaseDatabase().getReference().child("fonts");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_font_selection, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageLeft);
        imageView.setImageDrawable(new IconDrawable(getContext(), MaterialIcons.md_format_size)
                                    .colorRes(R.color.colorPrimaryDark)
                                    .sizeDp(24));
        discreteSeekBar = (DiscreteSeekBar) view.findViewById(R.id.seekbar);
        discreteSeekBar.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                return value * 10;
            }
        });
        discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                meme.setFontSize(value * 10);
                onMemeDataModified(meme);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
        attachValueEventListener();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        detachValueEventListener();
    }

    private void detachValueEventListener() {
        mFontsDatabaseReference.removeEventListener(valueEventListener);
    }

    private void attachValueEventListener() {
        if (valueEventListener == null){
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        fontList.add((String) child.getValue());
                    }
                    progressBar.setVisibility(View.GONE);
                    setupRecycler();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mFontsDatabaseReference.addValueEventListener(valueEventListener);
        }
    }

    private void setupRecycler() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SnapSelectableLayoutWrapper snapSelectableLayoutWrapper =
                new SnapSelectableLayoutWrapper(
                        String.class,
                        FontSelectViewHolder.class,
                        R.layout.font_select_recyclerview_item,
                        1,
                        true);
        snapSelectableAdapter = new SnapSelectableAdapter<>(
                getContext(),
                snapSelectableLayoutWrapper,
                recyclerView,
                AbstractSnapSelectableAdapter.SelectionType.SINGLE);
        snapSelectableAdapter.addAll(fontList);
        recyclerView.setAdapter(snapSelectableAdapter);
        Log.d("limit", snapSelectableAdapter.getSelectionLimit() + "");

        snapSelectableAdapter.setOnSelectionListener(new SelectionListener<String>() {
            @Override
            public void onSelectionModeEnabled(AbstractSnapSelectableAdapter.SelectionType selectionType) {

            }

            @Override
            public void onSelectionModeDisabled(AbstractSnapSelectableAdapter.SelectionType selectionType) {

            }

            @Override
            public void onItemSelected(String s, int i) {
                Log.d("tag", s);
                meme.setFont(s);
                onMemeDataModified(meme);
            }

            @Override
            public void onItemDeselected(String s, int i) {

            }

            @Override
            public void onSelectionLimitReached() {

            }

            @Override
            public void onSelectionLimitExceeding() {

            }

            @Override
            public void onNoneSelected() {

            }
        });
        snapSelectableAdapter.setOnItemClickListener(new SnapSelectableOnItemClickListener(snapSelectableAdapter) {
            @Override
            public void onItemClick(SnapSelectableViewHolder snapSelectableViewHolder, View view, int i) {
                snapSelectableAdapter.selectItem(i);
            }

            @Override
            public void onItemLongPress(SnapSelectableViewHolder snapSelectableViewHolder, View view, int i) {

            }
        });
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
}