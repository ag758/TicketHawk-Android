package com.thawk.tickethawk;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.Token;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.URI;

public class StripeActivity extends Activity {

    Stripe stripe;
    Integer amount;
    String name;
    Card card;
    Token tok;

    public String pKey;

    StripeActivity sA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe);

        stripe = new Stripe(this, getResources().getString(R.string.publishableKey));
    }

    public void submitCard(View v, StripeActivity sB){
        TextView cardNumberField = (TextView) findViewById(R.id.cardNumber);
        TextView monthField = (TextView) findViewById(R.id.month);
        TextView yearField = (TextView) findViewById(R.id.year);
        TextView cvcField = (TextView) findViewById(R.id.cvc);



        this.sA = sB;


        card = new Card.Builder(
                cardNumberField.getText().toString(),
                Integer.valueOf(monthField.getText().toString()),
                Integer.valueOf(yearField.getText().toString()),
                cvcField.getText().toString()
        ).build();



        stripe.createToken(card, getResources().getString(R.string.publishableKey), new StripeTokenCallback() {


            public void onSuccess(Token token) {
                // TODO: Send Token information to your backend to initiate a charge
                Toast.makeText(getApplicationContext(), "Token created: " + token.getId(), Toast.LENGTH_LONG).show();

                tok = token;
                new StripeCharge(token.getId(), pKey).execute();
            }

            public void onError(Exception error) {
                Log.d("Stripe", error.getLocalizedMessage());
            }
        });
    }




}

public class StripeCharge extends AsyncTask<String, Void, String> {
    String token;
    StripeActivity sA;

    public StripeCharge(String token, StripeActivity sA) {
        this.token = token;
        this.sA = sA;
    }

    @Override
    protected String doInBackground(String... params) {
        new Thread() {
            @Override
            public void run() {
                postData(name,token,""+amount);
            }
        }.start();
        return "Done";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.e("Result",s);
    }

    public void postData(String description, String token,String amount) {
        // Create a new HttpClient and Post Header
        try {
            URL url;
            try {
                String pk = (sA).getResources().getString(R.string.publishableKey);
                url = new URL(pk);
            } catch (Exception e){
                url = new URL("");
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            try {
                conn.setRequestMethod("POST");
            } catch (Exception e){

            }

            conn.setDoInput(true);
            conn.setDoOutput(true);

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("method", "charge");
            params.put("description", description);
            params.put("source", token);
            params.put("amount", amount);

            OutputStream os = null;

            os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


