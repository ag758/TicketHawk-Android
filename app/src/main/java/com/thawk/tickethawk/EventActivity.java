package com.thawk.tickethawk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventActivity extends Activity {

    public ImageView eventImageView, vendorImageView;
    public TextView eventTitle, vendorName, dateAndTime, location, dressCode, going, description;
    Button purchaseTickets;

    DatabaseReference ref;

    String vendorID, eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();

        vendorID = getIntent().getStringExtra("vendorID");
        eventID = getIntent().getStringExtra("eventID");


        setContentView(R.layout.activity_customerevent);
        findViews();
        loadEventDetails();
    }

    void findViews(){
        eventImageView = findViewById(R.id.event_image);
        vendorImageView = findViewById(R.id.vendor_image);
        eventTitle = findViewById(R.id.event_title);
        vendorName = findViewById(R.id.vendor_name);
        dateAndTime = findViewById(R.id.date_and_time);
        location = findViewById(R.id.location_event);
        dressCode = findViewById(R.id.dress_code_event);
        going = findViewById(R.id.going_view);
        description = findViewById(R.id.description_event);
        purchaseTickets = findViewById(R.id.purchaseTickets);

        purchaseTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EventActivity.this, EventTicketNumberActivity.class);
                i.putExtra("vendorID", vendorID);
                i.putExtra("eventID", eventID);
                startActivity(i);
            }
        });
    }

    void loadEventDetails(){
        ref.child("vendors").child(vendorID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String vendorName = (String)dataSnapshot.child("organizationName").getValue();
                (EventActivity.this).vendorName.setText(vendorName);

                String url = (String)dataSnapshot.child("organizationProfileImage").getValue();
                Uri u = Uri.parse(url);
                Picasso.get().load(u).into((EventActivity.this).vendorImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.child("vendors").child(this.vendorID).child("events").child(this.eventID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String url = (String)dataSnapshot.child("pictureURL").getValue();
                Uri u = Uri.parse(url);
                Picasso.get().load(u).into((EventActivity.this).eventImageView);

                eventTitle.setText((String)dataSnapshot.child("eventTitle").getValue());

                String startDateAndTime = (String)dataSnapshot.child("startDateAndTime").getValue();
                String endDateAndTime = (String)dataSnapshot.child("endDateAndTime").getValue();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
                Date d1 = new Date();
                Date d2 = new Date();
                try {
                    d1 = simpleDateFormat.parse(startDateAndTime);
                    d2 = simpleDateFormat.parse(endDateAndTime);
                } catch (Exception e) {

                }
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MMM d, h:mm a", Locale.US);
                try {
                    startDateAndTime = simpleDateFormat2.format(d1);
                    endDateAndTime = simpleDateFormat2.format(d2);
                } catch (Exception e) {

                }

                (EventActivity.this).dateAndTime.setText(startDateAndTime + " to " + endDateAndTime);
                (EventActivity.this).location.setText((String)dataSnapshot.child("location").getValue());

                String dressCode = "No Dress Code";
                String dC = (String)dataSnapshot.child("dressCode").getValue();
                if (dC == null || dC.equals("")){

                } else {
                    dressCode = dC;
                }

                (EventActivity.this).dressCode.setText(dressCode);

                Long l = (Long)dataSnapshot.child("going").getValue();
                int i = 0;
                try {
                   i=  l.intValue();
                } catch (Exception e){

                }

                (EventActivity.this).going.setText(String.valueOf(i) + " going");
                (EventActivity.this).description.setText((String)dataSnapshot.child("description").getValue());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
