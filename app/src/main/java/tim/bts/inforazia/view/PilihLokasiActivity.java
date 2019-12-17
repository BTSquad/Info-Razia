package tim.bts.inforazia.view;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import tim.bts.inforazia.R;

import tim.bts.inforazia.view.Fragment.PilihProvinsiFragment;

public class PilihLokasiActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_lokasi);

        String activity = getIntent().getStringExtra("Activity");

        Bundle bundle = new Bundle();
        bundle.putString("Activity", activity);
        PilihProvinsiFragment fragment = new PilihProvinsiFragment();
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment, "Pilih Provinsi");
        fragmentTransaction.commit();

        ImageView back_btn = findViewById(R.id.back);

        assert activity != null;
        if (activity.equals("ActivityLapor")){

            back_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intentBack = new Intent(PilihLokasiActivity.this, LaporRaziaActivity.class);
                    startActivity(intentBack);
                }
            });
        }else {

            back_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intentBack = new Intent(PilihLokasiActivity.this, SetelanActivity.class);
                    startActivity(intentBack);
                }
            });
        }

    }


}
