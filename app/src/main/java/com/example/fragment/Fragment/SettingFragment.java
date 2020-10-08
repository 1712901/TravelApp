package com.example.fragment.Fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fragment.Login;
import com.example.fragment.Model.GlobalClass;
import com.example.fragment.R;
import com.example.fragment.Response.UserInforRes;
import com.example.fragment.TourInfor;
import com.example.fragment.network.APIService;
import com.example.fragment.network.RetrofitClient;
import com.example.fragment.updatePassword;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting,container,false);
    }

    private ImageButton imbtavatar,imbtUserCancel,imbtUserCheck,setting;
    private EditText edtUserName,edtUserEmail,edtUserPhone;
    private TextView edtHeaderName;
    private Button btnUserDob;
    private Spinner spnUserGender;
    private Integer gender;
    private String authorization;
    final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    final SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd");
    Date dob;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final GlobalClass globalVariable = (GlobalClass)getActivity().getApplicationContext();
        authorization=globalVariable.getAuthorization();
        init();
        getInfor(authorization);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.Gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUserGender.setAdapter(adapter);
        spnUserGender.setOnItemSelectedListener(this);


        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getContext(), view);
                MenuInflater inflater = popup.getMenuInflater();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.mnedit:
                                setVisibility(false);
                                setEnabled(true);
                                return true;

                            case R.id.mnLogout:
                                Intent intent1=new Intent(getActivity(), Login.class);
                                startActivity(intent1);
                                getActivity().finish();
                                return true;
                            case R.id.mnPw:
                                Intent intent=new Intent(getActivity(), updatePassword.class);
                                startActivity(intent);
                                return true;
                        }
                        return false;
                    }
                });
                inflater.inflate(R.menu.menu_setting, popup.getMenu());
                popup.show();
            }
        });

        btnUserDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                btnUserDob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                c.set(year, (monthOfYear), dayOfMonth);
                                dob=c.getTime();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        imbtUserCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIService apiService = RetrofitClient.getApiService();
               apiService.updateInfoUser(authorization,edtUserName.getText().toString(),edtUserEmail.getText().toString()
                       ,edtUserPhone.getText().toString(),gender,dob)
                       .enqueue(new Callback<JSONObject>() {
                           @Override
                           public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                               if(response.isSuccessful()){
                                   Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();
                                   setVisibility(true);
                                   setEnabled(false);
                               }else{
                                   Toast.makeText(getContext(),response.message(),Toast.LENGTH_SHORT).show();
                               }
                           }

                           @Override
                           public void onFailure(Call<JSONObject> call, Throwable t) {
                               Toast.makeText(getContext(),"Failed2",Toast.LENGTH_SHORT).show();
                           }
                       });

            }
        });
        imbtUserCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibility(true);
                setEnabled(false);
            }
        });
    }
    public void init(){
        this.imbtavatar=(ImageButton)getActivity().findViewById(R.id.imbtavatar);
        this.imbtUserCancel=(ImageButton)getActivity().findViewById(R.id.imbtUserCancel);
        this.imbtUserCheck=(ImageButton)getActivity().findViewById(R.id.imbtUserCheck);
        this.setting=(ImageButton)getActivity().findViewById(R.id.setting);


        this.edtUserName=(EditText)getActivity().findViewById(R.id.edtUserName);
        this.edtUserEmail=(EditText)getActivity().findViewById(R.id.edtUserEmail);
        this.edtUserPhone=(EditText)getActivity().findViewById(R.id.edtUserPhone);
        this.edtHeaderName=(TextView)getActivity().findViewById(R.id.edtHeaderName);

        this.spnUserGender = (Spinner)getActivity().findViewById(R.id.spnUserGender);

        this.btnUserDob=(Button) getActivity().findViewById(R.id.btnUserDob);

        setVisibility(true);
        setEnabled(false);
    }
    public void setVisibility(boolean bl){
        if(bl) {
            this.imbtUserCheck.setVisibility(View.INVISIBLE);
            this.imbtUserCancel.setVisibility(View.INVISIBLE);
        }
        else {
            this.imbtUserCheck.setVisibility(View.VISIBLE);
            this.imbtUserCancel.setVisibility(View.VISIBLE);
        }
    }
    public void setEnabled(boolean bl){
        this.edtUserName.setEnabled(bl);
        this.edtUserEmail.setEnabled(bl);
        this.edtUserPhone.setEnabled(bl);
        this.btnUserDob.setEnabled(bl);
        this.spnUserGender.setEnabled(bl);
    }
    public  void setDisplay(UserInforRes infor){
        this.edtHeaderName.setText(infor.getFullName());
        this.edtUserName.setText(infor.getFullName());

        this.edtUserEmail.setText(infor.getEmail());
        this.edtUserPhone.setText(infor.getPhone());
        if(infor.getGender()==null){
            this.spnUserGender.setSelection(1);
            gender=1;
        }else {
            gender=infor.getGender();
            this.spnUserGender.setSelection(gender);
        }
        try{
            dob = inputFormat.parse(infor.getDob());
            this.btnUserDob.setText(outputFormat.format(dob));
        }catch (Exception e) {

        }
    }
    public void getInfor(String authorization){
        APIService apiService = RetrofitClient.getApiService();
        apiService.getUserInfor(authorization).enqueue(new Callback<UserInforRes>() {
            @Override
            public void onResponse(Call<UserInforRes> call, Response<UserInforRes> response) {
                if(response.isSuccessful()){
                    setDisplay(response.body());
                }else {
                    Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserInforRes> call, Throwable t) {
                Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        gender=position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}
