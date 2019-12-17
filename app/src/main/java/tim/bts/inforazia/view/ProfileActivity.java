package tim.bts.inforazia.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tim.bts.inforazia.R;
import tim.bts.inforazia.adapter.PostSayaListAdapter;
import tim.bts.inforazia.model.DataUpload_model;
import tim.bts.inforazia.model.Upload_model;

public class ProfileActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    private ImageView imageUser, back_btn, updatePhoto_btn;
    private TextView namaUser;

    private RecyclerView mListUpload;
    private List<DataUpload_model> dataUpload_models;
    private PostSayaListAdapter postSayaListAdapter;
    private LinearLayoutManager manager;
    private int PICK_MEDIA_GALLERY =  1001;
    private StorageReference firebaseStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        imageUser = findViewById(R.id.image_profile_user);
        namaUser = findViewById(R.id.namaUserProfile);
        updatePhoto_btn = findViewById(R.id.edit_PhotoProfile);

        mListUpload = findViewById(R.id.recyclerView_userPost);

        firebaseStorage = FirebaseStorage.getInstance().getReference();

        manager = new LinearLayoutManager(this);
        mListUpload.setHasFixedSize(true);
        mListUpload.setLayoutManager(manager);

        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);


        back_btn = findViewById(R.id.back);

        loadUserInformation();

        dataUpload_models = new ArrayList<>();

        getUserPost();

        postSayaListAdapter = new PostSayaListAdapter(this, dataUpload_models);
        mListUpload.setAdapter(postSayaListAdapter);


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentBack = new Intent(ProfileActivity.this, HomeActivity.class);
                intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentBack);
            }
        });


        updatePhoto_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"pilih gambar"), PICK_MEDIA_GALLERY);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData() != null){

            Uri fileUri = data.getData();
            try {

                InputStream is = getContentResolver().openInputStream(fileUri);

                Bitmap bitmap = BitmapFactory.decodeStream(is);
                imageUser.setImageBitmap(bitmap);

            }catch (Exception e){
                e.printStackTrace();

            }

            updatePhotoProfile(fileUri);

        }

    }

    public void loadUserInformation(){


        if (firebaseUser != null) {

            if (firebaseUser.getPhotoUrl() != null) {


                Picasso.get().load(firebaseUser.getPhotoUrl().toString()).into(imageUser);
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

    private void getUserPost(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("viewPost");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    DataUpload_model dataUpload_model1 = ds.getValue(DataUpload_model.class);

                    if (dataUpload_model1.getUID().equals(firebaseUser.getUid())){

                        dataUpload_models.add(dataUpload_model1);

                    }


                }

                mListUpload.setAdapter(postSayaListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void updatePhotoProfile(Uri fileUri){

        String namaFileUpload = getFileName(fileUri);

        final StorageReference fileToupload = firebaseStorage.child("PhotoUpdateUser");

        fileToupload.child(namaFileUpload).putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileToupload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {

                        final String url = String.valueOf(uri);

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(url))
                                .build();

                        firebaseUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(ProfileActivity.this, "Photo Berhasil diubah", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String getFileName(Uri uri){
        String result = null;
        if (uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri , null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }
        if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1){
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}

