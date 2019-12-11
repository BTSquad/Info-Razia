package tim.bts.inforazia.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import com.squareup.picasso.Target;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import tim.bts.inforazia.APIService;
import tim.bts.inforazia.R;
import tim.bts.inforazia.adapter.ImageListAdapter;
import tim.bts.inforazia.model.DataUpload_model;
import tim.bts.inforazia.model.Upload_model;
import tim.bts.inforazia.notify.Client;
import tim.bts.inforazia.notify.Data;
import tim.bts.inforazia.notify.Response;
import tim.bts.inforazia.notify.Sender;
import tim.bts.inforazia.notify.Token;

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
    private DatabaseReference inputDetail;
    private ProgressDialog progressDialog;
    private long maxid_post = 1 ;

    APIService apiService;
    private boolean notify = false;



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

        progressBar.setVisibility(View.INVISIBLE);

        fileDonelist = new ArrayList<>();
        fileUriList = new ArrayList<>();
        imageListAdapter = new ImageListAdapter(fileDonelist, fileUriList);

        mUploadList = findViewById(R.id.horizontalUpload);

        mUploadList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mUploadList.setAdapter(imageListAdapter);

        updateToken(FirebaseInstanceId.getInstance().getToken());

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
                notify = true;
                String alamat = editLokasi.getText().toString().trim();
                String deskripsi = deskripsiPost.getText().toString().trim();
                if (deskripsi.isEmpty())
                {
                    deskripsiPost.setError("Deskripsi Wajib Diisi");
                }else if(alamat.isEmpty())
                {
                    editLokasi.setError("Silahkan Berikan lokasi Anda");
                }else {


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

        Query getLastCounter = databaseReference.orderByKey().limitToLast(1);

        getLastCounter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {

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


                final StorageReference fileToupload = firebaseStorage.child("LaporRaziaImage").child(namaFileUpload);

                final int finalUpload_count = upload_count;

                fileToupload.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                        uploadId, String.valueOf(maxid_post));

                                if (finalUpload_count < 1) {

                                    databaseReference.child(String.valueOf(maxid_post)).setValue(data);
                                }

                                final DatabaseReference simpanDetail = inputDetail.child(uploadId);

                                Upload_model upload_model = new Upload_model(url);
                                simpanDetail.push().setValue(upload_model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(LaporRaziaActivity.this, "Upload Berhasil", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        fileUriList.clear();

                                        if (notify){
                                            sendNofication(alamat, url);
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


    private void updateToken(String token){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }



    private void sendNofication(final String alamat, final String url){

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("tokens");

        tokens.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), url, alamat, "coba", ds.getKey());


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

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


}
