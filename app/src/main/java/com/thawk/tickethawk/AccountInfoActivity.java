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
import com.google.firebase.database.ValueEventListener;
import com.thawk.tickethawk.RecyclerClasses.CommunityAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class AccountInfoActivity extends Activity {

    DatabaseReference ref;

    EditText nameTextField, emailTextField;

    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ref = SplitMainActivity.ref;

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_accountinfo);

        findViews();
        setClickListener();
        setDefaults();
    }

    void findViews(){
        nameTextField = findViewById(R.id.nameTextField);
        emailTextField = findViewById(R.id.emailTextField);

        saveButton = findViewById(R.id.nextButton);
    }

    void setDefaults(){
        String userID = FirebaseAuth.getInstance().getUid();

        ref.child("customers").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameTextField.setText((String)dataSnapshot.child("contactName").getValue());
                emailTextField.setText((String)dataSnapshot.child("contactEmail").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void setClickListener(){
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userID = FirebaseAuth.getInstance().getUid();

                if (!nameTextField.getText().toString().equals("") && !emailTextField.getText().toString().equals("")
                   ){

                    ref.child("customers").child(userID).child("contactName").setValue(nameTextField.getText().toString());
                    ref.child("customers").child(userID).child("contactEmail").setValue(emailTextField.getText().toString());


                   finish();

                } else {
                    Log.i("sufficient", "false");
                }
            }
        });
    }
}
