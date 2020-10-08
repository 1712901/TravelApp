package com.example.fragment.Fragment;

import android.app.Dialog;
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
import com.example.fragment.Model.GlobalClass;
import com.example.fragment.OnItemClickListener;
import com.example.fragment.TourInfor;
import com.example.fragment.network.APIService;
import com.example.fragment.ItemLoadmore;
import com.example.fragment.Response.ListTourRes;
import com.example.fragment.Model.ModelTour;
import com.example.fragment.MyAdapterTour;
import com.example.fragment.R;
import com.example.fragment.network.RetrofitClient;
import com.example.fragment.Response.TourRes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LisTourFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_tour,container,false);
    }

    private MyAdapterTour myAdapter;
    private  ArrayList<ModelTour> models= new ArrayList<>();
    private int total;
    private int pageIndex=1;
    private int pageSize=10;
    private String authorization;
    private final int LISTTOUR=2;
    private  Dialog dialogLoading;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final GlobalClass globalVariable = (GlobalClass)getActivity().getApplicationContext();
        authorization=globalVariable.getAuthorization();

        RecyclerView mRecylerview;

        mRecylerview = (RecyclerView)view.findViewById(R.id.recylerview);
        mRecylerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecylerview.setHasFixedSize(true);
        models=new ArrayList<>();
        getDataListTour(authorization,pageSize,pageIndex); // get data
        pageIndex++;
        myAdapter = new MyAdapterTour(mRecylerview,getActivity(), models,LISTTOUR,authorization);
        mRecylerview.setAdapter(myAdapter);
        myAdapter.setClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent=new Intent(getActivity(), TourInfor.class);
                intent.putExtra("IdTour",models.get(position).getID());
                intent.putExtra("Activity",1);
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
               // loadMoreData();
            }
        });
    }
    // API ListTour
    private void getDataListTour(String authorization, int rowPerPage, final int pageNum ){
        APIService apiService = RetrofitClient.getApiService();
        apiService.getListTour(authorization,rowPerPage,pageNum).enqueue(new Callback<ListTourRes>() {
            @Override
            public void onResponse(Call<ListTourRes> call, final Response<ListTourRes> response) {
                if(response.isSuccessful()) {
                    total=response.body().getTotal();
//                    ArrayList<ModelTour> newdata=new ArrayList<>();
//                    newdata.addAll(getMylist(response.body().getTours()));
//                    if(newdata.size()==0){
//                        loadMoreData();
//                        return;
//                    }
                    models.addAll(getMylist(response.body().getTours()));

                    myAdapter.notifyDataSetChanged();
                    myAdapter.setLoaded();
                }
                else if(response.code()==500) {
                    Toast.makeText(getActivity(),response.message(),Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ListTourRes> call, Throwable t) {
                Toast.makeText(getActivity(),"Không có Internet",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private ArrayList<ModelTour> getMylist(List<TourRes> tourList) {
        ArrayList<ModelTour> models = new ArrayList<>();
        ModelTour model;
        int index=0;
        for(int i=0;i<tourList.size();i++) {
//            if(tourList.get(i).getName().length()==0){
//                continue;
//            }
            model=new ModelTour();
            // getDatetime
            model.setID(tourList.get(i).getId());
            model.setName(tourList.get(i).getName());
            model.setType(tourList.get(i).getAdults()+" Adults- "+tourList.get(i).getChilds()+" Childs");
            model.setCost(tourList.get(i).getMinCost()+"-"+tourList.get(i).getMaxCost());
            model.setImg(tourList.get(i).getAvatar());
            models.add(model);
        }
        return models;
    }
    public void loadMoreData(){
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
        Log.i("page",pageIndex+"");
    }
}
