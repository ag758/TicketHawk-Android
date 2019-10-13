package com.thawk.tickethawk;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class CustomerActivity2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_customeractivity2);
    }
}
