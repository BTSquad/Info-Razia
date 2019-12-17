package tim.bts.inforazia.view.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tim.bts.inforazia.R;
import tim.bts.inforazia.adapter.ProvinsiListAdapter;
import tim.bts.inforazia.model.Provinsi_model;

/**
 * A simple {@link Fragment} subclass.
 */
public class PilihProvinsiFragment extends Fragment {

    private RecyclerView mLokasi;
    private LinearLayoutManager manager;
    private ProvinsiListAdapter provinsiListAdapter;
    private DatabaseReference reference;

    List<Provinsi_model> provinsiData;

    private String activity;

    public PilihProvinsiFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getArguments().getString("Activity");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewProvinsi =  inflater.inflate(R.layout.fragment_pilih_provinsi, container, false);


        reference = FirebaseDatabase.getInstance().getReference("Lokasi");

        mLokasi = viewProvinsi.findViewById(R.id.recyclerView_listProvinsi);

        manager = new LinearLayoutManager(getContext());
        mLokasi.setHasFixedSize(true);
        mLokasi.setLayoutManager(manager);

        provinsiData = new ArrayList<>();

        provinsiListAdapter = new ProvinsiListAdapter(getContext(), provinsiData, activity);
        mLokasi.setAdapter(provinsiListAdapter);
        getProvinsi();


        return viewProvinsi;
    }


    private void getProvinsi(){
        Query getDataProvinsi = reference.orderByChild("provinsi");
        getDataProvinsi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    provinsiData.add(ds.getValue(Provinsi_model.class));


                    mLokasi.setAdapter(provinsiListAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
