package com.thawk.tickethawk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CustomerTicketGeneration extends Activity {

    DatabaseReference ref;

    HashMap<TicketType, Object> map = new HashMap<>();
    String vendorID = "";
    String eventID = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();

        map = (HashMap<TicketType,Object>)getIntent().getSerializableExtra("map");
        vendorID = getIntent().getStringExtra("vendorID");
        eventID = getIntent().getStringExtra("eventID");

        setContentView(R.layout.activity_ticket_generation);

    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        //do nothing
    }


}
