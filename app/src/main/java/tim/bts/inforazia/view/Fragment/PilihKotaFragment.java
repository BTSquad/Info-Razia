package tim.bts.inforazia.view.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tim.bts.inforazia.R;
import tim.bts.inforazia.adapter.KotaListAdapter;
import tim.bts.inforazia.model.Kota_model;


public class PilihKotaFragment extends Fragment {

    private String provinsi;
    private RecyclerView mKota;
    private LinearLayoutManager manager;
    private KotaListAdapter kotaListAdapter;
    private DatabaseReference reference;

    List<Kota_model> kotaData;
    private String activity;


    public PilihKotaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        provinsi = getArguments().getString("provinsi");
        activity = getArguments().getString("Activity");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewKota = inflater.inflate(R.layout.fragment_pilih_kota, container, false);

        reference = FirebaseDatabase.getInstance().getReference("detailLokasi");

        mKota = viewKota.findViewById(R.id.recyclerView_listKota);

        manager = new LinearLayoutManager(getContext());
        mKota.setHasFixedSize(true);
        mKota.setLayoutManager(manager);

        kotaData = new ArrayList<>();

        kotaListAdapter = new KotaListAdapter(getContext(), kotaData, activity);
        mKota.setAdapter(kotaListAdapter);
        getKota();

        return viewKota;
    }

    private void getKota() {

        Query getDataProvinsi = reference.child(provinsi).orderByChild("value");
        getDataProvinsi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    kotaData.add(ds.getValue(Kota_model.class));

                    mKota.setAdapter(kotaListAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Gagal Mengambil Data", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
