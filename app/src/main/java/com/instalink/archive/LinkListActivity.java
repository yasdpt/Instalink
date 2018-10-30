package com.instalink.archive;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.instalink.archive.helpers.CategoriesAdapter;
import com.instalink.archive.helpers.DatabaseHelper;
import com.instalink.archive.helpers.LinksAdapter;
import com.instalink.archive.model.Category;
import com.instalink.archive.model.Link;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LinkListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    @BindView(R.id.link_recycler_view)
    RecyclerView linksRecyclerView;
    @BindView(R.id.listLinkToolbar)
    Toolbar linkListToolbar;

    private static LinksAdapter linksAdapter;
    private static List<Link> linksList = new ArrayList<>();
    private static DatabaseHelper db;
    private static TextView tvEmptyLink;
    int cat_id=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_list);
        ButterKnife.bind(this);
        ViewCompat.setLayoutDirection(linkListToolbar,ViewCompat.LAYOUT_DIRECTION_RTL);
        setSupportActionBar(linkListToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvEmptyLink = findViewById(R.id.tvEmptyLink);

        Bundle extras = getIntent().getExtras();


        if (extras != null)
        {
            cat_id = extras.getInt("cat_id");
        }

        db = new DatabaseHelper(this);
        linksList.clear();
        linksList.addAll(db.getAllLinks(cat_id));

        linksAdapter = new LinksAdapter(this, linksList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        linksRecyclerView.setLayoutManager(mLayoutManager);
        linksRecyclerView.setItemAnimator(new DefaultItemAnimator());
        linksRecyclerView.setAdapter(linksAdapter);

        toggleEmptyLink();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        linksList.clear();
        linksList.addAll(db.getAllLinks(cat_id));
        linksAdapter.notifyDataSetChanged();
        toggleEmptyLink();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem search=menu.findItem(R.id.action_search);

        //this 2 lines
        SearchView searchView=(SearchView)search.getActionView();
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(search, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //Toast.makeText(getApplicationContext(),"back pressed",Toast.LENGTH_SHORT).show();
                linksList.clear();
                linksList.addAll(db.getAllLinks(cat_id));
                linksAdapter.notifyDataSetChanged();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onQueryTextChange(String query) {
        linksList.clear();
        linksList.addAll(db.getAllLinks(cat_id));

        final List<Link> filteredModelList = filter(linksList, query);

        linksList.clear();
        linksList.addAll(filteredModelList);
        linksAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private static List<Link> filter(List<Link> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Link> filteredModelList = new ArrayList<>();
        for (Link model : models) {
            final String text = model.getTitle().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public static void deleteLiknk(final Link link, final int position, final Context context) {

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                    {
                        // deleting the note from db
                        db.deleteLink(link);

                        // removing the note from the list
                        linksList.remove(link);
                        linksAdapter.notifyItemRemoved(position);
                        toggleEmptyLink();
                    }
                    break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("آیا مطمئنید که میخواهید لینک را حذف کنید؟").setPositiveButton("بله", dialogClickListener)
                .setNegativeButton("بیخیال", dialogClickListener).show();



    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    private static void toggleEmptyLink() {
        if (linksList.size() > 0) {
            tvEmptyLink.setVisibility(View.GONE);
        } else {
            tvEmptyLink.setVisibility(View.VISIBLE);
        }
    }


}
