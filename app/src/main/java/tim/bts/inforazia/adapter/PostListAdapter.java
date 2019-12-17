package tim.bts.inforazia.adapter;

import android.app.AlertDialog;
import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;


import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import java.util.List;

import tim.bts.inforazia.R;
import tim.bts.inforazia.model.DataUpload_model;

import tim.bts.inforazia.model.Laporan_model;
import tim.bts.inforazia.view.DetailRaziaActivity;
import tim.bts.inforazia.view.ViewProfileActivity;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> implements Filterable {

    private List<DataUpload_model> mUpload;
    private List<DataUpload_model> mUploadSearch;

    private Context mContext;
    long  maxid_post = 1;



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
                intentDetail.putExtra("activity", "postView");
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
        });long  maxid_post = 1;


        holder.postLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri peta = Uri.parse("google.navigation:q="+dataUpload_model.getAlamat());
                Intent intentpeta = new Intent(Intent.ACTION_VIEW,peta);
                intentpeta.setPackage("com.google.android.apps.maps");
                mContext.startActivity(intentpeta);

            }
        });

        holder.more_sett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                more_alert(dataUpload_model.getIdUpload());

            }
        });


    }

    @Override
    public int getItemCount() {

        return mUpload.size() ;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        private ImageView postImageView, postUserImage, more_sett;
        private TextView  postUsername, postLokasi;
        private LinearLayout infoUserPost;

        View mView;

        private PostViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            more_sett = mView.findViewById(R.id.more_sett);
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


    private void more_alert(final String idUpload){

        ListView listView = new ListView(mContext);

        final List<String> data = new ArrayList<>();
        data.add("Laporkan");
        data.add("Share");

        //Array Adapter

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1 , data);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(true);
        builder.setView(listView);
        final AlertDialog alertDialog = builder.create();

        alertDialog.show();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    final FirebaseUser mUserGet = mAuth.getCurrentUser();
                    final DatabaseReference getUserReport = FirebaseDatabase.getInstance().getReference("UserReport").child(mUserGet.getUid());


                    Query getLastCounter = getUserReport.orderByKey().limitToLast(1);

                    getLastCounter.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds : dataSnapshot.getChildren())
                            {
                                maxid_post = Long.parseLong(ds.getKey()) + 1;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    getUserReport.orderByChild("idUpload").equalTo(idUpload).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()){
                                        alertDialog.dismiss();
                                        Toast.makeText(mContext, "Anda telah melaporkan postingan ini", Toast.LENGTH_SHORT).show();
                                    }else {

                                        getUserReport.child(String.valueOf(maxid_post)).child("idUpload").setValue(idUpload);
                                        tambahLaporan(idUpload, alertDialog);

                                        Toast.makeText(mContext, "Terimakasih telah melaporkan post yang tidak sesuai", Toast.LENGTH_SHORT).show();

                                    }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else if(position == 1){
                    Toast.makeText(mContext, "Belum dibuat ini jangan dklik lah aiiii kau ini", Toast.LENGTH_LONG).show();
                }

            }
        });

    }


    private void  tambahLaporan(String idUpload, final AlertDialog alertDialog){

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("viewPost");

        reference.orderByChild("idUpload").equalTo(idUpload).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        String key = ds.getKey();
                        DataUpload_model dataUpload_model1 = ds.getValue(DataUpload_model.class);

                        int laporan = Integer.parseInt(dataUpload_model1.getLaporan()) +1;

                        reference.child(key).child("laporan").setValue(String.valueOf(laporan));
                        alertDialog.dismiss();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
