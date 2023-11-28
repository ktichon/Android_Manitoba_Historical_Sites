package com.example.manitobahistoricalsites;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
        mViewModel.setDisplayMode(DisplayMode.FullDetail);


        pBackgroundColour = findPreference(getString(R.string.background_colour_key));



        mainView.setBackgroundColor(getValidColour(pBackgroundColour.getText(), Color.WHITE));
        pBackgroundColour.setSummary(pBackgroundColour.getText());

        pBackgroundColour.setOnBindEditTextListener(editText -> new ColorPickerPopup.Builder(getContext())
                .initialColor(getValidColour(pBackgroundColour.getText(), Color.WHITE)) // Set initial color
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
        pSecondaryColour.setSummary(pSecondaryColour.getText());

        pSecondaryColour.setOnBindEditTextListener(editText -> new ColorPickerPopup.Builder(getContext())
                .initialColor(getValidColour(pSecondaryColour.getText(), Color.BLACK)) // Set initial color
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
        pTextColour.setSummary(pTextColour.getText());
        pTextColour.setOnBindEditTextListener(editText -> new ColorPickerPopup.Builder(getContext())
                .initialColor(getValidColour(pTextColour.getText(), Color.BLACK)) // Set initial color
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
            ((ListPreference) preference).setValue(newValue.toString());
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

    //Makes sure that a valid number is entered
    public int getValidColour(String colour, int defaultColour)
    {

        int resultColour = defaultColour;
        try {
            resultColour = Color.parseColor(colour);
        }
        catch (Exception e)
        {
            resultColour = defaultColour;
            Toast.makeText(getContext(), "Unable to get Colour", Toast.LENGTH_SHORT).show();
        }
        return resultColour;

    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.setDisplayMode(DisplayMode.FullDetail);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.setDisplayMode(previousDisplayMode);
    }
}