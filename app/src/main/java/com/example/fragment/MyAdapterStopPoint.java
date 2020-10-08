package com.example.fragment;

import android.app.Activity;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.RecyclerView;
import com.example.fragment.Model.ModelStopPoint;
import com.example.fragment.network.APIService;
import com.example.fragment.network.RetrofitClient;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAdapterStopPoint extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;
    private ArrayList<ModelStopPoint> models;
    private OnItemClickListener clickListener;
    private String authorization;

    public MyAdapterStopPoint(Activity context, ArrayList<ModelStopPoint> models,String authorization) {
        this.context = context;
        this.models = models;
        this.authorization=authorization;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_stopoint,viewGroup ,false);
        return new MyHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        final ModelStopPoint model=models.get(position);

        MyAdapterStopPoint.MyHolder myHolder=(MyAdapterStopPoint.MyHolder) holder;
        myHolder.mName.setText(model.getName());
        myHolder.mTimeArr.setText(model.getTimeArr());
        myHolder.mTimeLeave.setText(model.getTimeLeave());
        myHolder.mCost.setText(model.getCost());
        myHolder.mAdress.setText(model.getAdress());
        //myHolder.mImgView.setImageResource(model.getImg());

        try {
            myHolder.uri = Uri.parse(model.getImg());
        }catch (Exception e){
            myHolder.uri = Uri.parse("res:///"+R.drawable.marker);
        }
        myHolder.mImgView.setImageURI(myHolder.uri);

        myHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialog(model,position);
            }
        });
    }
    @Override
    public int getItemCount() {
        return models.size();
    }
    // Class Holder
    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Uri uri;
        SimpleDraweeView mImgView;
        TextView mName,mTimeArr,mTimeLeave,mAdress,mCost;
        ImageButton btnRemove;
        public MyHolder(@NonNull final View itemView) {
            super(itemView);
            this.mImgView=(SimpleDraweeView) itemView.findViewById(R.id.imgStopPoint);
            this.mName=(TextView)itemView.findViewById(R.id.txtNameSP);
            this.mTimeArr=(TextView)itemView.findViewById(R.id.txtArrivalSP);
            this.mTimeLeave=(TextView)itemView.findViewById(R.id.txtLeaveSP);
            this.mAdress=(TextView)itemView.findViewById(R.id.txtAdressSP);
            this.mCost=(TextView)itemView.findViewById(R.id.txtCostSP);
            this.btnRemove=(ImageButton)itemView.findViewById(R.id.imbtClose);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            // get the position on recyclerview.
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }
    public void removeStopPointApi(ModelStopPoint model, final int position ){
        APIService apiService = RetrofitClient.getApiService();
        apiService.removeStopPoint(authorization,model.getID()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context,"Thành Công",Toast.LENGTH_SHORT).show();
                    removeAt(position);
                }else if(response.code()==500){
                    Toast.makeText(context,response.message(),Toast.LENGTH_SHORT).show();
                }else if (response.code()==403){
                    Toast.makeText(context,"Không được phép",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context,"Không thể xóa điểm dừng",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void ShowDialog(final ModelStopPoint model, final int position ){
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Xóa Điểm dừng")
                .setMessage("Bạn có muốn tiếp tục")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeStopPointApi(model,position);
                    }
                })
                //set negative button
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // nothing
                    }
                })
                .show();
    }
    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
    public void removeAt(int position) {
        models.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, models.size());
    }

}
