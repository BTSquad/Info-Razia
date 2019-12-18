package tim.bts.inforazia.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import tim.bts.inforazia.R;

public class UbahSandiActivity extends AppCompatActivity {

    private EditText sandiLama, sandiBaru, verifSandi;
    private Button simpanPerubahan;
    private ProgressBar progressBar;
    private ImageView back_btn;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_sandi);


        sandiLama = findViewById(R.id.pass_lama);
        sandiBaru = findViewById(R.id.pass_baru);
        verifSandi = findViewById(R.id.verifikasi_pass_baru);
        simpanPerubahan = findViewById(R.id.simpan_perubahan_btn);
        progressBar = findViewById(R.id.progressBar_ganti);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        progressBar.setVisibility(View.INVISIBLE);

        simpanPerubahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validasiMasukForm();
            }
        });

        back_btn = findViewById(R.id.back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentBack = new Intent(UbahSandiActivity.this, SetelanActivity.class);
                startActivity(intentBack);
            }
        });

    }

    private void validasiMasukForm() {

        String $sandi_lama = sandiLama.getText().toString().trim();
        String $sandi_baru = sandiBaru.getText().toString().trim();
        String $sandi_verif = verifSandi.getText().toString().trim();

        if ($sandi_lama.isEmpty()) {
            sandiLama.setError("Masukkan kata sandi lama anda");
            sandiLama.requestFocus();

        } else if ($sandi_baru.isEmpty()) {
            sandiBaru.setError("Masukkan kata sandi baru anda");
            sandiBaru.requestFocus();
        }else if ($sandi_verif.isEmpty()){
            verifSandi.setError("Verifikasi kata sandi harus diisi");
            verifSandi.requestFocus();
        }else if(!$sandi_baru.equals($sandi_verif)){
            Toast.makeText(this, "Kata sandi anda tidak sama dengan sandi verifikasi", Toast.LENGTH_LONG).show();

        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            getUserPass($sandi_lama, $sandi_baru);

        }
    }



    private void gantiKataSandi(String sandi){


        firebaseUser.updatePassword(sandi)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.VISIBLE);
                            Toast.makeText(UbahSandiActivity.this, "Kata Sandi Berhasil Diubah", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(UbahSandiActivity.this, SetelanActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                });


    }

    private void getUserPass(String pass_lama, final String pass_baru){
        String email = firebaseUser.getEmail();

        if (!email.isEmpty()){
            firebaseAuth.signInWithEmailAndPassword(email, pass_lama)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        gantiKataSandi(pass_baru);
                    }else {
                        progressBar.setVisibility(View.INVISIBLE
                        );
                        Toast.makeText(UbahSandiActivity.this, "Kata sandi lama salah", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }


    }


}
