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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.june.tripcaptain.Adapter.NewsAdapter;
import com.june.tripcaptain.DataClass.News;
import com.june.tripcaptain.R;
import java.util.ArrayList;
import java.util.Calendar;

public class NewsFragment extends Fragment {

    private Context mContext;
    private RecyclerView rvNews;
    private LinearLayoutManager layoutManager;
    private NewsAdapter newsAdapter;
    private ArrayList<News> mNewsList;
    private FirebaseFirestore db;
    private static String TAG = "Debug";

    public NewsFragment(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news, container, false);
        rvNews = v.findViewById(R.id.rvNews);
        rvNews.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        rvNews.setLayoutManager(layoutManager);
        mNewsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(mContext, mNewsList);
        rvNews.setAdapter(newsAdapter);
        db = FirebaseFirestore.getInstance();

        initializeNews();

        return v;
    }

    public void initializeNews(){
        mNewsList.add(new News("000", Calendar.getInstance().getTime(), "loading...", "gs://trip-captain.appspot.com/broken.png","loading...","loading..."));

        db.collection("news")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mNewsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            mNewsList.add(new News(document.getId(),
                                    ((Timestamp)document.get("date")).toDate(),
                                    document.get("author").toString(),
                                    document.get("imageUriStr").toString(),
                                    document.get("title").toString(),
                                    document.get("content").toString()));
                        }

                        newsAdapter.notifyDataSetChanged();

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });

    }

}
