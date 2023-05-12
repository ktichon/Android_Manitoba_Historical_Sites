package com.example.winnipeghistoricalsites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;




        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.LinearLayout;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.cardview.widget.CardView;
        import androidx.recyclerview.widget.RecyclerView;

     

        import java.util.List;

public class DirectionStepAdapter extends RecyclerView.Adapter<DirectionStepAdapter.ViewHolder>{
    Context context;
    List<directionStep> allSteps;
  

    public DirectionStepAdapter(Context context, List<directionStep> allSteps) {
        this.context = context;
        this.allSteps = allSteps;

    }

    @NonNull
    @Override
    public DirectionStepAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.directions_step, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DirectionStepAdapter.ViewHolder holder, int position) {
        directionStep currentSet = allSteps.get(position);
        holder.tvDistanceTime.setText(currentSet.distance + " (" + currentSet.duration + ")");
        holder.tvHtmlDirections.setText(currentSet.htmlDirections);


    }

    @Override
    public int getItemCount() {
        return allSteps.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDistanceTime ;
        TextView tvHtmlDirections;

        public ViewHolder(@NonNull View view) {
            super(view);

            tvDistanceTime = view.findViewById(R.id.tvDistanceTime);
            tvHtmlDirections = view.findViewById(R.id.tvHtmlDirections);
        }
    }
}