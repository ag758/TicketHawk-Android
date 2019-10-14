package com.thawk.tickethawk;

import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thawk.tickethawk.BaseClasses.Event;
import com.thawk.tickethawk.BaseClasses.Vendor;
import com.thawk.tickethawk.RecyclerClasses.EventAdapter;
import com.thawk.tickethawk.RecyclerClasses.VendorAdapter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CustomerMainBrowseFragment extends Fragment {

    String communityKey = "";
    DatabaseReference ref;
    ArrayList<Vendor> vendors = new ArrayList<>();
    ArrayList<Event> loadedEvents = new ArrayList<>();
    ArrayList<String> loadedEventsStringIDs = new ArrayList<>();
    ArrayList<Vendor> filteredVendors = new ArrayList<>();

    RecyclerView eventsRecyclerView, vendorsRecyclerView;
    RecyclerView.Adapter mEventsAdapter, mVendorsAdapter;
    RecyclerView.LayoutManager mEventsLayoutManager, mVendorsLayoutManager;

    Button communityTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        super.onCreateView(inflater, parent, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_browse, parent, false);
        ref = FirebaseDatabase.getInstance().getReference();

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        findViews(view);
        loadCommunity();

        communityTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Vendor e: filteredVendors){
                    Log.i("debug_main_cust", e.pictureURL);
                }
            }
        });

    }

    void findViews(View v){

        communityTitle = v.findViewById(R.id.button2);
        eventsRecyclerView = v.findViewById(R.id.recyclerView);
        vendorsRecyclerView = v.findViewById(R.id.recyclerView2);

        mEventsAdapter = new EventAdapter(loadedEvents, getContext());
        mVendorsAdapter = new VendorAdapter(filteredVendors, getContext());

        mEventsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mVendorsLayoutManager = new LinearLayoutManager(getContext());

        eventsRecyclerView.setAdapter(mEventsAdapter);
        eventsRecyclerView.setLayoutManager(mEventsLayoutManager);

        vendorsRecyclerView.setAdapter(mVendorsAdapter);
        vendorsRecyclerView.setLayoutManager(mVendorsLayoutManager);

    }

    void loadCommunity(){
        //this.vendors = new ArrayList<>();
        //this.loadedEvents = new ArrayList<>();
        //this.loadedEventsStringIDs = new ArrayList<>();
        //this.filteredVendors = new ArrayList<>();

        this.mVendorsAdapter.notifyDataSetChanged();
        this.mEventsAdapter.notifyDataSetChanged();

        String userID = FirebaseAuth.getInstance().getUid();
        ref.child("customers").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String cKey = (String)(dataSnapshot.child("primaryCommunity").getValue());
                (CustomerMainBrowseFragment.this).communityKey = cKey;
                communityTitle.setText(communityKey);

                loadCommunityVendorIDS(cKey);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void loadCommunityVendorIDS(String communityKey){
        DatabaseReference query = ref.child("communities").child(communityKey).child("vendors");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    final String k = d.getKey();

                    //new Thread(new Runnable() {
                        //public void run() {
                            chooseEventFromVendor(k);
                        //}
                    //}).start();

                    //new Thread(new Runnable() {
                        //public void run() {
                            loadCommunityVendorDetails(k);
                        //}
                    //}).start();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                String i = (String)dataSnapshot.child("id").getValue();

                ArrayList<Vendor> toBeRemoved = new ArrayList<>();
                for (Vendor v: vendors){
                    if (v.id.equals(i)){
                        toBeRemoved.add(v);
                    }
                }
                vendors.removeAll(toBeRemoved);
                mVendorsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void chooseEventFromVendor(final String k){
        ref.child("vendors").child(k).child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<String> keys = new ArrayList<>();
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    String k = d.getKey();
                    keys.add(k);
                }

                int i = (int)(Math.random() * (keys.size()));

                String eventId = keys.get(i);

                boolean isAlreadyAdded = false;

                for (String k : loadedEventsStringIDs){
                    if (k.equals(eventId) ){
                        isAlreadyAdded = true;
                    }
                }

                if (!isAlreadyAdded){
                    loadedEventsStringIDs.add(eventId);
                    loadEvent(k, eventId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void loadEvent(final String vendorID, final String eventID){

        ref.child("vendors").child(vendorID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String vendorName =(String) dataSnapshot.child("organizationName").getValue();
                DataSnapshot eventSnapshot = dataSnapshot.child("events").child(eventID);

                String title = (String)eventSnapshot.child("eventTitle").getValue();
                String startDateAndTime = (String)eventSnapshot.child("startDateAndTime").getValue();
                String pictureURL = (String)eventSnapshot.child("pictureURL").getValue();


                Log.i("debug_main_cust", "start");

                HashMap<String, Double> ticketTypes = new HashMap<>();

                Log.i("debug_main_cust", String.valueOf(eventSnapshot.child("ticketTypes").getChildrenCount()));

                for (DataSnapshot d : eventSnapshot.child("ticketTypes").getChildren()){
                    Log.i("debug_main_cust", d.getKey() + " " + String.valueOf(d.getValue()));
                    ticketTypes.put(d.getKey(), ((Long)d.getValue()).doubleValue());
                }

                String id = (String)eventSnapshot.child("key").getValue();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
                Date d1 = new Date();
                try {
                     d1 = simpleDateFormat.parse(startDateAndTime);
                } catch (Exception e) {

                }
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MMM d, h:mm a", Locale.US);
                try {
                    startDateAndTime = simpleDateFormat.format(d1);
                } catch (Exception e) {

                }

                Double minimumprice = Double.MAX_VALUE;

                for ( HashMap.Entry<String, Double> e : ticketTypes.entrySet()){

                    Log.i("debug_main_cust", String.valueOf(Double.valueOf(e.getValue())));
                    Log.i("debug_main_cust", String.valueOf(minimumprice));

                    if (Double.valueOf(e.getValue()) / 100 < minimumprice){
                        minimumprice = Double.valueOf(e.getValue()) / 100;
                    }
                }

                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                String number = formatter.format(minimumprice);

                Event eventInstance = new Event(title, startDateAndTime, number, pictureURL, id, vendorID, vendorName);

                Date endDate = new Date();
                try {
                    endDate = simpleDateFormat.parse((String)eventSnapshot.child("endDateAndTime").getValue());
                } catch (Exception e){

                }

                if (endDate.after(new Date())){
                    System.out.println("jellyfish");
                }

                Log.i("debug_amount", "events_size" + " " + String.valueOf(loadedEvents.size()));

                loadedEvents = randomAppend(loadedEvents, eventInstance);

                mEventsAdapter.notifyDataSetChanged();

                Log.i("debug_amount", "events_size" + " " + String.valueOf(loadedEvents.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void loadCommunityVendorDetails(final String k){
        ref.child("vendors").child(k).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String orgName = (String)dataSnapshot.child("organizationName").getValue();
                String pictureURL = (String)dataSnapshot.child("organizationProfileImage").getValue();
                String ticketCategory = (String)dataSnapshot.child("ticketCategory").getValue();
                Vendor vendorToBeAdded = new Vendor(k, orgName, pictureURL, ticketCategory);

                vendors = randomAppend(vendors, vendorToBeAdded);

                filteredVendors = vendors;
                mVendorsAdapter.notifyDataSetChanged();

                Log.i("debug_amount", "vendors_size" + " " + String.valueOf(filteredVendors.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public <T> ArrayList<T> randomAppend(ArrayList<T> a, T b){
        a.add((int)(Math.random()*(a.size() + 1)), b);
        return a;
    }
}
