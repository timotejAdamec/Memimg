package com.adamec.timotej.poznavacka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.adamec.timotej.poznavacka.BottomNavigationViewHelper;
import com.adamec.timotej.poznavacka.R;
import com.adamec.timotej.poznavacka.activities.lists.MyListsActivity;
import com.adamec.timotej.poznavacka.activities.practice.PracticeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ViewPager mViewPager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        /* Navigation bar*/
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_practice:
                        Intent intent0 = new Intent(MainActivity.this, PracticeActivity.class);
                        startActivity(intent0);
                        break;

                    case R.id.nav_lists:
                        Intent intent1 = new Intent(MainActivity.this, MyListsActivity.class);
                        startActivity(intent1);
                        break;

                    /*case R.id.nav_test:
                        Intent intent3 = new Intent(MainActivity.this, TestActivity.class);
                        startActivity(intent3);
                        break;*/

                    case R.id.nav_account:
                        /*Intent intent4 = new Intent(ListsActivity.this, AccountActivity.class);
                        startActivity(intent4);
                        break;*/

                }


                return false;
            }
        });

    }
}
