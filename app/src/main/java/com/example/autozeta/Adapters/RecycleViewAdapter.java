package com.example.autozeta.Adapters;


import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.autozeta.R;

import java.util.ArrayList;

import data.Service;


public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private ArrayList<Service> mDataSet;
    private ArrayList<Service> passedServices;
    private SparseBooleanArray checkedState = new SparseBooleanArray();

    public void setCheckedState(int position,boolean checked){
        checkedState.append(position,checked);
    }
    public boolean isItemChecked(int position){
        return checkedState.get(position);
    }
    public SparseBooleanArray getCheckedState(){
        return checkedState;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final Switch swch;

        public ViewHolder(View v) {
            super(v);
            /*v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });*/
            swch = (Switch) v.findViewById(R.id.Switch);
            textView = (TextView) v.findViewById(R.id.textView);
        }

        public Switch getSwitch(){return swch;}

        public TextView getTextView() {
            return textView;
        }
    }

    public RecycleViewAdapter(ArrayList<Service> dataSet,ArrayList<Service> passedServices) {
        this.passedServices = passedServices;
        mDataSet = dataSet;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(v);
    }
    public ArrayList<Service> getCheckedItems(){
        ArrayList<Service> checkedItems= new ArrayList<Service>();
        for(int i=0;i<getItemCount();i++)
        {
            if(isItemChecked(i)){
                checkedItems.add(mDataSet.get(i));
            }
        }
        return checkedItems;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {


        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getTextView().setText(mDataSet.get(position).getName());
        viewHolder.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCheckedState(viewHolder.getAdapterPosition(),isChecked);
            }
        });
        if(passedServices!=null) {
            if (passedServices.contains(mDataSet.get(position))) {
                viewHolder.getSwitch().setChecked(true);
            }
        }
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return  mDataSet.size();
    }
}
