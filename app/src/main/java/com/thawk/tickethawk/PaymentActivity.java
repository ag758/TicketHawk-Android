package com.thawk.tickethawk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;
import com.stripe.android.PaymentAuthConfig;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.Address;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.ShippingMethod;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.HashMap;

public class PaymentActivity extends Activity {
    private Stripe mStripe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Optional: customize the payment authentication experience.
        // PaymentAuthConfig.init() must be called before Stripe object
        // is instantiated.
        final PaymentAuthConfig.Stripe3ds2UiCustomization uiCustomization =
                new PaymentAuthConfig.Stripe3ds2UiCustomization.Builder()
                        .build();
        PaymentAuthConfig.init(new PaymentAuthConfig.Builder()
                .set3ds2Config(new PaymentAuthConfig.Stripe3ds2Config.Builder()
                        // set a 5 minute timeout for challenge flow
                        .setTimeout(5)
                        // customize the UI of the challenge flow
                        .setUiCustomization(uiCustomization)
                        .build())
                .build());

        PaymentConfiguration.init(this, getResources().getString(R.string.publishableKey));
        mStripe = new Stripe(this,
                PaymentConfiguration.getInstance(this).getPublishableKey());

        // now retrieve the PaymentIntent that was created on your backend
    }

    private void confirmPayment(@NonNull ConfirmPaymentIntentParams params) {
        mStripe.confirmPayment(this, params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mStripe.onPaymentResult(requestCode, data,
                new ApiResultCallback<PaymentIntentResult>() {
                    @Override
                    public void onSuccess(@NonNull PaymentIntentResult result) {
                        // If authentication succeeded, the PaymentIntent will
                        // have user actions resolved; otherwise, handle the
                        // PaymentIntent status as appropriate (e.g. the
                        // customer may need to choose a new payment method)

                        final PaymentIntent paymentIntent = result.getIntent();
                        final PaymentIntent.Status status =
                                paymentIntent.getStatus();
                        if (status == PaymentIntent.Status.Succeeded) {
                            // show success UI
                        } else if (PaymentIntent.Status.RequiresPaymentMethod
                                == status) {
                            // attempt authentication again or
                            // ask for a new Payment Method
                        }
                    }

                    @Override
                    public void onError(@NonNull Exception e) {
                        // handle error
                    }
                });
    }
}