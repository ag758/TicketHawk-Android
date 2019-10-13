package com.thawk.tickethawk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class CustomerActivity1 extends Activity {

    DatabaseReference ref;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ref = SplitMainActivity.ref;

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_customeractivity1);

        checkLoggedIn();
    }

    void checkLoggedIn(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.i("userstatus", "start");
        if(user!=null){
            Log.i("userstatus", "nonnull");
            customerLogin();
        } else {
            Log.i("userstatus", "null");
            attemptLogin();
        }
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Log.i("userstatus", "start");
        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public  void  onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.i("userstatus", "start");
                if(user!=null){
                    Log.i("userstatus", "nonnull");
                    customerLogin();
                } else {
                    Log.i("userstatus", "null");
                    attemptLogin();
                }
            }
        };
    }

    void attemptLogin(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    void customerLogin(){

    }


}
