package tim.bts.inforazia.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import tim.bts.inforazia.R;
import tim.bts.inforazia.model.Users_model;

public class SetelanNotifikasiActivity extends AppCompatActivity {

    private Switch aSwitch;
    private ImageView back_btn;
    private LinearLayout pilih_lokasi;
    private String uid;
    private TextView lokasiPilihanUser;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setelan_notifikasi);

        uid = getIntent().getStringExtra("uid");
        reference = FirebaseDatabase.getInstance().getReference("Users");

        Query getUserSetelan = reference.orderByChild("userId").equalTo(uid);

        lokasiPilihanUser = findViewById(R.id.lokasi_pilihan);

        getUserSetelan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    Users_model users_model = ds.getValue(Users_model.class);
                    lokasiPilihanUser.setText(users_model.getLokasiNotif());

                    if (users_model.getNotif().equals("1")){
                        aSwitch.setChecked(true);
                    }else {
                        aSwitch.setChecked(false);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        aSwitch = findViewById(R.id.switch1);
        pilih_lokasi = findViewById(R.id.pilih_lokasi_notif);

        pilih_lokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_pilihLokasi = new Intent(SetelanNotifikasiActivity.this, PilihLokasiActivity.class);
                intent_pilihLokasi.putExtra("Activity", "SetelanNotif");
                intent_pilihLokasi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_pilihLokasi);
            }
        });


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    reference.orderByChild("userId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            aSwitch.getTextOn();

                            for (DataSnapshot ds : dataSnapshot.getChildren())
                            {
                                String key = ds.getKey();
                                reference.child(key).child("notif").setValue("1");

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else {
                    reference.orderByChild("userId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            aSwitch.getTextOn();

                            for (DataSnapshot ds : dataSnapshot.getChildren())
                            {
                                String key = ds.getKey();
                                reference.child(key).child("notif").setValue("0");

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
        });

        back_btn = findViewById(R.id.back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentBack = new Intent(SetelanNotifikasiActivity.this, SetelanActivity.class);
                startActivity(intentBack);
            }
        });



    }
}
