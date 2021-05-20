package com.example.carwasher.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwasher.R;
import com.example.carwasher.models.RequestModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder>
{

    private final List<RequestModel> list;
    private final ButtonsClickListener buttonsClickListener;

    public RequestsAdapter(List<RequestModel> list, ButtonsClickListener buttonsClickListener) {
        this.list = list;
        this.buttonsClickListener = buttonsClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_username;
        private final TextView txt_time;
        private final CircleImageView img_profile;
        private final Button btnAccept;
        private final Button btnReject;
        View view;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);

            txt_username = itemView.findViewById(R.id.request_username);
            txt_time = itemView.findViewById(R.id.request_time);
            img_profile = itemView.findViewById(R.id.request_profile_image);
            btnAccept = itemView.findViewById(R.id.request_accept_button);
            btnReject = itemView.findViewById(R.id.request_ignore_button);
            view = itemView;

            btnAccept.setOnClickListener(this);
            btnReject.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == btnAccept.getId())
            {
                int pos = getAdapterPosition();
                buttonsClickListener.AccepterClick(pos);
            }

            if (v.getId() == btnReject.getId())
            {
                int pos = getAdapterPosition();
                buttonsClickListener.RejectClick(pos);
            }
        }
    }

    public interface ButtonsClickListener extends ValueEventListener {
        void AccepterClick(int position);
        void RejectClick(int position);

        @Override
        void onDataChange(@NonNull DataSnapshot snapshot);

        @Override
        void onCancelled(@NonNull DatabaseError error);
    }

    @NonNull
    @Override
    public RequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_request_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsAdapter.ViewHolder holder, final int position) {

        RequestModel ld = list.get(position);
        holder.txt_username.setText(ld.getRequester());
        holder.txt_time.setText(ld.getDate());
        Picasso
                .get()
                .load(ld.getProfile())
                .into(holder.img_profile);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
