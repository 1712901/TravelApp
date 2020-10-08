package com.example.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.fragment.Fragment.InforTourFragment;
import com.example.fragment.Fragment.LisTourFragment;
import com.example.fragment.Fragment.ReviewsTourFragment;
import com.example.fragment.Model.GlobalClass;
import com.example.fragment.Model.ModelMember;
import com.example.fragment.Model.ModelStopPoint;
import com.example.fragment.Model.ModelTour;
import com.example.fragment.Model.StopPoint;
import com.example.fragment.Response.InforTourRes;
import com.example.fragment.Response.Member;
import com.example.fragment.Response.TourRes;
import com.example.fragment.network.APIService;
import com.example.fragment.network.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourInfor extends AppCompatActivity{


    BottomNavigationView bottomNav;
    ImageButton imbtInfBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_infor);

        bottomNav=(BottomNavigationView)findViewById(R.id.navInfor);
        this.imbtInfBack=(ImageButton)findViewById(R.id.imbtInfBack);

        imbtInfBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentInforTour,new InforTourFragment()).commit();

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragmet=null;
                switch (menuItem.getItemId()){
                    case R.id.nav_inf_general:
                        selectedFragmet=new InforTourFragment();
                        break;
                    case  R.id.nav_inf_reviews:
                        selectedFragmet=new ReviewsTourFragment();
                        break;
                        default:
                            return false;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentInforTour,selectedFragmet).commit();
                return true;
            }
        });
    }
}

