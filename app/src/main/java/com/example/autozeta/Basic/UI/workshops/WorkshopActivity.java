package com.example.autozeta.Basic.UI.workshops;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.autozeta.ActivityCheckClass;
import com.example.autozeta.Basic.UI.workshops.workshopPages.CalendarFragment;
import com.example.autozeta.Basic.UI.workshops.workshopPages.InfoFragment;
import com.example.autozeta.Basic.UI.workshops.workshopPages.ReviewsFragment;
import com.example.autozeta.Basic.UI.workshops.workshopPages.WorkshopDataHolder;
import com.example.autozeta.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import data.Review;
import data.Workshop;

public class WorkshopActivity extends FragmentActivity {

    private static final int NUM_PAGES=3;

    public Workshop workshop;
    public String workshopID;
    public TextView name;

    private TabLayout tabLayout;

    public Review review;

    public ViewPager2 viewPager;

    private FragmentStateAdapter pagerAdapter;

    public WorkshopDataHolder holder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workshop);

        workshopID = getIntent().getExtras().get("workshop_id").toString();

        name=findViewById(R.id.name);
        viewPager=findViewById(R.id.view_pager);
        pagerAdapter=new WorkshopActivity.ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setUserInputEnabled(false);


        tabLayout=findViewById(R.id.tabs);
        String[] pages = new String[]{"Osnovne informacije","Kalendar","Recenzije"};
        new TabLayoutMediator(tabLayout,viewPager,((tab, position) -> tab.setText(pages[(position)]))).attach();

    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter{

        public ScreenSlidePagerAdapter(FragmentActivity f){
            super(f);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position){
                case 0:
                    return new InfoFragment();
                case 1:
                    return new CalendarFragment();
                case 2:
                    return new ReviewsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityCheckClass.SetActivity(this);
        ActivityCheckClass.setOtherUser(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityCheckClass.clearOtherUser(null);
        ActivityCheckClass.ClearActivity(this);
    }

}
