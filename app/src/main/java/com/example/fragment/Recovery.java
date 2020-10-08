package com.example.fragment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fragment.Model.RecoveryReq;
import com.example.fragment.Response.RecoveryRes;
import com.example.fragment.network.APIService;
import com.example.fragment.network.RetrofitClient;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Recovery extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spnTypeRecovery;
    private Button btnSend;
    private EditText value;
    private ImageButton imbtBackReg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        spnTypeRecovery=(Spinner)findViewById(R.id.spnTypeRecovery);
        btnSend=(Button)findViewById(R.id.btnSend);
        value=(EditText)findViewById(R.id.btnValue);
        imbtBackReg=(ImageButton) findViewById(R.id.imbtBackReg);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Recovery.this,
                R.array.Type_Recovery, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTypeRecovery.setAdapter(adapter);
        spnTypeRecovery.setOnItemSelectedListener(this);

        imbtBackReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(Recovery.this,Login.class);
//                startActivity(intent);
                finish();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIService apiService= RetrofitClient.getApiService();
                apiService.recoveryPassword("application/json",new RecoveryReq(spnTypeRecovery.getSelectedItem().toString(),value.getText().toString()))
                        .enqueue(new Callback<RecoveryRes>() {
                            @Override
                            public void onResponse(Call<RecoveryRes> call, Response<RecoveryRes> response) {
                                if(response.isSuccessful()){
                                    Intent intent=new Intent(Recovery.this,verifyOTP.class);
                                    intent.putExtra("IdUser",response.body().getUserId());
                                    startActivity(intent);
                                    finish();
                                }
                                else if(response.code()==500){
                                    Toast.makeText(Recovery.this,response.message(),Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<RecoveryRes> call, Throwable t) {
                                Toast.makeText(Recovery.this,"No Internet",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                APIService apiService= RetrofitClient.getApiService();
//                apiService.recoveryPW(spnTypeRecovery.getSelectedItem().toString().trim(), value.getText().toString().trim(), new Callback<RecoveryRes>() {
//                    @Override
//                    public void onResponse(Call<RecoveryRes> call, Response<RecoveryRes> response) {
//                     if(response.isSuccessful()){
//                         Log.i("Value",response.body().toString()+"Success");
//                     }else{
//                         Log.i("Value",response.toString());
//                     }
//                    }
//
//                    @Override
//                    public void onFailure(Call<RecoveryRes> call, Throwable t) {
//                        Log.i("Value",t.getMessage()+"Fail");
//                    }
//                });
//            }
//        });

    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
