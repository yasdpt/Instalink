package com.instalink.archive;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.instalink.archive.helpers.CategoriesAdapter;
import com.instalink.archive.helpers.DatabaseHelper;
import com.instalink.archive.model.Category;
import com.instalink.archive.model.Link;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.mainToolbar)
    Toolbar toolbar;
    @BindView(R.id.category_recycler_view)
    RecyclerView categoriesRecyclerView;



    private static CategoriesAdapter categoriesAdapter;
    private static List<Category> categoriesList = new ArrayList<>();
    private static DatabaseHelper db;
    private SQLiteDatabase sqldb;
    private static TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ViewCompat.setLayoutDirection(toolbar,ViewCompat.LAYOUT_DIRECTION_RTL);
        toolbar.setTitle("اینستالینک");
        setSupportActionBar(toolbar);
        tvEmpty = findViewById(R.id.tvEmptyMain);


        db = new DatabaseHelper(this);
        categoriesList.clear();
        categoriesList.addAll(db.getAllCategories());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SaveLinkActivity.class);
                intent.putExtra("type","add");
                startActivity(intent);
            }
        });

        categoriesAdapter = new CategoriesAdapter(this, categoriesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        categoriesRecyclerView.setLayoutManager(mLayoutManager);
        categoriesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        categoriesRecyclerView.setAdapter(categoriesAdapter);

        toggleEmptyCategory();


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        categoriesList.clear();
        categoriesList.addAll(db.getAllCategories());
        categoriesAdapter.notifyDataSetChanged();
        toggleEmptyCategory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        categoriesList.clear();
        categoriesList.addAll(db.getAllCategories());
        categoriesAdapter.notifyDataSetChanged();
        toggleEmptyCategory();
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
                categoriesList.clear();
                categoriesList.addAll(db.getAllCategories());
                categoriesAdapter.notifyDataSetChanged();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onQueryTextChange(String query) {
        categoriesList.clear();
        categoriesList.addAll(db.getAllCategories());

        final List<Category> filteredModelList = filter(categoriesList, query);

        categoriesList.clear();
        categoriesList.addAll(filteredModelList);
        categoriesAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private static List<Category> filter(List<Category> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Category> filteredModelList = new ArrayList<>();
        for (Category model : models) {
            final String text = model.getCategory().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }


    public static void createCategory(String category) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertCategory(category);

        // get the newly inserted note from db
        Category n = db.getCategory(id);

        if (n != null) {
            // adding new note to array list at 0 position
            categoriesList.add(0, n);

            // refreshing the list
            categoriesAdapter.notifyDataSetChanged();

        }
        toggleEmptyCategory();
    }


    public static void updateNote(String note, int position) {
        Category n = categoriesList.get(position);
        // updating note text
        n.setCategory(note);

        // updating note in db
        db.updateCategory(n);

        // refreshing the list
        categoriesList.set(position, n);
        categoriesAdapter.notifyItemChanged(position);

        toggleEmptyCategory();
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    public static void deleteCategory(final Category category, final int position, final Context context) {

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                    {
                        // deleting the note from db
                        db.deleteCategory(category);

                        // removing the note from the list
                        categoriesList.remove(category);
                        categoriesAdapter.notifyItemRemoved(position);
                        toggleEmptyCategory();
                    }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("آیا مطمئنید که میخواهید دسته را حذف کنید؟").setPositiveButton("بله", dialogClickListener)
                .setNegativeButton("بیخیال", dialogClickListener).show();



    }

    private static void toggleEmptyCategory() {
        if (categoriesList.size() > 0) {
            tvEmpty.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }
}
