package com.thawk.tickethawk;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomerMainActivity extends FragmentActivity {

    FrameLayout fl;

    CustomerMainAccountFragment fMAF = new CustomerMainAccountFragment();
    CustomerMainBrowseFragment fMBF = new CustomerMainBrowseFragment();
    CustomerMainTicketFragment fMTF = new CustomerMainTicketFragment();

    BottomNavigationView bNV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customermainactivity);
        findViews();
        //
        setBottomNavListeners();
        replaceFragment(fMBF);
    }

    void findViews(){
        bNV = findViewById(R.id.bottomNavigationView);
    }

    void replaceFragment(Fragment f){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.frame_layout, f);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    void setBottomNavListeners(){
        BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_search:
                        Log.i("menu", "search");
                        replaceFragment(fMBF);
                        break;
                    case R.id.menu_tickets:
                        Log.i("menu", "tickets");
                        replaceFragment(fMTF);
                        break;
                    case R.id.menu_account:
                        Log.i("menu", "account");
                        replaceFragment(fMAF);
                        break;
                    default:
                        break;
                }
                return true;
            }
        };
        this.bNV.setOnNavigationItemSelectedListener(listener);
    }
}
