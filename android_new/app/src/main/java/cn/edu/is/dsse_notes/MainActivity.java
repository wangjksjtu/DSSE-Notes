package cn.edu.is.dsse_notes;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.net.Uri;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cn.edu.is.dsse_notes.Async.DeleteTask;
import cn.edu.is.dsse_notes.Async.PostTask;
import cn.edu.is.dsse_notes.Async.PutTask;
import cn.edu.is.dsse_notes.Async.QueryTask;
import cn.edu.is.dsse_notes.Async.SyncTask;
import cn.edu.is.dsse_notes.note.NoteContent;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener,
        ListFragment.ListFragmentInteractionListener,
        cn.edu.is.dsse_notes.DetailFragment.OnFragmentInteractionListener,
        cn.edu.is.dsse_notes.Async.PostTask.PostListener,
        cn.edu.is.dsse_notes.Async.QueryTask.QueryCompleteListener,
        cn.edu.is.dsse_notes.Async.SyncTask.SyncCompleteListener
{

    private int currentDrawerPosition = 0;
    private SearchView searchView;
    private MenuItem myActionMenuItem;
    private ListFragment currentListFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity","FAB clicked");
                onFABClicked();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {
            @Override
            public void onDrawerOpened(View drawerView) {
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        selectItem(R.id.drawer_nav_list);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        myActionMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Notes");
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                launchListFragment();
                return false;
            }
        });

        searchView.setOnQueryTextListener(this);
        searchView.setIconified(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        selectItem(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void selectItem(int id) {
        switch (id) {
            case R.id.drawer_nav_list:
                launchListFragment();
                break;
            case R.id.drawer_action_query:
                launchListFragment();
                Log.d("Drawer","SearchView is " + searchView.toString());
                searchView.setIconified(false);
                break;
            case R.id.drawer_action_sync:
                SyncTask syncTask = new SyncTask();
                syncTask.setSyncCompleteListener(this);
                syncTask.execute();
                break;
            case R.id.drawer_about:
                Intent intent = new Intent();
                intent.setClass(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
    }

    // Floating Action Button Click Handler
    private void onFABClicked() {
        NoteContent.NoteItem newItem = new NoteContent.NoteItem(NoteContent.getNewItemID(), "", "");
        NoteContent.ITEMS.add(newItem);
        NoteContent.ITEM_MAP.put(newItem.id, newItem);
        launchDetailFragment(newItem);
    }

    // List Item Click Interface
    @Override
    public void onItemClicked(int position) {
        NoteContent.NoteItem noteClicked = NoteContent.ITEMS.get(position);
        launchDetailFragment(noteClicked);
    }

    private void launchListFragment() {
        ListFragment lf = new ListFragment();
        lf.setListener(this);
        currentListFragment = lf;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, lf, "Visible Fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void launchDetailFragment(NoteContent.NoteItem item) {
        Fragment fd = DetailFragment.newInstance(item);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, fd);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
    private void doSearch(String query) {

        ArrayList<String> tagList = new ArrayList<String>();
        ArrayList<Integer> toSearchTags = new ArrayList<>();
        for (String tag : NoteContent.tagList) {
            tagList.add(tag);
        }
        HashSet<String> tagSet = new HashSet<>(tagList);
        for (String tag : query.split(" ")) {
            if (tagSet.contains(tag)) {
                toSearchTags.add(tagList.indexOf(tag));
            }
        }
        Integer[] queryTags = toSearchTags.toArray(new Integer[toSearchTags.size()]);
        QueryTask queryTask = new QueryTask();
        queryTask.setQueryCompleteListener(this);
        queryTask.execute(queryTags);
    }

    @Override
    public void onLeftClicked(int position) {

    }

    @Override
    public void onRightClicked(int position){

    }

    // SearchView Interface
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, "TextSubmitted: " + query, Toast.LENGTH_SHORT).show();
        doSearch(query);
        return true;
    }

    public boolean onQueryTextChange(String newText) {
        // Toast.makeText(this, "TextChanged: " + newText, Toast.LENGTH_SHORT).show();
        return true;
    }


    private void onShareAction() {
    }

    // Detail Fragment Interface
    @Override
    public void onDetailFragmentInteraction(Uri uri) {
        return;
    }

    @Override
    public void onSave(NoteContent.NoteItem note) {
        NoteContent.ITEM_MAP.get(note.id).title = note.title;
        NoteContent.ITEM_MAP.get(note.id).details = note.details;
        if (note.remoteId == null) {
            // post task
            Log.d("onSave","preparing to post");
            PostTask postTask = new PostTask();
            postTask.setListener(this);
            postTask.execute(note);
        } else {
            // put task
            Log.d("onSave","preparing to put");
            PutTask putTask = new PutTask();
            putTask.execute(note);
        }
    }

    // Post task listener
    @Override
    public void onPostComplete(NoteContent.NoteItem noteItem, Integer remoteID) {
        // set remote ID for newly posted note item
        NoteContent.ITEM_MAP.get(noteItem.id).remoteId = remoteID.toString();
        for (NoteContent.NoteItem item : NoteContent.ITEMS) {
            Log.d("PostTaskC", "remote ID for " + item.id + " is " + item.remoteId);
        }
    }

    // Query task listener
    @Override
    public void handleQueryResult(ArrayList<NoteContent.NoteItem> resultList) {
        currentListFragment.onDataSetChanged(resultList);
    }

    // Sync task listener
    @Override
    public void handleSyncResult(ArrayList<NoteContent.NoteItem> resultList) {
        NoteContent.ITEMS = resultList;
        NoteContent.ITEM_MAP = new HashMap<>();
        for (NoteContent.NoteItem item : resultList) {
            NoteContent.ITEM_MAP.put(item.id, item);
        }
        ListFragment lf = ListFragment.newInstance(resultList);
        lf.setListener(this);
        currentListFragment = lf;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, lf, "Visible Fragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}


