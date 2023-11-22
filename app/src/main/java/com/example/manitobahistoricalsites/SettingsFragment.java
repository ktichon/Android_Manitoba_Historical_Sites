package com.example.manitobahistoricalsites;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    private View mainView;
    private HistoricalSiteDetailsViewModel mViewModel;
    private DisplayMode previousDisplayMode;



    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainView = view;
        mViewModel = new ViewModelProvider(getActivity()).get(HistoricalSiteDetailsViewModel.class);

        //Stores the old display mode,
        previousDisplayMode = mViewModel.getDisplayMode().getValue();
        mViewModel.setDisplayMode(DisplayMode.FullSiteDetail);


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
    }
}