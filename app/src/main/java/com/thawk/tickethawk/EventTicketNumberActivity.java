package com.thawk.tickethawk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thawk.tickethawk.RecyclerClasses.EventModAdapter;
import com.thawk.tickethawk.RecyclerClasses.TicketTypeAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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

        confirmPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paymentTotalInt == 0){
                    new AlertDialog.Builder((EventTicketNumberActivity.this))
                            .setTitle("No Tickets")
                            .setMessage("You haven't selected any tickets.")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }

                ref.child("vendors").child(vendorID).child("events").child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int going = 0;
                        int maxIndivCapacity = Integer.MAX_VALUE;
                        int maxTotalCapacity = Integer.MAX_VALUE;

                        DataSnapshot maxIndivCapacityDS = dataSnapshot.child("maxTickets");
                        DataSnapshot goingDS = dataSnapshot.child("going");
                        DataSnapshot maxTotalCapacityDS = dataSnapshot.child("totalVenueCapacity");

                        if (maxIndivCapacityDS.getValue() != null){
                            maxIndivCapacity = ((Long)maxIndivCapacityDS.getValue()).intValue();
                        }

                        Log.i("purchase_info", String.valueOf(maxIndivCapacity));
                        Log.i("purchase_info", String.valueOf(purchaseQuantity));



                        if (purchaseQuantity > maxIndivCapacity){

                            new AlertDialog.Builder((EventTicketNumberActivity.this))
                                    .setTitle("Exceeds Individual Order Amount")
                                    .setMessage("The Vendor has set a limit of " + String.valueOf(maxIndivCapacity) + " tickets.")

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();


                        } else {

                            ref.child("vendors").child(vendorID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String accountID = (String)dataSnapshot.child("stripeAcctID").getValue();

                                    Intent i = new Intent(EventTicketNumberActivity.this, StripeActivity.class);

                                    // Put extras here
                                    i.putExtra("map", map);

                                    i.putExtra("purchaseQuantity", purchaseQuantity);

                                    i.putExtra("amount", paymentTotalInt);

                                    i.putExtra("vendorID", vendorID);
                                    i.putExtra("eventID", eventId);

                                    i.putExtra("withTax", paymentTotalInt);
                                    i.putExtra("withoutTax", paymentTotalWithoutTaxInt);

                                    i.putExtra("feeAmount", fees);
                                    i.putExtra("accountID", accountID);



                                    startActivity(i);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    public void updateTotalPrice(){
        calculateItemTotal();

        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        Double newfees = (Double.valueOf(this.fees)) / 100;
        Double newsubtotal = (Double.valueOf(this.paymentTotalInt)) / 100;

        this.feeTextView.setText("Processing Fee: " + formatter.format(newfees));
        this.subtotalTextView.setText("Total: " + formatter.format(newsubtotal));
    }

    void calculateItemTotal(){
        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        int total = 0;
        this.purchaseQuantity = 0;

        for ( Map.Entry<TicketType,Integer> e : map.entrySet()){

            int q = e.getValue();
            int p = e.getKey().price;

            this.purchaseQuantity = this.purchaseQuantity + q;

            total = total + p * q;

        }

        this.paymentTotalWithoutTaxInt = total;

        if (total > 0){
            this.fees = (int)(Math.ceil(  (Double.valueOf(total)* 0.15 ) + 70)  );
            this.paymentTotalInt = (this.fees) + total;
        } else {
            this.fees = 0;
            this.paymentTotalInt = 0;
        }
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

        ref.child("vendors").child(vendorID).child("events").child(eventId).child("ticketTypes").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String name = (String)dataSnapshot.getKey();

                ArrayList<TicketType> toBeRemoved = new ArrayList<>();

                for (TicketType t : (EventTicketNumberActivity.this).ticketTypes){
                    if ((t.name).equals(name)){
                        toBeRemoved.add(t);
                    }
                }

                ticketTypes.removeAll(toBeRemoved);
                ticketsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

