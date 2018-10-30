package com.instalink.archive.helpers;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.instalink.archive.LinkListActivity;
import com.instalink.archive.MainActivity;
import com.instalink.archive.R;
import com.instalink.archive.SaveLinkActivity;
import com.instalink.archive.model.Category;
import com.instalink.archive.model.Link;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LinksAdapter extends RecyclerView.Adapter<LinksAdapter.MyViewHolder> {

    private Context context;
    private List<Link> linksList;
    private String title;

    interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.etLink)
        TextView title;
        @BindView(R.id.etUrl)
        TextView etUrl;
        @BindView(R.id.btnLinkEdit)
        ImageButton btnLinkEdit;
        @BindView(R.id.btnLinkDelete)
        ImageButton btnLinkDelete;



        private ItemClickListener clickListener;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            view.setTag(view);
            view.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getPosition(), false);
        }


    }


    public LinksAdapter(Context context, List<Link> linksList) {
        this.context = context;
        this.linksList = linksList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.link_ticket, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Link link = linksList.get(position);
        title = link.getTitle();
        if (title.length() > 19)
        {
            title = title.substring(0,19) + "...";
        }
        holder.title.setText(title);
        holder.etUrl.setText(link.getUrl());
        holder.btnLinkEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SaveLinkActivity.class);
                intent.putExtra("type","edit");
                intent.putExtra("title",link.getTitle());
                intent.putExtra("url",link.getUrl());
                intent.putExtra("id",link.getId());
                intent.putExtra("cat_id",link.getCategory_id());
                context.startActivity(intent);
            }
        });
        holder.btnLinkDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinkListActivity.deleteLiknk(link,position,context);
            }
        });

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

                String loadUrl = link.getUrl();
                try {
                    if(Patterns.WEB_URL.matcher(loadUrl).matches()) {
                        if (loadUrl.contains("https://www.instagram.com/p/")) {
                            Uri uri = Uri.parse(loadUrl);
                            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                            likeIng.setPackage("com.instagram.android");

                            try {
                                context.startActivity(likeIng);
                            } catch (ActivityNotFoundException e) {
                                context.startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(loadUrl)));
                            }
                        } else {
                            if(!loadUrl.startsWith("http://") && !loadUrl.startsWith("https://")){
                                loadUrl = "http://"+loadUrl;
                            }
                            context.startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(loadUrl)));
                        }
                    }
                }catch (Exception ex)
                {
                    //Toast.makeText(context, ex.getMessage(),Toast.LENGTH_LONG);
                }

            }
        });



    }

    @Override
    public int getItemCount() {
        return linksList.size();
    }
}