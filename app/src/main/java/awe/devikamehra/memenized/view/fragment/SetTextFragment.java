package awe.devikamehra.memenized.view.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;

import awe.devikamehra.memenized.R;
import awe.devikamehra.memenized.rest.model.Meme;
import awe.devikamehra.memenized.view.activity.MemeDisplayActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SetTextFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SetTextFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetTextFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "meme_data";

    private Meme meme;

    EditText topText, bottomText;

    private OnFragmentInteractionListener mListener;

    public SetTextFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param meme Parameter 1.
     * @return A new instance of fragment SetTextFragment.
     */
    public static SetTextFragment newInstance(Meme meme) {
        SetTextFragment fragment = new SetTextFragment();
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_text, container, false);

        topText = (EditText) view.findViewById(R.id.top_text);
        bottomText = (EditText) view.findViewById(R.id.bottom_text);
        topText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                meme.setTopText(topText.getText().toString());
                meme.setBottomText(bottomText.getText().toString());
                onMemeDataModified(meme);
            }
        });
        topText.setCompoundDrawables(new IconDrawable(getContext(), MaterialIcons.md_vertical_align_top)
                                    .colorRes(R.color.colorPrimary)
                                    .actionBarSize(), null, null, null);
        bottomText.setCompoundDrawables(new IconDrawable(getContext(), MaterialIcons.md_vertical_align_bottom)
                .colorRes(R.color.colorPrimary)
                .actionBarSize(), null, null, null);
        bottomText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                meme.setTopText(topText.getText().toString());
                meme.setBottomText(bottomText.getText().toString());
                onMemeDataModified(meme);
            }
        });
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.meme_save_option_menu, menu);
        menu.findItem(R.id.saving_meme).setIcon(
                new IconDrawable(getContext(), MaterialIcons.md_check)
                        .colorRes(R.color.white)
                        .actionBarSize());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.saving_meme : if(validate()) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(ARG_PARAM1, meme);
                Intent intent = new Intent(getContext(), MemeDisplayActivity.class);
                intent.putExtra(ARG_PARAM1, bundle);
                startActivity(intent);
            }
            default:
        }
        return true;
    }

    private boolean validate() {
            if (meme.getImageUrl().startsWith("http")) {
                if (!meme.getFont().equals("")) {
                    return true;
                } else {
                    Log.d("error", "occured");
                    new AlertDialog.Builder(getContext())
                            .setMessage(R.string.font_error_msg)
                            .setCancelable(false)
                            .setTitle(R.string.dialog_title)
                            .setPositiveButton(R.string.positive_button_text, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    return false;
                }
            } else {
                Log.d("error", "occured");
                new AlertDialog.Builder(getContext())
                        .setMessage(R.string.meme_error_msg)
                        .setCancelable(false)
                        .setTitle(R.string.dialog_title)
                        .setPositiveButton(R.string.positive_button_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return false;
            }
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
