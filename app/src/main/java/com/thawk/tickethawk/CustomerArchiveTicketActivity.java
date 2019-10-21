package com.thawk.tickethawk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thawk.tickethawk.RecyclerClasses.ArchiveAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CustomerArchiveTicketActivity extends Activity {

    public static DatabaseReference ref;

    RecyclerView archiveRecyclerView;
    RecyclerView.Adapter mArchiveRecyclerViewAdapter;
    RecyclerView.LayoutManager mArchiveLayoutManager;

    ArrayList<Ticket> archivedTickets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_archivetickets);

        findViews();
        loadTickets();
    }

    public void findViews(){
        archiveRecyclerView = findViewById(R.id.archive_recyclerview);
        mArchiveRecyclerViewAdapter = new ArchiveAdapter(archivedTickets, this);
        mArchiveLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        archiveRecyclerView.setAdapter(mArchiveRecyclerViewAdapter);
        archiveRecyclerView.setLayoutManager(mArchiveLayoutManager);
    }

    public void loadTickets(){
        Log.i("ticket_info", String.valueOf(archivedTickets.size()));
        archivedTickets.removeAll(archivedTickets);
        //tickets = new ArrayList<>();

        DatabaseReference query = ref.child("customers").child(FirebaseAuth.getInstance().getUid()).child("archivedTickets");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = (String)dataSnapshot.child("key").getValue();
                String eventTitle = (String)dataSnapshot.child("title").getValue();
                String dateAndTime = (String)dataSnapshot.child("dateAndTime").getValue();
                String ticketType = (String)dataSnapshot.child("ticketType").getValue();
                String userName = (String)dataSnapshot.child("userName").getValue();
                String location = (String)dataSnapshot.child("location").getValue();

                Ticket t = new Ticket(key, eventTitle, ticketType, userName, dateAndTime, location);
                appendAfterDate(t);

                Log.i("ticket_info", "one added");
                //notify recycler view

                mArchiveRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                String key = (String)dataSnapshot.child("key").getValue();

                ArrayList<Ticket> toBeRemoved = new ArrayList<>();
                for (Ticket t : archivedTickets){
                    if (t.key.equals(key)) {
                        toBeRemoved.add(t);
                    }
                }
                archivedTickets.removeAll(toBeRemoved);
                mArchiveRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void appendAfterDate(Ticket t){
        int index = 0;
        while (index < archivedTickets.size()){

            if (compareDates(archivedTickets.get(index).dateAndTime, t.dateAndTime)){
                index++;
            } else {
                break;
            }

        }
        archivedTickets.add(index, t);
    }

    boolean compareDates(String date1, String date2){
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy h:mm a", Locale.US);

        Date d1 = new Date();
        Date d2 = new Date();
        try {
            d1 = sdf.parse(date1);
            d2 = sdf.parse(date2);
        } catch (Exception e){

        }
        if (d1.after(d2)){
            return true;
        }
        return true;
    }
}
