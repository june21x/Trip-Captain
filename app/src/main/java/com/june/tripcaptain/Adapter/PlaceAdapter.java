package com.june.tripcaptain.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.june.tripcaptain.DataClass.Place;
import com.june.tripcaptain.Helper.GlideApp;
import com.june.tripcaptain.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class PlaceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Place> mPlaceList;
    private ImageView ivImage;
    private TextView tvName;
    private AppCompatRatingBar rating;
    private CardView cvRating;
    private CardView cvOpenNow;
    private TextView tvOpenNow;
    private Button btnAddToTrip;
    private static String TAG = "Debug";
    private static String MY_TRIP_FILE_NAME = "My_Trip";

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public MyViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public PlaceAdapter(Context context, ArrayList<Place> placeList) {
        mContext = context;
        mPlaceList = placeList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cvPlace = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_place, parent, false);

        PlaceAdapter.MyViewHolder vh = new PlaceAdapter.MyViewHolder(cvPlace);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ivImage = holder.itemView.findViewById(R.id.ivImage);
        tvName = holder.itemView.findViewById(R.id.tvName);
        rating = holder.itemView.findViewById(R.id.rating);
        cvRating = holder.itemView.findViewById(R.id.cvRating);
        cvOpenNow = holder.itemView.findViewById(R.id.cvOpenNow);
        tvOpenNow = holder.itemView.findViewById(R.id.tvOpenNow);
        btnAddToTrip = holder.itemView.findViewById(R.id.btnAddToTrip);

        String photoRef = mPlaceList.get(position).getPhotoRef();

        if(photoRef == null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://trip-captain.appspot.com/broken.png");

            try {
                GlideApp.with(mContext).load(storageRef).transition(withCrossFade()).into(ivImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Glide.with(mContext).load(getPhotoURL(photoRef)).transition(withCrossFade()).into(ivImage);
        }

        if(mPlaceList.get(position).getName().length() > 15) {
            tvName.setText(mPlaceList.get(position).getName().substring(0, 15) + "...");
        } else {
            tvName.setText(mPlaceList.get(position).getName());
        }

        if(mPlaceList.get(position).getRating() == null) {
            rating.setVisibility(View.INVISIBLE);
            cvRating.setVisibility(View.INVISIBLE);
        } else {
            rating.setRating(mPlaceList.get(position).getRating());
        }

        if(mPlaceList.get(position).getOpenNow() == null) {
            cvOpenNow.setVisibility(View.INVISIBLE);
            tvOpenNow.setVisibility(View.INVISIBLE);
        } else if (mPlaceList.get(position).getOpenNow() == true){
            cvOpenNow.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorPrimaryLight, null)));
            tvOpenNow.setText("OPEN");
        } else {
            cvOpenNow.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorAccent, null)));
            tvOpenNow.setText("CLOSED");
        }

        btnAddToTrip.setOnClickListener(v -> {
            addPlaceToTripFile(mPlaceList.get(position).getPlaceID());
            Snackbar.make(holder.itemView, mPlaceList.get(position).getName() + " is added to TRIP 1.", BaseTransientBottomBar.LENGTH_LONG).show();
        });

    }

    public void addPlaceToTripFile(String placeID) {
        File file = new File(mContext.getFilesDir(), MY_TRIP_FILE_NAME);
        FileReader fileReader = null;
        FileWriter fileWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        String response = null;

        try{
            if(!file.exists()) {
                file.createNewFile();
                fileWriter = new FileWriter(file.getAbsoluteFile());
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write("{}");
                bufferedWriter.close();
            }

            StringBuffer output = new StringBuffer();
            fileReader = new FileReader(file.getAbsolutePath());
            bufferedReader = new BufferedReader(fileReader);
            String line = "";

            while ((line = bufferedReader.readLine())!= null) {
                output.append(line + "\n");
            }

            response = output.toString();
            bufferedReader.close();

            JSONObject trip = new JSONObject(response);
            Boolean isTripExist = trip.has("trip_01");

            if(!isTripExist) {
                JSONArray placeList = new JSONArray();
                placeList.put(placeID);
                trip.put("trip_01", placeList);
            } else {
                JSONArray placeList = (JSONArray)trip.get("trip_01");
                placeList.put(placeID);
            }

            fileWriter = new FileWriter(file.getAbsoluteFile());
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(trip.toString());
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
