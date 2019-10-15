package com.thawk.tickethawk;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.connection.ConnectionAuthTokenProvider;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.Token;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.URI;
import java.util.List;

public class StripeActivity extends Activity {

    Stripe stripe;
    Integer amount;
    String name;
    Card card;
    Token tok;

    Button submitButton;

    public String pKey;

    StripeActivity sA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe);

        amount = 1000;
        name = "TicketHawk";

        stripe = new Stripe(this, getResources().getString(R.string.publishableKey));
        submitButton = (Button)findViewById(R.id.submitButton);
    }

    public void submitCard(View v){

        submitButton.setEnabled(false);

        TextView cardNumberField = (TextView) findViewById(R.id.cardNumber);
        TextView monthField = (TextView) findViewById(R.id.month);
        TextView yearField = (TextView) findViewById(R.id.year);
        TextView cvcField = (TextView) findViewById(R.id.cvc);

        card = new Card.Builder(
                cardNumberField.getText().toString(),
                Integer.valueOf(monthField.getText().toString()),
                Integer.valueOf(yearField.getText().toString()),
                cvcField.getText().toString()
        ).build();


        stripe.createToken(
                card,
                new ApiResultCallback<Token>() {
                    public void onSuccess(@NonNull Token token) {
                        // send token ID to your server, you'll create a charge next
                        Toast.makeText(getApplicationContext(), "Token created: " + token.getId(), Toast.LENGTH_LONG).show();
                        tok = token;
                        new StripeCharge(token.getId()).execute();
                    }

                    @Override
                    public void onError(@NotNull Exception e) {
                        Log.d("Stripe", e.getLocalizedMessage());
                    }
                }
        );
    }

    public class StripeCharge extends AsyncTask<String, Void, String> {
        String token;
        String data = "";
        HttpURLConnection conn = null;

        public StripeCharge(String token) {
            this.token = token;
        }

        @Override
        protected String doInBackground(String... params) {
            new Thread() {
                @Override
                public void run() {
                    postData(name, token, "" + amount);
                }
            }.start();
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Result", s);
        }

        public void postData(String description, String token, String amount) {
            // Create a new HttpClient and Post Header
            try {
                URL url = new URL(getResources().getString(R.string.baseURLString));
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Log.e("TAG", "hello");

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("method", "charge"));
                params.add(new BasicNameValuePair("description", description));
                params.add(new BasicNameValuePair("source", token));
                params.add(new BasicNameValuePair("amount", amount));
                params.add(new BasicNameValuePair("currency", "usd"));

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(getQuery(params));
                wr.flush();
                wr.close();

                InputStream in = conn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    Log.e("TAG", "hello2" + data);
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG", "hello3");
            } finally {

                if (data.contains("Success")){
                    //Success
                } else {

                }
                Log.e("TAG", "hello4");
                if (conn != null){
                    conn.disconnect();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        submitButton.setEnabled(true);
                    }
                });


            }
        }
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }




}




