package com.june.tripcaptain.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.june.tripcaptain.Adapter.TripAdapter;
import com.june.tripcaptain.R;

public class MyTripFragment extends Fragment {
    private Context mContext;
    private RecyclerView rvTrip;
    private LinearLayoutManager layoutManager;
    private TripAdapter tripAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_trip, container, false);
    }
}
