package com.example.MHSmanitobahistoricalsites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MHSmanitobahistoricalsites.Database.ManitobaHistoricalSite;

import java.util.List;

public class SearchSiteAdapter extends RecyclerView.Adapter<SearchSiteAdapter.ViewHolder> {

    Context context;
    List<ManitobaHistoricalSite> siteList;

    FragmentActivity activity;
    private HistoricalSiteDetailsViewModel mViewModel;


    public SearchSiteAdapter(Context context, List<ManitobaHistoricalSite> siteList, FragmentActivity activity) {
        this.context = context;
        this.siteList = siteList;
        this.activity = activity;
        this.mViewModel = new ViewModelProvider(activity).get(HistoricalSiteDetailsViewModel.class);
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
        String[] siteTypes = context.getResources().getStringArray(R.array.Site_Types);
        ManitobaHistoricalSite currentSite = siteList.get(position);
        holder.setTvName(currentSite.getName());
        String details = currentSite.getAddress() == null? "": currentSite.getAddress() + ", ";

        details += currentSite.getMunicipality() + ", " + siteTypes[currentSite.getMain_type() - 1];
        holder.setTvDetails(details);
        holder.getView().setOnClickListener(v -> {
            Fragment newFragment = HistoricalSiteDetailsFragment.newInstance(currentSite.getSite_id());
            FragmentManager fragmentManager = activity.getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    //.replace(R.id.fcvDetails, HistoricalSiteDetailsFragment.class, null)
                    /*.setCustomAnimations(
                            R.anim.slide_in,  // enter
                            R.anim.fade_out,  // exit
                            R.anim.fade_in,   // popEnter
                            R.anim.slide_out  // popExit
                    )*/
                    .replace(R.id.fcvDetails, newFragment, null)
                    .setReorderingAllowed(true)
                    .addToBackStack(activity.getString(R.string.site_fragment))
                    .commit();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
        public void setTvName(String tvName) { this.tvName.setText(tvName);}
        public void setTvDetails(String tvDetails) {
            this.tvDetails.setText(tvDetails);
        }


    }


}
