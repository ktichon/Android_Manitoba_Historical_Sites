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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manitobahistoricalsites.Database.ManitobaHistoricalSite;
import com.example.manitobahistoricalsites.HolderClasses.DisplayMode;

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
        mViewModel = new ViewModelProvider(getActivity()).get(HistoricalSiteDetailsViewModel.class);


        //Stores the old display mode,
        previousDisplayMode = mViewModel.getDisplayMode().getValue();
        mViewModel.setDisplayMode(DisplayMode.FullSiteDetail);






        Spinner spinner = (Spinner) mainView.findViewById(R.id.spnSearchBy);
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
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
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.setDisplayMode(previousDisplayMode);
                FragmentManager fm = getActivity().getSupportFragmentManager();

                fm.popBackStack();
            }
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

        mDisposable.add(
                mViewModel.getHistoricalSiteDatabase().manitobaHistoricalSiteDao().loadAllManitobaHistoricalSites()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( manitobaHistoricalSites -> setSearchableSites( manitobaHistoricalSites),
                                throwable ->  Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
                        )
        );

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSites(etSearch.getText().toString(), false);
            }
        });



    }


    //Sets all searchable sites, makes sure that the edittext is only active after there is data
    public void setSearchableSites(List<ManitobaHistoricalSite> sites)
    {

        searchableSites.clear();
        searchableSites = sites;
        etSearch.setEnabled(true);
        btnSearch.setEnabled(true);
        Toast.makeText(getContext(), "Found " +  searchableSites.size() + " Applicable sites.", Toast.LENGTH_SHORT).show();
    }

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
        mViewModel.setDisplayMode(DisplayMode.FullSiteDetail);
    }

    @Override
    public void onStop() {
        super.onStop();
        mViewModel.setDisplayMode(previousDisplayMode);
        mDisposable.clear();
    }
}