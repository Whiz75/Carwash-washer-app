package com.example.carwasher.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwasher.R;
import com.example.carwasher.models.SpecsModel;

import java.util.List;

public class SpecsAdapter extends RecyclerView.Adapter<SpecsAdapter.ViewHolder> {

    List<SpecsModel> list;

    public SpecsAdapter(List<SpecsModel> list) {
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txt_itemname;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_itemname = itemView.findViewById(R.id.spec_item_name);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_specs_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SpecsModel ld = list.get(position);
        holder.txt_itemname.setText(ld.getItem());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
