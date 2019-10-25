package com.thawk.tickethawk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerActivity1 extends Activity {

    DatabaseReference ref;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_customeractivity1);

        checkLoggedIn();
    }

    @Override
    public void onResume(){
        super.onResume();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Log.i("userstatus", "nonnull");
            customerLogin();
        }

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
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final String userID = user.getUid();
        final String userName = user.getDisplayName();

        ref = FirebaseDatabase.getInstance().getReference();

        ref.child("customers").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                boolean snapshotIsNull = snapshot.child("didFinishSigningUp").getValue() == null;
                boolean didFinishProfile = false;
                if (!snapshotIsNull){
                    didFinishProfile = (boolean)(snapshot.child("didFinishSigningUp").getValue());
                }

                Log.i("condition1", String.valueOf(snapshotIsNull));
                Log.i("condition2", String.valueOf(didFinishProfile));
                if (snapshotIsNull || !didFinishProfile){
                    ref.child("customers").child(userID).child("contactName").setValue(userName);
                    ref.child("customers").child(userID).child("contactEmail").setValue(user.getEmail());
                    ref.child("customers").child(userID).child("didFinishSigningUp").setValue(false);

                    //continue editing profile...
                    Intent i = new Intent(CustomerActivity1.this, CustomerActivity2.class);
                    startActivity(i);
                } else {
                    //Transition to main customer activity

                    Intent i = new Intent(CustomerActivity1.this, CustomerMainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                }
            }
            @Override
            public void onCancelled(DatabaseError e) {
            }
        });
    }


}
