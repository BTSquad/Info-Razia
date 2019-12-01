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
import com.google.firebase.auth.FirebaseAuth;

import tim.bts.inforazia.R;

public class LupaKataSandi extends AppCompatActivity {

    private ImageView back_btn;

    private FirebaseAuth firebaseAuth;
    private Button lupa_sandi_btn;
    private EditText email_reset;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_kata_sandi);
        progressBar = findViewById(R.id.progressBar_lupa);
        progressBar.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();

        back_btn = findViewById(R.id.back);

        email_reset = findViewById(R.id.email_reset_txt);
        lupa_sandi_btn = findViewById(R.id.reset_email_btn);




        lupa_sandi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String $email = email_reset.getText().toString().trim();

                Validasi_Form($email);
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentBack = new Intent(LupaKataSandi.this, MasukActivity.class);
                startActivity(intentBack);

            }
        });

    }


    public static boolean ValidasiEmail(String email){
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

    public void Validasi_Form(String email){

        if (email.isEmpty())
        {
            email_reset.setText("Email Harus Diisi");
            email_reset.requestFocus();
        }else if(!ValidasiEmail(email))
        {
            email_reset.setText("Format Email Salah");
        }else{
            progressBar.setVisibility(View.VISIBLE);
            lupa_kataSandi(email);
        }

    }

    public void lupa_kataSandi(String email)
    {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.INVISIBLE);
                if (task.isSuccessful())
                {
                    Toast.makeText(LupaKataSandi.this,
                            "Kami Telah Mengirimkan Bantuan Lewat Email Anda",
                            Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LupaKataSandi.this, task.getException().getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
