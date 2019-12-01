package tim.bts.inforazia.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import tim.bts.inforazia.R;

public class ProfileActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    private ImageView imageUser;
    private TextView namaUser;

    private ImageView back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageUser = findViewById(R.id.image_profile_user);
        namaUser = findViewById(R.id.namaUserProfile);

        back_btn = findViewById(R.id.back);

        loadUserInformation();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentBack = new Intent(ProfileActivity.this, HomeActivity.class);
                intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentBack);
            }
        });
    }



    public void loadUserInformation(){

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {

            if (firebaseUser.getPhotoUrl() != null) {

                Glide.with(this).load(firebaseUser.getPhotoUrl().toString()).into(imageUser);
            }
            if (firebaseUser.getDisplayName() != null) {

                namaUser.setText(firebaseUser.getDisplayName());

            }else{
                namaUser.setText(firebaseUser.getDisplayName());
            }
//            if (firebaseUser.getEmail() != null) {
//
//                navEmailUser.setText(firebaseUser.getEmail());
//
//            }
        }
    }
}

