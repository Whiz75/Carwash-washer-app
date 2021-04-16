package com.example.carwasher.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwasher.R;
import com.example.carwasher.models.CompletedReqModel;
import com.example.carwasher.models.RequestModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompletedAdapter extends RecyclerView.Adapter<CompletedAdapter.ViewHolder>{

    private ArrayList<CompletedReqModel> items;

    public CompletedAdapter(ArrayList<CompletedReqModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_completed_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CompletedReqModel ld = items.get(position);

        holder.txt_username.setText(ld.getRequester());
        holder.txt_date.setText(ld.getDate());
        Picasso
                .get()
                .load(ld.getProfile())
                .into(holder.img_request);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView txt_username,txt_date;
        private CircleImageView img_request;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_username = itemView.findViewById(R.id.completed_requester);
            txt_date = itemView.findViewById(R.id.completed_date);
            img_request = itemView.findViewById(R.id.complete_profile_image);
        }
    }


}
