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

public class Register extends AppCompatActivity {

    EditText edtFullName,edtEmail,edtPhone,edtPassword,edtConfirm;
    Button btnRegister;
    ImageButton btnBackReg;
    String fullName,email,phone,password,confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtFullName=(EditText)findViewById(R.id.edtFullName);
        edtEmail=(EditText)findViewById(R.id.edtEmail);
        edtPhone=(EditText)findViewById(R.id.edtPhone);
        edtPassword=(EditText)findViewById(R.id.edtPassword);
        edtConfirm=(EditText)findViewById(R.id.edtConfirm);
        btnRegister=(Button)findViewById(R.id.btnRegister);
        btnBackReg=(ImageButton)findViewById(R.id.btnBackReg);

        btnBackReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(Register.this,Login.class);
//                startActivity(intent);
                finish();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullName=edtFullName.getText().toString().trim();
                email=edtEmail.getText().toString().trim();
                phone=edtPhone.getText().toString().trim();
                password=edtPassword.getText().toString().trim();
                confirm=edtConfirm.getText().toString().trim();

                APIService apiService = RetrofitClient.getApiService();
                apiService.register(password,fullName,email,phone)
                        .enqueue(new Callback<JSONObject>() {
                            @Override
                            public void onResponse(Call<JSONObject> call, retrofit2.Response<JSONObject> response) {
                                if(response.isSuccessful()){
                                    Intent intent=new Intent(Register.this, Login.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(),"Đăng ký thành công",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else if(response.code()==400){
                                    Toast.makeText(Register.this,"Email hoặc Phone đã tồn tại",Toast.LENGTH_SHORT).show();
                                }
                                else if(response.code()==500){
                                    Toast.makeText(Register.this,"Server error Vui lòng thử lại",Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<JSONObject> call, Throwable t) {
                                //TODO Xử lý lỗi
                                Toast.makeText(Register.this,"No Internet",Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        /* login by google*/
//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
//        if (acct != null) {
//            String personName = acct.getDisplayName();
//            String personGivenName = acct.getGivenName();
//            String personFamilyName = acct.getFamilyName();
//            String personEmail = acct.getEmail();
//            String personId = acct.getId();
//            Uri personPhoto = acct.getPhotoUrl();
//            String tokenID=acct.getIdToken();
//            Toast.makeText(Register.this,tokenID,Toast.LENGTH_SHORT).show();
//            Log.println(Log.INFO,"aa",tokenID);
//
//            OkHttpClient client = new OkHttpClient();
//            RequestBody requestBody = new FormEncodingBuilder()
//                    .add("grant_type", "authorization_code")
//                    .add("client_id", "812741506391-h38jh0j4fv0ce1krdkiq0hfvt6n5amrf.apps.googleusercontent.com")
//                    .add("client_secret", "{clientSecret}")
//                    .add("redirect_uri","")
//                    .add("code", "4/4-GMMhmHCXhWEzkobqIHGG_EnNYYsAkukHspeYUk9E8")
//                    .add("id_token", tokenID) // Added this extra parameter here
//                    .build();
//
//        }
    }
}
