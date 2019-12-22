package tim.bts.inforazia.view;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.Arrays;

import tim.bts.inforazia.R;
import tim.bts.inforazia.model.Users_model;

public class MasukActivity extends AppCompatActivity {

    static final int GOOGLE_SIGN = 1001;
    private static final String TAG = "test";

    private FirebaseAuth mAuth;

    private EditText email_Masuk, kataSandi_Masuk;
    private Button masuk_btn ;
    private ImageView back_btn;
    private ProgressBar progressBar;
    private ImageView fbButton, btnSignIn_google;
    CallbackManager callbackManager;

    GoogleSignInClient mGoogleSignInClient;

    private DatabaseReference mDatabaseRef;

    private boolean doubleClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masuk);
        progressBar = findViewById(R.id.progressBar_Masuk);
        progressBar.setVisibility(View.INVISIBLE);


        //facebook login--------------------------------------------------------------
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        //----------------------------------------------------------------------------

        //firebase------------------------------------------
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        //--------------------------------------------------


        //Google Sign In------------------------------------------------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //--------------------------------------------------------------

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        email_Masuk =  findViewById(R.id.email_masuk_txt);
        kataSandi_Masuk =  findViewById(R.id.kataSandi_masuk_txt);
        masuk_btn =  findViewById(R.id.btn_masuk);
        back_btn = findViewById(R.id.back);
        btnSignIn_google = findViewById(R.id.btn_login_google);

        btnSignIn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                signIn();

            }
        });

        masuk_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String $email = email_Masuk.getText().toString();
                String $kataSandi = kataSandi_Masuk.getText().toString();

                validasiMasukForm($email, $kataSandi);

            }
        });


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                moveTaskToBack(true);
            }
        });

        fbButton = findViewById(R.id.login_button);

        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbButton.setEnabled(false);
                LoginManager.getInstance().logInWithReadPermissions(MasukActivity.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MasukActivity.this, "Login Di batalkan" , Toast.LENGTH_LONG).show();
                        fbButton.setEnabled(true);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(MasukActivity.this, "error : " + error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

            }
        });


    }


    @Override
    public void onBackPressed() {

        if (doubleClick) {
            super.onBackPressed();
            return;
        }else {
            progressBar.setVisibility(View.INVISIBLE);
        }

        this.doubleClick = true;
        Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                doubleClick=false;
            }
        }, 2000);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null && firebaseUser.isEmailVerified()){
            updateUI(firebaseUser);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                progressBar.setVisibility(View.INVISIBLE);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                e.printStackTrace();
                // Google Sign In failed, update UI appropriately

            }
        }
    }




    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN);

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                           // Sign in success, update UI with the signed-in user's information
                            btnSignIn_google.setEnabled(true);

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            progressBar.setVisibility(View.VISIBLE);
                            cekUserId(currentUser);
                            updateUI(currentUser);

                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MasukActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                         }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void updateUI(FirebaseUser user){

        if (user != null){
            progressBar.setVisibility(View.INVISIBLE);

            Intent intent = new Intent(MasukActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }


    private void handleFacebookAccessToken(AccessToken token) {
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressBar.setVisibility(View.INVISIBLE);

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            cekUserId(currentUser);
                            updateUI(currentUser);

                            fbButton.setEnabled(true);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                             Toast.makeText(MasukActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                             updateUI(null);

                             fbButton.setEnabled(true);
                        }

                        // ...
                    }
                });
    }


    private void MasukUser(String email, String kataSandi){
         mAuth.signInWithEmailAndPassword(email, kataSandi)
                .addOnCompleteListener(MasukActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                         if (task.isSuccessful())
                        {
                            if (mAuth.getCurrentUser().isEmailVerified())
                            {
                                FirebaseUser currentUser = mAuth.getCurrentUser();

                                cekUserId(currentUser);
                                updateUI(currentUser);

                            }else {
                                Toast.makeText(MasukActivity.this, "Akun Belum Terverifikasi", Toast.LENGTH_SHORT).show();

                            }

                        }else {

                            Toast.makeText(MasukActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                        }

                    }
                });

    }

    private void validasiMasukForm(String email, String kataSandi)
    {
        if (email.isEmpty())
        {
            email_Masuk.setError("Email Harus Diisi");
            email_Masuk.requestFocus();

        }else if(kataSandi.isEmpty())
        {
            kataSandi_Masuk.setError("Password Harus Diisi");
            kataSandi_Masuk.requestFocus();
        }
        else if(!ValidasiEmail(email)){

            email_Masuk.setError("Format Email Salah");
            email_Masuk.requestFocus();
        }

        else
        {
            progressBar.setVisibility(View.VISIBLE);
            MasukUser(email, kataSandi);

        }

    }

    private static boolean ValidasiEmail(String email){
        boolean validasi;
        String emailPatern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String emailPattern2 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";

        if (email.matches(emailPatern) || email.matches(emailPattern2) && email.length() > 0)
        {
            validasi = true;
        }else{
            validasi = false;
        }

        return validasi;
    }

    public void Daftar(View view) {
        Intent intenDaftar = new Intent(MasukActivity.this, DaftarActivity.class);
        startActivity(intenDaftar);
    }

    public void bantuan_masuk(View view) {

        Intent intenBantuan = new Intent(MasukActivity.this, LupaKataSandi.class);
        startActivity(intenBantuan);


    }

    private void cekUserId(final FirebaseUser user){

      Query cekUser = mDatabaseRef.child("Users").orderByChild("userId").equalTo(user.getUid());

        cekUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) simpanUserId(user);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void simpanUserId(final FirebaseUser user){

        DatabaseReference simpanUser = mDatabaseRef.child("Users").child(user.getUid());

        Users_model users_model = new Users_model(user.getUid(),
                user.getDisplayName(),
                user.getEmail(),
                String.valueOf(user.getPhotoUrl())
                , "1", "Pilih Lokasi");

        simpanUser.setValue(users_model);
    }


   }




