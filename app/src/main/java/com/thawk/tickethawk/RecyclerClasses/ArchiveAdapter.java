package com.thawk.tickethawk.RecyclerClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.thawk.tickethawk.R;
import com.thawk.tickethawk.Ticket;

import java.util.ArrayList;

public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.MyViewHolder> {

    public ArrayList<Ticket> mDataset = new ArrayList<>();
    private Context mContext;

    DatabaseReference ref;

    public ArchiveAdapter(ArrayList<Ticket> dataset, Context context){
        mDataset = dataset;
        mContext = context;

        ref = FirebaseDatabase.getInstance().getReference();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView topView, bottomView;

        public MyViewHolder(View v) {
            super(v);

            topView = v.findViewById(R.id.top_line);
            bottomView = v.findViewById(R.id.bottom_line);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }




    }
    @Override
    public ArchiveAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.archived_ticketcell, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final @NonNull ArchiveAdapter.MyViewHolder myViewHolder, final int position) {

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String ticketKey = mDataset.get(position).key;

                AlertDialog.Builder builder = new AlertDialog.Builder((mContext), R.style.com_facebook_auth_dialog)
                        .setTitle(mDataset.get(position).eventTitle)
                        .setMessage("")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("unarchive", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation

                                ref.child("customers").child(FirebaseAuth.getInstance().getUid()).child("archivedTickets").child(ticketKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Object o = dataSnapshot.getValue();

                                        ref.child("customers").child(FirebaseAuth.getInstance().getUid()).child("activeTickets").child(ticketKey).setValue(o,new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                if (error != null) {

                                                } else {
                                                    (ArchiveAdapter.this).ref.child("customers").child(FirebaseAuth.getInstance().getUid()).child("archivedTickets").child(ticketKey).removeValue();
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                            }
                        })
                        .setIcon(R.drawable.thawk_solid);
                final AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.filled_rounded_rec_grey));
                alertDialog.show();
                alertDialog.getWindow().setGravity(Gravity.BOTTOM);
            }
        });

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                myViewHolder.topView.setText(mDataset.get(position).eventTitle);
                myViewHolder.bottomView.setText(mDataset.get(position).ticketType + " | " + mDataset.get(position).dateAndTime);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}