package cn.edu.is.dsse_notes;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import cn.edu.is.dsse_notes.Async.DeleteTask;
import cn.edu.is.dsse_notes.note.NoteContent;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link MyListRecyclerViewAdapter.Listener}
 * interface.
 */
public class ListFragment extends Fragment implements
        MyListRecyclerViewAdapter.Listener,
        SwipeController.SwipeAction {

    public static interface ListFragmentInteractionListener {
        public void onItemClicked(int position);
        public void onLeftClicked(int position);
        public void onRightClicked(int position);
    }

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private ListFragmentInteractionListener listFragmentInteractionListener;
    private MyListRecyclerViewAdapter myListRecyclerViewAdapter = null;
    private RecyclerView mRecyclerView = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListFragment() {
    }

    @SuppressWarnings("unused")
    public static ListFragment newInstance(int columnCount) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Log.d("ListFrag", "Recycler View Created");
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView = recyclerView;
            MyListRecyclerViewAdapter adapter = new MyListRecyclerViewAdapter(NoteContent.ITEMS, this);
            recyclerView.setAdapter(adapter);
            myListRecyclerViewAdapter = adapter;
            final SwipeController swipeController = new SwipeController();
            swipeController.setSwipeAction(this);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
            itemTouchHelper.attachToRecyclerView(recyclerView);
            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                    swipeController.onDraw(c);
                }
            });
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(int position) {
        Toast.makeText(getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
        listFragmentInteractionListener.onItemClicked(position);
        return;
    }

    public void setListener(ListFragmentInteractionListener listFragmentInteractionListener) {
        this.listFragmentInteractionListener = listFragmentInteractionListener;
    }

    // Swipe Action interface
    @Override
    public void onLeftClicked(int position) {
        // Edit is handled by main activity
        listFragmentInteractionListener.onLeftClicked(position);
    }

    @Override
    public void onRightClicked(int position) {

        // Delete

        // First remove this from the dataset and display it
        Integer toDeleteRemoteID = null;
        if (NoteContent.ITEMS.get(position).remoteId != null) {
             toDeleteRemoteID = Integer.parseInt(NoteContent.ITEMS.get(position).remoteId);
        }
        NoteContent.ITEMS.remove(position);
        myListRecyclerViewAdapter.notifyItemRemoved(position);
        myListRecyclerViewAdapter.notifyItemRangeChanged(position, myListRecyclerViewAdapter.getItemCount());

        // Then fire up a DeleteTask
        if (toDeleteRemoteID != null) {
            DeleteTask deleteTask = new DeleteTask();
            deleteTask.execute(toDeleteRemoteID);
        }
    }

    public void onDataSetChanged(ArrayList<NoteContent.NoteItem> dataSet) {
        MyListRecyclerViewAdapter myListRecyclerViewAdapter = new MyListRecyclerViewAdapter(dataSet, this);
        mRecyclerView.setAdapter(myListRecyclerViewAdapter);
    }

}
