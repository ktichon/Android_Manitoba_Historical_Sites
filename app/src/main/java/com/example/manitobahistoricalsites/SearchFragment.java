package com.example.manitobahistoricalsites;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manitobahistoricalsites.Database.ManitobaHistoricalSite;
import com.example.manitobahistoricalsites.HolderClasses.DisplayMode;
import com.example.manitobahistoricalsites.HolderClasses.SiteFilter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class SearchFragment extends Fragment {

    View mainView;


    private HistoricalSiteDetailsViewModel mViewModel;

    private DisplayMode previousDisplayMode;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private List<ManitobaHistoricalSite> searchableSites;

    private List<ManitobaHistoricalSite> applicableSites;




    private EditText etSearch;

    String searchBy = "Site Address";

    TextView tvBack;

    TextView tvSearchAmount;


    int limit = 10;
    LinearLayout llActiveTypeFilters;

    TextView tvActiveFilterTypes;
    LinearLayout llActiveMunicipalityFilters;

    TextView tvActiveFilterMunicipalities;

    SearchSiteAdapter searchSiteAdapter;
    RecyclerView recyclerView;

    private AppCompatButton btnSearch;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainView = view;
        mViewModel = new ViewModelProvider(requireActivity()).get(HistoricalSiteDetailsViewModel.class);


        //Stores the old display mode,
        previousDisplayMode = mViewModel.getDisplayMode().getValue();
        mViewModel.setDisplayMode(DisplayMode.FullDetail);






        Spinner spinner = (Spinner) mainView.findViewById(R.id.spnSearchBy);
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireActivity(),
                R.array.Search_By,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchBy = parent.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinner.setSelection(0);

        tvSearchAmount = mainView.findViewById(R.id.tvSearchAmount);
        tvSearchAmount.setText("");


        tvBack = mainView.findViewById(R.id.tvSearchGoBack);
        tvBack.setOnClickListener(v -> {
            mViewModel.setDisplayMode(previousDisplayMode);
            FragmentManager fm = requireActivity().getSupportFragmentManager();

            fm.popBackStack();
        });

        //Set up searched site adapter
        applicableSites = new ArrayList<>();
        searchSiteAdapter = new SearchSiteAdapter(getContext(), applicableSites, getActivity());
        recyclerView = mainView.findViewById(R.id.rvSearchSiteHolder);
        recyclerView.setAdapter(searchSiteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        etSearch = mainView.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(searchTextListener);

        btnSearch = mainView.findViewById(R.id.btnSearch);

        //Gets sites to search through
        searchableSites = new ArrayList<>();



        btnSearch.setOnClickListener(v -> searchSites(etSearch.getText().toString(), false));

        llActiveTypeFilters = mainView.findViewById(R.id.llActiveTypeFilters);
        llActiveMunicipalityFilters = mainView.findViewById(R.id.llActiveMunicipalityFilters);
        tvActiveFilterMunicipalities = mainView.findViewById(R.id.tvActiveFilterMunicipalities);
        tvActiveFilterTypes = mainView.findViewById(R.id.tvActiveFilterTypes);

        loadBaseOnFilters(mViewModel.getSiteFilters().getValue());



    }


    //Sets all searchable sites, makes sure that the edittext is only active after there is data
    public void setSearchableSites(List<ManitobaHistoricalSite> sites)
    {

        searchableSites.clear();
        searchableSites = sites;
        etSearch.setEnabled(true);
        btnSearch.setEnabled(true);
        Toast.makeText(getContext(), "Found " +  searchableSites.size() + " Applicable sites.", Toast.LENGTH_SHORT).show();
        if (etSearch != null && searchableSites.size() > 0)
        {
            searchSites(etSearch.getText().toString(), false);
        }
    }

    public void loadBaseOnFilters(SiteFilter siteFilter)
    {
        try {
            //No filter
            //Needed to check to make sure it doesn't run when app is first loaded
            if (siteFilter.isAllMunicipalities() && siteFilter.isAllSiteTypes()  )
            {
                llActiveTypeFilters.setVisibility(View.GONE);
                llActiveMunicipalityFilters.setVisibility(View.GONE);
                mDisposable.add(
                        mViewModel.getHistoricalSiteDatabase().manitobaHistoricalSiteDao().loadAllManitobaHistoricalSites()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe( manitobaHistoricalSites -> setSearchableSites( manitobaHistoricalSites),
                                        throwable ->  Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
                                )
                );
            }
            //Only municipality filter
            else if (!siteFilter.isAllMunicipalities() && siteFilter.isAllSiteTypes()){
                llActiveMunicipalityFilters.setVisibility(View.VISIBLE);
                llActiveTypeFilters.setVisibility(View.GONE);
                tvActiveFilterMunicipalities.setText(siteFilter.getMunicipalityFilter().toString().replace("[","").replace("]", ""));
                mDisposable.add(
                        mViewModel.getHistoricalSiteDatabase().manitobaHistoricalSiteDao().loadManitobaHistoricalSitesFilterMunicipality(siteFilter.getMunicipalityFilter())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe( manitobaHistoricalSites -> setSearchableSites( manitobaHistoricalSites),
                                        throwable ->  Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
                                )
                );
            }
            //Only type filter
            else if (siteFilter.isAllMunicipalities() && !siteFilter.isAllSiteTypes()) {
                llActiveMunicipalityFilters.setVisibility(View.GONE);
                llActiveTypeFilters.setVisibility(View.VISIBLE);
                tvActiveFilterTypes.setText(siteFilter.getSiteTypeFilter().toString().replace("[","").replace("]", ""));
                mDisposable.add(
                        mViewModel.getHistoricalSiteDatabase().manitobaHistoricalSiteDao().loadManitobaHistoricalSitesFilterType(siteFilter.getSiteTypeFilter())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe( manitobaHistoricalSites -> setSearchableSites( manitobaHistoricalSites),
                                        throwable ->  Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
                                )
                );
            }
            // Get both municipality and type filter
            else if (!siteFilter.isAllMunicipalities() && !siteFilter.isAllSiteTypes()) {
                llActiveMunicipalityFilters.setVisibility(View.VISIBLE);
                llActiveTypeFilters.setVisibility(View.VISIBLE);
                tvActiveFilterTypes.setText(siteFilter.getSiteTypeFilter().toString().replace("[","").replace("]", ""));
                tvActiveFilterMunicipalities.setText(siteFilter.getMunicipalityFilter().toString().replace("[","").replace("]", ""));
                mDisposable.add(
                        mViewModel.getHistoricalSiteDatabase().manitobaHistoricalSiteDao().loadManitobaHistoricalSitesAllFilters(siteFilter.getSiteTypeFilter(), siteFilter.getMunicipalityFilter())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe( manitobaHistoricalSites -> setSearchableSites( manitobaHistoricalSites),
                                        throwable ->  Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
                                )
                );
            }
        } catch (Exception e) {
            Log.e("Error", "updateDataToComplyWithNewFilters: Error updating the map to reflect the viewModel\n" + e.getMessage());
        }
    }

    //On text change do stuff
    TextWatcher searchTextListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            searchSites(s.toString(), true);

        }
    };


    public void searchSites(String searchText, boolean limitResults)
    {
        try {
            applicableSites.clear();
            if (searchText != null &&  !searchText.isEmpty())
            {
                for (ManitobaHistoricalSite site: searchableSites) {
                    String compareSiteValue = searchBy.equals("Site Address")? site.getAddress(): site.getName();
                    if (compareSiteValue.toLowerCase().contains(searchText.toLowerCase()))
                    {
                        applicableSites.add(site);
                    }
                    if (limitResults && applicableSites.size() >= limit)
                    {
                        break;
                    }

                }
                searchSiteAdapter.notifyDataSetChanged();
                String searchAmountText =  "Displaying ";
                if (limitResults && applicableSites.size() == limit)
                    searchAmountText += "top ";
                searchAmountText += applicableSites.size() + " sites:";
                tvSearchAmount.setText(searchAmountText);
            }
            else {
                tvSearchAmount.setText("");
            }
        }
        catch (Exception e)
        {
            Log.e("Error", "searchSites: Error displaying searched sites\n" + e.getMessage());
        }





    }






    @Override
    public void onResume() {
        super.onResume();
        mViewModel.setDisplayMode(DisplayMode.FullDetail);
        loadBaseOnFilters(mViewModel.getSiteFilters().getValue());

    }

    @Override
    public void onStop() {
        super.onStop();
        mDisposable.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.setDisplayMode(previousDisplayMode);
    }
}