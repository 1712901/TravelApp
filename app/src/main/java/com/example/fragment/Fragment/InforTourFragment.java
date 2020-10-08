package com.example.fragment.Fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fragment.AddStopPoint;
import com.example.fragment.Model.GlobalClass;
import com.example.fragment.Model.ModelMember;
import com.example.fragment.Model.ModelStopPoint;
import com.example.fragment.Model.StopPoint;
import com.example.fragment.MyAdapterMember;
import com.example.fragment.MyAdapterStopPoint;
import com.example.fragment.OnItemClickListener;
import com.example.fragment.R;
import com.example.fragment.Response.InforTourRes;
import com.example.fragment.Response.Member;
import com.example.fragment.TourInfor;
import com.example.fragment.network.APIService;
import com.example.fragment.network.RetrofitClient;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InforTourFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_general_inf,container,false);
    }

    private EditText edInfTourName,edMInfinCost,edInfMaxCost;
    private RadioButton rdInfIsPrivate;
    private Button btnInfStartDate,btnInfEndDate;
    private Date InfstartDate,InfendDate;
    private RecyclerView mRecylerview,mRecylMenber;
    private ImageButton imbtAddStopPoint,imbtAddMember,imbtInfEdit,imbtInfCancel,imbtInfCheck;
    private ArrayList<ModelStopPoint> models;
    private ArrayList<ModelMember> modelMembers;
    private MyAdapterStopPoint myAdapter;
    private MyAdapterMember myAdapterMB;

    final SimpleDateFormat inputFormat = new SimpleDateFormat("ssssssssssSSS");
    final SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    final SimpleDateFormat newdate=new SimpleDateFormat("dd/MM/yyy");
    private String authorization;
    int tourId;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        final GlobalClass globalVariable = (GlobalClass)getActivity().getApplicationContext();
        authorization=globalVariable.getAuthorization();
        tourId=getActivity().getIntent().getIntExtra("IdTour",-1);
        int type=getActivity().getIntent().getIntExtra("Activity",-1);;
        init(view);
        // Kiểm tra activity nào đã gọi
        if(type!=2){
            imbtAddStopPoint.setVisibility(View.INVISIBLE);
            imbtInfEdit.setVisibility(View.INVISIBLE);
            imbtAddMember.setVisibility(View.INVISIBLE);
        }
        getReqInfor(authorization,tourId);
        /// stop point
        mRecylerview.setHasFixedSize(true);
        mRecylerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        models=new ArrayList<>();
        //Members
        mRecylMenber.setHasFixedSize(true);
        mRecylMenber.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        modelMembers=new ArrayList<>();

        imbtAddStopPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), AddStopPoint.class);
                intent.putExtra("IDTour",tourId);
                startActivity(intent);
                onPause();
            }
        });


        imbtInfEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEnabled(true);
                imbtInfEdit.setVisibility(View.INVISIBLE);
                imbtInfCheck.setVisibility(View.VISIBLE);
                imbtInfCancel.setVisibility(View.VISIBLE);
            }
        });
        rdInfIsPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rdInfIsPrivate.isSelected()){
                    rdInfIsPrivate.setChecked(false);
                    rdInfIsPrivate.setSelected(false);
                }else{
                    rdInfIsPrivate.setChecked(true);
                    rdInfIsPrivate.setSelected(true);
                }
            }
        });
        btnInfStartDate.setOnClickListener(new View.OnClickListener() {
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

                                btnInfStartDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                c.set(year, (monthOfYear), dayOfMonth);
                                InfstartDate=c.getTime();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        btnInfEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                btnInfEndDate.setText(dayOfMonth + "-" + (monthOfYear +1) + "-" + year);
                                c.set(year, (monthOfYear), dayOfMonth);
                                InfendDate=c.getTime();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        imbtInfCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imbtInfCheck.setVisibility(View.INVISIBLE);
                imbtInfCancel.setVisibility(View.INVISIBLE);
                imbtInfEdit.setVisibility(View.VISIBLE);
                setEnabled(false);
            }
        });
        imbtInfCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIService apiService = RetrofitClient.getApiService();
                apiService.updateInfTour(authorization,tourId,edInfTourName.getText().toString(),InfstartDate.getTime(),InfendDate.getTime()
                        ,Integer.parseInt(edMInfinCost.getText().toString()),Integer.parseInt(edInfMaxCost.getText().toString()),rdInfIsPrivate.isChecked())
                        .enqueue(new Callback<JSONObject>() {
                            @Override
                            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                                imbtInfCheck.setVisibility(View.INVISIBLE);
                                imbtInfCancel.setVisibility(View.INVISIBLE);
                                imbtInfEdit.setVisibility(View.VISIBLE);
                                Toast.makeText(getActivity(),"Cập nhật Thành Công",Toast.LENGTH_SHORT).show();
                                setEnabled(false);
                            }
                            @Override
                            public void onFailure(Call<JSONObject> call, Throwable t) {
                                Toast.makeText(getActivity(),"Cập nhật thất bại",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
    public void init(View view){
        this.edInfTourName=(EditText)view.findViewById(R.id.edtInfTourName);
        this.edMInfinCost=(EditText)view.findViewById(R.id.edInfMinCost);
        this.edInfMaxCost=(EditText)view.findViewById(R.id.edInfMaxCost);
        this.btnInfStartDate=(Button) view.findViewById(R.id.btnInfStartDate);
        this.btnInfEndDate=(Button) view.findViewById(R.id.btnInfEndDate);
        this.rdInfIsPrivate=(RadioButton) view.findViewById(R.id.rdInfIsPrivate);
        this.imbtAddStopPoint=(ImageButton)view.findViewById(R.id.imbtAddStopPoint);
        this.imbtAddMember=(ImageButton)view.findViewById(R.id.imbtAddMenber);
        this.imbtInfEdit=(ImageButton)view.findViewById(R.id.imbtInfEdit);
        this.imbtInfCheck=(ImageButton)view.findViewById(R.id.imbtInfCheck);
        this.imbtInfCancel=(ImageButton)view.findViewById(R.id.imbtInfCancel);
        this.mRecylerview = (RecyclerView)view.findViewById(R.id.rcylStopPoint);
        this.mRecylMenber=(RecyclerView)view.findViewById(R.id.rcylMember);
        this.imbtInfCheck.setVisibility(View.INVISIBLE);
        this.imbtInfCancel.setVisibility(View.INVISIBLE);
    }
    public void getReqInfor(final String authorization, final int tourId){
        APIService apiService = RetrofitClient.getApiService();
        apiService.getTourInfo(authorization,tourId)
                .enqueue(new Callback<InforTourRes>() {
                    @Override
                    public void onResponse(Call<InforTourRes> call, final Response<InforTourRes> response) {
                        if (response.isSuccessful()){
                            try {
                                getInfor(response.body());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getContext(),"Thành Công",Toast.LENGTH_SHORT).show();

                            try {
                                models.addAll(getMylistStopPoint(response.body().getStopPoints()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            myAdapter = new MyAdapterStopPoint(getActivity(), models,authorization);
                            mRecylerview.setAdapter(myAdapter);
                            mRecylerview.setItemAnimator(new DefaultItemAnimator());
                            //  Update stopPoint
                            myAdapter.setClickListener(new OnItemClickListener() {
                                @Override
                                public void onClick(View view, int position) {
                                    // send stop point
                                    Intent intent=new Intent(getActivity(),AddStopPoint.class);
                                    intent.putExtra("IDTour",tourId);
                                    intent.putExtra("StopPointInf",  response.body().getStopPoints().get(position));
                                    startActivity(intent);
                                    onPause();
                                }
                            });
                            myAdapter.notifyDataSetChanged();
                            modelMembers.addAll(getMylistMember(response.body().getMembers()));
                            myAdapterMB = new MyAdapterMember(getActivity(), modelMembers);
                            mRecylMenber.setAdapter(myAdapterMB);
                            mRecylMenber.setItemAnimator(new DefaultItemAnimator());
                            myAdapterMB.notifyDataSetChanged();
                        }
                        else{
                            Toast.makeText(getActivity(),"Thất Bại",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<InforTourRes> call, Throwable t) {
                        Toast.makeText(getActivity(),"No Internet",Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void getInfor(InforTourRes infor) throws ParseException {
        this.edInfTourName.setText(infor.getName());
        this.edMInfinCost.setText(infor.getMinCost());
        this.edInfMaxCost.setText(infor.getMaxCost());

        this.btnInfStartDate.setText(parseIntToString(infor.getStartDate()));
        try {
            InfstartDate=inputFormat.parse(infor.getStartDate());
        }catch (Exception e){
            InfstartDate=null;
        }
        this.btnInfEndDate.setText(parseIntToString(infor.getEndDate()));
        try {
            InfendDate=inputFormat.parse(infor.getStartDate());
        }catch (Exception e){
            InfendDate=null;
        }
        try {
            this.rdInfIsPrivate.setChecked(infor.getIsPrivate());
        }catch (Exception e){
            this.rdInfIsPrivate.setChecked(false);
        }
        setEnabled(false);
    }
    public void setEnabled(boolean bl){
        this.edInfTourName.setEnabled(bl);
        this.edMInfinCost.setEnabled(bl);
        this.edInfMaxCost.setEnabled(bl);
        this.btnInfStartDate.setEnabled(bl);
        this.btnInfEndDate.setEnabled(bl);
        this.rdInfIsPrivate.setEnabled(bl);
    }
    private ArrayList<ModelStopPoint> getMylistStopPoint(List<StopPoint> stopPointList) throws ParseException {// set model
        ArrayList<ModelStopPoint> models = new ArrayList<>();
        ModelStopPoint model;
        Date d;
        for(int i=0;i<stopPointList.size();i++) {
//            inputFormat.parse();
            model=new ModelStopPoint();
            // getDatetime
            model.setImg(stopPointList.get(i).getAvatar());
            model.setID(stopPointList.get(i).getId());
            model.setName(stopPointList.get(i).getName());
            model.setAdress(stopPointList.get(i).getAddress());
            model.setTimeArr(getTimeDate(stopPointList.get(i).getArrivalAt()));
            model.setTimeLeave(getTimeDate(stopPointList.get(i).getLeaveAt()));
            model.setCost(stopPointList.get(i).getMinCost()+"-"+stopPointList.get(i).getMaxCost());
            models.add(model);
        }
        return models;
    }
    public String parseIntToString(String dateInt) throws ParseException {
        String date="";
        try {
            Date d=inputFormat.parse(dateInt);
            date=newdate.format(d);
        }catch (Exception e){
            return "Null";
        }
        return date;
    }
    public String getTimeDate(long dateLong){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(dateLong);

        return getTime(calendar)+" "+getDate(calendar);
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
    private ArrayList<ModelMember> getMylistMember(List<Member> memberList){
        ArrayList<ModelMember> modelMembers=new ArrayList<>();
        ModelMember modelMB;
        for (int i=0;i<memberList.size();i++){
            modelMB=new ModelMember();
            modelMB.setImg(memberList.get(i).getAvatar());//Lấy ảnh
            modelMB.setName(memberList.get(i).getName());
            modelMB.setPhoneNumber(memberList.get(i).getPhone());

            modelMembers.add(modelMB);
        }
        return modelMembers;
    }
}
