package tim.bts.inforazia.adapter;

import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import tim.bts.inforazia.R;
import tim.bts.inforazia.model.DataUpload_model;

import tim.bts.inforazia.model.Users_model;
import tim.bts.inforazia.view.DetailRaziaActivity;
import tim.bts.inforazia.view.ProfileActivity;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> {

    List<DataUpload_model> mUpload;
    List<Users_model> mUserUpload;
    Context mContext;


    public PostListAdapter( Context context, List<DataUpload_model> upload, List<Users_model> userUpload){
        mUpload = upload;
        mContext = context;
        mUserUpload = userUpload;
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
        final Users_model users_model = mUserUpload.get(position);

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

                Intent intentUserProfile = new Intent(mContext, ProfileActivity.class);
                intentUserProfile.putExtra("namaUser", users_model.getNamaUser());
                intentUserProfile.putExtra("photoUser", users_model.getPhotoUser());
                intentUserProfile.putExtra("emailUser", users_model.getEmailUser());
                intentUserProfile.putExtra("userId", users_model.getUserId());

            }
        });


    }

    @Override
    public int getItemCount() {
        return mUpload.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{

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




}
