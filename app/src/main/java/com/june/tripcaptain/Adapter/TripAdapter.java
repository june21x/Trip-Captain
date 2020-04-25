package com.june.tripcaptain.Adapter;

import android.content.Context;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.june.tripcaptain.DataClass.Trip;

import java.util.ArrayList;

public class TripAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private ArrayList<Trip> mTripList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public MyViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public TripAdapter(Context context, ArrayList<Trip> tripList) {
        mContext = context;
        mTripList = tripList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mTripList.get(0).getPlaceList().size();
    }
}
