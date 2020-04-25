package com.june.tripcaptain.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.june.tripcaptain.Adapter.CategoryAdapter;
import com.june.tripcaptain.Adapter.PlaceAdapter;
import com.june.tripcaptain.DataClass.Category;
import com.june.tripcaptain.DataClass.Place;
import com.june.tripcaptain.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;


public class RecommendationsFragment extends Fragment {

    private Context mContext;
    private Map<String, String> mTypeList;
    private ArrayList<Category> mCategoryList;
    private ArrayList<Place> mPlaceList;
    private RecyclerView rvCategory;
    private RecyclerView rvPlace;
    private RecyclerView.LayoutManager lmCategory;
    private RecyclerView.LayoutManager lmPlace;
    private CategoryAdapter categoryAdapter;
    private PlaceAdapter placeAdapter;
    private FirebaseFirestore db;
    private RequestQueue queue;
    private JSONObject placesJSON;
    private static String TAG = "Debug";
    private static String placesBaseURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private String APIkey;
    private String location;
    private String radius;

    public RecommendationsFragment(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recommendations, container, false);
        lmCategory = new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL,
                false);
        rvCategory = v.findViewById(R.id.rvCategory);
        rvCategory.setHasFixedSize(true);
        rvCategory.setLayoutManager(lmCategory);
        mCategoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(mContext, mCategoryList);
        rvCategory.setAdapter(categoryAdapter);
        lmPlace = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL,
                false);
        rvPlace = v.findViewById(R.id.rvPlace);
//        rvPlace.setHasFixedSize(true);
        rvPlace.setLayoutManager(lmPlace);
        mPlaceList = new ArrayList<>();
        placeAdapter = new PlaceAdapter(mContext, mPlaceList);
        rvPlace.setAdapter(placeAdapter);

        db = FirebaseFirestore.getInstance();

        APIkey = getResources().getString(R.string.google_maps_key);
        location = "4.322696,101.144344";
        radius = "11000";

        initializeCategory();
        mPlaceList.add(new Place(null, "loading...", null, null, 0, "gs://trip-captain.appspot.com/broken.png"));
        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(mContext);
        initializePlace();

        return v;
    }

    public void setTypeList(String categoryID, Category category) {
        ArrayList<String> typeList = new ArrayList<>();

        CollectionReference colRef = db.collection("category").document(categoryID).collection("type");
        colRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    typeList.add(document.get("name").toString());
                    Log.d("TEST_TYPE", document.getData().toString());
                }

                category.setTypeList(typeList);
                categoryAdapter.notifyDataSetChanged();

                for(String type: category.getTypeList()) {
                    Log.d("TEST_TYPELIST", type);
                }

            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    public void initializeCategory() {
        mCategoryList.add(new Category("gs://trip-captain.appspot.com/broken.png", "loading...",
                            null));

        db.collection("category")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mCategoryList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            Category category = new Category(document.get("icon").toString(),
                                    document.get("name").toString(), null);

                            mCategoryList.add(category);

                            setTypeList(document.getId(), category);

                        }
                        categoryAdapter.notifyDataSetChanged();

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }


    public String getPlacesURL(String type, String language, String pageToken) {
        String placesURL = placesBaseURL
                + "location=" + location
                + "&radius=" + radius;

        if (type != null) {
            placesURL = placesURL + "&type=" + type;
        }

        if (language != null) {
            placesURL = placesURL + "&language=" + language;
        }

        placesURL = placesURL + "&key=" + APIkey;

        if (pageToken != null) {
            placesURL = placesURL + "&pagetoken=" + pageToken;
        }

        return placesURL;
    }

    public void displayMorePlaces(String nextPageToken) {
        // Get Places URL
        String placesURL = getPlacesURL(null, null, nextPageToken);

        Log.d("NextPageTokenURL", placesURL);

        // Request a json response from the provided URL.
        final JsonObjectRequest placesJSONRequest = new JsonObjectRequest
                (Request.Method.GET, placesURL, null, response -> {
                    Log.d(TAG, "NextPageResponse: " + response.toString());
                    placesJSON = response;
                    try {
                        JSONArray resultList = placesJSON.getJSONArray("results");

                        String nextNextPageToken = null;
                        try {
                            nextNextPageToken =  placesJSON.getString("next_page_token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int i = 0; i < resultList.length(); i++) {
                            JSONObject result = resultList.getJSONObject(i);
                            String placeID = result.getString("place_id");
                            String placeName = result.getString("name");
                            Boolean openNow = null;
                            try {
                                JSONObject openingHours = result.getJSONObject("opening_hours");
                                openNow = openingHours.getBoolean("open_now");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JSONArray types = result.getJSONArray("types");
                            Double rating = null;
                            try {
                                rating = result.getDouble("rating");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            int priceLevel = 0;
                            try {
                                priceLevel = result.getInt("price_level");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String photoRef = null;
                            try {
                                JSONArray photos = result.getJSONArray("photos");
                                JSONObject firstPhoto = photos.getJSONObject(0);
                                photoRef = firstPhoto.getString("photo_reference");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.d(TAG, placeID + "|" + placeName + "|" + openNow + "|" + rating
                                    + "|" + priceLevel + "|" + photoRef);

                            mPlaceList.add(new Place(placeID, placeName, openNow, rating, priceLevel, photoRef));
                            placeAdapter.notifyDataSetChanged();

                        }

                        if(nextNextPageToken != null) {
                            displayMorePlaces(nextNextPageToken);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> Log.d(TAG, error.toString()));


        // Set the tag on the request.
        placesJSONRequest.setTag("Places");

        // Add the request to the RequestQueue.
        queue.add(placesJSONRequest);
    }


    public void initializePlace() {
        //clear default mPlaceList
        mPlaceList.clear();

        // Get Places URL
        String placesURL = getPlacesURL("restaurant", "en", null);

        // Request a json response from the provided URL.
        final JsonObjectRequest placesJSONRequest = new JsonObjectRequest
                (Request.Method.GET, placesURL, null, response -> {
                    Log.d(TAG, "Response: " + response.toString());
                    placesJSON = response;
                    try {
                        JSONArray resultList = placesJSON.getJSONArray("results");

                        String nextPageToken = null;
                        try {
                            nextPageToken =  placesJSON.getString("next_page_token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int i = 0; i < resultList.length(); i++) {
                            JSONObject result = resultList.getJSONObject(i);
                            String placeID = result.getString("place_id");
                            String placeName = result.getString("name");
                            Boolean openNow = null;
                            try {
                                JSONObject openingHours = result.getJSONObject("opening_hours");
                                openNow = openingHours.getBoolean("open_now");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JSONArray types = result.getJSONArray("types");
                            Double rating = null;
                            try {
                                rating = result.getDouble("rating");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            int priceLevel = 0;
                            try {
                                priceLevel = result.getInt("price_level");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String photoRef = null;
                            try {
                                JSONArray photos = result.getJSONArray("photos");
                                JSONObject firstPhoto = photos.getJSONObject(0);
                                photoRef = firstPhoto.getString("photo_reference");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.d(TAG, placeID + "|" + placeName + "|" + openNow + "|" + rating
                                    + "|" + priceLevel + "|" + photoRef);

                            mPlaceList.add(new Place(placeID, placeName, openNow, rating, priceLevel, photoRef));
                            placeAdapter.notifyDataSetChanged();

                        }

                        if(nextPageToken != null) {
                            displayMorePlaces(nextPageToken);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> Log.d(TAG, error.toString()));

        // Set the tag on the request.
        placesJSONRequest.setTag("Places");

        // Add the request to the RequestQueue.
        queue.add(placesJSONRequest);
    }


}
