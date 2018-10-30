package com.instalink.archive;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.instalink.archive.helpers.DatabaseHelper;
import com.instalink.archive.helpers.PrefManage;
import com.instalink.archive.model.Category;
import com.instalink.archive.model.Link;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class SaveLinkActivity extends AppCompatActivity implements OnClickListener, OnItemSelectedListener  {

    @BindView(R.id.saveLinkToolbar)
    Toolbar saveLinkToolbar;
    @BindView(R.id.edTitleSave)
    MaterialEditText edTitleSave;
    @BindView(R.id.edUrlSave)
    MaterialEditText edUrlSave;
    @BindView(R.id.saveSpinner)
    Spinner saveSpinner;
    @BindView(R.id.btnSaveLink)
    Button btnSaveLink;
    @BindView(R.id.saveLinkMainLayout)
    RelativeLayout saveLinkMainLayout;


    String type,saveTitle,saveUrl,bTitle,bUrl,clipBoardText;
    int bId,bCat_id;
    private static DatabaseHelper db;
    Cursor cursorSpinner;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_link);
        ButterKnife.bind(this);
        PrefManage prefManage = new PrefManage(this);
        if (prefManage.isFirstTimeToLaunch()) {
            Toast.makeText(getApplicationContext(),"لطفا اول به حساب خود وارد شوید",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        setSupportActionBar(saveLinkToolbar);
        ViewCompat.setLayoutDirection(saveLinkMainLayout,ViewCompat.LAYOUT_DIRECTION_RTL);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        intent = getIntent();
        Bundle extras = getIntent().getExtras();
        this.saveSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);



        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        assert clipboard != null;

        clipBoardText = clipboard.getText().toString();





        if (extras != null)
        {
            type = extras.getString("type");
        }



        if (type != null) {
            switch (type)
            {
                case "add":
                {
                    setTitle("ثبت لینک");

                    if(Patterns.WEB_URL.matcher(clipboard.getText()).matches()) {
                        if (clipBoardText.contains("https://www.instagram.com/p/"))
                        {
                            clipBoardText = "https://api.instagram.com/oembed/?url="+clipBoardText;
                            if (isNetworkAvailable())
                            {
                                new GetUsername(edTitleSave).execute(clipBoardText);
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"لطفا اینترنت خود را متصل و دوباره امتحان کنید!",Toast.LENGTH_LONG).show();
                                finish();
                            }

                        }
                        edUrlSave.setText(clipboard.getText());
                    }
                }
                break;
                default:
                {
                    setTitle("ویرایش لینک");
                    btnSaveLink.setText("ویرایش");
                    bTitle = extras.getString("title");
                    bUrl = extras.getString("url");
                    bId = extras.getInt("id");
                    bCat_id = extras.getInt("cat_id");

                    edTitleSave.setText(bTitle);
                    edUrlSave.setText(bUrl);
                    saveSpinner.setSelection(bCat_id);
                }
                break;
            }
        }





        btnSaveLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (type)
                {
                    case "add":
                    {
                        saveTitle = edTitleSave.getText().toString();
                        saveUrl = edUrlSave.getText().toString();
                        if (!(saveTitle.equals("")) && !(saveUrl.equals("")) && saveSpinner.getSelectedItemId()!= -1)
                        {
                            db.insertLink(saveTitle,saveUrl, (int) saveSpinner.getSelectedItemId());
                            Toast.makeText(getApplicationContext(),"لینک با موفقیت اضافه شد!",Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),"لطفا مقادیر خواسته شده را وارد کنید!",Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                    default:
                    {
                        saveTitle = edTitleSave.getText().toString();
                        saveUrl = edUrlSave.getText().toString();
                        if (!(saveTitle.equals("")) && !(saveUrl.equals("")) && saveSpinner.getSelectedItemId()!= -1)
                        {
                                db.updateLink(new Link(bId,saveTitle,saveUrl, (int) saveSpinner.getSelectedItemId()));
                                Toast.makeText(getApplicationContext(),"لینک با موفقیت ویرایش شد!",Toast.LENGTH_LONG).show();
                                finish();
                        } else {
                            Toast.makeText(getApplicationContext(),"لطفا مقادیر خواسته شده را وارد کنید!",Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                }
            }
        });


    }



    @Override
    protected void onStart() {
        super.onStart();
        db = new DatabaseHelper(this);
        updateCategoriesList();

        String action = this.intent.getAction();
        String intentType = this.intent.getType();
        assert intentType != null;
        if (action != null && action.equals("android.intent.action.SEND") && intentType.equals("text/plain")) {
            type = "add";
            String title = this.intent.getStringExtra("android.intent.extra.SUBJECT");
            String url = this.intent.getStringExtra("android.intent.extra.TEXT");

            if(Patterns.WEB_URL.matcher(url).matches()) {
                if (url.contains("https://www.instagram.com/p/"))
                {
                    url = "https://api.instagram.com/oembed/?url="+url;
                    if (isNetworkAvailable())
                    {
                        new GetUsername(edTitleSave).execute(url);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"لطفا اینترنت خود را متصل و دوباره امتحان کنید!",Toast.LENGTH_LONG).show();
                        finish();
                    }

                } else {
                    edTitleSave.setText(title);
                }
            }



            edUrlSave.setText(url);
        }
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if (id == -1) {
            inputNewCategory();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void updateCategoriesList(){
        cursorSpinner = db.getCategoriesList("_id DESC");
        String[] columns = new String[]{Link.COLUMN_ID, Category.COLUMN_CATEGORY};

        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new String[]{"-1", "افزودن دسته جدید..."});
        cursorSpinner = new MergeCursor(new Cursor[]{cursorSpinner,matrixCursor});

        saveSpinner.setAdapter(
                new SimpleCursorAdapter(this,R.layout.spinner_item, cursorSpinner,
                        new String[]{Category.COLUMN_CATEGORY},
                        new int[]{R.id.tv_tinted_spinner}, 0));


    }


    public void inputNewCategory() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);

        View view = layoutInflaterAndroid.inflate(R.layout.category_dialog, null);

        ViewCompat.setLayoutDirection(view,ViewCompat.LAYOUT_DIRECTION_RTL);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(view.getContext());
        alertDialogBuilderUserInput.setView(view);

        final ExtendedEditText inputNote = view.findViewById(R.id.eet_category_add);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(getString(R.string.input_new_category));


        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("ذخیره", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("بیخیال",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputNote.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "لطفا متن را وارد کنید!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                    // create new note
                createCategory(inputNote.getText().toString());
                updateCategoriesList();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    public void createCategory(String category) {
        // inserting note in db and getting
        // newly inserted note id
        db.insertCategory(category);

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }


    private class GetUsername extends AsyncTask<String, Void, String> {
        private MaterialEditText eetTitleSave;

        public GetUsername(MaterialEditText edTitleSave ) {
            this.eetTitleSave = edTitleSave;
        }

        @Override
        protected String doInBackground(String... strings) {
            String username = "UNDEFINED";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }

                JSONObject topLevel = new JSONObject(builder.toString());
                username = String.valueOf(topLevel.getString("author_name"));

                urlConnection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return username;
        }

        @Override
        protected void onPostExecute(String temp) {
            edTitleSave.setText(temp);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
