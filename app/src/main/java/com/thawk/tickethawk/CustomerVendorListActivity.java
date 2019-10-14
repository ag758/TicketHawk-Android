package com.thawk.tickethawk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thawk.tickethawk.BaseClasses.Event;
import com.thawk.tickethawk.RecyclerClasses.EventAdapter;
import com.thawk.tickethawk.RecyclerClasses.EventModAdapter;
import com.thawk.tickethawk.RecyclerClasses.VendorAdapter;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CustomerVendorListActivity extends Activity {

    DatabaseReference ref;
    String vendorID;

    ImageView vendorImageView;
    TextView vendorName, phoneNumber, email;
    Button reportButton;

    RecyclerView eventsRecyclerView;
    RecyclerView.Adapter mEventsAdapter;
    RecyclerView.LayoutManager mEventsLayoutManager;

    ArrayList<Event> loadedEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();

        vendorID = getIntent().getStringExtra("vendorID");
        setContentView(R.layout.activity_customervendorlist);

        findViews();
        setDefaults();
        loadEvents();
    }

    void findViews(){
        vendorImageView = findViewById(R.id.vendor_image_heading);
        vendorName = findViewById(R.id.vendor_name_heading);
        phoneNumber = findViewById(R.id.phone_number);
        email = findViewById(R.id.email_view);
        reportButton = findViewById(R.id.reportButton);

        eventsRecyclerView = findViewById(R.id.events_recycler_view);

        mEventsAdapter = new EventModAdapter(loadedEvents, this);
        mEventsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        eventsRecyclerView.setAdapter(mEventsAdapter);
        eventsRecyclerView.setLayoutManager(mEventsLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(eventsRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        eventsRecyclerView.addItemDecoration(dividerItemDecoration);

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.howToReportURL)));
                startActivity(browserIntent);
            }
        });
    }

    void setDefaults(){
        ref.child("vendors").child(this.vendorID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String orgName = (String)dataSnapshot.child("organizationName").getValue();
                vendorName.setText(orgName);

                String url = (String)dataSnapshot.child("organizationProfileImage").getValue();
                Uri u = Uri.parse(url);
                Picasso.get().load(u).into((CustomerVendorListActivity.this).vendorImageView);

                phoneNumber.setText((String)dataSnapshot.child("custSuppPhoneNumber").getValue());
                email.setText((String)dataSnapshot.child("custSupportEmail").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadEvents(){

        ref.child("vendors").child(this.vendorID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String ownName = (String)dataSnapshot.child("organizationName").getValue();

                ArrayList<String> allKeys = new ArrayList<>();
                DataSnapshot eventsSnapshot = dataSnapshot.child("events");
                for (DataSnapshot dS : eventsSnapshot.getChildren()){
                    allKeys.add(dS.getKey());
                }

                for (String k : allKeys){
                    DataSnapshot event = dataSnapshot.child("events").child(k);
                    String title = (String)event.child("eventTitle").getValue();
                    String startDateAndTime = (String)event.child("startDateAndTime").getValue();
                    String pictureURL = (String)event.child("pictureURL").getValue();

                    HashMap<String, Double> ticketTypes = new HashMap<>();

                    for (DataSnapshot d : event.child("ticketTypes").getChildren()){
                        Log.i("debug_main_cust", d.getKey() + " " + String.valueOf(d.getValue()));
                        ticketTypes.put(d.getKey(), ((Long)d.getValue()).doubleValue());
                    }
                    Double minimumprice = Double.MAX_VALUE;

                    for ( HashMap.Entry<String, Double> e : ticketTypes.entrySet()){

                        Log.i("debug_main_cust", String.valueOf(Double.valueOf(e.getValue())));
                        Log.i("debug_main_cust", String.valueOf(minimumprice));

                        if (Double.valueOf(e.getValue()) / 100 < minimumprice){
                            minimumprice = Double.valueOf(e.getValue()) / 100;
                        }
                    }

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
                    Date d1 = new Date();
                    try {
                        d1 = simpleDateFormat.parse(startDateAndTime);
                    } catch (Exception e) {

                    }
                    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MMM d, h:mm a", Locale.US);
                    try {
                        startDateAndTime = simpleDateFormat2.format(d1);
                    } catch (Exception e) {

                    }

                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                    String number = formatter.format(minimumprice);

                    Event eventInstance = new Event(title, startDateAndTime, number, pictureURL, k, vendorID, ownName);
                    if (d1.after(new Date())){
                        (CustomerVendorListActivity.this).loadedEvents.add(eventInstance);
                        loadedEvents = sortByTime(loadedEvents);
                        mEventsAdapter.notifyDataSetChanged();
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<Event> sortByTime(ArrayList<Event> events){
        for (int idx = 0; idx < events.size(); idx++){
            int closestInt = idx;

            for (int idy = idx+1; idy < events.size(); idy++){
                if (compareDates(events.get(idx).dateAndTime, events.get(idy).dateAndTime)){
                    closestInt = idy;
                }
            }

            Event one = events.get(idx);
            Event two = events.get(closestInt);

            events.set(closestInt, one);
            events.set(idx, two);

        }


        return events;
    }

    public boolean compareDates(String date1, String date2){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        Date d1 = new Date();
        Date d2 = new Date();
        try {
            d1 = simpleDateFormat.parse(date1);
            d2 = simpleDateFormat.parse(date2);
        } catch (Exception e) {

        }

        if (d1.after(d2)){
            return true;
        }
        return false;
    }

    public void transitionToCustomerEvent(String vendorID, String eventID){
        Intent i = new Intent(CustomerVendorListActivity.this, EventActivity.class);
        i.putExtra("vendorID", vendorID);
        i.putExtra("eventID", eventID);
        startActivity(i);
    };

}
