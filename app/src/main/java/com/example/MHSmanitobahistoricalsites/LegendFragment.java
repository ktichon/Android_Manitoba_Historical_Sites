package com.example.MHSmanitobahistoricalsites;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.MHSmanitobahistoricalsites.HolderClasses.DisplayMode;
import com.example.MHSmanitobahistoricalsites.HolderClasses.LegendItem;
import com.example.MHSmanitobahistoricalsites.HolderClasses.SiteFilter;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.List;

public class LegendFragment extends Fragment {



    // All possible municipality values





  String [] allTypes;

    View mainView;


    private HistoricalSiteDetailsViewModel mViewModel;

    TextView tvBack;


    SiteFilter originalFilters;

    SiteFilter newFilters;

    DisplayMode previousDisplayMode;

    OnBackPressedCallback fullScreenCallback;

    LegendAdapter legendAdapter;

    List<LegendItem> legendItems;
    RecyclerView rvLegend;
    public LegendFragment() {
        // Required empty public constructor
    }


    //Overrides the callback to go back to full Legend info
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(HistoricalSiteDetailsViewModel.class);
        previousDisplayMode = mViewModel.getDisplayMode().getValue();
        /*fullScreenCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                mViewModel.setDisplayMode(DisplayMode.Other);
                setEnabled(false);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, fullScreenCallback);
        originalFilters = new SiteFilter();
        if (mViewModel.getSiteFilters().getValue() != null)
            originalFilters = mViewModel.getSiteFilters().getValue();*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_legend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainView = view;

        //mViewModel.setDisplayMode(DisplayMode.Other);

        allTypes = getResources().getStringArray(R.array.Site_Types);
        legendItems = new ArrayList<>();
        legendAdapter = new LegendAdapter(legendItems);
        rvLegend = mainView.findViewById(R.id.rvLegend);
        rvLegend.setAdapter(legendAdapter);
        rvLegend.setLayoutManager(new LinearLayoutManager(getContext()));




        tvBack = mainView.findViewById(R.id.tvLegendGoBack);
        tvBack.setOnClickListener(v -> {
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            fm.popBackStack();
        });
        displayLegendValue(allTypes, mViewModel.getMarkerColours().getValue());

    }

    public void displayLegendValue(String[] types, List<Float> colours)
    {
        legendItems.clear();

        try {
            for (int k = 0; k < types.length; k ++)
            {
                LegendItem newItem = new LegendItem(types[k], colours.get(k));
                legendItems.add(newItem);
            }
            legendAdapter.notifyDataSetChanged();
        }
        catch (Exception e)
        {
            Log.e("Error", "displayLegendValue: Error displaying marker colours\n" + e.getMessage());
        }



    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.setDisplayMode(DisplayMode.Other);

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.setDisplayMode(previousDisplayMode);
    }
}