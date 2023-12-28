package com.example.MHSmanitobahistoricalsites;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.MHSmanitobahistoricalsites.HolderClasses.DisplayMode;
import com.example.MHSmanitobahistoricalsites.HolderClasses.SiteFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//Allows user to filter out which sites to see
public class FilterFragment extends Fragment {

   //String[] allTypes = {"Building" , "Cemetery" ,  "Location" , "Monument" ,"Museum or Archives" , "Other" };

    String[] allTypes;

   // All possible municipality values
    String[] allMunicipalities =  {"Alexander" , "Alonsa" , "Altona" , "Arborg" , "Argyle" , "Armstrong" , "Atikaki Provincial Park" , "Beausejour" , "Bifrost-Riverton" , "Black River First Nation" , "Boissevain-Morton" , "Brandon" , "Brenda-Waskada" , "Brokenhead" , "Brokenhead Ojibway Nation" , "Buffalo Point First Nation" , "Carberry" , "Carman" , "Cartier" , "Cartwright-Roblin" , "Churchill" , "Clanwilliam-Erickson" , "Coldwell" , "Cornwallis" , "Dauphin (City)" , "Dauphin (RM)" , "De Salaberry" , "Deloraine-Winchester" , "Dufferin" , "Dunnottar" , "East St. Paul" , "Eastern Manitoba" , "Ellice-Archie" , "Elton" , "Emerson-Franklin" , "Ethelbert" , "Fisher" , "Fisher River Cree Nation" , "Flin Flon" , "Fox Lake Cree Nation" , "Gambler First Nation" , "Gilbert Plains" , "Gillam" , "Gimli" , "Glenboro-South Cypress" , "Glenella-Lansdowne" , "Grahamdale" , "Grand Rapids" , "Grandview" , "Grassland" , "Grey" , "Gypsumville" , "Hamiota" , "Hanover" , "Harrison Park" , "Headingley" , "Hecla-Grindstone Provincial Park" , "Kelsey" , "Killarney-Turtle Mountain" , "La Broquerie" , "Lac du Bonnet (RM)" , "Lac du Bonnet (Town)" , "Lake Manitoba First Nation" , "Lakeshore" , "Leaf Rapids" , "Lorne" , "Louise" , "Lynn Lake" , "Macdonald" , "McCreary" , "Melita" , "Minitonas-Bowsman" , "Minnedosa" , "Minto-Odanah" , "Misipawistik Cree Nation" , "Montcalm" , "Morden" , "Morris (RM)" , "Morris (Town)" , "Mossey River" , "Mountain" , "Neepawa" , "Niverville" , "None" , "Norfolk Treherne" , "North Cypress-Langford" , "North Norfolk" , "Northern Manitoba" , "Northwest Angle Provincial Forest" , "O-Chi-Chak-Ko-Sipi First Nation" , "Oakland-Wawanesa" , "Oakview" , "Opaskwayak Cree Nation" , "Peguis First Nation" , "Pembina" , "Pinawa" , "Pine Creek First Nation" , "Piney" , "Pipestone" , "Portage la Prairie (City)" , "Portage la Prairie (RM)" , "Powerview-Pine Falls" , "Prairie Lakes" , "Prairie View" , "Reynolds" , "Rhineland" , "Riding Mountain National Park" , "Riding Mountain West" , "Ritchot" , "Riverdale" , "Roblin" , "Rockwood" , "Roland" , "Roseau River First Nation" , "Rosedale" , "Rossburn" , "Rosser" , "Russell-Binscarth" , "Sagkeeng First Nation" , "Selkirk" , "Sifton" , "Sioux Valley Dakota Nation" , "Snow Lake" , "Souris-Glenwood" , "Southeastern Manitoba" , "Springfield" , "Spruce Woods Provincial Park" , "St-Pierre-Jolys" , "St. Andrews" , "St. Clements" , "St. Francois Xavier" , "St. Laurent" , "Stanley" , "Ste. Anne (RM)" , "Ste. Anne (Town)" , "Ste. Rose" , "Steinbach" , "Stonewall" , "Stuartburn" , "Swan Lake First Nation" , "Swan River" , "Swan Valley West" , "Tache" , "Tataskweyak Cree Nation" , "Teulon" , "The Pas" , "Thompson (City)" , "Thompson (RM)" , "Two Borders" , "USA" , "Victoria" , "Victoria Beach" , "Virden" , "Wallace-Woodworth" , "West Interlake" , "West St. Paul" , "WestLake-Gladstone" , "Whitehead" , "Whitemouth" , "Whiteshell Provincial Park" , "Winkler" , "Winnipeg" , "Winnipeg Beach" , "Woodlands" , "Woodridge Provincial Park" , "Yellowhead" , "Other" } ;


    //Whether each municipality is selected or not
    boolean[] selectedMunicipalities;


    List<String> municipalitiesFilter;
    ArrayList<Integer> munitList = new ArrayList<>();


    List<Integer> typeFilter;
    boolean[] selectedTypes;
    ArrayList<Integer> typeList = new ArrayList<>();

    View mainView;


    private HistoricalSiteDetailsViewModel mViewModel;

    TextView tvBack;

    TextView tvMultiMunicipalities;
    TextView  tvMultiTypes;

    AppCompatButton btnConfirmFilters;

    SiteFilter originalFilters;

    SiteFilter newFilters;

    DisplayMode previousDisplayMode;






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
        mViewModel = new ViewModelProvider(requireActivity()).get(HistoricalSiteDetailsViewModel.class);
        previousDisplayMode = mViewModel.getDisplayMode().getValue();
        mViewModel.setDisplayMode(DisplayMode.FullDetail);

        allTypes = getResources().getStringArray(R.array.Site_Types);

        originalFilters = new SiteFilter();
        if (mViewModel.getSiteFilters().getValue() != null)
            originalFilters = mViewModel.getSiteFilters().getValue();
        newFilters = originalFilters;
        municipalitiesFilter = originalFilters.getMunicipalityFilter();

        typeFilter = originalFilters.getSiteTypeFilter();

        tvBack = mainView.findViewById(R.id.tvFilterGoBack);
        tvBack.setOnClickListener(v -> {
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            fm.popBackStack();
        });

        //Sets up municipalities
        tvMultiMunicipalities = mainView.findViewById(R.id.tvMultiSelectMunicipality);
        selectedMunicipalities = setPreSelectedMunitFilter(originalFilters.getMunicipalityFilter(), allMunicipalities);
        tvMultiMunicipalities.setOnClickListener(onMunicipalityMultiClick);

        //Sets up types
        tvMultiTypes = mainView.findViewById(R.id.tvMultiSelectType);
        selectedTypes = setPreSelectedTypeFilter(originalFilters.getSiteTypeFilter(), allTypes);
        tvMultiTypes.setOnClickListener(onTypeMultiClick);

        
        btnConfirmFilters = mainView.findViewById(R.id.btnConfirmFilters);

        //Updates filters
        btnConfirmFilters.setOnClickListener(v -> {
            try {

                mViewModel.setSiteFilters(newFilters);
                Toast.makeText(getContext(), "Updating Historical Site Filters", Toast.LENGTH_SHORT).show();

                FragmentManager fm = requireActivity().getSupportFragmentManager();
                fm.popBackStack();

            }
            catch (Exception e)
            {
                Log.e("Error", "btnConfirmFiltersOnClick: Error saving site filters\n" + e.getMessage());
                Toast.makeText(getContext(), "Error updating Historical Site Filters", Toast.LENGTH_SHORT).show();
            }


        });

    }

    public View.OnClickListener onMunicipalityMultiClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {
                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.DialogBoxTheme);

                // set title
                builder.setTitle("Select Municipalities");

                // set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(allMunicipalities, selectedMunicipalities, (dialogInterface, i, b) -> {
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
                });

                builder.setPositiveButton("OK", (dialogInterface, i) -> {
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
                });

                builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                    // dismiss dialog
                    dialogInterface.dismiss();
                });
                builder.setNeutralButton("Clear All", (dialogInterface, i) -> {
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
                });
                // show dialog
                builder.show();
            }
            catch (Exception e)
            {
                Log.e("Error", "onMunicipalityMultiClick: Error with municipality multi select\n" + e.getMessage());
            }
        }



    };

    public View.OnClickListener onTypeMultiClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {
                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.DialogBoxTheme);

                // set title
                builder.setTitle("Select Site Types");

                // set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(allTypes, selectedTypes, (dialogInterface, i, b) -> {
                    // check condition
                    if (b) {
                        // when checkbox selected
                        // Add position  in lang list
                        typeList.add(i);
                        // Sort array list
                        Collections.sort(typeList);
                    } else {
                        // when checkbox unselected
                        // Remove position from langList
                        typeList.remove(Integer.valueOf(i));
                    }
                });

                builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    // Initialize string builder
                    StringBuilder stringBuilder = new StringBuilder();
                    typeFilter.clear();

                    // use for loop
                    for (int j = 0; j < typeList.size(); j++) {
                        // concat array value
                        stringBuilder.append(allTypes[typeList.get(j)]);
                        typeFilter.add(typeList.get(j) + 1);

                        // check condition
                        if (j != typeList.size() - 1) {
                            // When j value  not equal
                            // to lang list size - 1
                            // add comma
                            stringBuilder.append(", ");
                        }
                    }
                    // set text on textView
                    tvMultiTypes.setText(stringBuilder.toString());
                    newFilters.setSiteTypeFilter(typeFilter);
                    Toast.makeText(getContext(),typeFilter.toString(), Toast.LENGTH_LONG).show();
                });

                builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                    // dismiss dialog
                    dialogInterface.dismiss();
                });
                builder.setNeutralButton("Clear All", (dialogInterface, i) -> {
                    // use for loop
                    for (int j = 0; j < selectedTypes.length; j++) {
                        // remove all selection
                        selectedTypes[j] = false;
                        // clear language list
                        typeList.clear();
                        typeFilter.clear();
                        // clear text view value
                        tvMultiTypes.setText("");
                    }
                });
                // show dialog
                builder.show();
            }
            catch (Exception e)
            {
                Log.e("Error", "onTypeMultiClick: Error with municipality multi select\n" + e.getMessage());
            }
        }



    };


    //Pre-sets the selected filters and text box for municipalities
    private boolean [] setPreSelectedMunitFilter (List<String> preSetStrings, String[] allArray  )
    {
        boolean[] presetFilter = new boolean[allArray.length];

        try {
            for (int i = 0; i < allArray.length; i ++)
            {
                presetFilter[i] = preSetStrings.contains(allArray[i]);
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < preSetStrings.size(); i++) {
                // concat array value
                stringBuilder.append(preSetStrings.get(i));
                // check condition
                if (i != preSetStrings.size() - 1) {
                    // When j value  not equal
                    // to lang list size - 1
                    // add comma
                    stringBuilder.append(", ");
                }
            }
            tvMultiMunicipalities.setText(stringBuilder.toString());


        }
        catch (Exception e)
        {
            Log.e("Error", "setPreSelectedMunitFilter: Error setting preset filters\n" + e.getMessage());
        }
        return  presetFilter;
    }

    //Pre-sets the selected filters and text box for types. It has to be its own function because site types use int values.
    private boolean [] setPreSelectedTypeFilter (List<Integer> preSetTypes, String[] allArray  )
    {
        boolean[] presetFilter = new boolean[allArray.length];

        try {
            StringBuilder preSetTypeString = new StringBuilder();
            for (int i = 0; i < allArray.length; i ++)
            {
                presetFilter[i] = preSetTypes.contains(i + 1);


            }
            for (int i = 0; i < preSetTypes.size(); i++) {
                //Arrays starts at 0, however types start at 1
                preSetTypeString.append(allTypes[(preSetTypes.get(i) - 1)]);

                if (i != preSetTypes.size() - 1) {
                    preSetTypeString.append(", ");
                }
            }

            tvMultiTypes.setText(preSetTypeString);
        }
        catch (Exception e)
        {
            Log.e("Error", "setPreSelectedTypeFilter: Error setting preset filters\n" + e.getMessage());
        }
        return  presetFilter;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.setDisplayMode(previousDisplayMode);
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.setDisplayMode(DisplayMode.FullDetail);
    }
}