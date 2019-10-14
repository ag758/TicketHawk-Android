package com.thawk.tickethawk;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.Address;
import com.stripe.android.model.ShippingMethod;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.HashMap;

public class StripeApiClient extends Activity {

    private Stripe mStripe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaymentConfiguration.init(this, getResources().getString(R.string.publishableKey));
        mStripe = new Stripe(this,
                PaymentConfiguration.getInstance(this).getPublishableKey());

        myBackendApiClient.createPaymentIntent(100, "usd", "off_session",
                new ApiResultCallback<String>() {
                    @Override
                    public void onSuccess(@NonNull String clientSecret) {
                        // Hold onto the clientSecret for Step 4
                    }

                    @Override
                    public void onError(@NonNull Exception e) {
                    }
                });

    }

}
