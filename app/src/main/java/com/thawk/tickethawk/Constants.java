package com.thawk.tickethawk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constants{

    public static DatabaseReference ref;

    public Constants(){
        ref = FirebaseDatabase.getInstance().getReference();
    }

}
