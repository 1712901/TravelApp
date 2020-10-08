package com.example.fragment.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fragment.ItemLoadmore;
import com.example.fragment.Login;
import com.example.fragment.Model.GlobalClass;
import com.example.fragment.Model.ModelReview;
import com.example.fragment.MyAdapteReview;
import com.example.fragment.R;
import com.example.fragment.Response.Comment;
import com.example.fragment.Response.ListCommetRes;
import com.example.fragment.network.APIService;
import com.example.fragment.network.RetrofitClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsTourFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reviews,container,false);
    }
    private ImageButton imbAdd;
    private EditText edtComment;
    private RecyclerView rclComment;
    private ArrayList<ModelReview> modelReviews;
    private MyAdapteReview myAdapteReview;
    private int idTour;
    private String idUser;
    private String authorization;
    private int pageSize=5;
    private int pageIndex=1;
    private int sizeLoad;
    private Dialog myDialog;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imbAdd=(ImageButton)view.findViewById(R.id.imbAdd);
        rclComment=(RecyclerView)view.findViewById(R.id.rclComment);
        rclComment.setHasFixedSize(true);
        rclComment.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        modelReviews=new ArrayList<>();

        final GlobalClass globalVariable = (GlobalClass)getActivity().getApplicationContext();
        authorization=globalVariable.getAuthorization();
        idUser=globalVariable.getId();
        idTour=getActivity().getIntent().getIntExtra("IdTour",-1);
        getCommentToSever(authorization,idTour,pageIndex,pageSize);
        pageIndex++;

        myDialog=new Dialog(getContext());
        myAdapteReview=new MyAdapteReview(rclComment,getActivity(),modelReviews);
        rclComment.setAdapter(myAdapteReview);

        myAdapteReview.setLoadmore(new ItemLoadmore() {
            @Override
            public void onLoadMore() {
                if(sizeLoad>0){
                    modelReviews.add(null);
                    myAdapteReview.notifyItemInserted(modelReviews.size()-1);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            modelReviews.remove(modelReviews.size()-1);
                            myAdapteReview.notifyItemRemoved(modelReviews.size());

                            getCommentToSever(authorization,idTour,pageIndex,pageSize);
                            pageIndex++;

                        }
                    },1000);
                }else{
                    Toast.makeText(getContext(),"complete",Toast.LENGTH_SHORT).show();
                }
            }
        });
        imbAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText editText;
                ImageButton imbtClose;
                Button btnSend;
                final RatingBar ratingBar;
                myDialog.setContentView(R.layout.layout_comment);
                editText=(EditText)myDialog.findViewById(R.id.edtCommnet);
                imbtClose=(ImageButton)myDialog.findViewById(R.id.imbCMClose);
                btnSend=(Button)myDialog.findViewById(R.id.btnSendComment);
                ratingBar=(RatingBar)myDialog.findViewById(R.id.rtbComment);
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratBar, float rating, boolean b) {
                        if((int)rating<2){
                            ratingBar.setRating(2);
                        }
                    }
                });
                imbtClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myDialog.dismiss();
                    }
                });
                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String comment=editText.getText().toString().trim();
                        if(comment.length()==0){
                            return;
                        }
                        else {
                            APIService apiService=RetrofitClient.getApiService();
                            apiService.sendComment(authorization,idTour,(int)ratingBar.getRating(),comment)
                                    .enqueue(new Callback<JSONObject>() {
                                        @Override
                                        public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                                            if(response.isSuccessful()){
                                                myDialog.dismiss();
                                                Toast.makeText(getContext(),"success",Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(getContext(),response.message(),Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<JSONObject> call, Throwable t) {
                                            Toast.makeText(getContext(),"Fail",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });

                myDialog.show();
            }
        });
    }
    public void getCommentToSever(String authorization,int idTour,int pageIndex,int pageSize){

        APIService apiService= RetrofitClient.getApiService();
        apiService.getListComment(authorization,idTour,pageIndex,pageSize)
                .enqueue(new Callback<ListCommetRes>() {
                    @Override
                    public void onResponse(Call<ListCommetRes> call, Response<ListCommetRes> response) {
                        if(response.isSuccessful()){
                            sizeLoad=response.body().getReviewList().size();
                            modelReviews.addAll(getModelReviews(response.body().getReviewList()));
//                            myAdapteReview=new MyAdapteReview(rclComment,getActivity(),modelReviews);
//
//                            rclComment.setAdapter(myAdapteReview);
                            myAdapteReview.notifyDataSetChanged();
                            myAdapteReview.setLoaded();
                        }
                    }
                    @Override
                    public void onFailure(Call<ListCommetRes> call, Throwable t) {

                    }
                });

    }
    public ArrayList<ModelReview> getModelReviews(List<Comment> list){
        ArrayList<ModelReview> modelReviews=new ArrayList<>();
        ModelReview modelReview;

        for (int i=0;i<list.size();i++){
            modelReview=new ModelReview();
            modelReview.setName(list.get(i).getName());
            modelReview.setAvatar(list.get(i).getAvatar());
            modelReview.setComment(list.get(i).getComment());
            modelReview.setDate(getTimeDate(Long.parseLong(list.get(i).getDate())));
            modelReview.setRatingbar(list.get(i).getPoint());
            modelReviews.add(modelReview);
        }
        return modelReviews;
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
}
