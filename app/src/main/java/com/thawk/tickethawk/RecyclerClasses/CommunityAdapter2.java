package com.thawk.tickethawk.RecyclerClasses;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thawk.tickethawk.CommunityEditActivity;
import com.thawk.tickethawk.CustomerActivity2;
import com.thawk.tickethawk.R;

import java.util.ArrayList;

public class CommunityAdapter2 extends RecyclerView.Adapter<CommunityAdapter2.MyViewHolder> {

    public ArrayList<String> mDataset = new ArrayList<>();
    private Activity mContext;

    private int selectedPos = RecyclerView.NO_POSITION;

    public CommunityAdapter2(ArrayList<String> dataset, Activity context){
        mDataset = dataset;
        mContext = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView stringView;
        public String key;

        public MyViewHolder(View v) {
            super(v);
            stringView = v.findViewById(R.id.string_view);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            ((CommunityEditActivity)mContext).intendedCommunity = (String)stringView.getText();

            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);
        }




    }
    @Override
    public CommunityAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_string_cell, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final @NonNull CommunityAdapter2.MyViewHolder myViewHolder, int position) {
        final int p = position;
        myViewHolder.stringView.setText(mDataset.get(position));

        myViewHolder.itemView.setSelected(selectedPos == position);

    }



    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}