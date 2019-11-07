package com.example.expensemanager;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView showCost, showDescription, showSubCategory, showMainCategory, showDate, showUUID;
    View mView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        //item click
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });

        //item long click listener
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
        });

        //initialize views with cardview.xml
        showCost = itemView.findViewById(R.id.showCost);
        showDescription = itemView.findViewById(R.id.showDescription);
        showSubCategory = itemView.findViewById(R.id.showSubCategory);
        showMainCategory = itemView.findViewById(R.id.showMainCategory);
        showDate = itemView.findViewById(R.id.showDate);
        showUUID = itemView.findViewById(R.id.showUUID);
    }

    private ViewHolder.clickListener mClickListener;

    //interface for click listener
    public interface clickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    public void setOnClickListener(ViewHolder.clickListener clickListener){
        mClickListener = clickListener;
    }
}
