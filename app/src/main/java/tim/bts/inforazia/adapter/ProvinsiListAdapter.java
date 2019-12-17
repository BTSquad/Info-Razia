package tim.bts.inforazia.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import tim.bts.inforazia.R;
import tim.bts.inforazia.model.Provinsi_model;
import tim.bts.inforazia.view.Fragment.PilihKotaFragment;


public class ProvinsiListAdapter extends RecyclerView.Adapter<ProvinsiListAdapter.LokasiViewHolder> {

    private Context context;
    private List<Provinsi_model> mProvinsi;
    private String activity;

    public ProvinsiListAdapter(Context context, List<Provinsi_model> mProvinsi, String activity){
        this.context = context;
        this.mProvinsi = mProvinsi;
        this.activity = activity;


    }


    @NonNull
    @Override
    public LokasiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_provinsi, parent, false);
        return new LokasiViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LokasiViewHolder holder, int position) {
            final Provinsi_model provinsi_model = mProvinsi.get(position);



            holder.provinsiShow.setText(provinsi_model.getProvinsi());
            holder.kota.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PilihKotaFragment fragment = new PilihKotaFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("provinsi", provinsi_model.getProvinsi());
                    bundle.putString("Activity", activity);
                    fragment.setArguments(bundle);
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();


                }
            });


    }

    @Override
    public int getItemCount() {
        return mProvinsi.size();
    }

    public class LokasiViewHolder extends RecyclerView.ViewHolder {

        private TextView provinsiShow;
        private LinearLayout kota;


        View mView;
        private LokasiViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            provinsiShow = itemView.findViewById(R.id.provinsi_lokasi);
            kota = itemView.findViewById(R.id.kotaOpen);
        }
    }


}
