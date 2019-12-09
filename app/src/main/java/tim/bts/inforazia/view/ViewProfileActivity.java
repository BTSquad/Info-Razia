package tim.bts.inforazia.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import tim.bts.inforazia.R;
import tim.bts.inforazia.model.Users_model;

public class ViewProfileActivity extends AppCompatActivity {

    private ImageView back_btn, user_image;
    private TextView userName, emailUserProfile;

    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        user_image = findViewById(R.id.imageUserViewProfile);
        userName = findViewById(R.id.namaUserViewProfile);
        emailUserProfile = findViewById(R.id.emailUserViewProfile);

        String userId = getIntent().getStringExtra("userId");
        userLoadProfile(userId);


        back_btn = findViewById(R.id.back);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentBack = new Intent(ViewProfileActivity.this, HomeActivity.class);
                intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentBack);
            }
        });

    }


    private void userLoadProfile(String uid){

        Query db_user = databaseReference.orderByChild("userId").equalTo(uid);

        db_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    Users_model users_model = ds.getValue(Users_model.class);

                    userName.setText(users_model.getNamaUser());
                    emailUserProfile.setText(users_model.getEmailUser());

                    Picasso.get()
                            .load(users_model.getPhotoUser())
                            .fit()
                            .centerCrop()
                            .into(user_image);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
