package com.example.timad.poznavacka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RWAdapter extends RecyclerView.Adapter<RWAdapter.ZastupceViewHolder> {
    private ArrayList<Zastupce> mZastupceList;

    public static class ZastupceViewHolder extends RecyclerView.ViewHolder{
        public TextView textView1;
        public TextView textView2;

        public ZastupceViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.itemText1);
            textView2 = itemView.findViewById(R.id.itemText2);
        }
    }

    public RWAdapter(ArrayList<Zastupce> zastupceList){
        mZastupceList = zastupceList;
    }

    @NonNull
    @Override
    public ZastupceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zastupce, parent, false);
        ZastupceViewHolder zvh = new ZastupceViewHolder(v);
        return zvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ZastupceViewHolder holder, int position) {
        Zastupce currentZastupce = mZastupceList.get(position);
        holder.textView1.setText(currentZastupce.getText1());
        holder.textView1.setText(currentZastupce.getText2());
    }

    @Override
    public int getItemCount() {
        return mZastupceList.size();
    }
}
