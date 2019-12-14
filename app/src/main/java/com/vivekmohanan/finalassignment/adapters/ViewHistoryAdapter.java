package com.vivekmohanan.finalassignment.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vivekmohanan.finalassignment.R;
import com.vivekmohanan.finalassignment.models.SensorValues;

import java.util.ArrayList;

public class ViewHistoryAdapter extends RecyclerView.Adapter<ViewHistoryAdapter.ViewHistoryViewHolder> {
    ArrayList<SensorValues> sensorValuesList;

    public ViewHistoryAdapter(ArrayList<SensorValues> arrayList) {
        sensorValuesList = arrayList;
    }

    @Override
    public ViewHistoryAdapter.ViewHistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_item,viewGroup, false);

        return new ViewHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHistoryAdapter.ViewHistoryViewHolder holder, int position) {


        holder.xValue.setText(Float.toString(sensorValuesList.get(position).getxValue()));
        holder.yValue.setText(Float.toString(sensorValuesList.get(position).getyValue()));
        holder.time.setText(sensorValuesList.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        return sensorValuesList.size();
    }

    public class ViewHistoryViewHolder extends RecyclerView.ViewHolder {


        public TextView xValue;
        public TextView yValue;
        public TextView time;

        public ViewHistoryViewHolder(View itemView) {
            super(itemView);


            xValue = itemView.findViewById(R.id.textView_X);
            yValue = itemView.findViewById(R.id.textView_Y);
            time = itemView.findViewById(R.id.textView_Time);
        }
    }
}
