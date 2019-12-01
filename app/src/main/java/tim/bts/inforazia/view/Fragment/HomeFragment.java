package tim.bts.inforazia.view.Fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tim.bts.inforazia.R;
import tim.bts.inforazia.adapter.PostListAdapter;
import tim.bts.inforazia.model.DataUpload_model;
import tim.bts.inforazia.model.Upload_model;
import tim.bts.inforazia.model.Users_model;
import tim.bts.inforazia.view.HomeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    RecyclerView mPostList;
    long maxid;

    public HomeFragment() {
        // Required empty public constructor
    }

    private List<DataUpload_model> mUploads;
    private List<Users_model> mUserUpload;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        DatabaseReference mDatabaseRefrence = FirebaseDatabase.getInstance().getReference();


        mPostList = v.findViewById(R.id.post_list);
        mPostList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mUploads = new ArrayList<>();
        mUserUpload = new ArrayList<>();

        final Query getUser = mDatabaseRefrence.child("Users");

        getUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnap : dataSnapshot.getChildren()){

                    Users_model users_model = userSnap.getValue(Users_model.class);
                    mUserUpload.add(users_model);
                    getDetailPost(users_model.getUserId());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

      return v;
    }

    private void getDetailPost(String UID){

        DatabaseReference refDetail = FirebaseDatabase.getInstance().getReference();


        Query mPostView = refDetail.child("viewPost").child(UID);

        mPostView.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnap : dataSnapshot.getChildren()){

                    DataUpload_model dataUpload_model = postSnap.getValue(DataUpload_model.class);
                    mUploads.add(dataUpload_model);

                    PostListAdapter postListAdapter = new PostListAdapter(getContext(), mUploads, mUserUpload);
                    mPostList.setAdapter(postListAdapter);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "error: "+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}
