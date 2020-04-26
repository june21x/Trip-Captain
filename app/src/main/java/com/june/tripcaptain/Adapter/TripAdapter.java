package com.june.tripcaptain.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.june.tripcaptain.DataClass.Place;
import com.june.tripcaptain.DataClass.Trip;
import com.june.tripcaptain.R;
import java.util.ArrayList;
import com.june.tripcaptain.Helper.GlideApp;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class TripAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private ArrayList<Place> mPlaceList;
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

    public TripAdapter(Context context, ArrayList<Place> placeList) {
        mContext = context;
        mPlaceList = placeList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cvTrip = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trip, parent, false);

        TripAdapter.MyViewHolder vh = new TripAdapter.MyViewHolder(cvTrip);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ivIcon = holder.itemView.findViewById(R.id.ivIcon);
        tvName = holder.itemView.findViewById(R.id.tvName);

        String photoRef = mPlaceList.get(position).getPhotoRef();

        if(photoRef == null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://trip-captain.appspot.com/broken.png");

            try {
                GlideApp.with(mContext).load(storageRef).transition(withCrossFade()).into(ivIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Glide.with(mContext).load(getPhotoURL(photoRef)).transition(withCrossFade()).into(ivIcon);
        }

        tvName.setText(mPlaceList.get(position).getName());
    }

    public String getPhotoURL(String photoRef) {
        String photoBaseURL = "https://maps.googleapis.com/maps/api/place/photo?";
        String APIkey = mContext.getResources().getString(R.string.google_maps_key);
        String maxHeight = "600";

        String photoURL = photoBaseURL
                + "maxheight=" + maxHeight
                + "&photoreference=" + photoRef
                + "&key=" + APIkey;

        return photoURL;
    }


    @Override
    public int getItemCount() {
        return mPlaceList.size();
    }
}
