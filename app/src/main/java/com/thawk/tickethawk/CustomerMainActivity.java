package com.thawk.tickethawk;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomerMainActivity extends FragmentActivity {

    FrameLayout fl;

    CustomerMainAccountFragment fMAF;
    CustomerMainBrowseFragment fMBF;
    CustomerMainTicketFragment fMTF;

    BottomNavigationView bNV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    void findViews(){
        bNV = findViewById(R.id.bottomNavigationView);
        fl = findViewById(R.id.frame_layout);
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
                        break;
                    case R.id.menu_tickets:
                        Log.i("menu", "tickets");
                        replaceFragment(fMTF);
                        fl.setLayoutParams(new ConstraintLayout.LayoutParams(width,(int)(height) - bNV.getMeasuredHeight() - 60));
                        break;
                    case R.id.menu_account:
                        Log.i("menu", "account");
                        replaceFragment(fMAF);
                        fl.setLayoutParams(new ConstraintLayout.LayoutParams(width,(int)(height) - bNV.getMeasuredHeight() - 60));
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
