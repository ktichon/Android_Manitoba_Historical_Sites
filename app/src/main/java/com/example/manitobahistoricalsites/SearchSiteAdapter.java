package com.example.manitobahistoricalsites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manitobahistoricalsites.Database.ManitobaHistoricalSite;

import java.util.List;

public class SearchSiteAdapter extends RecyclerView.Adapter<SearchSiteAdapter.ViewHolder> {

    Context context;
    List<ManitobaHistoricalSite> siteList;
    private HistoricalSiteDetailsViewModel mViewModel;
    private FragmentManager fragmentManager;

    public SearchSiteAdapter(Context context, List<ManitobaHistoricalSite> siteList, FragmentActivity activity) {
        this.context = context;
        this.siteList = siteList;
        this.mViewModel = new ViewModelProvider(activity).get(HistoricalSiteDetailsViewModel.class);
        this.fragmentManager = activity.getSupportFragmentManager();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchSiteAdapter.ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.search_site_holder_layout, parent, false
                ) );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ManitobaHistoricalSite currentSite = siteList.get(position);
        holder.setTvName(currentSite.getName());
        String details = currentSite.getAddress() == null? "": currentSite.getAddress() + ", ";
        details += currentSite.getMunicipality() + ", " + currentSite.getMain_type();
        holder.setTvDetails(details);
        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.setCurrentSite(currentSite);
                fragmentManager.popBackStack();
            }
        });

    }

    @Override
    public int getItemCount() {
        return siteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvDetails;
        View view;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
            tvName = view.findViewById(R.id.tvHolderSiteName);
            tvDetails = view.findViewById(R.id.tvHolderSiteInfo);
        }

        public View getView() {
            return view;
        }

        public TextView getTvName() {
            return tvName;
        }

        public void setTvName(String tvName) {
            this.tvName.setText(tvName);
        }

        public TextView getTvDetails() {
            return tvDetails;
        }

        public void setTvDetails(String tvDetails) {
            this.tvDetails.setText(tvDetails);
        }


    }


}
