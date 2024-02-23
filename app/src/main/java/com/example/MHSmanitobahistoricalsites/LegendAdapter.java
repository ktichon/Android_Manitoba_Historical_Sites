package com.example.MHSmanitobahistoricalsites;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MHSmanitobahistoricalsites.HolderClasses.LegendItem;

import java.util.List;

public class LegendAdapter extends RecyclerView.Adapter<LegendAdapter.ViewHolder>{
    List<LegendItem> legendItems;

    public LegendAdapter(List<LegendItem> legendItems) {
        this.legendItems = legendItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LegendAdapter.ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.legend_holder_layout, parent, false
                ) );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LegendItem currentItem = legendItems.get(position);
        //Color.HSVToColor(new float[])
        holder.imIcon.setColorFilter( Math.round(currentItem.getColour()));
        holder.tvType.setText(currentItem.getTypeName());


    }

    @Override
    public int getItemCount() {
        return this.legendItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imIcon;
        TextView tvType;
        View view;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
            tvType = view.findViewById(R.id.tvHolderLegendType);
            imIcon = view.findViewById(R.id.imLegendHolder);
        }

        public View getView() {
            return view;
        }


    }
}
