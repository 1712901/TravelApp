package com.example.fragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.fragment.Fragment.MyTourFragment;
import com.example.fragment.Model.GlobalClass;
import com.example.fragment.Response.CreateTourRes;
import com.example.fragment.network.APIService;
import com.example.fragment.network.RetrofitClient;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTour extends AppCompatActivity implements TextWatcher {


    EditText edTourName,edMinCost,edMaxCost;
    RadioButton rdIsPrivate;
    ImageButton imbtStartDate,imbtEndDate,imbtCreBack;
    Button btnCreate,btnSrcPlace,btnStartDate,btnEndDate,btnDesPlace;
    Date startDate,endDate;
    private final int REQUESTCODE_SRC=901;
    private final int REQUESTCODE_DES=902;

    private double latSrc;
    private double longSrc;
    private double latDes;
    private double longDEs;
    private  String descripSrc;
    private String descripDes;
    private int adults=0;
    private int child=0;



    private String authorization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        authorization=globalVariable.getAuthorization();

        init();

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTour.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                btnStartDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                c.set(year, (monthOfYear), dayOfMonth);
                                startDate=c.getTime();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTour.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                btnEndDate.setText(dayOfMonth + "-" + (monthOfYear +1) + "-" + year);
                                c.set(year, (monthOfYear), dayOfMonth);
                                endDate=c.getTime();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        btnSrcPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CreateTour.this,Map.class);
                startActivityForResult(intent,REQUESTCODE_SRC);

            }
        });
        btnDesPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CreateTour.this,Map.class);
                startActivityForResult(intent,REQUESTCODE_DES);
            }
        });

        rdIsPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rdIsPrivate.isSelected()){
                    rdIsPrivate.setChecked(false);
                    rdIsPrivate.setSelected(false);
                }else{
                    rdIsPrivate.setChecked(true);
                    rdIsPrivate.setSelected(true);
                }
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(startDate==null||endDate==null){
                    Toast.makeText(CreateTour.this,"khong thể tạo tour",Toast.LENGTH_SHORT).show();
                    return;
                }else if((long)startDate.getTime()>(long)endDate.getTime()){
                    Toast.makeText(CreateTour.this,"không thể tạo tour",Toast.LENGTH_SHORT).show();
                    return;
                }
                APIService apiService = RetrofitClient.getApiService();
                apiService.createTour(authorization,edTourName.getText().toString(),startDate.getTime(),endDate.getTime(),
                        latSrc,longSrc,latDes,longDEs,rdIsPrivate.isChecked(),adults,child,Integer.parseInt(edMinCost.getText().toString()),Integer.parseInt(edMaxCost.getText().toString()))
                        .enqueue(new Callback<CreateTourRes>() {
                            @Override
                            public void onResponse(Call<CreateTourRes> call, Response<CreateTourRes> response) {
                                if(response.code()==200){
                                    Toast.makeText(getApplicationContext(),"Tạo tour THÀNH CÔNG",Toast.LENGTH_SHORT).show();
                                    // start activity Thêm điểm Dừng
                                    Intent intent=new Intent(CreateTour.this,AddStopPoint.class);
                                    intent.putExtra("IDTour",response.body().getId());
                                    startActivity(intent);
                                }
                            }
                            @Override
                            public void onFailure(Call<CreateTourRes> call, Throwable t) {
                                Toast.makeText(getApplicationContext(),"Tạo tour THẤT BẠI",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        // Thoát
        imbtCreBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(CreateTour.this,MainActivity.class);
//                startActivity(intent);
                if(findViewById(R.id.fragment_container)!=null) {
                    MyTourFragment myTourFragment = new MyTourFragment();
                    myTourFragment.setArguments(getIntent().getExtras());

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, myTourFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                finish();

            }
        });
    }
    private void init(){
        edTourName=(EditText)findViewById(R.id.edtTourName);
        btnSrcPlace=(Button)findViewById(R.id.edtSrcPlace);
        btnDesPlace=(Button)findViewById(R.id.edtDesPlace);
        btnStartDate=(Button) findViewById(R.id.edtStartDate);
        btnEndDate=(Button) findViewById(R.id.edtEndDate);
        edMinCost=(EditText)findViewById(R.id.edMinCost);
        edMaxCost=(EditText)findViewById(R.id.edMaxCost);

        imbtStartDate=(ImageButton)findViewById(R.id.imbtStartDate);
        imbtEndDate=(ImageButton)findViewById(R.id.imbtEndDate);
        imbtCreBack=(ImageButton)findViewById(R.id.imbtCreBack);
        rdIsPrivate=(RadioButton)findViewById(R.id.rdIsPrivate);
        btnCreate=(Button)findViewById(R.id.btnCreate);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUESTCODE_SRC:
                if(1==resultCode) {
                    latSrc=data.getDoubleExtra("Latitude",0);
                    longSrc=data.getDoubleExtra("Longitude",0);
                    descripSrc=data.getStringExtra("Descrip");
                    btnSrcPlace.setText(descripSrc);
                }
                break;
            case REQUESTCODE_DES:
                if(1==resultCode) {
                    latDes=data.getDoubleExtra("Latitude",0);
                    longDEs=data.getDoubleExtra("Longitude",0);
                    descripDes=data.getStringExtra("Descrip");
                    btnDesPlace.setText(descripDes);
                }
                break;
        }
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }
    @Override
    public void afterTextChanged(Editable editable) {

    }
}
