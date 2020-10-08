package com.example.fragment.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fragment.ItemLoadmore;
import com.example.fragment.Model.GlobalClass;
import com.example.fragment.Model.ModelTour;
import com.example.fragment.MyAdapterTour;
import com.example.fragment.OnItemClickListener;
import com.example.fragment.R;
import com.example.fragment.Response.ListTourRes;
import com.example.fragment.Response.TourRes;
import com.example.fragment.TourInfor;
import com.example.fragment.network.APIService;
import com.example.fragment.network.RetrofitClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTourFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_tour,container,false);
    }
    private MyAdapterTour myAdapter;
    private ArrayList<ModelTour> models= new ArrayList<>();
    private int total;
    private int pageIndex=1;
    private int pageSize=10;
    RecyclerView mRecylerview;
    private String authorization;
    private final int MYTOUR=1;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final GlobalClass globalVariable = (GlobalClass) Objects.requireNonNull(getActivity()).getApplicationContext();
        authorization=globalVariable.getAuthorization();

        mRecylerview = (RecyclerView)view.findViewById(R.id.recylerviewMyTour);
        mRecylerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecylerview.setHasFixedSize(true);

        models=new ArrayList<>();
        getDataListTour(authorization,pageSize,pageIndex); // get data
        pageIndex++;

        myAdapter = new MyAdapterTour(mRecylerview,getActivity(), models,MYTOUR,authorization);
        mRecylerview.setAdapter(myAdapter);

        myAdapter.setClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent=new Intent(getActivity(), TourInfor.class);
                intent.putExtra("IdTour",models.get(position).getID());
                intent.putExtra("Activity",2);
                startActivity(intent);
                onPause();
            }
        });
        myAdapter.setLoadmore(new ItemLoadmore() {
            @Override
            public void onLoadMore() {
                if(models.size()<=total){
                    models.add(null);
                    myAdapter.notifyItemInserted(models.size()-1);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            models.remove(models.size()-1);
                            myAdapter.notifyItemRemoved(models.size());

                            getDataListTour(authorization,pageSize,pageIndex);
                            pageIndex++;

                        }
                    },1000);
                }else{
                    Toast.makeText(getContext(),"complete",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    // API ListMyTour
    private void getDataListTour(String authorization, int pageSize, final int pageIndex ){
        APIService apiService = RetrofitClient.getApiService();
        apiService.getListMyTour(authorization,pageSize,pageIndex).enqueue(new Callback<ListTourRes>() {
            @Override
            public void onResponse(Call<ListTourRes> call, final Response<ListTourRes> response) {
                if(response.isSuccessful()) {
                    total=response.body().getTotal();
                    models.addAll(getMylist(response.body().getTours()));

                    myAdapter.notifyDataSetChanged();
                    myAdapter.setLoaded();
                }
                else if(response.code()==500){
                    Toast.makeText(getContext(),response.message(),Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ListTourRes> call, Throwable t) {
                    Toast.makeText(getContext(),"Không có Internet",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private ArrayList<ModelTour> getMylist(List<TourRes> tourList) {

        ArrayList<ModelTour> models = new ArrayList<>();
        ModelTour model;
        Calendar calStart = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        Long startDate,endDate;
        for(int i=0;i<tourList.size();i++) {
            if(tourList.get(i).getStatus()==-1){
                continue;
            }
            model=new ModelTour();
            // getDatetime
            String date;
            try {
                date=getStringDate(tourList.get(i).getStartDate(),tourList.get(i).getEndDate());
            }catch (Exception e){
                date="null";
            }
            model.setDate(date);
            model.setID(tourList.get(i).getId());
            model.setName(tourList.get(i).getName());
            model.setType(tourList.get(i).getAdults()+" Adults- "+tourList.get(i).getChilds()+" Childs");
            model.setCost(tourList.get(i).getMinCost()+"-"+tourList.get(i).getMaxCost());
            models.add(model);
        }
        return models;
    }
    public String getStringDate(String longStartDate,String longEndDate){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calStart = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        calStart.setTimeInMillis(Long.parseLong(longStartDate));
        calEnd.setTimeInMillis(Long.parseLong(longEndDate));

        String StartDate=dateFormat.format(calStart.getTime());
        String StartEnd=dateFormat.format(calEnd.getTime());
        return StartDate+" - "+StartEnd;
    }

}
