package com.instalink.archive.model;

public class Link {
    public static final String TABLE_NAME = "links";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_CATEGORY_ID = "category_id";

    private int id;
    private String title;
    private String url;
    private int category_id;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TITLE + " TEXT,"
                    + COLUMN_URL + " TEXT,"
                    + COLUMN_CATEGORY_ID + " INTEGER"
                    + ")";

    public Link() {
    }

    public Link(int id, String title, String url, int category_id) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.category_id = category_id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCategory_id() {
         return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }
}
