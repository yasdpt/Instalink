package com.instalink.archive;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.instalink.archive.helpers.PrefManage;

public class LoginActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefManage prefManage = new PrefManage(this);
        if (!(prefManage.isFirstTimeToLaunch())) {
            prefManage.setFirstTimeLaunch(false);
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.loginToolbar);
        toolbar.setTitle("اینستالینک");
        setSupportActionBar(toolbar);
        LinearLayout linearLayout =findViewById(R.id.loginActivityLayout);
        ViewCompat.setLayoutDirection(linearLayout,ViewCompat.LAYOUT_DIRECTION_RTL);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        FragmentAdapter adapter = new FragmentAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
}
