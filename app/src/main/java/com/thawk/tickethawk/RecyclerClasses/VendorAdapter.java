package com.thawk.tickethawk.RecyclerClasses;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.thawk.tickethawk.BaseClasses.Vendor;
import com.thawk.tickethawk.CustomerActivity2;
import com.thawk.tickethawk.CustomerMainBrowseFragment;
import com.thawk.tickethawk.R;

import java.net.URI;
import java.util.ArrayList;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.MyViewHolder> {

    public ArrayList<Vendor> mDataset = new ArrayList<>();
    private Context mContext;
    CustomerMainBrowseFragment f;


    public VendorAdapter(ArrayList<Vendor> dataset, Context context, CustomerMainBrowseFragment f){
        mDataset = dataset;
        mContext = context;
        this.f = f;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView vendorName, categoryName;
        public ImageView vendorImage;

        public MyViewHolder(View v) {
            super(v);
            vendorName = v.findViewById(R.id.vendor_title);
            categoryName = v.findViewById(R.id.vendor_category);
            vendorImage = v.findViewById(R.id.vendor_image);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }




    }
    @Override
    public VendorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_cell, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final @NonNull VendorAdapter.MyViewHolder myViewHolder, int position) {

        Log.i("vendor_adapter", "cell");
        myViewHolder.vendorName.setText(mDataset.get(position).name);
        myViewHolder.categoryName.setText(mDataset.get(position).ticketCategory);


        try {
            Uri u = Uri.parse(mDataset.get(position).pictureURL);
            Picasso.get().setLoggingEnabled(true);
            Picasso.get().load(u).into(myViewHolder.vendorImage);
        } catch (Exception e ){
            Log.i("picasso_error", "error");
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}