package com.june.tripcaptain;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.june.tripcaptain.Adapter.ViewPagerAdapter;
import com.june.tripcaptain.Helper.CubeInDepthTransformation;
import com.june.tripcaptain.Helper.GlideApp;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private Context mContext;
    private FragmentActivity mFragmentActivity;
    private ArrayList<String> mFragmentTitles = new ArrayList<>();
    private ImageView ivProfile;
    private static String TAG = "Debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        tabLayout = findViewById(R.id.tab_layout);
        mContext = this;
        mFragmentActivity = this;

        mFragmentTitles.add("News");
        mFragmentTitles.add("Recommendations");
        mFragmentTitles.add("My Trip");

        viewPager2 = findViewById(R.id.viewPager2);
        viewPager2.setPageTransformer(new CubeInDepthTransformation());
        viewPager2.setUserInputEnabled(false);
        viewPagerAdapter = new ViewPagerAdapter(mContext, mFragmentActivity);
        viewPager2.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> tab.setText(mFragmentTitles.get(position))).attach();

        ivProfile = navigationView.getHeaderView(0).findViewById(R.id.ivProfile);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://trip-captain.appspot.com/profile-image/92107850_10216474787288626_2400556895040438272_n.jpg");

        try {
            RequestOptions sharedOptions = new RequestOptions()
                                                .fitCenter()
                                                .circleCrop();
            GlideApp.with(this).load(storageRef).apply(sharedOptions).into(ivProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            // Android home
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;      // manage other entries if you have it ...
        }    return true;
    }

}
