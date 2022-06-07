package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.Inet4Address;
import java.util.ArrayList;

//Fragment that shows all modules available in a recycler view
public class LearnFragment extends Fragment {

    private static final String TAG = "LearnFragment";

    private RecyclerView  moduleRecyclerView;
    private ModuleAdapter moduleAdapter;
    private RecyclerView.LayoutManager moduleLayoutManager;
    ArrayList<Modules> modulesList = new ArrayList<>();
    private String uUsername;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_learn, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Retrieve username from home fragment
        uUsername = getActivity().getIntent().getStringExtra("FLAG");

        //Instantiate elements
        moduleRecyclerView = view.findViewById(R.id.moduleRecylerView);
        moduleRecyclerView.setHasFixedSize(true);
        moduleLayoutManager = new LinearLayoutManager(getContext());

        //Load all the modules the user has into the recycler view
        loadUserModules();
    }

    //Load all modules the user has not completed into the recyclerview
    // 1. Load all
    // 2. Filter load later
    private void loadUserModules() {
        Log.d(TAG, "Begin loading of all modules the user has");

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername).child("modules");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            String tempModTitle, tempStatus, tempUrl;
            int tempModID, tempModPreviewImg, tempXP;
            int counter = 1;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()) {
                    tempModID = counter;
                    tempModTitle = ds.child("name").getValue(String.class);
                    tempStatus = ds.child("status").getValue(String.class);
                    tempUrl = ds.child("url").getValue(String.class);
                    tempXP = ds.child("xp").getValue(Integer.class);
                    tempModPreviewImg = ds.child("preview image").getValue(Integer.class);
                    modulesList.add(new Modules(tempModID, tempModTitle, tempUrl, tempXP, tempModPreviewImg));
                    Log.d(TAG, "New Module added with details: ID " + tempModID + " Title: " + tempModTitle + " URL: " + tempUrl + " XP: " + tempXP + " Image: " + tempModPreviewImg);
                    counter++;
                }
                //Loading modules into recycler view
                moduleAdapter = new ModuleAdapter(modulesList);
                moduleRecyclerView.setLayoutManager(moduleLayoutManager);
                moduleRecyclerView.setAdapter(moduleAdapter);
                Log.d(TAG, String.valueOf(modulesList));

                //Set on click listener to take into detail screen
                moduleAdapter.setOnItemClickListener(new ModuleAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Intent i = new Intent(getContext(), DetailedModuleActivity.class);
                        i.putExtra("module ID", String.valueOf(modulesList.get(position).getModuleID()));
                        i.putExtra("username", uUsername);
                        startActivity(i);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }
}
