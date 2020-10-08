package com.example.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fragment.Model.ModelTour;
import com.example.fragment.network.APIService;
import com.example.fragment.network.RetrofitClient;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAdapterTour extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM=0,VIEW_TYPE_LOADING=1;

    private Activity context;
    private ArrayList<ModelTour> models;
    private ItemLoadmore loadmore;

    private OnItemClickListener clickListener;
    private Boolean isLoading=false;
    private int visibleThreshold = 1;
    private int lastVisibleItem,totalItemCount;
    private RecyclerView.ViewHolder recyclerView;
    private int position;
    private final int MYTOUR=1,LISTOUR=2;
    private int TYPE;
    private String authorization;
    private int tourId;
    public MyAdapterTour(@NonNull RecyclerView recyclerView, Activity context, ArrayList<ModelTour> models,int type,String authorization) {
        this.context = context;
        this.models = models;
        this.TYPE=type;
        this.authorization=authorization;

        final LinearLayoutManager linearLayoutManager=(LinearLayoutManager)recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem=linearLayoutManager.findLastVisibleItemPosition()+1;
                if(!isLoading&&totalItemCount<=(lastVisibleItem + visibleThreshold)){
                    if(loadmore!=null) {
                        loadmore.onLoadMore();
                    }
                    isLoading=true;
                }
            }
        });
    }

    public void setLoadmore(ItemLoadmore loadmore) {
        this.loadmore = loadmore;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if(viewType==VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview,viewGroup ,false);
            return new MyHolder(view);
        }else if (viewType==VIEW_TYPE_LOADING){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, viewGroup ,false);
            return new LoadingHoder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof MyHolder){
            final ModelTour model=models.get(position);

            final MyHolder myHolder=(MyHolder) holder;
            myHolder.mName.setText(model.getName());
            myHolder.mDate.setText(model.getDate());
            myHolder.mType.setText(model.getType());
            myHolder.mCost.setText(model.getCost());
            if(TYPE==LISTOUR){
                myHolder.imbtRevClone.setImageResource(R.drawable.ic_clone);
            }
            try {
                myHolder.uri = Uri.parse("res:///"+R.drawable.img_travel);
            }catch (Exception e){
                myHolder.uri = Uri.parse(model.getImg());
            }
            myHolder.mImgView.setImageURI(myHolder.uri);

            myHolder.imbtRevClone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(TYPE==MYTOUR){
                        // call API delete
                        ShowDialog(model.getID(),position);
                    }else if(TYPE==LISTOUR){
                        APIService apiService = RetrofitClient.getApiService();
                        apiService.cloneTour(authorization,model.getID())
                                .enqueue(new Callback<JSONObject>() {
                                    @Override
                                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                                        if(response.isSuccessful()){
                                            Toast.makeText(context,"Clone Tour success",Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(context,"Clone Tour fail",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<JSONObject> call, Throwable t) {
                                        Toast.makeText(context,"Clone Tour fail",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            });
        }
        else if(holder instanceof LoadingHoder){
            LoadingHoder loadingHoder=(LoadingHoder)holder;
            loadingHoder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return models==null ? 0 : models.size();
    }

    @Override
    public int getItemViewType(int position) {
        return models.get(position)==null?VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
    }

    public void setLoaded () {
        isLoading = false;
    }
    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
    // Class Holder
    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private SimpleDraweeView mImgView;
        private TextView mName,mDate,mType,mCost;
        private Uri uri;
        private ImageButton imbtRevClone;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            this.mImgView=(SimpleDraweeView) itemView.findViewById(R.id.imgIv);
            this.mName=(TextView)itemView.findViewById(R.id.txtName);
            this.mDate=(TextView)itemView.findViewById(R.id.txtDate);
            this.mType=(TextView)itemView.findViewById(R.id.txtType);
            this.mCost=(TextView)itemView.findViewById(R.id.txtCost);
            this.imbtRevClone=(ImageButton)itemView.findViewById(R.id.imbtRevClone);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }
    public void ShowDialog(final int tourId, final int position){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                //set icon
                .setIcon(android.R.drawable.ic_dialog_info)
                //set title
                .setTitle("Delete Tour")
                //set message
                .setMessage("Bạn có muốn tiếp tục xóa")
                //set positive button
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        APIService apiService = RetrofitClient.getApiService();
                        apiService.deleteTour(authorization,tourId,-1)
                                .enqueue(new Callback<JSONObject>() {
                                    @Override
                                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                                        if(response.isSuccessful()){
                                            removeAt(position);
                                            Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(context,"Fail",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<JSONObject> call, Throwable t) {
                                        Toast.makeText(context,"Fail",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                //set negative button
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what should happen when negative button is clicked

                    }
                })
                .show();
    }
    public void removeAt(int position) {
        models.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, models.size());
    }
}
