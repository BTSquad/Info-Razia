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
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
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

    private static final String ID_UNIT_TEST = "ca-app-pub-3940256099942544/1033173712";
    private static final String ID_UNIT_IKLAN_INTERESIAL = "ca-app-pub-7973392951366806/4923011625";


    private InterstitialAd interstitialIklan;


    private String namauser;
    private String idupload;
    private String userId;
    private String lokasi;
    private String tanggal;
    private String waktu;
    private String deskripsi;
    private String urlPhoto;
    private String activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_razia);

        // ads------------------------------------------------------------------------------------
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        interstitialIklan = new InterstitialAd(this);
        interstitialIklan.setAdUnitId(ID_UNIT_IKLAN_INTERESIAL);

        interstitialIklan.loadAd(new AdRequest.Builder().build());

       //reqIklan();
        interesialAds();


        //end ads----------------------------------------------------------------------------

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
        activity = getIntent().getStringExtra("activity");

        detailPost = FirebaseDatabase.getInstance().getReference().child("detailPost").child(userId).child(idupload);

        urlList = new ArrayList<>();

        detailPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Upload_model upload_model = ds.getValue(Upload_model.class);

                    urlList.add(upload_model);

                    setDetail();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        back_btn = findViewById(R.id.back);

        if (activity.equals("postSaya")){

            back_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intentBack = new Intent(DetailRaziaActivity.this, ProfileActivity.class);
                    intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentBack);
                }
            });
        }else {

            back_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intentBack = new Intent(DetailRaziaActivity.this, HomeActivity.class);
                    intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentBack);
                }
            });

        }




    }

    private void setDetail(){

        ImageSliderAdapter adapter = new ImageSliderAdapter(this, dataUpload_models, urlList);
        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GREEN);
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

    private void interesialAds()
    {

        interstitialIklan.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                tampilAds();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(DetailRaziaActivity.this,
                        "onAdFailedToLoad() with error code: " + errorCode,
                        Toast.LENGTH_SHORT).show();
            }


        });




    }

    private void tampilAds(){
        if (interstitialIklan != null && interstitialIklan.isLoaded()) {
            interstitialIklan.show();
        } else {
            Toast.makeText(this, "Tidak ada iklan yang di tampilkan", Toast.LENGTH_SHORT).show();

        }
    }


    private void reqIklan()
    {
        if (!interstitialIklan.isLoading() && !interstitialIklan.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            interstitialIklan.loadAd(adRequest);
        }

    }



}
