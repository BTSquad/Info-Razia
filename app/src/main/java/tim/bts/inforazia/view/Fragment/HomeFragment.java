package tim.bts.inforazia.view.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;

import java.util.List;

import tim.bts.inforazia.R;

import tim.bts.inforazia.adapter.PostListAdapter;
import tim.bts.inforazia.model.DataUpload_model;

import tim.bts.inforazia.view.SetelanActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment  {

    private RecyclerView mPostList;
    private LinearLayoutManager manager;
    private PostListAdapter postListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private DatabaseReference refDetail;
    private int totalItemLoad = 7;
    private int total_item = 0, last_visible_item;
    private boolean isLoading = false, isMaxData = false;
    private String last_node="", last_key="";

    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);

        refDetail = FirebaseDatabase.getInstance().getReference();

        mPostList = v.findViewById(R.id.post_list);
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh);

        getLastKeyFromFirebase();

        manager = new LinearLayoutManager(getActivity());
        mPostList.setLayoutManager(manager);

        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);


        postListAdapter = new PostListAdapter(getContext());
        mPostList.setAdapter(postListAdapter);


        getPost();


        mPostList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                total_item = manager.getItemCount();
                last_visible_item = manager.findLastVisibleItemPosition();

                    if (!isLoading && total_item <= ((last_visible_item + totalItemLoad)))
                    {
                        getPost();
                        isLoading = true;

                    }

            }
        });

     swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
         @Override
         public void onRefresh() {

             getLastKeyFromFirebase();
             getPost();
             swipeRefreshLayout.setRefreshing(false);
         }
     });




      return v;
    }


    private void getPost(){

        if (!isMaxData){

            Query mPostView;

            if (TextUtils.isEmpty(last_node)) {
                mPostView = refDetail.child("viewPost").orderByKey().limitToFirst(totalItemLoad);

            }else {
                mPostView = refDetail.child("viewPost").orderByKey().startAt(last_node).limitToFirst(totalItemLoad);

            }

           mPostView.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if (dataSnapshot.hasChildren())
                   {
                       List<DataUpload_model> newData = new ArrayList<>();

                       for (DataSnapshot ds : dataSnapshot.getChildren())
                       {
                                final DataUpload_model dataUpload_model = ds.getValue(DataUpload_model.class);

                                if (Integer.parseInt(dataUpload_model.getLaporan()) >= 30){

                                    final StorageReference photoRef =
                                            FirebaseStorage.getInstance()
                                                    .getReferenceFromUrl(dataUpload_model.getmImageUrl());
                                    //delete file storage
                                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Query deletePost = refDetail.child("viewPost")
                                                    .orderByChild("idUpload")
                                                    .equalTo(dataUpload_model.getIdUpload());

                                            deletePost.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot dsDelete : dataSnapshot.getChildren())
                                                    {
                                                        DatabaseReference deleteDetailPost  =
                                                                refDetail.child("detailPost")
                                                                        .child(dataUpload_model.getUID())
                                                                        .child(dataUpload_model.getIdUpload());
                                                        //delete viewPost
                                                        dsDelete.getRef().removeValue();
                                                        //delete detailPost
                                                        deleteDetailPost.removeValue();

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    });




                                }else {
                                    if (dataUpload_model.getIdUpload() != null){
                                        newData.add(dataUpload_model);
                                    }else {
                                        Toast.makeText(getActivity(), "Data belum ada", Toast.LENGTH_SHORT).show();
                                    }

                                }
                       }

                       if (newData.size() == 0){
                           Toast.makeText(getActivity(), "Data masih kosong", Toast.LENGTH_SHORT).show();
                       }else {

                           last_node = String.valueOf(newData.get(newData.size() - 1).getCounter());


                           if (!last_node.equals(last_key))
                           {
                               newData.remove(newData.size() - 1);
                           }else {
                               last_node = "end";

                           }

                           postListAdapter.addAll(newData);

                           mPostList.setAdapter(postListAdapter);
                           isLoading = false;

                       }

                   }else {
                       isLoading = false;
                       isMaxData = true;
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                    isLoading = false;
               }
           });

        }


        }



        private void getLastKeyFromFirebase()
        {
            Query getLastKey = refDetail.child("viewPost").orderByKey().limitToLast(1);

            getLastKey.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        last_key = ds.getKey();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Gagal mengambil key", Toast.LENGTH_SHORT).show();
                }
            });
        }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.action_setting, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.setelan:
                Intent intent = new Intent(getActivity(), SetelanActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            case R.id.pencarian:

                SearchView searchView = (SearchView) item.getActionView();
                searchView.setQueryHint("Pencarian Lokasi");

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        postListAdapter.getFilter().filter(newText);

                        return false;
                    }
                });
                return true;

            case R.id.my_post:

                Toast.makeText(getActivity(), "Belum dibuat sabar lah", Toast.LENGTH_SHORT).show();


                return true;



            default:
                return super.onOptionsItemSelected(item);

        }
    }



}







