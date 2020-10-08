package com.example.fragment;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fragment.Model.ModelMember;
import com.example.fragment.Model.ModelStopPoint;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class MyAdapterMember extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Activity context;
    private ArrayList<ModelMember> models;

    public MyAdapterMember(Activity context, ArrayList<ModelMember> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.carview_member,viewGroup ,false);
        return new MyAdapterMember.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ModelMember model=models.get(position);
        MyAdapterMember.MyHolder myHolder=(MyAdapterMember.MyHolder) holder;
        myHolder.name.setText(model.getName());
        myHolder.phoneNumber.setText(model.getPhoneNumber());

        try {
            myHolder.uri = Uri.parse(model.getImg());
        }catch (Exception e){
            myHolder.uri = Uri.parse("res:///"+R.drawable.icon_person);
        }
        myHolder.img.setImageURI(myHolder.uri);
    }
    @Override
    public int getItemCount() {
        return models.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        private Uri uri;
        private SimpleDraweeView img;
        private TextView name,phoneNumber;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            this.img=(SimpleDraweeView)itemView.findViewById(R.id.imAvatar);
            this.name=(TextView)itemView.findViewById(R.id.txtMbName);
            this.phoneNumber=(TextView)itemView.findViewById(R.id.txtphone);

        }
    }
}
