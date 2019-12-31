package com.thawk.tickethawk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StripeActivity extends AppCompatActivity {

    Stripe stripe;
    Integer amount;
    String name;
    Card card;
    Token tok;

    Integer feeAmount;
    String accountID;

    Button submitButton;

    EditText cardNumberField, monthField, yearField;

    ProgressBar pB;

    public String pKey;

    StripeActivity sA;

    HashMap<TicketType, Object> map;
    int purchaseQuantity;

    int purchaseTotalWithTax, purchaseTotalWithoutTax;

    String vendorID, eventID;

    DatabaseReference ref;

    boolean shouldAllowBack = true;

    int addedTickets = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_stripe);

        amount = getIntent().getIntExtra("amount", 0);
        purchaseQuantity = getIntent().getIntExtra("purchaseQuantity", 0);
        map = (HashMap<TicketType,Object>)getIntent().getSerializableExtra("map");
        name = "TicketHawk";

        feeAmount = getIntent().getIntExtra("feeAmount", 0);
        accountID = getIntent().getStringExtra("accountID");

        eventID = getIntent().getStringExtra("eventID");
        vendorID = getIntent().getStringExtra("vendorID");

        purchaseTotalWithoutTax = getIntent().getIntExtra("withoutTax", 0);
        purchaseTotalWithTax = getIntent().getIntExtra("withTax", 0);

        stripe = new Stripe(this, getResources().getString(R.string.publishableKey));
        submitButton = (Button)findViewById(R.id.submitButton);

        cardNumberField = (EditText) findViewById(R.id.cardNumber);
        cardNumberField.addTextChangedListener(new FourDigitCardFormatWatcher());

        monthField = (EditText) findViewById(R.id.month);
        yearField = (EditText) findViewById(R.id.year);

        pB = findViewById(R.id.paymentProgress);

        pB.setVisibility(View.INVISIBLE);

        //monthField.addTextChangedListener(new TwoDigitFormatWatcher());
        //yearField.addTextChangedListener(new TwoDigitFormatWatcher());
    }

    public void submitCard(View v){


        //Check for total venue capacity

        ref.child("vendors").child(vendorID).child("events").child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int going = 0;
                int maxIndivCapacity = Integer.MAX_VALUE;
                int maxTotalCapacity = Integer.MAX_VALUE;

                DataSnapshot maxIndivCapacityDS = dataSnapshot.child("maxTickets");
                DataSnapshot goingDS = dataSnapshot.child("going");
                DataSnapshot maxTotalCapacityDS = dataSnapshot.child("totalVenueCapacity");

                if (maxTotalCapacityDS.getValue() != null) {
                    maxTotalCapacity = ((Long) maxTotalCapacityDS.getValue()).intValue();
                }

                if (goingDS.getValue() != null){
                    going = ((Long) goingDS.getValue()).intValue();
                }

                Log.i("purchase_info", String.valueOf(purchaseQuantity + going));
                Log.i("purchase_info", String.valueOf(maxTotalCapacity));

                if (purchaseQuantity + going > maxTotalCapacity) {
                    new AlertDialog.Builder((StripeActivity.this))
                            .setTitle("Capacity Error")
                            .setMessage("There are " + String.valueOf(maxTotalCapacity - going) + " tickets available for purchase remaining. Please change your order and try again.")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    submitCardContinue();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void submitCardContinue(){

        disableBreaks();

        //Run Transaction Blocks



        //Formatting
        if (monthField.length() < 2){
            monthField.getEditableText().insert(0, "0");
        }
        if (yearField.length() > 2){
            yearField.getEditableText().insert(0, "0");
        }


        TextView cvcField = (TextView) findViewById(R.id.cvc);

        card = new Card.Builder(
                cardNumberField.getText().toString(),
                Integer.valueOf(monthField.getText().toString()),
                Integer.valueOf(yearField.getText().toString()),
                cvcField.getText().toString()
        ).build();

        //Toast.makeText(getApplicationContext(), "PROCESS START", Toast.LENGTH_SHORT).show();

        stripe.createToken(
                card,
                new ApiResultCallback<Token>() {
                    public void onSuccess(@NonNull Token token) {
                        // send token ID to your server, you'll create a charge next
                        //Toast.makeText(getApplicationContext(), "Token created: " + token.getId(), Toast.LENGTH_SHORT).show();
                        tok = token;
                        new StripeCharge(token.getId()).execute();
                    }

                    @Override
                    public void onError(@NotNull Exception e) {
                        Log.d("Stripe", e.getLocalizedMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                enableBreaks();
                            }
                        });

                        Toast.makeText(getApplicationContext(), "Error: Payment Information Error", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }



    public void disableBreaks(){
        pB.setVisibility(View.VISIBLE);
        submitButton.setEnabled(false);
        shouldAllowBack = false;
    }

    public void enableBreaks(){
        pB.setVisibility(View.INVISIBLE);
        submitButton.setEnabled(true);
        shouldAllowBack = true;
    }

    @Override
    public void onBackPressed() {
        if (!shouldAllowBack) {
        } else {
            super.onBackPressed();
        }
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
                    postData(name, token, "" + amount, accountID, "" + feeAmount);
                }
            }.start();
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Result", s);
        }

        public void postData(String description, String token, String amount, String accountID, String feeAmount) {
            // Create a new HttpClient and Post Header
            try {
                URL url = new URL(getResources().getString(R.string.baseURLString) + "charge");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Log.e("TAG", "hello");

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("method", "charge"));
                params.add(new BasicNameValuePair("description", description));
                params.add(new BasicNameValuePair("token", token));
                params.add(new BasicNameValuePair("amount", amount));
                params.add(new BasicNameValuePair("currency", "usd"));

                params.add(new BasicNameValuePair("account_id", accountID));
                params.add(new BasicNameValuePair("application_fee_amount", feeAmount));

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(getQuery(params));
                wr.flush();
                wr.close();

                Log.i("responsecode", String.valueOf(conn.getResponseCode()));
                Log.i("responsemessage", String.valueOf(conn.getResponseMessage()));

                if (conn.getResponseCode() == 200){
                    //Success

                    /**

                    Intent i = new Intent(StripeActivity.this, CustomerTicketGeneration.class);

                    i.putExtra("map", map);
                    i.putExtra("vendorID", vendorID);
                    i.putExtra("eventID", eventID);

                    runTransactionBlocks();

                    startActivity(i);

                     **/

                    runTransactionBlocks();
                    generateTickets();
                } else {

                }
                Log.e("TAG", "hello4");
                if (conn != null){
                    conn.disconnect();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enableBreaks();
                    }
                });







            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG", "hello3");
            } finally {

                Log.e("TAG", data.toString());



            }
        }
    }

    public void runTransactionBlocks(){
        DatabaseReference goingRef = ref.child("vendors").child(vendorID).child("events").child(eventID).child("going");
        DatabaseReference grossSalesRef = ref.child("vendors").child(vendorID).child("events").child(eventID).child("grossSales");
        DatabaseReference netSalesRef = ref.child("vendors").child(vendorID).child("events").child(eventID).child("netSales");

        goingRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                int value;
                if (mutableData.getValue() == null){
                    value = 0;
                } else {
                    value = ((Long)mutableData.getValue()).intValue();
                }
                mutableData.setValue(value + purchaseQuantity);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
        grossSalesRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                int value;
                if (mutableData.getValue() == null){
                    value = 0;
                } else {
                    value = ((Long)mutableData.getValue()).intValue();
                }
                mutableData.setValue(value + purchaseTotalWithTax);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
        netSalesRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                int value;
                if (mutableData.getValue() == null){
                    value = 0;
                } else {
                    value = ((Long)mutableData.getValue()).intValue();
                }
                mutableData.setValue(value + purchaseTotalWithoutTax);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
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

    private void generateTickets(){


        ref.child("vendors").child(vendorID).child("events").child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()){
                    return;
                }

                String eventTitle = (String)dataSnapshot.child("eventTitle").getValue();

                String dateAndTime = (String)dataSnapshot.child("startDateAndTime").getValue();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
                Date d1 = new Date();
                try {
                    d1 = simpleDateFormat.parse(dateAndTime);
                } catch (Exception e) {

                }
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM-dd-yyyy h:mm a", Locale.US);
                try {
                    dateAndTime = simpleDateFormat2.format(d1);
                } catch (Exception e) {

                }

                String location = (String)dataSnapshot.child("location").getValue();
                String dressCodeString = (String)dataSnapshot.child("dressCode").getValue();

                String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

                for (Map.Entry<TicketType, Object> e : map.entrySet()){

                    int countdown = (int)e.getValue();

                    while (countdown != 0){

                        String key = ref.child("vendors").child(vendorID).child("events").child(eventID).child("activeTickets").push().getKey();

                        HashMap<String, Object> ticket = new HashMap<>();

                        ticket.put("userName", userName);
                        ticket.put("title", eventTitle);
                        ticket.put("dateAndTime", dateAndTime);
                        ticket.put("location", location);
                        ticket.put("ticketType", e.getKey().name);
                        ticket.put("key", key);

                        ref.child("vendors").child(vendorID).child("events").child(eventID).child("activeTickets").child(key).setValue(ticket, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError firebaseError, DatabaseReference ref) {
                                if (firebaseError != null) {

                                } else {
                                    addedTickets++;
                                    determineIfFinished(addedTickets);

                                }
                            }
                        });

                        ref.child("customers").child(FirebaseAuth.getInstance().getUid()).child("activeTickets").child(key).setValue(ticket, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError firebaseError, DatabaseReference ref) {
                                if (firebaseError != null) {

                                } else {
                                    addedTickets++;
                                    determineIfFinished(addedTickets);
                                }
                            }
                        });
                        countdown--;
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void determineIfFinished(int quantity) {
        int totalQuantity = 0;

        for (Map.Entry<TicketType, Object> e: map.entrySet()){
            totalQuantity += (int)e.getValue();
        }

        /**

        ref.child("vendors").child(vendorID).child("closedEvents").child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null){
                    ref.child("vendors").child(vendorID).child("events").child(eventID).removeValue();
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         **/


        if (quantity == totalQuantity * 2){

            //Check if the event has been closed already

            ref.child("vendors").child(vendorID).child("closedEvents").child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.getValue() == null){

                        new AlertDialog.Builder((StripeActivity.this))
                                .setTitle("Your Purchase was Successful!")
                                .setMessage("View your new tickets in the 'My Tickets' tab.")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Pop to main customer activity

                                        Intent i = new Intent(StripeActivity.this, CustomerMainActivity.class);
                                        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    } else {

                        ref.child("vendors").child(vendorID).child("events").child(eventID).removeValue();

                        new AlertDialog.Builder((StripeActivity.this))
                                .setTitle("Your Purchase was Unsuccessful!")
                                .setMessage("Contact TicketHawk support for information on refunds.")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Pop to main customer activity

                                        Intent i = new Intent(StripeActivity.this, CustomerMainActivity.class);
                                        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });





        }

    }




}




