package tim.bts.inforazia.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import tim.bts.inforazia.R;

public class SetelanActivity extends AppCompatActivity {

    private ImageView back_btn, imageUser;
    private TextView namaUser;
    private LinearLayout loadProfileActivity;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    private Switch aSwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setelan);



        back_btn = findViewById(R.id.back);
        imageUser = findViewById(R.id.imageSetelanuser);
        namaUser = findViewById(R.id.namaSetelanUser);
        aSwitch = findViewById(R.id.switch1);
        loadProfileActivity = findViewById(R.id.profileActivity);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentBack = new Intent(SetelanActivity.this, HomeActivity.class);
                intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentBack);
            }
        });


        loadProfileActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetelanActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        loadUserInformation();
    }




    public void loadUserInformation() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {

            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imageUser);
            namaUser.setText(firebaseUser.getDisplayName());


        }
    }
}
