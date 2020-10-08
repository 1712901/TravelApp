package com.example.fragment;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fragment.Model.ModelReview;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.ArrayList;

public class MyAdapteReview extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;
    private ArrayList<ModelReview> modelReviews;
    private ItemLoadmore loadmore;
    private Boolean isLoading=false;
    private int visibleThreshold = 1;
    private int lastVisibleItem,totalItemCount;
    private final int VIEW_TYPE_ITEM=0,VIEW_TYPE_LOADING=1;

    public MyAdapteReview(@NonNull RecyclerView recyclerView ,Activity context, ArrayList<ModelReview> modelReviews) {
        this.context = context;
        this.modelReviews = modelReviews;

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
    };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if(viewType==VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_review,viewGroup ,false);
            return new MyAdapteReview.Myholder(view);
        }else if (viewType==VIEW_TYPE_LOADING){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, viewGroup ,false);
            return new LoadingHoder(view);
        }
        return null;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof Myholder) {
            final ModelReview modelReview = modelReviews.get(position);
            MyAdapteReview.Myholder myholder = (MyAdapteReview.Myholder) holder;

            myholder.comment.setText(modelReview.getComment());
            myholder.name.setText(modelReview.getName());
            myholder.date.setText(modelReview.getDate());
            try {
                myholder.uri = Uri.parse(modelReview.getAvatar());

            } catch (Exception e) {
                myholder.uri = Uri.parse("res:///" + R.drawable.icon_person);
            }
            myholder.avatar.setImageURI(myholder.uri);
            myholder.ratingBar.setRating(Float.parseFloat(modelReview.getRatingbar() + ""));
        }else if(holder instanceof LoadingHoder){
            LoadingHoder loadingHoder=(LoadingHoder)holder;
            loadingHoder.progressBar.setIndeterminate(true);
        }
    }
    public void setLoaded () {
        isLoading = false;
    }
    public int getItemViewType(int position) {
        return modelReviews.get(position)==null?VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
    }

    public class Myholder extends RecyclerView.ViewHolder{

        private Uri uri;
        private SimpleDraweeView avatar;
        private TextView name,date,comment;
        private RatingBar ratingBar;
        public Myholder(@NonNull View itemView) {
            super(itemView);
            this.avatar=(SimpleDraweeView)itemView.findViewById(R.id.imRevAvatar);
            this.name=(TextView)itemView.findViewById(R.id.txtRevName);
            this.date=(TextView)itemView.findViewById(R.id.txtRevDate);
            this.comment=(TextView)itemView.findViewById(R.id.txtRevComment);
            this.ratingBar=(RatingBar)itemView.findViewById(R.id.rtbRev);
        }

    }
    @Override
    public int getItemCount() {
        return modelReviews==null?0:modelReviews.size();
    }
}
