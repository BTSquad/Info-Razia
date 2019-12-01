package tim.bts.inforazia.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tim.bts.inforazia.R;
import tim.bts.inforazia.adapter.ImageListAdapter;
import tim.bts.inforazia.model.DataUpload_model;
import tim.bts.inforazia.model.Upload_model;

public class LaporRaziaActivity extends AppCompatActivity {


    private ImageView back_btn;
    private Uri imageUri;

    private String namaFileUpload;

    private final int CAMERA_REQUEST_PICK = 7777, PICK_MEDIA_GALLERY = 8888;
    private final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1;

    private ImageView upload, gambar, pickMedia;
    private LinearLayout lokasiBtn;
    private TextView lokasiSaatIni;
    private ProgressBar progressBar;
    private RecyclerView mUploadList;
    private Button post;
    private EditText deskripsiPost, editLokasi;

    private FusedLocationProviderClient fusedLocationClient;

    private List<Bitmap> fileDonelist;
    private List<Uri> fileUriList;

    private ImageListAdapter imageListAdapter;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private StorageReference firebaseStorage;

    private DatabaseReference databaseReference;
    private DatabaseReference inputDb;
    private DatabaseReference inputDetail;
    private ProgressDialog progressDialog;
    long maxid_post, maxid_detailPost ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lapor_razia);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Proses upload silahkan tunggu");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String UidUser = firebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("viewPost").child(UidUser);
        inputDetail = FirebaseDatabase.getInstance().getReference().child("detailPost").child(UidUser);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    maxid_post = dataSnapshot.getChildrenCount() ;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        firebaseStorage = FirebaseStorage.getInstance().getReference();


        upload = findViewById(R.id.upload_gambar_btn);
        gambar = findViewById(R.id.gambarSet);
        lokasiBtn = findViewById(R.id.berbagiLokasi);
        lokasiSaatIni = findViewById(R.id.txt_lokasiSaatini);
        progressBar = findViewById(R.id.progresBarLokasi);
        back_btn = findViewById(R.id.back);
        pickMedia = findViewById(R.id.pick_gambar_btn);
        post = findViewById(R.id.post_btn);
        deskripsiPost = findViewById(R.id.deskripsi_txt);
        editLokasi = findViewById(R.id.field_lokasi);

        progressBar.setVisibility(View.INVISIBLE);


        fileDonelist = new ArrayList<>();
        fileUriList = new ArrayList<>();
        imageListAdapter = new ImageListAdapter(fileDonelist, fileUriList);

        mUploadList = findViewById(R.id.horizontalUpload);


        mUploadList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mUploadList.setAdapter(imageListAdapter);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentBack = new Intent(LaporRaziaActivity.this, HomeActivity.class);
                startActivity(intentBack);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, true);
                startActivityForResult(intentCamera, CAMERA_REQUEST_PICK);
            }
        });

        lokasiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                fetchlocation();
            }
        });

        pickMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"pilih gambar"), PICK_MEDIA_GALLERY);

            }
        });

        lokasiSaatIni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri peta = Uri.parse("google.navigation:q="+editLokasi.getText().toString().trim());
                Intent intentpeta = new Intent(Intent.ACTION_VIEW,peta);
                intentpeta.setPackage("com.google.android.apps.maps");
                startActivity(intentpeta);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String alamat = editLokasi.getText().toString().trim();
                String deskripsi = deskripsiPost.getText().toString().trim();
                if (deskripsi.isEmpty())
                {
                    deskripsiPost.setError("Deskripsi Wajib Diisi");
                }else if(alamat.isEmpty())
                {
                    editLokasi.setError("Silahkan Berikan lokasi Anda");
                }else {

                //    laporRazia();
                uploadGambar();
                }

           }
        });


    } @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        pickImage(requestCode,resultCode,data);

    }



    private void pickImage(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST_PICK && resultCode == RESULT_OK)
        {

            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");

               fileDonelist.add(bitmap);

        }else if (requestCode == PICK_MEDIA_GALLERY && resultCode == RESULT_OK)
        {
            if (data.getClipData() != null)
            {
                int totalImage = data.getClipData().getItemCount();

                if (totalImage > 5){
                    Toast.makeText(this, "Jumlah Maksimum Gambar 5", Toast.LENGTH_SHORT).show();
                }else {

                    for (int i = 0; i < totalImage; i++) {
                        Uri fileUri = data.getClipData().getItemAt(i).getUri();

                        try {

                            InputStream is = getContentResolver().openInputStream(fileUri);

                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            fileDonelist.add(bitmap);
                            fileUriList.add(fileUri);

                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                        imageListAdapter.notifyDataSetChanged();

                    }
                }
            }else if(data.getData() != null){

                Uri fileUri = data.getData();
                try {

                    InputStream is = getContentResolver().openInputStream(fileUri);

                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    fileDonelist.add(bitmap);
                    fileUriList.add(fileUri);

                }catch (Exception e){
                    e.printStackTrace();

                }
                imageListAdapter.notifyDataSetChanged();
            }

        }
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


    private void uploadGambar()
    {
        progressDialog.show();

        final String uploadId = databaseReference.push().getKey();
        final DatabaseReference simpanDetail = inputDetail.child(uploadId);

            for (int upload_count = 0; upload_count < fileUriList.size(); upload_count++){

                imageUri = fileUriList.get(upload_count);
                namaFileUpload = getFileName(imageUri);

                inputDb = databaseReference.child(String.valueOf(maxid_post + 1));
                final StorageReference fileToupload = firebaseStorage.child("LaporRaziaImage").child(namaFileUpload);

                fileToupload.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        fileToupload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {

                                final String url = String.valueOf(uri);


                                String alamat = editLokasi.getText().toString().trim();
                                String deskripsi = deskripsiPost.getText().toString().trim();
                                String tanggal = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                String waktu = new SimpleDateFormat("HH-mm-ss", Locale.getDefault()).format(new Date());
                                firebaseAuth = FirebaseAuth.getInstance();
                                firebaseUser = firebaseAuth.getCurrentUser();
                                String UidUser = firebaseUser.getUid();
                                String userName = firebaseUser.getDisplayName();
                                String photoUrl = firebaseUser.getPhotoUrl().toString();

                                DataUpload_model data = new DataUpload_model(alamat, deskripsi, tanggal, waktu, url, UidUser, userName, photoUrl, uploadId);

                                inputDb.setValue(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Upload_model upload_model = new Upload_model(url);
                                                simpanDetail.push().setValue(upload_model);

                                                Toast.makeText(LaporRaziaActivity.this, "Upload Berhasil", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                fileUriList.clear();

                                                Intent intent = new Intent(LaporRaziaActivity.this, HomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        });

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LaporRaziaActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                    }
                });

            }

    }

    private void fetchlocation()
    {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(LaporRaziaActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            progressBar.setVisibility(View.INVISIBLE);

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LaporRaziaActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                new AlertDialog.Builder(this).setTitle("Izin Lokasi").setMessage("Yakin Memberikan Akses lokasi ?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(LaporRaziaActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_COARSE_LOCATION);

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(LaporRaziaActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
            }
        } else {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {

                                Double lattiude = location.getLatitude();
                                Double longtitude = location.getLongitude();

                                try {
                                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(lattiude, longtitude, 1);
                                    if (addresses != null && addresses.size() > 0){

                                        String address = addresses.get(0).getAddressLine(0);

                                        progressBar.setVisibility(View.INVISIBLE);
                                        editLokasi.setText(address);

                                    }
                                }catch (Exception e){
                                    Toast.makeText(LaporRaziaActivity.this, "eroor : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

        }

    }
}
