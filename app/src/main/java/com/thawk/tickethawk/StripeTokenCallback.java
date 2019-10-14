package com.thawk.tickethawk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.jetbrains.annotations.NotNull;

public class StripeTokenCallback implements ApiResultCallback{

    @Override
    public void onError(@NotNull Exception e) {

    }

    @Override
    public void onSuccess(Object o) {

    }
}