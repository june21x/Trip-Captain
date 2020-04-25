package com.june.tripcaptain.Adapter;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.june.tripcaptain.DataClass.News;
import com.june.tripcaptain.Fragment.MyTripFragment;
import com.june.tripcaptain.Fragment.NewsFragment;
import com.june.tripcaptain.Fragment.RecommendationsFragment;

import java.util.ArrayList;

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class ViewPagerAdapter extends FragmentStateAdapter {
    private Context mContext;

    public ViewPagerAdapter(Context context, FragmentActivity fragmentActivity, ArrayList<News> newsList) {
        super(fragmentActivity);
        mContext = context;
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new NewsFragment(mContext);
            case 1:
                return new RecommendationsFragment(mContext);
            case 2:
                return new MyTripFragment();
        }
        return new NewsFragment(mContext);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}

