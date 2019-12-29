package tim.bts.inforazia.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import tim.bts.inforazia.R;

import tim.bts.inforazia.model.Users_model;
import tim.bts.inforazia.notify.Token;
import tim.bts.inforazia.view.Fragment.HomeFragment;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    private DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToogle;

    GoogleSignInClient mGoogleSignInClient;
    CircleImageView navImageView;
    TextView navNamaUser;
    TextView navEmailUser;

    Users_model getUser;

    private boolean doubleClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (!isConnected(HomeActivity.this)) {
            buildDialog(HomeActivity.this).show();
        }

        //Google Sign In------------------------------------------------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //--------------------------------------------------------------

        updateToken(FirebaseInstanceId.getInstance().getToken());

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = findViewById(R.id.shortCut_lapor);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeActivity.this, LaporRaziaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
            }
        });


        mDrawerLayout = findViewById(R.id.drawer_navigasi);
        mDrawerToogle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mDrawerToogle);
        mDrawerToogle.syncState();


        // inisialisasi Home -----------------------------------------------------------------------
        HomeFragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment, "Home");
        fragmentTransaction.commit();

        // Read Navigasi ---------------------------------------------------------------------------
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
         navEmailUser =  headerView.findViewById(R.id.email_user);
         navNamaUser =  headerView.findViewById(R.id.nama_user);
         navImageView =  headerView.findViewById(R.id.profile_image);

         loadUserInformation();
         cekUserLokasi(firebaseUser);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.home){


            HomeFragment fragment = new HomeFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment, "Home");
            fragmentTransaction.commit();

        }else if (id == R.id.pasal){

            Intent intent = new Intent(HomeActivity.this, PasalActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }else if (id == R.id.lapor){

            Intent intent = new Intent(HomeActivity.this, LaporRaziaActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }else if (id == R.id.about){

            Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }else if (id == R.id.keluar){
              FirebaseAuth.getInstance().signOut();;
              LoginManager.getInstance().logOut();
              mGoogleSignInClient.signOut();

            Intent intent = new Intent(HomeActivity.this, MasukActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }



    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else {

            Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();
        }


        if (doubleClick) {
            super.onBackPressed();
            return;
        }

        this.doubleClick = true;


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleClick=false;
            }
        }, 2000);

    }


    public void loadUserInformation(){

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {

            if (firebaseUser.getPhotoUrl() == null){
                navImageView.setEnabled(false);
            }else{

                Picasso.get().load(firebaseUser.getPhotoUrl().toString()).into(navImageView);
            }

            navNamaUser.setText(firebaseUser.getDisplayName());
            navEmailUser.setText(firebaseUser.getEmail());

            if (firebaseUser.getPhotoUrl() == null) {

                for (UserInfo profile : firebaseUser.getProviderData()) {
                        if (profile.getPhotoUrl() == null){
                            navEmailUser.setEnabled(false);
                        }else{

                            Picasso.get().load(firebaseUser.getPhotoUrl().toString()).into(navImageView);

                        }

                }
            }
            if (firebaseUser.getDisplayName() == null) {

                for (UserInfo profile : firebaseUser.getProviderData()) {

                    navNamaUser.setText(profile.getDisplayName());

                  }
            }
            if (firebaseUser.getEmail() == null) {

                for (UserInfo profile : firebaseUser.getProviderData()) {

                    navEmailUser.setText(profile.getEmail());

                }

            }
        }
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
        else return false;
        } else
        return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Anda Sedang Offline");
        builder.setMessage("Tidak ada jaringan yang terhubung, Anda tidak bisa melihat postingan terbaru");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder;
    }

    private void updateToken(String token){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }

    private void cekUserLokasi(final FirebaseUser user){

        DatabaseReference db_user = FirebaseDatabase.getInstance().getReference("Users");
        Query getUserLokasi = db_user.orderByChild("userId").equalTo(user.getUid());

        getUserLokasi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                     getUser = ds.getValue(Users_model.class);

                    assert getUser != null;
                    if (getUser.getLokasiNotif().equals("Pilih Lokasi")){

                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);


                        builder.setMessage("Lengkapi informasi anda, untuk mendapatkan notifikasi sesuai kota anda")
                                .setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(HomeActivity.this, SetelanNotifikasiActivity.class);
                                        intent.putExtra("uid", user.getUid());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                }).setNegativeButton("Tidak Sekarang", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        final AlertDialog alertDialog = builder.create();
                        int waktu_loading = 5000;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                            alertDialog.show();

                            }
                        },waktu_loading);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
