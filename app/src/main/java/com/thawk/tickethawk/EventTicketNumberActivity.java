package com.thawk.tickethawk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.thawk.tickethawk.RecyclerClasses.EventModAdapter;
import com.thawk.tickethawk.RecyclerClasses.TicketTypeAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class EventTicketNumberActivity extends Activity {

    String vendorID, eventId;

    DatabaseReference ref;

    int fees = 0;
    int paymentTotalInt = 0;
    int paymentTotalWithoutTaxInt = 0;
    int purchaseQuantity = 0;

    ArrayList<TicketType> ticketTypes = new ArrayList<>();
    public HashMap<TicketType, Integer> map = new HashMap<>();

    RecyclerView ticketsRecyclerView;
    Button confirmPurchase;
    TextView feeTextView, subtotalTextView;

    RecyclerView.Adapter ticketsAdapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ref = FirebaseDatabase.getInstance().getReference();
        vendorID = getIntent().getStringExtra("vendorID");
        eventId = getIntent().getStringExtra("eventID");
        setContentView(R.layout.activity_ticket_number);

        findViews();

        loadTicketTypes();
    }

    void findViews(){
        ticketsRecyclerView = findViewById(R.id.ticket_recycler_view);
        confirmPurchase = findViewById(R.id.confirm_purchase);
        feeTextView = findViewById(R.id.event_processing_fee);
        subtotalTextView = findViewById((R.id.event_total));


        ticketsAdapter = new TicketTypeAdapter(ticketTypes, this);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        ticketsRecyclerView.setAdapter(ticketsAdapter);
        ticketsRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ticketsRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        ticketsRecyclerView.addItemDecoration(dividerItemDecoration);

    }

    void loadTicketTypes(){
        ref.child("vendors").child(vendorID).child("events").child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot ticketsSnapShot = dataSnapshot.child("ticketTypes");

                for (DataSnapshot dS : ticketsSnapShot.getChildren()){
                    String name = (String)dS.getKey();
                    Long l = (long)dS.getValue();
                    int price = l.intValue();

                    TicketType tt = new TicketType(name, price);
                    ticketTypes.add(tt);
                    map.put(tt, 0);

                    ticketsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

