package com.thawk.tickethawk.RecyclerClasses;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.thawk.tickethawk.EventTicketNumberActivity;
import com.thawk.tickethawk.R;
import com.thawk.tickethawk.Ticket;
import com.thawk.tickethawk.TicketType;

import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.MyViewHolder> {

    public ArrayList<Ticket> mDataset = new ArrayList<>();
    private Context mContext;
    public Activity mTicketActivity;


    public TicketAdapter(ArrayList<Ticket> dataset, Context context, Activity a){
        mDataset = dataset;
        mContext = context;
        mTicketActivity = a;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView qrCode;
        TextView titleView, ticketTypeView, nameView, dateView, locationView;

        public MyViewHolder(View v) {
            super(v);

            qrCode = v.findViewById(R.id.qr_codeview);
            titleView = v.findViewById(R.id.titleView);
            nameView = v.findViewById(R.id.name_view);
            ticketTypeView = v.findViewById(R.id.tickettype_view);
            dateView = v.findViewById(R.id.date_view);
            locationView = v.findViewById(R.id.address_view);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }




    }
    @Override
    public TicketAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticketview_cell, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final @NonNull TicketAdapter.MyViewHolder myViewHolder, final int position) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                myViewHolder.titleView.setText(mDataset.get(position).eventTitle);
                myViewHolder.locationView.setText(mDataset.get(position).location);

                myViewHolder.dateView.setText(mDataset.get(position).dateAndTime);
                myViewHolder.ticketTypeView.setText(mDataset.get(position).ticketType);

                myViewHolder.nameView.setText(mDataset.get(position).userName);

                try {
                    QRCodeWriter writer = new QRCodeWriter();
                    BitMatrix matrix = null;
                    try {
                        matrix = writer.encode(mDataset.get(position).key, BarcodeFormat.QR_CODE, 100, 100);
                    } catch (WriterException ex) {
                        //
                    }
                    final Bitmap bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
                    for (int x = 0; x < 100; x++){
                        for (int y = 0; y < 100; y++){
                            bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
                        }
                    }
                    mTicketActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            (myViewHolder.qrCode).setImageBitmap(bmp);
                        }
                    });



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });





    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}