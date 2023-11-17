package com.example.manitobahistoricalsites;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//Allows user to filter out which sites to see
public class FilterFragment extends Fragment {

   String[] allTypes = {"Building" , "Cemetery" ,  "Location" , "Monument" ,"Museum or Archives" , "Other" };


   // All possible municipality values
    String[] allMunicipalities =  {"Alexander" , "Alonsa" , "Altona" , "Arborg" , "Argyle" , "Armstrong" , "Atikaki Provincial Park" , "Beausejour" , "Bifrost-Riverton" , "Black River First Nation" , "Boissevain-Morton" , "Brandon" , "Brenda-Waskada" , "Brokenhead" , "Brokenhead Ojibway Nation" , "Buffalo Point First Nation" , "Carberry" , "Carman" , "Cartier" , "Cartwright-Roblin" , "Churchill" , "Clanwilliam-Erickson" , "Coldwell" , "Cornwallis" , "Dauphin (City)" , "Dauphin (RM)" , "De Salaberry" , "Deloraine-Winchester" , "Dufferin" , "Dunnottar" , "East St. Paul" , "Eastern Manitoba" , "Ellice-Archie" , "Elton" , "Emerson-Franklin" , "Ethelbert" , "Fisher" , "Fisher River Cree Nation" , "Flin Flon" , "Fox Lake Cree Nation" , "Gambler First Nation" , "Gilbert Plains" , "Gillam" , "Gimli" , "Glenboro-South Cypress" , "Glenella-Lansdowne" , "Grahamdale" , "Grand Rapids" , "Grandview" , "Grassland" , "Grey" , "Gypsumville" , "Hamiota" , "Hanover" , "Harrison Park" , "Headingley" , "Hecla-Grindstone Provincial Park" , "Kelsey" , "Killarney-Turtle Mountain" , "La Broquerie" , "Lac du Bonnet (RM)" , "Lac du Bonnet (Town)" , "Lake Manitoba First Nation" , "Lakeshore" , "Leaf Rapids" , "Lorne" , "Louise" , "Lynn Lake" , "Macdonald" , "McCreary" , "Melita" , "Minitonas-Bowsman" , "Minnedosa" , "Minto-Odanah" , "Misipawistik Cree Nation" , "Montcalm" , "Morden" , "Morris (RM)" , "Morris (Town)" , "Mossey River" , "Mountain" , "Neepawa" , "Niverville" , "None" , "Norfolk Treherne" , "North Cypress-Langford" , "North Norfolk" , "Northern Manitoba" , "Northwest Angle Provincial Forest" , "O-Chi-Chak-Ko-Sipi First Nation" , "Oakland-Wawanesa" , "Oakview" , "Opaskwayak Cree Nation" , "Peguis First Nation" , "Pembina" , "Pinawa" , "Pine Creek First Nation" , "Piney" , "Pipestone" , "Portage la Prairie (City)" , "Portage la Prairie (RM)" , "Powerview-Pine Falls" , "Prairie Lakes" , "Prairie View" , "Reynolds" , "Rhineland" , "Riding Mountain National Park" , "Riding Mountain West" , "Ritchot" , "Riverdale" , "Roblin" , "Rockwood" , "Roland" , "Roseau River First Nation" , "Rosedale" , "Rossburn" , "Rosser" , "Russell-Binscarth" , "Sagkeeng First Nation" , "Selkirk" , "Sifton" , "Sioux Valley Dakota Nation" , "Snow Lake" , "Souris-Glenwood" , "Southeastern Manitoba" , "Springfield" , "Spruce Woods Provincial Park" , "St-Pierre-Jolys" , "St. Andrews" , "St. Clements" , "St. Francois Xavier" , "St. Laurent" , "Stanley" , "Ste. Anne (RM)" , "Ste. Anne (Town)" , "Ste. Rose" , "Steinbach" , "Stonewall" , "Stuartburn" , "Swan Lake First Nation" , "Swan River" , "Swan Valley West" , "Tache" , "Tataskweyak Cree Nation" , "Teulon" , "The Pas" , "Thompson (City)" , "Thompson (RM)" , "Two Borders" , "USA" , "Victoria" , "Victoria Beach" , "Virden" , "Wallace-Woodworth" , "West Interlake" , "West St. Paul" , "WestLake-Gladstone" , "Whitehead" , "Whitemouth" , "Whiteshell Provincial Park" , "Winkler" , "Winnipeg" , "Winnipeg Beach" , "Woodlands" , "Woodridge Provincial Park" , "Yellowhead" , "Other" } ;


    //Whether each municipality is selected or not
    boolean[] selectedMunicipalities;


    List<String> municipalitiesFilter;
    ArrayList<Integer> munitList = new ArrayList<>();


    List<String> typeFilter;

    View mainView;


    private HistoricalSiteDetailsViewModel mViewModel;

    TextView tvBack;

    TextView tvMultiMunicipalities;
    CheckBox cbAllMunicipalities;
    CheckBox cbAllSiteTypes;

    AppCompatButton btnConfirmFilters;

    SiteFilter originalFilters;

    SiteFilter newFilters;





    public FilterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainView = view;
        mViewModel = new ViewModelProvider(getActivity()).get(HistoricalSiteDetailsViewModel.class);

        originalFilters = mViewModel.getSiteFilters().getValue();
        newFilters = originalFilters;
        municipalitiesFilter = originalFilters.getMunicipalityFilter();

        cbAllMunicipalities = mainView.findViewById(R.id.cbSelectAllMunicipalities);
        cbAllMunicipalities.setChecked(originalFilters.isAllMunicipalities());


        cbAllMunicipalities.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                newFilters.setAllMunicipalities(isChecked);
                if (isChecked)
                {
                    tvMultiMunicipalities.setText("");
                }
                else {
                    tvMultiMunicipalities.setText(R.string.multi_select_municipalities);
                }

            }
        });



        cbAllSiteTypes = mainView.findViewById(R.id.cbSelectAllTypes);
        cbAllSiteTypes.setChecked(originalFilters.isAllSiteTypes());

        tvBack = mainView.findViewById(R.id.tvFilterGoBack);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.setDisplayMode(DisplayMode.FullMap);
                FragmentManager fm = getActivity().getSupportFragmentManager();

                fm.popBackStack();
            }
        });


        tvMultiMunicipalities = mainView.findViewById(R.id.tvMultiSelectMunicipality);

        selectedMunicipalities = setPreSelectedFilter(originalFilters.municipalityFilter, allMunicipalities);
        tvMultiMunicipalities.setOnClickListener(onMunicipalityMultiClick);

        tvMultiMunicipalities.setText(setPreSetTexbox(originalFilters.isAllMunicipalities(), originalFilters.getMunicipalityFilter()));
        btnConfirmFilters = mainView.findViewById(R.id.btnConfirmFilters);

        //Updates filters
        btnConfirmFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    // if "All" is select or sizeT is 0, set to "All" and clear list
                    if (newFilters.isAllMunicipalities() || newFilters.getMunicipalityFilter().size() == 0){
                        newFilters.setAllMunicipalities(true);
                        newFilters.getMunicipalityFilter().clear();
                    }


                    mViewModel.setSiteFilters(newFilters);
                    Toast.makeText(getContext(), "Updating Historical Site Filters", Toast.LENGTH_SHORT).show();

                }
                catch (Exception e)
                {
                    Log.e("Error", "btnConfirmFiltersOnClick: Error saving site filters\n" + e.getMessage());
                }


            }
        });

    }

    public View.OnClickListener onMunicipalityMultiClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!newFilters.isAllMunicipalities())
            {
                try {
                    // Initialize alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogBoxTheme);

                    // set title
                    builder.setTitle("Select Municipalities");

                    // set dialog non cancelable
                    builder.setCancelable(false);

                    builder.setMultiChoiceItems(allMunicipalities, selectedMunicipalities, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                            // check condition
                            if (b) {
                                // when checkbox selected
                                // Add position  in lang list
                                munitList.add(i);
                                // Sort array list
                                Collections.sort(munitList);
                            } else {
                                // when checkbox unselected
                                // Remove position from langList
                                munitList.remove(Integer.valueOf(i));
                            }
                        }
                    });

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Initialize string builder
                            StringBuilder stringBuilder = new StringBuilder();
                            municipalitiesFilter.clear();

                            // use for loop
                            for (int j = 0; j < munitList.size(); j++) {
                                // concat array value
                                stringBuilder.append(allMunicipalities[munitList.get(j)]);
                                municipalitiesFilter.add(allMunicipalities[munitList.get(j)]);



                                // check condition
                                if (j != munitList.size() - 1) {
                                    // When j value  not equal
                                    // to lang list size - 1
                                    // add comma
                                    stringBuilder.append(", ");
                                }
                            }
                            // set text on textView
                            tvMultiMunicipalities.setText(stringBuilder.toString());
                            newFilters.setMunicipalityFilter(municipalitiesFilter);
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // dismiss dialog
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // use for loop
                            for (int j = 0; j < selectedMunicipalities.length; j++) {
                                // remove all selection
                                selectedMunicipalities[j] = false;
                                // clear language list
                                munitList.clear();
                                municipalitiesFilter.clear();
                                // clear text view value
                                tvMultiMunicipalities.setText("");
                            }
                        }
                    });
                    // show dialog
                    builder.show();
                }
                catch (Exception e)
                {
                    Log.e("Error", "onMunicipalityMultiClick: Error with municipality multi select\n" + e.getMessage());
                }
            }


        }
    };


    //Pre-sets the selected filters for the multi selects
    private boolean [] setPreSelectedFilter (List<String> preSetStrings, String[] allArray  )
    {
        boolean presetFilter [] = new boolean[allArray.length];

        try {
            for (int i = 0; i < allArray.length; i ++)
            {
                presetFilter[i] = preSetStrings.contains(allArray[i]);
            }

        }
        catch (Exception e)
        {
            Log.e("Error", "setPreSelectedFilter: Error setting preset filters\n" + e.getMessage());
        }
        return  presetFilter;
    }


    //Gets text if there is already a filter set
    private String setPreSetTexbox( Boolean isAll, List<String>  preSetString)
    {
        String result = "";
        try {

            if (!isAll)
            {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < preSetString.size(); i++) {
                    // concat array value
                    stringBuilder.append(preSetString.get(i));
                    // check condition
                    if (i != preSetString.size() - 1) {
                        // When j value  not equal
                        // to lang list size - 1
                        // add comma
                        stringBuilder.append(", ");
                    }
                }
                result = stringBuilder.toString();

            }

        }
        catch (Exception e)
        {
            Log.e("Error", "setPreSetTexbox: Error setting preset text\n" + e.getMessage());
        }
        return result;

    }

    @Override
    public void onStop() {
        mViewModel.setDisplayMode(DisplayMode.FullMap);
        super.onStop();
    }
}