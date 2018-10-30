package com.instalink.archive.model;

public class Category {
    public static final String TABLE_NAME = "categories";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CATEGORY = "category";

    private int id;
    private String category;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_CATEGORY + " TEXT"
                    + ")";

    public Category() {
    }

    public Category(int id, String category) {
        this.id = id;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(int id) {
        this.id = id;
    }
}
