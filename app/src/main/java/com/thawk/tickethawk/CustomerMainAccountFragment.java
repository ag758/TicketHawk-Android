package com.thawk.tickethawk;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thawk.tickethawk.BaseClasses.Event;
import com.thawk.tickethawk.BaseClasses.Vendor;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CustomerMainAccountFragment extends Fragment {

    Button communityButton, accountButton, logoutButton;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment

        View v = inflater.inflate(R.layout.fragment_account, parent, false);

        setOnClickListeners(v);

        return v;
    }

    void setOnClickListeners(View v){
        communityButton = v.findViewById(R.id.communityButton);
        accountButton = v.findViewById(R.id.accountButton);
        logoutButton = v.findViewById(R.id.logoutButton);

        communityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CommunityEditActivity.class);
                startActivity(i);
            }
        });

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AccountInfoActivity.class);
                startActivity(i);

            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getActivity(), SplitMainActivity.class);
                startActivity(i);

            }
        });
    }



}
