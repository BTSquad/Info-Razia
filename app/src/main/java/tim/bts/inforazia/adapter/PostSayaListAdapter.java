package tim.bts.inforazia.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tim.bts.inforazia.R;
import tim.bts.inforazia.model.DataUpload_model;
import tim.bts.inforazia.view.DetailRaziaActivity;

public class PostSayaListAdapter extends RecyclerView.Adapter<PostSayaListAdapter.PostSayaListViewHolder> {

    Context context;
    List<DataUpload_model> mUpload_dataModel;


    public PostSayaListAdapter(Context context, List<DataUpload_model> mUpload_dataModel){

        this.context = context;
        this.mUpload_dataModel = mUpload_dataModel;
    }



    @NonNull
    @Override
    public PostSayaListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_post_saya, parent, false);
        return new PostSayaListAdapter.PostSayaListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostSayaListViewHolder holder, final int position) {

        final DataUpload_model model = mUpload_dataModel.get(position);

        holder.user_nama.setText(model.getNamaUser());
        holder.lokasi_post.setText(model.getAlamat());

        Picasso.get().load(model.getPhotoUrlUser()).fit().centerCrop().into(holder.user_photo);

        Picasso.get().load(model.getmImageUrl()).fit().centerCrop().into(holder.user_post_photo);

        holder.user_post_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentDetail = new Intent(context, DetailRaziaActivity.class);

                intentDetail.putExtra("userId", model.getUID());
                intentDetail.putExtra("uploadId", model.getIdUpload());
                intentDetail.putExtra("namaUser", model.getNamaUser());
                intentDetail.putExtra("photoUser", model.getPhotoUrlUser());
                intentDetail.putExtra("tanggal", model.getTanggal());
                intentDetail.putExtra("waktu", model.getWaktu());
                intentDetail.putExtra("deskripsi", model.getDeskripsi());
                intentDetail.putExtra("lokasi", model.getAlamat());
                intentDetail.putExtra("activity", "postSaya");
                context.startActivity(intentDetail);

            }
        });

        holder.more_sett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                more_alert(model, position);

            }
        });



    }

    @Override
    public int getItemCount() {
        return mUpload_dataModel.size();
    }

    public class PostSayaListViewHolder extends RecyclerView.ViewHolder {

        private ImageView user_photo, more_sett, user_post_photo;
        private TextView user_nama, lokasi_post;

        private View mView;

        public PostSayaListViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            user_photo = mView.findViewById(R.id.user_upload_postSaya);
            more_sett = mView.findViewById(R.id.more_sett_postSaya);
            user_post_photo = mView.findViewById(R.id.image_post_view_postSaya);
            user_nama = mView.findViewById(R.id.Username_post_postSaya);
            lokasi_post = mView.findViewById(R.id.lokasi_post_postSaya);


        }
    }

    private void more_alert(final DataUpload_model model, final int positionData){

        ListView listView = new ListView(context);

        final List<String> data = new ArrayList<>();
        data.add("Hapus Post");
       // data.add("Share");

        //Array Adapter

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1 , data);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setView(listView);
        final AlertDialog alertDialog = builder.create();

        alertDialog.show();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

               if (position == 0){

                   final StorageReference photoRef =
                           FirebaseStorage.getInstance()
                                   .getReferenceFromUrl(model.getmImageUrl());

                   photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {

                           final DatabaseReference refHapus = FirebaseDatabase.getInstance().getReference();
                           Query deletePost = refHapus.child("viewPost").orderByChild("idUpload").equalTo(model.getIdUpload());

                           deletePost.addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                   for (DataSnapshot dsDelete : dataSnapshot.getChildren())
                                   {
                                       final DatabaseReference deleteDetailPost  = refHapus.child("detailPost").child(model.getUID()).child(model.getUID());

                                       dsDelete.
                                               getRef().removeValue();
                                       deleteDetailPost.removeValue();
                                       Toast.makeText(context, "Berhasil Menghapus Post", Toast.LENGTH_SHORT).show();
                                       alertDialog.dismiss();
                                       removeItem(positionData);


                                   }
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           });


                       }
                   });

               }

            }
        });

    }

    private void removeItem(int position) {
        mUpload_dataModel.remove(position);
        notifyItemRemoved(position);
    }

}
