package tim.bts.inforazia.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import tim.bts.inforazia.R;

public class SetelanActivity extends AppCompatActivity {

    private ImageView back_btn, imageUser;
    private TextView namaUser;


    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setelan);


        LinearLayout setelanNotif = findViewById(R.id.setelan_notifikasi);
        LinearLayout ubahKataSandi = findViewById(R.id.ubah_kataSandi);
        back_btn = findViewById(R.id.back);
        imageUser = findViewById(R.id.imageSetelanuser);
        namaUser = findViewById(R.id.namaSetelanUser);

        LinearLayout loadProfileActivity = findViewById(R.id.profileActivity);


        setelanNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetelanActivity.this, SetelanNotifikasiActivity.class);
                intent.putExtra("uid", firebaseUser.getUid());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        ubahKataSandi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetelanActivity.this, UbahSandiActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentBack = new Intent(SetelanActivity.this, HomeActivity.class);
                intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentBack);
            }
        });


        loadProfileActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetelanActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });



        loadUserInformation();
    }




    public void loadUserInformation() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {

            Picasso.get().load(firebaseUser.getPhotoUrl()).into(imageUser);
            namaUser.setText(firebaseUser.getDisplayName());


        }
    }
}
