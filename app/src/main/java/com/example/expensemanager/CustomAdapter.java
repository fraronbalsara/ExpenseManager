package com.example.expensemanager;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {

    FragmentActivity fragmentActivity;
    List<Model> modelList;
    Context context;

    public CustomAdapter(FragmentActivity fragmentActivity, List<Model> modelList, Context context) {
        this.fragmentActivity = fragmentActivity;
        this.modelList = modelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //inflate layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);

        final ViewHolder viewHolder = new ViewHolder(itemView);

        //handle item clicks here
        viewHolder.setOnClickListener(new ViewHolder.clickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //this will be called when user clicks item
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                //this will be called when user long clicks item

                final String uuid = modelList.get(position).getSecret_uuid();
                final String date = modelList.get(position).getDate();
                final String cost = modelList.get(position).getCost();
                final String description  = modelList.get(position).getDescription();
                final String sub = modelList.get(position).getSub_category();
                final String main = modelList.get(position).getMain_category();

                //options to display in dialog
                String[] options = {"Update"};

                //Alert Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            //update is clicked
                            Intent intent = new Intent(fragmentActivity,update.class);
                            intent.putExtra("uuid",uuid);
                            intent.putExtra("date",date);
                            intent.putExtra("cost",cost);
                            intent.putExtra("description",description);
                            intent.putExtra("sub",sub);
                            intent.putExtra("main",main);
                            fragmentActivity.startActivity(intent);
                        }
                        /*else if(which == 1){
                            //delete is clicked
                            recentdata rd = new recentdata();
                            rd.deleteData(date,cost,description,main,sub,uuid,fragmentActivity);
                            Toast.makeText(fragmentActivity,"Sorry, this feature is currently unavailable.",Toast.LENGTH_LONG).show();
                        }*/
                    }
                }).create().show();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //bind views / set data
        holder.showCost.setText(modelList.get(position).getCost());
        holder.showDescription.setText(modelList.get(position).getDescription());
        holder.showSubCategory.setText(modelList.get(position).getSub_category());
        holder.showMainCategory.setText(modelList.get(position).getMain_category());
        holder.showDate.setText(modelList.get(position).getDate());
        holder.showUUID.setText(modelList.get(position).secret_uuid);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
