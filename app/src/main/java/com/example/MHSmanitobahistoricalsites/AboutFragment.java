package com.example.MHSmanitobahistoricalsites;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.MHSmanitobahistoricalsites.HolderClasses.DisplayMode;


public class AboutFragment extends Fragment {

    private View mainView;
    private HistoricalSiteDetailsViewModel mViewModel;
    private DisplayMode previousDisplayMode;



    public AboutFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(HistoricalSiteDetailsViewModel.class);
        //Stores the old display mode,
        previousDisplayMode = mViewModel.getDisplayMode().getValue();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainView = view;



        //mViewModel.setDisplayMode(DisplayMode.Other);
        TextView tvBack = mainView.findViewById(R.id.tvAboutGoBack);
        tvBack.setOnClickListener(v -> {
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            fm.popBackStack();
        });
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