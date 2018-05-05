package cn.edu.is.dsse_notes;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;

import cn.edu.is.dsse_notes.note.NoteContent;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class DetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NOTE = "param_note";

    // TODO: Rename and change types of parameters
    private NoteContent.NoteItem mParamNote;

    private OnFragmentInteractionListener mListener;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment DetailFragment.
     */
    public static DetailFragment newInstance(NoteContent.NoteItem param1) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_NOTE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamNote = getArguments().getParcelable(ARG_NOTE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView title = (TextView) view.findViewById(R.id.detail_title);
        if (mParamNote.title != "") {
            title.setText(mParamNote.title);
        }
        EditText detail = (EditText) view.findViewById(R.id.detail_content);
        if (mParamNote.details != "") {
            detail.setText(mParamNote.details);
        }
        final ChipCloud chipCloud = (ChipCloud) view.findViewById(R.id.detail_chip_cloud);
        chipCloud.addChips(NoteContent.tagList);
        for (Integer i : mParamNote.tags) {
            chipCloud.setSelectedChip(i);
        }
        chipCloud.setChipListener(new ChipListener() {
            @Override
            public void chipSelected(int i) {
                Log.d("DetailFragment","Tag #" + i + " selected");
                mParamNote.tags.add(i);
            }

            @Override
            public void chipDeselected(int i) {
                Log.d("DetailFragment","Tag #" + i + " deselected");
                mParamNote.tags.remove(Integer.valueOf(i));
            }
        });
        return view;
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
        View fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        TextView title = getActivity().findViewById(R.id.detail_title);
        EditText content = getActivity().findViewById(R.id.detail_content);
        NoteContent.NoteItem note = new NoteContent.NoteItem(mParamNote.id,
                title.getText().toString(),
                content.getText().toString());
        note.remoteId = mParamNote.remoteId;
        note.tags = mParamNote.tags;
        mListener.onSave(note);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        View fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE) ;
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
        // TODO: Update argument type and name
        void onDetailFragmentInteraction(Uri uri);
        void onSave(NoteContent.NoteItem note);
    }
}
