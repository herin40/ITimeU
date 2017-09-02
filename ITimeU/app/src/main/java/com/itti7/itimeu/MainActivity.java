package com.itti7.itimeu;

import android.graphics.Color;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    // ViewPager for movement between tabs
    ViewPager viewPager;

    // Tags of Fragments
    String mTimerTag;
    String mListTag;
    /** Getter/Setter of TimerTag */
    public void setTimerTag(String timerTag) {
        mTimerTag = timerTag;
    }

    public String getTimerTag(){
        return mTimerTag;
    }
    /** Getter/Setter of ListTag */
    public void setListTag(String listTag) {
        mListTag = listTag;
    }

    public String getListTag(){
        return mListTag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Add tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.list_selector).setText(R.string.tab_list));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.timer_selector).setText(R.string.tab_timer));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.statistics_selector).
                setText(R.string.tab_statistics));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.setting_selector).
                setText(R.string.tab_setting));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.about_selector).setText(R.string.tab_about));

        // ViewPager for swiping and navigation to the selected tab
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setBackgroundColor(Color.WHITE);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Setting pager adapter
        SimpleFragmentPagerAdapter adapter
                = new SimpleFragmentPagerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
    }

    /**
     * @return  Return existing viewpager
     * */
    public ViewPager getViewPager() {
        if (null == viewPager) {
            viewPager = (ViewPager) findViewById(R.id.viewpager);
        }
        return viewPager;
    }
}
