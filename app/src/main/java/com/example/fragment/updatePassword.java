package com.example.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fragment.Fragment.SettingFragment;
import com.example.fragment.Model.GlobalClass;
import com.example.fragment.network.APIService;
import com.example.fragment.network.RetrofitClient;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class updatePassword extends AppCompatActivity implements View.OnTouchListener {

    private EditText edtCurrent,edtNew,edtConfi;
    private TextView tvBack;
    private Button setPW;
    private String authorization="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIzNzgiLCJwaG9uZSI6IjAx" +
                                "MjEyMTIxMjEiLCJlbWFpbCI6ImVtYWlsdGVzdEBnbWFpbC5jb20iLCJleHAiOjE1NzYzODM1NzY3MjEsImFjY291bnQ" +
                                "iOiJ1c2VyIiwiaWF0IjoxNTczNzkxNTc2fQ.xSzTWhlRJa6THvqtgzlfP0idhnFVhTXMHs0EXYkPO_g";;
    private int idUser;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        init();

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        idUser=Integer.parseInt(globalVariable.getId());
        authorization=globalVariable.getAuthorization();


        edtCurrent.setOnTouchListener(this);
        edtNew.setOnTouchListener(this);
        edtConfi.setOnTouchListener(this);
        tvBack.setOnTouchListener(this);

        setPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag=true;
                String pwNew,pwCurrent,pwCnfir;
                pwNew=edtNew.getText().toString();
                pwCurrent=edtCurrent.getText().toString();
                pwCnfir=edtConfi.getText().toString();
                if(pwNew.isEmpty()){
                    edtNew.setError("required");
                    flag=false;
                }
                if(pwCnfir.isEmpty()){
                    edtConfi.setError("required");
                    flag=false;
                }
                if(pwCurrent.isEmpty()){
                    edtCurrent.setError("required");
                    flag=false;
                }
                if(!pwNew.equals(pwCnfir)&&flag){
                    edtConfi.setError("Don't math");
                    flag=false;
                }
                if(flag){
                    APIService apiService= RetrofitClient.getApiService();
                    apiService.updatePassword(authorization,idUser,pwCurrent,pwNew)
                            .enqueue(new Callback<JSONObject>() {
                                @Override
                                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                                    if(response.isSuccessful()){
                                        Toast.makeText(updatePassword.this,"Success",Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(updatePassword.this,response.message(),Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<JSONObject> call, Throwable t) {
                                    Toast.makeText(updatePassword.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
    private void init(){
        this.edtCurrent=(EditText)findViewById(R.id.edtCurrent);
        this.edtNew=(EditText)findViewById(R.id.edtNew);
        this.edtConfi=(EditText)findViewById(R.id.edtConfi);
        this.tvBack=(TextView)findViewById(R.id.tvBack);
        this.setPW=(Button)findViewById(R.id.setPW);
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;
        switch (view.getId()){
            case R.id.edtCurrent :
            case R.id.edtNew :
            case R.id.edtConfi :
                EditText ed=(EditText)view;
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if(motionEvent.getRawX() >= (ed.getRight() - ed.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        return true;
                    }
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (ed.getRight() - ed.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        if(ed.getInputType()==(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                            ed.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                        }else{
                            ed.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                        return true;
                    }
                }
                return false;
            case R.id.tvBack:
                TextView tv=(TextView) view;
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    return true;
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getX() <= tv.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width() + (2 * tv.getPaddingStart())) {
                        if(findViewById(R.id.fragment_container)!=null){
                            SettingFragment settingFragment=new SettingFragment();
                            settingFragment.setArguments(getIntent().getExtras());

                            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container,settingFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                        finish();
                        return true;
                    }
                }
                return false;
        }
        return false;
    }
}
