package com.thawk.tickethawk.RecyclerClasses;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thawk.tickethawk.BaseClasses.Vendor;
import com.thawk.tickethawk.CustomerMainActivity;
import com.thawk.tickethawk.CustomerMainBrowseFragment;
import com.thawk.tickethawk.EventTicketNumberActivity;
import com.thawk.tickethawk.R;
import com.thawk.tickethawk.TicketType;

import java.text.NumberFormat;
import java.util.ArrayList;

public class TicketTypeAdapter extends RecyclerView.Adapter<TicketTypeAdapter.MyViewHolder> {

    public ArrayList<TicketType> mDataset = new ArrayList<>();
    private Context mContext;



    public TicketTypeAdapter(ArrayList<TicketType> dataset, Context context){
        mDataset = dataset;
        mContext = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView ticketName, ticketPrice;
        public EditText ticketQuantity;

        public MyViewHolder(View v) {
            super(v);
            ticketName = v.findViewById(R.id.ticket_title);
            ticketPrice = v.findViewById(R.id.ticket_price);
            ticketQuantity = v.findViewById(R.id.ticket_quantity);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }




    }
    @Override
    public TicketTypeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_cell, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final @NonNull TicketTypeAdapter.MyViewHolder myViewHolder, final int position) {


        myViewHolder.ticketName.setText(mDataset.get(position).name);

        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        double newdouble = (double)((Double.valueOf(mDataset.get(position).price) / 100));
        String number = formatter.format(newdouble);

        myViewHolder.ticketPrice.setText(number);

        myViewHolder.ticketQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int quant = 0;
                try {
                    quant = Integer.parseInt(charSequence.toString());
                } catch (Exception e){
                }
                ((EventTicketNumberActivity)mContext).map.put(mDataset.get(position), quant);
                ((EventTicketNumberActivity)mContext).updateTotalPrice();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}