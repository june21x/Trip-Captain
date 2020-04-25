package com.june.tripcaptain.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.june.tripcaptain.DataClass.Category;
import com.june.tripcaptain.Helper.GlideApp;
import com.june.tripcaptain.R;

import java.util.ArrayList;


public class CategoryAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private ArrayList<Category> mCategoryList;
    private ImageView ivIcon;
    private TextView tvName;
    private static String TAG = "Debug";

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public MyViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public CategoryAdapter(Context context, ArrayList<Category> categoryList) {
        mContext = context;
        mCategoryList = categoryList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cvCategory = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);

        CategoryAdapter.MyViewHolder vh = new CategoryAdapter.MyViewHolder(cvCategory);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ivIcon = holder.itemView.findViewById(R.id.ivIcon);
        tvName = holder.itemView.findViewById(R.id.tvName);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(mCategoryList.get(position).getIconUriStr());

        Log.d(TAG, storageRef.toString());

        try {
            GlideApp.with(mContext).load(storageRef).into(ivIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvName.setText(mCategoryList.get(position).getName());

        holder.itemView.setOnClickListener(v -> {

        });

    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }
}
