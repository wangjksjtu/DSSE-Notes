package cn.edu.is.dsse_notes;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.net.Uri;
import android.widget.Toast;

import cn.edu.is.dsse_notes.dummy.DummyContent;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener,
        cn.edu.is.dsse_notes.ListFragment.OnItemClickListener,
        cn.edu.is.dsse_notes.BlankFragment.OnFragmentInteractionListener,
        cn.edu.is.dsse_notes.DetailFragment.OnFragmentInteractionListener
{

    private int currentDrawerPosition = 0;
    private SearchView searchView;
    private MenuItem myActionMenuItem;

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
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        selectItem(R.id.nav_list);


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
        searchView.setQueryHint("Search Notes");
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);
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
        Fragment fragment;
        if (id == R.id.nav_camera) {
            // Handle the camera action
            fragment = new BlankFragment();
        } else if (id == R.id.nav_gallery) {
            fragment = new BlankFragment();
        } else if (id == R.id.nav_slideshow) {
            fragment = new BlankFragment();
        } else if (id == R.id.nav_manage) {
            fragment = new BlankFragment();
        } else if (id == R.id.nav_share) {
            onShareAction();
            return;
        } else if (id == R.id.nav_send) {
            fragment = new BlankFragment();
        } else if (id == R.id.nav_list) {
            ListFragment lf = new ListFragment();
            lf.setListener(this);
            fragment = lf;
        } else {
            fragment = new BlankFragment();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, fragment, "Visible Fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    // Floating Action Button Click Handler
    private void onFABClicked() {
        DummyContent.DummyItem newItem = new DummyContent.DummyItem(DummyContent.getNewItemID(), "", "");
        DummyContent.ITEMS.add(newItem);
        DummyContent.ITEM_MAP.put(newItem.id, newItem);
        Fragment fd = DetailFragment.newInstance(newItem);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, fd);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
    // SearchView Interface
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, "TextSubmitted: " + query, Toast.LENGTH_SHORT).show();
        return true;
    }
    // List Item Click Interface
    @Override
    public void onItemClicked(int position) {
        DummyContent.DummyItem noteClicked = DummyContent.ITEMS.get(position);
        Fragment fd = DetailFragment.newInstance(noteClicked);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, fd);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
    // Search Action View Handler
    public boolean onQueryTextChange(String newText) {
        Toast.makeText(this, "TextChanged: " + newText, Toast.LENGTH_SHORT).show();
        return true;
    }

    public void onBlankFragmentInteraction(Uri uri) {
        return;
    }
    private void onShareAction() {
    }

    // Detail Fragment Interface
    @Override
    public void onDetailFragmentInteraction(Uri uri) {
        return;
    }

    @Override
    public void onSave(DummyContent.DummyItem note) {
        DummyContent.ITEM_MAP.get(note.id).content = note.content;
        DummyContent.ITEM_MAP.get(note.id).details = note.details;
        PostTask postTask = new PostTask();
        postTask.execute(DummyContent.ITEMS);
    }
}


