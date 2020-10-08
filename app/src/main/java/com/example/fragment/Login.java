package com.example.fragment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fragment.Model.GlobalClass;
import com.example.fragment.Response.User;
import com.example.fragment.network.APIService;
import com.example.fragment.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class Login extends AppCompatActivity {

    EditText edtEmail,edtPassword;
    TextView txtRegister,txtRecoveryPW;
    Button btnLogin;
//    SignInButton btnSinginGG;
//    LoginButton loginButtonFB;
//    GoogleSignInClient mGoogleSignInClient;
//    CallbackManager callbackManager;
    int RC_SIGN_IN=1999;
    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail=(EditText)findViewById(R.id.edtEmail);
        edtPassword=(EditText)findViewById(R.id.edtPassword);
        btnLogin=(Button)findViewById(R.id.btnLogin);
        txtRecoveryPW=(TextView)findViewById(R.id.txtRecoveryPW);
        txtRegister=(TextView)findViewById(R.id.txtRegister);

        //btnSinginGG=(SignInButton)findViewById(R.id.sign_in_button);
        // login by API
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APIService apiService = RetrofitClient.getApiService();
                apiService.login(edtEmail.getText().toString(), edtPassword.getText().toString())
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                                if(response.isSuccessful()){
                                    final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                                    // set global Variable
                                    globalVariable.setAuthorization(response.body().getToken());
                                    globalVariable.setId(response.body().getId());
                                    Intent intent=new Intent(Login.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Tài khoản hoặc mật khẩu sai",Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                //TODO Xử lý lỗi
                                Toast.makeText(getApplicationContext(),t.getMessage().toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Register
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),Register.class);
                startActivity(intent);
                onPause();
            }
        });
        // Recovery
        txtRecoveryPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),Recovery.class);
                startActivity(intent);
                onPause();
            }
        });

    }
}
