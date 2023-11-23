package com.example.manitobahistoricalsites;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.manitobahistoricalsites.HolderClasses.DisplayMode;

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
        mViewModel = new ViewModelProvider(getActivity()).get(HistoricalSiteDetailsViewModel.class);

        //Stores the old display mode,
        previousDisplayMode = mViewModel.getDisplayMode().getValue();
        mViewModel.setDisplayMode(DisplayMode.FullSiteDetail);


        pBackgroundColour = findPreference(getString(R.string.background_colour_key));
        mainView.setBackgroundColor(Color.parseColor(pBackgroundColour.getText()));

        pBackgroundColour.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                new ColorPickerPopup.Builder(getContext())
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
                            }


                        });
            }
        });

        pSecondaryColour = findPreference(getString(R.string.secondary_colour_key));
        pSecondaryColour.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                new ColorPickerPopup.Builder(getContext())
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
                            }


                        });
            }
        });

        pTextColour = findPreference(getString(R.string.text_colour_key));
        pTextColour.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                new ColorPickerPopup.Builder(getContext())
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


                            }


                        });
            }
        });


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