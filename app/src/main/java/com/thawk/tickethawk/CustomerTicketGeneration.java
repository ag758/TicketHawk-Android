package com.thawk.tickethawk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
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
