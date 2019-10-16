package com.thawk.tickethawk;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

public class TwoDigitFormatWatcher implements TextWatcher {

    // Change this to what you want... ' ', '-' etc..
    private static final char space = ' ';

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() < 2){
            s.insert(0, '0' + "");
        }
    }
}
