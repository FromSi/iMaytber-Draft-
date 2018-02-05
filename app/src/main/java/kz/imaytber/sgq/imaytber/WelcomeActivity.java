package kz.imaytber.sgq.imaytber;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import kz.imaytber.sgq.imaytber.adapters.ScreenSlideWelcomeAdapter;
import kz.imaytber.sgq.imaytber.fragments.ScreenSlideWelcomeFragment;

public class WelcomeActivity extends AppCompatActivity {
    private ViewPager mPager;
    private PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mPager = findViewById(R.id.pager);
        adapter = new ScreenSlideWelcomeAdapter(getSupportFragmentManager(), getApplicationContext());
        mPager.setAdapter(adapter);
    }

}
