package com.thawk.tickethawk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.thawk.tickethawk.RecyclerClasses.CommunityAdapter;
import com.thawk.tickethawk.RecyclerClasses.CommunityAdapter2;

import java.util.ArrayList;
import java.util.Collections;

public class CommunityEditActivity extends Activity {

    DatabaseReference ref;


    RecyclerView communitiesRecyclerView;
    RecyclerView.Adapter mCommunitiesAdapter;
    RecyclerView.LayoutManager mCommunitiesLayoutManager;

    ArrayList<String> communities = new ArrayList<>();

    Button saveButton;

    public String intendedCommunity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ref = SplitMainActivity.ref;

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_communityedit);

        findViews();
        setClickListener();
        fetchCommunities();
    }

    void fetchCommunities(){
        DatabaseReference query = ref.child("communities");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                communities.add((String)(dataSnapshot.child("name").getValue()));
                Collections.sort(communities, String.CASE_INSENSITIVE_ORDER);

                mCommunitiesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                communities.remove((String)(dataSnapshot.child("name").getValue()));

                mCommunitiesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void findViews(){
        communitiesRecyclerView = findViewById(R.id.communitiesRecyclerView);
        mCommunitiesLayoutManager = new LinearLayoutManager(this);
        mCommunitiesAdapter = new CommunityAdapter2(communities, this);

        communitiesRecyclerView.setAdapter(mCommunitiesAdapter);
        communitiesRecyclerView.setLayoutManager(mCommunitiesLayoutManager);

        saveButton = findViewById(R.id.nextButton);
    }

    void setClickListener(){
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userID = FirebaseAuth.getInstance().getUid();

                if (!intendedCommunity.equals("")){

                    ref.child("customers").child(userID).child("primaryCommunity").setValue(intendedCommunity);

                    //transition to main vendor activity

                    finish();

                } else {
                    Log.i("sufficient", "false");
                }
            }
        });
    }
}
