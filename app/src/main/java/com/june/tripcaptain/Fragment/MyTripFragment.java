package com.june.tripcaptain.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.june.tripcaptain.Adapter.TripAdapter;
import com.june.tripcaptain.DataClass.Place;
import com.june.tripcaptain.DataClass.Trip;
import com.june.tripcaptain.MapActivity;
import com.june.tripcaptain.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MyTripFragment extends Fragment {
    private Context mContext;
    private RecyclerView rvTrip;
    private LinearLayoutManager layoutManager;
    private TripAdapter tripAdapter;
    private ArrayList<Trip> mTripList;
    private static String TAG = "Debug";
    private static String MY_TRIP_FILE_NAME = "My_Trip";
    private static String placesBaseURL = "https://maps.googleapis.com/maps/api/place/details/json?";
    private String APIkey;
    private RequestQueue queue;
    private JSONObject detailsJSON;
    private Button btnOpenMap;

    public MyTripFragment(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_trip, container, false);

        layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL,
                false);
        rvTrip = v.findViewById(R.id.rvTrip);
//        rvTrip.setHasFixedSize(true);
        rvTrip.setLayoutManager(layoutManager);
        mTripList = new ArrayList<>();
        mTripList.add(new Trip());
        tripAdapter = new TripAdapter(mContext, mTripList.get(0).getPlaceList());
        rvTrip.setAdapter(tripAdapter);

        queue = Volley.newRequestQueue(mContext);
        readTripFromFile();

        btnOpenMap = v.findViewById(R.id.btnOpenMap);
        btnOpenMap.setOnClickListener(v1 -> {
            Intent intent = new Intent(mContext, MapActivity.class);
            mContext.startActivity(intent);
        });

        return v;
    }

    public void readTripFromFile() {
        File file = new File(mContext.getFilesDir(), MY_TRIP_FILE_NAME);
        FileReader fileReader;
        BufferedReader bufferedReader;
        String response;
        try {
            if (file.exists()) {
                StringBuilder output = new StringBuilder();
                fileReader = new FileReader(file.getAbsoluteFile());
                bufferedReader = new BufferedReader(fileReader);
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    output.append(line).append('\n');
                }

                response = output.toString();
                bufferedReader.close();

                JSONObject trip = new JSONObject(response);
                Boolean isTripExist = trip.has("trip_01");

                if(!isTripExist) {
                    //TODO no trip
                } else {
                    JSONArray placeList = (JSONArray)trip.get("trip_01");
                    for (int i = 0; i < placeList.length(); i++) {
                        String placeID = placeList.get(i).toString();
                        addPlaceToTrip(placeID);
                    }

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void addPlaceToTrip(String placeID) {

        // Get PlaceDetails URL
        String detailsURL = getPlaceDetailsURL(placeID);

        // Request a json response from the provided URL.
        final JsonObjectRequest detailsJSONRequest = new JsonObjectRequest
                (Request.Method.GET, detailsURL, null, response -> {
                    Log.d(TAG, "Response: " + response.toString());
                    detailsJSON = response;
                    try {
                        JSONObject result = detailsJSON.getJSONObject("result");

                        String placeName = result.getString("name");
                        Boolean openNow = null;
                        Float rating = null;
                        int priceLevel = 0;
                        String photoRef = null;


                        try {
                            JSONObject openingHours = result.getJSONObject("opening_hours");
                            openNow = openingHours.getBoolean("open_now");
                            rating = ((Double)result.getDouble("rating")).floatValue();
                            priceLevel = result.getInt("price_level");
                            JSONArray photos = result.getJSONArray("photos");
                            JSONObject firstPhoto = photos.getJSONObject(0);
                            photoRef = firstPhoto.getString("photo_reference");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d(TAG, placeID + "|" + placeName + "|" + openNow + "|" + rating
                                + "|" + priceLevel + "|" + photoRef);

                        mTripList.get(0).getPlaceList().add(new Place(placeID, placeName, openNow, rating, priceLevel, photoRef));
                        tripAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> Log.d(TAG, error.toString()));

        // Set the tag on the request.
        detailsJSONRequest.setTag("Details");

        // Add the request to the RequestQueue.
        queue.add(detailsJSONRequest);
    }

    public String getPlaceDetailsURL(String placeID) {
        APIkey = mContext.getResources().getString(R.string.google_maps_key);

        String detailsURL = placesBaseURL
                + "place_id=" + placeID
                + "&fields=name,opening_hours,rating,price_level,photo"
                + "&key=" + APIkey;

        return detailsURL;
    }

}
