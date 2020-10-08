package com.example.fragment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.fragment.network.APIService;
import com.example.fragment.network.RetrofitClient;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class verifyOTP extends AppCompatActivity {

    private int userId;
    private EditText edtOTP,edtedtnewPW;
    private Button btnDone;
    private ImageButton imbtBackVerify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);


        Intent intent=getIntent();
        userId=intent.getIntExtra("IdUser",-1);

        edtedtnewPW=(EditText)findViewById(R.id.edtnewPW);
        edtOTP=(EditText)findViewById(R.id.edtOTP);
        btnDone=(Button) findViewById(R.id.btnVerity);
        imbtBackVerify=(ImageButton)findViewById(R.id.imbtBackVerify);

        imbtBackVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(verifyOTP.this,Recovery.class);
                startActivity(intent);
                finish();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIService apiService= RetrofitClient.getApiService();
                apiService.verifyOtp(userId,edtedtnewPW.getText().toString().trim(),edtOTP.getText().toString().trim())
                        .enqueue(new Callback<JSONObject>() {
                            @Override
                            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                                if (response.isSuccessful()){
                                    //
                                    Intent intent=new Intent(verifyOTP.this,Login.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(verifyOTP.this,"Thay đổi password Thành công",Toast.LENGTH_SHORT).show();
                                }else if(response.code()==403){
                                    edtOTP.setError("");
                                }else {
                                    Toast.makeText(verifyOTP.this,response.message(),Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<JSONObject> call, Throwable t) {
                                Toast.makeText(verifyOTP.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}
