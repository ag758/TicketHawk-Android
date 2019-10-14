package com.thawk.tickethawk.RecyclerClasses;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.thawk.tickethawk.BaseClasses.Event;
import com.thawk.tickethawk.CustomerMainActivity;
import com.thawk.tickethawk.CustomerVendorListActivity;
import com.thawk.tickethawk.R;

import java.util.ArrayList;

public class EventModAdapter extends RecyclerView.Adapter<EventModAdapter.MyViewHolder> {

    public ArrayList<Event> mDataset = new ArrayList<>();
    private Context mContext;


    public EventModAdapter(ArrayList<Event> dataset, Context context){
        mDataset = dataset;
        mContext = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView eventImage;
        TextView eventTitle, eventDate, eventMinPrice;

        public MyViewHolder(View v) {
            super(v);

            eventImage = v.findViewById(R.id.event_image);
            eventTitle = v.findViewById(R.id.event_title);
            eventDate = v.findViewById(R.id.event_date);
            eventMinPrice = v.findViewById(R.id.event_min_price);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }




    }
    @Override
    public EventModAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_cell_mod, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final @NonNull EventModAdapter.MyViewHolder myViewHolder, final int position) {
        Event ev = mDataset.get(position);

        myViewHolder.eventTitle.setText(ev.title);
        myViewHolder.eventDate.setText(ev.dateAndTime);
        myViewHolder.eventMinPrice.setText(ev.lowestPrice);

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CustomerVendorListActivity)mContext).transitionToCustomerEvent(mDataset.get(position).creatorId, mDataset.get(position).id);
            }
        });

        try {
            Uri u = Uri.parse(mDataset.get(position).imageURL);
            Picasso.get().setLoggingEnabled(true);
            Picasso.get().load(u).into(myViewHolder.eventImage);
        } catch (Exception e ){
            Log.i("picasso_error", "error");
        }

    }



    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}