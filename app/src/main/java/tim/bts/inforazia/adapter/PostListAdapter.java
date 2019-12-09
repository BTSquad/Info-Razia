package tim.bts.inforazia.adapter;

import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;


import androidx.recyclerview.widget.RecyclerView;



import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tim.bts.inforazia.R;
import tim.bts.inforazia.model.DataUpload_model;

import tim.bts.inforazia.view.DetailRaziaActivity;
import tim.bts.inforazia.view.ViewProfileActivity;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> implements Filterable {

    List<DataUpload_model> mUpload;
    List<DataUpload_model> mUploadSearch;

    Context mContext;



    public PostListAdapter(Context context){
        mContext = context;
        mUpload = new ArrayList<>();
        mUploadSearch = new ArrayList<>();

    }

    public void addAll(List<DataUpload_model> newData)
    {
        int initSize = mUpload.size();
        mUpload.addAll(newData);
        notifyItemRangeChanged(initSize, newData.size());
        mUploadSearch.addAll(newData);
    }

    public String getLastItemId()
    {
        return mUpload.get(mUpload.size() - 1).getCounter();
    }


    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        final DataUpload_model dataUpload_model = mUpload.get(position);

        holder.postUsername.setText(dataUpload_model.getNamaUser());
        holder.postLokasi.setText(dataUpload_model.getAlamat());
        Picasso.get()
                .load(dataUpload_model.getPhotoUrlUser())
                .fit()
                .centerCrop()
                .into(holder.postUserImage);

        Picasso.get()
                .load(dataUpload_model.getmImageUrl())
                .fit()
                .centerCrop()
                .into(holder.postImageView);

        holder.postImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentDetail = new Intent(mContext, DetailRaziaActivity.class);

                intentDetail.putExtra("userId", dataUpload_model.getUID());
                intentDetail.putExtra("uploadId", dataUpload_model.getIdUpload());
                intentDetail.putExtra("namaUser", dataUpload_model.getNamaUser());
                intentDetail.putExtra("photoUser", dataUpload_model.getPhotoUrlUser());
                intentDetail.putExtra("tanggal", dataUpload_model.getTanggal());
                intentDetail.putExtra("waktu", dataUpload_model.getWaktu());
                intentDetail.putExtra("deskripsi", dataUpload_model.getDeskripsi());
                intentDetail.putExtra("lokasi", dataUpload_model.getAlamat());
                mContext.startActivity(intentDetail);
            }
        });

        holder.infoUserPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentUserProfile = new Intent(mContext, ViewProfileActivity.class);
                intentUserProfile.putExtra("userId", dataUpload_model.getUID());
                mContext.startActivity(intentUserProfile);

            }
        });


        holder.postLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri peta = Uri.parse("google.navigation:q="+dataUpload_model.getAlamat());
                Intent intentpeta = new Intent(Intent.ACTION_VIEW,peta);
                intentpeta.setPackage("com.google.android.apps.maps");
                mContext.startActivity(intentpeta);

            }
        });


    }

    @Override
    public int getItemCount() {
     //   Collections.reverse(mUpload);
        return mUpload.size() ;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        public ImageView postImageView, postUserImage;
        public TextView  postUsername, postLokasi;
        public LinearLayout infoUserPost;

        View mView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            postImageView =  mView.findViewById(R.id.image_post_view);
            postUsername = mView.findViewById(R.id.Username_post);
            postUserImage = mView.findViewById(R.id.user_upload);
            postLokasi = mView.findViewById(R.id.lokasi_post);
            infoUserPost = mView.findViewById(R.id.viewProfileUser);


        }
    }

    @Override
    public Filter getFilter() {
        return mUploadFilter;
    }

    private Filter mUploadFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<DataUpload_model> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(mUploadSearch);

            }else {
                String filterPatern = constraint.toString().toLowerCase().trim();

                for (DataUpload_model dataUpload_model : mUploadSearch)
                {
                    if (dataUpload_model.getAlamat().toLowerCase().contains(filterPatern))
                    {
                        filteredList.add(dataUpload_model);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
                mUpload.clear();
                mUpload.addAll((List) results.values);
                notifyDataSetChanged();
        }
    };
}
