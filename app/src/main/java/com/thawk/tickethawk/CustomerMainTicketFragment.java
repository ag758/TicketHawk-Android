package com.thawk.tickethawk;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thawk.tickethawk.RecyclerClasses.TicketAdapter;
import com.thawk.tickethawk.RecyclerClasses.VendorAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CustomerMainTicketFragment extends Fragment {

    DatabaseReference ref;

    ArrayList<Ticket> tickets = new ArrayList<>();

    RecyclerView ticketView;
    RecyclerView.Adapter mTicketAdapter;
    RecyclerView.LayoutManager mTicketLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment

        View v = inflater.inflate(R.layout.fragment_ticket, parent, false);

        ref = FirebaseDatabase.getInstance().getReference();

        findViews(v);

        loadTickets();


        return v;
    }

    public void findViews(View v){
        ticketView = v.findViewById(R.id.ticketRecyclerView);

        mTicketAdapter = new TicketAdapter(tickets, getContext(), getActivity());
        mTicketLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        ticketView.setAdapter(mTicketAdapter);
        ticketView.setLayoutManager(mTicketLayoutManager);
    }

    public void loadTickets(){
        Log.i("ticket_info", String.valueOf(tickets.size()));
        tickets.removeAll(tickets);
        //tickets = new ArrayList<>();

        DatabaseReference query = ref.child("customers").child(FirebaseAuth.getInstance().getUid()).child("activeTickets");

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

                mTicketAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

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
        while (index < tickets.size()){

            if (compareDates(tickets.get(index).dateAndTime, t.dateAndTime)){
                index++;
            } else {
                break;
            }

        }
        tickets.add(index, t);
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
