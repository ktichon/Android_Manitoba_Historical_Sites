package com.example.manitobahistoricalsites;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.manitobahistoricalsites.HolderClasses.DisplayMode;

import java.util.Arrays;
import java.util.HashMap;

import top.defaults.colorpicker.ColorPickerPopup;

public class SettingsFragment extends PreferenceFragmentCompat {
    private View mainView;
    private HistoricalSiteDetailsViewModel mViewModel;
    private DisplayMode previousDisplayMode;

    private EditTextPreference pBackgroundColour;

    private EditTextPreference pSecondaryColour;
    private EditTextPreference pTextColour;




    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainView = view;
        mViewModel = new ViewModelProvider(requireActivity()).get(HistoricalSiteDetailsViewModel.class);

        //Stores the old display mode,
        previousDisplayMode = mViewModel.getDisplayMode().getValue();
        mViewModel.setDisplayMode(DisplayMode.FullSiteDetail);


        pBackgroundColour = findPreference(getString(R.string.background_colour_key));
        mainView.setBackgroundColor(Color.parseColor(pBackgroundColour.getText()));
        pBackgroundColour.setSummary(pBackgroundColour.getText());

        pBackgroundColour.setOnBindEditTextListener(editText -> new ColorPickerPopup.Builder(getContext())
                .initialColor(Color.parseColor(pBackgroundColour.getText())) // Set initial color
                .enableBrightness(true) // Enable brightness slider or not
                .enableAlpha(true) // Enable alpha slider or not
                .okTitle("Choose")
                .cancelTitle("Cancel")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(mainView, new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int colour) {
                        mainView.setBackgroundColor(colour);
                        editText.setText (String.format("#%06X", (0xFFFFFF & colour)));
                        pBackgroundColour.setText(String.format("#%06X", (0xFFFFFF & colour)));
                        pBackgroundColour.setSummary(String.format("#%06X", (0xFFFFFF & colour)));
                    }


                }));

        pSecondaryColour = findPreference(getString(R.string.secondary_colour_key));
        pSecondaryColour.setSummary(pBackgroundColour.getText());

        pSecondaryColour.setOnBindEditTextListener(editText -> new ColorPickerPopup.Builder(getContext())
                .initialColor(Color.parseColor(pSecondaryColour.getText())) // Set initial color
                .enableBrightness(true) // Enable brightness slider or not
                .enableAlpha(true) // Enable alpha slider or not
                .okTitle("Choose")
                .cancelTitle("Cancel")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(mainView, new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int colour) {
                        //mainView.setBackgroundColor(colour);
                        editText.setText (String.format("#%06X", (0xFFFFFF & colour)));
                        editText.setBackgroundColor(colour);
                        pSecondaryColour.setText(String.format("#%06X", (0xFFFFFF & colour)));
                        pSecondaryColour.setSummary(String.format("#%06X", (0xFFFFFF & colour)));
                    }


                }));

        pTextColour = findPreference(getString(R.string.text_colour_key));
        pTextColour.setSummary(pBackgroundColour.getText());
        pTextColour.setOnBindEditTextListener(editText -> new ColorPickerPopup.Builder(getContext())
                .initialColor(Color.parseColor(pTextColour.getText())) // Set initial color
                .enableBrightness(true) // Enable brightness slider or not
                .enableAlpha(true) // Enable alpha slider or not
                .okTitle("Choose")
                .cancelTitle("Cancel")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(mainView, new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int colour) {
                        editText.setTextColor(colour);
                        editText.setText (String.format("#%06X", (0xFFFFFF & colour)));
                        pTextColour.setText(String.format("#%06X", (0xFFFFFF & colour)));
                        pTextColour.setSummary(String.format("#%06X", (0xFFFFFF & colour)));


                    }


                }));

        CheckBoxPreference pUpdateMarkers = findPreference(getString(R.string.update_marker_key));
        pUpdateMarkers.setChecked(false);

        ListPreference lpBuilding = findPreference("Building");
        lpBuilding.setOnPreferenceChangeListener(onPreferenceChangeListener);
        lpBuilding.setSummary(getColourName(lpBuilding.getValue()));

        ListPreference lpCemetery= findPreference("Cemetery");
        lpCemetery.setOnPreferenceChangeListener(onPreferenceChangeListener);
        lpCemetery.setSummary(getColourName(lpCemetery.getValue()));

        ListPreference lpLocation = findPreference("Location");
        lpLocation.setOnPreferenceChangeListener(onPreferenceChangeListener);
        lpLocation.setSummary(getColourName(lpLocation.getValue()));

        ListPreference lpMonument = findPreference("Monument");
        lpMonument.setOnPreferenceChangeListener(onPreferenceChangeListener);
        lpMonument.setSummary(getColourName(lpMonument.getValue()));

        ListPreference lpMuseumOrArchives = findPreference("Museum or Archives");
        lpMuseumOrArchives.setOnPreferenceChangeListener(onPreferenceChangeListener);
        lpMuseumOrArchives.setSummary(getColourName(lpMuseumOrArchives.getValue()));

        ListPreference lpOther = findPreference("Other");
        lpOther.setOnPreferenceChangeListener(onPreferenceChangeListener);
        lpOther.setSummary(getColourName(lpOther.getValue()));
    }

    Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            preference.setSummary(getColourName(newValue.toString()));
            return false;
        }
    };

    
    //Gets the colour name from the colour value
    public String getColourName (String colourValue)
    {
        String [] colourName = getResources().getStringArray(R.array.Colour_Names);
        String [] colourValues = getResources().getStringArray(R.array.Colour_Values);
        int position = Arrays.asList(colourValues).indexOf(colourValue);
        String displayColour = "HUE_RED";
        if (position > -1 && position < colourName.length)
            displayColour = colourName[position];
        return displayColour;
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