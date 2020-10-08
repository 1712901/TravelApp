package com.example.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.fragment.Fragment.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    final int REQUEST_CODE_LOGIN=1;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav=(BottomNavigationView)findViewById(R.id.nav);
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,new LisTourFragment());
        transaction.commit();

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragmet=null;
                switch (menuItem.getItemId()){
                    case R.id.nav_listTour:
                        selectedFragmet=new LisTourFragment();
                        fab.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.nav_myTour:
                        selectedFragmet=new MyTourFragment();
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case R.id.nav_notify:
                        selectedFragmet=new NotifyFragment();
                        fab.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.nav_setting:
                        selectedFragmet=new SettingFragment();
                        fab.setVisibility(View.INVISIBLE);
                        break;
                        default:
                            return false;
                }
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,selectedFragmet);
                transaction.commit();
                return true;
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,CreateTour.class);
                startActivity(intent);
                onPause();
            }
        });
    }

}
