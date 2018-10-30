package com.instalink.archive.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.instalink.archive.LinkListActivity;
import com.instalink.archive.MainActivity;
import com.instalink.archive.R;
import com.instalink.archive.model.Category;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {

    private static Context context;
    private List<Category > categoriesList;
    private ArrayList<Category> categories;
    private String title;

    interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.etCategory)
        TextView title;
        @BindView(R.id.btnEdit)
        ImageButton btnEdit;
        @BindView(R.id.btnDelete)
        ImageButton btnDelete;
        private LinksAdapter.ItemClickListener clickListener;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            view.setTag(view);
            view.setOnClickListener(this);
        }

        public void setClickListener(LinksAdapter.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getPosition(), false);
        }

    }


    public CategoriesAdapter(Context context, List<Category> categoriesList) {
        this.context = context;
        this.categoriesList = categoriesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_ticket, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Category category = categoriesList.get(position);
        title = category.getCategory();
        if (title.length() > 19)
        {
            title = title.substring(0,19) + "...";
        }
        holder.title.setText(title);
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showCategoryDialog(true,category,position);
                    }
                });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MainActivity.deleteCategory(category,position, context);
                    }
                });

        holder.setClickListener(new LinksAdapter.ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(context, LinkListActivity.class);
                intent.putExtra("cat_id",category.getId());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public static void showCategoryDialog(final boolean shouldUpdate, final Category category, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);

        View view = layoutInflaterAndroid.inflate(R.layout.category_dialog, null);

        ViewCompat.setLayoutDirection(view,ViewCompat.LAYOUT_DIRECTION_RTL);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(view.getContext());
        alertDialogBuilderUserInput.setView(view);

        final ExtendedEditText inputNote = view.findViewById(R.id.eet_category_add);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? context.getString(R.string.input_new_category) : context.getString(R.string.input_update_category));

        if (shouldUpdate && category != null) {
            inputNote.setText(category.getCategory());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "بروزرسانی" : "ذخیره", new DialogInterface.OnClickListener() {
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
                    Toast.makeText(context, "لطفا متن را وارد کنید!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && category != null) {
                    // update note by it's id
                    MainActivity.updateNote(inputNote.getText().toString(), position);
                } else {
                    // create new note
                    MainActivity.createCategory(inputNote.getText().toString());
                }
            }
        });
    }

    public void setFilter(List<Category> newList){
        categories=new ArrayList<>();
        categories.addAll(newList);
        notifyDataSetChanged();
    }

}
