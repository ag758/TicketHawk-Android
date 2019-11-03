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
import com.google.firebase.database.FirebaseDatabase;
import com.thawk.tickethawk.RecyclerClasses.CommunityAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CustomerActivity2 extends Activity {

    DatabaseReference ref;

    EditText nameTextField, emailTextField;

    RecyclerView communitiesRecyclerView;
    RecyclerView.Adapter mCommunitiesAdapter;
    RecyclerView.LayoutManager mCommunitiesLayoutManager;

    ArrayList<String> communities = new ArrayList<>();

    ImageButton nextButton;

    public String intendedCommunity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_customeractivity2);

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
        nameTextField = findViewById(R.id.nameTextField);
        emailTextField = findViewById(R.id.emailTextField);

        communitiesRecyclerView = findViewById(R.id.communitiesRecyclerView);
        mCommunitiesLayoutManager = new LinearLayoutManager(this);
        mCommunitiesAdapter = new CommunityAdapter(communities, this);

        communitiesRecyclerView.setAdapter(mCommunitiesAdapter);
        communitiesRecyclerView.setLayoutManager(mCommunitiesLayoutManager);

        nextButton = findViewById(R.id.nextButton);
    }

    void setClickListener(){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userID = FirebaseAuth.getInstance().getUid();

                if (!nameTextField.getText().toString().equals("") && !emailTextField.getText().toString().equals("")
                    && !intendedCommunity.equals("")){
                    Log.i("sufficient", "true");

                    ref.child("customers").child(userID).child("contactName").setValue(nameTextField.getText().toString());
                    ref.child("customers").child(userID).child("contactEmail").setValue(emailTextField.getText().toString());

                    ref.child("customers").child(userID).child("didFinishSigningUp").setValue(true);
                    ref.child("customers").child(userID).child("banned").setValue(false);
                    ref.child("customers").child(userID).child("primaryCommunity").setValue(intendedCommunity);

                    //transition to main vendor activity

                    Intent i = new Intent(CustomerActivity2.this, CustomerMainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                } else {
                    Log.i("sufficient", "false");
                }
            }
        });
    }
}
