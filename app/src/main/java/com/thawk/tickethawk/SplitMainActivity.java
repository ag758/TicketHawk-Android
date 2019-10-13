package com.thawk.tickethawk;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplitMainActivity extends Activity {

    DatabaseReference ref;

    Button customerButton, vendorButton, tosPressed, privacyPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("GGNORE");

        ref = FirebaseDatabase.getInstance().getReference();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        this.findViews();
        this.setOnClickListeners();
    }

    public void findViews(){
        customerButton = findViewById(R.id.customerButton);
        vendorButton = findViewById(R.id.vendorButton);
        tosPressed = findViewById(R.id.tosButton);
        privacyPressed = findViewById(R.id.privacyButton);
    }
    public void setOnClickListeners(){
        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        vendorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        tosPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.termsOfServiceURL)));
                startActivity(browserIntent);
            }
        });
        privacyPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacyPolicyURL)));
                startActivity(browserIntent);
            }
        });
    }
}
