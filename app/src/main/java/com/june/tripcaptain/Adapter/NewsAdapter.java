package com.june.tripcaptain.Adapter;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.june.tripcaptain.DataClass.News;
import com.june.tripcaptain.Helper.GlideApp;
import com.june.tripcaptain.NewsActivity;
import com.june.tripcaptain.R;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private TextView tvDate;
    private TextView tvAuthor;
    private ImageView ivImage;
    private TextView tvTitle;
    private TextView tvSubtitle;
    private ArrayList<News> mNewsList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public MyViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public NewsAdapter(Context context, ArrayList<News> newsList) {
        mContext = context;
        mNewsList = newsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cvNews = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);

        MyViewHolder vh = new MyViewHolder(cvNews);
        return vh;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        tvDate = holder.itemView.findViewById(R.id.tvDate);
        tvAuthor = holder.itemView.findViewById(R.id.tvAuthor);
        ivImage = holder.itemView.findViewById(R.id.ivImage);
        tvTitle = holder.itemView.findViewById(R.id.tvTitle);
        tvSubtitle = holder.itemView.findViewById(R.id.tvSubtitle);

        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy h:mm a");
        tvDate.setText(dateFormat.format(mNewsList.get(position).getDate()));
        tvAuthor.setText("by " + mNewsList.get(position).getAuthor());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(mNewsList.get(position).getImageUriStr());

        try {
            GlideApp.with(mContext).load(storageRef).transition(withCrossFade()).into(ivImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvTitle.setText(mNewsList.get(position).getTitle());
        if(mNewsList.get(position).getContent().length() > 80) {
            tvSubtitle.setText(mNewsList.get(position).getContent().substring(0, 80) + "...");
        }else {
            tvSubtitle.setText(mNewsList.get(position).getContent());
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, NewsActivity.class);
            intent.putExtra("News", mNewsList.get(position));
            mContext.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return mNewsList.size();

    }
}
