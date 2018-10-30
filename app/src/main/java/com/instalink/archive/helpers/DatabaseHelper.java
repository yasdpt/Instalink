package com.instalink.archive.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.instalink.archive.model.Category;
import com.instalink.archive.model.Link;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    //SQLiteDatabase dbReadable;
    //SQLiteDatabase dbWritable;


    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "links_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Category.CREATE_TABLE);
        db.execSQL(Link.CREATE_TABLE);


    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Category.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Link.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    /*public synchronized void close() {
        super.close();
        if (this.dbWritable != null) {
            this.dbWritable.close();
        }
        if (this.dbReadable != null) {
            this.dbReadable.close();
        }
    }*/

    public Cursor getCategoriesList(String sortBy) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Category.TABLE_NAME, new String[]{Category.COLUMN_ID,Category.COLUMN_CATEGORY}, null, null, null, null, sortBy);
        return cursor;
    }

    public long insertCategory(String categoryName) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Category.COLUMN_CATEGORY, categoryName);
        Long id = db.insert(Category.TABLE_NAME, null, contentValues);
        db.close();
        return id;
    }

    public void insertLink(String linkTitle,String linkUrl,int linkCategoryId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Link.COLUMN_TITLE,linkTitle);
        contentValues.put(Link.COLUMN_URL,linkUrl);
        contentValues.put(Link.COLUMN_CATEGORY_ID,linkCategoryId);

        db.insert(Link.TABLE_NAME,null,contentValues);
        db.close();
    }

    public Category getCategory(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Category.TABLE_NAME,
                new String[]{Category.COLUMN_ID, Category.COLUMN_CATEGORY},
                Category.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Category note = new Category(
                cursor.getInt(cursor.getColumnIndex(Category.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Category.COLUMN_CATEGORY)));

        // close the db connection
        cursor.close();

        return note;
    }

    public List<Category> getAllCategories() {

        SQLiteDatabase db = this.getWritableDatabase();
        List<Category> categories = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Category.TABLE_NAME + " ORDER BY " +
                Category.COLUMN_ID + " ASC";


        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndex(Category.COLUMN_ID)));
                category.setCategory(cursor.getString(cursor.getColumnIndex(Category.COLUMN_CATEGORY)));

                categories.add(category);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return categories;
    }

    public int getCategoriesCount() {
        String countQuery = "SELECT  * FROM " + Category.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public List<Link> getAllLinks(int cat_id) {
        List<Link> links = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Link.TABLE_NAME + " WHERE " + Link.COLUMN_CATEGORY_ID + "=" + cat_id + " ORDER BY " +
                Link.COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Link link = new Link();
                link.setId(cursor.getInt(cursor.getColumnIndex(Link.COLUMN_ID)));
                link.setTitle(cursor.getString(cursor.getColumnIndex(Link.COLUMN_TITLE)));
                link.setUrl(cursor.getString(cursor.getColumnIndex(Link.COLUMN_URL)));

                links.add(link);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return links;
    }

    public int getLinksCount(int cat_id) {
        String countQuery = "SELECT  * FROM " + Link.TABLE_NAME + " WHERE " + Link.COLUMN_CATEGORY_ID + "=" + cat_id ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Category.COLUMN_CATEGORY, category.getCategory());

        // updating row
        return db.update(Category.TABLE_NAME, values, Category.COLUMN_ID + " = ?",
                new String[]{String.valueOf(category.getId())});
    }

    public void deleteCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Category.TABLE_NAME, Category.COLUMN_ID + " = ?",
                new String[]{String.valueOf(category.getId())});
        db.close();
    }

    public int updateLink(Link link) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Link.COLUMN_TITLE,link.getTitle());
        values.put(Link.COLUMN_URL, link.getUrl());
        values.put(Link.COLUMN_CATEGORY_ID,link.getCategory_id());

        // updating row
        return db.update(Link.TABLE_NAME, values, Link.COLUMN_ID + " = ?",
                new String[]{String.valueOf(link.getId())});
    }

    public void deleteLink(Link link) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Link.TABLE_NAME, Link.COLUMN_ID + " = ?",
                new String[]{String.valueOf(link.getId())});
        db.close();
    }


}