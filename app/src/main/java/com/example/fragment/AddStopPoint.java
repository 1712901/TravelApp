package com.example.fragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.fragment.Model.GlobalClass;
import com.example.fragment.Model.ListStopPoints;
import com.example.fragment.Model.StopPoint;
import com.example.fragment.network.APIService;
import com.example.fragment.network.RetrofitClient;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.AdapterView.*;

public class AddStopPoint extends AppCompatActivity implements OnItemSelectedListener{


    EditText edtStopName,edtMinCost,edtMaxCost;
    Button btnAdress,btnTimeArrive,btnDateArrive,btnTimeLeave,btnDateLeave,btnAdd,btnUpdate;
    ImageButton btnCancel;
    AutoCompleteTextView attProvince;
    Spinner spnType;
    int serviceType=4;
    private final int REQUESTCODE=111;
    private double latAdress,longAdress;
    private long timeArrive,timeLeave;
    private String descripAdrr;
    private String idTour="2711";
    private String idStopPoint="";
    private static final String[] PROVINCES = new String[] {
            "Hồ Chí Minh", "Hà Nội", "Đà Nẵng", "Bình Dương", "Đồng Nai",
            "Khánh Hòa","Hải Phòng","Long An","Quảng Nam","Bà Rịa Vũng Tàu",
            "Đắk Lắk","Cần Thơ","Bình Thuận","Lâm Đồng","Thừa Thiên Huế",
            "Kiên Giang","Bắc Ninh","Quảng Ninh","Thanh Hóa","Nghệ An",
            "Hải Dương","Hải Dương","Bình Phước","Hưng Yên","Bình Định",
            "Tiền Giang", "Thái Bình","Bắc Giang","Hòa Bình","An Giang",
            "Vĩnh Phúc","Tây Ninh","Thái Nguyên","Lào Cai","Nam Định",
            "Quảng Ngãi","Bến Tre", "Đắk Nông","Cà Mau","Vĩnh Long",
            "Ninh Bình" , "Phú Thọ","Ninh Thuận","Phú Yên","Hà Nam",
            "Hà Tĩnh","Đồng Tháp","Sóc Trăng","Kon Tum","Quảng Bình",
            "Quảng Trị","Trà Vinh","Hậu Giang","Sơn La", "Bạc Liêu",
            "Yên Bái","Tuyên Quang","Điện Biên","Lai Châu" ,"Lạng Sơn",
            "Hà Giang","Bắc Kạn","Cao Bằng"
    };
    private String authorization;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stop_point);

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        authorization=globalVariable.getAuthorization();

        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        init();
        ArrayAdapter<String> adapterAutoText = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, PROVINCES);
        attProvince = (AutoCompleteTextView) findViewById(R.id.attProvince);
        attProvince.setThreshold(1);
        attProvince.setAdapter(adapterAutoText);

        spnType = (Spinner) findViewById(R.id.spnServiceType);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(adapter);
        spnType.setOnItemSelectedListener(this);

        // nhận dữ liệu
        Intent intent = getIntent();
        int id=intent.getIntExtra("IDTour",-1);
        idTour=String.valueOf(id);
        final StopPoint stopPointIntent =(StopPoint)intent.getSerializableExtra("StopPointInf");
        if(stopPointIntent!=null) {
            displayInfor(stopPointIntent);
        }

        btnAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAdress.setBackgroundResource(R.drawable.btn_input);
                Intent intent=new Intent(AddStopPoint.this,Map.class);
                startActivityForResult(intent,REQUESTCODE);
            }
        });
        btnDateArrive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDateArrive.setBackgroundResource(R.drawable.btn_input);
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddStopPoint.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                btnDateArrive.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                c.set(year, (monthOfYear), dayOfMonth);
                                Log.d("date",btnDateArrive.getText().toString());
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        btnTimeArrive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTimeArrive.setBackgroundResource(R.drawable.btn_input);
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR);
                int mMinute = c.get(Calendar.MINUTE);
                final TimePickerDialog timePickerDialog = new TimePickerDialog(AddStopPoint.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                btnTimeArrive.setText(hourOfDay + ":" + minute+":00");
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        btnDateLeave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDateLeave.setBackgroundResource(R.drawable.btn_input);
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddStopPoint.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                btnDateLeave.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                c.set(year, (monthOfYear), dayOfMonth);
                                Log.d("date",btnDateArrive.getText().toString());
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        btnTimeLeave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTimeLeave.setBackgroundResource(R.drawable.btn_input);
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR);
                int mMinute = c.get(Calendar.MINUTE);
                final TimePickerDialog timePickerDialog = new TimePickerDialog(AddStopPoint.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                btnTimeLeave.setText(hourOfDay + ":" + minute+":00");
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(AddStopPoint.this,MainActivity.class);
//                startActivity(intent);
                finish();
            }
        });
        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int idProvince=-1;
                idProvince=Arrays.asList(PROVINCES).indexOf(attProvince.getText().toString().trim());
                if(!isEmptyBtn(btnAdress,btnTimeArrive,btnDateArrive,btnTimeLeave,btnDateLeave)|!isEmpty(edtStopName,edtMinCost,edtMaxCost,idProvince))
                    return;
                try {
                    Date   dateArrive       = format.parse ( btnDateArrive.getText()+" "+btnTimeArrive.getText() );
                    Date   dateLeave       = format.parse ( btnDateLeave.getText()+" "+btnTimeLeave.getText() );
                    if(dateArrive.getTime()>dateLeave.getTime()){
                        throw new ParseException("Thơi gian không hợp lệ",0);
                    }
                    ArrayList<StopPoint> stopPoints=new ArrayList<>();
                    StopPoint stopPoint=new StopPoint(edtStopName.getText().toString(),btnAdress.getText().toString(),latAdress,
                            longAdress,dateArrive.getTime(),dateLeave.getTime(),serviceType,idProvince,edtMinCost.getText().toString(),edtMaxCost.getText().toString());
                    stopPoints.add(stopPoint);

                    ListStopPoints listStopPoints=new ListStopPoints();
                    listStopPoints.setTourId(idTour);
                    listStopPoints.setStopPoints(stopPoints);

                    APIService apiService = RetrofitClient.getApiService();
                    apiService.addStopPoint(authorization,listStopPoints)
                            .enqueue(new Callback<JSONObject>() {
                                @Override
                                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                                    if(response.code()==200){
                                        ShowDialog();
                                    }else if(response.code()==500) {
                                        Toast.makeText(getApplicationContext(),response.message(),Toast.LENGTH_LONG).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<JSONObject> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_LONG).show();
                                }
                            });
                } catch (ParseException e) {
                    Toast.makeText(getApplicationContext(),"Thời gian không hợp lệ",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });
        btnUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int idProvince=-1;
                idProvince=Arrays.asList(PROVINCES).indexOf(attProvince.getText().toString().trim());
                if(!isEmptyBtn(btnAdress,btnTimeArrive,btnDateArrive,btnTimeLeave,btnDateLeave)|!isEmpty(edtStopName,edtMinCost,edtMaxCost,idProvince))
                    return;
                Long minCost,maxCost;
                minCost=Long.parseLong(edtMinCost.getText().toString());
                maxCost=Long.parseLong(edtMinCost.getText().toString());
                if(minCost>maxCost){
                    Toast.makeText(AddStopPoint.this,"Giá không hợp lệ",Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Date   dateArrive       = format.parse ( btnDateArrive.getText()+" "+btnTimeArrive.getText() );
                    Date   dateLeave       = format.parse ( btnDateLeave.getText()+" "+btnTimeLeave.getText() );
                    if(dateArrive.getTime()>dateLeave.getTime()){
                        throw new ParseException("Thơi gian không hợp lệ",0);
                    }
                    ArrayList<StopPoint> stopPoints=new ArrayList<>();

                    StopPoint stopPoint=new StopPoint(edtStopName.getText().toString(),btnAdress.getText().toString(),latAdress,
                            longAdress,dateArrive.getTime(),dateLeave.getTime(),serviceType,idProvince,edtMinCost.getText().toString(),edtMaxCost.getText().toString());
                    stopPoint.setId( stopPointIntent.getId());
                    stopPoints.add(stopPoint);
                    ListStopPoints listStopPoints=new ListStopPoints();
                    listStopPoints.setTourId(idTour);
                    listStopPoints.setStopPoints(stopPoints);

                    APIService apiService = RetrofitClient.getApiService();
                    apiService.updateStopPoint(authorization,listStopPoints)
                            .enqueue(new Callback<JSONObject>() {
                                @Override
                                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                                    if(response.isSuccessful()){
                                        Toast.makeText(AddStopPoint.this,"Update Seccess",Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(AddStopPoint.this,"Không thể update",Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<JSONObject> call, Throwable t) {
                                    Toast.makeText(AddStopPoint.this,"Không thể update",Toast.LENGTH_LONG).show();
                                }
                            });
                } catch (ParseException e) {
                    Toast.makeText(getApplicationContext(),"Thời gian không hợp lệ",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }


            }
        });
    }
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        Log.i("Select",parent.getItemAtPosition(pos)+"  "+(pos+1));
        serviceType=pos+1;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUESTCODE:
                if(1==resultCode) {
                    latAdress=data.getDoubleExtra("Latitude",0);
                    longAdress=data.getDoubleExtra("Longitude",0);
                    descripAdrr=data.getStringExtra("Descrip");
                    btnAdress.setText(descripAdrr);
                }
                break;

        }
    }
    public  Boolean isEmpty(EditText name,EditText minCost,EditText maxCode ,int Autotext){
        Boolean index=true;
        if( name.getText().toString().length() == 0 ) {
            name.setError("required!");
            index= false;
        }
        if( minCost.getText().toString().length() == 0 ) {
            minCost.setError("required!");
            index= false;
        }
        if( maxCode.getText().toString().length() == 0 ) {
            maxCode.setError("required!");
            index= false;
        }
        if(Autotext<0){
            attProvince.setError("required!");
            index= false;
        }
        return index;
    }
    public  Boolean isEmptyBtn(Button btnAdress ,Button btnTimeArrive ,Button btnDateArrive ,Button btnTimeLeave ,Button btnDateLeave){
        Boolean index=true;
        if( btnAdress.getText().toString().length() == 0|btnAdress.getText().toString().equals("Select") ) {
            btnAdress.setBackgroundResource(R.drawable.btn_error);
            index= false;
        }
        if( btnTimeArrive.getText().toString().length() == 0 |btnTimeArrive.getText().toString().equals("Time")) {
            btnTimeArrive.setBackgroundResource(R.drawable.btn_error);
            index= false;
        }
        if( btnDateArrive.getText().toString().length() == 0| btnDateArrive.getText().toString().equals("Date") ) {
            btnDateArrive.setBackgroundResource(R.drawable.btn_error);
            index= false;
        }
        if( btnTimeLeave.getText().toString().length() == 0 |btnTimeLeave.getText().toString().equals("Time") ) {
            btnTimeLeave.setBackgroundResource(R.drawable.btn_error);
            index= false;
        }
        if( btnDateLeave.getText().toString().length() == 0|btnDateLeave.getText().toString().equals("Date") ) {
            btnDateLeave.setBackgroundResource(R.drawable.btn_error);
            index= false;
        }

        return index;
    }
    public void ShowDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Đã thêm điểm dừng Thành Công")
                .setMessage("Bạn có muốn thêm điểm dừng khác!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        resetInfor();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent =new Intent(AddStopPoint.this,MainActivity.class);
//                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
    public void init(){
        edtStopName=(EditText)findViewById(R.id.edtStopPoint);
        edtMinCost=(EditText)findViewById(R.id.edMinCost);
        edtMaxCost=(EditText)findViewById(R.id.edMaxCost);

        btnAdress=(Button)findViewById(R.id.btnAdress);

        btnTimeArrive=(Button)findViewById(R.id.btnTimeArrive);
        btnDateArrive=(Button)findViewById(R.id.btnDateArrive);
        btnTimeLeave=(Button)findViewById(R.id.btnTimeLeave);
        btnDateLeave=(Button)findViewById(R.id.btnDateLeave);
        btnAdd=(Button)findViewById(R.id.btnAddStopPoint);
        btnCancel=(ImageButton)findViewById(R.id.btnCancel) ;
        btnUpdate=(Button)findViewById(R.id.btnUpdateStopPoint);

        btnUpdate.setVisibility(INVISIBLE);

    }
    public void displayInfor(StopPoint stopPoint){
        this.idStopPoint=stopPoint.getId()+"";
        this.edtStopName.setText(stopPoint.getName());
        this.edtMinCost.setText(stopPoint.getMinCost());
        this.edtMaxCost.setText(stopPoint.getMaxCost());
        this.timeArrive=stopPoint.getArrivalAt();
        this.timeLeave=stopPoint.getLeaveAt();
        this.attProvince.setText(PROVINCES[stopPoint.getProvinceId()]);
        this.spnType.setSelection(stopPoint.getServiceTypeId()-1);
        this.latAdress=stopPoint.getLat();
        this.longAdress=stopPoint.getLongitude();
        Geocoder geocoder=new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        String address="";
        try {
            addresses=geocoder.getFromLocation(this.latAdress,this.longAdress,1);
            if(addresses.size()>0){
                address=addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.btnAdress.setText(address);
        btnUpdate.setVisibility(VISIBLE);
        btnAdd.setVisibility(INVISIBLE);
        Calendar calendar=Calendar.getInstance();

        calendar.setTimeInMillis(stopPoint.getArrivalAt());
        this.btnTimeArrive.setText(getTime(calendar));
        this.btnDateArrive.setText(getDate(calendar));

        calendar.setTimeInMillis(stopPoint.getLeaveAt());
        this.btnTimeLeave.setText(getTime(calendar));
        this.btnDateLeave.setText(getDate(calendar));

    }
    public String getTime(Calendar c){
        return formatTime(c.get(Calendar.HOUR_OF_DAY)+"")+":"+formatTime(c.get(Calendar.MINUTE)+"")+":"+formatTime(c.get(Calendar.SECOND)+"");
    }
    public String getDate(Calendar c){
        return c.get(Calendar.DATE)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR);
    }
    public String formatTime(String s){
        return s.length()==1?0+s:s;
    }
    public void resetInfor(){
        this.edtStopName.setText("");
        this.btnAdress.setText("");
        this.btnTimeArrive.setText("Time");
        this.btnDateArrive.setText("Date");
        this.btnTimeLeave.setText("Time");
        this.btnDateLeave.setText("Date");
        this.edtMinCost.setText("");
        this.edtMaxCost.setText("");
        this.longAdress=this.longAdress=0;
    }
}
