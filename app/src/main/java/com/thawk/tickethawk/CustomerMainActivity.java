package com.thawk.tickethawk;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.GoogleApiManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomerMainActivity extends FragmentActivity {

    FrameLayout fl;

    CustomerMainAccountFragment fMAF;
    CustomerMainBrowseFragment fMBF;
    CustomerMainTicketFragment fMTF;

    BottomNavigationView bNV;

    ImageButton archiveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FirebaseAuth.getInstance().signOut();

        //Main Page loaded by default -- transitions to splitcontroller if user is null
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user==null){
            Intent i = new Intent(this, SplitMainActivity.class);
            startActivity(i);
            return;
        }
        String userID = user.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("customers").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean didFinishProfile = (boolean)(dataSnapshot.child("didFinishSigningUp").getValue());

                if (!didFinishProfile){
                    FirebaseAuth.getInstance().signOut();
                    Intent i = new Intent(CustomerMainActivity.this, SplitMainActivity.class);
                    startActivity(i);
                    return;
                } else {
                    onCreateContinue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void onCreateContinue(){


        //

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        setContentView(R.layout.activity_customermainactivity);
        findViews();

        fMBF = new CustomerMainBrowseFragment();
        fMAF = new CustomerMainAccountFragment();
        fMTF = new CustomerMainTicketFragment();

        replaceFragment(fMBF);
        setBottomNavListeners();
        fl.setLayoutParams(new ConstraintLayout.LayoutParams(width,(int)(height * 1.2)));
    }

    @Override
    protected void onResume(){
        super.onResume();

        //fMBF.loadCommunity();
    }

    void findViews(){
        bNV = findViewById(R.id.bottomNavigationView);
        fl = findViewById(R.id.frame_layout);
        archiveButton = findViewById(R.id.archive_button);
        archiveButton.setVisibility(View.INVISIBLE);

        archiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CustomerMainActivity.this, CustomerArchiveTicketActivity.class);
                startActivity(i);
            }
        });
    }

    public void replaceFragment(Fragment f){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, f);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    void setBottomNavListeners(){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_search:
                        Log.i("menu", "search");
                        replaceFragment(fMBF);
                        fl.setLayoutParams(new ConstraintLayout.LayoutParams(width,(int)(height * 1.2)));
                        archiveButton.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.menu_tickets:
                        Log.i("menu", "tickets");
                        replaceFragment(fMTF);
                        archiveButton.setVisibility(View.VISIBLE);
                        fl.setLayoutParams(new ConstraintLayout.LayoutParams(width,(int)(height) - bNV.getMeasuredHeight() - 60));
                        break;
                    case R.id.menu_account:
                        Log.i("menu", "account");
                        replaceFragment(fMAF);
                        fl.setLayoutParams(new ConstraintLayout.LayoutParams(width,(int)(height) - bNV.getMeasuredHeight() - 60));
                        archiveButton.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        break;
                }
                return true;
            }
        };
        this.bNV.setOnNavigationItemSelectedListener(listener);
    }

    public void transitionToVendorList(String vendorID){
        Intent i = new Intent(CustomerMainActivity.this, CustomerVendorListActivity.class);
        i.putExtra("vendorID", vendorID);
        startActivity(i);
    }

    public void transitionToCustomerEvent(String vendorID, String eventID){
        Intent i = new Intent(CustomerMainActivity.this, EventActivity.class);
        i.putExtra("vendorID", vendorID);
        i.putExtra("eventID", eventID);
        startActivity(i);
    }
}
