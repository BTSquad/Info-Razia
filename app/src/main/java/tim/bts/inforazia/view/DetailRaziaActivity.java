package tim.bts.inforazia.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tim.bts.inforazia.R;
import tim.bts.inforazia.adapter.ImageSliderAdapter;
import tim.bts.inforazia.model.DataUpload_model;
import tim.bts.inforazia.model.Upload_model;

public class DetailRaziaActivity extends AppCompatActivity {


    List<Upload_model> urlList;
    List<DataUpload_model> dataUpload_models;
    DatabaseReference detailPost;
    ImageView back_btn;
    private String TAG = "test";
    SliderView sliderView;

    private TextView namaUser_detail, detail_lokasi, tanggalUpload, waktuUpload, deskripsiDetail;
    private ImageView userDetailPhoto;

    private String namauser;
    private String idupload;
    private String userId;
    private String lokasi;
    private String tanggal;
    private String waktu;
    private String deskripsi;
    private String urlPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_razia);

        sliderView = findViewById(R.id.imageSlider);

        namaUser_detail = findViewById(R.id.Username_post_detail);
        detail_lokasi = findViewById(R.id.lokasi_detail);
        tanggalUpload = findViewById(R.id.tanggal_upload_detail);
        waktuUpload = findViewById(R.id.waktu_upload_detail);
        deskripsiDetail = findViewById(R.id.deskripsi_detail);
        userDetailPhoto = findViewById(R.id.user_upload_detail);


        userId = getIntent().getStringExtra("userId");
        idupload = getIntent().getStringExtra("uploadId");
        namauser = getIntent().getStringExtra("namaUser");
        lokasi = getIntent().getStringExtra("lokasi");
        tanggal = getIntent().getStringExtra("tanggal");
        waktu = getIntent().getStringExtra("waktu");
        deskripsi = getIntent().getStringExtra("deskripsi");
        urlPhoto = getIntent().getStringExtra("photoUser");

        detailPost = FirebaseDatabase.getInstance().getReference().child("detailPost").child(userId).child(idupload);

        urlList = new ArrayList<>();

        detailPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Upload_model upload_model = ds.getValue(Upload_model.class);
                    urlList.add(upload_model);

                    Log.d(TAG, "Value :"+ upload_model.getmImageUrl());
                    succses();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        back_btn = findViewById(R.id.back);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intentBack = new Intent(DetailRaziaActivity.this, HomeActivity.class);
                intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentBack);
            }
        });




    }

    private void succses(){

        ImageSliderAdapter adapter = new ImageSliderAdapter(this, dataUpload_models, urlList);
        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.BLUE);
        sliderView.setScrollTimeInSec(10); //set scroll delay in seconds :
        sliderView.startAutoCycle();

        namaUser_detail.setText(namauser);
        detail_lokasi.setText(lokasi);
        tanggalUpload.setText(tanggal);
        waktuUpload.setText(waktu);
        deskripsiDetail.setText(deskripsi);

        Picasso.get()
                .load(urlPhoto)
                .fit()
                .centerCrop()
                .into(userDetailPhoto);

    }



}
