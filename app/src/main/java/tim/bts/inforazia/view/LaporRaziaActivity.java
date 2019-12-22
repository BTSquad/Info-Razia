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
import android.os.Build;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Callback;
import tim.bts.inforazia.APIService;
import tim.bts.inforazia.R;
import tim.bts.inforazia.adapter.ImageListAdapter;
import tim.bts.inforazia.model.DataUpload_model;
import tim.bts.inforazia.model.Upload_model;
import tim.bts.inforazia.model.Users_model;
import tim.bts.inforazia.notify.Client;
import tim.bts.inforazia.notify.Data;
import tim.bts.inforazia.notify.Response;
import tim.bts.inforazia.notify.Sender;
import tim.bts.inforazia.notify.Token;

public class LaporRaziaActivity extends AppCompatActivity {

    ImageView back_btn;
    Uri imageUri;

    private String namaFileUpload;

    private final int CAMERA_REQUEST_PICK = 7777, PICK_MEDIA_GALLERY = 8888;
    private final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1;

    private ImageView upload, gambar, pickMedia;
    private LinearLayout lokasiBtn;
    private TextView lokasiSaatIni, kotaKab;
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
    private DatabaseReference inputDetail;
    private ProgressDialog progressDialog;
    private long maxid_post = 1 ;

    APIService apiService;
    private boolean notify = false;
    private final int PERMISSION_CODE = 1000;
    private String kota;
    String UidUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lapor_razia);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Proses upload silahkan tunggu");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        UidUser = firebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("viewPost");
        inputDetail = FirebaseDatabase.getInstance().getReference().child("detailPost").child(UidUser);

        firebaseStorage = FirebaseStorage.getInstance().getReference();

        apiService = Client.getClient("https://fcm.googleapis.com").create(APIService.class);

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
        kotaKab = findViewById(R.id.kotaKabupaten);

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
                intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentBack);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                            || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){

                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

                        requestPermissions(permission, PERMISSION_CODE);


                    }else {
                        //permission granted

                       cameraOpen();
                    }
                }else {
                    // os system < marsmellow
                  cameraOpen();
                }

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

        kota = getIntent().getStringExtra("kota");

        if (kota != null){
            kotaKab.setText(kota);
        }else {
            cekUserLokasi();
        }


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String alamat = editLokasi.getText().toString().trim();
                String deskripsi = deskripsiPost.getText().toString().trim();
                String kotaAtauKab = kotaKab.getText().toString().trim();

                if (deskripsi.isEmpty())
                {
                    deskripsiPost.setError("Deskripsi Wajib Diisi");
                }else if(alamat.isEmpty())
                {
                    editLokasi.setError("Silahkan Berikan lokasi Anda");
                }else if(kotaAtauKab.isEmpty())
                {
                    kotaKab.setError("Pilih Kota Atau Kabupaten");
                    Toast.makeText(LaporRaziaActivity.this, "Pilih Kota atau Kabupaten", Toast.LENGTH_SHORT).show();
                }else if (fileUriList.size() <= 0){
                    Toast.makeText(LaporRaziaActivity.this, "Anda belum mengupload gambar", Toast.LENGTH_SHORT).show();
                }
                else {
                uploadGambar();
                }

           }
        });

    } @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        pickImage(requestCode,resultCode,data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    cameraOpen();
                }else {
                    Toast.makeText(this, "Izin Kamera Diperlukan", Toast.LENGTH_SHORT).show();
                }
            }
            case MY_PERMISSIONS_REQUEST_COARSE_LOCATION:{

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getLokasiTerakhir();

                }else {

                    Toast.makeText(this, "Izin Lokasi Diperlukan", Toast.LENGTH_SHORT).show();

                }

            }
        }

    }

    private void cameraOpen(){

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, System.currentTimeMillis() + "NEW PRICTURE");
        values.put(MediaStore.Images.Media.DESCRIPTION, System.currentTimeMillis() + "FROM CAMERA");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        // Intent Camera
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentCamera, CAMERA_REQUEST_PICK);

    }

    private void pickImage(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_PICK && resultCode == RESULT_OK)
        {
            try {

                InputStream is = getContentResolver().openInputStream(imageUri);

                Bitmap bitmap = BitmapFactory.decodeStream(is);
                fileDonelist.add(bitmap);
                fileUriList.add(imageUri);

            }catch (Exception e){
                e.printStackTrace();

            }
            imageListAdapter.notifyDataSetChanged();


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

    private String getFileName(Uri uri){
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

    private void uploadGambar() {
        progressDialog.show();

        Query getLastCounter = databaseReference.orderByKey().limitToLast(1);

        getLastCounter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    maxid_post = Long.parseLong(ds.getKey()) + 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final String uploadId = databaseReference.push().getKey();

        int upload_count;
            for (upload_count = 0; upload_count < fileUriList.size(); upload_count++){

                imageUri = fileUriList.get(upload_count);
                namaFileUpload = getFileName(imageUri);

                try {

                    InputStream is = getContentResolver().openInputStream(imageUri);

                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] final_image = baos.toByteArray();


                    final StorageReference fileToupload = firebaseStorage.child("LaporRaziaImage").child(namaFileUpload);

                    UploadTask uploadTask = fileToupload.putBytes(final_image);


                    final int finalUpload_count = upload_count;

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileToupload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {

                                    final String url = String.valueOf(uri);

                                    final String alamat = editLokasi.getText().toString().trim();
                                    String deskripsi = deskripsiPost.getText().toString().trim();
                                    String tanggal = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                    String waktu = new SimpleDateFormat("HH-mm-ss", Locale.getDefault()).format(new Date());
                                    firebaseAuth = FirebaseAuth.getInstance();
                                    firebaseUser = firebaseAuth.getCurrentUser();
                                    String UidUser = firebaseUser.getUid();
                                    final String userName = firebaseUser.getDisplayName();
                                    String photoUrl = firebaseUser.getPhotoUrl().toString();

                                    DataUpload_model data = new DataUpload_model(alamat, deskripsi, tanggal,
                                            waktu, url, UidUser, userName, photoUrl,
                                            uploadId, String.valueOf(maxid_post), "0");

                                    if (finalUpload_count < 1) {

                                        databaseReference.child(String.valueOf(maxid_post)).setValue(data);
                                    }

                                    final DatabaseReference simpanDetail = inputDetail.child(uploadId);

                                    final Upload_model upload_model = new Upload_model(url);
                                    simpanDetail.push().setValue(upload_model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(LaporRaziaActivity.this, "Upload Berhasil", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            fileUriList.clear();

                                            if (notify){
                                                getUser(alamat, url);
                                            }
                                            notify = false;
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


                }catch (IOException e){
                    e.printStackTrace();
                }

            }
    }

    private void fetchlocation() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(LaporRaziaActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            progressBar.setVisibility(View.INVISIBLE);

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
        } else {

                getLokasiTerakhir();
        }

    }

    private void getLokasiTerakhir(){

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

    private void getUser(final String alamat, final String url){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Users_model users_model = ds.getValue(Users_model.class);

                    assert users_model != null;
                    if (users_model.getLokasiNotif().contentEquals(kotaKab.getText()) && users_model.getNotif().equals("1")){
                        sendNotifikasi(users_model.getLokasiNotif(), alamat, url, users_model.getUserId());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotifikasi(final String lokasiNotif, final String alamat, final String url, final String uid){

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("tokens");

        Query getToken = tokens.orderByKey().equalTo(uid);

        getToken.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Token token = ds.getValue(Token.class);

                    Data data = new Data(lokasiNotif, url, alamat, kotaKab.getText().toString(), uid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender).enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                            if (response.code() == 200)
                            {
                                if (response.body().succsess == 1)
                                {
                                    Toast.makeText(LaporRaziaActivity.this, "Send Sucsses", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Response> call, Throwable t) {
                            Toast.makeText(LaporRaziaActivity.this, "Gagal Mengirim Notif", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LaporRaziaActivity.this, "Gagal " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void cekUserLokasi(){

        DatabaseReference db_user = FirebaseDatabase.getInstance().getReference("Users");
        Query getUserLokasi = db_user.orderByChild("userId").equalTo(UidUser);

        getUserLokasi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Users_model getUser = ds.getValue(Users_model.class);

                    if (getUser.getLokasiNotif().equals("Pilih Lokasi")){

                        AlertDialog.Builder builder = new AlertDialog.Builder(LaporRaziaActivity.this);

                        builder.setMessage("Lengkapi informasi anda, untuk memposting razia")
                                .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(LaporRaziaActivity.this, SetelanNotifikasiActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                }).setNegativeButton("Kembali", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentHome = new Intent(LaporRaziaActivity.this, HomeActivity.class);
                                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intentHome);
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }else {
                        kotaKab.setText(getUser.getLokasiNotif());
                    }



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}
