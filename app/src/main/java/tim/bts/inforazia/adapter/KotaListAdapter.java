package tim.bts.inforazia.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import java.util.List;

import tim.bts.inforazia.R;
import tim.bts.inforazia.model.Kota_model;

import tim.bts.inforazia.view.LaporRaziaActivity;
import tim.bts.inforazia.view.SetelanNotifikasiActivity;

public class KotaListAdapter extends RecyclerView.Adapter<KotaListAdapter.KotaViewHolder> {

    private Context context;
    private List<Kota_model> mKota;
    private String activity;

    private DatabaseReference reference;


    public KotaListAdapter(Context context, List<Kota_model> mKota, String activity){
        this.context = context;
        this.mKota = mKota;
        this.activity = activity;

    }

    @NonNull
    @Override
    public KotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_kota, parent, false);
        return new KotaListAdapter.KotaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull KotaViewHolder holder, int position) {
            final Kota_model kota_model = mKota.get(position);

            holder.kotaShow.setText(kota_model.getValue());

            if (activity.equals("SetelanNotif")){
                holder.pilihKota.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseUser mUser = mAuth.getCurrentUser();
                        final String uid = mUser.getUid();
                        reference = FirebaseDatabase.getInstance().getReference("Users");

                        reference.orderByChild("userId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists())
                                {
                                    for (DataSnapshot ds : dataSnapshot.getChildren())
                                    {
                                        String key = ds.getKey();
                                        reference.child(key).child("lokasiNotif").setValue(kota_model.getValue());

                                        Intent intenNotifSetelan = new Intent(context, SetelanNotifikasiActivity.class);
                                        intenNotifSetelan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intenNotifSetelan.putExtra("uid", uid);
                                        context.startActivity(intenNotifSetelan);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });
            }else {

                holder.pilihKota.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentLapor = new Intent(context, LaporRaziaActivity.class);
                        intentLapor.putExtra("kota", kota_model.getValue());
                        context.startActivity(intentLapor);
                    }
                });

            }


    }

    @Override
    public int getItemCount() {
        return mKota.size();
    }

    public class KotaViewHolder extends RecyclerView.ViewHolder {

        private TextView kotaShow;
        private LinearLayout pilihKota;
        View mView;

        public KotaViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            kotaShow = mView.findViewById(R.id.kota_lokasi);
            pilihKota = mView.findViewById(R.id.pick_kota);
        }

    }
}
