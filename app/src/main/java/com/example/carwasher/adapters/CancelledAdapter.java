package com.example.carwasher.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwasher.R;
import com.example.carwasher.models.CompletedReqModel;

import java.util.List;

public class CancelledAdapter extends RecyclerView.Adapter<CancelledAdapter.ViewHolder>
{
    List<CompletedReqModel> list;

    public CancelledAdapter(List<CompletedReqModel> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView txt_username,txt_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_username = itemView.findViewById(R.id.cancelled_requester);
            txt_date = itemView.findViewById(R.id.cancelled_date);
        }
    }

    @NonNull
    @Override
    public CancelledAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_cancelled_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CancelledAdapter.ViewHolder holder, int position) {

        CompletedReqModel mdl = list.get(position);
        holder.txt_username.setText(mdl.getRequester());
        holder.txt_date.setText(mdl.getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
