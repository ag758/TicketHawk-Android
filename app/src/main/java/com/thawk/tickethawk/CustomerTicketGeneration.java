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

        ref.child("vendors").child(vendorID).child("events").child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()){
                    return;
                }

                String eventTitle = (String)dataSnapshot.child("eventTitle").getValue();

                String dateAndTime = (String)dataSnapshot.child("startDateAndTime").getValue();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
                Date d1 = new Date();
                try {
                    d1 = simpleDateFormat.parse(dateAndTime);
                } catch (Exception e) {

                }
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MMM d, h:mm a", Locale.US);
                try {
                    dateAndTime = simpleDateFormat2.format(d1);
                } catch (Exception e) {

                }

                String location = (String)dataSnapshot.child("location").getValue();
                String dressCodeString = (String)dataSnapshot.child("dressCode").getValue();

                String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

                for (Map.Entry<TicketType, Object> e : map.entrySet()){

                    int countdown = (int)e.getValue();

                    while (countdown != 0){

                        String key = ref.child("vendors").child(vendorID).child("events").child(eventID).child("activeTickets").push().getKey();

                        HashMap<String, Object> ticket = new HashMap<>();

                        ticket.put("userName", userName);
                        ticket.put("title", eventTitle);
                        ticket.put("dateAndTime", dateAndTime);
                        ticket.put("location", location);
                        ticket.put("ticketType", e.getKey().name);
                        ticket.put("key", key);

                        ref.child("vendors").child(vendorID).child("events").child(eventID).child("activeTickets").child("key").setValue(ticket, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError firebaseError, DatabaseReference ref) {
                                if (firebaseError != null) {

                                } else {

                                }
                            }
                        });
                        }
                        ref.child("customers").child(FirebaseAuth.getInstance().getUid()).child("activeTickets").child("key").setValue(ticket);

                        countdown--;
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    void determineIfFinished(int quantity) {
        int totalQuantity = 0;
        for (Map.Entry<TicketType, Object> e: map.entrySet()){
            totalQuantity += (int)e.getValue();
        }

        if (quantity == totalQuantity * 2){

            new AlertDialog.Builder((CustomerTicketGeneration.this))
                    .setTitle("Your Purchase was Successful!")
                    .setMessage("View your new tickets in the 'My Tickets' tab.")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Pop to main customer activity

                            Intent i = new Intent(CustomerTicketGeneration.this, CustomerMainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }

    }
}
